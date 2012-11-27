package com.tistory.devyongsik.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.exception.CrescentUnvalidRequestException;

public class CustomQueryStringParserTest {

	@Test
	public void rangeQuery() throws ParseException, CrescentUnvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setCustomQuery("title:\"[10 TO 100000]\"");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		CustomQueryStringParser parser = new CustomQueryStringParser();
		Query query = parser.getQueryFromCustomQuery(csrw.getTargetSearchFields()
				,csrw.getCustomQuery()
				,new KoreanAnalyzer(false));
		
		System.out.println(query);
		
		Assert.assertEquals("title:[10 TO 100000]", query.toString());
	}
	
	@Test(expected = CrescentUnvalidRequestException.class)
	public void rangeQueryNoSearchField() throws ParseException, CrescentUnvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setCustomQuery("field1:\"[10 TO 100000]\"");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		CustomQueryStringParser parser = new CustomQueryStringParser();
		Query query = parser.getQueryFromCustomQuery(csrw.getTargetSearchFields()
				,csrw.getCustomQuery()
				,new KoreanAnalyzer(false));
		
		System.out.println(query);
		
	}
	
	@Test
	public void sample() {
		BooleanQuery bq = new BooleanQuery();
		
		Query termQuery = new TermQuery(new Term("field", "파이썬 프로그래밍"));
		bq.add(termQuery, Occur.MUST);
		
		NumericRangeQuery<Integer> rangeQuery = NumericRangeQuery.newIntRange("field2", 0, 10, true, true);
		
		bq.add(rangeQuery, Occur.MUST);
		
		System.out.println(bq);
	}
	
	@Test
	public void patternMatch() {
		String query = "title:\"ab cd\" body:\"addd cd\" +price:\"[1000 to 10000]\" description:\"addd cd\"";
		
		Pattern pattern = Pattern.compile("(.*?)(:)(\".*?\")");
		Matcher m = pattern.matcher(query);
		
		while(m.find()) {
			System.out.println(m.groupCount());
			System.out.println(m.group(0) + " , " + m.group(1) + " , " + m.group(2) + " , " + m.group(3));
			
		}
		
		System.out.println(m.groupCount());
	}
}
