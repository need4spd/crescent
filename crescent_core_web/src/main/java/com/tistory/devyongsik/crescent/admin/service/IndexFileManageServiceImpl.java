package com.tistory.devyongsik.crescent.admin.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tistory.devyongsik.crescent.admin.entity.CrescentTermStats;
import com.tistory.devyongsik.crescent.admin.entity.HighFreqTermResult;
import com.tistory.devyongsik.crescent.admin.entity.HighFreqTermResult.TermStatsQueue;
import com.tistory.devyongsik.crescent.admin.entity.IndexInfo;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollectionField;

@Service("indexFileManageService")
public class IndexFileManageServiceImpl implements IndexFileManageService {
	//TODO 호출할때마다 계산하는게 아니라, 색인시간 체크해서 보여주도록
	private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Override
    public IndexInfo getIndexInfo(CrescentCollection selectCollection) throws IOException {
        IndexInfo indexInfo = new IndexInfo();

        Directory directory = FSDirectory.open(new File(selectCollection.getIndexingDirectory()));
        DirectoryReader directoryReader = DirectoryReader.open(directory);

        indexInfo.setNumOfDoc(directoryReader.numDocs());
        indexInfo.setHasDel(directoryReader.hasDeletions());
        indexInfo.setIndexVersion(directoryReader.getVersion());
        indexInfo.setSelectCollectionName(selectCollection.getName());
        indexInfo.setIndexName(selectCollection.getIndexingDirectory());

        Map<String, Long> termCountByFieldNameMap = new HashMap<String, Long>();
        
        long totalTermCount = 0L;
        long totalTermCountByField = 0L;
        
        List<String> fieldNames = new ArrayList<String>();
        for (CrescentCollectionField field : selectCollection.getFields()) {
            fieldNames.add(field.getName());
            
            totalTermCountByField = directoryReader.getSumTotalTermFreq(field.getName());
            totalTermCount += totalTermCountByField;
            
            termCountByFieldNameMap.put(field.getName(), totalTermCountByField);
        }

        indexInfo.setFieldNames(fieldNames);
        indexInfo.setNumOfField(fieldNames.size());
        indexInfo.setTermCountByFieldNameMap(termCountByFieldNameMap);
        indexInfo.setTotalTermCount(totalTermCount);
        
        try {
        	HighFreqTermResult highFreqTermResult = getHighFreqTerms(selectCollection);
			TermStatsQueue q = highFreqTermResult.getTermStatsQueue();
			
			List<CrescentTermStats> crescentTermStatsList = new ArrayList<CrescentTermStats>();
			
			while(q.size() > 0) {
				CrescentTermStats stats = q.pop();
				
				crescentTermStatsList.add(stats);
			}
			
			Collections.sort(crescentTermStatsList, new Comparator<CrescentTermStats>() {

				@Override
				public int compare(CrescentTermStats o1, CrescentTermStats o2) {
					if (o2.getTotalTermFreq() > o1.getTotalTermFreq()) {
						return 1;
					} else if (o2.getTotalTermFreq() < o1.getTotalTermFreq()) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			
			indexInfo.setCrescentTermStatsList(crescentTermStatsList);
			
		} catch (Exception e) {
			logger.error("Exception in getIndexInfo : " , e);
		}

        return indexInfo;
    }

	private HighFreqTermResult getHighFreqTerms(CrescentCollection selectCollection) throws Exception {

        HighFreqTermResult highFreqTermResult = new HighFreqTermResult();

		List<String> fieldNames = new ArrayList<String>();
		
		for (CrescentCollectionField field : selectCollection.getFields()) {
			fieldNames.add(field.getName());
		}

        HighFreqTermResult.TermStatsQueue termStatsQueue = highFreqTermResult.getTermStatsQueue();
        TermsEnum te = null;
        int i = 0;
		try {

            Directory directory = FSDirectory.open(new File(selectCollection.getIndexingDirectory()));
            DirectoryReader directoryReader = DirectoryReader.open(directory);

            if (fieldNames != null) {
                Fields fields = MultiFields.getFields(directoryReader);
                Terms terms = MultiFields.getTerms(directoryReader, fieldNames.get(0));
                TermsEnum termsEnum = terms.iterator(TermsEnum.EMPTY);
                
                if (fields == null) {
                    logger.error("Index has no fields.....!");
                    throw new RuntimeException("Index has no fields.....!");
                }
                
                String termText;
                CrescentCollectionField crescentField = selectCollection.getCrescentFieldByName().get(fieldNames.get(0));
               
                while(termsEnum.next() != null) {
                	BytesRef ref = termsEnum.term();
                	
                	 if("LONG".equalsIgnoreCase(crescentField.getType())) {
                     	termText = String.valueOf(NumericUtils.prefixCodedToLong(ref));
                     } else if ("INTEGER".equalsIgnoreCase(crescentField.getType())) {
                     	termText = String.valueOf(NumericUtils.prefixCodedToInt(ref));
                     } else {
                     	termText = ref.utf8ToString();
                     }
         			
                	 termStatsQueue.insertWithOverflow(new CrescentTermStats(fieldNames.get(0), termText, termsEnum.docFreq(), termsEnum.totalTermFreq()));
                	 i++;
                }
//                for (String field : fieldNames) {
//                    Terms terms = MultiFields.getTerms(directoryReader, field);
//                    if (terms != null) {
//                        te = terms.iterator(te);
//                        CrescentCollectionField crescentField = selectCollection.getCrescentFieldByName().get(field);
//                        fillQueue(field, te, highFreqTermResult.getTermStatsQueue(), crescentField.getType());
//                    }
//                }
            }

        } catch (Exception e) {
            logger.error("Error in getHighFreqTerm....!", e);
            throw new RuntimeException("Error in getHighFreqTerm....!", e);
        }

		logger.info("highFreqTermResult count : {}", i);
        return highFreqTermResult;
	}

    private void fillQueue(String fieldName, TermsEnum termsEnum, HighFreqTermResult.TermStatsQueue tiq, String fieldType) throws Exception {
    	BytesRef ref = null;
    	String termText;
    	int i = 0;
    	while ((ref = termsEnum.next()) != null) {
            if("LONG".equalsIgnoreCase(fieldType)) {
            	termText = String.valueOf(NumericUtils.prefixCodedToLong(ref));
            } else if ("INTEGER".equalsIgnoreCase(fieldType)) {
            	termText = String.valueOf(NumericUtils.prefixCodedToInt(ref));
            } else {
            	termText = ref.utf8ToString();
            }
			
           tiq.insertWithOverflow(new CrescentTermStats(fieldName, termText, termsEnum.docFreq(), termsEnum.totalTermFreq()));
        }
    }
}