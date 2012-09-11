package com.tistory.devyongsik.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class AdminMainController {

	private Logger logger = LoggerFactory.getLogger(AdminMainController.class);
	
	@RequestMapping("/adminMain")
	public ModelAndView adminMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/main");
		
		return modelAndView;
	}
	
	@RequestMapping("/dicAdmin")
	public ModelAndView dictionaryAdminMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/dicAdminMain");
		
		return modelAndView;
	}
	
	@RequestMapping("/customNouns")
	public ModelAndView customNounManage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/dictionaryManage");
		
		return modelAndView;
	}
}
