package com.tistory.devyongsik.crescent.admin.service;

import java.io.IOException;

import com.tistory.devyongsik.crescent.admin.entity.IndexInfo;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;

public interface IndexFileManageService {
	public IndexInfo getIndexInfo(CrescentCollection selectCollection) throws IOException;
}
