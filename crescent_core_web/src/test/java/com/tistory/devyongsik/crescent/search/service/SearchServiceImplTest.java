package com.tistory.devyongsik.crescent.search.service;

import java.io.IOException;

import javax.annotation.PostConstruct;

import junit.framework.Assert;

import org.junit.Test;

import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.crescent.search.entity.SearchResult;
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
		
		SearchResult searchResult = searchService.search(searchRequest);
		
		Assert.assertTrue(searchResult.getTotalHitsCount() > 0);	
	}
}
