package com.tistory.devyongsik.domain;

import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;

public class SearchResult {

	private int totalHitsCount = 0;
	private List<Map<String, String>> resultList = null;
	private int errorCode = 0;
	private String errorMsg = "";
	
	public int getTotalHitsCount() {
		return totalHitsCount;
	}
	public void setTotalHitsCount(int totalHitsCount) {
		this.totalHitsCount = totalHitsCount;
	}
	public List<Map<String, String>> getResultList() {
		return resultList;
	}
	public void setResultList(List<Map<String, String>> resultList) {
		this.resultList = resultList;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
