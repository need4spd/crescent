package com.tistory.devyongsik.crescent.admin.service;

import java.util.List;

import com.tistory.devyongsik.crescent.dictionary.CrescentDictionaryType;

public interface DictionaryService {
	public List<String> getDictionary(CrescentDictionaryType dicType);
	public void addWordToDictionary (CrescentDictionaryType dicType, String word);
	public void removeWordFromDictionary (CrescentDictionaryType dicType, String word);
	public List<String> findWordFromDictionary (CrescentDictionaryType dicType, String word);
	public void writeToDictionaryFile(CrescentDictionaryType dicType);
	public void rebuildDictionary(CrescentDictionaryType dictionaryType);
}
