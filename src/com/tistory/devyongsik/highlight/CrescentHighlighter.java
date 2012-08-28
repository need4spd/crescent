package com.tistory.devyongsik.highlight;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.query.CrescentRequestQueryStrParser;
import com.tistory.devyongsik.query.DefaultKeywordParser;

public class CrescentHighlighter {
	private Logger logger = LoggerFactory.getLogger(CrescentHighlighter.class);
	private Highlighter highlighter;
	private ArrayList<String> searchFieldsNameList;
	private Fragmenter fragmenter;
	//TODO Analyzer 동적으로 생성하도록..
	private Analyzer analyzer = new KoreanAnalyzer(false);
	
	public CrescentHighlighter(CrescentRequestQueryStrParser crqsp) {
		
		DefaultKeywordParser keywordParser = new DefaultKeywordParser();
		Query query = keywordParser.parse(crqsp, analyzer);
		
		QueryScorer scorer = new QueryScorer(query);
		highlighter = new Highlighter(scorer);
		highlighter.setMaxDocCharsToAnalyze(1000);
		this.searchFieldsNameList = new ArrayList<String>(Arrays.asList(crqsp.getSearchFieldNames()));
		fragmenter = new SimpleFragmenter(100);
		
		highlighter.setTextFragmenter(fragmenter);
	}
	
	public String getBestFragment(String fieldName,String value) {
		String fragment = "";
		
		if(searchFieldsNameList.contains(fieldName)) {
			TokenStream stream = analyzer.tokenStream("", new StringReader(value));
			try {
				fragment = highlighter.getBestFragments(stream, value, 3, "...");
				if(fragment == null || "".equals(fragment)) {
					if(value.length() > 100) {
						fragment = value.substring(0,100);
					} else {
						fragment = value;
					}
				}
				
				return fragment;
			} catch (IOException e) {
				
				logger.error("error in crescent highlighter", e);
				
				return value;
			} catch (InvalidTokenOffsetsException e) {
				
				logger.error("error in crescent highlighter", e);
				
				return value;
			}
		} else {
			return value;
		}
	}
}
