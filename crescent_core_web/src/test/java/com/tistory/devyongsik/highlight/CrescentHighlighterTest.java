package com.tistory.devyongsik.highlight;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.annotation.PostConstruct;

import junit.framework.Assert;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import com.tistory.devyongsik.domain.CrescentCollectionField;
import com.tistory.devyongsik.domain.SearchRequest;
import com.tistory.devyongsik.exception.CrescentInvalidRequestException;
import com.tistory.devyongsik.query.CrescentSearchRequestWrapper;
import com.tistory.devyongsik.search.CrescentSearcherManager;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;


public class CrescentHighlighterTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() {
		super.init();
	}
	
	@Test
	public void highlightTest() throws CrescentInvalidRequestException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("입니다");
		searchRequest.setCollectionName("sample");
		searchRequest.setSearchField("title,dscr");
		
		CrescentSearchRequestWrapper csrw = new CrescentSearchRequestWrapper(searchRequest);
		List<CrescentCollectionField> targetSearchFields = csrw.getTargetSearchFields();
		
		System.out.println("####### : " + targetSearchFields);
		
		CrescentHighlighter highlighter = new CrescentHighlighter();
		String r = highlighter.getBestFragment(csrw.getTargetSearchFields().get(0), "제목 입니다.텍스트 입니다.제목 입니다.", csrw.getQuery());
		String r2 = highlighter.getBestFragment(csrw.getTargetSearchFields().get(1), "텍스트 입니다. 제목.", csrw.getQuery());
		
		System.out.println(r);
		
		System.out.println("-------");
		
		System.out.println(r2);
		
		Assert.assertEquals("제목 <b>입니다</b>.텍스트 <b>입니다</b>.제목 <b>입니다</b>.", r);
		Assert.assertEquals("텍스트 <b>입니다</b>. 제목.", r2);
	}
	
	@Test
	public void highlightUsage() throws IOException, InvalidTokenOffsetsException {
		String text = "my fox jump group org next fox spring health care book fox tape java fox fox shop world fox";
		
		TermQuery query = new TermQuery(new Term("f", "fox"));

		QueryScorer scorer = new QueryScorer(query);

		Highlighter highlighter = new Highlighter(scorer);

		Fragmenter fragmenter = new SimpleFragmenter(5);
		highlighter.setTextFragmenter(fragmenter);

		Analyzer a = new KoreanAnalyzer(false);
		TokenStream tokenStream = a.tokenStream("f", new StringReader(text));

		String result =
		        highlighter.getBestFragments(tokenStream, text,2, "...");

		a.close();
		
		System.out.println(result);
		
		Assert.assertEquals(" <B>fox</B>... <B>fox</B>", result);
	}
	
	@Test
	public void fastVectorTest() throws IOException {
		//Directory dir = new RAMDirectory();
        //IndexWriter indexWriter = new IndexWriter(dir, new IndexWriterConfig(Lucene.VERSION, Lucene.STANDARD_ANALYZER));
 
        //indexWriter.addDocument(doc().add(field("_id", "1")).add(field("content", "the big bad dog", Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS)).build());
 
        //IndexReader reader = IndexReader.open(indexWriter, true);
        //IndexSearcher searcher = new IndexSearcher(reader);
        //TopDocs topDocs = searcher.search(new TermQuery(new Term("_id", "1")), 1);
 
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setKeyword("입니다");
		searchRequest.setCollectionName("sample");
		searchRequest.setSearchField("title,dscr");

		//IndexWriterManager indexWriterManager = IndexWriterManager.getIndexWriterManager();
		//IndexWriter indexWriter = indexWriterManager.getIndexWriter("sample");
		//IndexReader indexReader = IndexReader.open(indexWriter, true);
		
		CrescentSearcherManager crescentSearcherManager = CrescentSearcherManager.getCrescentSearcherManager();
		SearcherManager searcherManager = crescentSearcherManager.getSearcherManager("sample");
		IndexSearcher indexSearcher = searcherManager.acquire();
		
		IndexReader indexReader = indexSearcher.getIndexReader();
		
		TopDocs topDocs = indexSearcher.search(new TermQuery(new Term("dscr", "입니다")), 3);
		
		System.out.println("ddd : " + indexReader.document(0));
		System.out.println(topDocs.totalHits);

		FastVectorHighlighter highlighter = new FastVectorHighlighter();
		
		System.out.println("field query : " + highlighter.getFieldQuery(new TermQuery(new Term("dscr", "입니다"))));
		
		//System.out.println(topDocs.scoreDocs[0].doc);
		String fragment = highlighter.getBestFragment(highlighter.getFieldQuery(new TermQuery(new Term("dscr", "텍스트"))),
				indexReader, topDocs.scoreDocs[0].doc, "dscr", 30);
		
		System.out.println(fragment);
		
//        assertThat(topDocs.totalHits, equalTo(1));
// 
//        FastVectorHighlighter highlighter = new FastVectorHighlighter();
//        String fragment = highlighter.getBestFragment(highlighter.getFieldQuery(new TermQuery(new Term("content", "bad"))),
//                reader, topDocs.scoreDocs[0].doc, "content", 30);
//        assertThat(fragment, notNullValue());
//        System.out.println(fragment);
		
	}
	
	@Test public void testVectorHighlighter() throws Exception {
        Directory dir = new RAMDirectory();
        IndexWriter indexWriter 
        	= new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_36, new KoreanAnalyzer(true)));
 
        Document doc = new Document();
        Field f1 = new Field("_id", "1", Store.YES, Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS);
        Field f2 = new Field("content", "the big 입니다. dog", Store.YES, Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS);
   
        doc.add(f1);
        doc.add(f2);
        
        indexWriter.addDocument(doc);
 
        IndexReader reader = IndexReader.open(indexWriter, true);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(new TermQuery(new Term("_id", "1")), 1);
 
        System.out.println(topDocs.totalHits);
         
        FastVectorHighlighter highlighter = new FastVectorHighlighter();
        String fragment = highlighter.getBestFragment(highlighter.getFieldQuery(new TermQuery(new Term("content", "입니다"))),
        		reader, topDocs.scoreDocs[0].doc, "content", 30);
       
        System.out.println(fragment);
        
        searcher.close();
    }
}
