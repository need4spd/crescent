package com.tistory.devyongsik.service;

import java.io.IOException;

import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.domain.SearchResult;
import com.tistory.devyongsik.query.CrescentRequestQueryStrParser;
import com.tistory.devyongsik.query.DefaultKeywordParser;
import com.tistory.devyongsik.search.SearchModule;

public class SearchServiceImpl implements SearchService {

	private Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
	
	@Override
	public SearchResult search(SearchRequest searchRequest) throws IOException {
		CrescentRequestQueryStrParser crqsp 
			= new CrescentRequestQueryStrParser(searchRequest);
		
		DefaultKeywordParser keywordParser = new DefaultKeywordParser();
		Query query = keywordParser.parse(crqsp.getCollectionName()
				,crqsp.getSearchFieldNames()
				,crqsp.getKeyword()
				,new KoreanAnalyzer(false));
		
		logger.debug("query : {}" , query);
		
		SearchModule searchModule = new SearchModule(crqsp);
		SearchResult searchResult = searchModule.search();
		
		
		return searchResult;
	}

}
