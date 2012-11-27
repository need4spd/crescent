package com.tistory.devyongsik.query;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.domain.CrescentCollectionField;

/**
 * Query 생성 클래스
 *
 * @author : 장용석, 2008. 12. 29., need4spd@google.com
 */

public class DefaultKeywordParser {

	private Logger logger = LoggerFactory.getLogger(DefaultKeywordParser.class);
	
	public Query parse(List<CrescentCollectionField> searchFields, String keyword, Analyzer analyzer) {
	
		logger.debug("search fields : {}", searchFields);
		
		BooleanQuery resultQuery = new BooleanQuery();

		//검색어를 split
		String[] keywords = keyword.split( " " );

		for(int i = 0; i < keywords.length; i++) {
			ArrayList<String> analyzedTokenList = analyzedTokenList(analyzer, keywords[i]);

			//필드만큼 돌아간다..
			for(CrescentCollectionField field : searchFields) {
				if(analyzedTokenList.size() == 0) { //색인되어 나온 것이 없으면
					Term t = new Term(field.getName(), keywords[i]);
					Query query = new TermQuery(t);
					if(field.getBoost() > 1F) {
						query.setBoost(field.getBoost());
					}
					resultQuery.add(query, Occur.SHOULD);
				} else {
					for(String str : analyzedTokenList) {
						
						Term t = new Term(field.getName(), str);
						Query query = new TermQuery(t);
						if(field.getBoost() > 1F) {
							query.setBoost(field.getBoost());
						}
						
						resultQuery.add(query, field.getOccur());
					}
				}
			}
		}

		//TODO 테스트 해볼 것
		logger.debug("검색 쿼리 : {}", resultQuery);

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

	final protected Query getWildcardQuery(String field, String term) throws ParseException {
		throw new ParseException("no use wild card");
	}

	final protected Query getFuzzyQuery(String field, String term) throws ParseException {
		throw new ParseException("no use fuzzy");
	}
}
