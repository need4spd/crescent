package com.tistory.devyongsik.domain;

import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.lucene.document.Fieldable;
import org.junit.Assert;
import org.junit.Test;

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.config.SpringApplicationContext;
import com.tistory.devyongsik.index.LuceneFieldBuilder;
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
			Fieldable luceneField = luceneFieldBuilder.create(field, "30");
			
			System.out.println(luceneField);
			
			Assert.assertNotNull(luceneField);
		}
	}
}
