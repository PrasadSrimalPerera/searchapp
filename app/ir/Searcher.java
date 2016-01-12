package ir;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.postingshighlight.PostingsHighlighter;
import org.apache.lucene.store.FSDirectory;
import play.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by prasad on 11/01/16.
 */
@Singleton
public class Searcher {
    private IndexReader indexReader;
    private IndexSearcher indexSearcher;
    private QueryParser queryParser;

    @Inject
    private IrConfigurationManager irConfigurationManager;

    public void setupSearch() throws IOException {
        this.indexReader = DirectoryReader.open(FSDirectory
                .open(Paths.get(irConfigurationManager.getIndexPath())));
        this.indexSearcher = new IndexSearcher(this.indexReader);
        this.queryParser = new QueryParser("content", irConfigurationManager.getSystemDocumentAnalyzer());
    }

    public void shutdownSearch() throws IOException {
        this.indexReader.close();
    }

    public List<SystemDocumentResult> performSearch(String searchString, int from, int to) throws IOException {
        List<SystemDocumentResult> systemDocumentResultList = new ArrayList<>();
        try {
            Query query = this.queryParser.parse(searchString.trim());
            TopDocs topDocs = this.indexSearcher.search(query, irConfigurationManager.getMaxSearchHitCount());
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            int totalHits = topDocs.totalHits;
            from = Math.min(from * irConfigurationManager.getSearchPageSize(), totalHits);
            to = Math.min(to * irConfigurationManager.getSearchPageSize(), totalHits);
            PostingsHighlighter postingsHighlighter = new PostingsHighlighter();
            String[] highlights = postingsHighlighter.highlight("content", query, this.indexSearcher, topDocs);

            for (int i = from; i < to; ++i) {
                Document document = indexSearcher.doc(scoreDocs[i].doc);
                String path = document.get("path");
                String dateString = document.get("date");
                String snippet = highlights[i];
                Date publishDate = DateTools.stringToDate(dateString);
                systemDocumentResultList.add(new SystemDocumentResult(path,
                        publishDate,
                        snippet));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return systemDocumentResultList;
    }
}
