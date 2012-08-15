package com.tistory.devyongsik.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.tistory.devyongsik.config.CollectionConfig;
import com.tistory.devyongsik.domain.Collection;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 11.
 */
public class SearcherManager {

	/**
	 * 
List<IndexReader> indexReaders = getIndexReaders(); //다수의 인덱스 디렉토리로부터 IndexReader를 생성하여 반환하는 메서드
MultiReader multiReader = new MultiReader(indexReaders.toArray(new IndexReader[0]));
IndexSearcher indexSearcher = new IndexSearcher(multiReader);

	 */
	private static SearcherManager searcherManager = new SearcherManager();
	
	private Map<String, IndexSearcher> indexSearchersByCollection = new ConcurrentHashMap<String, IndexSearcher>();
	private Map<String, List<IndexReader>> indexReadersByCollection = new ConcurrentHashMap<String, List<IndexReader>>();
		
	private SearcherManager() {
		try {
			indexSearcherInit();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException();
		}
	}
	
	public static SearcherManager getSearcherManager() {
		return searcherManager;
	}
	
	private void indexSearcherInit() throws IOException {
		CollectionConfig collectionConfig = CollectionConfig.getInstance();
		
		Map<String, Collection> collections = collectionConfig.getCollections();
		Set<String> collectionNames = collections.keySet();
		
		for(String collectionName : collectionNames) {
			Collection collection = collections.get(collectionName);
			String indexDir = collection.getIndexingDir();
			int numOfIndex = collection.getNumberOfIndexFiles();
			
			List<IndexReader> readers = Collections.synchronizedList(new ArrayList<IndexReader>());
			
			for(int indexNumber = 0; indexNumber < numOfIndex; indexNumber++) {
				String directory = indexDir + "/" + indexNumber;
				Directory dir = FSDirectory.open(new File(directory));
				IndexReader reader = IndexReader.open(dir);
				readers.add(reader);
			}
			
			MultiReader multiReader = new MultiReader(readers.toArray(new IndexReader[0]));
			IndexSearcher searcher = new IndexSearcher(multiReader);

			indexReadersByCollection.put(collectionName, readers);
			indexSearchersByCollection.put(collectionName, searcher);
		}
	}
	
	public IndexSearcher getIndexSearcher(String collectionName) {
		return indexSearchersByCollection.get(collectionName);
	}
}
