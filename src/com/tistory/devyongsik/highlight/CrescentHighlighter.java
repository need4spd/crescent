package com.tistory.devyongsik.highlight;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.tistory.devyongsik.query.CrescentSearchRequestWrapper;
import com.tistory.devyongsik.query.DefaultKeywordParser;

public class CrescentHighlighter {
	private Logger logger = LoggerFactory.getLogger(CrescentHighlighter.class);
	//private Highlighter highlighter;
	private List<String> searchFieldsNameList;
	//TODO Analyzer 동적으로 생성하도록..
	private Analyzer analyzer = new KoreanAnalyzer(false);
	private CrescentSearchRequestWrapper csrw = null;
	private DefaultKeywordParser keywordParser = new DefaultKeywordParser();
	private SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<b>","</b>");
    
	public CrescentHighlighter(CrescentSearchRequestWrapper csrw) {
		this.csrw = csrw;
		this.searchFieldsNameList = new ArrayList<String>(Arrays.asList(csrw.getSearchFieldNames()));
	
		logger.debug("searchFieldsNameList : {}", searchFieldsNameList);
		
	}
	
	public String getBestFragment(String fieldName, String value) {
		String fragment = "";
		
		logger.debug("fieldName : {}", fieldName);
		
		if(searchFieldsNameList.contains(fieldName)) {
			
			try {
				String[] fields = {fieldName};
				Query query = keywordParser.parse(csrw.getCollectionName()
						,fields
						,csrw.getKeyword()
						, analyzer);
				
				logger.debug("query for highlighter : {}" , query);
				
				QueryScorer scorer = new QueryScorer(query);
				
				Highlighter highlighter = new Highlighter(formatter, scorer);
			    highlighter.setTextFragmenter(new SimpleFragmenter(50));
			    
				
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
