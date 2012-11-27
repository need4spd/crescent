package com.tistory.devyongsik.query;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.domain.CrescentCollectionField;
import com.tistory.devyongsik.exception.CrescentUnvalidRequestException;

public class CustomQueryStringParser {

	private Logger logger = LoggerFactory.getLogger(CustomQueryStringParser.class);
	private static Pattern pattern = Pattern.compile("(.*?)(:)(\".*?\")");
	
	public Query getQueryFromCustomQuery(List<CrescentCollectionField> searchFields, String customQueryString, Analyzer analyzer) 
			throws CrescentUnvalidRequestException {
		
		//패턴분석
		Matcher m = pattern.matcher(customQueryString);
		
		String fieldName = "";
		Occur occur = Occur.SHOULD;
		String userRequestQuery = "";
		
		boolean isRangeQuery = false;
		BooleanQuery resultQuery = new BooleanQuery();
		
		while(m.find()) {
			if(m.groupCount() != 3) {
				throw new CrescentUnvalidRequestException("쿼리 문법 오류. [" + customQueryString + "]");
			}
			
			fieldName = m.group(1);
			if(fieldName.startsWith("-")) {
				occur = Occur.MUST_NOT;
				fieldName = fieldName.substring(1);
			} else if (fieldName.startsWith("+")) {
				occur = Occur.MUST;
				fieldName = fieldName.substring(1);
			}
			
			//field가 검색 대상에 있는지 확인..
			boolean any = true;
			for(CrescentCollectionField crescentField : searchFields) {
				if(fieldName.equals(crescentField.getName())) {
					any = false;
					break;
				}
			}
			
			if(any) {
				logger.error("검색 할 수 없는 필드입니다. {} " , fieldName);
				throw new CrescentUnvalidRequestException("검색 할 수 없는 필드입니다. [" + fieldName + "]");
			}
			
			
			userRequestQuery = m.group(3).replaceAll("\"", "");
			if((userRequestQuery.startsWith("[") && userRequestQuery.endsWith("]")) 
					|| (userRequestQuery.startsWith("{") && userRequestQuery.endsWith("}"))) {
				
				isRangeQuery = true;
			
			}
			
			//range쿼리인 경우에는 RangeQuery 생성
			if(isRangeQuery) {
	
				QueryParser qp = new QueryParser(Version.LUCENE_36, fieldName, analyzer);
	
				try {
					Query query = qp.parse(userRequestQuery.replace("to", "TO"));
					resultQuery.add(query, occur);
					
					logger.debug("Query : {} ", query);
					logger.debug("Result Query : {} ", resultQuery);
					
				} catch (ParseException e) {
					logger.error("Exception in CustomQuery Parser ", e);
					throw new CrescentUnvalidRequestException("Range Query 문법 오류 [" + userRequestQuery + "]");
				}
			}
		}
		
		return resultQuery;
	}
}
