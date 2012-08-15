package com.tistory.devyongsik.query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.lucene.search.Sort;
import org.junit.Before;
import org.junit.Test;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 5.
 */
public class QueryParserTest {
	
	private static Map<String, String> paramMap;
	
	@Before
	public void initParamMap() {
		paramMap = new HashMap<String,String>();
		paramMap.put("col_name", "sample");
	}
	
	@Test
	public void getStartOffset() {
		QueryParser queryParser = new QueryParser(paramMap);		
		Assert.assertEquals(0, queryParser.getStartOffset());
		
		paramMap.put("page", "5");
		Assert.assertEquals(80, queryParser.getStartOffset());
	}
	
	@Test
	public void getPage() {
		QueryParser queryParser = new QueryParser(paramMap);		
		Assert.assertEquals(1, queryParser.getPage());
		
		paramMap.put("page", "5");
		Assert.assertEquals(5, queryParser.getPage());
	}
	
	@Test
	public void getHitsForPage() {
		QueryParser queryParser = new QueryParser(paramMap);		
		Assert.assertEquals(20, queryParser.getHitsForPage());
		
		paramMap.put("row", "5");
		Assert.assertEquals(5, queryParser.getHitsForPage());
	}
	
	@Test
	public void getSearchFieldNames() {
		QueryParser queryParser = new QueryParser(paramMap);
		String[] searchFieldNames = queryParser.getSearchFieldNames();
		
		Assert.assertEquals("[title, dscr]", Arrays.toString(searchFieldNames));
	}
	
	@Test
	public void getSort() {
		paramMap.put("sort", "title_sort desc, board_id_sort asc");
		QueryParser queryParser = new QueryParser(paramMap);
		
		Sort sort = queryParser.getSort();
		Assert.assertEquals("<string: \"title_sort\">!,<long: \"board_id_sort\">", sort.toString());
		
		paramMap.put("sort", "score desc, title_sort desc");
		sort = queryParser.getSort();
		
		Assert.assertEquals("<score>,<string: \"title_sort\">!", sort.toString());
	}
}
