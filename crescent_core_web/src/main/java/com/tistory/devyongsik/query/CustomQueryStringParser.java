package com.tistory.devyongsik.query;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.regex.RegexQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.domain.CrescentCollectionField;
import com.tistory.devyongsik.exception.CrescentInvalidRequestException;

public class CustomQueryStringParser {

	private Logger logger = LoggerFactory.getLogger(CustomQueryStringParser.class);
	private static Pattern pattern = Pattern.compile("(.*?)(:)(\".*?\")");
	private Query resultQuery = null;
	
	protected Query getQuery(List<CrescentCollectionField> indexedFields, String customQueryString, Analyzer analyzer, String regexQueryString) throws CrescentInvalidRequestException {
		if(resultQuery != null) {
			return this.resultQuery;
		} else {
			return getQueryFromCustomQuery(indexedFields, customQueryString, analyzer, regexQueryString);
		}
	}
	
	private Query getQueryFromCustomQuery(List<CrescentCollectionField> indexedFields, String customQueryString, Analyzer analyzer, String regexQueryString) 
			throws CrescentInvalidRequestException {
		
		List<QueryAnalysisResult> queryAnalysisResultList = getQueryAnalysisResults(customQueryString);
		
		BooleanQuery resultQuery = new BooleanQuery();
		
		CrescentCollectionField searchTargetField = null;
		
		String fieldName = "";
		Occur occur = Occur.SHOULD;
		String userRequestQuery = "";
		float boost = 0F;
		
		boolean isRangeQuery = false;
		
		boolean any = true;
		boolean isLongField = false;
		boolean isAnalyzed = false;
		
		for(QueryAnalysisResult queryAnalysisResult : queryAnalysisResultList) {
		
			fieldName = queryAnalysisResult.getFieldName();
			occur = queryAnalysisResult.getOccur();
			userRequestQuery = queryAnalysisResult.getUserQuery();
			boost = queryAnalysisResult.getBoost();
			isRangeQuery = queryAnalysisResult.isRangeQuery();
			
			//field가 검색 대상에 있는지 확인..
			for(CrescentCollectionField crescentField : indexedFields) {
				if(fieldName.equals(crescentField.getName())) {
					any = false;
					searchTargetField = crescentField;
					
					isLongField = "LONG".equals(crescentField.getType());
					isAnalyzed = crescentField.isAnalyze();
					
					logger.debug("selected searchTargetField : {} ", searchTargetField);
					break;
				}
			}
			
			if(any) {
				logger.error("검색 할 수 없는 필드입니다. {} " , fieldName);
				throw new CrescentInvalidRequestException("검색 할 수 없는 필드입니다. [" + fieldName + "]");
			}
			
			//range쿼리인 경우에는 RangeQuery 생성
			if(isRangeQuery) {

				//QueryParser qp = new QueryParser(Version.LUCENE_36, fieldName, analyzer);
				String minValue = "";
				String maxValue = "";
				boolean isIncludeMin = false;
				boolean isIncludeMax = false;
				
				String[] splitQuery = userRequestQuery.split("TO");
				logger.info("splitQuery : {}", Arrays.toString(splitQuery));
				
				if(splitQuery.length != 2) {
					logger.error("문법 오류 확인바랍니다. {} " , userRequestQuery);
					throw new CrescentInvalidRequestException("문법 오류 확인바랍니다. [" + userRequestQuery + "]");
				}
				
				if(splitQuery[0].trim().startsWith("[")) {
					isIncludeMin = true;
				}
				
				if(splitQuery[1].trim().endsWith("]")) {
					isIncludeMax = true;
				}
				
				logger.debug("minInclude : {}, maxInclude : {}", isIncludeMin, isIncludeMax);
				
				minValue = splitQuery[0].trim().substring(1);
				maxValue = splitQuery[1].trim().substring(0, splitQuery[1].trim().length() - 1);
				
				logger.debug("minValue : {}, maxValue : {}", minValue, maxValue);
				
				boolean isNumeric = false;
				isNumeric = StringUtils.isNumeric(minValue) && StringUtils.isNumeric(maxValue);
				
				logger.debug("isLongField : {}", isLongField);
				logger.debug("is numeric : {}", isNumeric);
				
				Query query = null;
				
				if(isAnalyzed) {
					logger.error("범위검색 대상 field는 analyzed값이 false이어야 합니다. {} " , userRequestQuery);
					throw new CrescentInvalidRequestException("범위검색 대상 field는 analyzed값이 false이어야 합니다. [" + userRequestQuery + "]");
				}
				if(isLongField && isNumeric) {
					query = NumericRangeQuery.newLongRange(fieldName, Long.parseLong(minValue), Long.parseLong(maxValue), isIncludeMin, isIncludeMax);
				} else if (!(isLongField && isNumeric)){
					query = new TermRangeQuery(fieldName, minValue, maxValue, isIncludeMin, isIncludeMax);
				} else {
					logger.error("범위검색은 필드의 타입과 쿼리의 타입이 맞아야 합니다. {} " , userRequestQuery);
					throw new CrescentInvalidRequestException("범위검색은 필드의 타입과 쿼리의 타입이 맞아야 합니다. [" + userRequestQuery + "]");
				}
				
				resultQuery.add(query, occur);
				
			} else {
				//쿼리 생성..
				String[] keywords = userRequestQuery.split( " " );
				
				if(logger.isDebugEnabled()) {
					logger.debug("split keyword : {}", Arrays.toString(keywords));
				}
				
				for(int i = 0; i < keywords.length; i++) {
					ArrayList<String> analyzedTokenList = analyzedTokenList(analyzer, keywords[i]);

					if(!isAnalyzed || occur.equals(Occur.MUST_NOT) || occur.equals(Occur.MUST)  || analyzedTokenList.size() == 0) {
						
						Term t = new Term(fieldName, keywords[i]);
						Query query = new TermQuery(t);
						
						if(searchTargetField.getBoost() > 1F && boost > 1F) {
							query.setBoost(searchTargetField.getBoost() + boost);
						} else if (boost > 1F) {
							query.setBoost(boost);
						} else if (searchTargetField.getBoost() > 1F) {
							query.setBoost(searchTargetField.getBoost());
						}
						
						resultQuery.add(query, occur);
						
						logger.debug("query : {} ", query.toString());
						logger.debug("result query : {} ", resultQuery.toString());
						
					} else {
						
						for(String str : analyzedTokenList) {
							
							Term t = new Term(fieldName, str);
							Query query = new TermQuery(t);
							
							if(searchTargetField.getBoost() > 1F && boost > 1F) {
								query.setBoost(searchTargetField.getBoost() + boost);
							} else if (boost > 1F) {
								query.setBoost(boost);
							} else if (searchTargetField.getBoost() > 1F) {
								query.setBoost(searchTargetField.getBoost());
							}
							
							resultQuery.add(query, occur);
							
							logger.debug("query : {} ", query.toString());
							logger.debug("result query : {} ", resultQuery.toString());
						}
					}
				}
			}
		}
		
		if(regexQueryString != null && regexQueryString.length() > 0) {
			List<QueryAnalysisResult> regexQueryAnalysisResultList = getQueryAnalysisResults(regexQueryString);
			
			for(QueryAnalysisResult queryAnalysisResult : regexQueryAnalysisResultList) {
				Term term = new Term(queryAnalysisResult.getFieldName(), queryAnalysisResult.getUserQuery());
				Query regexQuery = new RegexQuery(term);
				
				logger.info("Regex Query : {}", regexQuery);
				
				resultQuery.add(regexQuery, queryAnalysisResult.getOccur());
			}
		}
		
		logger.info("result query : {} ", resultQuery.toString());
		
		this.resultQuery = resultQuery;
		
		return resultQuery;
	}
	
