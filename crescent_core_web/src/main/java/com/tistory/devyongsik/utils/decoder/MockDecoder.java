package com.tistory.devyongsik.utils.decoder;

public class MockDecoder implements Decoder {

	@Override
	public String decodeTerm(Object value) throws Exception {
		return (String)value;
	}

	@Override
	public String decodeStored(Object value) throws Exception {
		return value.toString();
	}

}
