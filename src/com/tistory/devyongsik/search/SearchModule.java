package com.tistory.devyongsik.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.config.CollectionConfig;
import com.tistory.devyongsik.domain.Collection;
import com.tistory.devyongsik.domain.SearchResult;
import com.tistory.devyongsik.highlight.CrescentHighlighter;
import com.tistory.devyongsik.query.CrescentRequestQueryStrParser;

public class SearchModule {
	private Logger logger = LoggerFactory.getLogger(SearchModule.class);

	private CrescentDocSearcher cds;
	private CrescentRequestQueryStrParser crqsp;
	
	public SearchModule(CrescentRequestQueryStrParser crqsp) {
		
		this.crqsp = crqsp;
		
		if(crqsp.getSort() == null) {
			cds = new CrescentDefaultDocSearcher(crqsp);
		} else {
			cds = new CrescentSortDocSearcher(crqsp);
		}
	}
	
	public SearchResult search() {
		
		SearchResult searchResult = new SearchResult();
		CrescentHighlighter highlighter = new CrescentHighlighter(crqsp);
		
		try {
			
			ScoreDoc[] hits = cds.search();
			
			if(hits.length > 0) { 
			
				int startOffset = crqsp.getStartOffSet();
				int endOffset = startOffset + hits.length;
				
				logger.debug("start : [{}], end : [{}]", new Object[]{startOffset, endOffset});
				
				Collection collection = CollectionConfig.getInstance().getCollection(crqsp.getCollectionName());
				
				List<String> fieldList = collection.getFieldNames();
				String value = null;
				
				IndexSearcher indexSearcher 
					= SearcherManager.getSearcherManager().getIndexSearcher(crqsp.getCollectionName());
				
				List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
				Map<String, Object> result = new HashMap<String, Object>();
				
				for(int i = startOffset; i < hits.length; i++) {
					Document doc = indexSearcher.doc(hits[i].doc);
					Map<String,String> resultMap = new HashMap<String, String>();
					
					resultMap.put("docnum", Integer.toString(i));
					for(String fieldName : fieldList) {
						//필드별 결과를 가져온다.
						value = highlighter.getBestFragment(fieldName, doc.get(fieldName));
						resultMap.put(fieldName, value);
					}
					
					resultList.add(resultMap);
				}
				
				result.put("total_count", cds.getTotalHitsCount());
				result.put("result_list", resultList);
				result.put("error_code", "0");
				result.put("error_msg", "SUCCESS");
				
				logger.debug("result list {}", resultList);
				
				searchResult.setResultList(resultList);
				searchResult.setTotalHitsCount(cds.getTotalHitsCount());
				searchResult.setSearchResult(result);
				
			} else {
				
				//결과없음
				Map<String, Object> result = new HashMap<String, Object>();
				List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
				
				result.put("total_count", cds.getTotalHitsCount());
				result.put("result_list", resultList);
				
				result.put("error_code", "0");
				result.put("error_msg", "SUCCESS");
				
				logger.debug("result list {}", resultList);
				
				searchResult.setResultList(resultList);
				searchResult.setTotalHitsCount(0);
				searchResult.setSearchResult(result);
			
			}
		
			return searchResult;
			
		} catch (Exception e) {
			e.printStackTrace();
			
			Map<String, Object> result = new HashMap<String, Object>();
			List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
			
			result.put("total_count", cds.getTotalHitsCount());
			result.put("result_list", resultList);
			
			result.put("error_code", "0");
			result.put("error_msg", e.getMessage());
			
			logger.error("검색 중 에러 발생함." + e);
			
			searchResult.setErrorCode(-1);
			searchResult.setErrorMsg(e.getMessage());
			searchResult.setSearchResult(result);
			searchResult.setResultList(resultList);
			
			return searchResult;
		}
	}
}
