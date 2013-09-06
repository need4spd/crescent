package com.tistory.devyongsik.crescent.admin.service;

import com.tistory.devyongsik.crescent.admin.entity.HighFreqTermResult;
import com.tistory.devyongsik.crescent.admin.entity.IndexInfo;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;

import java.io.IOException;

public interface IndexFileManageService {
	public HighFreqTermResult reload(CrescentCollection selectCollection) throws Exception;
	public HighFreqTermResult reload(CrescentCollection selectCollection, int docNum);
	public IndexInfo getIndexInfo(CrescentCollection selectCollection) throws IOException;
	
}
