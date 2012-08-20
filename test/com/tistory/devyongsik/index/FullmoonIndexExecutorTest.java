package com.tistory.devyongsik.index;

import org.junit.Test;

import com.tistory.devyongsik.config.CollectionConfig;
import com.tistory.devyongsik.domain.Collection;
import com.tistory.devyongsik.handler.Handler;
import com.tistory.devyongsik.handler.JsonDataHandler;

/**
 * author : need4spd, need4spd@naver.com, 2012. 7. 15.
 */
public class FullmoonIndexExecutorTest {

	@Test
	public void execute() {
		CollectionConfig collectionConfig = CollectionConfig.getInstance();
		Collection collection = collectionConfig.getCollection("sample");
		
		Handler handler = new JsonDataHandler();
		
		String jsonFormStr = "[{\"board_id\":\"1\",\"title\":\"title\",\"dscr\":\"상세설명\",\"creuser\":\"작성자\"}," +
				"{\"board_id\":\"2\",\"title\":\"제목\",\"dscr\":\"상세설명\",\"creuser\":\"작성자\"}]";
		FullmoonIndexExecutor executor = new FullmoonIndexExecutor(collection, handler);
		executor.execute(jsonFormStr);
	}
}
