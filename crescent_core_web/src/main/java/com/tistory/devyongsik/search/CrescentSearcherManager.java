package com.tistory.devyongsik.search;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.config.SpringApplicationContext;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollections;
import com.tistory.devyongsik.index.IndexWriterManager;

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
		
		CrescentCollectionHandler collectionHandler 
		= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
		CrescentCollections collections = collectionHandler.getCrescentCollections();
		
		Map<String, CrescentCollection> collectionsMap = collections.getCrescentCollectionsMap();
		
		Set<String> collectionNames = collectionsMap.keySet();
		
		IndexWriterManager indexWriterManager = IndexWriterManager.getIndexWriterManager();
		
		for(String collectionName : collectionNames) {
			
			logger.info("collection name {}", collectionName);
			
			SearcherFactory searcherFactory = new SearcherFactory();
			IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);
			SearcherManager searcherManager = new SearcherManager(indexWriter, true, searcherFactory);
			
			searcherManagerByCollection.put(collectionName, searcherManager);
			
			logger.info("searcher manager created....");
		}
	}
	
	public SearcherManager getSearcherManager(String collectionName) {
		SearcherManager searcherManager = searcherManagerByCollection.get(collectionName);
		
		try {
			
			searcherManager.maybeRefresh();
		
		} catch (IOException e) {
		
			logger.error("exception in CrescentSearcherManager : {}", e);
			new IllegalStateException("SearcherManager maybeRefresh Exception in " + collectionName + ".");
		
		}
		
		//logger.info("current searcher generation : {}", nrtManager.getCurrentSearchingGen());
		
		return searcherManager;
	}
}
