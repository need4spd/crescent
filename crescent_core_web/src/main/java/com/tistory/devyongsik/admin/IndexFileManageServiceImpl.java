package com.tistory.devyongsik.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.domain.CrescentCollectionField;
import com.tistory.devyongsik.domain.CrescentCollections;

public class IndexFileManageServiceImpl implements IndexFileManageService {
	//TODO 호출할때마다 계산하는게 아니라, 색인시간 체크해서 보여주도록
	private Logger logger = LoggerFactory.getLogger(IndexFileManageServiceImpl.class);
	private Map<String, Integer>fieldTermCount = new HashMap<String, Integer>();
	Map<String, Object> result = new HashMap<String, Object>();
	
	public enum View { Overview, Document };
	
	String[] resultField1 = {"collectionName", "indexName", "numOfField", "numOfDoc", "numOfTerm",
						"hasDel", "isOptimize", "indexVersion", "lastModify", "termCount", "topRanking"};
	
	
	@Override
	public boolean reload(String collectionName) {
		if (collectionName == null) {
			return false;
		}
		
		CrescentCollections collections = CrescentCollectionHandler.getInstance().getCrescentCollections();
		CrescentCollection collection = collections.getCrescentCollection(collectionName);
		
		if (collection == null) {
			logger.debug("doesn't Collection Info => {}", collectionName);
			init(View.Overview);
			return false;
		}
		
		try {
			Directory directory = FSDirectory.open(new File(collection.getIndexingDirectory()));
			IndexReader reader = IndexReader.open(directory);
			
			TermEnum terms = reader.terms();
			int termFreq = 0;
			int termCount = 0;
			Term beforeTerm = null;
			//init term count
			fieldTermCount.clear();
			for (CrescentCollectionField field : collection.getFields())
				fieldTermCount.put(field.getName(), 0);
			while(terms.next()) {
				Term currTerm = terms.term();
				if (beforeTerm == null) {
					beforeTerm = currTerm;
				}
				
				if (beforeTerm.field() == currTerm.field()) {
					termCount++;
				} else {
					fieldTermCount.put(beforeTerm.field(), termCount);
					termCount = 1;
					beforeTerm = currTerm;
				}
				termFreq++;
			}
			if (beforeTerm != null)
				fieldTermCount.put(beforeTerm.field(), termCount);
			
			terms.close();
			result.put("numOfTerm", termFreq);
			result.put("numOfDoc", reader.numDocs());
			result.put("hasDel", reader.hasDeletions());
			result.put("isOptimize", reader.isOptimized());
			result.put("indexVersion", reader.getVersion());
			result.put("lastModify", new Date(IndexReader.lastModified(directory)));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		result.put("collectionName", collectionName);
		result.put("indexName", collection.getIndexingDirectory());
		result.put("numOfField", collection.getFields().size());
		result.put("termCount", fieldTermCount);
		result.put("topRanking", null);
		
		return true;
	}

	@Override
	public boolean reload(String collectionName, int docNum) {
		if (collectionName == null)
			return false;
		
		CrescentCollections collections = CrescentCollectionHandler.getInstance().getCrescentCollections();
		CrescentCollection collection = collections.getCrescentCollection(collectionName);
		
		if (collection == null) {
			logger.debug("doesn't Collection Info => {}", collectionName);
			return false;
		}
		
		List<String> fieldName = new ArrayList<String>();
		List<String> flag = new ArrayList<String>();
		List<String> norm = new ArrayList<String>();
		List<String> value = new ArrayList<String>();
		
		try {
			Directory directory = FSDirectory.open(new File(collection.getIndexingDirectory()));
			IndexReader reader = IndexReader.open(directory);
			
			Document document = null;
			try {
				document = reader.document(docNum);
			} catch(IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			}
			
			String fName = null;
			for (Fieldable field : document.getFields()) {
				fName = field.name();
				fieldName.add(fName);
				flag.add(fieldFlag(field));
				if (reader.hasNorms(fName)) {
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
		
		
		result.put("collection", collectionName);
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
	
	private void init(View view) {
		if (view == View.Overview) {
			for (String field : resultField1) {
				result.put(field, "?????");
			}
		} else if (view == View.Document) {
			
		}
	}
	
	// org source =>  Luke:Util.java
	private static String fieldFlag(Fieldable f) {
		if (f == null) {
		      return "--------------";
		    }
		    StringBuffer flags = new StringBuffer();
		    if (f.isIndexed()) flags.append("I");
		    else flags.append("-");
		    IndexOptions opts = f.getIndexOptions();
		    if (f.isIndexed() && opts != null) {
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
		    if (f.isTokenized()) flags.append("T");
		    else flags.append("-");
		    if (f.isStored()) flags.append("S");
		    else flags.append("-");
		    if (f.isTermVectorStored()) flags.append("V");
		    else flags.append("-");
		    if (f.isStoreOffsetWithTermVector()) flags.append("o");
		    else flags.append("-");
		    if (f.isStorePositionWithTermVector()) flags.append("p");
		    else flags.append("-");
		    if (f.getOmitNorms()) flags.append("-");
		    else flags.append("N");
		    if (f.isLazy()) flags.append("L");
		    else flags.append("-");
		    if (f.isBinary()) flags.append("B");
		    else flags.append("-");
		    if (f instanceof NumericField) flags.append("#");
		    else flags.append("-");
		    return flags.toString();
	}

	@Override
	public Map<String, Object> getResult() {
		return result;
	}

}
