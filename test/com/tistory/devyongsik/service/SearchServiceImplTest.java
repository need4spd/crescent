package com.tistory.devyongsik.service;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.junit.Test;

import com.tistory.devyongsik.domain.SearchRequest;

public class SearchServiceImplTest {

	@Test
	public void search() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setKeyword("타이틀");
		
		SearchService searchService = new SearchServiceImpl();
		List<Document> searchResult = searchService.search(searchRequest);
		
		
	}
}
