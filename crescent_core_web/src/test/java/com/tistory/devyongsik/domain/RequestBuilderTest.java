package com.tistory.devyongsik.domain;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class RequestBuilderTest {

	@Test
	public void searchRequestMapping() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("keyword", "nike");
		request.setParameter("col_name", "test");
		request.setParameter("start_offset", "50");
		request.setParameter("page_size", "30");
		request.setParameter("sort", "name desc");
		request.setParameter("search_field", "title, desc");
		
		RequestBuilder<SearchRequest> builder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = builder.mappingRequestParam(request, SearchRequest.class);
		
		System.out.println(searchRequest);
	}
}
