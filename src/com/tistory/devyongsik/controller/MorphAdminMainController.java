package com.tistory.devyongsik.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.tistory.devyongsik.admin.MorphService;
import com.tistory.devyongsik.domain.MorphResult;

@Controller
public class MorphAdminMainController {
	private Logger logger = LoggerFactory.getLogger(MorphAdminMainController.class);

	@Autowired
	private MorphService morphService = null;

	public void setMorphService(MorphService morphService) {
		this.morphService = morphService;
	}

	@RequestMapping("/morphMain")
	public ModelAndView morphMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/morphMain");

		return modelAndView;
	}
	
	@RequestMapping("/doMorphTest")
	public ModelAndView morphTest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String keyword = request.getParameter("keyword");
		
		logger.debug("keyword : {}", keyword);
		
		List<Token> resultTokenListIndexingMode = morphService.getTokens(keyword, true);
		List<Token> resultTokenListQueryMode = morphService.getTokens(keyword, false);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/morphMain");

		modelAndView.addObject("resultTokenListIndexingMode", resultTokenListIndexingMode);
		modelAndView.addObject("resultTokenListQueryMode", resultTokenListQueryMode);
		
		return modelAndView;
	}
	
	@RequestMapping("/doMorphTestAjax")
	public void morphTestAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String keyword = request.getParameter("keyword");
		
		logger.debug("keyword : {}", keyword);
		
		List<Token> resultTokenListIndexingMode = morphService.getTokens(keyword, true);
		List<Token> resultTokenListQueryMode = morphService.getTokens(keyword, false);
		
		List<MorphResult> morphIndexingTestResult = new ArrayList<MorphResult>();
		List<MorphResult> morphQueryTestResult = new ArrayList<MorphResult>();
		
		Gson gson = new Gson();
		
		Map<String, List<MorphResult>> morphTestResultSet = new HashMap<String, List<MorphResult>>();
		
		for(Token token : resultTokenListIndexingMode) {
			MorphResult morphResult = new MorphResult();
			morphResult.setWord(token.toString());
			morphResult.setType(token.type());
			morphResult.setStartOffset(token.startOffset());
			morphResult.setEndOffset(token.endOffset());
			
			morphIndexingTestResult.add(morphResult);
		}
		
		for(Token token : resultTokenListQueryMode) {
			MorphResult morphResult = new MorphResult();
			morphResult.setWord(token.toString());
			morphResult.setType(token.type());
			morphResult.setStartOffset(token.startOffset());
			morphResult.setEndOffset(token.endOffset());
			
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
