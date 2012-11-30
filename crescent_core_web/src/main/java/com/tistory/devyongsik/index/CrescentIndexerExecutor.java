package com.tistory.devyongsik.index;

import java.util.List;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.handler.Handler;

public class CrescentIndexerExecutor {
	private Logger logger = LoggerFactory.getLogger(CrescentIndexerExecutor.class);
	private CrescentCollection collection = null;
	private Handler handler = null;
	
	public CrescentIndexerExecutor(CrescentCollection collection, Handler handler) {
		this.collection = collection;
		this.handler = handler;
	}

	public String bulkIndexing(String jsonFormStr) {
		
		List<Document> documentList = handler.handledData(jsonFormStr, collection.getCrescentFieldByName());		
		CrescentIndexer crescentIndexer = new CrescentIndexer(collection.getName());
		crescentIndexer.indexing(documentList);
		
		crescentIndexer.commit();
		
		String logMessage = collection.getName() + "의 " + documentList.size() + "건 색인 완료";
		logger.info("{} 의 {}건 인덱싱이 완료되었습니다.", new String[] {collection.getName(), String.valueOf(documentList.size())});
		
		return logMessage;
	}
	
	public String incrementalIndexing(String jsonFormStr) {
		
		List<Document> documentList = handler.handledData(jsonFormStr, collection.getCrescentFieldByName());		
		CrescentIndexer crescentIndexer = new CrescentIndexer(collection.getName());
		crescentIndexer.indexing(documentList);
		
		String logMessage = collection.getName() + "의 " + documentList.size() + "건 색인 완료";
		logger.info("{} 의 {}건 인덱싱이 완료되었습니다.", new String[] {collection.getName(), String.valueOf(documentList.size())});
		
		return logMessage;
	}
}
