package com.tistory.devyongsik.crescent.search;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonFormConverter {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public String convert(Object targetObject) {
		
		try {
			
			ObjectMapper mapper = new ObjectMapper();	
			String json = mapper.writeValueAsString(targetObject);
			return json;
			
		} catch (IOException e) {
			logger.error("Exception while make json form string.", e);
			throw new IllegalStateException("Exception while make json form string.", e);
		}
	}
}
