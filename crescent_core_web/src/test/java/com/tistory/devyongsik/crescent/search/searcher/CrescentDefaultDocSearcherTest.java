package com.tistory.devyongsik.crescent.search.searcher;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import junit.framework.Assert;

import org.junit.Test;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.data.handler.JsonDataHandler;
import com.tistory.devyongsik.crescent.index.entity.IndexingRequestForm;
import com.tistory.devyongsik.crescent.query.CrescentSearchRequestWrapper;
import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.crescent.search.entity.SearchResult;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;

public class CrescentDefaultDocSearcherTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() {
		super.init();
	}

	@Test
	public void search() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("1");
		searchRequest.setCollectionName("sample");

		CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest);

		SearchResult searchResult = crescentDocSearcher.search(csrw);

		Assert.assertTrue(searchResult.getResultList().size() > 0);
	}

	@Test
	public void successSearchResultHasZeroErrorCode() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("제목");
		searchRequest.setCollectionName("sample");

		CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest);
		SearchResult searchResult = crescentDocSearcher.search(csrw);

		Map<String, Object> result = searchResult.getSearchResult();
		Assert.assertEquals(0, result.get("error_code"));
		Assert.assertEquals("SUCCESS", result.get("error_msg"));
	}

	@Test
	public void zeroResultSearchHasZeroErrorCode() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("존재하지않는키워드xyz");
		searchRequest.setCollectionName("sample");

		CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest);
		SearchResult searchResult = crescentDocSearcher.search(csrw);

		Assert.assertEquals(0, searchResult.getTotalHitsCount());
		Map<String, Object> result = searchResult.getSearchResult();
		Assert.assertEquals(0, result.get("error_code"));
		Assert.assertEquals("SUCCESS", result.get("error_msg"));
	}

	@Test
	public void searchLoanwordWithJosaReturnsResults() throws IOException {
		// dscr에 "파이썬은"으로 색인 → 분석기가 조사 분리 못해 "파이썬" 검색 시 dscr 미매칭
		// dscr must=false 변경 후 title 매칭만으로 결과 반환되는지 확인
		String testData = "{\"command\":\"add\",\"indexingType\":\"bulk\",\"documentList\":"
			+ "[{\"title\":\"파이썬 프로그래밍 입문\","
			+ "\"dscr\":\"파이썬은 쉽고 강력한 프로그래밍 언어입니다\","
			+ "\"creuser\":\"test\",\"board_id\":\"99\"}]}";

		IndexingRequestForm form = new JsonDataHandler().handledData(testData);
		CrescentCollection collection = collectionHandler.getCrescentCollections().getCrescentCollection("sample");
		executor.indexing(collection, form);

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("파이썬");
		searchRequest.setCollectionName("sample");

		CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest);
		SearchResult searchResult = crescentDocSearcher.search(csrw);

		Assert.assertTrue(
			"dscr must=false 설정 후 외래어+조사 문서를 title 매칭으로 검색할 수 있어야 합니다",
			searchResult.getTotalHitsCount() > 0);
	}
}
