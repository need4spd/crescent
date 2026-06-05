package com.tistory.devyongsik.crescent.dictionary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.ko.dict.UserDictionary;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nori 분석기용 사용자 사전을 관리하는 싱글톤.
 *
 * 4종 사전 파일(custom/compounds/stop/synonym)을 메모리에 로드하고,
 * 단어 추가/삭제/검색/파일 저장 및 Nori 분석 컴포넌트(UserDictionary, 불용어 CharArraySet,
 * SynonymMap) 재빌드를 담당한다. 기존 korean-analyzer-4.x의 DictionaryFactory를 대체한다.
 *
 * 분석기({@link com.tistory.devyongsik.crescent.analyzer.CrescentNoriAnalyzer})는
 * Spring 빈이 아닌 리플렉션으로 생성되므로, 이 싱글톤을 통해 사전 컴포넌트에 접근한다.
 */
public class CrescentNoriDictionary {

	private static final Logger logger = LoggerFactory.getLogger(CrescentNoriDictionary.class);

	private static final CrescentNoriDictionary INSTANCE = new CrescentNoriDictionary();

	private final Map<CrescentDictionaryType, List<String>> wordsByType = new LinkedHashMap<>();

	private volatile UserDictionary userDictionary;
	private volatile CharArraySet stopWords;
	private volatile SynonymMap synonymMap;

	private boolean loaded = false;

	private CrescentNoriDictionary() {
	}

	public static CrescentNoriDictionary getInstance() {
		return INSTANCE;
	}

	private synchronized void ensureLoaded() {
		if (loaded) {
			return;
		}
		for (CrescentDictionaryType type : CrescentDictionaryType.values()) {
			wordsByType.put(type, loadFromFile(type));
		}
		rebuildUserDictionary();
		rebuildStopWords();
		rebuildSynonymMap();
		loaded = true;
	}

