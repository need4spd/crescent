package com.tistory.devyongsik.config;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("collection")
public class NewCollection {
	
	private String analyzer;
	
	@XStreamAlias("name")
	@XStreamAsAttribute
	private String name;
	
	private String indexingDirectory;
	
	public String getIndexingDirectory() {
		return indexingDirectory;
	}
	public void setIndexingDirectory(String indexingDirectory) {
		this.indexingDirectory = indexingDirectory;
	}
	
	private List<NewCollectionField> fields;
	
	
	private List<NewDefaultSearchField> defaultSearchFields;
	private List<SortField> sortsField;
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
	public List<NewDefaultSearchField> getDefaultSearchFields() {
		return defaultSearchFields;
	}
	public List<NewCollectionField> getFields() {
		return fields;
	}
	public void setFields(List<NewCollectionField> fields) {
		this.fields = fields;
	}
	public void setDefaultSearchFields(
			List<NewDefaultSearchField> defaultSearchFields) {
		this.defaultSearchFields = defaultSearchFields;
	}
	public List<SortField> getSortsField() {
		return sortsField;
	}
	public void setSortsField(List<SortField> sortsField) {
		this.sortsField = sortsField;
	}
	
	
	
}
