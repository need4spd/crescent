package com.tistory.devyongsik.query;

import org.apache.lucene.search.Query;
import org.junit.Test;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.domain.SearchRequest;

public class DefaultKeywordParserTest {

	@Test
	public void keywordParse() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setKeyword("나이키청바지");
		
		CrescentRequestQueryStrParser crqsp 
			= new CrescentRequestQueryStrParser(searchRequest);
		
		DefaultKeywordParser parser = new DefaultKeywordParser();
		Query query = parser.parse(crqsp.getCollectionName()
				,crqsp.getSearchFieldNames()
				,crqsp.getKeyword()
				,new KoreanAnalyzer(false));
		
		System.out.println(query);
	}
}
