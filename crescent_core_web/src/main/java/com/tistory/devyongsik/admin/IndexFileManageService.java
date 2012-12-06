package com.tistory.devyongsik.admin;

import java.util.Map;

public interface IndexFileManageService {
	public Map<String, Object> getResult();
	public boolean reload(String collectionName);
	public boolean reload(String collectionName, int docNum);
	public boolean reload(String collectionName, String field, String query);
	
}
