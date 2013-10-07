package com.tistory.devyongsik.crescent.index;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.FieldType.NumericType;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollectionField;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 4.
 */
public class LuceneFieldBuilder {
	private Logger logger = LoggerFactory.getLogger(LuceneFieldBuilder.class);

	public IndexableField create(CrescentCollectionField collectionField, String value) {
		
		FieldType fieldType = new FieldType();
		fieldType.setIndexed(collectionField.isIndex());
		fieldType.setStored(collectionField.isStore());
		fieldType.setTokenized(collectionField.isAnalyze());
		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		fieldType.setStoreTermVectors(collectionField.isTermvector());
		
		if("STRING".equalsIgnoreCase(collectionField.getType())) {
			Field f = new Field(collectionField.getName(),
					StringUtils.defaultString(value, ""),
					fieldType);

			f.setBoost(collectionField.getBoost());
			
			logger.debug("Field : {}", f);

			return f;

		} else if("LONG".equalsIgnoreCase(collectionField.getType())) {
			fieldType.setNumericType(NumericType.LONG);
			
			Field f = new LongField(collectionField.getName(),
					Long.parseLong(value),
					fieldType);
			
			logger.debug("Field : {}", f);
			
			return f;
		
		} else {
			return null;
		}
	}
}
