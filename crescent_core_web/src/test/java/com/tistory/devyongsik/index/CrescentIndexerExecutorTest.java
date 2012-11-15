package com.tistory.devyongsik.index;

import java.util.Map;

import org.junit.Test;

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollections;
import com.tistory.devyongsik.handler.Handler;
import com.tistory.devyongsik.handler.JsonDataHandler;

public class CrescentIndexerExecutorTest {
	@Test
	public void execute() {
		CrescentCollections crescentCollections = CrescentCollectionHandler.getInstance().getCrescentCollections();
		Map<String, CrescentCollection> collections = crescentCollections.getCrescentCollectionsMap();

		CrescentCollection sampleCollection = collections.get("sample_wiki");

		Handler handler = new JsonDataHandler();
		
		String jsonFormStr = "[{\"space_idx\":\"0\",\"wiki_title\":\"제목 입니다0\",\"wiki_idx\":\"0\",\"wiki_text\":\"본문 입니다.0\",\"ins_date\":\"20120819\"" +
				",\"ins_user\":\"need4spd\"}]";
		CrescentIndexerExecutor executor = new CrescentIndexerExecutor(sampleCollection, handler);
		executor.execute(jsonFormStr);
	}
}
