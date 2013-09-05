package com.tistory.devyongsik.crescent.admin.service;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tistory.devyongsik.analyzer.DictionaryProperties;
import com.tistory.devyongsik.analyzer.dictionary.DictionaryFactory;
import com.tistory.devyongsik.analyzer.dictionary.DictionaryType;

@Service("dictionaryService")
public class DictionaryServiceImpl implements DictionaryService {
	
	private Logger logger = LoggerFactory.getLogger(DictionaryServiceImpl.class);
	
	public List<String> getDictionary (DictionaryType dicType) {
		
		List<String> dictionary = DictionaryFactory.getFactory().get(dicType);
		
		return dictionary;
	
	}
	
	public void addWordToDictionary (DictionaryType dicType, String word) {
		
		if(dicType == DictionaryType.COMPOUND) {
		
			if(word.split(":").length < 2) {
				logger.warn("복합명사사전의 단어는 [N:A,B] 형식이어야 합니다. [{}]", word);
				throw new IllegalStateException("복합명사사전의 단어는 [N:A,B] 형식이어야 합니다.");
			}
		}
		
		List<String> dictionary = DictionaryFactory.getFactory().get(dicType);
		dictionary.add(word);
	}
	
	public void removeWordFromDictionary (DictionaryType dicType, String word) {
		List<String> dictionary = DictionaryFactory.getFactory().get(dicType);
		dictionary.remove(word);
	}
	
	public List<String> findWordFromDictionary (DictionaryType dicType, String word) {
		List<String> dictionary = DictionaryFactory.getFactory().get(dicType);
		
		List<String> returnResult = new ArrayList<String>();
		
		for(String s : dictionary) {
			if(s.indexOf(word) >= 0) {
				returnResult.add(s);
			}
		}
	
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
			
			FileOutputStream fos = new FileOutputStream(dictionaryFile, false);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName("utf-8")));
			
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
}
