package com.tistory.devyongsik.utils;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollections;
import com.tistory.devyongsik.handler.Handler;
import com.tistory.devyongsik.handler.IndexingRequestForm;
import com.tistory.devyongsik.handler.JsonDataHandler;
import com.tistory.devyongsik.index.CrescentIndexerExecutor;
import com.tistory.devyongsik.search.CrescentDocSearcher;
import com.tistory.devyongsik.search.CrescentSearcherManager;
import com.tistory.devyongsik.service.SearchService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
		"classpath:spring/applicationContext.xml",
 		"classpath:spring/action-config.xml"})
public class CrescentTestCaseUtil {
	
	@Autowired
	@Qualifier("crescentSearcherManager")
	protected CrescentSearcherManager crescentSearcherManager;
	
	@Autowired
	@Qualifier("crescentCollectionHandler")
	protected CrescentCollectionHandler collectionHandler;
	
	@Autowired
	@Qualifier("crescentIndexerExecutor")
	protected CrescentIndexerExecutor executor;
	
	@Autowired
	@Qualifier("crescentDefaultDocSearcher")
	protected CrescentDocSearcher crescentDocSearcher;
	
	@Autowired
	@Qualifier("searchService")
	protected SearchService searchService;
	
	private static String bulkIndexingTestText = "{\"command\":\"add\", \"indexingType\":\"bulk\",\"documentList\"" +
			":[{\"title\":\"제목 입니다0\",\"dscr\":\"텍스트 입니다0\",\"creuser\":\"creuser0\",\"board_id\":\"0\"}" +
			",{\"title\":\"제목 입니다1\",\"dscr\":\"텍스트 입니다1\",\"creuser\":\"creuser1\",\"board_id\":\"1\"}" +
			",{\"title\":\"제목 입니다2\",\"dscr\":\"텍스트 입니다2\",\"creuser\":\"creuser2\",\"board_id\":\"2\"}]}";

	static {
		System.setProperty("runningMode","test");
	}
	
	public void init() {
		initIndexFile();
	}
	
	private void initIndexFile() {
		indexingTestData();
	}

	private void indexingTestData() {
		
		Handler handler = new JsonDataHandler();
		IndexingRequestForm indexingRequestForm = handler.handledData(bulkIndexingTestText);
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		CrescentCollection collection = crescentCollections.getCrescentCollection("sample");
		
		String message = executor.indexing(collection, indexingRequestForm);

		System.out.println("indexing result message : " + message);
	}
}
