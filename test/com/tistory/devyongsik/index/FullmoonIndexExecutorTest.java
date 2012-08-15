package com.tistory.devyongsik.index;

import org.junit.Test;

import com.tistory.devyongsik.config.CollectionConfig;
import com.tistory.devyongsik.domain.Collection;

/**
 * author : need4spd, need4spd@naver.com, 2012. 7. 15.
 */
public class FullmoonIndexExecutorTest {

	@Test
	public void execute() {
		CollectionConfig collectionConfig = CollectionConfig.getInstance();
		Collection collection = collectionConfig.getCollection("sample");
		
		FullmoonIndexExecutor executor = new FullmoonIndexExecutor(collection);
		executor.execute();
	}
}
