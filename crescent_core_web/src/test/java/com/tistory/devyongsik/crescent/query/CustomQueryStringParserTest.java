package com.tistory.devyongsik.crescent.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import junit.framework.Assert;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;

import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.crescent.search.exception.CrescentInvalidRequestException;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;

public class CustomQueryStringParserTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() {
		super.init();
	}

	@Test
	public void rangeQuery() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setCustomQuery("board_id:\"[10 TO 100000]\"");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		Query query = csrw.getQuery();
		
		System.out.println(query);
		
		Assert.assertEquals("board_id:[10 TO 100000]", query.toString());
	}
	
	@Test(expected = CrescentInvalidRequestException.class)
	public void rangeQueryNoSearchField() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setCustomQuery("field1:\"[10 TO 100000]\"");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		Query query = csrw.getQuery();
		
		System.out.println(query);
		
	}
	
	@Test
	public void normalTermQuery() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setCustomQuery("dscr:\"파이썬 프로그래밍 공부\"");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		Query query = csrw.getQuery();
		
		System.out.println(query);
		
		Assert.assertEquals("dscr:파이썬 dscr:파이 dscr:프로그래밍 dscr:공부", query.toString());
	}
	
	@Test
	public void normalTermQueryWithDefaultFieldBoost() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setCustomQuery("title:\"파이썬 프로그래밍 공부\"");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		Query query = csrw.getQuery();
		
		System.out.println(query);
		
		Assert.assertEquals("title:파이썬^2.0 title:파이^2.0 title:프로그래밍^2.0 title:공부^2.0", query.toString());
	}
	
	@Test
	public void multipleTermQueryWithDefaultFieldBoost() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setCustomQuery("title:\"파이썬 프로그래밍 공부\" +dscr:\"자바 병렬 프로그래밍\"");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		Query query = csrw.getQuery();
		
		System.out.println(query);
		
		Assert.assertEquals("title:파이썬^2.0 title:파이^2.0 title:프로그래밍^2.0 title:공부^2.0 +dscr:자바 +dscr:병렬 +dscr:프로그래밍", query.toString());
	}
	
	@Test
	public void normalTermQueryWithCustomBoost() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setCustomQuery("dscr:\"파이썬 프로그래밍 공부^10\"");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		Query query = csrw.getQuery();
		
		System.out.println(query);
		
		Assert.assertEquals("dscr:파이썬^10.0 dscr:파이^10.0 dscr:프로그래밍^10.0 dscr:공부^10.0", query.toString());
	}
	
	@Test
	public void normalTermQueryWithDefaultFieldBoostAndCustomBoost() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setCustomQuery("title:\"파이썬 프로그래밍 공부^10\" dscr:\"파이썬 프로그래밍 공부^10\"");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		Query query = csrw.getQuery();
		
		System.out.println(query);
		
		Assert.assertEquals("title:파이썬^12.0 title:파이^12.0 title:프로그래밍^12.0 title:공부^12.0 dscr:파이썬^10.0 dscr:파이^10.0 dscr:프로그래밍^10.0 dscr:공부^10.0", query.toString());
	}
	
	@Test(expected = CrescentInvalidRequestException.class)
	public void complexQueryWithDefaultFieldBoostAndCustomBoostException() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setCustomQuery("title:\"파이썬 프로그래밍 공부^10\" dscr:\"파이썬 프로그래밍 공부^10\" title:\"[50 TO 50000]\"");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		Query query = csrw.getQuery();
		
		System.out.println(query);
		
		Assert.assertEquals("title:파이썬^12.0 title:파이^12.0 title:프로그래밍^12.0 title:공부^12.0 dscr:파이썬^10.0 dscr:파이^10.0 dscr:프로그래밍^10.0 dscr:공부^10.0 title:[50 TO 50000]", query.toString());
	}
	
	@Test
	public void complexQueryWithDefaultFieldBoostAndCustomBoost() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setCustomQuery("title:\"파이썬 프로그래밍 공부^10\" dscr:\"파이썬 프로그래밍 공부^10\" board_id:\"[50 TO 50000]\"");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		Query query = csrw.getQuery();
		
		System.out.println(query);
		
		Assert.assertEquals("title:파이썬^12.0 title:파이^12.0 title:프로그래밍^12.0 title:공부^12.0 dscr:파이썬^10.0 dscr:파이^10.0 dscr:프로그래밍^10.0 dscr:공부^10.0 board_id:[50 TO 50000]", query.toString());
	}
	
	@Test
	public void filterQuery() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setFilter("title:\"python\"");
		
		CrescentSearchRequestWrapper csrw 
			= new CrescentSearchRequestWrapper(searchRequest);
		
		Filter filter = csrw.getFilter();
		
		System.out.println(filter);
		
		Assert.assertEquals("QueryWrapperFilter(title:python^2.0)", filter.toString());
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
			//System.out.println(m.groupCount());
			System.out.println(m.group(0) + " , " + m.group(1) + " , " + m.group(2) + " , " + m.group(3));
			
		}
		
		System.out.println(m.groupCount());
	}
}
