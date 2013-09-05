package com.tistory.devyongsik.crescent.collection.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("collections")
public class CrescentCollections {

	@XStreamImplicit(itemFieldName="collection")
	private List<CrescentCollection> crescentCollections = null;

	@XStreamOmitField
	private Map<String, CrescentCollection> crescentCollectionsMap = null;
	
	private void lazyLoadMap() {
		if(crescentCollectionsMap == null) {
			crescentCollectionsMap = new HashMap<String, CrescentCollection>();
			
			for(CrescentCollection c : crescentCollections) {
				crescentCollectionsMap.put(c.getName(), c);
			}
		}
	}
	
	public CrescentCollection getCrescentCollection(String name) {
		lazyLoadMap();
		
		return crescentCollectionsMap.get(name); 
	}
	
	public Map<String, CrescentCollection> getCrescentCollectionsMap() {
		lazyLoadMap();
		
		return crescentCollectionsMap;
	}

	public void setCrescentCollectionsMap(
			Map<String, CrescentCollection> crescentCollectionsMap) {
		this.crescentCollectionsMap = crescentCollectionsMap;
	}

	public List<CrescentCollection> getCrescentCollections() {
		return crescentCollections;
	}

	public void setCrescentCollections(List<CrescentCollection> crescentCollections) {
		this.crescentCollections = crescentCollections;
	}

	@Override
	public String toString() {
		return "CrescentCollections [crescentCollections="
				+ crescentCollections + "]";
	}
}
