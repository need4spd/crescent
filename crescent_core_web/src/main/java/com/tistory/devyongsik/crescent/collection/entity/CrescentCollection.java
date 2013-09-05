package com.tistory.devyongsik.crescent.collection.entity;

import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("collection")
public class CrescentCollection {
	
	@XStreamAsAttribute
	private String name;
	
	private String indexingDirectory;
	
	private String searcherReloadScheduleMin;
	
	@XStreamOmitField
	private Map<String, CrescentCollectionField> crescentFieldByName;
	
	private List<CrescentCollectionField> fields;
	private List<CrescentDefaultSearchField> defaultSearchFields;
	private List<CrescentSortField> sortFields;
	
	private List<CrescentAnalyzerHolder> analyzers;
	
	@XStreamOmitField
	private Analyzer indexingModeAnalyzer;
	
	@XStreamOmitField
	private Analyzer searchModeAnalyzer;
	
	public Analyzer getIndexingModeAnalyzer() {
		return indexingModeAnalyzer;
	}
	
	public Analyzer getSearchModeAnalyzer() {
		return searchModeAnalyzer;
	}
	
	public void setIndexingModeAnalyzer(Analyzer analyzer) {
		this.indexingModeAnalyzer = analyzer;
	}
	
	public void setSearchModeAnalyzer(Analyzer analyzer) {
		this.searchModeAnalyzer = analyzer;
	}
	
	
	public List<CrescentAnalyzerHolder> getAnalyzers() {
		return analyzers;
	}
	public void setAnalyzers(List<CrescentAnalyzerHolder> analyzers) {
		this.analyzers = analyzers;
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
		return "CrescentCollection [name=" + name + ", indexingDirectory="
				+ indexingDirectory + ", searcherReloadScheduleMin="
				+ searcherReloadScheduleMin + ", crescentFieldByName="
				+ crescentFieldByName + ", fields=" + fields
				+ ", defaultSearchFields=" + defaultSearchFields
				+ ", sortFields=" + sortFields + ", analyzers=" + analyzers
				+ "]";
	}
}
