package com.tistory.devyongsik.crescent.admin.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.tistory.devyongsik.crescent.admin.entity.MorphToken;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;

@Service("morphService")
public class MorphServiceImpl implements MorphService {

	private Logger logger = LoggerFactory.getLogger(MorphServiceImpl.class);

	@Autowired
	@Qualifier("crescentCollectionHandler")
	private CrescentCollectionHandler collectionHandler;

	@Override
	public List<MorphToken> getTokens(String keyword, boolean isIndexingMode, String collectionName) throws IOException {
		StringReader reader = new StringReader(keyword);

		CrescentCollection crescentCollection = collectionHandler.getCrescentCollections().getCrescentCollection(collectionName);
		Analyzer analyzer = null;

		if(isIndexingMode) {
			analyzer = crescentCollection.getIndexingModeAnalyzer();
		} else {
			analyzer = crescentCollection.getSearchModeAnalyzer();
		}

		TokenStream stream = analyzer.tokenStream("dummy", reader);
		stream.reset();

		CharTermAttribute charTermAtt = stream.getAttribute(CharTermAttribute.class);
		OffsetAttribute offSetAtt = stream.getAttribute(OffsetAttribute.class);
		TypeAttribute typeAtt = stream.getAttribute(TypeAttribute.class);

		List<MorphToken> resultTokenList = new ArrayList<MorphToken>();

		while(stream.incrementToken()) {
			logger.debug("termAtt : {}, startOffset : {}, endOffset : {}, typeAtt : {}",
					new Object[] {charTermAtt.toString(), offSetAtt.startOffset(), offSetAtt.endOffset(), typeAtt.type()});

			MorphToken mt = new MorphToken();
			mt.setTerm(charTermAtt.toString());
			mt.setType(typeAtt.type());
			mt.setStartOffset(offSetAtt.startOffset());
			mt.setEndOffset(offSetAtt.endOffset());

			resultTokenList.add(mt);
		}

		stream.close();

		return resultTokenList;
	}

}
