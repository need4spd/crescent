package com.tistory.devyongsik.crescent.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tistory.devyongsik.crescent.admin.entity.IndexInfo;
import com.tistory.devyongsik.crescent.admin.service.IndexFileManageService;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.config.SpringApplicationContext;

@Controller
public class IndexFileManageController {
	
	@Autowired
	@Qualifier("indexFileManageService")
	private IndexFileManageService indexFileManageService;
	
	@RequestMapping("/indexFileManageMain")
	public ModelAndView indexFileManageMain(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView modelAndView = new ModelAndView();
		Map<String, Object> result = new HashMap<String, Object>();

		modelAndView.setViewName("/admin/indexFileManageMain");

        CrescentCollectionHandler collectionHandler
                = SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
        List<String> collectionNames = new ArrayList<String>();

        for (CrescentCollection crescentCollection : collectionHandler.getCrescentCollections().getCrescentCollections()) {
            collectionNames.add(crescentCollection.getName());
        }
        
        String selectCollectionName;
        if (request.getParameter("selectCollectionName") != null) {
        	selectCollectionName = (String)request.getParameter("selectCollectionName");
        } else {
        	selectCollectionName = collectionNames.get(0);
        }
        CrescentCollection selectCollection = collectionHandler
                                                    .getCrescentCollections()
                                                    .getCrescentCollection(selectCollectionName);
        
        String selectTopField = null;
        if (request.getParameter("selectTopField") != null) {
        	selectTopField = (String)request.getParameter("selectTopField");
        } else {
        	selectTopField = selectCollection.getFields().get(0).getName();
        }
		IndexInfo indexInfo = indexFileManageService.getIndexInfo(selectCollection, selectTopField);

		result.put("collectionNames", collectionNames);
		result.put("selectCollectionName", selectCollectionName);

		if (indexInfo != null) {
			result.put("hasIndexInfo", true);
			result.put("indexName", indexInfo.getIndexName());
			result.put("topRankingFields", indexInfo.getFieldNames());
			result.put("selectTopField", selectTopField);
			result.put("numOfField", indexInfo.getNumOfField());
			result.put("numOfDoc", indexInfo.getNumOfDoc());
			result.put("hasDel", indexInfo.isHasDel());
			result.put("indexVersion", indexInfo.getIndexVersion());
			result.put("termCountByFieldName", indexInfo.getTermCountByFieldNameMap());
			result.put("numOfTerm", indexInfo.getTotalTermCount());
			result.put("termStatsList", indexInfo.getCrescentTermStatsList());
		}

		modelAndView.addObject("RESULT", result);
		return modelAndView;
	}
	
	@RequestMapping("/indexFileManageDoc")
	public ModelAndView indexFileManageDoc(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		
		return modelAndView;
	}
}
