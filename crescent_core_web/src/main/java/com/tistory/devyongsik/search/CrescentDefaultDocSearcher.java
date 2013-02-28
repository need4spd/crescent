package com.tistory.devyongsik.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.config.SpringApplicationContext;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollectionField;
import com.tistory.devyongsik.domain.SearchResult;
import com.tistory.devyongsik.highlight.CrescentFastVectorHighlighter;
import com.tistory.devyongsik.logger.CrescentLogger;
import com.tistory.devyongsik.logger.LogInfo;
import com.tistory.devyongsik.query.CrescentSearchRequestWrapper;

public class CrescentDefaultDocSearcher implements CrescentDocSearcher {

	private Logger logger = LoggerFactory.getLogger(CrescentDefaultDocSearcher.class);
	
	@Override
	public SearchResult search(CrescentSearchRequestWrapper csrw) throws IOException {
		
		SearchResult searchResult = new SearchResult();
		int totalHitsCount = 0;
		String errorMessage = "SUCCESS";
		int errorCode = 0;
		
		//5page * 50
		int numOfHits = csrw.getDefaultHitsPage() * csrw.getHitsForPage();
		IndexSearcher indexSearcher = null;
		SearcherManager searcherManager = CrescentSearcherManager.getCrescentSearcherManager().getSearcherManager(csrw.getCollectionName());
		
		try {
			indexSearcher = searcherManager.acquire();
			
			Query query = csrw.getQuery();
			Filter filter = csrw.getFilter();
			Sort sort = csrw.getSort();
			
			logger.debug("query : {}" , query);
			logger.debug("filter : {}" , filter);
			logger.debug("sort : {}" , sort);
			
			long startTime = System.currentTimeMillis();
			TopDocs topDocs = null;
			
			if(sort == null) {
				topDocs = indexSearcher.search(query, filter, numOfHits);
			} else {
				topDocs = indexSearcher.search(query, filter, numOfHits, sort);
			}
			
			long endTime = System.currentTimeMillis();
			
			//전체 검색 건수
			totalHitsCount = topDocs.totalHits;
			
			LogInfo logInfo = new LogInfo();
			logInfo.setCollectionName(csrw.getCollectionName());
			logInfo.setElaspedTimeMil(endTime - startTime);
			logInfo.setKeyword(csrw.getKeyword());
			logInfo.setPageNum(csrw.getPageNum());
			logInfo.setPcid(csrw.getPcId());
			logInfo.setQuery(query);
			logInfo.setSort(csrw.getSort());
			logInfo.setTotalCount(totalHitsCount);
			logInfo.setUserId(csrw.getUserId());
			logInfo.setUserIp(csrw.getUserIp());
			logInfo.setFilter(csrw.getFilter());
			
			CrescentLogger.logging(logInfo);
			
			
			logger.debug("Total Hits Count : {} ", totalHitsCount);
			
			ScoreDoc[] hits = topDocs.scoreDocs;
			
			//총 검색건수와 실제 보여줄 document의 offset (min ~ max)를 비교해서 작은 것을 가져옴
			int endOffset = Math.min(totalHitsCount, csrw.getStartOffSet() + csrw.getHitsForPage());
			
			if(endOffset > hits.length) {
				logger.debug("기본 설정된 검색건수보다 더 검색을 원하므로, 전체를 대상으로 검색합니다.");
				
				if(sort == null) {
					topDocs = indexSearcher.search(query, filter, totalHitsCount);
				} else {
					topDocs = indexSearcher.search(query, filter, totalHitsCount, sort);
				}
				
		        hits = topDocs.scoreDocs;
			}
	
			int startOffset = csrw.getStartOffSet();
			endOffset = Math.min(hits.length, startOffset + csrw.getHitsForPage());
									
			//for(int i = startOffset; i < endOffset; i++) {
			//	Document doc = indexSearcher.doc(hits[i].doc);
			//	resultDocumentList.add(doc);
			//}
			
			logger.debug("start offset : [{}], end offset : [{}], total : [{}], numOfHits :[{}]"
							,new Object[]{csrw.getStartOffSet(), endOffset, totalHitsCount, numOfHits});
			logger.debug("hits count : [{}]", hits.length);
			logger.debug("startOffset + hitsPerPage : [{}]", csrw.getStartOffSet() + csrw.getHitsForPage());
			
			
			if(totalHitsCount > 0) { 
				List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
				Map<String, Object> result = new HashMap<String, Object>();
				
				logger.debug("make highlight... start");
				
				CrescentFastVectorHighlighter highlighter = new CrescentFastVectorHighlighter();
				
				CrescentCollectionHandler collectionHandler 
				= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
				CrescentCollection collection = collectionHandler.getCrescentCollections().getCrescentCollection(csrw.getCollectionName());
				
				//int docnum = 0;
				for(int i = startOffset; i < endOffset; i++) {
					logger.debug("make highlight...{}", hits[i].doc);
					
					Map<String,String> resultMap = new HashMap<String, String>();
					
					for(CrescentCollectionField field : collection.getFields()) {
						String value = null;
						
						logger.debug("field... name : {}, isStore :{}, isNumeric :{}", new Object[]{field.getName(), field.isStore(), field.isNumeric()});
						
						if(field.isStore() && !field.isNumeric()) {
							
							//필드별 결과를 가져온다.
							value = highlighter.getBestFragment(indexSearcher.getIndexReader(), hits[i].doc, csrw.getQuery(), field.getName());
						
							logger.debug("highlighted value :{}" , value);
							
						}
						
						if(value == null || value.length() == 0) {
							Document doc = indexSearcher.doc(hits[i].doc);
							value = doc.get(field.getName());
							
							logger.debug("not mark highlight value :{}" , value);
							
						}
						
						logger.debug("final value : {}, size : {}", value, value.length());
						
						resultMap.put(field.getName(), value);
					}
					
					resultList.add(resultMap);
				}
				
				result.put("total_count", totalHitsCount);
				result.put("result_list", resultList);
				result.put("error_code", errorCode);
				result.put("error_msg", errorMessage);
				
				logger.debug("result list {}", resultList);
				
				searchResult.setResultList(resultList);
				searchResult.setTotalHitsCount(totalHitsCount);
				searchResult.setSearchResult(result);
				
			} else {
				
				//결과없음
				Map<String, Object> result = new HashMap<String, Object>();
				List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
				
				result.put("total_count", totalHitsCount);
				result.put("result_list", resultList);
				result.put("error_code", errorCode);
				result.put("error_msg", errorMessage);
				
				
				logger.debug("result list {}", resultList);
				
				searchResult.setResultList(resultList);
				searchResult.setTotalHitsCount(0);
				searchResult.setSearchResult(result);
			
			}
			
			
		} catch (Exception e) {
			
			logger.error("error in CrescentDefaultDocSearcher : ", e);
			
			Map<String, Object> result = new HashMap<String, Object>();
			List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
			
			result.put("total_count", totalHitsCount);
			result.put("result_list", resultList);
			result.put("error_code", errorCode);
			result.put("error_msg", errorMessage);
			
			logger.error("검색 중 에러 발생함. {}", e);
			
			searchResult.setErrorCode(errorCode);
			searchResult.setErrorMsg(errorMessage);
			searchResult.setSearchResult(result);
			searchResult.setResultList(resultList);
			
			return searchResult;
			
			
		} finally {
			searcherManager.release(indexSearcher);
			indexSearcher = null;
		}
		
		return searchResult;
	}
}
