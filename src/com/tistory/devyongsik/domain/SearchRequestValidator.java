package com.tistory.devyongsik.domain;

import java.util.Map;

import com.tistory.devyongsik.config.CollectionConfig;
import com.tistory.devyongsik.exception.CrescentUnvalidRequestException;

public class SearchRequestValidator {
	
	public boolean isValid(SearchRequest searchRequest) throws CrescentUnvalidRequestException {
		
		//collection name
		Collection collection = CollectionConfig.getInstance().getCollection(searchRequest.getCollectionName());
		if(collection == null) {
			throw new CrescentUnvalidRequestException("Wrong Collection Name : " + searchRequest.getCollectionName());
		}
		
		
		//request search field
		String[] requestSearchFieldNames = searchRequest.getSearchField().split(",");
		
		if(requestSearchFieldNames != null && requestSearchFieldNames[0].length() > 0) {
			for(String requestFieldName : requestSearchFieldNames) {
				if(!collection.getFieldsByName().containsKey(requestFieldName)) {
					throw new CrescentUnvalidRequestException("Wrong Search Field Name : " + searchRequest.getSearchField());
				}
			}
		}
		
		//request sort field
		String sortQueryString = searchRequest.getSort();
		if(sortQueryString == null || "".equals(sortQueryString) || "null".equals(sortQueryString)) {
			//nothing
		} else {
	
			String[] parts = sortQueryString.split(",");
			if(parts.length == 0) {
				throw new CrescentUnvalidRequestException("Wrong Sort Field Name : " + sortQueryString);
			}
	
			for(int i = 0; i < parts.length; i++) {
				String part = parts[i].trim(); //part = field desc
				
				int idx = part.indexOf( ' ' );
				
				if(idx <= 0) {
					throw new CrescentUnvalidRequestException("No Order Condition (DESC/ASC) : " + sortQueryString);
				}
				
				part = part.substring( 0, idx ).trim(); //part = field
				Map<String, CollectionField> collectionFields = collection.getFieldsByName();
				
				CollectionField f = collectionFields.get(part);
				
				if(f == null) throw new CrescentUnvalidRequestException("요청된 필드가 존재하지 않습니다. ["+part+"]");
				if(f.isAnalyze()) throw new CrescentUnvalidRequestException("Analyze 된 필드는 Sort가 불가능합니다. ["+part+"]");
			}
		}
		
		return true;
	}
}
