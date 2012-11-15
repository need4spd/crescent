package com.tistory.devyongsik.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("defaultSearchField")
public class CrescentDefaultSearchField {

	@XStreamAsAttribute
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "CrescentDefaultSearchField [name=" + name + "]";
	}
	
	
}
