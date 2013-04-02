package com.tistory.devyongsik.admin;

import java.io.IOException;
import java.util.List;

import com.tistory.devyongsik.domain.MorphToken;

public interface MorphService {
	List<MorphToken> getTokens(String keyword, boolean isIndexingMode, String collectionName) throws IOException;
}
