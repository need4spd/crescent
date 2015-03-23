package com.tistory.devyongsik.crescent.admin.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.tistory.devyongsik.analyzer.dictionary.DictionaryType;
import com.tistory.devyongsik.crescent.admin.service.DictionaryService;


@Controller
public class AdminMainController {

	private Logger logger = LoggerFactory.getLogger(AdminMainController.class);
	
	@Autowired
	@Qualifier("dictionaryService")
	private DictionaryService dictionaryService = null;
	
	@RequestMapping("/adminMain")
	public ModelAndView adminMain() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/main");
		
		return modelAndView;
	}
	
	@RequestMapping("/dictionaryManage")
	public ModelAndView dictionaryManage(@RequestParam(value="dicType") String dicType
										, @RequestParam(value="pagingAction", required=false) String pagingAction
										, @RequestParam(value="startOffset", defaultValue="0") int startOffset) throws Exception {
		List<String> dictionary = loadDictionary(dicType);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/dictionaryManage");
		modelAndView.addObject("dicType", dicType);
		
		if(dictionary != null && dictionary.size() < 20) {
			modelAndView.addObject("dictionary", dictionary);
			modelAndView.addObject("startOffset", "0");
			modelAndView.addObject("dictionarySize", dictionary.size());
			
			return modelAndView;
		} 
		
		int endOffset = 0;
		
		if("prev".equals(pagingAction)) {
			endOffset = startOffset - 20;
			startOffset = endOffset - 20;
			
			if (endOffset <= 0) {
				endOffset = 0;
				startOffset = 0;
			}
			
		} else {
			endOffset = startOffset + 20;
		}
		
		logger.debug("dicType : {}", dicType);
		logger.debug("startOffset : {} , endOffset : {}, dictionary size : {}", new Object[]{startOffset, endOffset, dictionary.size()});
			
		if(dictionary != null && dictionary.size() > endOffset) {
			modelAndView.addObject("dictionary", dictionary.subList(startOffset, endOffset));
		} else {
			modelAndView.addObject("dictionary", dictionary.subList(startOffset, dictionary.size()));
		}
		
		modelAndView.addObject("startOffset", String.valueOf(endOffset));
		modelAndView.addObject("dictionarySize", dictionary.size());
		
		
		return modelAndView;
	}
	
	@RequestMapping("/dictionaryManageAdd")
	public ModelAndView dictionaryManageAdd(@RequestParam(value="dicType") String dicType
											, @RequestParam(value="wordToAdd") String wordToAdd) throws Exception {
		
		int startOffset = 0;
		int endOffset = 20;
		
		logger.debug("dicType : {}", dicType);
		logger.debug("startOffset : {} , endOffset : {}", new Object[]{startOffset, endOffset});
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/dictionaryManage");
		
		modelAndView.addObject("dicType", dicType);
		
		//Add Word to Dictionary
		logger.debug("word to add : {}", wordToAdd);
		
		dictionaryService.addWordToDictionary(getDictionaryType(dicType), wordToAdd);
		dictionaryService.writeToDictionaryFile(getDictionaryType(dicType));
		dictionaryService.rebuildDictionary(getDictionaryType(dicType));

		//dictionaryService.
		List<String> dictionary = loadDictionary(dicType);
		
		if(dictionary != null && dictionary.size() > endOffset) {
			modelAndView.addObject("dictionary", dictionary.subList(startOffset, endOffset));
		} else {
			modelAndView.addObject("dictionary", dictionary.subList(startOffset, dictionary.size()));
		}

		modelAndView.addObject("startOffset", String.valueOf(endOffset));
		modelAndView.addObject("dictionarySize", dictionary.size());
		
		return modelAndView;
	}
	
	@RequestMapping("/dictionaryManageRemove")
	public ModelAndView dictionaryManageRemove(@RequestParam(value="dicType") String dicType
											, @RequestParam(value="wordsToRemove",defaultValue="") String wordsToRemove) throws Exception {
		int startOffset = 0;
		int endOffset = 20;
		
		logger.debug("dicType : {}", dicType);
		logger.debug("wordsToRemove : {}", wordsToRemove);
		logger.debug("startOffset : {} , endOffset : {}", new Object[]{startOffset, endOffset});
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/dictionaryManage");
		
		modelAndView.addObject("dicType", dicType);
		
		dictionaryService.removeWordFromDictionary(getDictionaryType(dicType), wordsToRemove);	
		dictionaryService.writeToDictionaryFile(getDictionaryType(dicType));
		dictionaryService.rebuildDictionary(getDictionaryType(dicType));
		
		List<String> dictionary = loadDictionary(dicType);
		
		if(dictionary != null && dictionary.size() > endOffset) {
			modelAndView.addObject("dictionary", dictionary.subList(startOffset, endOffset));
		} else {
			modelAndView.addObject("dictionary", dictionary.subList(startOffset, dictionary.size()));
		}

		modelAndView.addObject("startOffset", String.valueOf(endOffset));
		modelAndView.addObject("dictionarySize", dictionary.size());
		
		return modelAndView;
	}
	
	@RequestMapping("/dictionaryManageFind")
	public ModelAndView dictionaryManageFind(@RequestParam(value="dicType") String dicType
										, @RequestParam(value="wordToFind", defaultValue="") String wordToFind) throws Exception {
		int startOffset = 0;
		int endOffset = 20;
		
		logger.debug("dicType : {}", dicType);
		logger.debug("wordToFind : {}", wordToFind);
		logger.debug("startOffset : {} , endOffset : {}", new Object[]{startOffset, endOffset});
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/dictionaryManage");
		
		modelAndView.addObject("dicType", dicType);
		
		List<String> dictionary = dictionaryService.findWordFromDictionary(getDictionaryType(dicType), wordToFind);
		
		if(dictionary != null && dictionary.size() > endOffset) {
			modelAndView.addObject("dictionary", dictionary.subList(startOffset, endOffset));
		} else {
			modelAndView.addObject("dictionary", dictionary.subList(startOffset, dictionary.size()));
		}

		modelAndView.addObject("startOffset", String.valueOf(endOffset));
		modelAndView.addObject("dictionarySize", dictionary.size());
		
		return modelAndView;
	}
	
	private List<String> loadDictionary(String dicType) {
		
		List<String> dictionary = null;
		
		if("noun".equals(dicType)) {
			dictionary = dictionaryService.getDictionary(DictionaryType.CUSTOM);
		} else if ("stop".equals(dicType)) {
			dictionary = dictionaryService.getDictionary(DictionaryType.STOP);
		} else if ("syn".equals(dicType)) {
			dictionary = dictionaryService.getDictionary(DictionaryType.SYNONYM);
		} else if ("compound".equals(dicType)) {
			dictionary = dictionaryService.getDictionary(DictionaryType.COMPOUND);
		}
		
		return dictionary;
	}
	
	private DictionaryType getDictionaryType(String dicType) {
		if("noun".equals(dicType)) {
			
			return DictionaryType.CUSTOM;
			
		} else if ("stop".equals(dicType)) {
			
			return DictionaryType.STOP;
			
		} else if ("syn".equals(dicType)) {
			
			return DictionaryType.SYNONYM;
			
		} else if ("compound".equals(dicType)) {
			
			return DictionaryType.COMPOUND;
		} else {
			
			return null;
		
		}
	}
}
