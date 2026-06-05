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
 */
public class CrescentNoriAnalyzer extends Analyzer {

	private final boolean indexingMode;
	private final CrescentNoriDictionary dictionary;

	public CrescentNoriAnalyzer(boolean indexingMode) {
		this.indexingMode = indexingMode;
		this.dictionary = CrescentNoriDictionary.getInstance();
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		UserDictionary userDictionary = dictionary.getUserDictionary();

		Tokenizer tokenizer = new KoreanTokenizer(
				TokenStream.DEFAULT_TOKEN_ATTRIBUTE_FACTORY,
				userDictionary,
				KoreanTokenizer.DEFAULT_DECOMPOUND,
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
