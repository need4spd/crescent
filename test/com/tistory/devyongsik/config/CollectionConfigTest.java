package com.tistory.devyongsik.config;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.tistory.devyongsik.domain.Collection;

/**
 * author : need4spd, need4spd@naver.com, 2012. 2. 26.
 */
public class CollectionConfigTest {

	@Test
	public void getCollectionConfig() {
		CollectionConfig collectionConfig = CollectionConfig.getInstance();
		Map<String, Collection> collections = collectionConfig.getCollections();
		
		System.out.println(collections);
		
		Assert.assertNotNull(collections);
	}
}
