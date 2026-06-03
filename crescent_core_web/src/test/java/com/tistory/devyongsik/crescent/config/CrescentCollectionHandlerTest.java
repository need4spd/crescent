package com.tistory.devyongsik.crescent.config;

import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.junit.Test;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollections;
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

		Assert.assertNotNull(collections);
		Assert.assertTrue(collections.getCrescentCollections().size() > 0);
	}

	@Test
	public void writeToXML() {
		CrescentCollectionHandler collectionHandler
			= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);

		CrescentCollections collections = collectionHandler.getCrescentCollections();
		Assert.assertNotNull(collections);

		collectionHandler.writeToXML();
		collectionHandler.reloadCollectionsXML();

		CrescentCollections collections2 = collectionHandler.getCrescentCollections();
		Assert.assertNotNull(collections2);
	}

	@Test
	public void testIndexingDirectoryIsMemoryInTestMode() {
		CrescentCollectionHandler collectionHandler
			= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);

		CrescentCollections collections = collectionHandler.getCrescentCollections();
		List<CrescentCollection> list = collections.getCrescentCollections();

		for (CrescentCollection collection : list) {
			Assert.assertEquals(
				"테스트 모드에서는 indexingDirectory가 memory여야 합니다",
				"memory", collection.getIndexingDirectory());
		}
	}

	@Test
	public void resolveIndexingDirectoryUsesCrescentHome() {
		CrescentCollectionHandler collectionHandler
			= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);

		// crescentHome이 설정된 경우 상대 경로를 crescentHome 기준으로 해석
		// (테스트 환경에서는 crescentHomeLocation이 "default"이므로 relativePath 그대로 반환)
		String result = collectionHandler.resolveIndexingDirectory("crescent_index/sample");
		Assert.assertNotNull(result);
		Assert.assertTrue("결과 경로에 상대 경로 세그먼트가 포함되어야 합니다",
			result.contains("crescent_index/sample"));
	}
}
