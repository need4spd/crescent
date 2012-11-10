package com.tistory.devyongsik.search;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.search.NRTManager;
import org.apache.lucene.search.NRTManager.TrackingIndexWriter;
import org.apache.lucene.search.SearcherFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.config.CollectionConfig;
import com.tistory.devyongsik.domain.Collection;
import com.tistory.devyongsik.index.IndexWriterManager;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 11.
 */
public class CrescentSearcherManager {

	private static CrescentSearcherManager searcherManager = new CrescentSearcherManager();
	
	private Map<String, NRTManager> nrtManagerByCollection = new ConcurrentHashMap<String, NRTManager>();
	//private Map<String, IndexReader> indexReadersByCollection = new ConcurrentHashMap<String, IndexReader>();

	private Logger logger = LoggerFactory.getLogger(CrescentSearcherManager.class);
	
	private CrescentSearcherManager() {
		try {
			indexSearcherInit();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException();
		}
	}
	
	public static CrescentSearcherManager getCrescentSearcherManager() {
		return searcherManager;
	}
	
	private void indexSearcherInit() throws IOException {
		
		logger.info("indexSearcherManager init start.....");
		
		CollectionConfig collectionConfig = CollectionConfig.getInstance();
		
		Map<String, Collection> collections = collectionConfig.getCollections();
		Set<String> collectionNames = collections.keySet();
		
		IndexWriterManager indexWriterManager = IndexWriterManager.getIndexWriterManager();
		
		for(String collectionName : collectionNames) {
			
			logger.info("collection name {}", collectionName);
			
			SearcherFactory searcherFactory = new SearcherFactory();
			TrackingIndexWriter trackingIndexWriter = indexWriterManager.getTrackingIndexWriterBy(collectionName);
			NRTManager nrtManager = new NRTManager(trackingIndexWriter, searcherFactory);
			
			nrtManagerByCollection.put(collectionName, nrtManager);
			
			logger.info("searcher manager created....");
		}
	}
	
	public synchronized NRTManager getSearcherManager(String collectionName) {
		NRTManager nrtManager = nrtManagerByCollection.get(collectionName);
		
		try {
			
			nrtManager.maybeRefresh();
		
		} catch (IOException e) {
		
			logger.error("exception in CrescentSearcherManager : {}", e);
			new IllegalStateException("NRT Manager maybeRefresh Exception in " + collectionName + ".");
		
		}
		
		logger.info("current searcher generation : {}", nrtManager.getCurrentSearchingGen());
		
		return nrtManager;
	}
}
