package com.tistory.devyongsik.crescent.collection.entity;

import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.lucene.index.IndexableField;
import org.junit.Assert;
import org.junit.Test;

import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.config.SpringApplicationContext;
import com.tistory.devyongsik.crescent.index.LuceneFieldBuilder;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;

public class LuceneFieldBuilderTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() {
		super.init();
	}

	@Test
	public void create() {
		CrescentCollectionHandler collectionHandler 
		= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
		
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();

		Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

		CrescentCollection sampleCollection = collections.get("sample");

		
		Map<String, CrescentCollectionField> fieldsByName = sampleCollection.getCrescentFieldByName();
		
		Set<String> fieldNames = fieldsByName.keySet();
		LuceneFieldBuilder luceneFieldBuilder = new LuceneFieldBuilder();
		
		for(String fieldName : fieldNames) {
			CrescentCollectionField field = fieldsByName.get(fieldName);
			IndexableField luceneField = luceneFieldBuilder.create(field, "30");
			
			System.out.println(luceneField);
			
			Assert.assertNotNull(luceneField);
		}
	}
}
