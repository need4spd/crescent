package com.tistory.devyongsik.crescent.data.handler;

import java.io.IOException;

import junit.framework.Assert;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import com.tistory.devyongsik.crescent.index.entity.IndexingRequestForm;
import com.tistory.devyongsik.utils.FormattedTextBuilder;

public class JsonDataHandlerTest {

	@Test
	public void indexingAddDocumentBulk() {
		String inputText = FormattedTextBuilder.getAddDocBulkJsonForm();
		
		ObjectMapper mapper = new ObjectMapper();
		IndexingRequestForm indexingRequest = null;
		try {
			indexingRequest = mapper.readValue(inputText, IndexingRequestForm.class);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		
		Assert.assertEquals("add", indexingRequest.getCommand());
		Assert.assertEquals(null, indexingRequest.getQuery());
		Assert.assertEquals("bulk", indexingRequest.getIndexingType());
		Assert.assertEquals("[{board_id=0, title=제목 입니다0, dscr=본문 입니다.0, creuser=test}]",
				indexingRequest.getDocumentList().toString());
	}
	
	@Test
	public void indexingAddDocumentIncrement() {
		String inputText = FormattedTextBuilder.getAddDocIncJsonForm();
		
		ObjectMapper mapper = new ObjectMapper();
		IndexingRequestForm indexingRequest = null;
		try {
			indexingRequest = mapper.readValue(inputText, IndexingRequestForm.class);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		
		Assert.assertEquals("add", indexingRequest.getCommand());
		Assert.assertEquals(null, indexingRequest.getQuery());
		Assert.assertEquals("incremental", indexingRequest.getIndexingType());
		Assert.assertEquals("[{board_id=0, title=제목 입니다0, dscr=본문 입니다.0, creuser=test}]",
				indexingRequest.getDocumentList().toString());
	}
	
	@Test
	public void indexingUpdateDocumentBulk() {
		String inputText = FormattedTextBuilder.getUpdateDocBulkJsonForm();
		
		ObjectMapper mapper = new ObjectMapper();
		IndexingRequestForm indexingRequest = null;
		try {
			indexingRequest = mapper.readValue(inputText, IndexingRequestForm.class);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		
		Assert.assertEquals("update", indexingRequest.getCommand());
		Assert.assertEquals("creuser:test", indexingRequest.getQuery());
		Assert.assertEquals("bulk", indexingRequest.getIndexingType());
		Assert.assertEquals("[{board_id=0, title=제목 입니다0 업데이트..., dscr=본문 입니다.0, creuser=test}]",
				indexingRequest.getDocumentList().toString());
	}
	
	@Test
	public void indexingUpdateDocumentInc() {
		String inputText = FormattedTextBuilder.getUpdateDocIncJsonForm();
		
		ObjectMapper mapper = new ObjectMapper();
		IndexingRequestForm indexingRequest = null;
		try {
			indexingRequest = mapper.readValue(inputText, IndexingRequestForm.class);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		
		
		Assert.assertEquals("update", indexingRequest.getCommand());
		Assert.assertEquals("creuser:test", indexingRequest.getQuery());
		Assert.assertEquals("incremental", indexingRequest.getIndexingType());
		Assert.assertEquals("[{board_id=0, title=제목 입니다0 업데이트..., dscr=본문 입니다.0, creuser=test}]",
				indexingRequest.getDocumentList().toString());
	}
	
	@Test
	public void indexingDeleteDocumentBulk() {
		String inputText = FormattedTextBuilder.getDeleteDocBulkJsonForm();
		
		ObjectMapper mapper = new ObjectMapper();
		IndexingRequestForm indexingRequest = null;
		try {
			indexingRequest = mapper.readValue(inputText, IndexingRequestForm.class);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		
		Assert.assertEquals("delete", indexingRequest.getCommand());
		Assert.assertEquals("creuser:test", indexingRequest.getQuery());
		Assert.assertEquals("bulk", indexingRequest.getIndexingType());
		Assert.assertEquals(null, indexingRequest.getDocumentList());
	}
	
	@Test
	public void indexingDeleteDocumentInc() {
		String inputText = FormattedTextBuilder.getDeleteDocIncJsonForm();
		
		ObjectMapper mapper = new ObjectMapper();
		IndexingRequestForm indexingRequest = null;
		try {
			indexingRequest = mapper.readValue(inputText, IndexingRequestForm.class);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		
		Assert.assertEquals("delete", indexingRequest.getCommand());
		Assert.assertEquals("creuser:test", indexingRequest.getQuery());
		Assert.assertEquals("incremental", indexingRequest.getIndexingType());
		Assert.assertEquals(null, indexingRequest.getDocumentList());
	}
}
