package com.tistory.devyongsik.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexFileManageController {
	
	@RequestMapping("/indexFileManageMain")
	public ModelAndView adminMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/indexFileManageMain");
		
		return modelAndView;
	}
	
	@RequestMapping("/indexFileManageDoc")
	public ModelAndView dictionaryAdminMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/indexFileManageDoc");
		
		return modelAndView;
	}
}
