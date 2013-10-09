package com.tistory.devyongsik.crescent.admin.entity;


public class CrescentTermStats {
	private String termtext;
	private String field;
	private int docFreq;
	private long totalTermFreq;

	public CrescentTermStats(String field, String termtext, int df) {
		this.termtext = termtext;
		this.field = field;
		this.docFreq = df;
	}

	public CrescentTermStats(String field, String termtext, int df, long tf) {
		this.termtext = termtext;
		this.field = field;
		this.docFreq = df;
		this.totalTermFreq = tf;
	}

	
	public String getTermtext() {
		return termtext;
	}

	public void setTermtext(String termtext) {
		this.termtext = termtext;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public int getDocFreq() {
		return docFreq;
	}

	public void setDocFreq(int docFreq) {
		this.docFreq = docFreq;
	}

	public long getTotalTermFreq() {
		return totalTermFreq;
	}

	public void setTotalTermFreq(long totalTermFreq) {
		this.totalTermFreq = totalTermFreq;
	}

	public String toString() {
		return field + ":" + termtext + ":" + docFreq + ":" + totalTermFreq;
	}
}
