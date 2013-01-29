package com.tistory.devyongsik.search;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tistory.devyongsik.utils.CrescentTestCaseUtil;

public class IndexSearcherTest {

	@BeforeClass
	public static void init() {
		CrescentTestCaseUtil.init();
	}
	@Test
	public void defaultSearch() throws IOException {
		SearcherManager searcherManager = CrescentSearcherManager.getCrescentSearcherManager().getSearcherManager("sample");
		IndexSearcher indexSearcher = searcherManager.acquire();
		
		Term t = new Term("title", "2");
		Query q = new TermQuery(t);
		
		TopDocs topDocs = indexSearcher.search(q, 5);
		
		int totalCount = topDocs.totalHits;
		
		System.out.print("total count : " + totalCount);
		
		Assert.assertTrue(totalCount > 0);
	}
}
