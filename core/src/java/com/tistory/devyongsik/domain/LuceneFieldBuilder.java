package com.tistory.devyongsik.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * author : need4spd, need4spd@naver.com, 2012. 3. 4.
 */
public class LuceneFieldBuilder {
	private Logger logger = LoggerFactory.getLogger(LuceneFieldBuilder.class);

	public Field create(CollectionField collectionField, String value) {
		Field f = new Field(collectionField.getName(),
				StringUtils.defaultString(value, ""),
				getFieldStore(collectionField),
				getFieldIndex(collectionField),
				getFieldTermVector(collectionField));

		f.setBoost(collectionField.getFieldBoost());

		logger.debug("Field : {}", f);

		return f;
	}

	private Field.Store getFieldStore(CollectionField field) {
		return field.isStore() ? Field.Store.YES : Field.Store.NO;
	}

	private Field.Index getFieldIndex(CollectionField field) {
		return field.isIndexing() ? (field.isAnalyze() ? Field.Index.ANALYZED : 
			Field.Index.NOT_ANALYZED) : Field.Index.NO;
	}

	private Field.TermVector getFieldTermVector(CollectionField field) {
		Field.TermVector ftv = Field.TermVector.NO;

		if (field.hasTermPosition() && field.hasTermOffset())
			ftv = Field.TermVector.WITH_POSITIONS_OFFSETS;
		else if (field.hasTermPosition())
			ftv = Field.TermVector.WITH_POSITIONS;
		else if (field.hasTermOffset())
			ftv = Field.TermVector.WITH_OFFSETS;            
		else if (field.hasTermVector())
			ftv = Field.TermVector.YES;
		return ftv;
	}
}
