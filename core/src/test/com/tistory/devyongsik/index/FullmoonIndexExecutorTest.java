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
		Collection collection = collectionConfig.getCollection("sample_wiki");
		
		Handler handler = new JsonDataHandler();
		
		String jsonFormStr = "[{\"space_idx\":\"0\",\"wiki_title\":\"제목 입니다0\",\"wiki_idx\":\"0\",\"wiki_text\":\"본문 입니다.0\",\"ins_date\":\"20120819\"" +
				",\"ins_user\":\"need4spd\"}]";
		CrescentIndexerExecutor executor = new CrescentIndexerExecutor(collection, handler);
		executor.execute(jsonFormStr);
	}
}
