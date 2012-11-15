package com.tistory.devyongsik.domain;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.tistory.devyongsik.config.CrescentCollectionHandler;

public class CrescentCollectionTest {

	@Test
	public void collectionTest() {
		CrescentCollections crescentCollections = CrescentCollectionHandler.getInstance().getCrescentCollections();

		Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

		CrescentCollection sampleCollection = collections.get("sample");
		
		Assert.assertNotNull(sampleCollection);
		
		
	}
}
