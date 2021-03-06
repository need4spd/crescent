package com.tistory.devyongsik.crescent.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
	private InputStream inputStream = null;
	private URL url = null;
	
	public ResourceLoader(String name) {
		
		logger.info("ResourceLoader init..");
		
		this.name = name;
		this.classLoader = Thread.currentThread().getContextClassLoader();
		initInputStream();
	}
	
	private void initInputStream() {
		
		try {
			inputStream = this.classLoader.getResourceAsStream(name);
			
			if(inputStream == null) {
				inputStream = new FileInputStream(new File(name));
			}
			
			if(inputStream == null) {
				logger.error("inputStream {} 를 지정된 경로에서 찾을 수 없습니다.", name);
			}
			
			url = this.classLoader.getResource(name);
			
			if(url == null) {
				url = new File(name).toURI().toURL();
			}
			
			if(url == null) {
				logger.error("url {} 를 지정된 경로에서 찾을 수 없습니다.", name);
			}
			
		} catch (Exception e) {
			logger.error("{}에 대한 resource를 찾지 못 했습니다.", name);
			throw new IllegalStateException(name+" 에 대한 resource를 찾지 못 했습니다.");
		}
	}
	
	protected InputStream getInputStream() {
		return inputStream;
	}
	
	protected URL getURL() {
		
		return url;
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
			buildDocument(inputStream);
		}
		
		return document;
	}
	
	public Properties getProperties() {
		if(properties == null) {
			buildProperties(inputStream);
		}
		
		return properties;
	}
}
