package com.tistory.devyongsik.utils;

public class RankingTerm implements Comparable<RankingTerm>{
	private String text;
	private String field;
	private int count;
	
	public RankingTerm(String text, String field, int count) {
		this.text = text;
		this.field = field;
		this.count= count;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public String toString() {
		return this.text + ", " + this.count;
	}
	
	
	public int compareTo(RankingTerm x) {
		if (this.count < x.count) {
			return -1;
		} else if (this.count == x.count) {
			return 0;
		} else {
			return 1;
		}
	}
}