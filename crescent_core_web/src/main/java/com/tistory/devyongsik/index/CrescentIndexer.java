package com.tistory.devyongsik.index;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * author : need4spd, need4spd@naver.com, 2012. 2. 26.
 */
public class CrescentIndexer {
	private Logger logger = LoggerFactory.getLogger(CrescentIndexer.class);
	private String collectionName = null;
	private IndexWriter indexWriter = null;
	
	public CrescentIndexer(String collectionName) {
		this.collectionName = collectionName;
		IndexWriterManager indexWriterManager = IndexWriterManager.getIndexWriterManager();
		indexWriter = indexWriterManager.getIndexWriter(collectionName);
	}
	
	public void addDocument(List<Document> documentList) {
		try {
			
			logger.info("collectionName : {}", collectionName);			
			logger.info("add indexing start................");
			
			for(Document doc : documentList) {
				indexWriter.addDocument(doc);
			}
					
			logger.info("end");
			
		} catch (IOException e) {
			
			logger.error("error : ", e);
			throw new RuntimeException("색인 중 에러가 발생하였습니다. ["+e.getMessage()+"]");
			
		}
	}
	
	public void updateDocument(Term term, Document document) {
		try {
			
			logger.info("collectionName : {}", collectionName);			
			logger.info("update indexing start................{}, {}", term, document);
			
			indexWriter.updateDocument(term, document);
					
			logger.info("end");
			
		} catch (IOException e) {
			
			logger.error("error : ", e);
			throw new RuntimeException("색인 중 에러가 발생하였습니다. ["+e.getMessage()+"]");
			
		}
	}
	
	public void deleteDocument(Query query) {
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
	
	public void commit() {
		try {
			
			logger.info("Commit {}", collectionName);
			
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
