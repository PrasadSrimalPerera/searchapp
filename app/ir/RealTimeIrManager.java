package ir;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by prasad on 12/01/16.
 * RealTimeIrManager initialize all the indexing/searching related Lucene components.
 * In order implement Lucene near real time search, here we initalize:
 * TrackingIndexWriter
 * ReferenceManager/SearchManager
 * ControlledRealTimeReopenThread
 * However, in order to simply and let controller update new generations,
 * we do not wait for new generations provided by TrackingIndexWriter
 * As an effect, the search results start to update according to the given
 * TARGET_MAX_STALE_SEC, TARGET_MIN_STALE_SEC parameters
 */
@Singleton
public class RealTimeIrManager {
    private IndexWriter indexWriter;
    private TrackingIndexWriter trackingIndexWriter;
    private ReferenceManager<IndexSearcher> indexSearcherReferenceManager;
    private ControlledRealTimeReopenThread<IndexSearcher> controlledRealTimeReopenThread;
    private static final double TARGET_MAX_STALE_SEC = 60.0f;
    private static final double TARGET_MIN_STALE_SEC = 0.1f;

    @Inject
    IrConfigurationManager irConfigurationManager;

    @Inject
    public RealTimeIrManager() {
    }

    /**
     * setup indexing /searching related components at the application startup
     * @throws IOException
     */
    public void setupRealTimeIrManager() throws IOException {
        // Initialize index writer components
        Directory directory = FSDirectory.open(Paths.get(irConfigurationManager.getIndexPath()));
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
                irConfigurationManager.getSystemDocumentAnalyzer());
        this.indexWriter = new IndexWriter(directory, indexWriterConfig);
        this.indexWriter.commit();

        this.trackingIndexWriter = new TrackingIndexWriter(this.indexWriter);

        // Initialize index searcher related components
        this.indexSearcherReferenceManager = new SearcherManager(this.indexWriter,true, null);
        this.controlledRealTimeReopenThread = new ControlledRealTimeReopenThread<>(this.trackingIndexWriter,
                indexSearcherReferenceManager, TARGET_MAX_STALE_SEC, TARGET_MIN_STALE_SEC);

        this.controlledRealTimeReopenThread.start();
    }

    /**
     * shutdown Lucene components when application is stopped
     * @throws IOException
     */
    public void shutdownRealTimeIrManager() throws IOException {
        this.controlledRealTimeReopenThread.interrupt();
        this.controlledRealTimeReopenThread.close();
        this.indexWriter.commit();
        this.indexWriter.close();
    }

    /**
     * Retrieve tracking index writer to commit index operations
     * @return tracking index writer
     */
    public synchronized TrackingIndexWriter getTrackingIndexWriter() {
        return this.trackingIndexWriter;
    }

    /**
     * Retrieve an index searcher for search operations.
     * This will be updated based on new document being indexed
     * @return index searcher
     * @throws IOException
     */
    public synchronized IndexSearcher getIndexSearcher() throws IOException {
       return this.indexSearcherReferenceManager.acquire();
    }
}
