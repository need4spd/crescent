package com.tistory.devyongsik.crescent.admin.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo.DocValuesType;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.tistory.devyongsik.crescent.admin.entity.RankingTerm;
import com.tistory.devyongsik.crescent.admin.entity.TopRankingQueue;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollectionField;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.config.SpringApplicationContext;
import com.tistory.devyongsik.crescent.utils.decoder.Decoder;
import com.tistory.devyongsik.crescent.utils.decoder.LongDecoder;
import com.tistory.devyongsik.crescent.utils.decoder.MockDecoder;

@Service("indexFileManageService")
public class IndexFileManageServiceImpl implements IndexFileManageService {
	//TODO 호출할때마다 계산하는게 아니라, 색인시간 체크해서 보여주도록
	private Logger logger = LoggerFactory.getLogger(IndexFileManageServiceImpl.class);
	private Map<String, Integer>fieldTermCount = new HashMap<String, Integer>();
	private RankingTerm[] topRankingTerms = null;
	Map<String, Object> result = new HashMap<String, Object>();

	public enum View { Overview, Document };
	private final static int DEFAULT_TOPRANKING_TERM = 20;

	@Override
	public boolean reload(String selectCollection, String topRankingField) throws Exception {
		CrescentCollectionHandler collectionHandler 
		= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
		List<String> collectionNames = new ArrayList<String>();

		for (CrescentCollection crescentCollection : collectionHandler.getCrescentCollections().getCrescentCollections())
			collectionNames.add(crescentCollection.getName());
		result.put("collectionNames", collectionNames);

		if (selectCollection == null)
			selectCollection = collectionNames.get(0);

		CrescentCollection collection = collectionHandler.getCrescentCollections().getCrescentCollection(selectCollection);
		Decoder decoder = null;

		if (!StringUtils.hasText(topRankingField)) {
			if (collection.getDefaultSearchFields().get(0) != null) {
				topRankingField = collection.getDefaultSearchFields().get(0).getName();
			} else {
				logger.debug("doesn't defaultSearchField => {}", selectCollection);
				init(View.Overview);
				return false;
			}
		}

		for (CrescentCollectionField field :collection.getFields()) {
			if (field.getName().equals(topRankingField)) {
				if ("LONG".equalsIgnoreCase(field.getType())) {
					decoder = new LongDecoder();
				} else {
					decoder = new MockDecoder();
				}
			}
		}

		List<String> fieldNames = new ArrayList<String>();
		for (CrescentCollectionField field : collection.getFields()) {
			fieldNames.add(field.getName());
		}

		TopRankingQueue topRankingQueue = new TopRankingQueue(DEFAULT_TOPRANKING_TERM, new RankingTermComparator());

		try {

			Directory directory = FSDirectory.open(new File(collection.getIndexingDirectory()));
			DirectoryReader directoryReader = DirectoryReader.open(directory);

			int termFreq = 0;
			int termCount = 0;
			Term beforeTerm = null;
			topRankingTerms = null;
			//init term count
			fieldTermCount.clear();
			
			for (CrescentCollectionField field : collection.getFields()) {
				fieldTermCount.put(field.getName(), 0);
			}
			
			topRankingQueue.clear();

			for (CrescentCollectionField field : collection.getFields()) {
				
				TermsEnum termEnum = MultiFields.getTerms(directoryReader, field.getName()).iterator(null);
				
				while(termEnum.next() != null) {
					DocsEnum td = termEnum.docs(null, null, 0);
		            td.nextDoc();
		            
					if (beforeTerm == null) {
						beforeTerm = td;
					}
	
					if (beforeTerm.field() == td.field()) {
						termCount++;
					} else {
						fieldTermCount.put(beforeTerm.field(), termCount);
						termCount = 1;
						beforeTerm = td;
					}
	
					if (td.field().equals(topRankingField)) {
						RankingTerm e = new RankingTerm(
								decoder.decodeTerm(td.text()), td.field(), td.freq());
						topRankingQueue.add(e);
					}
					termFreq++;
				}
			
				if (beforeTerm != null) {
					fieldTermCount.put(beforeTerm.field(), termCount);
				}
			}
			
			termEnum.close();
			
			result.put("numOfTerm", termFreq);
			result.put("numOfDoc", directoryReader.numDocs());
			result.put("hasDel", directoryReader.hasDeletions());
			//			result.put("isOptimize", reader.isOptimized());
			result.put("indexVersion", directoryReader.getVersion());
			//			result.put("lastModify", new Date(IndexReader.lastModified(directory)));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		if (topRankingQueue.size() != 0) {
			topRankingTerms = topRankingQueue.toArray();
			Arrays.sort(topRankingTerms, Collections.reverseOrder());
		}

		result.put("selectCollection", selectCollection);
		result.put("indexName", collection.getIndexingDirectory());
		result.put("numOfField", collection.getFields().size());
		result.put("termCount", fieldTermCount);
		result.put("topRanking", topRankingTerms);
		result.put("topRankingField", topRankingField);
		result.put("topRankingCount", DEFAULT_TOPRANKING_TERM);
		result.put("fieldName", fieldNames);

		return true;
	}

