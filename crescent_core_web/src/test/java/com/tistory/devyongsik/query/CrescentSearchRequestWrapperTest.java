package com.tistory.devyongsik.query;

import java.util.List;

import junit.framework.Assert;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.junit.Before;
import org.junit.Test;

import com.tistory.devyongsik.domain.CrescentCollectionField;
import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.exception.CrescentInvalidRequestException;

public class CrescentSearchRequestWrapperTest {
	private static SearchRequest searchRequest;

	@Before
	public void initParamMap() {
		searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
	}

	@Test
	public void getStartOffset() {
		CrescentSearchRequestWrapper searchRequestWrapper = new CrescentSearchRequestWrapper(searchRequest);		
		Assert.assertEquals(0, searchRequestWrapper.getStartOffSet());

		searchRequest.setPageNum("10");
		Assert.assertEquals(180, searchRequestWrapper.getStartOffSet());
	}

	@Test
	public void getHitsForPage() {
		CrescentSearchRequestWrapper searchRequestWrapper = new CrescentSearchRequestWrapper(searchRequest);		
		Assert.assertEquals(20, searchRequestWrapper.getHitsForPage());

		searchRequest.setPageSize("30");
		Assert.assertEquals(30, searchRequestWrapper.getHitsForPage());
	}

	@Test
	public void getSearchFieldNames() {
		CrescentSearchRequestWrapper searchRequestWrapper = new CrescentSearchRequestWrapper(searchRequest);
		List<CrescentCollectionField> searchFields = searchRequestWrapper.getTargetSearchFields();
		
		String result = "[";
		for(CrescentCollectionField f : searchFields) {
			result += f.getName() + ", ";
		}
		result += "]";
		Assert.assertEquals("[title, dscr, ]", result);
	}

	@Test
	public void getSort() {
		searchRequest.setSort("title_sort desc, board_id_sort asc");
		CrescentSearchRequestWrapper searchRequestWrapper = new CrescentSearchRequestWrapper(searchRequest);

		Sort sort = searchRequestWrapper.getSort();
		Assert.assertEquals("<string: \"title_sort\">!,<long: \"board_id_sort\">", sort.toString());

		searchRequest.setSort("score desc, title_sort desc");
		sort = searchRequestWrapper.getSort();

		Assert.assertEquals("<score>,<string: \"title_sort\">!", sort.toString());
	}

	@Test
	public void getKeyword() {
		searchRequest.setKeyword("청바지");
		CrescentSearchRequestWrapper searchRequestWrapper = new CrescentSearchRequestWrapper(searchRequest);

		Assert.assertEquals("청바지", searchRequestWrapper.getKeyword());
	}
	
	@Test
	public void getFilter() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setFilter("title:\"파이썬 프로그래밍 공부\" +dscr:\"자바 병렬 프로그래밍\"");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		Filter filter = csrw.getFilter();
		
		System.out.println(filter);
		
		Assert.assertEquals("QueryWrapperFilter(title:파이썬^2.0 title:파이^2.0 title:프로그래밍^2.0 title:공부^2.0 +dscr:자바 +dscr:병렬 +dscr:프로그래밍)", filter.toString());
	}
	
	@Test
	public void getQuery() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setCustomQuery("title:\"파이썬 프로그래밍 공부\" +dscr:\"자바 병렬 프로그래밍\"");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		Query query = csrw.getQuery();
		
		System.out.println(query);
		
		Assert.assertEquals("title:파이썬^2.0 title:파이^2.0 title:프로그래밍^2.0 title:공부^2.0 +dscr:자바 +dscr:병렬 +dscr:프로그래밍", query.toString());
	}
}
