package com.tistory.devyongsik.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author : need4spd, need4spd@naver.com, 2012. 2. 26.
 */

public class Collection {
	private String analyzerName;
	private String collectionName;
	private String indexingDir;
	private Map<String, CollectionField> fieldsByName = new ConcurrentHashMap<String, CollectionField>();
	private List<String> defaultSearchFieldNames = new ArrayList<String>();
	private List<String> sortFieldNames = new ArrayList<String>();
	private List<String> fieldNames = new ArrayList<String>();
	
	
	public List<String> getFieldNames() {
		return fieldNames;
	}

	public void addFieldName(String fieldName) {
		this.fieldNames.add(fieldName);
	}

	public String getAnalyzerName() {
		return analyzerName;
	}

	public void setAnalyzerName(String analyzerName) {
		this.analyzerName = analyzerName;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getIndexingDir() {
		return indexingDir;
	}
	
	public void setIndexingDir(String indexingDir) {
		this.indexingDir = indexingDir;
	}
	
	public Map<String, CollectionField> getFieldsByName() {
		return fieldsByName;
	}

	public void putField(String fieldName, CollectionField field) {
		this.fieldsByName.put(fieldName, field);
	}

	
	public List<String> getDefaultSearchFieldNames() {
		return defaultSearchFieldNames;
	}

	public void addDefaultSearchFieldNames(String name) {
		this.defaultSearchFieldNames.add(name);
	}

	
	public List<String> getSortFieldNames() {
		return sortFieldNames;
	}

	public void addSortFieldNames(String name) {
		this.sortFieldNames.add(name);
	}

	public void setSortFieldNames(List<String> sortFieldNames) {
		this.sortFieldNames = sortFieldNames;
	}

	@Override
	public String toString() {
		return "Collection [analyzerName=" + analyzerName + "\n, collectionName="
				+ collectionName + "\n, indexingDir=" + indexingDir
				+ "\n, fieldsByName=" + fieldsByName
				+ "\n, defaultSearchFieldNames=" + defaultSearchFieldNames
				+ "\n, sortFieldNames=" + sortFieldNames + "]";
	}	
}
