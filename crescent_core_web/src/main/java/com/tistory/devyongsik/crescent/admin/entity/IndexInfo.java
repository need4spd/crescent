package com.tistory.devyongsik.crescent.admin.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndexInfo {
    private int numOfDoc;
    private boolean hasDel;
    private long indexVersion;
    private String selectCollectionName;
    private String indexName;
    private int numOfField;
    private Map<String, Long> termCount;
    private long totalTermCount;

	private List<String> fieldNames = new ArrayList<String>();

    public long getTotalTermCount() {
		return totalTermCount;
	}

	public void setTotalTermCount(long totalTermCount) {
		this.totalTermCount = totalTermCount;
	}
	
    public int getNumOfDoc() {
        return numOfDoc;
    }

    public void setNumOfDoc(int numOfDoc) {
        this.numOfDoc = numOfDoc;
    }

    public boolean isHasDel() {
        return hasDel;
    }

    public void setHasDel(boolean hasDel) {
        this.hasDel = hasDel;
    }

    public long getIndexVersion() {
        return indexVersion;
    }

    public void setIndexVersion(long indexVersion) {
        this.indexVersion = indexVersion;
    }

    public String getSelectCollectionName() {
        return selectCollectionName;
    }

    public void setSelectCollectionName(String selectCollectionName) {
        this.selectCollectionName = selectCollectionName;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public int getNumOfField() {
        return numOfField;
    }

    public void setNumOfField(int numOfField) {
        this.numOfField = numOfField;
    }

    public Map<String, Long> getTermCount() {
        return termCount;
    }

    public void setTermCount(Map<String, Long> termCount) {
        this.termCount = termCount;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }
}
