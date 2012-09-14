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
		searchRequest.setKeyword("제목");
		searchRequest.setCollectionName("sample");
		
		CrescentRequestQueryStrParser crqsp = new CrescentRequestQueryStrParser(searchRequest);
		
		SearchModule searchModule = new SearchModule(crqsp);
		SearchResult searchResult = searchModule.search();
		
		Assert.assertTrue(searchResult.getTotalHitsCount() > 0);
		Assert.assertTrue(searchResult.getResultList().size() > 0);
		Assert.assertTrue(searchResult.getResultList().size() == 100);
	}
	
	@Test
	public void searchChangePageSize10() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("제목");
		searchRequest.setCollectionName("sample");
		searchRequest.setPageSize("10");
		
		CrescentRequestQueryStrParser crqsp = new CrescentRequestQueryStrParser(searchRequest);
		
		SearchModule searchModule = new SearchModule(crqsp);
		SearchResult searchResult = searchModule.search();
		
		Assert.assertTrue(searchResult.getTotalHitsCount() > 0);
		Assert.assertTrue(searchResult.getResultList().size() > 0);
		Assert.assertTrue(searchResult.getResultList().size() == 10);
	}
	
	@Test
	public void searchChangePageSize30() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("제목");
		searchRequest.setCollectionName("sample");
		searchRequest.setPageSize("30");
		
		CrescentRequestQueryStrParser crqsp = new CrescentRequestQueryStrParser(searchRequest);
		
		SearchModule searchModule = new SearchModule(crqsp);
		SearchResult searchResult = searchModule.search();
		
		Assert.assertTrue(searchResult.getTotalHitsCount() > 0);
		Assert.assertTrue(searchResult.getResultList().size() > 0);
		Assert.assertTrue(searchResult.getResultList().size() == 30);
	}
	
	@Test
	public void searchChangePageSize80() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("제목");
		searchRequest.setCollectionName("sample");
		searchRequest.setPageSize("80");
		
		CrescentRequestQueryStrParser crqsp = new CrescentRequestQueryStrParser(searchRequest);
		
		SearchModule searchModule = new SearchModule(crqsp);
		SearchResult searchResult = searchModule.search();
		
		Assert.assertTrue(searchResult.getTotalHitsCount() > 0);
		Assert.assertTrue(searchResult.getResultList().size() > 0);
		Assert.assertTrue(searchResult.getResultList().size() == 80);
	}
	
	@Test
	public void searchChangePageNum5() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("제목");
		searchRequest.setCollectionName("sample");
		searchRequest.setPageSize("5");
		searchRequest.setPageNum("5");
		
		CrescentRequestQueryStrParser crqsp = new CrescentRequestQueryStrParser(searchRequest);
		
		SearchModule searchModule = new SearchModule(crqsp);
		SearchResult searchResult = searchModule.search();
		
		Assert.assertTrue(searchResult.getTotalHitsCount() > 0);
		Assert.assertTrue(searchResult.getResultList().size() > 0);
		Assert.assertTrue(searchResult.getResultList().size() == 5);
	}
}
