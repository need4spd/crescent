package com.tistory.devyongsik.index;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.config.SpringApplicationContext;
import com.tistory.devyongsik.domain.CrescentCollection;

public class IndexWriterManager {

	private static IndexWriterManager indexWriterManager = new IndexWriterManager();
	private Map<String, IndexWriter> indexWritersByCollectionName = new ConcurrentHashMap<String, IndexWriter>();
	private Logger logger = LoggerFactory.getLogger(IndexWriterManager.class);
	
	private IndexWriterManager() {
		try {
			initIndexWriter();
		} catch (IOException e) {
			logger.error("IndexWriter 생성 실패", e);
			throw new IllegalStateException("IndexWriter 생성 실패");
		}
	}
	
	public static IndexWriterManager getIndexWriterManager() {
		return indexWriterManager;
	}
	
	private void initIndexWriter() throws IOException {
		CrescentCollectionHandler collectionHandler 
		= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
		
		
		for(CrescentCollection crescentCollection : collectionHandler.getCrescentCollections().getCrescentCollections()) {
			
			logger.info("collection name {}", crescentCollection.getName());
			
			String indexDir = crescentCollection.getIndexingDirectory();
			
			logger.info("index file dir ; {}", indexDir);
			
			Directory dir = null;
			
			if(indexDir.equals("memory")) {
				dir = new RAMDirectory();
			} else {
				dir = FSDirectory.open(new File(indexDir));
			}
			
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_35, new KoreanAnalyzer(true));
			//conf.setOpenMode(OpenMode.CREATE);
			//conf.setIndexDeletionPolicy(new LastCommitDeletePolicy());
			
			IndexWriter indexWriter = new IndexWriter(dir, conf);
			indexWritersByCollectionName.put(crescentCollection.getName(), indexWriter);
			
			logger.info("index writer for collection {} is initialized...", crescentCollection.getName());
		}
	}
	
	public IndexWriter getIndexWriter(String collectionName) {
		return indexWritersByCollectionName.get(collectionName);
	}
}
