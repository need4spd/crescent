package com.tistory.devyongsik.query;

import java.util.Arrays;

import junit.framework.Assert;

import org.apache.lucene.search.Sort;
import org.junit.Before;
import org.junit.Test;

import com.tistory.devyongsik.domain.SearchRequest;

public class CrescentRequestQueryStrParserTest {
private static SearchRequest searchRequest;
	
	@Before
	public void initParamMap() {
		searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
	}
	
	@Test
	public void getStartOffset() {
		CrescentRequestQueryStrParser queryParser = new CrescentRequestQueryStrParser(searchRequest);		
		Assert.assertEquals(0, queryParser.getStartOffSet());
		
		searchRequest.setPageNum("10");
		Assert.assertEquals(180, queryParser.getStartOffSet());
	}
	
	@Test
	public void getHitsForPage() {
		CrescentRequestQueryStrParser queryParser = new CrescentRequestQueryStrParser(searchRequest);		
		Assert.assertEquals(20, queryParser.getHitsForPage());
		
		searchRequest.setPageSize("30");
		Assert.assertEquals(30, queryParser.getHitsForPage());
	}
	
	@Test
	public void getSearchFieldNames() {
		CrescentRequestQueryStrParser queryParser = new CrescentRequestQueryStrParser(searchRequest);
		String[] searchFieldNames = queryParser.getSearchFieldNames();
		
		Assert.assertEquals("[title, dscr]", Arrays.toString(searchFieldNames));
	}
	
	@Test
	public void getSort() {
		searchRequest.setSort("title_sort desc, board_id_sort asc");
		CrescentRequestQueryStrParser queryParser = new CrescentRequestQueryStrParser(searchRequest);
		
		Sort sort = queryParser.getSort();
		Assert.assertEquals("<string: \"title_sort\">!,<long: \"board_id_sort\">", sort.toString());
		
		searchRequest.setSort("score desc, title_sort desc");
		sort = queryParser.getSort();
		
		Assert.assertEquals("<score>,<string: \"title_sort\">!", sort.toString());
	}
	
	@Test
	public void getKeyword() {
		searchRequest.setKeyword("청바지");
		CrescentRequestQueryStrParser queryParser = new CrescentRequestQueryStrParser(searchRequest);
		
		Assert.assertEquals("청바지", queryParser.getKeyword());
	}
}
