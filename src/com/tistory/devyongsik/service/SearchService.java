package com.tistory.devyongsik.service;

import java.io.IOException;

import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.domain.SearchResult;

public interface SearchService {
	SearchResult search(SearchRequest searchRequest) throws IOException;
}
