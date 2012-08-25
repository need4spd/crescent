package com.tistory.devyongsik.search;

import junit.framework.Assert;

import org.apache.lucene.search.IndexSearcher;
import org.junit.BeforeClass;
import org.junit.Test;

public class SearcherManagerTest {

	@BeforeClass
	public static void initSearcherManager() {
		SearcherManager searcherManager = SearcherManager.getSearcherManager();
		
		Assert.assertNotNull(searcherManager);
	}
	
	@Test
	public void getSearcher() {
		IndexSearcher indexSearcher = null;
		SearcherManager searcherManager = SearcherManager.getSearcherManager();
		
		indexSearcher = searcherManager.getIndexSearcher("sample");
		Assert.assertNotNull(indexSearcher);
	}
}
