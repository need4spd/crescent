package com.tistory.devyongsik.search;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class JsonFormConverter {

	public String convert(List<Map<String, String>> targetList) {
		Gson gson = new Gson();
		String json = gson.toJson(targetList);
		
		return json;
	}
}
