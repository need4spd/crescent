package com.tistory.devyongsik.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.domain.Collection;
import com.tistory.devyongsik.domain.CollectionField;

/**
 * author : need4spd, need4spd@naver.com, 2012. 2. 26.
 */

public class CollectionConfig {
	private  Logger logger = LoggerFactory.getLogger(CollectionConfig.class);

	private final String DEFAULT_CONFIG_XML = "collection/collections.xml";
	private Map<String, Collection> collections = new HashMap<String, Collection>();

	private static CollectionConfig instance = new CollectionConfig();

	public static CollectionConfig getInstance() {
		return instance;
	}

	public Collection getCollection(String collectionName) {
		return collections.get(collectionName);
	}
	
	public Map<String, Collection> getCollections() {
		return collections;
	}
	
	private CollectionConfig() {
		logger.info("start init Collection Configs.");
		
		initProperty();
	}

	private void initProperty() {
		ResourceLoader resourceLoader = new ResourceLoader(DEFAULT_CONFIG_XML);
		Document document = resourceLoader.getDocument();

		//get collection list
		@SuppressWarnings("unchecked")
		List<Element> collectionList = document.selectNodes("//collection");

		logger.debug("collectionList : {}", collectionList.toString());
		
		for(Element node : collectionList) {
			Collection collection = new Collection();
						
			logger.debug("indexingDirectory : {}", node.elementText("indexingDirectory"));
			logger.debug("name : {}", node.attributeValue("name"));
			
			collection.setIndexingDir(node.elementText("indexingDirectory"));
			collection.setCollectionName(node.attributeValue("name"));
			
			@SuppressWarnings("unchecked")
			List<Element> fieldsList = node.selectNodes("//collection[@name='"+collection.getCollectionName()+"']"+"/fields/field");
			
			if(logger.isDebugEnabled()) {
				for(Element e : fieldsList) {
					logger.debug("field name : {}", e.attributeValue("name"));
					logger.debug("field store : {}", e.attributeValue("store"));
					logger.debug("field index : {}", e.attributeValue("index"));
					logger.debug("field type : {}", e.attributeValue("type"));
					logger.debug("field analyze : {}", e.attributeValue("analyze"));
					logger.debug("field must : {}", e.attributeValue("must"));
					logger.debug("field boost : {}", e.attributeValue("boost"));
					logger.debug("field termposition : {}", e.attributeValue("termposition"));
					logger.debug("field termoffset : {}", e.attributeValue("termoffset"));
				}
			}
			
			for(Element e : fieldsList) {
				CollectionField collectionField = new CollectionField();
				collectionField.setName(e.attributeValue("name"));
				collectionField.setisStore(Boolean.valueOf(e.attributeValue("store")));
				collectionField.setisIndexing(Boolean.valueOf(e.attributeValue("index")));
				collectionField.setType(e.attributeValue("type"));
				collectionField.setisAnalyze(Boolean.valueOf(e.attributeValue("analyze")));
				collectionField.setisMust(Boolean.valueOf(e.attributeValue("must")));
				collectionField.setFieldBoost(Float.parseFloat(StringUtils.defaultString(e.attributeValue("boost"), "0")));
				collectionField.setHasTermOffset(Boolean.valueOf(e.attributeValue("termoffset")));
				collectionField.setHasTermPosition(Boolean.valueOf(e.attributeValue("termposition")));
				collectionField.setHasTermVector(Boolean.valueOf(e.attributeValue("termvector")));
				
				collection.putField(collectionField.getName(), collectionField);
				collection.addFieldName(collectionField.getName());
			}

			@SuppressWarnings("unchecked")
			List<Element> defaultSearchFields = node.selectNodes("//collection[@name='"+collection.getCollectionName()+"']"+"//defaultSearchFields/defaultSearchField");
			if(logger.isDebugEnabled()) {
				for(Element e : defaultSearchFields) {
					logger.debug("default search fields : {}", e.attributeValue("name"));
				}
			}
			
			for(Element e : defaultSearchFields) {
				collection.addDefaultSearchFieldNames(e.attributeValue("name"));
			}

			@SuppressWarnings("unchecked")
			List<Element> sortFields = node.selectNodes("//collection[@name='"+collection.getCollectionName()+"']"+"//sortFields/sortField");
			
			if(logger.isDebugEnabled()) {
				for(Element e : sortFields) {
					logger.debug("sortField source : {}", e.attributeValue("source"));
					logger.debug("sortField dest : {}", e.attributeValue("dest"));
				}
			}
			
			for(Element e : sortFields) {
				String srcFieldName = e.attributeValue("source");
				
				CollectionField srcField = collection.getFieldsByName().get(srcFieldName);
				if(srcField == null) {
					throw new IllegalStateException("정렬 필드 설정에 필요한 원본(source) 필드가 없습니다.");
				}
				
				try {
					String dstFieldName = e.attributeValue("dest");
					
					CollectionField dstField = (CollectionField) srcField.clone();
					dstField.setisAnalyze(false);
					dstField.setisIndexing(true);
					dstField.setName(dstFieldName);
					
					collection.putField(dstFieldName, dstField);
					collection.addSortFieldNames(dstFieldName);
					
				} catch (CloneNotSupportedException e1) {
					logger.error("error : ", e1);
				}
			}
			
			collections.put(collection.getCollectionName(), collection);
		}// end for(Element node : collectionList)
		
		logger.debug("collections : {}", collections);
		
	}
}
