package com.tistory.devyongsik.crescent.search.service;

import java.io.IOException;

import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.crescent.search.entity.SearchResult;

public interface SearchService {
	SearchResult search(SearchRequest searchRequest) throws IOException;
}
