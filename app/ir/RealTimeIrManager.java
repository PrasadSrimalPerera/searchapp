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

    public void setupRealTimeIrManager() throws IOException {
        Directory directory = FSDirectory.open(Paths.get(irConfigurationManager.getIndexPath()));
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
                irConfigurationManager.getSystemDocumentAnalyzer());
        this.indexWriter = new IndexWriter(directory, indexWriterConfig);
        this.indexWriter.commit();

        this.trackingIndexWriter = new TrackingIndexWriter(this.indexWriter);
        this.indexSearcherReferenceManager = new SearcherManager(this.indexWriter,true, null);
        this.controlledRealTimeReopenThread = new ControlledRealTimeReopenThread<>(this.trackingIndexWriter,
                indexSearcherReferenceManager, TARGET_MAX_STALE_SEC, TARGET_MIN_STALE_SEC);

        this.controlledRealTimeReopenThread.start();
    }

    public void shutdownRealTimeIrManager() throws IOException {
        this.controlledRealTimeReopenThread.interrupt();
        this.controlledRealTimeReopenThread.close();
        this.indexWriter.commit();
        this.indexWriter.close();
    }

    public synchronized TrackingIndexWriter getTrackingIndexWriter() {
        return this.trackingIndexWriter;
    }

    public synchronized IndexSearcher getIndexSearcher() throws IOException {
       return this.indexSearcherReferenceManager.acquire();
    }
}
