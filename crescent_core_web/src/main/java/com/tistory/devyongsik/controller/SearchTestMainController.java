package com.tistory.devyongsik.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	private SearchService searchService = null;

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	@RequestMapping("/searchTestMain")
	public ModelAndView searchTestMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/searchTestMain");
		
		logger.debug("search Test main");

		return modelAndView;
	}

	@RequestMapping("/searchTest")
	public ModelAndView searchTest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		System.out.println("!!!! : " + request.getParameter("keyword"));
		RequestBuilder<SearchRequest> requestBuilder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = requestBuilder.mappingRequestParam(request, SearchRequest.class);

		SearchResult searchResult = searchService.search(searchRequest);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("searchResult", searchResult);
		modelAndView.setViewName("/admin/searchTestMain");

		return modelAndView;
	}

}
