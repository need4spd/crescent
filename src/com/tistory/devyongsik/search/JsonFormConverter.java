package com.tistory.devyongsik.search;

import com.google.gson.Gson;

public class JsonFormConverter {

	public String convert(Object targetObject) {
		Gson gson = new Gson();
		String json = gson.toJson(targetObject);
		
		return json;
	}
}
