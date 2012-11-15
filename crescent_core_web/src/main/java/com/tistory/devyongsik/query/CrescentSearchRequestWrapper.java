package com.tistory.devyongsik.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollectionField;
import com.tistory.devyongsik.domain.CrescentDefaultSearchField;
import com.tistory.devyongsik.domain.SearchRequest;

public class CrescentSearchRequestWrapper {
	private Logger logger = LoggerFactory.getLogger(CrescentSearchRequestWrapper.class);

	private SearchRequest searchRequest = null;

	//한 page에 보여 줄 결과건수
	private final int DEFAULT_HITS_FOR_PAGE = 20;
	//몇 페이지?
	private final int DEFAULT_PAGE_NUM = 1;
	
	//디폴트로 몇 페이지까지 검색 넘어갈 수 있도록?
	private final int DEFAULT_HITS_PAGE = 5;
	

	public CrescentSearchRequestWrapper(SearchRequest searchRequest) {
		this.searchRequest = searchRequest;	
	}

	public String getKeyword() {
		return searchRequest.getKeyword();
	}

	public String getCollectionName() {
		return searchRequest.getCollectionName();
	}

	public int getDefaultHitsPage() {
		return DEFAULT_HITS_PAGE;
	}
	
	public int getPageNum() {
		if(searchRequest.getPageNum() == null || "".equals(searchRequest.getPageNum())) {
			return DEFAULT_PAGE_NUM;
		}
		
		return Integer.parseInt(searchRequest.getPageNum());
	}
	
	public int getStartOffSet() {
		return ((getPageNum() - 1) * getHitsForPage());
	}

	public int getHitsForPage() {
		if(searchRequest.getPageSize() == null || "".equals(searchRequest.getPageSize())) {
			return DEFAULT_HITS_FOR_PAGE;
		}

		return Integer.parseInt(searchRequest.getPageSize());
	}

	public Sort getSort() {
		String sortQueryString = searchRequest.getSort();

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
				CrescentCollection collection = CrescentCollectionHandler.getInstance().getCrescentCollections().getCrescentCollection(searchRequest.getCollectionName());
				Map<String, CrescentCollectionField> collectionFields = collection.getCrescentFieldByName();

				CrescentCollectionField f = collectionFields.get(part);
					
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

	public List<CrescentCollectionField> getTargetSearchFields() {
		List<CrescentCollectionField> searchFields = new ArrayList<CrescentCollectionField>();
		CrescentCollection collection = CrescentCollectionHandler.getInstance().getCrescentCollections().getCrescentCollection(searchRequest.getCollectionName());
		
		Map<String, CrescentCollectionField> fieldMap = collection.getCrescentFieldByName();
		
		if(searchRequest.getSearchField() != null && !"".equals(searchRequest.getSearchField())) { 
			String[] requestSearchField = searchRequest.getSearchField().split(",");
			for(String fieldName : requestSearchField) {
				CrescentCollectionField field = fieldMap.get(fieldName);
				if(field == null) {
					throw new IllegalStateException("There is no Field in Collection [" + searchRequest.getCollectionName() + "] [" + fieldName + "]");
				}
				
				searchFields.add(fieldMap.get(fieldName));
			}
			
		} else {//검색 대상 필드가 지정되어 있지 않으면..
			for(CrescentDefaultSearchField f : collection.getDefaultSearchFields()) {
				searchFields.add(fieldMap.get(f.getName()));
			}
		}
		
		return searchFields;
	}
	
	public String getUserIp() {
		return searchRequest.getUserIp();
	}
	
	public String getUserId() {
		return searchRequest.getUserId();
	}
	
	public String getPcId() {
		return searchRequest.getPcId();
	}
}
