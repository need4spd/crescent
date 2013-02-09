package com.tistory.devyongsik.search;

import javax.annotation.PostConstruct;

import junit.framework.Assert;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.junit.Test;

import com.tistory.devyongsik.utils.CrescentTestCaseUtil;

public class SearcherManagerTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() {
		super.init();
	}
	
	@Test
	public void initSearcherManager() {
		CrescentSearcherManager searcherManager = CrescentSearcherManager.getCrescentSearcherManager();
		
		Assert.assertNotNull(searcherManager);
	}
	
	@Test
	public void getSearcher() {
		IndexSearcher indexSearcher = null;
		SearcherManager searcherManager = CrescentSearcherManager.getCrescentSearcherManager().getSearcherManager("sample");
		
		indexSearcher = searcherManager.acquire();
		
		Assert.assertNotNull(indexSearcher);
	}
}
