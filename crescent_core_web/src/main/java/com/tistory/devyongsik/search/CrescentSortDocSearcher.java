package com.tistory.devyongsik.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NRTManager;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopFieldDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.logger.CrescentLogger;
import com.tistory.devyongsik.logger.LogInfo;
import com.tistory.devyongsik.query.CrescentSearchRequestWrapper;
import com.tistory.devyongsik.query.DefaultKeywordParser;

public class CrescentSortDocSearcher implements CrescentDocSearcher {

	private Logger logger = LoggerFactory.getLogger(CrescentSortDocSearcher.class);
	private CrescentSearchRequestWrapper csrw = null;
	
	private int totalHitsCount;
	private String errorMessage = "SUCCESS";
	private int errorCode;
	
	public CrescentSortDocSearcher(CrescentSearchRequestWrapper csrw) {
		this.csrw = csrw;
	}

	public int getTotalHitsCount() {
		return totalHitsCount;
	}

	@Override
	public List<Document> search() throws IOException {
		
		//5page * 50
		int numOfHits = csrw.getDefaultHitsPage() * csrw.getHitsForPage();
		
		IndexSearcher indexSearcher = null;
		NRTManager nrtManager = CrescentSearcherManager.getCrescentSearcherManager().getSearcherManager(csrw.getCollectionName());
		List<Document> resultList = new ArrayList<Document>();
		
		try {
			
			indexSearcher = nrtManager.acquire();
			
			Sort sort = csrw.getSort();

			logger.debug("sort : {}", sort);
			
			DefaultKeywordParser keywordParser = new DefaultKeywordParser();
			Query query = keywordParser.parse(csrw.getCollectionName()
					,csrw.getSearchFieldNames()
					,csrw.getKeyword()
					, new KoreanAnalyzer(false));
			
			long startTime = System.currentTimeMillis();
			TopFieldDocs tfd = indexSearcher.search(query,null,numOfHits,sort);
			long endTime = System.currentTimeMillis();
			
			logger.debug("query : {}" , query);
					
			//전체 검색 건수
			totalHitsCount = tfd.totalHits;
			
			LogInfo logInfo = new LogInfo();
			logInfo.setCollectionName(csrw.getCollectionName());
			logInfo.setElaspedTimeMil(endTime - startTime);
			logInfo.setKeyword(csrw.getKeyword());
			logInfo.setPageNum(csrw.getPageNum());
			logInfo.setPcid(csrw.getPcId());
			logInfo.setQuery(query);
			logInfo.setSort(csrw.getSort());
			logInfo.setTotalCount(totalHitsCount);
			logInfo.setUserId(csrw.getUserId());
			logInfo.setUserIp(csrw.getUserIp());
			
			CrescentLogger.logging(logInfo);
			
			logger.debug("Total Hits Count : {} ", totalHitsCount);
			
			ScoreDoc[] hits = tfd.scoreDocs;
			
			//총 검색건수와 실제 보여줄 document의 offset (min ~ max)를 비교해서 작은 것을 가져옴
			int endOffset = Math.min(totalHitsCount, csrw.getStartOffSet() + csrw.getHitsForPage());
			
			if(endOffset > hits.length) {
				logger.debug("기본 설정된 검색건수보다 더 검색을 원하므로, 전체를 대상으로 검색합니다.");
				
				tfd = indexSearcher.search(query,null,totalHitsCount,sort);
		        hits = tfd.scoreDocs;
			}
	
			int startOffset = csrw.getStartOffSet();
			endOffset = Math.min(hits.length, startOffset + csrw.getHitsForPage());
									
			for(int i = startOffset; i < endOffset; i++) {
				Document doc = indexSearcher.doc(hits[i].doc);
				resultList.add(doc);
			}
			
			logger.debug("start : [{}], end : [{}], total : [{}]"
							,new Object[]{csrw.getStartOffSet(), endOffset, totalHitsCount});
			logger.debug("hits count : [{}]", hits.length);
			logger.debug("startOffset + hitsPerPage : [{}]", numOfHits);
			
		} catch (Exception e) {
			
			logger.error("error in CrescentDefaultDocSearcher : {} ", e);
			
			errorMessage = e.toString();
			errorCode = -1;
			
		} finally {
			
			nrtManager.release(indexSearcher);
			indexSearcher = null;
		}
		

		return resultList;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public int getErrorCode() {
		return errorCode;
	}

}
