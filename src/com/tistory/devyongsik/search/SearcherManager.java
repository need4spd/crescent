package com.tistory.devyongsik.search;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.config.CollectionConfig;
import com.tistory.devyongsik.domain.Collection;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 11.
 */
public class SearcherManager {

	private static SearcherManager searcherManager = new SearcherManager();
	
	private Map<String, IndexSearcher> indexSearchersByCollection = new ConcurrentHashMap<String, IndexSearcher>();
	private Map<String, IndexReader> indexReadersByCollection = new ConcurrentHashMap<String, IndexReader>();

	private Logger logger = LoggerFactory.getLogger(SearcherManager.class);
	
	private SearcherManager() {
		try {
			indexSearcherInit();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException();
		}
	}
	
	public static SearcherManager getSearcherManager() {
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
			
			//int numOfIndex = collection.getNumberOfIndexFiles();
			
			Directory dir = FSDirectory.open(new File(indexDir));
			IndexReader reader = IndexReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(reader);
		
			logger.info("indexreader and indexsearcher created....");
			indexReadersByCollection.put(collectionName, reader);
			indexSearchersByCollection.put(collectionName, searcher);
		}
	}
	
	public IndexSearcher getIndexSearcher(String collectionName) {
		return indexSearchersByCollection.get(collectionName);
	}
}
