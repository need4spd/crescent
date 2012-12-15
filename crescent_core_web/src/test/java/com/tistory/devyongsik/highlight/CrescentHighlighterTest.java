package com.tistory.devyongsik.highlight;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import junit.framework.Assert;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.junit.Test;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.domain.CrescentCollectionField;
import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.exception.CrescentInvalidRequestException;
import com.tistory.devyongsik.query.CrescentSearchRequestWrapper;

public class CrescentHighlighterTest {

	@Test
	public void highlightTest() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("입니다");
		searchRequest.setCollectionName("sample");
		searchRequest.setSearchField("title,dscr");
		
		CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest);
		List<CrescentCollectionField> targetSearchFields = csrw.getTargetSearchFields();
		
		System.out.println("####### : " + targetSearchFields);
		
		CrescentHighlighter highlighter = new CrescentHighlighter(csrw);
		String r = highlighter.getBestFragment(csrw.getTargetSearchFields().get(0), "제목 입니다.텍스트 입니다.제목 입니다.");
		String r2 = highlighter.getBestFragment(csrw.getTargetSearchFields().get(1), "텍스트 입니다. 제목.");
		
		System.out.println(r);
		
		System.out.println("-------");
		
		System.out.println(r2);
		
		Assert.assertEquals("제목 <b>입니다</b>.텍스트 <b>입니다</b>.제목 <b>입니다</b>.", r);
		Assert.assertEquals("텍스트 <b>입니다</b>. 제목.", r2);
	}
	
	@Test
	public void highlightUsage() throws IOException, InvalidTokenOffsetsException {
		String text = "my fox jump group org next fox spring health care book fox tape java fox fox shop world fox";
		
		TermQuery query = new TermQuery(new Term("f", "fox"));

		QueryScorer scorer = new QueryScorer(query);

		Highlighter highlighter = new Highlighter(scorer);

		Fragmenter fragmenter = new SimpleFragmenter(5);
		highlighter.setTextFragmenter(fragmenter);

		Analyzer a = new KoreanAnalyzer(false);
		TokenStream tokenStream = a.tokenStream("f", new StringReader(text));

		String result =
		        highlighter.getBestFragments(tokenStream, text,2, "...");

		a.close();
		
		System.out.println(result);
		
		Assert.assertEquals(" <B>fox</B>... <B>fox</B>", result);
	}
}
