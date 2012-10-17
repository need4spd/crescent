package com.tistory.devyongsik.admin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.tistory.devyongsik.analyzer.DictionaryProperties;
import com.tistory.devyongsik.analyzer.dictionary.DictionaryFactory;
import com.tistory.devyongsik.analyzer.dictionary.DictionaryType;

public class DictionaryServiceImpl implements DictionaryService, ApplicationContextAware {
	
	private Logger logger = LoggerFactory.getLogger(DictionaryServiceImpl.class);
	private ApplicationContext applicationContext = null;
	
	public List<String> getDictionary (DictionaryType dicType) {
		
		List<String> dictionary = DictionaryFactory.getFactory().get(dicType);
		
		return dictionary;
	
	}
	
	public void addWordToDictionary (DictionaryType dicType, String word) {
		List<String> dictionary = DictionaryFactory.getFactory().get(dicType);
		dictionary.add(word);
	}
	
	public void removeWordFromDictionary (DictionaryType dicType, String word) {
		List<String> dictionary = DictionaryFactory.getFactory().get(dicType);
		dictionary.remove(word);
	}
	
	public List<String> findWordFromDictionary (DictionaryType dicType, String word) {
		List<String> dictionary = DictionaryFactory.getFactory().get(dicType);
		int index = dictionary.indexOf(word);
		
		logger.debug("index : {}", index);
		
		if(index < 0) {
			return new ArrayList<String>();
		} 
		
		List<String> returnResult = new ArrayList<String>();
		returnResult.add(dictionary.get(index));
		
		logger.debug("returnResult : {}", returnResult);
		
		return returnResult;
	}
	
	public void writeToDictionaryFile(DictionaryType dicType) {
		String dicPath = DictionaryProperties.getInstance().getProperty(dicType.getPropertiesKey());
		
		logger.debug("dictionary path : {}", dicPath);
		
		URL url = DictionaryServiceImpl.class.getClassLoader().getResource(dicPath);
		
		logger.debug("URL : {}", url);
		
		try {
		
			File dictionaryFile = new File(url.toURI());
			
			logger.debug("dictionaryFile : {}", dictionaryFile);
			
			ServletContext sc = (ServletContext)applicationContext.getBean("servletContext");
			String charSet = sc.getInitParameter("dictionary-charset");
			
			logger.debug("dictionary charSet : {}", charSet);
			
			FileOutputStream fos = new FileOutputStream(dictionaryFile, false);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName(charSet)));
			
			List<String> dictionary = DictionaryFactory.getFactory().get(dicType);
			for(String dicWord : dictionary) {
				bw.write(dicWord);
				bw.write("\n");
			}
			
			bw.flush();
			bw.close();
			fos.close();
		
		} catch (URISyntaxException e) {
			e.printStackTrace();
			logger.error("error : ", e);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("error : ", e);
			
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("error : ", e);
			
		}		
	}
	
	public void rebuildDictionary(DictionaryType dictionaryType) {
		DictionaryFactory.getFactory().rebuildDictionary(dictionaryType);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		
		this.applicationContext = applicationContext;
	}
}
