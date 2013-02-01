package com.tistory.devyongsik.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tistory.devyongsik.admin.IndexFileManageService;

@Controller
public class IndexFileManageController {
	
	@Autowired
	private IndexFileManageService indexFileManageService;
	
	@RequestMapping("/indexFileManageMain")
	public ModelAndView adminMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/indexFileManageMain");
		
		String collection = request.getParameter("collection");
		String topRankingField = request.getParameter("topRankingField");
		
		if (indexFileManageService.reload(collection, topRankingField) == false)
			return modelAndView;
		
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> temp = indexFileManageService.getResult();
		
		result.put("collectionName", temp.get("collectionName"));
		result.put("indexName", temp.get("indexName"));
		result.put("numOfField", temp.get("numOfField"));
		result.put("numOfTerm", temp.get("numOfTerm"));
		result.put("numOfDoc", temp.get("numOfDoc"));
		result.put("hasDel", temp.get("hasDel"));
		result.put("isOptimize", temp.get("isOptimize"));
		result.put("indexVersion", temp.get("indexVersion"));
		result.put("lastModify", temp.get("lastModify"));
		result.put("termCount", temp.get("termCount"));
		result.put("topRanking", temp.get("topRanking"));
		
		modelAndView.addObject("RESULT", result);
		return modelAndView;
	}
	
	@RequestMapping("/indexFileManageDoc")
	public ModelAndView dictionaryAdminMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/indexFileManageDoc");
		
		String collectionName = request.getParameter("collection");
		String docNum = request.getParameter("docNum");
		if (docNum == null)
			return modelAndView;
		
		if (indexFileManageService.reload(collectionName, Integer.parseInt(docNum)) == false)
			return modelAndView;
		
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> temp = indexFileManageService.getResult();
		
		result.put("collectionName", collectionName);
		result.put("docNum", docNum);
		
		result.put("fieldName", temp.get("fieldName"));
		result.put("flag", temp.get("flag"));
		result.put("norm", temp.get("norm"));
		result.put("value", temp.get("value"));
		
		modelAndView.addObject("RESULT", result);
		
		return modelAndView;
	}
}
