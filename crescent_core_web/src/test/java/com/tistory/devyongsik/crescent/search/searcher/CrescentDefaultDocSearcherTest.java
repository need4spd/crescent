package com.tistory.devyongsik.crescent.search.searcher;

import java.io.IOException;

import javax.annotation.PostConstruct;

import junit.framework.Assert;

import org.junit.Test;

import com.tistory.devyongsik.crescent.query.CrescentSearchRequestWrapper;
import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.crescent.search.entity.SearchResult;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;

public class CrescentDefaultDocSearcherTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() {
		super.init();
	}
	
	@Test
	public void search() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("1");
		searchRequest.setCollectionName("sample");
		
		CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest);
		
		SearchResult searchResult = crescentDocSearcher.search(csrw);
		
		Assert.assertTrue(searchResult.getResultList().size() > 0);
	}
}
