package com.tistory.devyongsik.admin;

import javax.servlet.http.HttpServletRequest;

import com.tistory.devyongsik.domain.CrescentCollection;

public interface CollectionManageService {
	
	CrescentCollection updateCollectionInfo(HttpServletRequest request);
	CrescentCollection addCollectionInfo(HttpServletRequest request);
	void deleteCollectionInfo(String collectionName);
}
