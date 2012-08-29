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
		
		result.add(row);
		
		
		JsonFormConverter converter = new JsonFormConverter();
		String jsonForm = converter.convert(result);
		
		Assert.assertEquals("[{\"title\":\"타이틀\",\"dscr\":\"상세내용\"}]", jsonForm);
	}
}
