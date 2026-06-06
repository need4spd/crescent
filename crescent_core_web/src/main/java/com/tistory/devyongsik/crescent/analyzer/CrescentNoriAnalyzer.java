package com.tistory.devyongsik.crescent.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.ko.KoreanPartOfSpeechStopFilter;
import org.apache.lucene.analysis.ko.KoreanReadingFormFilter;
import org.apache.lucene.analysis.ko.KoreanTokenizer;
import org.apache.lucene.analysis.ko.dict.UserDictionary;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;

import com.tistory.devyongsik.crescent.dictionary.CrescentNoriDictionary;

/**
 * Crescent 사용자 사전을 적용한 Nori 기반 한국어 분석기.
 *
 * 분석 체인:
 *   KoreanTokenizer(UserDictionary)
 *     → KoreanPartOfSpeechStopFilter (기본 품사 불용 태그)
 *     → KoreanReadingFormFilter (한자 독음 변환)
 *     → LowerCaseFilter
 *     → StopFilter (불용어사전)
 *     → SynonymGraphFilter (동의어사전) [+ 색인 모드일 때 FlattenGraphFilter]
 *
 * 사용자 사전(명사/복합명사/불용어/동의어)은 {@link CrescentNoriDictionary} 싱글톤에서 가져온다.
 * 사전이 변경되면 분석기를 새로 생성해야 변경 내용이 반영된다.
 *
 * collections.xml에서 constructor-args로 색인/검색 모드를 지정한다.
 *   - constructor-args="true"  : 색인 모드 (SynonymGraphFilter 뒤에 FlattenGraphFilter 적용)
 *   - constructor-args="false" : 검색 모드
 *
 * 복합어 분해 방식(DecompoundMode)은 시스템 프로퍼티 {@code crescent.nori.decompound}로 조정한다.
 *   - NONE    : 복합어를 분해하지 않음
 *   - DISCARD : 복합어를 분해하고 원형은 버림 (기본값, 색인량 작음)
 *   - MIXED   : 복합어 원형 + 분해된 부분 모두 유지 (recall 향상, 색인량 증가)
 * 미설정 시 기본값은 DISCARD다. 색인/검색 분석기가 동일 모드를 사용하도록 같은 프로퍼티를 참조한다.
 */
public class CrescentNoriAnalyzer extends Analyzer {

	/** 복합어 분해 방식 설정 시스템 프로퍼티 키 */
	public static final String DECOMPOUND_MODE_PROPERTY = "crescent.nori.decompound";

	private final boolean indexingMode;
	private final CrescentNoriDictionary dictionary;

	public CrescentNoriAnalyzer(boolean indexingMode) {
		this.indexingMode = indexingMode;
		this.dictionary = CrescentNoriDictionary.getInstance();
	}

	/**
	 * 시스템 프로퍼티에서 DecompoundMode를 해석한다. 미설정·잘못된 값이면 기본값(DISCARD)을 사용한다.
	 */
	static KoreanTokenizer.DecompoundMode resolveDecompoundMode() {
		String value = System.getProperty(DECOMPOUND_MODE_PROPERTY);
		if (value == null || value.trim().isEmpty()) {
			return KoreanTokenizer.DEFAULT_DECOMPOUND;
		}
		try {
			return KoreanTokenizer.DecompoundMode.valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			return KoreanTokenizer.DEFAULT_DECOMPOUND;
		}
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		UserDictionary userDictionary = dictionary.getUserDictionary();

		Tokenizer tokenizer = new KoreanTokenizer(
				TokenStream.DEFAULT_TOKEN_ATTRIBUTE_FACTORY,
				userDictionary,
				resolveDecompoundMode(),
				false);

		TokenStream stream = new KoreanPartOfSpeechStopFilter(tokenizer);
		stream = new KoreanReadingFormFilter(stream);
		stream = new LowerCaseFilter(stream);

		CharArraySet stopWords = dictionary.getStopWords();
		if (stopWords != null && !stopWords.isEmpty()) {
			stream = new StopFilter(stream, stopWords);
		}

		SynonymMap synonymMap = dictionary.getSynonymMap();
		if (synonymMap != null) {
			stream = new SynonymGraphFilter(stream, synonymMap, true);
			if (indexingMode) {
				stream = new FlattenGraphFilter(stream);
			}
		}

		return new TokenStreamComponents(tokenizer, stream);
	}
}
