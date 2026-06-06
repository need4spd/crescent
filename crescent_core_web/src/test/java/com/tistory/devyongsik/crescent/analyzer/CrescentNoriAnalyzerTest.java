package com.tistory.devyongsik.crescent.analyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Assert;
import org.junit.Test;

/**
 * CrescentNoriAnalyzer가 사용자 사전(명사/복합명사/불용어/동의어)을 실제로 적용하는지 검증한다.
 * 사전 파일은 src/main/resources의 custom.txt / compounds.txt / stop.txt / synonym.txt를 사용한다.
 */
public class CrescentNoriAnalyzerTest {

	private List<String> tokenize(boolean indexingMode, String text) throws IOException {
		List<String> tokens = new ArrayList<>();
		try (Analyzer analyzer = new CrescentNoriAnalyzer(indexingMode)) {
			TokenStream stream = analyzer.tokenStream("f", new StringReader(text));
			CharTermAttribute term = stream.getAttribute(CharTermAttribute.class);
			stream.reset();
			while (stream.incrementToken()) {
				tokens.add(term.toString());
			}
			stream.end();
			stream.close();
		}
		return tokens;
	}

	@Test
	public void compoundNounIsDecomposedByUserDictionary() throws IOException {
		// compounds.txt: 맥북에어:맥북,에어
		List<String> tokens = tokenize(true, "맥북에어");
		Assert.assertTrue("복합명사 분해 결과에 '맥북' 포함", tokens.contains("맥북"));
		Assert.assertTrue("복합명사 분해 결과에 '에어' 포함", tokens.contains("에어"));
	}

	@Test
	public void customNounIsRecognized() throws IOException {
		// custom.txt: 청바지 (사용자 명사) → 나이키청바지가 [나이키][청바지]로 분리
		List<String> tokens = tokenize(false, "나이키청바지");
		Assert.assertTrue("사용자 명사 '청바지' 인식", tokens.contains("청바지"));
	}

	@Test
	public void stopWordIsRemoved() throws IOException {
		// stop.txt: "를" 포함 → 불용어 제거
		List<String> tokens = tokenize(false, "프로그래밍을 를");
		Assert.assertFalse("불용어 '를'는 제거되어야 함", tokens.contains("를"));
	}

	@Test
	public void synonymIsExpandedAtSearchTime() throws IOException {
		// synonym.txt: 오라클,oracle → 검색 모드에서 동의어 확장
		List<String> tokens = tokenize(false, "오라클");
		Assert.assertTrue("동의어 'oracle' 확장", tokens.contains("oracle"));
		Assert.assertTrue("원본 '오라클' 유지", tokens.contains("오라클"));
	}
}
