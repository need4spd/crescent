package com.tistory.devyongsik.admin;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Token;

public interface MorphService {
	List<Token> getTokens(String keyword, boolean isIndexingMode) throws IOException;
}
