package com.tistory.devyongsik.domain;

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;

public class RequestBuilder<T> {
	
	private Logger logger = LoggerFactory.getLogger(RequestBuilder.class);

	public T mappingRequestParam(HttpServletRequest request, Class<T> clazz) throws Exception {
		
		T returnObject = clazz.newInstance();
		
		Field[] fields = clazz.getDeclaredFields();
		
		BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(returnObject);
		
		for(Field field : fields) {
			
			RequestParamName requestParamName = field.getAnnotation(RequestParamName.class);
			
			String paramName = null;
			String paramValue = null;
			
			if(requestParamName != null) {
				paramName = requestParamName.name();
				
				paramValue = request.getParameter(paramName);
				
				if(paramValue == null) {
					paramValue = requestParamName.defaultValue();
				}
			}
			
			logger.debug("field name : {}, paramName : {}, defaultValue : {}", new String[]{field.getName(), paramName, paramValue});
			
			beanWrapperImpl.setPropertyValue(field.getName(), paramValue);
		}
		
		return returnObject;
	}
}
