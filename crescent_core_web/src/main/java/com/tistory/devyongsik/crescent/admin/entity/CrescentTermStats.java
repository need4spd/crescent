package com.tistory.devyongsik.crescent.admin.entity;

import org.apache.lucene.util.BytesRef;

public class CrescentTermStats {
	private BytesRef termtext;
	private String field;
	private int docFreq;
	private long totalTermFreq;

	public CrescentTermStats(String field, BytesRef termtext, int df) {
		this.termtext = (BytesRef)termtext.clone();
		this.field = field;
		this.docFreq = df;
	}

	public CrescentTermStats(String field, BytesRef termtext, int df, long tf) {
		this.termtext = (BytesRef)termtext.clone();
		this.field = field;
		this.docFreq = df;
		this.totalTermFreq = tf;
	}

	
	public BytesRef getTermtext() {
		return termtext;
	}

	public void setTermtext(BytesRef termtext) {
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

	public String getTermText() {
		return termtext.utf8ToString();
	}

	public String toString() {
		return field + ":" + termtext.utf8ToString() + ":" + docFreq + ":" + totalTermFreq;
	}
}
