package com.tistory.devyongsik.handler;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;

import com.tistory.devyongsik.domain.CollectionField;

public interface Handler {

	List<Document> handledData(File dataSourceFile, Map<String, CollectionField> fieldsByName);
}
