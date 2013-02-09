package com.tistory.devyongsik.search;

import java.io.IOException;

import com.tistory.devyongsik.domain.SearchResult;
import com.tistory.devyongsik.query.CrescentSearchRequestWrapper;

public interface CrescentDocSearcher {
	SearchResult search(CrescentSearchRequestWrapper csrw) throws IOException;
}
