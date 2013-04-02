package com.tistory.devyongsik.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("analyzer")
public class CrescentAnalyzerHolder {

	@XStreamAsAttribute
	private String type;
	
	@XStreamAlias("className")
	@XStreamAsAttribute
	private String className;
	
	@XStreamAlias("constructor-args")
	@XStreamAsAttribute
	private String constructorArgs;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getConstructorArgs() {
		return constructorArgs;
	}

	public void setConstructorArgs(String constructorArgs) {
		this.constructorArgs = constructorArgs;
	}

	@Override
	public String toString() {
		return "CrescentAnalyzerHolder [type=" + type + ", className="
				+ className + ", constructorArgs=" + constructorArgs + "]";
	}
}
