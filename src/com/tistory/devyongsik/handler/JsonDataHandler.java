package com.tistory.devyongsik.handler;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tistory.devyongsik.domain.CollectionField;
import com.tistory.devyongsik.domain.LuceneDocumentBuilder;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 17.
 * 
 * 파일로부터 Json 포맷의 데이터를 읽어와서 Gson을 사용 Document 타입으로 변환한다.
 */
public class JsonDataHandler implements Handler {
	private Logger logger = LoggerFactory.getLogger(JsonDataHandler.class);
	
	@Override
	public List<Document> handledData(String jonsFormStr, Map<String, CollectionField> fieldsByName) {

		Type collectionType = new TypeToken<List<Map<String,String>>>(){}.getType();
		Gson gson = new Gson();
		
		logger.debug("jonsFormStr : {}", jonsFormStr);
		
		try {

			List<Map<String,String>> indexingData = gson.fromJson(jonsFormStr, collectionType);
			List<Document> docList = LuceneDocumentBuilder.buildDocumentList(indexingData, fieldsByName);
			
			return docList;
			
		} catch (Exception e) {
			logger.error("error : ", e);
			throw new IllegalStateException("색인 대상 문서를 변환 중 에러가 발생하였습니다. [" + jonsFormStr +"]");
		}
	}
}
