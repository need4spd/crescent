package com.tistory.devyongsik.search;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.domain.SearchResult;
import com.tistory.devyongsik.query.CrescentSearchRequestWrapper;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;

public class SearchModuleTest {

	@BeforeClass
	public static void init() {
		CrescentTestCaseUtil.init();
	}
	
	@Test
	public void search() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("텍스트");
		searchRequest.setCollectionName("sample");
		
		CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest);
		
		SearchModule searchModule = new SearchModule(csrw);
		SearchResult searchResult = searchModule.search();
		
		Assert.assertTrue(searchResult.getTotalHitsCount() > 0);
		Assert.assertTrue(searchResult.getResultList().size() > 0);
	}
	
	@Test
	public void searchChangePageSize1() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("텍스트");
		searchRequest.setCollectionName("sample");
		searchRequest.setPageSize("1");
		
		CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest);
		
		SearchModule searchModule = new SearchModule(csrw);
		SearchResult searchResult = searchModule.search();
		
		Assert.assertTrue(searchResult.getTotalHitsCount() > 0);
		Assert.assertTrue(searchResult.getResultList().size() > 0);
		Assert.assertTrue(searchResult.getResultList().size() == 1);
	}
	
	@Test
	public void searchChangePageSize2() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("텍스트");
		searchRequest.setCollectionName("sample");
		searchRequest.setPageSize("2");
		
		CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest);
		
		SearchModule searchModule = new SearchModule(csrw);
		SearchResult searchResult = searchModule.search();
		
		Assert.assertTrue(searchResult.getTotalHitsCount() > 0);
		Assert.assertTrue(searchResult.getResultList().size() > 0);
		Assert.assertTrue(searchResult.getResultList().size() == 2);
	}
	
	@Test
	public void searchChangePageSize3() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("텍스트");
		searchRequest.setCollectionName("sample");
		searchRequest.setPageSize("3");
		
		CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest);
		
		SearchModule searchModule = new SearchModule(csrw);
		SearchResult searchResult = searchModule.search();
		
		Assert.assertTrue(searchResult.getTotalHitsCount() > 0);
		Assert.assertTrue(searchResult.getResultList().size() > 0);
		Assert.assertTrue(searchResult.getResultList().size() == 3);
	}
	
	@Test
	public void searchChangePageNum5() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("텍스트");
		searchRequest.setCollectionName("sample");
		searchRequest.setPageSize("1");
		searchRequest.setPageNum("3");
		
		CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest);
		
		SearchModule searchModule = new SearchModule(csrw);
		SearchResult searchResult = searchModule.search();
		
		Assert.assertTrue(searchResult.getTotalHitsCount() > 0);
		Assert.assertTrue(searchResult.getResultList().size() > 0);
		Assert.assertTrue(searchResult.getResultList().size() == 1);
	}
}
