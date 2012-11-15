package com.tistory.devyongsik.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("sortField")
public class CrescentSortField {

	@XStreamAsAttribute
	private String source;
	
	@XStreamAsAttribute
	private String dest;
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	
	@Override
	public String toString() {
		return "CrescentSortField [source=" + source + ", dest=" + dest + "]";
	}
}
