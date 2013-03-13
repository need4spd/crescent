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
	
	public static String getUpdateNewDocBulkJsonForm() {
		return "{\"command\":\"update\", \"indexingType\":\"bulk\", \"documentList\":[{\"board_id\":\"0\",\"title\":\"제목 입니다0 업데이트...\",\"dscr\":\"본문 입니다.0\"," +
				"\"creuser\":\"testnew\"}], \"query\":\"creuser:testnew\"}";
	}
	
	public static String getUpdateNewDocListBulkJsonForm() {
		return "{\"command\":\"update\", \"indexingType\":\"bulk\", \"documentList\":[{\"board_id\":\"0\",\"title\":\"제목 입니다0 업데이트...\",\"dscr\":\"본문 입니다.0\"," +
				"\"creuser\":\"testnew\"}, {\"board_id\":\"1\",\"title\":\"제목 입니다1 업데이트...\",\"dscr\":\"본문 입니다.1\"," +
				"\"creuser\":\"testnew\"}], \"query\":\"creuser:testnew\"}";
	}
	
	public static String getUpdateByFieldValueDocBulkJsonForm() {
		return "{\"command\":\"update_by_field_value\", \"indexingType\":\"bulk\", \"documentList\":[{\"board_id\":\"0\",\"title\":\"제목 입니다0 업데이트...\",\"dscr\":\"본문 입니다.0\"," +
				"\"creuser\":\"test\"}], \"query\":\"creuser:*\"}";
	}
	
	public static String getUpdateByFieldValueNewDocListBulkJsonForm() {
		return "{\"command\":\"update_by_field_value\", \"indexingType\":\"bulk\", \"documentList\":[{\"board_id\":\"0\",\"title\":\"제목 입니다0 업데이트...\",\"dscr\":\"본문 입니다.0\"," +
				"\"creuser\":\"testnew\"}, {\"board_id\":\"1\",\"title\":\"제목 입니다1 업데이트...\",\"dscr\":\"본문 입니다.1\"," +
				"\"creuser\":\"testnew\"}], \"query\":\"creuser:*\"}";
	}
}
