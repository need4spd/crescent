package com.tistory.devyongsik.utils.decoder;

import org.apache.lucene.util.NumericUtils;


public class LongDecoder implements Decoder {

	  @Override
	  public String decodeTerm(Object value) {
	    return Long.toString(NumericUtils.prefixCodedToLong(value.toString()));
	  }
	  
	  @Override
	  public String decodeStored(Object value) {
	    return value.toString();
	  }
	  
	  public String toString() {
	    return "numeric-long";
	  }


}
