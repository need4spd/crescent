package com.tistory.devyongsik.domain;

import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;
import org.junit.Assert;
import org.junit.Test;

import com.tistory.devyongsik.config.CollectionConfig;

public class LuceneFieldBuilderTest {

	@Test
	public void create() {
		CollectionConfig collectionConfig = CollectionConfig.getInstance();
		Map<String, Collection> collections = collectionConfig.getCollections();
		
		Collection sampleCollection = collections.get("sample");
		
		
		Map<String, CollectionField> fieldsByName = sampleCollection.getFieldsByName();
		
		Set<String> fieldNames = fieldsByName.keySet();
		LuceneFieldBuilder luceneFieldBuilder = new LuceneFieldBuilder();
		
		for(String fieldName : fieldNames) {
			CollectionField field = fieldsByName.get(fieldName);
			Field luceneField = luceneFieldBuilder.create(field, "");
			
			System.out.println(luceneField);
			
			Assert.assertNotNull(luceneField);
		}
	}
}
