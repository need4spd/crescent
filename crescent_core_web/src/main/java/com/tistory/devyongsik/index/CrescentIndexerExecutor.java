package com.tistory.devyongsik.index;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.handler.IndexingRequestForm;

public class CrescentIndexerExecutor {
	private Logger logger = LoggerFactory.getLogger(CrescentIndexerExecutor.class);
	private CrescentCollection collection = null;
	private IndexingRequestForm indexingRequestForm = null;
	
	public CrescentIndexerExecutor(CrescentCollection collection, IndexingRequestForm indexingRequestForm) {
		this.collection = collection;
		this.indexingRequestForm = indexingRequestForm;
	}

	public String indexing() {
		logger.info("indexingRequestForm : {}", indexingRequestForm);
		
		IndexingType indexingType = IndexingType.valueOf(indexingRequestForm.getIndexingType().toUpperCase());
		IndexingCommand indexingCommand = IndexingCommand.valueOf(indexingRequestForm.getCommand().toUpperCase());
		String query = indexingRequestForm.getQuery();
		
		logger.info("Indexing type : {} , Indexing command : {} ", indexingType, indexingCommand);
		logger.info("Query : {}", query);
		
		String resultMessage = "Nothing To Execute...";
		
		CrescentIndexer crescentIndexer = new CrescentIndexer(collection.getName());
		
		if(IndexingCommand.ADD == indexingCommand) {
			List<Document> documentList = LuceneDocumentBuilder.buildDocumentList(indexingRequestForm.getDocumentList(), collection.getCrescentFieldByName());
			crescentIndexer.addDocument(documentList);
		
			resultMessage = documentList.size() + "건의 색인이 완료되었습니다.";
			
		} else if (IndexingCommand.UPDATE == indexingCommand) {
			
			List<Document> documentList = LuceneDocumentBuilder.buildDocumentList(indexingRequestForm.getDocumentList(), collection.getCrescentFieldByName());
			if(documentList.size() > 1) {
				logger.error("Update에는 업데이트 할 document가 한개 이상일 수 없습니다.");
				throw new IllegalStateException("Update에는 업데이트 할 document가 한개 이상일 수 없습니다.");
			}
			
			if(documentList.size() == 0) {
				logger.error("Update에는 업데이트 할 document가 없습니다.");
				throw new IllegalStateException("Update에는 업데이트 할 document가 없습니다.");
			}
			
			Document updateDoc = documentList.get(0);
			
			String[] splitQuery = query.split(":");
			if(splitQuery.length != 2) {
				logger.error("Update 대상 문서를 찾을 Query식이 잘못되었습니다. [{}]", query);
				throw new IllegalStateException("Update 대상 문서를 찾을 Query식이 잘못되었습니다. ["+query+"]");
			}
			String field = query.split(":")[0];
			String value = query.split(":")[1];
			
			logger.info("field : {}, value : {}", field, value);
			
			Term updateTerm = new Term(field, value);
			
			crescentIndexer.updateDocument(updateTerm, updateDoc);
			
			resultMessage = updateTerm.toString() + "에 대한 update가 완료되었습니다.";
			
		} else if (IndexingCommand.DELETE == indexingCommand) {
			
			String[] splitQuery = query.split(":");
			if(splitQuery.length != 2) {
				logger.error("Delete 대상 문서를 찾을 Query식이 잘못되었습니다. [{}]", query);
				throw new IllegalStateException("Delete 대상 문서를 찾을 Query식이 잘못되었습니다. ["+query+"]");
			}
			String field = query.split(":")[0];
			String value = query.split(":")[1];
			
			logger.info("field : {}, value : {}", field, value);
			
			Term deleteTerm = new Term(field, value);
			Query deleteTermQuery = new TermQuery(deleteTerm);
			
			crescentIndexer.deleteDocument(deleteTermQuery);
			
			resultMessage = deleteTerm.toString() + "에 대한 delete가 완료되었습니다.";
		}
		
		if(IndexingType.BULK == indexingType) {
			crescentIndexer.commit();
		}
		
		return resultMessage;
	}
}
