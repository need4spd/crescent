package com.tistory.devyongsik.crescent.search.searcher;

import java.io.IOException;

import com.tistory.devyongsik.crescent.query.CrescentSearchRequestWrapper;
import com.tistory.devyongsik.crescent.search.entity.SearchResult;

public interface CrescentDocSearcher {
	SearchResult search(CrescentSearchRequestWrapper csrw) throws IOException;
}
