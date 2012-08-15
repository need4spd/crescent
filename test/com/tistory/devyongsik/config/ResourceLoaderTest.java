package com.tistory.devyongsik.config;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

/**
 * author : need4spd, need4spd@naver.com, 2012. 2. 26.
 */
public class ResourceLoaderTest {

	@Test
	public void documentRead() {
		ResourceLoader resourceLoader = new ResourceLoader("collection/collections.xml");
		Document document = resourceLoader.getDocument();
		
		Assert.assertNotNull(document);
	}
	
	@Test
	public void readElements() {
		ResourceLoader resourceLoader = new ResourceLoader("collection/collections.xml");
		Document document = resourceLoader.getDocument();
		
		//get collection list
		@SuppressWarnings("unchecked")
		List<Element> collectionList = document.selectNodes("//collection");
		
		System.out.println(collectionList);
		
		for(Element node : collectionList) {
			System.out.println(node.elementText("analyzer"));
			System.out.println(node.elementText("indexingDirectory"));
			
			@SuppressWarnings("unchecked")
			List<Element> fieldsList = document.selectNodes("//field");
			for(Element e : fieldsList) {
				System.out.println(e.attributeValue("name"));
				System.out.println(e.attributeValue("store"));
				System.out.println(e.attributeValue("index"));
				System.out.println(e.attributeValue("type"));
				System.out.println(e.attributeValue("analyze"));
				System.out.println(e.attributeValue("must"));
				System.out.println(e.attributeValue("boost"));
			}
			
			@SuppressWarnings("unchecked")
			List<Element> defaultSearchFields = document.selectNodes("//defaultSearchField");
			for(Element e : defaultSearchFields) {
				System.out.println(e.attributeValue("name"));
			}
			
			@SuppressWarnings("unchecked")
			List<Element> sortFields = document.selectNodes("//sortField");
			for(Element e : sortFields) {
				System.out.println(e.attributeValue("source"));
				System.out.println(e.attributeValue("dest"));
			}
		}
	}
}
