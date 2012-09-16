package com.tistory.devyongsik.admin;

import java.util.List;

import com.tistory.devyongsik.analyzer.dictionary.DictionaryType;

public interface DictionaryService {
	public List<String> getDictionary(DictionaryType dicType);
	public void addWordToDictionary (DictionaryType dicType, String word);
	public void removeWordFromDictionary (DictionaryType dicType, String word);
	public String findWordFromDictionary (DictionaryType dicType, String word);
	public void writeToDictionaryFile(DictionaryType dicType);
}
