package com.tistory.devyongsik.utils;

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollections;
import com.tistory.devyongsik.handler.Handler;
import com.tistory.devyongsik.handler.IndexingRequestForm;
import com.tistory.devyongsik.handler.JsonDataHandler;
import com.tistory.devyongsik.index.CrescentIndexerExecutor;

public class CrescentTestCaseUtil {

	private static String bulkIndexingTestText = "{\"command\":\"add\", \"indexingType\":\"bulk\",\"documentList\"" +
			":[{\"title\":\"제목 입니다0\",\"dscr\":\"텍스트 입니다0\",\"creuser\":\"creuser0\",\"board_id\":\"0\"}" +
			",{\"title\":\"제목 입니다1\",\"dscr\":\"텍스트 입니다1\",\"creuser\":\"creuser1\",\"board_id\":\"1\"}" +
			",{\"title\":\"제목 입니다2\",\"dscr\":\"텍스트 입니다2\",\"creuser\":\"creuser2\",\"board_id\":\"2\"}]}";

	public static void init() {
		System.setProperty("runningMode","test");
		initIndexFile();
	}
	
	private static void initIndexFile() {
		indexingTestData();
	}

	private static void indexingTestData() {
		CrescentCollectionHandler crescentCollectionHandler = CrescentCollectionHandler.getInstance();
		Handler handler = new JsonDataHandler();
		IndexingRequestForm indexingRequestForm = handler.handledData(bulkIndexingTestText);
		CrescentCollections crescentCollections = crescentCollectionHandler.getCrescentCollections();
		CrescentCollection collection = crescentCollections.getCrescentCollection("sample");
		CrescentIndexerExecutor executor = new CrescentIndexerExecutor(collection, indexingRequestForm);

		String message = executor.indexing();

		System.out.println("indexing result message : " + message);
	}
}
