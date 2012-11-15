package com.tistory.devyongsik.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollectionField;
import com.tistory.devyongsik.domain.CrescentCollections;
import com.tistory.devyongsik.domain.CrescentSortField;

public class CrescentCollectionHandler {
	private  Logger logger = LoggerFactory.getLogger(CrescentCollectionHandler.class);

	private final String DEFAULT_CONFIG_XML = "collection/collections.xml";
	private CrescentCollections crescentCollections = null;
	
	private static CrescentCollectionHandler instance = new CrescentCollectionHandler();

	public static CrescentCollectionHandler getInstance() {
		return instance;
	}

	private CrescentCollectionHandler() {
		loadFromXML();
		makeFieldsMap();
		makeAddtionalFields();
	}
	
	private void loadFromXML() {
		XStream stream = new XStream();
		stream.processAnnotations(CrescentCollections.class);
		stream.alias( "collections", CrescentCollections.class );
		stream.addImplicitCollection( CrescentCollections.class, "crescentCollections" );
		
		ResourceLoader resourceLoader = new ResourceLoader();
		InputStream inputStream = resourceLoader.openResource(DEFAULT_CONFIG_XML);
		
		crescentCollections = (CrescentCollections)stream.fromXML(inputStream);
		
		if(crescentCollections == null) {
			String errorMsg = "Crescent Collections is not loaded from xml : ["+DEFAULT_CONFIG_XML+"]";
			logger.error(errorMsg);
			
			throw new IllegalStateException(errorMsg);
		}
		
		try {
			inputStream.close();
		} catch (Exception e) {
			logger.error("stream close error ; ", e);
		}
	}
	
	public CrescentCollections getCrescentCollections() {
		return this.crescentCollections;
	}
	
	public void writeToXML() {
		
		ResourceLoader resourceLoader = new ResourceLoader();
		URL collectionXmlUrl = resourceLoader.getURL(DEFAULT_CONFIG_XML);
		
		XStream stream = new XStream();
		stream.processAnnotations(CrescentCollections.class);
		
		logger.debug("collectionXmlUrl : {}", collectionXmlUrl);
		
		try {
			
			File collectionsXmlFile = new File(collectionXmlUrl.toURI());
			
			logger.debug("collectionsXmlFile : {}", collectionsXmlFile);
			
			FileOutputStream fos = new FileOutputStream(collectionsXmlFile, false);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName("utf-8")));
			
			stream.toXML(this.crescentCollections, bw);
			
			bw.close();
			fos.close();
		
		} catch (URISyntaxException e) {
			e.printStackTrace();
			logger.error("error : ", e);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("error : ", e);
			
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("error : ", e);
			
		}	
	}

	private void makeFieldsMap() {
		List<CrescentCollection> crescentCollectionList = crescentCollections.getCrescentCollections();
		
		if(crescentCollectionList.size() == 0) {
			String errorMsg = "There are no Crescent collections!!";
			logger.error(errorMsg);
			
			throw new IllegalStateException(errorMsg);
		}
		
		for(CrescentCollection crescentCollection : crescentCollectionList) {
			Map<String, CrescentCollectionField> fieldMap = crescentCollection.getCrescentFieldByName();
			if(fieldMap == null) {
				fieldMap = new HashMap<String, CrescentCollectionField>();
			}
			
			List<CrescentCollectionField> fieldList = crescentCollection.getFields();
			for(CrescentCollectionField field : fieldList) {
				fieldMap.put(field.getName(), field);
			}
			
			crescentCollection.setCrescentFieldByName(fieldMap);
		}
	}
	
	private void makeAddtionalFields() {
		List<CrescentCollection> crescentCollectionList = crescentCollections.getCrescentCollections();
		
		for(CrescentCollection crescentCollection : crescentCollectionList) {
			List<CrescentSortField> crescentSortFieldList = crescentCollection.getSortFields();
			Map<String, CrescentCollectionField> fieldMap = crescentCollection.getCrescentFieldByName();
			
			for(CrescentSortField sortField : crescentSortFieldList) {
				CrescentCollectionField field = fieldMap.get(sortField.getSource());
				
				if(field == null) {
					throw new IllegalStateException("정렬 필드 설정에 필요한 원본(source) 필드가 없습니다.");
				}
				
				try {
					CrescentCollectionField newSortField = (CrescentCollectionField)field.clone();
					
					newSortField.setAnalyze(false);
					newSortField.setIndex(true);
					newSortField.setName(sortField.getDest());
					
					fieldMap.put(sortField.getDest(), newSortField);
					
				} catch (CloneNotSupportedException e1) {
					logger.error("error : ", e1);
				}
				
			}
		}
	}
	
	public void reloadCollectionsXML() {
		loadFromXML();
		makeFieldsMap();
		makeAddtionalFields();
	}
}
