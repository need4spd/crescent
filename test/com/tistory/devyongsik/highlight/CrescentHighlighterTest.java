package com.tistory.devyongsik.highlight;

import org.junit.Test;

import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.query.CrescentRequestQueryStrParser;

public class CrescentHighlighterTest {

	@Test
	public void highlightTest() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("제목");
		searchRequest.setCollectionName("glider_wiki");
		
		CrescentRequestQueryStrParser crqsp = new CrescentRequestQueryStrParser(searchRequest);
		
		CrescentHighlighter highlighter = new CrescentHighlighter(crqsp);
		String r = highlighter.getBestFragment("we_wiki_title", "제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.제목 입니다.");
		
		System.out.println(r);
	}
}
