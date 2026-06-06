package com.tistory.devyongsik.crescent.admin.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.dictionary.CrescentDictionaryType;
import com.tistory.devyongsik.crescent.dictionary.CrescentNoriDictionary;

/**
 * Nori 사용자 사전 관리 서비스.
 *
 * {@link CrescentNoriDictionary} 싱글톤에 단어 추가/삭제/검색/파일 저장을 위임하고,
 * 사전 재빌드 시 모든 컬렉션의 분석기를 재생성해 변경 내용이 검색/형태소분석에 반영되도록 한다.
 */
@Service("dictionaryService")
public class DictionaryServiceImpl implements DictionaryService {

	private Logger logger = LoggerFactory.getLogger(DictionaryServiceImpl.class);

	@Autowired
	@Qualifier("crescentCollectionHandler")
	private CrescentCollectionHandler collectionHandler;

	private CrescentNoriDictionary dictionary() {
		return CrescentNoriDictionary.getInstance();
	}

	@Override
	public List<String> getDictionary(CrescentDictionaryType dicType) {
		return dictionary().getWords(dicType);
	}

	@Override
	public void addWordToDictionary(CrescentDictionaryType dicType, String word) {
		dictionary().addWord(dicType, word);
	}

	@Override
	public void removeWordFromDictionary(CrescentDictionaryType dicType, String word) {
		dictionary().removeWord(dicType, word);
	}

	@Override
	public List<String> findWordFromDictionary(CrescentDictionaryType dicType, String word) {
		List<String> result = dictionary().findWords(dicType, word);
		logger.debug("findWordFromDictionary result : {}", result);
		return result;
	}

	@Override
	public void writeToDictionaryFile(CrescentDictionaryType dicType) {
		dictionary().writeToFile(dicType);
	}

	@Override
	public void rebuildDictionary(CrescentDictionaryType dictionaryType) {
		dictionary().rebuild(dictionaryType);
		// 사전이 바뀌면 분석기를 새 인스턴스로 교체해야 검색/형태소분석에 반영된다.
		collectionHandler.rebuildAnalyzers();
	}
}
