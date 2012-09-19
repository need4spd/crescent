package com.tistory.devyongsik.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tistory.devyongsik.admin.MorphService;

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
}
