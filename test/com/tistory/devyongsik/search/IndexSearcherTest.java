package com.tistory.devyongsik.search;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

public class IndexSearcherTest {

	@Test
	public void defaultSearch() throws IOException {
		SearcherManager searcherManager = SearcherManager.getSearcherManager();
		IndexSearcher indexSearcher = searcherManager.getIndexSearcher("sample");
		
		Term t = new Term("title", "2");
		Query q = new TermQuery(t);
		
		TopDocs topDocs = indexSearcher.search(q, 5);
		
		int totalCount = topDocs.totalHits;
		
		System.out.print("total count : " + totalCount);
		
		Assert.assertTrue(totalCount > 0);
	}
}
