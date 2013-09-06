package com.tistory.devyongsik.crescent.admin.entity;

import java.util.ArrayList;
import java.util.List;

public class IndexInfo {
    private int numOfDoc;
    private boolean hasDel;
    private long indexVersion;
    private String selectCollectionName;
    private String indexName;
    private int numOfField;
    private long termCount;
    private List<String> fieldNames = new ArrayList<String>();

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

    public long getTermCount() {
        return termCount;
    }

    public void setTermCount(long termCount) {
        this.termCount = termCount;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }
}
