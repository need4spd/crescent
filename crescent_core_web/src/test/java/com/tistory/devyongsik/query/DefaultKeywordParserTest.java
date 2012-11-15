package com.tistory.devyongsik.query;

import junit.framework.Assert;

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
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		DefaultKeywordParser parser = new DefaultKeywordParser();
		Query query = parser.parse(csrw.getCollectionName()
				,csrw.getTargetSearchFields()
				,csrw.getKeyword()
				,new KoreanAnalyzer(false));
		
		System.out.println(query);
		
		Assert.assertEquals("title:나이키청바지^2.0 title:청바지^2.0 title:나이키^2.0 title:나이키청바^2.0 dscr:나이키청바지^0.0 +dscr:청바지^0.0 +dscr:나이키^0.0 +dscr:나이키청바^0.0",
				query.toString());
	}
}