	private ArrayList<String> analyzedTokenList(Analyzer analyzer, String splitedKeyword) {
		Logger logger = LoggerFactory.getLogger(DefaultKeywordParser.class);
		
		ArrayList<String> rst = new ArrayList<String>();
		//split된 검색어를 Analyze..
		TokenStream stream = analyzer.tokenStream("", new StringReader(splitedKeyword));
		CharTermAttribute charTerm = stream.getAttribute(CharTermAttribute.class);
		

		try {
			stream.reset();
			
			while(stream.incrementToken()) {
				rst.add(charTerm.toString());
			}
			
		} catch (IOException e) {
			logger.error("error in DefaultKeywordParser : ", e);
			throw new RuntimeException(e);
		}

		logger.debug("[{}] 에서 추출된 명사 : [{}]", new String[]{splitedKeyword, rst.toString()});
			

		return rst;
	}
	
	private List<QueryAnalysisResult> getQueryAnalysisResults(String analysisTargetString) throws CrescentInvalidRequestException {
		List<QueryAnalysisResult> queryAnalysisResultList = new ArrayList<QueryAnalysisResult>();
		
		Matcher m = pattern.matcher(analysisTargetString);
		
		while(m.find()) {
			if(m.groupCount() != 3) {
				throw new CrescentInvalidRequestException("쿼리 문법 오류. [" + analysisTargetString + "]");
			}
			
			QueryAnalysisResult anaysisResult = new QueryAnalysisResult();
			
			Occur occur = Occur.SHOULD;
			String userRequestQuery = "";
			float boost = 0F;
			boolean isRangeQuery = false;
			
			String fieldName = m.group(1).trim();
			if(fieldName.startsWith("-")) {
				occur = Occur.MUST_NOT;
				fieldName = fieldName.substring(1);
			} else if (fieldName.startsWith("+")) {
				occur = Occur.MUST;
				fieldName = fieldName.substring(1);
			}
			
			userRequestQuery = m.group(3).trim().replaceAll("\"", "");
			if((userRequestQuery.startsWith("[") && userRequestQuery.endsWith("]")) 
					|| (userRequestQuery.startsWith("{") && userRequestQuery.endsWith("}"))) {
				
				isRangeQuery = true;
			
			}
			
			//boost 정보 추출
			int indexOfBoostSign = userRequestQuery.indexOf("^");
			if(indexOfBoostSign >= 0) {
				boost = Float.parseFloat(userRequestQuery.substring(indexOfBoostSign+1));
				userRequestQuery = userRequestQuery.substring(0, indexOfBoostSign);
			}
			
			logger.debug("user Request Query : {} ", userRequestQuery);
			logger.debug("boost : {} ", boost);
			
			anaysisResult.setFieldName(fieldName);
			anaysisResult.setBoost(boost);
			anaysisResult.setOccur(occur);
			anaysisResult.setRangeQuery(isRangeQuery);
			anaysisResult.setUserQuery(userRequestQuery);

			queryAnalysisResultList.add(anaysisResult);
		}
		
		return queryAnalysisResultList;
	}
	
	private class QueryAnalysisResult {
		
		private String fieldName;
		private String userQuery;
		private Occur occur = Occur.SHOULD;
		private float boost = 0F;
		private boolean isRangeQuery = false;
		
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		public String getUserQuery() {
			return userQuery;
		}
		public void setUserQuery(String userQuery) {
			this.userQuery = userQuery;
		}
		public Occur getOccur() {
			return occur;
		}
		public void setOccur(Occur occur) {
			this.occur = occur;
		}
		public float getBoost() {
			return boost;
		}
		public void setBoost(float boost) {
			this.boost = boost;
		}
		public boolean isRangeQuery() {
			return isRangeQuery;
		}
		public void setRangeQuery(boolean isRangeQuery) {
			this.isRangeQuery = isRangeQuery;
		}
	}
}
