package com.tistory.devyongsik.handler;

import java.util.List;
import java.util.Map;

public class IndexingRequestForm {
	
	private String command;
	private List<Map<String, String>> documentList;
	private String query;
	private String indexingType;
	
	public String getIndexingType() {
		return indexingType;
	}
	public void setIndexingType(String indexingType) {
		this.indexingType = indexingType;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public List<Map<String, String>> getDocumentList() {
		return documentList;
	}
	public void setDocumentList(List<Map<String, String>> documentList) {
		this.documentList = documentList;
	}
	
	@Override
	public String toString() {
		return "IndexingRequestForm [command=" + command + ", documentList size ="
				+ documentList.size() + ", query=" + query + "]";
	}
}
