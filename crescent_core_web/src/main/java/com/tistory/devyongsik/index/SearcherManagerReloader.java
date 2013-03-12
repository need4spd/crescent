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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.search.CrescentSearcherManager;

@Component
public class SearcherManagerReloader implements SmartLifecycle {

	private Logger logger = LoggerFactory.getLogger(SearcherManagerReloader.class);
	
	@Autowired
	@Qualifier("crescentSearcherManager")
	private CrescentSearcherManager crescentSearcherManager;
	
	@Autowired
	@Qualifier("crescentCollectionHandler")
	private CrescentCollectionHandler crescentCollectionHandler;
	
	@Autowired
	@Qualifier("indexWriterManager")
	private IndexWriterManager indexWriterManager;
	
	private List<ScheduledThreadPoolExecutor> execList = new ArrayList<ScheduledThreadPoolExecutor>();
	
	private boolean isRunning = false;
	
	private void reloadStart() {
		
		List<CrescentCollection> crescentCollectionList = crescentCollectionHandler.getCrescentCollections().getCrescentCollections();

		logger.info("reloader start.....[{}]", crescentCollectionList);
		

		for(CrescentCollection collection : crescentCollectionList) {
			
			ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
			execList.add(exec);
			
			exec.scheduleWithFixedDelay(new Reloader(collection.getName()), 0, Integer.parseInt(collection.getSearcherReloadScheduleMin()), TimeUnit.MINUTES);
		}
	}
	
	private void shutdown() {
		for(ScheduledThreadPoolExecutor exec : execList) {
			List<Runnable> rList = exec.shutdownNow();
			
			logger.info("Reloader Shutdown.. {}", rList.toString());
		}
		
		List<CrescentCollection> crescentCollectionList = crescentCollectionHandler.getCrescentCollections().getCrescentCollections();
		
		for(CrescentCollection collection : crescentCollectionList) {
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

	@Override
	public void start() {
		reloadStart();

		isRunning = true;
	}

	@Override
	public void stop() {
		shutdown();
		
		isRunning = false;
	}

	@Override
	public boolean isRunning() {
		
		return isRunning;
	}

	@Override
	public int getPhase() {
		return 0;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}
}
