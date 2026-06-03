package com.tistory.devyongsik.crescent.query;

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
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.AutomatonQuery;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.RegExp;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollectionField;
import com.tistory.devyongsik.crescent.search.exception.CrescentInvalidRequestException;

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

		BooleanQuery.Builder builder = new BooleanQuery.Builder();

		CrescentCollectionField searchTargetField = null;

		String fieldName = "";
		Occur occur = Occur.SHOULD;
		String userRequestQuery = "";
		float boost = 0F;

		boolean isRangeQuery = false;

		for(QueryAnalysisResult queryAnalysisResult : queryAnalysisResultList) {

			boolean any = true;
			boolean isLongField = false;
			boolean isAnalyzed = false;

			fieldName = queryAnalysisResult.getFieldName();
			occur = queryAnalysisResult.getOccur();
			userRequestQuery = queryAnalysisResult.getUserQuery();
			boost = queryAnalysisResult.getBoost();
			isRangeQuery = queryAnalysisResult.isRangeQuery();

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

			if(isRangeQuery) {
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

				boolean isNumeric = StringUtils.isNumeric(minValue) && StringUtils.isNumeric(maxValue);

				logger.debug("isLongField : {}", isLongField);
				logger.debug("is numeric : {}", isNumeric);

				Query query = null;

				if(isAnalyzed) {
					logger.error("범위검색 대상 field는 analyzed값이 false이어야 합니다. {} " , userRequestQuery);
					throw new CrescentInvalidRequestException("범위검색 대상 field는 analyzed값이 false이어야 합니다. [" + userRequestQuery + "]");
				}

				if(isLongField && isNumeric) {
					long min = isIncludeMin ? Long.parseLong(minValue) : Long.parseLong(minValue) + 1;
					long max = isIncludeMax ? Long.parseLong(maxValue) : Long.parseLong(maxValue) - 1;
					query = LongField.newRangeQuery(fieldName, min, max);

				} else if (!(isLongField && isNumeric)){

					BytesRef minValBytes = new BytesRef(minValue);
					BytesRef maxValBytes = new BytesRef(maxValue);

					query = new TermRangeQuery(fieldName, minValBytes, maxValBytes, isIncludeMin, isIncludeMax);

				} else {
					logger.error("범위검색은 필드의 타입과 쿼리의 타입이 맞아야 합니다. {} " , userRequestQuery);
					throw new CrescentInvalidRequestException("범위검색은 필드의 타입과 쿼리의 타입이 맞아야 합니다. [" + userRequestQuery + "]");
				}

				builder.add(query, occur);

			} else {
				String[] keywords = userRequestQuery.split( " " );

				if(logger.isDebugEnabled()) {
					logger.debug("split keyword : {}", Arrays.toString(keywords));
				}

				for(int i = 0; i < keywords.length; i++) {
					ArrayList<String> analyzedTokenList = analyzedTokenList(analyzer, keywords[i]);

					if(!isAnalyzed || analyzedTokenList.size() == 0) {

						Term t = new Term(fieldName, keywords[i]);
						Query query = new TermQuery(t);
						query = applyBoost(query, searchTargetField.getBoost(), boost);

						builder.add(query, occur);

						logger.debug("query : {} ", query.toString());

					} else {

						for(String str : analyzedTokenList) {

							Term t = new Term(fieldName, str);
							Query query = new TermQuery(t);
							query = applyBoost(query, searchTargetField.getBoost(), boost);

							builder.add(query, occur);

							logger.debug("query : {} ", query.toString());
						}
					}
				}
			}
		}

		if(regexQueryString != null && regexQueryString.length() > 0) {
			List<QueryAnalysisResult> regexQueryAnalysisResultList = getQueryAnalysisResults(regexQueryString);

			for(QueryAnalysisResult queryAnalysisResult : regexQueryAnalysisResultList) {
				Term term = new Term(queryAnalysisResult.getFieldName(), queryAnalysisResult.getUserQuery());
				Automaton automaton = new RegExp(queryAnalysisResult.getUserQuery()).toAutomaton();
				Query regexQuery = new AutomatonQuery(term, automaton);

				logger.info("Regex Query : {}", regexQuery);

				builder.add(regexQuery, queryAnalysisResult.getOccur());
			}
		}

		BooleanQuery resultQuery = builder.build();
		logger.info("result query : {} ", resultQuery.toString());

		this.resultQuery = resultQuery;

		return resultQuery;
	}

	private Query applyBoost(Query query, float fieldBoost, float boost) {
		float effectiveBoost = 0F;
		if(fieldBoost > 1F && boost > 1F) {
			effectiveBoost = fieldBoost + boost;
		} else if (boost > 1F) {
			effectiveBoost = boost;
		} else if (fieldBoost > 1F) {
			effectiveBoost = fieldBoost;
		}
		if(effectiveBoost > 0F) {
			return new BoostQuery(query, effectiveBoost);
		}
		return query;
	}

	private ArrayList<String> analyzedTokenList(Analyzer analyzer, String splitedKeyword) {
		ArrayList<String> rst = new ArrayList<String>();
		try (TokenStream stream = analyzer.tokenStream("", new StringReader(splitedKeyword))) {
			CharTermAttribute charTerm = stream.getAttribute(CharTermAttribute.class);
			stream.reset();
			while (stream.incrementToken()) {
				rst.add(charTerm.toString());
			}
			stream.end();
		} catch (IOException e) {
			logger.error("error in analyzedTokenList : ", e);
			throw new RuntimeException(e);
		}
		logger.debug("[{}] 에서 추출된 명사 : [{}]", splitedKeyword, rst.toString());
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
