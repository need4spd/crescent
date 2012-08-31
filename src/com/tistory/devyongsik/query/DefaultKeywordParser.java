package com.tistory.devyongsik.query;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

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

import com.tistory.devyongsik.config.CollectionConfig;
import com.tistory.devyongsik.domain.Collection;
import com.tistory.devyongsik.domain.CollectionField;

/**
 * Query 생성 클래스
 *
 * @author : 장용석, 2008. 12. 29., need4spd@google.com
 */

public class DefaultKeywordParser {

	private Logger logger = LoggerFactory.getLogger(DefaultKeywordParser.class);
	
	public Query parse(String collectionName, String[] fieldNames, String keyword, Analyzer a) {
		Collection collection = CollectionConfig.getInstance().getCollection(collectionName);
		
		logger.debug("search fields : {}", Arrays.toString(fieldNames));
		

		//검색대상 필드를 가져온다.
		CollectionField[] fields = new CollectionField[fieldNames.length];
		for(int idx = 0; idx < fieldNames.length; idx++) {
			fields[idx] = collection.getFieldsByName().get(fieldNames[idx].trim());
		}

		BooleanQuery resultQuery = new BooleanQuery();

		//검색어를 split
		String[] keywords = keyword.split( " " );


		for(int i = 0; i < keywords.length; i++) {
			ArrayList<String> analyzedTokenList = analyzedTokenList(a, keywords[i]);

			//필드만큼 돌아간다..
			for(int j = 0; j < fields.length; j++) {
				if(analyzedTokenList.size() == 0) { //색인되어 나온 것이 없으면
					Term t = new Term(fields[j].getName(), keywords[i]);
					Query query = new TermQuery(t);
					query.setBoost(fields[j].getFieldBoost());
					resultQuery.add(query, Occur.SHOULD);
				} else {
					int keySeq = 0; //처음 분석되어 나온 키워드를 찾기 위해..
					for(String str : analyzedTokenList) {
						Term t = new Term(fields[j].getName(), str);
						Query query = new TermQuery(t);
						query.setBoost(fields[j].getFieldBoost());
						resultQuery.add(query, (keySeq == 0) ? Occur.SHOULD : fields[j].getOccur());
						keySeq++;
					}
				}
			}
		}

		//TODO 테스트 해볼 것
		logger.debug("검색 쿼리 : {}", resultQuery);

		return resultQuery;
	}

	private ArrayList<String> analyzedTokenList(Analyzer a, String splitedKeyword) {
		Logger logger = LoggerFactory.getLogger(DefaultKeywordParser.class);
		
		ArrayList<String> rst = new ArrayList<String>();
		//split된 검색어를 Analyze..
		TokenStream stream = a.tokenStream("", new StringReader(splitedKeyword));
		CharTermAttribute charTerm = stream.getAttribute(CharTermAttribute.class);

		try {
			while(stream.incrementToken()) {
				rst.add(charTerm.toString());
			}
		} catch (IOException e) {
			logger.error("error : " + e);
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
