package com.tistory.devyongsik.crescent.search.entity;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.tistory.devyongsik.crescent.search.entity.RequestBuilder;
import com.tistory.devyongsik.crescent.search.entity.SearchRequest;

public class RequestBuilderTest {

	@Test
	public void keyword() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("keyword", "nike");
		
		RequestBuilder<SearchRequest> builder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = builder.mappingRequestParam(request, SearchRequest.class);
		
		Assert.assertEquals("nike", searchRequest.getKeyword());
	}
	
	@Test
	public void collectionName() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("col_name", "test");
		
		RequestBuilder<SearchRequest> builder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = builder.mappingRequestParam(request, SearchRequest.class);
		
		Assert.assertEquals("test", searchRequest.getCollectionName());
	}
	
	@Test
	public void pageSize() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("page_size", "50");
		
		RequestBuilder<SearchRequest> builder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = builder.mappingRequestParam(request, SearchRequest.class);
		
		Assert.assertEquals("50", searchRequest.getPageSize());
	}
	
	@Test
	public void sort() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("sort", "name desc");
		
		RequestBuilder<SearchRequest> builder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = builder.mappingRequestParam(request, SearchRequest.class);
		
		Assert.assertEquals("name desc", searchRequest.getSort());
	}
	
	@Test
	public void searchField() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("search_field", "title, contents");
		
		RequestBuilder<SearchRequest> builder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = builder.mappingRequestParam(request, SearchRequest.class);
		
		Assert.assertEquals("title, contents", searchRequest.getSearchField());
	}
	
	@Test
	public void customQuery() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("cq", "title:\"jang\"");
		
		RequestBuilder<SearchRequest> builder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = builder.mappingRequestParam(request, SearchRequest.class);
		
		Assert.assertEquals("title:\"jang\"", searchRequest.getCustomQuery());
	}
}
