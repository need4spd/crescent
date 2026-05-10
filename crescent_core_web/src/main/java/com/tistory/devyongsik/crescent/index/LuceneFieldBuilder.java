package com.tistory.devyongsik.crescent.index;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollectionField;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 4.
 */
public class LuceneFieldBuilder {
	private Logger logger = LoggerFactory.getLogger(LuceneFieldBuilder.class);

	public IndexableField create(CrescentCollectionField collectionField, String value) {

		if("STRING".equalsIgnoreCase(collectionField.getType())) {
			FieldType fieldType = new FieldType();
			if (collectionField.isIndex()) {
				fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
			} else {
				fieldType.setIndexOptions(IndexOptions.NONE);
			}
			fieldType.setStored(collectionField.isStore());
			fieldType.setTokenized(collectionField.isAnalyze());
			fieldType.setStoreTermVectors(collectionField.isTermvector());
			fieldType.freeze();

			Field f = new Field(collectionField.getName(),
					StringUtils.defaultString(value, ""),
					fieldType);

			logger.debug("Field : {}", f);

			return f;

		} else if("LONG".equalsIgnoreCase(collectionField.getType())) {
			Field.Store store = collectionField.isStore() ? Field.Store.YES : Field.Store.NO;
			Field f = new LongField(collectionField.getName(),
					Long.parseLong(value),
					store);

			logger.debug("Field : {}", f);

			return f;

		} else if ("INTEGER".equalsIgnoreCase(collectionField.getType())) {
			Field.Store store = collectionField.isStore() ? Field.Store.YES : Field.Store.NO;
			Field f = new IntField(collectionField.getName(),
					Integer.parseInt(value),
					store);

			logger.debug("Field : {}", f);

			return f;

		} else {
			return null;
		}
	}
}
