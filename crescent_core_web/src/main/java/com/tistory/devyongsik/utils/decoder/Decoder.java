package com.tistory.devyongsik.utils.decoder;

public interface Decoder {
	public String decodeTerm(Object value) throws Exception;
	public String decodeStored(Object value) throws Exception;
}
