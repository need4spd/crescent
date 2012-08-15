package com.tistory.devyongsik.query;

import java.util.Map;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.config.CollectionConfig;
import com.tistory.devyongsik.domain.Collection;
import com.tistory.devyongsik.domain.CollectionField;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 4.
 */
public class QueryParser {
	private Logger logger = LoggerFactory.getLogger(QueryParser.class);
	
	private Map<String,String> paramMap = null;
	
	//한 page에 보여 줄 결과건수
	private final int DEFAULT_HITS_FOR_PAGE = 20;
	//몇 페이지?
	private final int DEFAULT_PAGE = 1;
	//디폴트로 몇 페이지까지 검색 넘어갈 수 있도록?
	private final int DEFAULT_HITS_PAGE = 5;
	
	private String collectionName;

	public QueryParser(Map<String,String> paramMap) {
		this.paramMap = paramMap;	
		this.collectionName = paramMap.get("col_name");
	}
	
	public int getStartOffset() {
		return ((getPage() - 1) * getHitsForPage());
	}
	
	public String getKeyword() {
		return paramMap.get("keyword");
	}
	
	public String getTargetVolume() {
		return paramMap.get("index");
	}
	
	public int getDefaultHitsPage() {
		return DEFAULT_HITS_PAGE;
	}
	
	public int getPage() {
		if(paramMap.containsKey("page")) {
			if(paramMap.get("page") != null) return Integer.parseInt(paramMap.get("page"));
			else return DEFAULT_PAGE;
		} else {
			return DEFAULT_PAGE;
		}
	}
	
	public int getHitsForPage() {
		if(paramMap.containsKey("row")) {
			if(paramMap.get("row") != null) return Integer.parseInt(paramMap.get("row"));
			else return DEFAULT_HITS_FOR_PAGE;
		} else {
			return DEFAULT_HITS_FOR_PAGE;
		}
	}
	
	public Sort getSort() {
		String sortQueryString = paramMap.get("sort");
		logger.debug("소트 파라미터 : {}", sortQueryString);
			
		
		if(sortQueryString == null || "".equals(sortQueryString) || "null".equals(sortQueryString)) return null;
		
		String[] parts = sortQueryString.split(",");
		if(parts.length == 0) return null;
		
		SortField[] lst = new SortField[parts.length];
		
		for(int i = 0; i < parts.length; i++) {
			String part = parts[i].trim(); //part = field desc
			
			logger.debug("part : {}", part);
			
			boolean descending = true;
			
			int idx = part.indexOf( ' ' );
			if(idx > 0) {
				String order = part.substring( idx+1 ).trim();
				
				if("desc".equals(order)) {
					descending = true;
				} else if("asc".equals(order)) {
					descending = false;
				} else {
					throw new IllegalStateException("알 수 없는 조건입니다. : " + order);
				}
				
				part = part.substring( 0, idx ).trim(); //part = field
				
			} else {
				throw new IllegalStateException("Order 조건이 없습니다.");
			}
			
			if(logger.isDebugEnabled())
				logger.debug("part order 제거 후: " + part);
			
			if("score".equals(part)) {
				if(descending) {
					if(parts.length == 1) {
						return null;
					}
					lst[i] = SortField.FIELD_SCORE;
				} else {
					lst[i] = new SortField(null,SortField.SCORE, true);
				}
			} else {
				Collection collection = CollectionConfig.getInstance().getCollection(collectionName);
				Map<String, CollectionField> collectionFields = collection.getFieldsByName();
				
				CollectionField f = collectionFields.get(part);
				
				if(f.isAnalyze()) throw new IllegalStateException("Analyze 된 필드는 Sort가 불가능합니다. ["+part+"]");
				lst[i] = new SortField(f.getName(),f.getSortFieldType(),descending);
			}
		}//end for
		
		if(logger.isDebugEnabled()) {
			for(int i=0; i < lst.length; i++) {
				logger.debug(lst[i].getField());
			}
		}
		return new Sort(lst);
	}
	
	public String[] getSearchFieldNames() {
		String fieldNames[] = null;
		if(fieldHasNotNull("field")) { 
			fieldNames = paramMap.get("field").split(",");
		} else {//검색 대상 필드가 지정되어 있지 않으면..
			Collection collection = CollectionConfig.getInstance().getCollection(collectionName);
			fieldNames = collection.getDefaultSearchFieldNames().toArray(new String[0]);
		}
		
		return fieldNames;
	}
	
	private boolean fieldHasNotNull(String key) {
		if(paramMap.containsKey(key)) {
			String tmp = paramMap.get(key);
			if(tmp != null && !"".equals(tmp)) return true;
		}
		
		return false;
	}
}
