package com.tistory.devyongsik.crescent.admin.service;

import com.tistory.devyongsik.crescent.admin.entity.HighFreqTermResult;
import com.tistory.devyongsik.crescent.admin.entity.IndexInfo;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollectionField;
import org.apache.lucene.codecs.TermStats;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service("indexFileManageService")
public class IndexFileManageServiceImpl implements IndexFileManageService {
	//TODO 호출할때마다 계산하는게 아니라, 색인시간 체크해서 보여주도록
	private Logger logger = LoggerFactory.getLogger(getClass());

	public enum View { Overview, Document };
    private static final TermStats[] EMPTY_STATS = new TermStats[0];

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

        long termCount = 0L;

        List<String> fieldNames = new ArrayList<String>();
        for (CrescentCollectionField field : selectCollection.getFields()) {
            fieldNames.add(field.getName());
            termCount += directoryReader.getSumTotalTermFreq(field.getName());
        }

        indexInfo.setFieldNames(fieldNames);
        indexInfo.setNumOfField(fieldNames.size());
        indexInfo.setTermCount(termCount);

        return indexInfo;
    }

	@Override
	public HighFreqTermResult reload(CrescentCollection selectCollection) throws Exception {

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
                        fillQueue(te, termStatsQueue);
                    }
                }
            } else {
                Fields fields = MultiFields.getFields(directoryReader);
                if (fields == null) {
                    logger.error("Index has no fields.....!");
                    throw new RuntimeException("Index has no fields.....!");
                }

                Iterator<String> fieldsNameIterator = fields.iterator();
                while(fieldsNameIterator.hasNext()) {
                    String fieldName = fieldsNameIterator.next();
                    if(fieldName != null) {
                        Terms terms = fields.terms(fieldName);
                        te = terms.iterator(te);
                        fillQueue(te, termStatsQueue);
                    }
                }

            }

        } catch (Exception e) {
            logger.error("Error in getHighFreqTerm....!", e);
            throw new RuntimeException("Error in getHighFreqTerm....!", e);
        }

        return highFreqTermResult;
	}

    public static void fillQueue(TermsEnum termsEnum, HighFreqTermResult.TermStatsQueue tiq) throws Exception {

        while (true) {
            BytesRef term = termsEnum.next();
            if (term != null) {
                BytesRef r = new BytesRef();
                r.copyBytes(term);
                tiq.insertWithOverflow(new TermStats(termsEnum.docFreq(), termsEnum.totalTermFreq()));
            } else {
                break;
            }
        }
    }

	@Override
	public HighFreqTermResult reload(CrescentCollection selectCollection, int docId) {
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

            Document document = directoryReader.document(docId);


            if (fieldNames != null) {
                Fields fields = directoryReader.getTermVectors(docId);

                if (fields == null) {
                    logger.error("Index has no fields.....!");
                    throw new RuntimeException("Index has no fields.....!");
                }

                for (String field : fieldNames) {
                    Terms terms = fields.terms(field);
                    if (terms != null) {
                        te = terms.iterator(te);
                        fillQueue(te, termStatsQueue);
                    }
                }
            } else {
                Fields fields = directoryReader.getTermVectors(docId);
                if (fields == null) {
                    logger.error("Index has no fields.....!");
                    throw new RuntimeException("Index has no fields.....!");
                }

                Iterator<String> fieldsNameIterator = fields.iterator();
                while(fieldsNameIterator.hasNext()) {
                    String fieldName = fieldsNameIterator.next();
                    if(fieldName != null) {
                        Terms terms = fields.terms(fieldName);
                        te = terms.iterator(te);
                        fillQueue(te, termStatsQueue);
                    }
                }

            }

        } catch (Exception e) {
            logger.error("Error in getHighFreqTerm....!", e);
            throw new RuntimeException("Error in getHighFreqTerm....!", e);
        }

        return highFreqTermResult;
	}
}
