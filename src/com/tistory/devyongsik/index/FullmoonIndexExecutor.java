package com.tistory.devyongsik.index;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.domain.Collection;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 18.
 */
public class FullmoonIndexExecutor {
	private Logger logger = LoggerFactory.getLogger(FullmoonIndexExecutor.class);
	private Collection collection = null;

	public FullmoonIndexExecutor(Collection collection) {
		this.collection = collection;
	}

	public void execute() {
		
		int numberOfIndexFiles = collection.getNumberOfIndexFiles();
		
		ExecutorService threadExecutor = Executors.newFixedThreadPool(numberOfIndexFiles);
		for(int part = 0; part < numberOfIndexFiles; part++) {
			threadExecutor.execute(new IndexExecutor(String.valueOf(part)));
		}
		
		threadExecutor.shutdown();
		
		while(!threadExecutor.isTerminated()) {
			logger.info("waiting...");
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class IndexExecutor implements Runnable {

		private String jobPart = null;

		public IndexExecutor(String part) {
			this.jobPart = part;
		}

		@Override
		public void run() {
			FullmoonIndexer fullmoonIndexer = new FullmoonIndexer(jobPart, collection);
			fullmoonIndexer.indexing();
			
			logger.info("{} 의 {} 인덱싱이 완료되었습니다.", new String[] {collection.getName(), jobPart});
		}
	}
}
