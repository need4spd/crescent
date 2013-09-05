package com.tistory.devyongsik.crescent.admin.entity;

/**
 * author : need4spd, need4spd@naver.com, 2013. 2. 4.
 */
public class MorphToken {
	private String term;
	private String type;
	private int startOffset;
	private int endOffset;
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getStartOffset() {
		return startOffset;
	}
	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}
	public int getEndOffset() {
		return endOffset;
	}
	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}
	@Override
	public String toString() {
		return "MorphToken [term=" + term + ", type=" + type + ", startOffset="
				+ startOffset + ", endOffset=" + endOffset + "]";
	}
	
	
}
