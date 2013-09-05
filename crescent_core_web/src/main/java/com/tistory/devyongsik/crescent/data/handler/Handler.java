package com.tistory.devyongsik.crescent.data.handler;

import com.tistory.devyongsik.crescent.index.entity.IndexingRequestForm;


public interface Handler {

	IndexingRequestForm handledData(String jsonFormStr);
}
