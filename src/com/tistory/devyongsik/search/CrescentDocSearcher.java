package com.tistory.devyongsik.search;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;

public interface CrescentDocSearcher {

	List<Document> search() throws IOException;
	int getTotalHitsCount();
	String getErrorMessage();
	int getErrorCode();
}
