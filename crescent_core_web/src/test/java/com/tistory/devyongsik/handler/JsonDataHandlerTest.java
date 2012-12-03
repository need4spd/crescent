package com.tistory.devyongsik.handler;

import junit.framework.Assert;

import org.junit.Test;

import com.google.gson.Gson;
import com.tistory.devyongsik.utils.FormattedTextBuilder;

public class JsonDataHandlerTest {

	@Test
	public void indexingAddDocumentBulk() {
		String inputText = FormattedTextBuilder.getAddDocBulkJsonForm();
		
		Gson gson = new Gson();
		
		IndexingRequestForm indexingRequest = gson.fromJson(inputText, IndexingRequestForm.class);
		
		Assert.assertEquals("add", indexingRequest.getCommand());
		Assert.assertEquals(null, indexingRequest.getQuery());
		Assert.assertEquals("bulk", indexingRequest.getIndexingType());
		Assert.assertEquals("[{board_id=0, title=제목 입니다0, dscr=본문 입니다.0, creuser=test}]",
				indexingRequest.getDocumentList().toString());
	}
	
	@Test
	public void indexingAddDocumentIncrement() {
		String inputText = FormattedTextBuilder.getAddDocIncJsonForm();
		
		Gson gson = new Gson();
		
		IndexingRequestForm indexingRequest = gson.fromJson(inputText, IndexingRequestForm.class);
		
		Assert.assertEquals("add", indexingRequest.getCommand());
		Assert.assertEquals(null, indexingRequest.getQuery());
		Assert.assertEquals("incremental", indexingRequest.getIndexingType());
		Assert.assertEquals("[{board_id=0, title=제목 입니다0, dscr=본문 입니다.0, creuser=test}]",
				indexingRequest.getDocumentList().toString());
	}
	
	@Test
	public void indexingUpdateDocumentBulk() {
		String inputText = FormattedTextBuilder.getUpdateDocBulkJsonForm();
		
		Gson gson = new Gson();
		
		IndexingRequestForm indexingRequest = gson.fromJson(inputText, IndexingRequestForm.class);
		
		Assert.assertEquals("update", indexingRequest.getCommand());
		Assert.assertEquals("creuser:test", indexingRequest.getQuery());
		Assert.assertEquals("bulk", indexingRequest.getIndexingType());
		Assert.assertEquals("[{board_id=0, title=제목 입니다0 업데이트..., dscr=본문 입니다.0, creuser=test}]",
				indexingRequest.getDocumentList().toString());
	}
	
	@Test
	public void indexingUpdateDocumentInc() {
		String inputText = FormattedTextBuilder.getUpdateDocIncJsonForm();
		
		Gson gson = new Gson();
		
		IndexingRequestForm indexingRequest = gson.fromJson(inputText, IndexingRequestForm.class);
		
		Assert.assertEquals("update", indexingRequest.getCommand());
		Assert.assertEquals("creuser:test", indexingRequest.getQuery());
		Assert.assertEquals("incremental", indexingRequest.getIndexingType());
		Assert.assertEquals("[{board_id=0, title=제목 입니다0 업데이트..., dscr=본문 입니다.0, creuser=test}]",
				indexingRequest.getDocumentList().toString());
	}
	
	@Test
	public void indexingDeleteDocumentBulk() {
		String inputText = FormattedTextBuilder.getDeleteDocBulkJsonForm();
		
		Gson gson = new Gson();
		
		IndexingRequestForm indexingRequest = gson.fromJson(inputText, IndexingRequestForm.class);
		
		Assert.assertEquals("delete", indexingRequest.getCommand());
		Assert.assertEquals("creuser:test", indexingRequest.getQuery());
		Assert.assertEquals("bulk", indexingRequest.getIndexingType());
		Assert.assertEquals(null, indexingRequest.getDocumentList());
	}
	
	@Test
	public void indexingDeleteDocumentInc() {
		String inputText = FormattedTextBuilder.getDeleteDocIncJsonForm();
		
		Gson gson = new Gson();
		
		IndexingRequestForm indexingRequest = gson.fromJson(inputText, IndexingRequestForm.class);
		
		Assert.assertEquals("delete", indexingRequest.getCommand());
		Assert.assertEquals("creuser:test", indexingRequest.getQuery());
		Assert.assertEquals("incremental", indexingRequest.getIndexingType());
		Assert.assertEquals(null, indexingRequest.getDocumentList());
	}
}
