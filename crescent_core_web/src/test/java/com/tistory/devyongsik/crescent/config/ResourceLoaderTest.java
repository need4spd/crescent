package com.tistory.devyongsik.crescent.config;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.junit.Assert;
import org.junit.Test;

/**
 * author : need4spd, need4spd@naver.com, 2012. 2. 26.
 */
public class ResourceLoaderTest {

	@Test
	public void documentRead() {
		ResourceLoader resourceLoader = new ResourceLoader("collection/test-collections.xml");
		Document document = resourceLoader.getDocument();

		Assert.assertNotNull(document);
	}

	@Test
	public void readElements() {
		ResourceLoader resourceLoader = new ResourceLoader("collection/test-collections.xml");
		Document document = resourceLoader.getDocument();

		List<Node> collectionList = document.selectNodes("//collection");

		System.out.println(collectionList);

		for(Node node : collectionList) {
			Element collectionEl = (Element) node;
			System.out.println(collectionEl.elementText("analyzer"));
			System.out.println(collectionEl.elementText("indexingDirectory"));

			List<Node> fieldsList = document.selectNodes("//field");
			for(Node f : fieldsList) {
				Element e = (Element) f;
				System.out.println(e.attributeValue("name"));
				System.out.println(e.attributeValue("store"));
				System.out.println(e.attributeValue("index"));
				System.out.println(e.attributeValue("type"));
				System.out.println(e.attributeValue("analyze"));
				System.out.println(e.attributeValue("must"));
				System.out.println(e.attributeValue("boost"));
			}

			List<Node> defaultSearchFields = document.selectNodes("//defaultSearchField");
			for(Node df : defaultSearchFields) {
				Element e = (Element) df;
				System.out.println(e.attributeValue("name"));
			}

			List<Node> sortFields = document.selectNodes("//sortField");
			for(Node sf : sortFields) {
				Element e = (Element) sf;
				System.out.println(e.attributeValue("source"));
				System.out.println(e.attributeValue("dest"));
			}
		}
	}
}
