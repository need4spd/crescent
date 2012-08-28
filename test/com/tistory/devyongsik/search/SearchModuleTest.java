package com.tistory.devyongsik.search;

import junit.framework.Assert;

import org.junit.Test;

import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.domain.SearchResult;
import com.tistory.devyongsik.query.CrescentRequestQueryStrParser;

public class SearchModuleTest {

	@Test
	public void search() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("2");
		searchRequest.setCollectionName("sample");
		
		CrescentRequestQueryStrParser crqsp = new CrescentRequestQueryStrParser(searchRequest);
		
		SearchModule searchModule = new SearchModule(crqsp);
		SearchResult searchResult = searchModule.search();
		
		Assert.assertTrue(searchResult.getTotalHitsCount() > 0);
		Assert.assertTrue(searchResult.getResultList().size() > 0);
	}
}
