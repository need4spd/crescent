package com.tistory.devyongsik.index;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollections;
import com.tistory.devyongsik.handler.Handler;
import com.tistory.devyongsik.handler.IndexingRequestForm;
import com.tistory.devyongsik.handler.JsonDataHandler;
import com.tistory.devyongsik.utils.FormattedTextBuilder;

public class CrescentIndexerExecutorTest {
	@Test
	public void addDocument() {
		CrescentCollections crescentCollections = CrescentCollectionHandler.getInstance().getCrescentCollections();
		Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

		CrescentCollection sampleCollection = collections.get("sample");

		Handler handler = new JsonDataHandler();
		IndexingRequestForm indexingRequestForm = handler.handledData(FormattedTextBuilder.getAddDocBulkJsonForm());
		
		CrescentIndexerExecutor executor = new CrescentIndexerExecutor(sampleCollection, indexingRequestForm);
		String returnMessage = executor.indexing();
		
		Assert.assertEquals("1건의 색인이 완료되었습니다.", returnMessage);
	}
}
