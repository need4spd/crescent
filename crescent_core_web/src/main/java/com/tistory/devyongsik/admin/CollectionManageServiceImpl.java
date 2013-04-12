package com.tistory.devyongsik.admin;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.controller.CollectionManageMainCotroller;
import com.tistory.devyongsik.domain.CrescentAnalyzerHolder;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollectionField;
import com.tistory.devyongsik.domain.CrescentCollections;
import com.tistory.devyongsik.domain.CrescentDefaultSearchField;
import com.tistory.devyongsik.domain.CrescentSortField;

@Service("collectionManageService")
public class CollectionManageServiceImpl implements CollectionManageService {

	private Logger logger = LoggerFactory.getLogger(CollectionManageMainCotroller.class);

	@Autowired
	@Qualifier("crescentCollectionHandler")
	private CrescentCollectionHandler collectionHandler;
	
	@Override
	public CrescentCollection updateCollectionInfo(HttpServletRequest request) {
		
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		
		String selectedCollectionName = request.getParameter("collectionName");

		logger.debug("selectedCollectionName : " + selectedCollectionName);

		String indexingModeAnalyzer = request.getParameter("indexingModeAnalyzer");
		String searchModeAnalyzer = request.getParameter("searchModeAnalyzer");
		
		String indexingModeAnalyzerType = request.getParameter("indexingModeAnalyzerType");
		String searchModeAnalyzerType = request.getParameter("searchModeAnalyzerType");
		
		String indexingModeAnalyzerConstArgs = request.getParameter("indexingModeAnalyzerConstArgs");
		String searchModeAnalyzerConstArgs = request.getParameter("searchModeAnalyzerConstArgs");
		
		CrescentCollection selectedCollection = crescentCollections.getCrescentCollection(selectedCollectionName);
		
		List<CrescentAnalyzerHolder> analyzerHolderList = new ArrayList<CrescentAnalyzerHolder>();
		CrescentAnalyzerHolder indexingModeAnalyzerHolder = new CrescentAnalyzerHolder();
		indexingModeAnalyzerHolder.setClassName(indexingModeAnalyzer);
		indexingModeAnalyzerHolder.setConstructorArgs(indexingModeAnalyzerConstArgs);
		indexingModeAnalyzerHolder.setType(indexingModeAnalyzerType);
		analyzerHolderList.add(indexingModeAnalyzerHolder);
		
		CrescentAnalyzerHolder searchModeAnalyzerHolder = new CrescentAnalyzerHolder();
		searchModeAnalyzerHolder.setClassName(searchModeAnalyzer);
		searchModeAnalyzerHolder.setConstructorArgs(searchModeAnalyzerConstArgs);
		searchModeAnalyzerHolder.setType(searchModeAnalyzerType);
		analyzerHolderList.add(searchModeAnalyzerHolder);
		
		selectedCollection.setAnalyzers(analyzerHolderList);
		
		selectedCollection.setSearcherReloadScheduleMin(StringUtils.defaultIfEmpty(request.getParameter("searcherReloadScheduleMin"), "10"));

		if(logger.isDebugEnabled()) {
			logger.debug("analyzer : {} ", request.getParameter("analyzer"));
			logger.debug("collection Name : {} ", request.getParameter("collectionName"));
			logger.debug("indexing Directory : {} ", request.getParameter("indexingDirectory"));
			logger.debug("searcher reload schedule min : {} ", request.getParameter("searcherReloadScheduleMin"));
			logger.debug("indexingModeAnalyzer : {} ", request.getParameter("indexingModeAnalyzer"));
			logger.debug("searchModeAnalyzer : {} ", request.getParameter("searchModeAnalyzer"));
			logger.debug("indexingModeAnalyzerType : {} ", request.getParameter("indexingModeAnalyzerType"));
			logger.debug("searchModeAnalyzerType : {} ", request.getParameter("searchModeAnalyzerType"));
			logger.debug("indexingModeAnalyzerConstArgs : {} ", request.getParameter("indexingModeAnalyzerConstArgs"));
			logger.debug("searchModeAnalyzerConstArgs : {} ", request.getParameter("searchModeAnalyzerConstArgs"));
		}

		List<CrescentCollectionField> crescentCollectionFieldList = selectedCollection.getFields();

		//추가되는 필드명을 모은다.
		@SuppressWarnings("unchecked")
		Enumeration<String> enumeration = request.getParameterNames();
		List<String> addFieldNameList = new ArrayList<String>();
		while(enumeration.hasMoreElements()) {
			String paramValue = enumeration.nextElement();
			if(paramValue.endsWith("fieldName")) {
				addFieldNameList.add(paramValue.substring(0, paramValue.lastIndexOf("-")));
			}
		}

		logger.debug("add field name list : {}", addFieldNameList);

		for(String fieldName :addFieldNameList) {
			CrescentCollectionField crescentField = new CrescentCollectionField();
			crescentField.setName(fieldName);
			
			if(!crescentCollectionFieldList.contains(crescentField)) {
				crescentCollectionFieldList.add(crescentField);
			}
		}
				
		for(CrescentCollectionField crescentField : crescentCollectionFieldList) {

			crescentField.setAnalyze("on".equals(request.getParameter(crescentField.getName()+"-analyze")) ? true : false);
			crescentField.setIndex("on".equals(request.getParameter(crescentField.getName()+"-index")) ? true : false);
			crescentField.setMust("on".equals(request.getParameter(crescentField.getName()+"-must")) ? true : false);
			crescentField.setStore("on".equals(request.getParameter(crescentField.getName()+"-store")) ? true : false);
			crescentField.setTermoffset("on".equals(request.getParameter(crescentField.getName()+"-termoffset")) ? true : false);
			crescentField.setTermposition("on".equals(request.getParameter(crescentField.getName()+"-termposition")) ? true : false);
			crescentField.setTermvector("on".equals(request.getParameter(crescentField.getName()+"-termvector")) ? true : false);
			crescentField.setRemoveHtmlTag("on".equals(request.getParameter(crescentField.getName()+"-removeHtmlTag")) ? true : false);

			crescentField.setBoost(Float.parseFloat(StringUtils.defaultString(request.getParameter(crescentField.getName()+"-boost"), "0")));
			crescentField.setType(StringUtils.defaultString(request.getParameter(crescentField.getName()+"-type"), "STRING"));


			//sort field 처리
			if("on".equals(request.getParameter(crescentField.getName()+"-sortField"))) {
				CrescentSortField sortField = new CrescentSortField();
				sortField.setSource(crescentField.getName());
				sortField.setDest(crescentField.getName()+"_sort");

				if(selectedCollection.getSortFields().contains(sortField)) {
					//Nothing
				} else {
					selectedCollection.getSortFields().add(sortField);
				}
			}

			//default search field 처리
			if("on".equals(request.getParameter(crescentField.getName()+"-defaultSearchField"))) {
				CrescentDefaultSearchField defaultSearchField = new CrescentDefaultSearchField();
				defaultSearchField.setName(crescentField.getName());

				if(selectedCollection.getDefaultSearchFields().contains(defaultSearchField)) {
					//Nothing
				} else {
					selectedCollection.getDefaultSearchFields().add(defaultSearchField);
				}
			}

			if(logger.isDebugEnabled()) {
				logger.debug("crescentField Name {} = {}", crescentField.getName(), "sortField : " + request.getParameter(crescentField.getName()+"-sortField"));
				logger.debug("crescentField Name {} = {}", crescentField.getName(), "defaultSearchField : "+ request.getParameter(crescentField.getName()+"-defaultSearchField"));
			}

			if(logger.isDebugEnabled()) {
				logger.debug("crescentField Name {} = {}", crescentField.getName(), "analyze : " + request.getParameter(crescentField.getName()+"-analyze"));
				logger.debug("crescentField Name {} = {}", crescentField.getName(), "index : " + request.getParameter(crescentField.getName()+"-index"));
				logger.debug("crescentField Name {} = {}", crescentField.getName(), "must : " + request.getParameter(crescentField.getName()+"-must"));
				logger.debug("crescentField Name {} = {}", crescentField.getName(), "store : " + request.getParameter(crescentField.getName()+"-store"));
				logger.debug("crescentField Name {} = {}", crescentField.getName(), "termoffset : " + request.getParameter(crescentField.getName()+"-termoffset"));
				logger.debug("crescentField Name {} = {}", crescentField.getName(), "termposition : " + request.getParameter(crescentField.getName()+"-termposition"));
				logger.debug("crescentField Name {} = {}", crescentField.getName(), "termvector : " + request.getParameter(crescentField.getName()+"-termvector"));
				logger.debug("crescentField Name {} = {}", crescentField.getName(), "boost : " + request.getParameter(crescentField.getName()+"-boost"));
				logger.debug("crescentField Name {} = {}", crescentField.getName(), "type : " + request.getParameter(crescentField.getName()+"-type"));
			}
		}
			
		collectionHandler.writeToXML();
		collectionHandler.reloadCollectionsXML();
		
		crescentCollections = collectionHandler.getCrescentCollections();
		selectedCollection = crescentCollections.getCrescentCollection(selectedCollectionName);
		
		return selectedCollection;
	}
	
	
	@Override
	public CrescentCollection addCollectionInfo(HttpServletRequest request) {
		
//		CrescentCollectionHandler collectionHandler 
//			= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
	
		
		String selectedCollectionName = request.getParameter("collectionName");

		logger.debug("selectedCollectionName : " + selectedCollectionName);
		
		CrescentCollection newCollection = new CrescentCollection();
		newCollection.setName(selectedCollectionName);
		newCollection.setIndexingDirectory(request.getParameter("indexingDirectory"));
		
		newCollection.setSearcherReloadScheduleMin(StringUtils.defaultIfEmpty(request.getParameter("searcherReloadScheduleMin"), "10"));

		String indexingModeAnalyzer = request.getParameter("indexingModeAnalyzer");
		String searchModeAnalyzer = request.getParameter("searchModeAnalyzer");
		
		String indexingModeAnalyzerType = request.getParameter("indexingModeAnalyzerType");
		String searchModelAnalyzerType = request.getParameter("searchModeAnalyzerType");
		
		String indexingModeAnalyzerConstArgs = request.getParameter("indexingModeAnalyzerConstArgs");
		String searchModeAnalyzerConstArgs = request.getParameter("searchModeAnalyzerConstArgs");
		
		List<CrescentAnalyzerHolder> analyzerHolderList = new ArrayList<CrescentAnalyzerHolder>();
		CrescentAnalyzerHolder indexingModeAnalyzerHolder = new CrescentAnalyzerHolder();
		indexingModeAnalyzerHolder.setClassName(indexingModeAnalyzer);
		indexingModeAnalyzerHolder.setConstructorArgs(indexingModeAnalyzerConstArgs);
		indexingModeAnalyzerHolder.setType(indexingModeAnalyzerType);
		analyzerHolderList.add(indexingModeAnalyzerHolder);
		
		CrescentAnalyzerHolder searchModeAnalyzerHolder = new CrescentAnalyzerHolder();
		searchModeAnalyzerHolder.setClassName(searchModeAnalyzer);
		searchModeAnalyzerHolder.setConstructorArgs(searchModeAnalyzerConstArgs);
		searchModeAnalyzerHolder.setType(searchModelAnalyzerType);
		analyzerHolderList.add(searchModeAnalyzerHolder);
		
		if(logger.isDebugEnabled()) {
			logger.debug("analyzer : {} ", request.getParameter("analyzer"));
			logger.debug("collection Name : {} ", request.getParameter("collectionName"));
			logger.debug("indexing Directory : {} ", request.getParameter("indexingDirectory"));
			logger.debug("searcher reload schedule min : {} ", request.getParameter("searcherReloadScheduleMin"));
			logger.debug("indexingModeAnalyzer : {} ", request.getParameter("indexingModeAnalyzer"));
			logger.debug("searchModeAnalyzer : {} ", request.getParameter("searchModeAnalyzer"));
			logger.debug("indexingModeAnalyzerType : {} ", request.getParameter("indexingModeAnalyzerType"));
			logger.debug("searchModelAnalyzerType : {} ", request.getParameter("searchModelAnalyzerType"));
			logger.debug("indexingModeAnalyzerConstArgs : {} ", request.getParameter("indexingModeAnalyzerConstArgs"));
			logger.debug("searchModeAnalyzerConstArgs : {} ", request.getParameter("searchModeAnalyzerConstArgs"));
		}

		//필드들을 걸러낸다.
		@SuppressWarnings("unchecked")
		Enumeration<String> enumeration = (Enumeration<String>)request.getParameterNames();
		List<String> fieldNameList = new ArrayList<String>();
		while(enumeration.hasMoreElements()) {
			String paramName = enumeration.nextElement();

			if(paramName.endsWith("-fieldName")) { //필수값
				String fieldName = paramName.split("-")[0];
				fieldNameList.add(fieldName);
			}
		}
		
		List<CrescentCollectionField> newCollectionFieldList = new ArrayList<CrescentCollectionField>();
		List<CrescentSortField> sortFieldList = new ArrayList<CrescentSortField>();
		List<CrescentDefaultSearchField> defaultSearchFieldList = new ArrayList<CrescentDefaultSearchField>();
		
		for(String fieldName : fieldNameList) {
			CrescentCollectionField newCollectionField = new CrescentCollectionField();
			
			newCollectionField.setName(fieldName);
			newCollectionField.setAnalyze("on".equals(request.getParameter(fieldName+"-analyze")) ? true : false);
			newCollectionField.setIndex("on".equals(request.getParameter(fieldName+"-index")) ? true : false);
			newCollectionField.setMust("on".equals(request.getParameter(fieldName+"-must")) ? true : false);
			newCollectionField.setStore("on".equals(request.getParameter(fieldName+"-store")) ? true : false);
			newCollectionField.setTermoffset("on".equals(request.getParameter(fieldName+"-termoffset")) ? true : false);
			newCollectionField.setTermposition("on".equals(request.getParameter(fieldName+"-termposition")) ? true : false);
			newCollectionField.setTermvector("on".equals(request.getParameter(fieldName+"-termvector")) ? true : false);

			//System.out.println("DDDDDDDDDDDDDD : " + request.getParameter(fieldName+"-boost"));
			
			newCollectionField.setBoost(Float.parseFloat(StringUtils.defaultIfEmpty(request.getParameter(fieldName+"-boost"), "0")));
			newCollectionField.setType(StringUtils.defaultString(request.getParameter(fieldName+"-type"), "STRING"));

			newCollectionFieldList.add(newCollectionField);
			
			//sort field 처리			
			if("on".equals(request.getParameter(fieldName+"-sortField"))) {
				CrescentSortField sortField = new CrescentSortField();
				sortField.setSource(fieldName);
				sortField.setDest(fieldName+"_sort");

				sortFieldList.add(sortField);
			}
			
			//default search field 처리
			if("on".equals(request.getParameter(fieldName+"-defaultSearchField"))) {
				CrescentDefaultSearchField defaultSearchField = new CrescentDefaultSearchField();
				defaultSearchField.setName(fieldName);
				defaultSearchFieldList.add(defaultSearchField);
			}
			

			if(logger.isDebugEnabled()) {
				logger.debug("crescentField Name {} = {}", fieldName, "sortField : " + request.getParameter(fieldName+"-sortField"));
				logger.debug("crescentField Name {} = {}", fieldName, "defaultSearchField : "+ request.getParameter(fieldName+"-defaultSearchField"));
			}

			if(logger.isDebugEnabled()) {
				logger.debug("crescentField Name {} = {}", fieldName, "analyze : " + request.getParameter(fieldName+"-analyze"));
				logger.debug("crescentField Name {} = {}", fieldName, "index : " + request.getParameter(fieldName+"-index"));
				logger.debug("crescentField Name {} = {}", fieldName, "must : " + request.getParameter(fieldName+"-must"));
				logger.debug("crescentField Name {} = {}", fieldName, "store : " + request.getParameter(fieldName+"-store"));
				logger.debug("crescentField Name {} = {}", fieldName, "termoffset : " + request.getParameter(fieldName+"-termoffset"));
				logger.debug("crescentField Name {} = {}", fieldName, "termposition : " + request.getParameter(fieldName+"-termposition"));
				logger.debug("crescentField Name {} = {}", fieldName, "termvector : " + request.getParameter(fieldName+"-termvector"));
				logger.debug("crescentField Name {} = {}", fieldName, "boost : " + request.getParameter(fieldName+"-boost"));
				logger.debug("crescentField Name {} = {}", fieldName, "type : " + request.getParameter(fieldName+"-type"));
			}
		}
		
		newCollection.setSortFields(sortFieldList);
		newCollection.setDefaultSearchFields(defaultSearchFieldList);
		newCollection.setFields(newCollectionFieldList);
		newCollection.setAnalyzers(analyzerHolderList);
		
		collectionHandler.getCrescentCollections().getCrescentCollections().add(newCollection);
		collectionHandler.writeToXML();
		collectionHandler.reloadCollectionsXML();
		
		return newCollection;
	}
	
	@Override
	public void deleteCollectionInfo(String collectionName) {

		CrescentCollections collections = collectionHandler.getCrescentCollections();
		List<CrescentCollection> collectionList = collections.getCrescentCollections();
		
		int targetIndex = -1;
		
		for(int index = 0; index < collectionList.size(); index++) {
			if(collectionName.equals(collectionList.get(index).getName())) {
				targetIndex = index;
				break;
			}
		}
		
		if(targetIndex > 0) {
			collectionList.remove(targetIndex);
		} else {
			logger.error("삭제하려는 컬렉션이 존재하지 않습니다. [{}]", collectionName);
			throw new IllegalArgumentException("삭제하려는 컬렉션이 존재하지 않습니다.");
		}
		
		collectionHandler.writeToXML();
		collectionHandler.reloadCollectionsXML();
	}
}
