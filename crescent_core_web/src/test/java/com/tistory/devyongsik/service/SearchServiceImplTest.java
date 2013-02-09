package com.tistory.devyongsik.service;

import java.io.IOException;

import javax.annotation.PostConstruct;

import junit.framework.Assert;

import org.junit.Test;

import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.domain.SearchResult;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;

public class SearchServiceImplTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() {
		super.init();
	}
	
	@Test
	public void search() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setKeyword("1");
		
		SearchService searchService = new SearchServiceImpl();
		SearchResult searchResult = searchService.search(searchRequest);
		
		Assert.assertTrue(searchResult.getTotalHitsCount() > 0);	
	}
}
