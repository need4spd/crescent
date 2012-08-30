package com.tistory.devyongsik.highlight;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.query.CrescentRequestQueryStrParser;
import com.tistory.devyongsik.query.DefaultKeywordParser;

public class CrescentHighlighter {
	private Logger logger = LoggerFactory.getLogger(CrescentHighlighter.class);
	private Highlighter highlighter;
	private ArrayList<String> searchFieldsNameList;
	//TODO Analyzer 동적으로 생성하도록..
	private Analyzer analyzer = new KoreanAnalyzer(false);
	
	public CrescentHighlighter(CrescentRequestQueryStrParser crqsp) {
		
		DefaultKeywordParser keywordParser = new DefaultKeywordParser();
		Query query = keywordParser.parse(crqsp, analyzer);
		
		logger.debug("query for highlighter : {}" , query);
		
		QueryScorer scorer = new QueryScorer(query);
		
		SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span class=\"hl\">","</span>");
	    highlighter = new Highlighter(formatter, scorer);
	    highlighter.setTextFragmenter(new SimpleFragmenter(50));
	    
		this.searchFieldsNameList = new ArrayList<String>(Arrays.asList(crqsp.getSearchFieldNames()));
	}
	
	public String getBestFragment(String fieldName,String value) {
		String fragment = "";
		
		if(searchFieldsNameList.contains(fieldName)) {
			try {
				TokenStream stream = analyzer.reusableTokenStream(fieldName, new StringReader(value));
				fragment = highlighter.getBestFragments(stream, value, 1, "...");
				
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
