package com.tistory.devyongsik.search;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.lucene.search.ScoreDoc;
import org.junit.Test;

import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.query.CrescentRequestQueryStrParser;

public class CrescentDefaultDocSearcherTest {

	@Test
	public void search() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("2");
		searchRequest.setCollectionName("sample");
		
		CrescentRequestQueryStrParser crqsp = new CrescentRequestQueryStrParser(searchRequest);
		
		CrescentDocSearcher crescentDocSearcher = new CrescentDefaultDocSearcher(crqsp);
		ScoreDoc[] scoreDocs = crescentDocSearcher.search();
		
		Assert.assertTrue(scoreDocs.length > 0);
	}
}