	@Override
	public boolean reload(String selectCollection, int docNum) {
		CrescentCollectionHandler collectionHandler 
		= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
		List<String> collectionNames = new ArrayList<String>();

		for (CrescentCollection crescentCollection : collectionHandler.getCrescentCollections().getCrescentCollections())
			collectionNames.add(crescentCollection.getName());

		if (selectCollection == null)
			selectCollection = collectionNames.get(0);

		CrescentCollection collection = collectionHandler.getCrescentCollections().getCrescentCollection(selectCollection);

		List<String> fieldName = new ArrayList<String>();
		List<String> flag = new ArrayList<String>();
		List<String> norm = new ArrayList<String>();
		List<String> value = new ArrayList<String>();

		try {
			Directory directory = FSDirectory.open(new File(collection.getIndexingDirectory()));
			DirectoryReader directoryReader = DirectoryReader.open(directory);
			
			Document document = null;
			try {
				document = directoryReader.document(docNum);
			} catch(IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			}

			String fName = null;
			for (IndexableField field : document.getFields()) {
				fName = field.name();
				fieldName.add(fName);
				flag.add(fieldFlag(field));
				if (directoryReader.hasNorms(fName)) {
					norm.add(String.valueOf(Similarity.decodeNorm(reader.norms(fName)[docNum])));
				} else {
					norm.add("---");
				}
				value.add(field.stringValue());
			}

		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}


		result.put("selectCollection", selectCollection);
		result.put("collectionNames", collectionNames);
		result.put("docNum", docNum);
		result.put("fieldName", fieldName);
		result.put("flag", flag);
		result.put("norm", norm);
		result.put("value", value);

		return true;
	}

	@Override
	public boolean reload(String collectionName, String field,
			String query) {
		// TODO Auto-generated method stub
		return true;
	}

	private static String[] resultField1 = {"selectCollection", "indexName", "numOfField", "numOfDoc", "numOfTerm",
		"hasDel", "isOptimize", "indexVersion", "lastModify", "termCount", "topRanking"};

	private void init(View view) {
		if (view == View.Overview) {
			for (String field : resultField1) {
				result.put(field, "?????");
			}
		} else if (view == View.Document) {

		}
	}

	// org source =>  Luke:Util.java
	private static String fieldFlag(IndexableField f) {
		if (f == null) {
			return "--------------";
		}
		StringBuffer flags = new StringBuffer();
		if (f.fieldType().indexed()) {
			flags.append("I");
		} else {
			flags.append("-");
		}

		IndexOptions opts = f.fieldType().indexOptions();

		if (f.fieldType().indexed() && opts != null) {
			switch (opts) {
			case DOCS_ONLY:
				flags.append("d--");
				break;
			case DOCS_AND_FREQS:
				flags.append("df-");
				break;
			case DOCS_AND_FREQS_AND_POSITIONS:
				flags.append("dfp");
			}
		} else {
			flags.append("---");
		}
		if (f.fieldType().tokenized()) flags.append("T");
		else flags.append("-");
		if (f.fieldType().stored()) flags.append("S");
		else flags.append("-");
		if (f.fieldType().storeTermVectors()) flags.append("V");
		else flags.append("-");
		if (f.fieldType().storeTermVectorOffsets()) flags.append("o");
		else flags.append("-");
		if (f.fieldType().storeTermVectorPositions()) flags.append("p");
		else flags.append("-");
		if (f.fieldType().omitNorms()) flags.append("-");
		else flags.append("N");
		if (f.fieldType().docValueType() == DocValuesType.BINARY) flags.append("B");
		else flags.append("-");
		if (f instanceof IntField) flags.append("#");
		else flags.append("-");
		return flags.toString();
	}

	@Override
	public Map<String, Object> getResult() {
		return result;
	}

}

class RankingTermComparator implements Comparator<RankingTerm>
{
	@Override
	public int compare(RankingTerm x, RankingTerm y)
	{
		// Assume neither string is null. Real code should
		// probably be more robust
		if (x.getCount() < y.getCount())
		{
			return -1;
		}
		if (x.getCount() > y.getCount())
		{
			return 1;
		}
		return 0;
	}
}
