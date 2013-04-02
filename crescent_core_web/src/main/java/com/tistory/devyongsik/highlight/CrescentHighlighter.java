package com.tistory.devyongsik.highlight;

import java.io.IOException;
import java.io.StringReader;

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

import com.tistory.devyongsik.domain.CrescentCollectionField;
import com.tistory.devyongsik.exception.CrescentInvalidRequestException;

@Deprecated
public class CrescentHighlighter {
	private Logger logger = LoggerFactory.getLogger(CrescentHighlighter.class);

	private SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<b>","</b>");

	public String getBestFragment(CrescentCollectionField field, String value, Query query, Analyzer analyzer) throws CrescentInvalidRequestException {
		String fragment = "";

		logger.debug("fieldName : {}", field.getName());

		try {
			
			logger.debug("query for highlighter : {}" , query);

			QueryScorer scorer = new QueryScorer(query);

			Highlighter highlighter = new Highlighter(formatter, scorer);
			highlighter.setTextFragmenter(new SimpleFragmenter(50));


			TokenStream stream = analyzer.reusableTokenStream(field.getName(), new StringReader(value));
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

	}
}