	private List<String> loadFromFile(CrescentDictionaryType type) {
		List<String> words = new ArrayList<>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		try (InputStream is = classLoader.getResourceAsStream(type.getFileName())) {
			if (is == null) {
				logger.warn("사전 파일을 찾을 수 없습니다: {}", type.getFileName());
				return words;
			}
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					String trimmed = line.trim();
					if (!trimmed.isEmpty()) {
						words.add(trimmed);
					}
				}
			}
		} catch (IOException e) {
			logger.error("사전 파일 로드 실패: {}", type.getFileName(), e);
			throw new IllegalStateException("사전 파일 로드 실패: " + type.getFileName(), e);
		}
		logger.info("사전 로드 완료: {} ({}건)", type.getFileName(), words.size());
		return words;
	}

	// ---------- 사전 단어 관리 (관리자 화면용) ----------

	public List<String> getWords(CrescentDictionaryType type) {
		ensureLoaded();
		return wordsByType.get(type);
	}

	public void addWord(CrescentDictionaryType type, String word) {
		ensureLoaded();
		if (type == CrescentDictionaryType.COMPOUND && word.split(":").length < 2) {
			throw new IllegalStateException("복합명사사전의 단어는 [복합어:분해1,분해2] 형식이어야 합니다. [" + word + "]");
		}
		getWords(type).add(word);
	}

	public void removeWord(CrescentDictionaryType type, String word) {
		ensureLoaded();
		getWords(type).remove(word);
	}

	public List<String> findWords(CrescentDictionaryType type, String word) {
		ensureLoaded();
		List<String> result = new ArrayList<>();
		for (String w : getWords(type)) {
			if (w.indexOf(word) >= 0) {
				result.add(w);
			}
		}
		return result;
	}

	public void writeToFile(CrescentDictionaryType type) {
		ensureLoaded();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoader.getResource(type.getFileName());
		if (url == null) {
			throw new IllegalStateException("사전 파일을 찾을 수 없습니다: " + type.getFileName());
		}

		try {
			File file = new File(url.toURI());
			try (FileOutputStream fos = new FileOutputStream(file, false);
				 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))) {
				for (String word : getWords(type)) {
					writer.write(word);
					writer.write("\n");
				}
			}
		} catch (URISyntaxException | IOException e) {
			logger.error("사전 파일 저장 실패: {}", type.getFileName(), e);
			throw new IllegalStateException("사전 파일 저장 실패: " + type.getFileName(), e);
		}
	}

	// ---------- Nori 컴포넌트 재빌드 ----------

	public synchronized void rebuild(CrescentDictionaryType type) {
		ensureLoaded();
		switch (type) {
			case CUSTOM:
			case COMPOUND:
				rebuildUserDictionary();
				break;
			case STOP:
				rebuildStopWords();
				break;
			case SYNONYM:
				rebuildSynonymMap();
				break;
		}
	}

	/**
	 * 명사사전(단일 명사) + 복합명사사전(분해 규칙)을 Nori UserDictionary 포맷으로 합쳐 빌드한다.
	 * - 명사: "랑콤"            → "랑콤"
	 * - 복합명사: "맥북에어:맥북,에어" → "맥북에어 맥북 에어"
	 * 표제어(surface) 중복 시 복합명사 정의가 우선한다.
	 */
	private void rebuildUserDictionary() {
		Map<String, String> entryBySurface = new LinkedHashMap<>();

		for (String noun : wordsByType.get(CrescentDictionaryType.CUSTOM)) {
			String surface = noun.trim();
			if (!surface.isEmpty()) {
				entryBySurface.put(surface, surface);
			}
		}

		for (String compound : wordsByType.get(CrescentDictionaryType.COMPOUND)) {
			String[] parts = compound.split(":");
			if (parts.length < 2) {
				logger.warn("복합명사 형식 오류, 건너뜀: {}", compound);
				continue;
			}
			String surface = parts[0].trim();
			String[] segments = parts[1].trim().split(",");
			StringBuilder entry = new StringBuilder(surface);
			for (String segment : segments) {
				String seg = segment.trim();
				if (!seg.isEmpty()) {
					entry.append(" ").append(seg);
				}
			}
			entryBySurface.put(surface, entry.toString());
		}

		if (entryBySurface.isEmpty()) {
			userDictionary = null;
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (String entry : entryBySurface.values()) {
			sb.append(entry).append("\n");
		}

		try {
			userDictionary = UserDictionary.open(new StringReader(sb.toString()));
			logger.info("Nori UserDictionary 재빌드 완료 ({}건)", entryBySurface.size());
		} catch (IOException e) {
			logger.error("Nori UserDictionary 빌드 실패", e);
			throw new IllegalStateException("Nori UserDictionary 빌드 실패", e);
		}
	}

	private void rebuildStopWords() {
		List<String> stops = wordsByType.get(CrescentDictionaryType.STOP);
		stopWords = new CharArraySet(stops, true);
		logger.info("Nori 불용어 재빌드 완료 ({}건)", stops.size());
	}

	/**
	 * 동의어사전(a,b,c) → SynonymMap. 한 줄의 모든 단어를 서로 동의어로 등록한다.
	 */
	private void rebuildSynonymMap() {
		List<String> synonymLines = wordsByType.get(CrescentDictionaryType.SYNONYM);
		SynonymMap.Builder builder = new SynonymMap.Builder(true);
		int pairCount = 0;

		for (String line : synonymLines) {
			String[] terms = line.split(",");
			List<String> cleaned = new ArrayList<>();
			for (String term : terms) {
				String t = term.trim();
				if (!t.isEmpty()) {
					cleaned.add(t);
				}
			}
			for (int i = 0; i < cleaned.size(); i++) {
				for (int j = 0; j < cleaned.size(); j++) {
					if (i != j) {
						builder.add(new CharsRef(cleaned.get(i)), new CharsRef(cleaned.get(j)), true);
						pairCount++;
					}
				}
			}
		}

		if (pairCount == 0) {
			synonymMap = null;
			return;
		}

		try {
			synonymMap = builder.build();
			logger.info("Nori 동의어 재빌드 완료 ({} pairs)", pairCount);
		} catch (IOException e) {
			logger.error("Nori SynonymMap 빌드 실패", e);
			throw new IllegalStateException("Nori SynonymMap 빌드 실패", e);
		}
	}

	// ---------- 분석기에서 사용하는 컴포넌트 접근자 ----------

	public UserDictionary getUserDictionary() {
		ensureLoaded();
		return userDictionary;
	}

	public CharArraySet getStopWords() {
		ensureLoaded();
		return stopWords;
	}

	public SynonymMap getSynonymMap() {
		ensureLoaded();
		return synonymMap;
	}
}
