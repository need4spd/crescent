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
import org.apache.lucene.index.MultiTerms;
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
import com.tistory.devyongsik.crescent.utils.decoder.LongDecoder;

@Service("indexFileManageService")
public class IndexFileManageServiceImpl implements IndexFileManageService {
	private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public IndexInfo getIndexInfo(CrescentCollection selectCollection, String selectTopField) throws IOException {
        IndexInfo indexInfo = new IndexInfo();

        Directory directory = FSDirectory.open(new File(selectCollection.getIndexingDirectory()).toPath());
        DirectoryReader directoryReader;
        if (DirectoryReader.indexExists(directory) == true ) {
            directoryReader = DirectoryReader.open(directory);
        } else {
        	return null;
        }
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
        	HighFreqTermResult highFreqTermResult = getHighFreqTerms(selectCollection, selectTopField);
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

	private HighFreqTermResult getHighFreqTerms(CrescentCollection selectCollection, String selectTopField) throws Exception {

        HighFreqTermResult highFreqTermResult = new HighFreqTermResult();

        HighFreqTermResult.TermStatsQueue termStatsQueue = highFreqTermResult.getTermStatsQueue();
        int i = 0;
        Directory directory = null;
        DirectoryReader directoryReader = null;
		try {

            directory = FSDirectory.open(new File(selectCollection.getIndexingDirectory()).toPath());
            directoryReader = DirectoryReader.open(directory);

            if (selectTopField != null) {
                Terms terms = MultiTerms.getTerms(directoryReader, selectTopField);
                if (terms == null) {
                    logger.error("Index has no fields.....!");
                    throw new RuntimeException("Index has no fields.....!");
                }
                TermsEnum termsEnum = terms.iterator();
                String termText;
                CrescentCollectionField crescentField = selectCollection.getCrescentFieldByName().get(selectTopField);

                BytesRef ref;
                while((ref = termsEnum.next()) != null) {
                	if("LONG".equalsIgnoreCase(crescentField.getType())) {
                    	termText = String.valueOf(NumericUtils.sortableBytesToLong(ref.bytes, ref.offset));
                    } else if ("INTEGER".equalsIgnoreCase(crescentField.getType())) {
                    	termText = String.valueOf(NumericUtils.sortableBytesToInt(ref.bytes, ref.offset));
                    } else {
                    	termText = ref.utf8ToString();
                    }

                	termStatsQueue.insertWithOverflow(new CrescentTermStats(selectTopField, termText, termsEnum.docFreq(), termsEnum.totalTermFreq()));
                	i++;
                }
            }

        } catch (Exception e) {
            logger.error("Error in getHighFreqTerm....!", e);
            throw new RuntimeException("Error in getHighFreqTerm....!", e);
        } finally {
        	if (directoryReader != null) directoryReader.close();
        	if (directory != null) directory.close();
        }

		logger.info("highFreqTermResult count : {}", i);
        return highFreqTermResult;
	}

}
