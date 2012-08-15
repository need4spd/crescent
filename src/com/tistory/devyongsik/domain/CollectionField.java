package com.tistory.devyongsik.domain;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.SortField;

/**
 * author : need4spd, need4spd@naver.com, 2012. 2. 26.
 */

public class CollectionField implements Cloneable {
	private String name;
	private boolean isStore;
	private boolean isIndexing;
	private String type;
	private boolean isAnalyze;
	private float fieldBoost = 0F;
	private boolean isMust;
	private boolean hasTermPosition;
	private boolean hasTermOffset;
	private boolean hasTermVector;	

	public boolean hasTermVector() {
		return hasTermVector;
	}
	public void setHasTermVector(boolean hasTermVector) {
		this.hasTermVector = hasTermVector;
	}
	
	public boolean hasTermPosition() {
		return hasTermPosition;
	}
	public void setHasTermPosition(boolean hasTermPosition) {
		this.hasTermPosition = hasTermPosition;
	}
	public boolean hasTermOffset() {
		return hasTermOffset;
	}
	public void setHasTermOffset(boolean hasTermOffset) {
		this.hasTermOffset = hasTermOffset;
	}
	public boolean isMust() {
		return isMust;
	}
	public void setisMust(boolean isMust) {
		this.isMust = isMust;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isStore() {
		return isStore;
	}
	public void setisStore(boolean isStore) {
		this.isStore = isStore;
	}
	public boolean isIndexing() {
		return isIndexing;
	}
	public void setisIndexing(boolean isIndexing) {
		this.isIndexing = isIndexing;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isAnalyze() {
		return isAnalyze;
	}
	public void setisAnalyze(boolean isAnalyze) {
		this.isAnalyze = isAnalyze;
	}
	public float getFieldBoost() {
		return fieldBoost;
	}
	public void setFieldBoost(float fieldBoost) {
		this.fieldBoost = fieldBoost;
	}
	
	public Occur getOccur() {
		return isMust ? Occur.MUST : Occur.SHOULD;
	}

	public int getSortFieldType() {
		if("string".equals(type)) return SortField.STRING;
		if("integer".equals(type)) return SortField.INT;
		if("long".equals(type)) return SortField.LONG;
		else return SortField.STRING;
	}
	
	@Override
	public String toString() {
		return "CollectionField [name=" + name + ", isStore=" + isStore
				+ ", isIndexing=" + isIndexing + ", type=" + type
				+ ", isAnalyze=" + isAnalyze + ", fieldBoost=" + fieldBoost
				+ ", isMust=" + isMust + ", hasTermPosition=" + hasTermPosition
				+ ", hasTermOffset=" + hasTermOffset + ", hasTermVector="
				+ hasTermVector + "]";
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Object o = super.clone();
		return o;
	}
}
