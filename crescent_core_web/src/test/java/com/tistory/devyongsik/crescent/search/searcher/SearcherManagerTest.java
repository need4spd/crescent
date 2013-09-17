package com.tistory.devyongsik.crescent.search.searcher;

import java.io.IOException;

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
		
		Assert.assertNotNull(crescentSearcherManager);
	}
	
	@Test
	public void getSearcher() throws IOException {
		IndexSearcher indexSearcher = null;
		SearcherManager searcherManager = crescentSearcherManager.getSearcherManager("sample");
		
		indexSearcher = searcherManager.acquire();
		
		Assert.assertNotNull(indexSearcher);
	}
}
