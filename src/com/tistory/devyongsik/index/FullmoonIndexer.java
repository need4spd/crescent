package com.tistory.devyongsik.index;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.domain.Collection;
import com.tistory.devyongsik.handler.Handler;
import com.tistory.devyongsik.handler.JsonDataHandler;


/**
 * author : need4spd, need4spd@naver.com, 2012. 2. 26.
 */
public class FullmoonIndexer {
	private Logger logger = LoggerFactory.getLogger(FullmoonIndexer.class);
	
	private Collection collection = null;
	private String part = null;
	
	public FullmoonIndexer(String part, Collection collection) {
		this.collection = collection;
		this.part = part;
	}
	
	public void indexing() {
		try {
			
			String dataSourceType = collection.getDataSourceType();
			String dataSourceDir = collection.getDataSourceDirectory() + "/"+ part;
			String indexFileDir = collection.getIndexingDir() + "/" + part;
			
			logger.info("dataSourceType : {}", dataSourceType);
			logger.info("dataSourceDir : {}", dataSourceDir);
			logger.info("indexFileDir : {}", indexFileDir);
			
			logger.info("indexing start................");
			
			Directory dir = FSDirectory.open(new File(indexFileDir));
			
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_35, new KoreanAnalyzer());
			IndexWriter indexWriter = new IndexWriter(dir, conf);
			
			File file = new File(dataSourceDir);
			String[] files = file.list();
			
			logger.info("data files : {}", Arrays.toString(files));
			
			Handler handler = new JsonDataHandler();
			for (String fileName : files) {

				List<Document> documentList = handler.handledData(new File(dataSourceDir+"/"+fileName), collection.getFieldsByName());
				
				for(Document doc : documentList) {
					indexWriter.addDocument(doc);
				}
			}

			indexWriter.commit();
			indexWriter.close();
			
			logger.info("end");
			
		} catch (IOException e) {
			logger.error("error : ", e);
			throw new RuntimeException("색인 중 에러가 발생하였습니다. ["+e.getMessage()+"]");
		} finally {
			
		}
	}
}
