package com.tistory.devyongsik.crescent.admin.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

        Map<String, Long> termCount = new HashMap<String, Long>();
        long totalTermCount = 0L;
        
        List<String> fieldNames = new ArrayList<String>();
        for (CrescentCollectionField field : selectCollection.getFields()) {
            fieldNames.add(field.getName());
            totalTermCount += directoryReader.getSumTotalTermFreq(field.getName());
        }

        indexInfo.setFieldNames(fieldNames);
        indexInfo.setNumOfField(fieldNames.size());
        indexInfo.setTermCount(termCount);
        indexInfo.setTotalTermCount(totalTermCount);
        
        try {
			HighFreqTermResult highFreqTermResult = getHighFreqTerms(selectCollection);
			TermStatsQueue q = highFreqTermResult.getTermStatsQueue();
			
			while(q.size() > 0) {
				CrescentTermStats stats = q.pop();
				termCount.put(stats.getTermText(), stats.getTotalTermFreq());
			}
			
			indexInfo.setTermCount(termCount);
			
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

		try {

            Directory directory = FSDirectory.open(new File(selectCollection.getIndexingDirectory()));
            DirectoryReader directoryReader = DirectoryReader.open(directory);

            if (fieldNames != null) {
                Fields fields = MultiFields.getFields(directoryReader);

                if (fields == null) {
                    logger.error("Index has no fields.....!");
                    throw new RuntimeException("Index has no fields.....!");
                }

                for (String field : fieldNames) {
                    Terms terms = fields.terms(field);
                    if (terms != null) {
                        te = terms.iterator(te);
                        fillQueue(field, te, termStatsQueue);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error in getHighFreqTerm....!", e);
            throw new RuntimeException("Error in getHighFreqTerm....!", e);
        }

        return highFreqTermResult;
	}

    private void fillQueue(String fieldName, TermsEnum termsEnum, HighFreqTermResult.TermStatsQueue tiq) throws Exception {

        while (true) {
            
        	BytesRef term = termsEnum.next();
            
            if (term != null) {
            	BytesRef r = new BytesRef();
                r.copyBytes(term);
                tiq.insertWithOverflow(new CrescentTermStats(fieldName, r, termsEnum.docFreq(), termsEnum.totalTermFreq()));
            
            } else {
                break;
            }
        }
    }
}
