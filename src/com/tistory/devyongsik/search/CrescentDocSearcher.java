package com.tistory.devyongsik.search;

import java.io.IOException;

import org.apache.lucene.search.ScoreDoc;

public interface CrescentDocSearcher {

	ScoreDoc[] search() throws IOException;
	int getTotalHitsCount();
}
