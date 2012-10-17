package com.tistory.devyongsik.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.junit.Assert;
import org.junit.Test;

import com.tistory.devyongsik.config.CollectionConfig;

public class LuceneDocumentBuilderTest {

	@Test
	public void create() {
		List<Map<String, String>> docList = new ArrayList<Map<String, String>>();
		Map<String, String> doc = new HashMap<String, String>();
		
		doc.put("dscr", "상세 내용");
		doc.put("creuser", "작성자");
		doc.put("board_id", "1");
		doc.put("title", "제목");
		
		docList.add(doc);
		
		CollectionConfig collectionConfig = CollectionConfig.getInstance();
		Map<String, Collection> collections = collectionConfig.getCollections();
		
		Collection sampleCollection = collections.get("sample");
		
		
		Map<String, CollectionField> fieldsByName = sampleCollection.getFieldsByName();
		
		List<Document> luceneDocuments = LuceneDocumentBuilder.buildDocumentList(docList, fieldsByName, sampleCollection.getSortFieldNames());
		
		Assert.assertNotNull(luceneDocuments);
		
		for(Document luceneDocument :luceneDocuments){ 
			System.out.println(luceneDocument);
		}
	}
}
