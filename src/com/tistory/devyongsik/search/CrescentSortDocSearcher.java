package com.tistory.devyongsik.search;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopFieldDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.query.CrescentRequestQueryStrParser;
import com.tistory.devyongsik.query.DefaultKeywordParser;

public class CrescentSortDocSearcher implements CrescentDocSearcher {

	private Logger logger = LoggerFactory.getLogger(CrescentSortDocSearcher.class);
	private CrescentRequestQueryStrParser crqp = null;
	
	private int totalHitsCount;
	
	public CrescentSortDocSearcher(CrescentRequestQueryStrParser crqp) {
		this.crqp = crqp;
	}

	public int getTotalHitsCount() {
		return totalHitsCount;
	}

	@Override
	public ScoreDoc[] search() throws IOException {
		
		//5page * 50
		int numOfHits = crqp.getDefaultHitsPage() * crqp.getHitsForPage();
		
		Sort sort = crqp.getSort();
		
		logger.debug("sort : {}", sort);
		
		IndexSearcher indexSearcher 
			= SearcherManager.getSearcherManager().getIndexSearcher(crqp.getCollectionName());
		
		DefaultKeywordParser keywordParser = new DefaultKeywordParser();
		Query query = keywordParser.parse(crqp.getCollectionName()
				,crqp.getSearchFieldNames()
				,crqp.getKeyword()
				, new KoreanAnalyzer(false));
		
		TopFieldDocs tfd = indexSearcher.search(query,null,numOfHits,sort);
		
		logger.debug("query : {}" , query);
				
		//전체 검색 건수
		totalHitsCount = tfd.totalHits;
		
		logger.debug("Total Hits Count : {} ", totalHitsCount);
		
		ScoreDoc[] hits = tfd.scoreDocs;
		
		//총 검색건수와 실제 보여줄 document의 offset (min ~ max)를 비교해서 작은 것을 가져옴
		int end = Math.min(totalHitsCount, crqp.getStartOffSet() + crqp.getHitsForPage());
		
		if(end > hits.length) {
			logger.debug("기본 설정된 검색건수보다 더 검색을 원하므로, 전체를 대상으로 검색합니다.");
			
			tfd = indexSearcher.search(query,null,totalHitsCount,sort);
	        hits = tfd.scoreDocs;
		}

		
		logger.debug("start : [{}], end : [{}], total : [{}]"
						,new Object[]{crqp.getStartOffSet(), end, totalHitsCount});
		logger.debug("hits count : [{}]", hits.length);
		logger.debug("startOffset + hitsPerPage : [{}]", numOfHits);
		

		return hits;
	}

}
