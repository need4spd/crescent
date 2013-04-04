package com.tistory.devyongsik.controller;

import java.util.HashMap;
import java.util.List;
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

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollections;
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
	
	@Autowired
	@Qualifier("crescentCollectionHandler")
	private CrescentCollectionHandler collectionHandler;

	@RequestMapping("/searchTestMain")
	public ModelAndView searchTestMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		
		String selectedCollectionName = request.getParameter("col_name");
		if(selectedCollectionName == null) {
			selectedCollectionName = crescentCollections.getCrescentCollections().get(0).getName();
		}
		
		ModelAndView modelAndView = new ModelAndView();
		//modelAndView.addObject("crescentCollections", crescentCollections);
		modelAndView.addObject("selectedCollectionName", selectedCollectionName);
		
		List<CrescentCollection> crescentCollectionList = crescentCollections.getCrescentCollections();
		
		modelAndView.addObject("crescentCollectionList", crescentCollectionList);
		modelAndView.addObject("selectedCollection", crescentCollections.getCrescentCollection(selectedCollectionName));
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
		userRequest.put("ft", searchRequest.getFilter());
		userRequest.put("rq", searchRequest.getRegexQuery());

		SearchResult searchResult = searchService.search(searchRequest);
		
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		
		String selectedCollectionName = request.getParameter("col_name");
		if(selectedCollectionName == null) {
			selectedCollectionName = crescentCollections.getCrescentCollections().get(0).getName();
		}
		
		ModelAndView modelAndView = new ModelAndView();
		//modelAndView.addObject("crescentCollections", crescentCollections);
		modelAndView.addObject("selectedCollectionName", selectedCollectionName);
		
		List<CrescentCollection> crescentCollectionList = crescentCollections.getCrescentCollections();
		
		modelAndView.addObject("crescentCollectionList", crescentCollectionList);
		modelAndView.addObject("selectedCollection", crescentCollections.getCrescentCollection(selectedCollectionName));
		
		modelAndView.addObject("searchResult", searchResult);
		modelAndView.addObject("USER_REQUEST", userRequest);
		modelAndView.setViewName("/admin/searchTestMain");

		return modelAndView;
	}

}
