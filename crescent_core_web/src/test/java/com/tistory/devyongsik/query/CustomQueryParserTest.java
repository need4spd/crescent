package com.tistory.devyongsik.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.junit.Test;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;

public class CustomQueryParserTest {

	@Test
	public void test() throws ParseException {
		String q = "+field:파이썬 프로그래밍 +field2:[0 TO 10]";
		
		QueryParser qp = new QueryParser(Version.LUCENE_36, "subject", new KoreanAnalyzer(false));
		Query query = qp.parse(q);
		
		System.out.println(query);
	}
	
	@Test
	public void sample() {
		BooleanQuery bq = new BooleanQuery();
		
		Query termQuery = new TermQuery(new Term("field", "파이썬 프로그래밍"));
		bq.add(termQuery, Occur.MUST);
		
		NumericRangeQuery rangeQuery = NumericRangeQuery.newIntRange("field2", 0, 10, true, true);
		
		bq.add(rangeQuery, Occur.MUST);
		
		System.out.println(bq);
	}
	
	@Test
	public void patternMatch() {
		String query = "title:\"ab cd\" body:\"addd cd\" +price:\"[1000 to 10000]\"";
		
		Pattern pattern = Pattern.compile("(.*?)(:)(\".*?\")");
		Matcher m = pattern.matcher(query);
		
		while(m.find()) {
			System.out.println("a");
			System.out.println(m.group(0) + " , " + m.group(1) + " , " + m.group(2) + " , " + m.group(3));
			
		}
		
		System.out.println(m.groupCount());
	}
}
