package com.tistory.devyongsik.crescent.admin.service;

import javax.servlet.http.HttpServletRequest;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;

public interface CollectionManageService {
	
	CrescentCollection updateCollectionInfo(HttpServletRequest request);
	CrescentCollection addCollectionInfo(HttpServletRequest request);
	void deleteCollectionInfo(String collectionName);
}
