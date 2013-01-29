package com.tistory.devyongsik.config;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollections;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;

public class CrescentCollectionHandlerTest {

	@BeforeClass
	public static void init() {
		CrescentTestCaseUtil.init();
	}
	
	@Test
	public void loadFromXML() {
		CrescentCollectionHandler handler = CrescentCollectionHandler.getInstance();
		CrescentCollections collections = handler.getCrescentCollections();
		
		System.out.println(collections);
		
		Assert.assertNotNull(collections);
		
		List<CrescentCollection> collectionList = collections.getCrescentCollections();
		
		Assert.assertTrue(collectionList.size() > 0);
	}
	
	@Test
	public void writeToXML() {
		CrescentCollectionHandler handler = CrescentCollectionHandler.getInstance();
		CrescentCollections collections = handler.getCrescentCollections();
		
		System.out.println(collections);
		
		Assert.assertNotNull(collections);
		
		handler.writeToXML();
		handler.reloadCollectionsXML();
		
		CrescentCollections collections2 = handler.getCrescentCollections();
		
		System.out.println(collections2);
	}
}
