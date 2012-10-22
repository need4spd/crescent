package com.tistory.devyongsik.search;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.config.CollectionConfig;
import com.tistory.devyongsik.domain.Collection;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 11.
 */
public class CrescentSearcherManager {

	private static CrescentSearcherManager searcherManager = new CrescentSearcherManager();
	
	private Map<String, SearcherManager> searcherManagerByCollection = new ConcurrentHashMap<String, SearcherManager>();
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
		
		for(String collectionName : collectionNames) {
			
			logger.info("collection name {}", collectionName);
			
			Collection collection = collections.get(collectionName);
			String indexDir = collection.getIndexingDir();
			
			logger.info("index file dir ; {}", indexDir);
			
			Directory dir = FSDirectory.open(new File(indexDir));
			//IndexReader reader = IndexReader.open(dir);
			
			SearcherFactory searcherFactory = new SearcherFactory();
			SearcherManager searcherManager = new SearcherManager(dir, searcherFactory);
			searcherManagerByCollection.put(collectionName, searcherManager);
			
			logger.info("searcher manager created....");
		}
	}
	
	public SearcherManager getSearcherManager(String collectionName) {
		return searcherManagerByCollection.get(collectionName);
	}
}
