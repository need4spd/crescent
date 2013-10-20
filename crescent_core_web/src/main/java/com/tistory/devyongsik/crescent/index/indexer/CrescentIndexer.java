package com.tistory.devyongsik.crescent.index.indexer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.tistory.devyongsik.crescent.IndexWriterManager;


/**
 * author : need4spd, need4spd@naver.com, 2012. 2. 26.
 */
@Component("crescentIndexer")
public class CrescentIndexer {
	private Logger logger = LoggerFactory.getLogger(CrescentIndexer.class);
	
	@Autowired
	@Qualifier("indexWriterManager")
	private IndexWriterManager indexWriterManager;
	
	public void addDocument(List<Document> documentList, String collectionName) {
		
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);
		
		try {
			
			logger.info("collectionName : {}", collectionName);			
			logger.info("add indexing start................");
			
			int indexingDocumentCount = 0;
			for(Document doc : documentList) {
				indexingDocumentCount++;
				if((indexingDocumentCount%50000) == 0) {
					logger.info("{} indexed...", indexingDocumentCount);
				}
				
				indexWriter.addDocument(doc);
			}
			
			logger.info("total indexed document count {}", indexingDocumentCount);
					
			logger.info("end");
			
		} catch (IOException e) {
			
			logger.error("error : ", e);
			throw new RuntimeException("색인 중 에러가 발생하였습니다. ["+e.getMessage()+"]");
			
		}
	}
	
	public void updateDocuments(Term term, List<Document> documents, String collectionName) {
		
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);
		
		try {
			
			logger.info("collectionName : {}", collectionName);			
			logger.info("update indexing start................{}, size : {}", term, documents.size());
			
			indexWriter.updateDocuments(term, documents);
					
			logger.info("end");
			
		} catch (IOException e) {
			
			logger.error("error : ", e);
			throw new RuntimeException("색인 중 에러가 발생하였습니다. ["+e.getMessage()+"]");
			
		}
	}
	
	public void updateDocument(Term term, Document document, String collectionName) {
		
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);
		
		try {
			
			logger.info("collectionName : {}", collectionName);			
			logger.info("update indexing start................{}", term);
			
			indexWriter.updateDocument(term, document);
					
			logger.info("end");
			
		} catch (IOException e) {
			
			logger.error("error : ", e);
			throw new RuntimeException("색인 중 에러가 발생하였습니다. ["+e.getMessage()+"]");
			
		}
	}
	
	public void deleteDocument(Query query, String collectionName) {
		
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);
		
		try {
			
			logger.info("collectionName : {}", collectionName);			
			logger.info("delete indexing start................ {}", query);
			
			indexWriter.deleteDocuments(query);
					
			logger.info("end");
			
		} catch (IOException e) {
			
			logger.error("error : ", e);
			throw new RuntimeException("색인 중 에러가 발생하였습니다. ["+e.getMessage()+"]");
			
		}
	}
	
	public void commit(String collectionName) {
		
		IndexWriter indexWriter = indexWriterManager.getIndexWriter(collectionName);
		
		try {
			
			logger.info("Commit {}", collectionName);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Map<String, String> indexUserData = new HashMap<String, String>();
			indexUserData.put("lastModified", dateFormat.format(new Date()));
			indexWriter.setCommitData(indexUserData);
			
			indexWriter.commit();
		
		} catch (CorruptIndexException e) {
			
			logger.error("error : ", e);
			throw new RuntimeException("색인 중 에러가 발생하였습니다. ["+e.getMessage()+"]");
			
		} catch (IOException e) {
			
			logger.error("error : ", e);
			throw new RuntimeException("색인 중 에러가 발생하였습니다. ["+e.getMessage()+"]");
		
		}
	}
}
