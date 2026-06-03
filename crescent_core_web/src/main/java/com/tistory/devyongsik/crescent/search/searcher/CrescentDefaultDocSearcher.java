package com.tistory.devyongsik.crescent.search.searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollectionField;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.config.SpringApplicationContext;
import com.tistory.devyongsik.crescent.logger.CrescentLogger;
import com.tistory.devyongsik.crescent.logger.LogInfo;
import com.tistory.devyongsik.crescent.query.CrescentSearchRequestWrapper;
import com.tistory.devyongsik.crescent.search.entity.SearchResult;
import com.tistory.devyongsik.crescent.search.highlight.CrescentFastVectorHighlighter;

@Component("crescentDefaultDocSearcher")
public class CrescentDefaultDocSearcher implements CrescentDocSearcher {

	private Logger logger = LoggerFactory.getLogger(CrescentDefaultDocSearcher.class);

	@Autowired
	@Qualifier("crescentSearcherManager")
	private CrescentSearcherManager crescentSearcherManager;

	@Override
	public SearchResult search(CrescentSearchRequestWrapper csrw) throws IOException {

		SearchResult searchResult = new SearchResult();
		int totalHitsCount = 0;
		String errorMessage = "SUCCESS";
		int errorCode = 0;

		int numOfHits = csrw.getDefaultHitsPage() * csrw.getHitsForPage();
		IndexSearcher indexSearcher = null;
		SearcherManager searcherManager = null;

		try {
			searcherManager = crescentSearcherManager.getSearcherManager(csrw.getCollectionName());
			if (searcherManager == null) {
				throw new IllegalStateException("컬렉션을 찾을 수 없습니다: " + csrw.getCollectionName());
			}
			indexSearcher = searcherManager.acquire();

			Query query = csrw.getQuery();
			Query filterQuery = csrw.getFilterQuery();
			Sort sort = csrw.getSort();

			// filter를 BooleanQuery의 FILTER 절로 통합
			Query effectiveQuery = query;
			if(filterQuery != null) {
				effectiveQuery = new BooleanQuery.Builder()
						.add(query, Occur.MUST)
						.add(filterQuery, Occur.FILTER)
						.build();
			}

			logger.debug("query : {}" , effectiveQuery);
			logger.debug("sort : {}" , sort);

			long startTime = System.currentTimeMillis();
			TopDocs topDocs = null;

			if(sort == null) {
				topDocs = indexSearcher.search(effectiveQuery, numOfHits);
			} else {
				topDocs = indexSearcher.search(effectiveQuery, numOfHits, sort);
			}

			long endTime = System.currentTimeMillis();

			totalHitsCount = (int) topDocs.totalHits.value;

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
			logInfo.setFilter(filterQuery);

			CrescentLogger.logging(logInfo);

			logger.debug("Total Hits Count : {} ", totalHitsCount);

			ScoreDoc[] hits = topDocs.scoreDocs;

			int endOffset = Math.min(totalHitsCount, csrw.getStartOffSet() + csrw.getHitsForPage());

			if(endOffset > hits.length) {
				logger.debug("기본 설정된 검색건수보다 더 검색을 원하므로, 전체를 대상으로 검색합니다.");

				if(sort == null) {
					topDocs = indexSearcher.search(effectiveQuery, totalHitsCount);
				} else {
					topDocs = indexSearcher.search(effectiveQuery, totalHitsCount, sort);
				}

		        hits = topDocs.scoreDocs;
			}

			int startOffset = csrw.getStartOffSet();
			endOffset = Math.min(hits.length, startOffset + csrw.getHitsForPage());

			logger.debug("start offset : [{}], end offset : [{}], total : [{}], numOfHits :[{}]"
							,new Object[]{csrw.getStartOffSet(), endOffset, totalHitsCount, numOfHits});
			logger.debug("hits count : [{}]", hits.length);
			logger.debug("startOffset + hitsPerPage : [{}]", csrw.getStartOffSet() + csrw.getHitsForPage());


			if(totalHitsCount > 0) {
				List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
				Map<String, Object> result = new HashMap<String, Object>();

				CrescentFastVectorHighlighter highlighter = new CrescentFastVectorHighlighter();

				CrescentCollectionHandler collectionHandler
				= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
				CrescentCollection collection = collectionHandler.getCrescentCollections().getCrescentCollection(csrw.getCollectionName());

				for(int i = startOffset; i < endOffset; i++) {

					Map<String,String> resultMap = new HashMap<String, String>();

					for(CrescentCollectionField field : collection.getFields()) {
						String value = null;

						if(field.isStore() && !field.isNumeric()) {

							value = highlighter.getBestFragment(indexSearcher.getIndexReader(), hits[i].doc, query, field.getName());

						}

						if(value == null || value.length() == 0) {
							Document doc = indexSearcher.storedFields().document(hits[i].doc);
							value = doc.get(field.getName());
						}

						resultMap.put(field.getName(), value);
					}

					resultList.add(resultMap);
				}

				result.put("total_count", totalHitsCount);
				result.put("result_list", resultList);
				result.put("error_code", errorCode);
				result.put("error_msg", errorMessage);

				logger.debug("result list {}", resultList);

				searchResult.setResultList(resultList);
				searchResult.setTotalHitsCount(totalHitsCount);
				searchResult.setSearchResult(result);

			} else {

				Map<String, Object> result = new HashMap<String, Object>();
				List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();

				result.put("total_count", totalHitsCount);
				result.put("result_list", resultList);
				result.put("error_code", errorCode);
				result.put("error_msg", errorMessage);

				logger.debug("result list {}", resultList);

				searchResult.setResultList(resultList);
				searchResult.setTotalHitsCount(0);
				searchResult.setSearchResult(result);

			}


		} catch (Exception e) {

			logger.error("검색 중 오류 발생: ", e);

			Map<String, Object> result = new HashMap<String, Object>();
			List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();

			result.put("total_count", 0);
			result.put("result_list", resultList);
			result.put("error_code", 1);
			result.put("error_msg", "검색 중 오류가 발생하였습니다.");

			searchResult.setErrorCode(1);
			searchResult.setErrorMsg("검색 중 오류가 발생하였습니다.");
			searchResult.setSearchResult(result);
			searchResult.setResultList(resultList);

			return searchResult;

		} finally {
			if (searcherManager != null && indexSearcher != null) {
				searcherManager.release(indexSearcher);
				indexSearcher = null;
			}
		}

		return searchResult;
	}
}
