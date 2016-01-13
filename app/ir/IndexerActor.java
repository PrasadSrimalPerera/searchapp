package ir;

import akka.actor.UntypedActor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.SystemDocument;
import java.io.IOException;

/**
 * Created by prasad on 10/01/16.
 * IndexerActor provides an UntypedActor to handle indexing related document operation
 * asynchronously.
 */
@Singleton
public class IndexerActor extends UntypedActor {
    /**
     * Play DI references
     */
    @Inject
    private Indexer indexer;

    @Inject
    public IndexerActor() {
    }

    /**
     * IndexingOperation class provides the objects used to communicate
     * with the IndexerActor as operations.
     */
    public static class IndexOperation {
        /**
         * AddIndex represents the add system document operation
         */
        public static class AddIndex {
            private SystemDocument document;
            public AddIndex(SystemDocument document) {
                this.document = document;
            }
        }

        /**
         * DeleteIndex represents delete system document operation
         */
        public static class DeleteIndex {
            private SystemDocument document;
            public DeleteIndex(SystemDocument document) {
                this.document = document;
            }
        }
    }

    /**
     * Provide the functionality to setup indexer related configurations
     * @throws IOException
     */
    @Override
    public void preStart() throws IOException {
        indexer.setupIndex();
    }

    /**
     * Provide the functionality to teardown/shutdown indexer related configurations
     * @throws IOException
     */
    @Override
    public void postStop() throws IOException {
        indexer.shutdownIndex();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof  IndexOperation.AddIndex) {
            indexer.addDocument(((IndexOperation.AddIndex)message).document);
        }
        else if (message instanceof IndexOperation.DeleteIndex) {
            indexer.deleteDocument(((IndexOperation.DeleteIndex)message).document);
        }
    }

    /**
     * Retrieve indexer actor ID
     * @return String value of indexer actor ID
     */
    public static String getIndexerActorID() {
        return "user/" + IrConfigurationManager.INDEXER_ACTOR;
    }
}
