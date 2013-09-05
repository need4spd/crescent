package com.tistory.devyongsik.crescent.admin.service;

import java.io.IOException;
import java.util.List;

import com.tistory.devyongsik.crescent.admin.entity.MorphToken;

public interface MorphService {
	List<MorphToken> getTokens(String keyword, boolean isIndexingMode, String collectionName) throws IOException;
}
