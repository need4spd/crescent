package com.tistory.devyongsik.index;

import java.util.List;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.domain.Collection;
import com.tistory.devyongsik.handler.Handler;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 18.
 */
public class FullmoonIndexExecutor {
	private Logger logger = LoggerFactory.getLogger(FullmoonIndexExecutor.class);
	private Collection collection = null;
	private Handler handler = null;
	
	public FullmoonIndexExecutor(Collection collection, Handler handler) {
		this.collection = collection;
		this.handler = handler;
	}

	public void execute(String jsonFormStr) {
		
		List<Document> documentList = handler.handledData(jsonFormStr, collection.getFieldsByName());		
		FullmoonIndexer fullmoonIndexer = new FullmoonIndexer(collection.getIndexingDir());
		fullmoonIndexer.indexing(documentList);
		
		logger.info("{} 의 {}건 인덱싱이 완료되었습니다.", new String[] {collection.getCollectionName(), String.valueOf(documentList.size())});
		//ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
		//for(int part = 0; part < numberOfIndexFiles; part++) {
		//	threadExecutor.execute(new IndexExecutor(String.valueOf(part)));
		//}
		
//		threadExecutor.shutdown();
//		
//		while(!threadExecutor.isTerminated()) {
//			logger.info("waiting...");
//			
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
//	private class IndexExecutor implements Runnable {
//
//		//private String jobPart = null;
//
////		public IndexExecutor(String part) {
////			this.jobPart = part;
////		}
//
//		@Override
//		public void run() {
//			
//		}
//	}
}
