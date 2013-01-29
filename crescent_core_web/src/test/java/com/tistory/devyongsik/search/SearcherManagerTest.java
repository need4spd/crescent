package com.tistory.devyongsik.search;

import junit.framework.Assert;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tistory.devyongsik.utils.CrescentTestCaseUtil;

public class SearcherManagerTest {

	@BeforeClass
	public static void init() {
		CrescentTestCaseUtil.init();
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
