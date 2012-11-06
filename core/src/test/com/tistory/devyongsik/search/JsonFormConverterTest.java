package com.tistory.devyongsik.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class JsonFormConverterTest {

	@Test
	public void convert() {
		List<Map<String ,String>> result = new ArrayList<Map<String, String>>();
		Map<String, String> row = new HashMap<String, String>();
		
		row.put("title", "타이틀");
		row.put("dscr", "상세내용");
		
		Map<String, String> row2 = new HashMap<String, String>();
		
		row2.put("title", "타이틀22");
		row2.put("dscr", "상세내용");
		
		result.add(row);
		result.add(row2);
		
		
		JsonFormConverter converter = new JsonFormConverter();
		String jsonForm = converter.convert(result);
		
		Assert.assertEquals("[{\"title\":\"타이틀\",\"dscr\":\"상세내용\"}," +
				"{\"title\":\"타이틀22\",\"dscr\":\"상세내용\"}]", jsonForm);
	}
	
	@Test
	public void convertSearchResult() {
		List<Map<String ,String>> resultList = new ArrayList<Map<String, String>>();
		Map<String, String> row = new HashMap<String, String>();
		
		row.put("title", "타이틀");
		row.put("dscr", "상세내용");
		
		Map<String, String> row2 = new HashMap<String, String>();
		
		row2.put("title", "타이틀22");
		row2.put("dscr", "상세내용");
		
		resultList.add(row);
		resultList.add(row2);
		
		Map<String,Object> result = 
				new HashMap<String, Object>();
		
		result.put("total_count", "10");
		result.put("result_list", resultList);
		
		JsonFormConverter converter = new JsonFormConverter();
		String jsonForm = converter.convert(result);
		
		Assert.assertEquals("{\"result_list\":[{\"title\":\"타이틀\",\"dscr\":\"상세내용\"}," +
				"{\"title\":\"타이틀22\",\"dscr\":\"상세내용\"}],\"total_count\":\"10\"}", jsonForm);
	}
}
