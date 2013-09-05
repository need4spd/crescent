package com.tistory.devyongsik.crescent.collection.entity;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.SortField;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("field")
public class CrescentCollectionField implements Cloneable {
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
	private float boost;
	
	@XStreamAsAttribute
	private boolean must;
	
	@XStreamAsAttribute
	private boolean termvector;
	
	@XStreamAsAttribute
	private boolean removeHtmlTag;
	
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
	public float getBoost() {
		return boost;
	}
	public void setBoost(float boost) {
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
	public boolean isRemoveHtmlTag() {
		return removeHtmlTag;
	}
	public void setRemoveHtmlTag(boolean removeHtmlTag) {
		this.removeHtmlTag = removeHtmlTag;
	}
	public Occur getOccur() {
		return must ? Occur.MUST : Occur.SHOULD;
	}

	public boolean isNumeric() {
		if("string".equals(type.toLowerCase())) return false;
		if("integer".equals(type.toLowerCase())) return true;
		if("long".equals(type.toLowerCase())) return true;
		
		return false;
	}
	
	public int getSortFieldType() {
		if("string".equals(type.toLowerCase())) return SortField.STRING;
		if("integer".equals(type.toLowerCase())) return SortField.INT;
		if("long".equals(type.toLowerCase())) return SortField.LONG;
		else return SortField.STRING;
	}
	@Override
	public String toString() {
		return "CrescentCollectionField [name=" + name + ", store=" + store
				+ ", index=" + index + ", type=" + type + ", analyze="
				+ analyze + ", termposition=" + termposition + ", termoffset="
				+ termoffset + ", boost=" + boost + ", must=" + must
				+ ", termvector=" + termvector + ", removeHtmlTag="
				+ removeHtmlTag + "]";
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		Object o = super.clone();
		return o;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CrescentCollectionField other = (CrescentCollectionField) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
}
