package com.tistory.devyongsik.config;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public class CollectionConfigUpdateModule {

	public static void main(String[] args) {
		XStream stream = new XStream();
		stream.processAnnotations(NewCollection.class);
		
		NewCollection newCollection = new NewCollection();
		newCollection.setName("sample");
		newCollection.setAnalyzer("com.tistory.devyongsik.analyzer.KoreanAnalyzer");
		newCollection.setIndexingDirectory("dir");
		
		List<NewCollectionField> newCollectionFields = new ArrayList<NewCollectionField>();
		
		NewCollectionField newCollectionField = new NewCollectionField();
		newCollectionField.setName("board_id");
		newCollectionField.setStore(true);
		
		NewCollectionField newCollectionField2 = new NewCollectionField();
		newCollectionField2.setName("title");
		newCollectionField2.setStore(true);
		
		newCollectionFields.add(newCollectionField);
		newCollectionFields.add(newCollectionField2);
		
		newCollection.setFields(newCollectionFields);
		
		
		System.out.println(stream.toXML(newCollection));
	}
}
