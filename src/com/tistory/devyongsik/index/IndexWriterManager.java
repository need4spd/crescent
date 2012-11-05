package com.tistory.devyongsik.index;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.config.CollectionConfig;
import com.tistory.devyongsik.domain.Collection;

public class IndexWriterManager {

	private static IndexWriterManager indexWriterManager = new IndexWriterManager();
	private Map<String, IndexWriter> indexWritersByCollectionName = new ConcurrentHashMap<String, IndexWriter>();
	private Logger logger = LoggerFactory.getLogger(IndexWriterManager.class);
	
	private IndexWriterManager() {
		try {
			initIndexWriter();
		} catch (IOException e) {
			throw new IllegalStateException("IndexWriter 생성 실패");
		}
	}
	
	public static IndexWriterManager getIndexWriterManager() {
		return indexWriterManager;
	}
	
	private void initIndexWriter() throws IOException {
		CollectionConfig collectionConfig = CollectionConfig.getInstance();
		
		Map<String, Collection> collections = collectionConfig.getCollections();
		Set<String> collectionNames = collections.keySet();
		
		for(String collectionName : collectionNames) {
			
			logger.info("collection name {}", collectionName);
			
			Collection collection = collections.get(collectionName);
			String indexDir = collection.getIndexingDir();
			
			logger.info("index file dir ; {}", indexDir);
			
			Directory dir = FSDirectory.open(new File(indexDir));
			
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_35, new KoreanAnalyzer(true));
			IndexWriter indexWriter = new IndexWriter(dir, conf);
			
			indexWritersByCollectionName.put(collectionName, indexWriter);
			
			logger.info("index writer for collection {} is initialized...", collectionName);
		}
	}
	
	public IndexWriter getIndexWriterBy(String collectionName) {
		return indexWritersByCollectionName.get(collectionName);
	}
}
