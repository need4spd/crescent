package com.tistory.devyongsik.search;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.query.CrescentRequestQueryStrParser;
import com.tistory.devyongsik.query.DefaultKeywordParser;

public class CrescentDefaultDocSearcher implements CrescentDocSearcher {

	private Logger logger = LoggerFactory.getLogger(CrescentDefaultDocSearcher.class);
	private CrescentRequestQueryStrParser crqp = null;
	
	private int totalHitsCount;
	
	public CrescentDefaultDocSearcher(CrescentRequestQueryStrParser crqp) {
		this.crqp = crqp;
	}

	public int getTotalHitsCount() {
		return totalHitsCount;
	}

	@Override
	public ScoreDoc[] search() throws IOException {
		
		int numOfHits = crqp.getStartOffSet() + crqp.getHitsForPage();
		
		TopScoreDocCollector collector = TopScoreDocCollector.create(numOfHits, true);
		IndexSearcher indexSearcher 
			= SearcherManager.getSearcherManager().getIndexSearcher(crqp.getCollectionName());
		
		DefaultKeywordParser keywordParser = new DefaultKeywordParser();
		Query query = keywordParser.parse(crqp, new KoreanAnalyzer(false));
		
		logger.debug("query : {}" , query);
		
		indexSearcher.search(query, collector);
		
		//전체 검색 건수
		totalHitsCount = collector.getTotalHits();
		
		logger.debug("Total Hits Count : {} ", totalHitsCount);
		
		//전체 검색 건수와 start off set 중 작은것을 end로 설정
		int end = Math.min(totalHitsCount, crqp.getStartOffSet() + crqp.getHitsForPage());
		
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		//실제 검색되어 나온 것이 더 적으면
		//
		if(end > hits.length) {
			logger.debug("기본 설정된 검색건수보다 더 검색을 원하므로, 전체를 대상으로 검색합니다.");
			
			collector = TopScoreDocCollector.create(totalHitsCount , true);
			indexSearcher.search(query, collector);
	        hits = collector.topDocs().scoreDocs;
		}

		
		logger.debug("start : [{}], end : [{}], total : [{}]"
						,new Object[]{crqp.getStartOffSet(), end, totalHitsCount});
		logger.debug("hits count : [{}]", hits.length);
		logger.debug("startOffset + hitsPerPage : [{}]", numOfHits);
		

		return hits;
	}
}
