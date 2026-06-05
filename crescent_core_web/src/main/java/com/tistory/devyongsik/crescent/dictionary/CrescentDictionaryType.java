package com.tistory.devyongsik.crescent.dictionary;

/**
 * Crescent 사전 종류.
 *
 * Nori 분석기에서 사용하는 사용자 사전을 종류별로 구분한다.
 * 기존 korean-analyzer-4.x의 DictionaryType을 대체한다.
 */
public enum CrescentDictionaryType {

	/** 명사사전 (사용자 명사) → Nori UserDictionary */
	CUSTOM("custom.txt", "명사사전"),

	/** 복합명사사전 (복합어:분해1,분해2) → Nori UserDictionary 분해 규칙 */
	COMPOUND("compounds.txt", "복합명사사전"),

	/** 불용어사전 → StopFilter */
	STOP("stop.txt", "불용어사전"),

	/** 동의어사전 (a,b,c) → SynonymGraphFilter */
	SYNONYM("synonym.txt", "동의어사전");

	private final String fileName;
	private final String description;

	CrescentDictionaryType(String fileName, String description) {
		this.fileName = fileName;
		this.description = description;
	}

	public String getFileName() {
		return fileName;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * 관리자 화면의 dicType 파라미터(noun/stop/syn/compound)를 enum으로 변환한다.
	 */
	public static CrescentDictionaryType fromDicType(String dicType) {
		if ("noun".equals(dicType)) {
			return CUSTOM;
		} else if ("stop".equals(dicType)) {
			return STOP;
		} else if ("syn".equals(dicType)) {
			return SYNONYM;
		} else if ("compound".equals(dicType)) {
			return COMPOUND;
		}
		return null;
	}
}
