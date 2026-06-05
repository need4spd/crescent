package com.tistory.devyongsik.crescent.search.highlight;


import java.io.IOException;
import java.io.StringReader;

import javax.annotation.PostConstruct;

import junit.framework.Assert;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.Test;

import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;


public class CrescentHighlighterTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() {
		super.init();
	}

	@Test
	public void highlightUsage() throws IOException, InvalidTokenOffsetsException {
		String text = "my fox jump group org next fox spring health care book fox tape java fox fox shop world fox";

		TermQuery query = new TermQuery(new Term("f", "fox"));

		QueryScorer scorer = new QueryScorer(query);

		Highlighter highlighter = new Highlighter(scorer);

		Fragmenter fragmenter = new SimpleFragmenter(5);
		highlighter.setTextFragmenter(fragmenter);

		Analyzer a = new WhitespaceAnalyzer();
		TokenStream tokenStream = a.tokenStream("f", new StringReader(text));

		String result =
		        highlighter.getBestFragments(tokenStream, text,2, "...");

		a.close();

		System.out.println(result);

		Assert.assertEquals(" <B>fox</B>... <B>fox</B>", result);
	}

	@Test
	public void fastVectorTest() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("입니다");
		searchRequest.setCollectionName("sample");
		searchRequest.setSearchField("title,dscr");

		SearcherManager searcherManager = crescentSearcherManager.getSearcherManager("sample");
		IndexSearcher indexSearcher = searcherManager.acquire();

		IndexReader indexReader = indexSearcher.getIndexReader();

		TopDocs topDocs = indexSearcher.search(new TermQuery(new Term("dscr", "텍스트")), 3);

		System.out.println("ddd : " + indexReader.leaves());
		System.out.println(topDocs.totalHits);

		FastVectorHighlighter highlighter = new FastVectorHighlighter();

		System.out.println("field query : " + highlighter.getFieldQuery(new TermQuery(new Term("dscr", "입니다"))));

		String fragment = highlighter.getBestFragment(highlighter.getFieldQuery(new TermQuery(new Term("dscr", "텍스트"))),
				indexReader, topDocs.scoreDocs[0].doc, "dscr", 30);

		System.out.println(fragment);
	}

	@Test public void testVectorHighlighter() throws Exception {
        Directory dir = new ByteBuffersDirectory();
        IndexWriter indexWriter
        	= new IndexWriter(dir, new IndexWriterConfig(new WhitespaceAnalyzer()));

        Document doc = new Document();
        FieldType fieldType = new FieldType();
		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		fieldType.setStored(true);
		fieldType.setTokenized(true);
		fieldType.setStoreTermVectors(true);

        Field f1 = new Field("_id", "1", fieldType);
        Field f2 = new Field("content", "the big 삼성전자연구원. dog", fieldType);

        doc.add(f1);
        doc.add(f2);

        indexWriter.addDocument(doc);

        indexWriter.commit();
        indexWriter.close();

        DirectoryReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(new TermQuery(new Term("_id", "1")), 1);

        System.out.println(topDocs.totalHits);

        FastVectorHighlighter highlighter = new FastVectorHighlighter();
        String fragment = highlighter.getBestFragment(highlighter.getFieldQuery(new TermQuery(new Term("content", "big"))),
        		reader, topDocs.scoreDocs[0].doc, "content", 200);

        System.out.println(fragment);
    }
}
