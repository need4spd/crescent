package com.tistory.devyongsik.index;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.NRTManager.TrackingIndexWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * author : need4spd, need4spd@naver.com, 2012. 2. 26.
 */
public class CrescentIndexer {
	private Logger logger = LoggerFactory.getLogger(CrescentIndexer.class);
	
//	private Collection collection = null;
//	private String part = null;
//	
//	public FullmoonIndexer(String part, Collection collection) {
//		this.collection = collection;
//		this.part = part;
//	}
	
	private String collectionName = null;
	
	public CrescentIndexer(String collectionName) {
		this.collectionName = collectionName;
	}
	
	public void indexing(List<Document> documentList) {
		try {
			
			//String dataSourceType = collection.getDataSourceType();
			//String dataSourceDir = collection.getDataSourceDirectory() + "/"+ part;
			//String indexFileDir = collection.getIndexingDir() + "/" + part;
			
			//logger.info("dataSourceType : {}", dataSourceType);
			//logger.info("dataSourceDir : {}", dataSourceDir);
			logger.info("collectionName : {}", collectionName);
			
			logger.info("indexing start................");
			
			IndexWriterManager indexWriterManager = IndexWriterManager.getIndexWriterManager();
			TrackingIndexWriter trackingIndexWriter = indexWriterManager.getTrackingIndexWriterBy(collectionName);
			//File file = new File(dataSourceDir);
			//String[] files = file.list();
			
			//logger.info("data files : {}", Arrays.toString(files));
			
			for(Document doc : documentList) {
				trackingIndexWriter.addDocument(doc);
			}
			
//			Handler handler = new JsonDataHandler();
//			for (String fileName : files) {
//
//				List<Document> documentList = handler.handledData(new File(dataSourceDir+"/"+fileName), collection.getFieldsByName());
//				
//				
//			}
			
			trackingIndexWriter.getIndexWriter().commit();
			//indexWriter.close();
			
			logger.info("end");
			
		} catch (IOException e) {
			logger.error("error : ", e);
			throw new RuntimeException("색인 중 에러가 발생하였습니다. ["+e.getMessage()+"]");
		} finally {
			
		}
	}
}
