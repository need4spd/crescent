package com.tistory.devyongsik.crescent.admin.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tistory.devyongsik.crescent.admin.service.IndexFileManageService;

@Controller
public class IndexFileManageController {
	
	@Autowired
	@Qualifier("indexFileManageService")
	private IndexFileManageService indexFileManageService;
	
	@RequestMapping("/indexFileManageMain")
	public ModelAndView adminMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		Map<String, Object> result = new HashMap<String, Object>();
		boolean isReload= false;
		
		modelAndView.setViewName("/admin/indexFileManageMain");
		String collection = request.getParameter("selectCollection");
		String topRankingField = request.getParameter("topRankingField");
		
		isReload = indexFileManageService.reload(collection, topRankingField);
		Map<String, Object>temp = indexFileManageService.getResult();
		result.put("collectionNames", temp.get("collectionNames"));
		if (isReload == false) {
			modelAndView.addObject("RESULT", result);
			return modelAndView;
		}
				
		result.put("selectCollection", temp.get("selectCollection"));
		result.put("collectionNames", temp.get("collectionNames"));
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
		result.put("topRankingCount", temp.get("topRankingCount"));
		result.put("topRankingFields", temp.get("fieldName"));
		result.put("topRankingField", temp.get("topRankingField"));
		
		modelAndView.addObject("RESULT", result);
		return modelAndView;
	}
	
	@RequestMapping("/indexFileManageDoc")
	public ModelAndView dictionaryAdminMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/indexFileManageDoc");
		
		String collectionName = request.getParameter("selectCollection");
		String docNum = request.getParameter("docNum");
		if (docNum == null)
			docNum = "0";
		
		if (indexFileManageService.reload(collectionName, Integer.parseInt(docNum)) == false)
			return modelAndView;
		
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> temp = indexFileManageService.getResult();
		
		result.put("collectionNames", temp.get("collectionNames"));
		result.put("selectCollection", temp.get("selectCollection"));
		result.put("docNum", docNum);
		
		result.put("fieldName", temp.get("fieldName"));
		result.put("flag", temp.get("flag"));
		result.put("norm", temp.get("norm"));
		result.put("value", temp.get("value"));
		
		modelAndView.addObject("RESULT", result);
		
		return modelAndView;
	}
}
