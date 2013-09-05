package com.tistory.devyongsik.crescent.query;

import javax.annotation.PostConstruct;

import junit.framework.Assert;

import org.apache.lucene.search.Query;
import org.junit.Test;

import com.tistory.devyongsik.crescent.query.CrescentSearchRequestWrapper;
import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.crescent.search.exception.CrescentInvalidRequestException;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;

public class DefaultKeywordParserTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() {
		super.init();
	}

	@Test
	public void keywordParse() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setKeyword("나이키청바지");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		Query query = csrw.getQuery();
		
		System.out.println(query);
		
		Assert.assertEquals("title:나이키청바지^2.0 title:청바지^2.0 title:나이키^2.0 title:나이키청바^2.0 +dscr:나이키청바지 +dscr:청바지 +dscr:나이키 +dscr:나이키청바",
				query.toString());
	}
}
