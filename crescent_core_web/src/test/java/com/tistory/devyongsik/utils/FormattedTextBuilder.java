package com.tistory.devyongsik.utils;

public class FormattedTextBuilder {

	public static String getAddDocBulkJsonForm() {
		return "{\"command\":\"add\", \"indexingType\":\"bulk\", \"documentList\":[{\"board_id\":\"0\",\"title\":\"제목 입니다0\",\"dscr\":\"본문 입니다.0\"," +
				"\"creuser\":\"test\"}]}";
	}
	
	public static String getAddDocIncJsonForm() {
		return "{\"command\":\"add\", \"indexingType\":\"incremental\", \"documentList\":[{\"board_id\":\"0\",\"title\":\"제목 입니다0\",\"dscr\":\"본문 입니다.0\"," +
				"\"creuser\":\"test\"}]}";
	}
	
	public static String getDeleteDocBulkJsonForm() {
		return "{\"command\":\"delete\", \"indexingType\":\"bulk\",\"query\":\"creuser:test\"}";
	}
	
	public static String getDeleteDocIncJsonForm() {
		return "{\"command\":\"delete\", \"indexingType\":\"incremental\",\"query\":\"creuser:test\"}";
	}
	
	public static String getUpdateDocBulkJsonForm() {
		return "{\"command\":\"update\", \"indexingType\":\"bulk\", \"documentList\":[{\"board_id\":\"0\",\"title\":\"제목 입니다0 업데이트...\",\"dscr\":\"본문 입니다.0\"," +
				"\"creuser\":\"test\"}], \"query\":\"creuser:test\"}";
	}
	
	public static String getUpdateDocIncJsonForm() {
		return "{\"command\":\"update\", \"indexingType\":\"incremental\", \"documentList\":[{\"board_id\":\"0\",\"title\":\"제목 입니다0 업데이트...\",\"dscr\":\"본문 입니다.0\"," +
				"\"creuser\":\"test\"}], \"query\":\"creuser:test\"}";
	}
}
