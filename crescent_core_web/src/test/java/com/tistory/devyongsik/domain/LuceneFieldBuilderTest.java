package com.tistory.devyongsik.domain;

import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;
import org.junit.Assert;
import org.junit.Test;

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.index.LuceneFieldBuilder;

public class LuceneFieldBuilderTest {

	@Test
	public void create() {
		CrescentCollections crescentCollections = CrescentCollectionHandler.getInstance().getCrescentCollections();

		Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

		CrescentCollection sampleCollection = collections.get("sample");

		
		Map<String, CrescentCollectionField> fieldsByName = sampleCollection.getCrescentFieldByName();
		
		Set<String> fieldNames = fieldsByName.keySet();
		LuceneFieldBuilder luceneFieldBuilder = new LuceneFieldBuilder();
		
		for(String fieldName : fieldNames) {
			CrescentCollectionField field = fieldsByName.get(fieldName);
			Field luceneField = luceneFieldBuilder.create(field, "");
			
			System.out.println(luceneField);
			
			Assert.assertNotNull(luceneField);
		}
	}
}
