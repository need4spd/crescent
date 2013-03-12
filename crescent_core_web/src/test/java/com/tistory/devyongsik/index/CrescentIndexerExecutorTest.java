package com.tistory.devyongsik.index;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import junit.framework.Assert;

import org.apache.lucene.index.CorruptIndexException;
import org.junit.Test;

import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollections;
import com.tistory.devyongsik.handler.Handler;
import com.tistory.devyongsik.handler.IndexingRequestForm;
import com.tistory.devyongsik.handler.JsonDataHandler;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;
import com.tistory.devyongsik.utils.FormattedTextBuilder;

public class CrescentIndexerExecutorTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() {
		super.init();
	}
	
	@Test
	public void addDocument() throws CorruptIndexException, IOException {
		
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

		CrescentCollection sampleCollection = collections.get("sample");

		Handler handler = new JsonDataHandler();
		IndexingRequestForm indexingRequestForm = handler.handledData(FormattedTextBuilder.getAddDocBulkJsonForm());
		
		String returnMessage = executor.indexing(sampleCollection, indexingRequestForm);
		
		Assert.assertEquals("1건의 색인이 완료되었습니다.", returnMessage);
	}
	
	@Test
	public void deleteDocument() throws CorruptIndexException, IOException {
		
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

		CrescentCollection sampleCollection = collections.get("sample");

		Handler handler = new JsonDataHandler();
		IndexingRequestForm indexingRequestForm = handler.handledData(FormattedTextBuilder.getDeleteDocBulkJsonForm());
		
		String returnMessage = executor.indexing(sampleCollection, indexingRequestForm);
		
		Assert.assertEquals("creuser:test에 대한 delete가 완료되었습니다.", returnMessage);
	}
	
	@Test
	public void updateDocument() throws CorruptIndexException, IOException {
		
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

		CrescentCollection sampleCollection = collections.get("sample");

		Handler handler = new JsonDataHandler();
		IndexingRequestForm indexingRequestForm = handler.handledData(FormattedTextBuilder.getUpdateDocBulkJsonForm());
		
		String returnMessage = executor.indexing(sampleCollection, indexingRequestForm);
		
		Assert.assertEquals("creuser:test에 대한 update가 완료되었습니다.", returnMessage);
	}
}
