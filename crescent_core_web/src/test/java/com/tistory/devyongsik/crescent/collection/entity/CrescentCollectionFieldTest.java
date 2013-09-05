package com.tistory.devyongsik.crescent.collection.entity;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.junit.Test;

import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.config.SpringApplicationContext;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;


public class CrescentCollectionFieldTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() {
		super.init();
	}

	@Test
	public void collectionFieldTest() {

		CrescentCollectionHandler collectionHandler 
		= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
		
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();

		Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

		CrescentCollection sampleCollection = collections.get("sample");


		Map<String, CrescentCollectionField> fieldsByName = sampleCollection.getCrescentFieldByName();

		Set<String> fieldNames = fieldsByName.keySet();

		for(String fieldName : fieldNames) {
			CrescentCollectionField field = fieldsByName.get(fieldName);
			Assert.assertNotNull(field);
		}
	}
}
