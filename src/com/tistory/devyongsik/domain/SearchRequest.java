package com.tistory.devyongsik.domain;

public class SearchRequest {

	@RequestParamName(name="col_name", defaultValue="sample")
	private String collectionName;
	
	@RequestParamName(name="keyword", defaultValue="")
	private String keyword;
	
	@RequestParamName(name="start_offset", defaultValue="0")
	private String startOffSet;
	
	@RequestParamName(name="page_size", defaultValue="10")
	private String pageSize;
	
	@RequestParamName(name="sort", defaultValue="")
	private String sort;
	
	@RequestParamName(name="search_field", defaultValue="")
	private String searchField;

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getStartOffSet() {
		return startOffSet;
	}

	public void setStartOffSet(String startOffSet) {
		this.startOffSet = startOffSet;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getSearchField() {
		return searchField;
	}

	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}

	@Override
	public String toString() {
		return "SearchRequest [collectionName=" + collectionName + ", keyword="
				+ keyword + ", startOffSet=" + startOffSet + ", pageSize="
				+ pageSize + ", sort=" + sort + ", searchField=" + searchField
				+ "]";
	}
	
	
}
