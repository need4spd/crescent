package com.tistory.devyongsik.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tistory.devyongsik.domain.RequestBuilder;
import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.service.SearchService;
import com.tistory.devyongsik.service.SearchServiceImpl;

@Controller
public class SearchController {
	private Logger logger = LoggerFactory.getLogger(SearchController.class);

	@RequestMapping("/search")
	public void updateDocument(HttpServletRequest request, HttpServletResponse response) throws Exception {

		RequestBuilder<SearchRequest> requestBuilder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = requestBuilder.mappingRequestParam(request, SearchRequest.class);
		
		SearchService searchService = new SearchServiceImpl();
		List<Document> searchResultList = searchService.search(searchRequest);
		
		logger.debug("search result : {}", searchResultList);
	}
}
