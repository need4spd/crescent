package com.tistory.devyongsik.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tistory.devyongsik.domain.RequestBuilder;
import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.domain.SearchResult;
import com.tistory.devyongsik.service.SearchService;

@Controller
public class SearchTestMainController {
	private Logger logger = LoggerFactory.getLogger(SearchTestMainController.class);

	@Autowired
	@Qualifier("searchService")
	private SearchService searchService = null;

	@RequestMapping("/searchTestMain")
	public ModelAndView searchTestMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/searchTestMain");
		
		logger.debug("search Test main");

		return modelAndView;
	}

	@RequestMapping("/searchTest")
	public ModelAndView searchTest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		RequestBuilder<SearchRequest> requestBuilder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = requestBuilder.mappingRequestParam(request, SearchRequest.class);
		
		Map<String, Object> userRequest = new HashMap<String, Object>();
		userRequest.put("collectionName",searchRequest.getCollectionName());
		userRequest.put("customQuery",searchRequest.getCustomQuery());
		userRequest.put("keyword",searchRequest.getKeyword());
		userRequest.put("searchField",searchRequest.getSearchField());
		userRequest.put("sort",searchRequest.getSort());
		userRequest.put("pageNum",searchRequest.getPageNum());
		userRequest.put("pageSize",searchRequest.getPageSize());
		userRequest.put("filter", searchRequest.getFilter());

		SearchResult searchResult = searchService.search(searchRequest);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("searchResult", searchResult);
		modelAndView.addObject("USER_REQUEST", userRequest);
		modelAndView.setViewName("/admin/searchTestMain");

		return modelAndView;
	}

}
