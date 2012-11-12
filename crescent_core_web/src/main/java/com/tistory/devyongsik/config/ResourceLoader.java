package com.tistory.devyongsik.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * author : need4spd, need4spd@naver.com, 2012. 2. 26.
 */
public class ResourceLoader {
	private Logger logger = LoggerFactory.getLogger(ResourceLoader.class);
	
	private ClassLoader classLoader;
	private Document document = null;
	private String name;
	private Properties properties = null;
	
	public ResourceLoader(String name) {
		
		logger.info("ResourceLoader init..");
		
		this.name = name;
		this.classLoader = getClassLoader();
	}
	
	private ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
	private InputStream openResource(String name) {
		InputStream is = null;
		
		is = this.classLoader.getResourceAsStream(name);
		
		if(is == null) {
			logger.error("{} 를 찾을 수 없습니다.", name);
			throw new IllegalStateException(name + " 을 classpath에서 찾을 수 없습니다.");
		}
		
		return is;
	}
	
	private void buildDocument(InputStream is) {
		SAXReader saxReader = new SAXReader();
    	
    	try {
			document = saxReader.read(is);
		} catch (DocumentException e) {
			logger.error("error : ", e);
			throw new RuntimeException(name + ".xml" + " 파일이 존재하지 않거나 Parsing 오류 발생.");
		}
	}
	
	private void buildProperties(InputStream is) {
		properties = new Properties();
		try {
			properties.load(is);
		} catch (IOException e) {
			logger.error("error : ", e);
			throw new RuntimeException(name + ".properties" + " 파일이 존재하지 않거나 Loading 중 오류 발생.");
		}
	}
	
	public Document getDocument() {
		if(document == null) {
			buildDocument(openResource(name));
		}
		
		return document;
	}
	
	public Properties getProperties() {
		if(properties == null) {
			buildProperties(openResource(name));
		}
		
		return properties;
	}
}
