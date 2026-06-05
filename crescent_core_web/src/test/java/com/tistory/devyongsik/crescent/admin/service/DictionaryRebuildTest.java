package com.tistory.devyongsik.crescent.admin.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.dictionary.CrescentDictionaryType;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;

/**
 * 사전 단어 추가 후 rebuildDictionary()가 컬렉션 분석기에 반영되는지(분석기 재생성 전파) 검증한다.
 * 파일에는 기록하지 않고(addWordToDictionary만 사용) 메모리 상태만 변경한다.
 */
public class DictionaryRebuildTest extends CrescentTestCaseUtil {

	@Autowired
	@Qualifier("dictionaryService")
	private DictionaryService dictionaryService;

	@PostConstruct
	public void init() {
		super.init();
	}

	private List<String> tokenize(Analyzer analyzer, String text) throws IOException {
		List<String> tokens = new ArrayList<>();
		TokenStream stream = analyzer.tokenStream("f", new StringReader(text));
		CharTermAttribute term = stream.getAttribute(CharTermAttribute.class);
		stream.reset();
		while (stream.incrementToken()) {
			tokens.add(term.toString());
		}
		stream.end();
		stream.close();
		return tokens;
	}

	@Test
	public void addedCustomNounIsReflectedAfterRebuild() throws IOException {
		String madeUpNoun = "크레센트노리테스트단어";

		// 등록 전: 사용자 명사가 아니므로 단일 토큰이 아님
		Analyzer before = collectionHandler.getCrescentCollections()
				.getCrescentCollection("sample").getSearchModeAnalyzer();
		List<String> beforeTokens = tokenize(before, madeUpNoun);
		Assert.assertFalse("등록 전에는 단일 토큰으로 인식되지 않아야 함",
				beforeTokens.size() == 1 && beforeTokens.contains(madeUpNoun));

		// 사용자 명사 추가 후 재빌드 → 분석기 재생성 전파
		dictionaryService.addWordToDictionary(CrescentDictionaryType.CUSTOM, madeUpNoun);
		dictionaryService.rebuildDictionary(CrescentDictionaryType.CUSTOM);

		// 등록 후: 재생성된 분석기를 컬렉션에서 다시 가져와 단일 토큰으로 인식되는지 확인
		Analyzer after = collectionHandler.getCrescentCollections()
				.getCrescentCollection("sample").getSearchModeAnalyzer();
		List<String> afterTokens = tokenize(after, madeUpNoun);
		Assert.assertTrue("재빌드 후 사용자 명사가 단일 토큰으로 인식되어야 함",
				afterTokens.contains(madeUpNoun));

		// 정리: 메모리 사전에서 제거하고 재빌드 (다른 테스트 영향 최소화)
		dictionaryService.removeWordFromDictionary(CrescentDictionaryType.CUSTOM, madeUpNoun);
		dictionaryService.rebuildDictionary(CrescentDictionaryType.CUSTOM);
	}
}
