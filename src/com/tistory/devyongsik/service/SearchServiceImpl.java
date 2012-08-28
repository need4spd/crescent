package com.tistory.devyongsik.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.query.CrescentRequestQueryStrParser;
import com.tistory.devyongsik.query.DefaultKeywordParser;
import com.tistory.devyongsik.search.SearcherManager;

public class SearchServiceImpl implements SearchService {

	private Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
	
	@Override
	public List<Document> search(SearchRequest searchRequest) throws IOException {
		CrescentRequestQueryStrParser crescentRequestQueryStrParser 
			= new CrescentRequestQueryStrParser(searchRequest);
		
		DefaultKeywordParser keywordParser = new DefaultKeywordParser();
		Query query = keywordParser.parse(crescentRequestQueryStrParser, new KoreanAnalyzer(false));
		
		logger.debug("query : {}" , query);
		
		IndexSearcher indexSearcher 
			= SearcherManager.getSearcherManager().getIndexSearcher(searchRequest.getCollectionName());
		TopDocs topDocs = indexSearcher.search(query, crescentRequestQueryStrParser.getHitsForPage());
		
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		logger.debug("total count : "+ topDocs.totalHits);
		
		List<Document> searchResultList = new ArrayList<Document>();
		
		for(int i = 0; i < scoreDocs.length; i++) {
			Document doc = indexSearcher.doc(scoreDocs[i].doc);
			searchResultList.add(doc);
		}
		
		logger.debug("searchResultList : {}" , searchResultList);
		
		
		return searchResultList;
	}

}
