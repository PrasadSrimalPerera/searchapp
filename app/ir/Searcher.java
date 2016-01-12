package ir;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.postingshighlight.PostingsHighlighter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by prasad on 11/01/16.
 */
@Singleton
public class Searcher {
    private QueryParser queryParser;
    @Inject
    private IrConfigurationManager irConfigurationManager;
    @Inject
    private RealTimeIrManager realTimeIrManager;

    public void setupSearch() throws IOException {
        this.queryParser = new QueryParser(IrConfigurationManager.INDEXING_CONTENT_FIELD,
                irConfigurationManager.getSystemDocumentAnalyzer());
    }

    public void shutdownSearch() {

    }

    public List<SystemDocumentResult> performSearch(String searchString, int from, int to) throws IOException {
        List<SystemDocumentResult> systemDocumentResultList = new ArrayList<>();

        try {
            IndexSearcher indexSearcher = this.realTimeIrManager.getIndexSearcher();
            Query query = this.queryParser.parse(searchString.trim());
            TopDocs topDocs = indexSearcher.search(query, irConfigurationManager.getMaxSearchHitCount());
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;

            int totalHits = topDocs.totalHits;
            from = Math.min(from * irConfigurationManager.getSearchPageSize(), totalHits);
            to = Math.min(to * irConfigurationManager.getSearchPageSize(), totalHits);
            PostingsHighlighter postingsHighlighter = new PostingsHighlighter();
            String[] highlights = postingsHighlighter.highlight(IrConfigurationManager.INDEXING_CONTENT_FIELD,
                    query,
                    indexSearcher,
                    topDocs);

            for (int i = from; i < to; ++i) {
                Document document = indexSearcher.doc(scoreDocs[i].doc);
                String path = document.get(IrConfigurationManager.INDEXING_PATH_FIELD);
                String dateString = document.get(IrConfigurationManager.INDEXING_DATE_FIELD);
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
