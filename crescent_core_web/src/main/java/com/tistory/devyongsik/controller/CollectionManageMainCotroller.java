package com.tistory.devyongsik.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tistory.devyongsik.admin.CollectionManageService;
import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollections;

@Controller
public class CollectionManageMainCotroller {

	@Autowired
	private CollectionManageService collectionManageService;
	
	public void setCollectionManageService(CollectionManageService collectionManageService) {
		this.collectionManageService = collectionManageService;
	}
	
	@RequestMapping("/collectionManageMain")
	public ModelAndView collectionManageMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CrescentCollectionHandler collectionHandler = CrescentCollectionHandler.getInstance();
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		
		String selectedCollectionName = request.getParameter("collectionName");
		if(selectedCollectionName == null) {
			selectedCollectionName = crescentCollections.getCrescentCollections().get(0).getName();
		}
		
		ModelAndView modelAndView = new ModelAndView();
		//modelAndView.addObject("crescentCollections", crescentCollections);
		modelAndView.addObject("selectedCollectionName", selectedCollectionName);
		
		List<CrescentCollection> crescentCollectionList = crescentCollections.getCrescentCollections();
		
		modelAndView.addObject("crescentCollectionList", crescentCollectionList);
		modelAndView.addObject("selectedCollection", crescentCollections.getCrescentCollection(selectedCollectionName));
		modelAndView.setViewName("/admin/collectionManageMain");
		
		
		return modelAndView;
	}
	
	@RequestMapping("/collectionUpdate")
	public ModelAndView collectionUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CrescentCollectionHandler collectionHandler = CrescentCollectionHandler.getInstance();
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		
		CrescentCollection selectedCollection = collectionManageService.updateCollectionInfo(request);
		
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("crescentCollections", crescentCollections);
		modelAndView.addObject("selectedCollectionName", selectedCollection.getName());
		
		modelAndView.setViewName("/admin/collectionManageMain");
		
		
		return modelAndView;
		
	}
	
	@RequestMapping("/addNewCollection")
	public ModelAndView addNewCollection(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/addNewCollectionForm");
		
		
		return modelAndView;
	}
	
	@RequestMapping("/collectionAdd")
	public ModelAndView collectionAdd(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CrescentCollection selectedCollection = collectionManageService.addCollectionInfo(request);
		
		ModelAndView modelAndView = new ModelAndView();
		//modelAndView.addObject("crescentCollections", crescentCollections);
		modelAndView.addObject("selectedCollectionName", selectedCollection.getName());
		
		modelAndView.setViewName("/admin/collectionManageMain");
		
		
		return modelAndView;
	}
	
	@RequestMapping("/deleteCollection")
	public ModelAndView deleteCollection(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		collectionManageService.deleteCollectionInfo(request.getParameter("collectionName"));
		
		ModelAndView modelAndView = new ModelAndView();
		//modelAndView.addObject("crescentCollections", crescentCollections);
		CrescentCollectionHandler collectionHandler = CrescentCollectionHandler.getInstance();
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		String selectedCollectionName = crescentCollections.getCrescentCollections().get(0).getName();
		
		modelAndView.addObject("selectedCollectionName", selectedCollectionName);
		
		modelAndView.setViewName("/admin/collectionManageMain");
		
		
		return modelAndView;
	}
}
