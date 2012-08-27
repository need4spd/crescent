package com.tistory.devyongsik.query;

import java.util.Arrays;

import junit.framework.Assert;

import org.apache.lucene.search.Sort;
import org.junit.Before;
import org.junit.Test;

import com.tistory.devyongsik.domain.SearchRequest;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 5.
 */
public class QueryParserTest {
	
	private static SearchRequest searchRequest;
	
	@Before
	public void initParamMap() {
		searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
	}
	
	@Test
	public void getStartOffset() {
		QueryParser queryParser = new QueryParser(searchRequest);		
		Assert.assertEquals(0, queryParser.getStartOffSet());
		
		searchRequest.setStartOffSet("20");
		Assert.assertEquals(20, queryParser.getStartOffSet());
	}
	
	@Test
	public void getHitsForPage() {
		QueryParser queryParser = new QueryParser(searchRequest);		
		Assert.assertEquals(20, queryParser.getHitsForPage());
		
		searchRequest.setPageSize("30");
		Assert.assertEquals(30, queryParser.getHitsForPage());
	}
	
	@Test
	public void getSearchFieldNames() {
		QueryParser queryParser = new QueryParser(searchRequest);
		String[] searchFieldNames = queryParser.getSearchFieldNames();
		
		Assert.assertEquals("[title, dscr]", Arrays.toString(searchFieldNames));
	}
	
	@Test
	public void getSort() {
		searchRequest.setSort("title_sort desc, board_id_sort asc");
		QueryParser queryParser = new QueryParser(searchRequest);
		
		Sort sort = queryParser.getSort();
		Assert.assertEquals("<string: \"title_sort\">!,<long: \"board_id_sort\">", sort.toString());
		
		searchRequest.setSort("score desc, title_sort desc");
		sort = queryParser.getSort();
		
		Assert.assertEquals("<score>,<string: \"title_sort\">!", sort.toString());
	}
	
	@Test
	public void getKeyword() {
		searchRequest.setKeyword("청바지");
		QueryParser queryParser = new QueryParser(searchRequest);
		
		Assert.assertEquals("청바지", queryParser.getKeyword());
	}
}
