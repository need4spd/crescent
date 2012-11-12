package com.tistory.devyongsik.domain;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.tistory.devyongsik.config.CollectionConfig;

public class CollectionTest {

	@Test
	public void collectionTest() {
		CollectionConfig collectionConfig = CollectionConfig.getInstance();
		Map<String, Collection> collections = collectionConfig.getCollections();
		
		Collection sampleCollection = collections.get("sample");
		
		Assert.assertNotNull(sampleCollection);
		
		
	}
}
