package com.tistory.devyongsik.crescent.admin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.tistory.devyongsik.crescent.admin.service.CollectionManageService;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollections;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;

@Controller
public class CollectionManageMainCotroller {

	@Autowired
	@Qualifier("collectionManageService")
	private CollectionManageService collectionManageService;
	
	@Autowired
	@Qualifier("crescentCollectionHandler")
	private CrescentCollectionHandler collectionHandler;
	
	@RequestMapping("/collectionManageMain")
	public ModelAndView collectionManageMain(@RequestParam(value="collectionName", required=false) String selectedCollectionName) throws Exception {
		
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		
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

		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		
		CrescentCollection selectedCollection = collectionManageService.updateCollectionInfo(request);
		
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("crescentCollectionList", crescentCollections.getCrescentCollections());
		modelAndView.addObject("selectedCollection", crescentCollections.getCrescentCollection(selectedCollection.getName()));
		modelAndView.addObject("selectedCollectionName", selectedCollection.getName());
		
		modelAndView.setViewName("/admin/collectionManageMain");
		
		
		return modelAndView;
		
	}
	
	@RequestMapping("/addNewCollection")
	public ModelAndView addNewCollection() throws Exception {
		
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/addNewCollectionForm");
		
		
		return modelAndView;
	}
	
	@RequestMapping("/collectionAdd")
	public ModelAndView collectionAdd(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CrescentCollection selectedCollection = collectionManageService.addCollectionInfo(request);
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
	
		ModelAndView modelAndView = new ModelAndView();
		//modelAndView.addObject("crescentCollections", crescentCollections);
		modelAndView.addObject("crescentCollectionList", crescentCollections.getCrescentCollections());
		modelAndView.addObject("selectedCollection", crescentCollections.getCrescentCollection(selectedCollection.getName()));
		modelAndView.addObject("selectedCollectionName", selectedCollection.getName());
		
		
		modelAndView.setViewName("/admin/collectionManageMain");
		
		
		return modelAndView;
	}
	
	@RequestMapping("/deleteCollection")
	public ModelAndView deleteCollection(@RequestParam(value="collectionName") String collectionName ) throws Exception {
		
		collectionManageService.deleteCollectionInfo(collectionName);
		
		ModelAndView modelAndView = new ModelAndView();
		
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		String selectedCollectionName = crescentCollections.getCrescentCollections().get(0).getName();
		
		modelAndView.addObject("selectedCollectionName", selectedCollectionName);
		modelAndView.addObject("crescentCollectionList", crescentCollections.getCrescentCollections());
		modelAndView.addObject("selectedCollection", crescentCollections.getCrescentCollection(selectedCollectionName));
		
		modelAndView.setViewName("/admin/collectionManageMain");
		
		
		return modelAndView;
	}
}
