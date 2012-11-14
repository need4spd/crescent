package com.tistory.devyongsik.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("field")
public class NewCollectionField {

	@XStreamAsAttribute
	private String name;
	
	@XStreamAsAttribute
	private boolean store;
	
	@XStreamAsAttribute
	private boolean index;
	
	@XStreamAsAttribute
	private String type;
	
	@XStreamAsAttribute
	private boolean analyze;
	
	@XStreamAsAttribute
	private boolean termposition;
	
	@XStreamAsAttribute
	private boolean termoffset;
	
	@XStreamAsAttribute
	private double boost;
	
	@XStreamAsAttribute
	private boolean must;
	
	@XStreamAsAttribute
	private boolean termvector;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isStore() {
		return store;
	}
	public void setStore(boolean store) {
		this.store = store;
	}
	public boolean isIndex() {
		return index;
	}
	public void setIndex(boolean index) {
		this.index = index;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isAnalyze() {
		return analyze;
	}
	public void setAnalyze(boolean analyze) {
		this.analyze = analyze;
	}
	public boolean isTermposition() {
		return termposition;
	}
	public void setTermposition(boolean termposition) {
		this.termposition = termposition;
	}
	public boolean isTermoffset() {
		return termoffset;
	}
	public void setTermoffset(boolean termoffset) {
		this.termoffset = termoffset;
	}
	public double getBoost() {
		return boost;
	}
	public void setBoost(double boost) {
		this.boost = boost;
	}
	public boolean isMust() {
		return must;
	}
	public void setMust(boolean must) {
		this.must = must;
	}
	public boolean isTermvector() {
		return termvector;
	}
	public void setTermvector(boolean termvector) {
		this.termvector = termvector;
	}
	
	
}
