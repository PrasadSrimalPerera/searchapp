package ir;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import play.Configuration;

/**
 * Created by prasad on 10/01/16.
 * IrConfigurationManager contains the ir (indexing/searching) related configurations,
 * parameters, field values shared between indexer,searcher classes.
 */
@Singleton
public class IrConfigurationManager {
    /**
     * Indexer/Searcher  parameters
     */
    private  String indexPath;
    private  Analyzer systemDocumentAnalyzer;
    private Integer maxSearchHitCount = -1;
    private Integer searchPageSize = -1;

    /**
     * Indexer,Searcher related external parameters defined in .conf
     */
    public static final String INDEX_PATH = "index.path";
    public static final String SEARCH_MAX_HITS = "search.maxhits";
    public static final String SEARCH_PAGE_SIZE = "search.pagesize";
    private static final String DEFAULT_INDEX_PATH = "index";
    private static final int DEFAULT_MAX_HITS = 1000;
    private static final int DEFAULT_PAGE_SIZE = 30;

    /**
     * Indexing/Searching field definisions
     */
    public static final String INDEXING_PATH_FIELD = "path";
    public static final String INDEXING_DATE_FIELD = "date";
    public static final String INDEXING_CONTENT_FIELD = "content";
    public static final String INDEXER_ACTOR = "indexer_actor";
    public static final String SEARCHER_ACTOR = "searcher_actor";

    @Inject
    @SuppressWarnings(value = "unassigned")
    private Configuration configuration;

    @Inject
    public IrConfigurationManager() {
    }

    /**
     * Retrieve system document analyzer
     * @return  analyzer
     */
    public  synchronized  Analyzer getSystemDocumentAnalyzer() {
        if (systemDocumentAnalyzer == null) {
            systemDocumentAnalyzer = new StandardAnalyzer();
        }
        return systemDocumentAnalyzer;
    }

    /**
     * Retrieve system document indexing path
     * @return indexing path string
     */
    public synchronized String getIndexPath() {
        if (indexPath == null)
            indexPath = configuration.getString(INDEX_PATH, DEFAULT_INDEX_PATH);
        return indexPath;
    }

    /**
     * Retrieve maximum search hits configured by the applocation
     * @return max hit count
     */
    public Integer getMaxSearchHitCount() {
        if (maxSearchHitCount == -1 )
            maxSearchHitCount = configuration.getInt(SEARCH_MAX_HITS, DEFAULT_MAX_HITS);
        return maxSearchHitCount;
    }

    /**
     * Retrieve the search page size
     * @return  search page size
     */
    public Integer getSearchPageSize() {
        if (searchPageSize == -1)
            searchPageSize = configuration.getInt(SEARCH_PAGE_SIZE, DEFAULT_PAGE_SIZE);
        return searchPageSize;
    }
}
