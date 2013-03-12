package com.tistory.devyongsik.index;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.domain.CrescentCollection;

@Component("indexWriterManager")
@DependsOn("crescentCollectionHandler")
public class IndexWriterManager {

	//private static IndexWriterManager indexWriterManager = new IndexWriterManager();
	private Map<String, IndexWriter> indexWritersByCollectionName = new ConcurrentHashMap<String, IndexWriter>();
	private Logger logger = LoggerFactory.getLogger(IndexWriterManager.class);
	
	@Autowired
	@Qualifier("crescentCollectionHandler")
	private CrescentCollectionHandler collectionHandler;
	
//	private IndexWriterManager() {
//		try {
//			initIndexWriter();
//		} catch (IOException e) {
//			logger.error("IndexWriter 생성 실패", e);
//			throw new IllegalStateException("IndexWriter 생성 실패");
//		}
//	}
	
//	public static IndexWriterManager getIndexWriterManager() {
//		return indexWriterManager;
//	}
	
	@SuppressWarnings("resource")
	@PostConstruct
	private void initIndexWriter() {
		
		for(CrescentCollection crescentCollection : collectionHandler.getCrescentCollections().getCrescentCollections()) {
			
			logger.info("collection name {}", crescentCollection.getName());
			
			String indexDir = crescentCollection.getIndexingDirectory();
			
			logger.info("index file dir ; {}", indexDir);
			
			Directory dir = null;
			
			try {
				if(indexDir.equals("memory")) {
					dir = new RAMDirectory();
				} else {
					dir = FSDirectory.open(new File(indexDir));
				}
				
				IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_35, new KoreanAnalyzer(true));
				conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
				//conf.setIndexDeletionPolicy(new LastCommitDeletePolicy());
				
				IndexWriter indexWriter = new IndexWriter(dir, conf);
				indexWritersByCollectionName.put(crescentCollection.getName(), indexWriter);
				
				logger.info("index writer for collection {} is initialized...", crescentCollection.getName());
				
			}catch(Exception e) {
				logger.error("index writer init error", e);
				throw new RuntimeException("index writer init error ", e);
			}
		}
	}
	
	public IndexWriter getIndexWriter(String collectionName) {
		return indexWritersByCollectionName.get(collectionName);
	}
}
