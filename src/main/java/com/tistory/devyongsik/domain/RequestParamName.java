package com.tistory.devyongsik.domain;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParamName {
	String name();
	String defaultValue();
}
