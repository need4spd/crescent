package com.tistory.devyongsik.crescent.search.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.tistory.devyongsik.crescent.query.CrescentSearchRequestWrapper;
import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.crescent.search.entity.SearchRequestValidator;
import com.tistory.devyongsik.crescent.search.entity.SearchResult;
import com.tistory.devyongsik.crescent.search.exception.CrescentInvalidRequestException;
import com.tistory.devyongsik.crescent.search.searcher.CrescentDocSearcher;

@Service("searchService")
public class SearchServiceImpl implements SearchService {

	private Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
	
	@Autowired
	@Qualifier("crescentDefaultDocSearcher")
	private CrescentDocSearcher crescentDocSearcher;
	
	@Override
	public SearchResult search(SearchRequest searchRequest) throws IOException {
		
		Query query = null;
		CrescentSearchRequestWrapper csrw 
				= new CrescentSearchRequestWrapper(searchRequest);
	
		try {
			
			SearchRequestValidator validator = new SearchRequestValidator();
			validator.isValid(searchRequest);
			
			
			query = csrw.getQuery();
		
		} catch (CrescentInvalidRequestException e) {
			SearchResult searchResult = new SearchResult();
			
			Map<String, Object> result = new HashMap<String, Object>();
			List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
			
			result.put("total_count", 0);
			result.put("result_list", resultList);
			result.put("error_code", -1);
			result.put("error_msg", e.getMessage());
			
			logger.error("검색 중 에러 발생함." , e);
			
			searchResult.setErrorCode(-1);
			searchResult.setErrorMsg(e.getMessage());
			searchResult.setSearchResult(result);
			searchResult.setResultList(resultList);
			
			return searchResult;
		}
		
		logger.debug("query : {}" , query);
		
		SearchResult searchResult = crescentDocSearcher.search(csrw);
		
		return searchResult;
	}

}
