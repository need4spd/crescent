package com.tistory.devyongsik.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
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

import com.google.gson.Gson;
import com.tistory.devyongsik.admin.MorphService;
import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollections;
import com.tistory.devyongsik.domain.MorphResult;
import com.tistory.devyongsik.domain.MorphToken;

@Controller
public class MorphAdminMainController {
	private Logger logger = LoggerFactory.getLogger(MorphAdminMainController.class);

	@Autowired
	@Qualifier("morphService")
	private MorphService morphService = null;
	
	@Autowired
	@Qualifier("crescentCollectionHandler")
	private CrescentCollectionHandler collectionHandler;

	@RequestMapping("/morphMain")
	public ModelAndView morphMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
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
		
		modelAndView.setViewName("/admin/morphMain");

		return modelAndView;
	}
	
	@RequestMapping("/doMorphTest")
	public ModelAndView morphTest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String keyword = request.getParameter("keyword");
		
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
		
		
		logger.debug("keyword : {}, collectionName : {}", keyword, selectedCollectionName);
		
		List<MorphToken> resultTokenListIndexingMode = morphService.getTokens(keyword, true, selectedCollectionName);
		List<MorphToken> resultTokenListQueryMode = morphService.getTokens(keyword, false, selectedCollectionName);
		
		modelAndView.setViewName("/admin/morphMain");

		modelAndView.addObject("indexingModeList", resultTokenListIndexingMode);
		modelAndView.addObject("queryModeList", resultTokenListQueryMode);
		
		return modelAndView;
	}
	
	@RequestMapping("/doMorphTestAjax")
	public void morphTestAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String keyword = request.getParameter("keyword");
		String selectedCollectionName = request.getParameter("collectionName");
		
		logger.debug("keyword : {}, collectionName : {}", keyword, selectedCollectionName);
		
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		
		if(selectedCollectionName == null) {
			selectedCollectionName = crescentCollections.getCrescentCollections().get(0).getName();
		}
		
		List<MorphToken> resultTokenListIndexingMode = morphService.getTokens(keyword, true, selectedCollectionName);
		List<MorphToken> resultTokenListQueryMode = morphService.getTokens(keyword, false, selectedCollectionName);
		
		List<MorphResult> morphIndexingTestResult = new ArrayList<MorphResult>();
		List<MorphResult> morphQueryTestResult = new ArrayList<MorphResult>();
		
		Gson gson = new Gson();
		
		Map<String, List<MorphResult>> morphTestResultSet = new HashMap<String, List<MorphResult>>();
		
		for(MorphToken token : resultTokenListIndexingMode) {
			MorphResult morphResult = new MorphResult();
			morphResult.setWord(token.getTerm());
			morphResult.setType(token.getType());
			morphResult.setStartOffset(token.getStartOffset());
			morphResult.setEndOffset(token.getEndOffset());
			
			morphIndexingTestResult.add(morphResult);
		}
		
		for(MorphToken token : resultTokenListQueryMode) {
			MorphResult morphResult = new MorphResult();
			morphResult.setWord(token.getTerm());
			morphResult.setType(token.getType());
			morphResult.setStartOffset(token.getStartOffset());
			morphResult.setEndOffset(token.getEndOffset());
			
			morphQueryTestResult.add(morphResult);
		}
		
		morphTestResultSet.put("indexResult", morphIndexingTestResult);
		morphTestResultSet.put("queryResult", morphQueryTestResult);
		
		String morphResult = gson.toJson(morphTestResultSet);
		
		logger.info("morphResult : {}", morphResult);
		
		response.setContentType("application/json;  charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(morphResult);
		writer.flush();
		writer.close();
	}
}
