package com.tistory.devyongsik.service;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;

import com.tistory.devyongsik.domain.SearchRequest;

public interface SearchService {
	List<Document> search(SearchRequest searchRequest) throws IOException;
}
