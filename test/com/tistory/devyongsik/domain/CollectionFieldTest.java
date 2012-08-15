package com.tistory.devyongsik.domain;

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.tistory.devyongsik.config.CollectionConfig;

public class CollectionFieldTest {

	@Test
	public void collectionFieldTest() {
		CollectionConfig collectionConfig = CollectionConfig.getInstance();
		Map<String, Collection> collections = collectionConfig.getCollections();
		
		Collection sampleCollection = collections.get("sample");
		
		
		Map<String, CollectionField> fieldsByName = sampleCollection.getFieldsByName();
		
		Set<String> fieldNames = fieldsByName.keySet();
		
		for(String fieldName : fieldNames) {
			CollectionField field = fieldsByName.get(fieldName);
			Assert.assertNotNull(field);
		}
	}
}
