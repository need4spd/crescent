package com.tistory.devyongsik.highlight;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrescentFastVectorHighlighter {
	private Logger logger = LoggerFactory.getLogger(CrescentFastVectorHighlighter.class);
	private FastVectorHighlighter highlighter = new FastVectorHighlighter();
	
	public String getBestFragment(IndexReader indexReader, int docId, Query query, String fieldName) {
		
		logger.debug("get highlight... {}, {}", docId, query);
		
		try {
			
			String fragment = highlighter.getBestFragment(highlighter.getFieldQuery(query),
					indexReader, docId, fieldName, 200);
			
			return fragment;
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return "";
		}
		
	}

}
