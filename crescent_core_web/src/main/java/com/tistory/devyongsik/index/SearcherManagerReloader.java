package com.tistory.devyongsik.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.SearcherManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.config.SpringApplicationContext;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollections;
import com.tistory.devyongsik.search.CrescentSearcherManager;

public class SearcherManagerReloader {

	private Logger logger = LoggerFactory.getLogger(SearcherManagerReloader.class);
	
	private CrescentSearcherManager crescentSearcherManager = null;
	private CrescentCollections crescentCollections = null;
	
	private List<ScheduledThreadPoolExecutor> execList = new ArrayList<ScheduledThreadPoolExecutor>();
	
	public SearcherManagerReloader() {
		crescentSearcherManager = CrescentSearcherManager.getCrescentSearcherManager();
		CrescentCollectionHandler collectionHandler 
			= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
		crescentCollections = collectionHandler.getCrescentCollections();
	}
	
	public void reloadStart() {
		List<CrescentCollection> crescentCollectionList = crescentCollections.getCrescentCollections();
		for(CrescentCollection collection : crescentCollectionList) {
			
			ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
			execList.add(exec);
			
			exec.scheduleWithFixedDelay(new Reloader(collection.getName()), 0, Integer.parseInt(collection.getSearcherReloadScheduleMin()), TimeUnit.MINUTES);
		}
	}
	
	public void shutdown() {
		for(ScheduledThreadPoolExecutor exec : execList) {
			List<Runnable> rList = exec.shutdownNow();
			
			logger.info("Reloader Shutdown.. {}", rList.toString());
		}
		
		List<CrescentCollection> crescentCollectionList = crescentCollections.getCrescentCollections();
		
		for(CrescentCollection collection : crescentCollectionList) {
			IndexWriterManager indexWriterManager = IndexWriterManager.getIndexWriterManager();
			IndexWriter indexWriter = indexWriterManager.getIndexWriter(collection.getName());
			
			try {
				indexWriter.close();
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			logger.error("IndexWriter close....{}", collection.getName());
		}
	}
	
	private class Reloader implements Runnable {
		
		private String collectionName = null;
		
		public Reloader(String collectionName) {
			this.collectionName = collectionName;
			
			logger.info("Reloader Start {} ", collectionName);
		}
		
		@Override
		public void run() {
			SearcherManager searcherManager = crescentSearcherManager.getSearcherManager(collectionName);
			boolean refreshed = false;
			
			try {
				
				refreshed = searcherManager.maybeRefresh();
				
			} catch (IOException e) {
				logger.error("Searcher Manager Reloader Error!", e);
			}
			
			logger.info("Searcher Manager Reloaded..{}, {}", collectionName, refreshed);
		}	
	}
}
