package com.tistory.devyongsik.service;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.domain.SearchResult;

public class SearchServiceImplTest {

	@Test
	public void search() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setKeyword("1");
		
		SearchService searchService = new SearchServiceImpl();
		SearchResult searchResult = searchService.search(searchRequest);
		
		Assert.assertTrue(searchResult.getTotalHitsCount() > 0);	
	}
	
	@Test
	public void searchNullKeyword() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		
		SearchService searchService = new SearchServiceImpl();
		SearchResult searchResult = searchService.search(searchRequest);
		
		Assert.assertTrue(searchResult.getTotalHitsCount() == 0);
		Assert.assertEquals("Keyword is Null", searchResult.getErrorMsg());
	}
}
