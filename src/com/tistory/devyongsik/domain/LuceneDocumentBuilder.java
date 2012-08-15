package com.tistory.devyongsik.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 4.
 */
public class LuceneDocumentBuilder {
	public static List<Document> buildDocumentList(List<Map<String, String>> docList, Map<String, CollectionField> fieldsByName) {
		List<Document> documentList = new ArrayList<Document>();
		
		LuceneFieldBuilder luceneFieldBuilder = new LuceneFieldBuilder();
		
		for(Map<String, String> doc : docList) {
			Set<String> fieldNames = doc.keySet();
			
			Document document = new Document();
			
			for(String fieldName : fieldNames) {
				String value = doc.get(fieldName);
				
				Field field = luceneFieldBuilder.create(fieldsByName.get(fieldName), value);
				document.add(field);
			}
			
			documentList.add(document);
		}
		
		return documentList;
	}
}
