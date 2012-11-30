package com.tistory.devyongsik.domain;

import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("collection")
public class CrescentCollection {
	private String analyzer;
	
	@XStreamAsAttribute
	private String name;
	
	private String indexingDirectory;
	
	private String searcherReloadScheduleMin;
	
	@XStreamOmitField
	private Map<String, CrescentCollectionField> crescentFieldByName;
	
	private List<CrescentCollectionField> fields;
	private List<CrescentDefaultSearchField> defaultSearchFields;
	private List<CrescentSortField> sortFields;

	public String getAnalyzer() {
		return analyzer;
	}
	public void setAnalyzer(String analyzer) {
		this.analyzer = analyzer;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<CrescentCollectionField> getFields() {
		return fields;
	}
	public void setFields(List<CrescentCollectionField> fields) {
		this.fields = fields;
	}
	public List<CrescentDefaultSearchField> getDefaultSearchFields() {
		return defaultSearchFields;
	}
	public void setDefaultSearchFields(
			List<CrescentDefaultSearchField> defaultSearchFields) {
		this.defaultSearchFields = defaultSearchFields;
	}
	public List<CrescentSortField> getSortFields() {
		return sortFields;
	}
	public void setSortFields(List<CrescentSortField> sortFields) {
		this.sortFields = sortFields;
	}
	
	public String getIndexingDirectory() {
		return indexingDirectory;
	}
	public void setIndexingDirectory(String indexingDirectory) {
		this.indexingDirectory = indexingDirectory;
	}
	
	public Map<String, CrescentCollectionField> getCrescentFieldByName() {
		return crescentFieldByName;
	}
	public void setCrescentFieldByName(
			Map<String, CrescentCollectionField> crescentFieldByName) {
		this.crescentFieldByName = crescentFieldByName;
	}
	public String getSearcherReloadScheduleMin() {
		return searcherReloadScheduleMin;
	}
	public void setSearcherReloadScheduleMin(String searcherReloadScheduleMin) {
		this.searcherReloadScheduleMin = searcherReloadScheduleMin;
	}
	@Override
	public String toString() {
		return "CrescentCollection [analyzer=" + analyzer + ", name=" + name
				+ ", indexingDirectory=" + indexingDirectory
				+ ", searcherReloadScheduleMin=" + searcherReloadScheduleMin
				+ ", crescentFieldByName=" + crescentFieldByName + ", fields="
				+ fields + ", defaultSearchFields=" + defaultSearchFields
				+ ", sortFields=" + sortFields + "]";
	}
}
