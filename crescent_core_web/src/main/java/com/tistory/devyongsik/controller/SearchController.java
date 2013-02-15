package com.tistory.devyongsik.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tistory.devyongsik.domain.RequestBuilder;
import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.domain.SearchResult;
import com.tistory.devyongsik.search.JsonFormConverter;
import com.tistory.devyongsik.service.SearchService;

@Controller
public class SearchController {
	private Logger logger = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	@Qualifier("searchService")
	private SearchService searchService;
	
	@RequestMapping("/search")
	public void searchDocument(HttpServletRequest request, HttpServletResponse response) throws Exception {

		RequestBuilder<SearchRequest> requestBuilder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = requestBuilder.mappingRequestParam(request, SearchRequest.class);
		
		SearchResult searchResult = searchService.search(searchRequest);
		
		logger.debug("search result : {}", searchResult.getResultList());
		
		JsonFormConverter converter = new JsonFormConverter();
		PrintWriter writer = null;
		try {
			
			String jsonForm = converter.convert(searchResult.getSearchResult());
			
			logger.debug("search result json form : {}", jsonForm);
			
			response.setContentType("application/json;  charset=UTF-8");
			
			writer = response.getWriter();
			writer.write(jsonForm);
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			logger.error("error : ", e);
		}
	}
}
