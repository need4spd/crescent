package com.tistory.devyongsik.domain;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.tistory.devyongsik.config.CrescentCollectionHandler;

public class CrescentCollectionFieldTest {

	@Test
	public void collectionFieldTest() {

		CrescentCollections crescentCollections = CrescentCollectionHandler.getInstance().getCrescentCollections();

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
