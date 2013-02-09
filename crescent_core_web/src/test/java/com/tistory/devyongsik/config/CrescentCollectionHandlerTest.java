package com.tistory.devyongsik.config;

import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.junit.Test;

import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollections;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;

public class CrescentCollectionHandlerTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() {
		super.init();
	}
	
	@Test
	public void loadFromXML() {
		CrescentCollectionHandler collectionHandler 
			= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
		
		CrescentCollections collections = collectionHandler.getCrescentCollections();
		
		System.out.println(collections);
		
		Assert.assertNotNull(collections);
		
		List<CrescentCollection> collectionList = collections.getCrescentCollections();
		
		Assert.assertTrue(collectionList.size() > 0);
	}
	
	@Test
	public void writeToXML() {
		CrescentCollectionHandler collectionHandler 
			= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
		
		CrescentCollections collections = collectionHandler.getCrescentCollections();
		
		System.out.println(collections);
		
		Assert.assertNotNull(collections);
		
		collectionHandler.writeToXML();
		collectionHandler.reloadCollectionsXML();
		
		CrescentCollections collections2 = collectionHandler.getCrescentCollections();
		
		System.out.println(collections2);
	}
}
