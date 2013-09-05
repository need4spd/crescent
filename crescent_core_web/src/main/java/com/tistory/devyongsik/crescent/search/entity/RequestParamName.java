package com.tistory.devyongsik.crescent.search.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParamName {
	String name();
	String defaultValue();
}
