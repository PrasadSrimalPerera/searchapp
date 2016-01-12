package ir;

import akka.actor.UntypedActor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.SystemDocument;
import java.io.IOException;

/**
 * Created by prasad on 10/01/16.
 */
@Singleton
public class IndexerActor extends UntypedActor {
    @Inject
    private Indexer indexer;

    @Inject
    public IndexerActor() {
    }

    public static class IndexOperation {
        public static class AddIndex {
            private SystemDocument document;
            public AddIndex(SystemDocument document) {
                this.document = document;
            }
        }
        public static class DeleteIndex {
            private SystemDocument document;
            public DeleteIndex(SystemDocument document) {
                this.document = document;
            }
        }
    }

    @Override
    public void preStart() throws IOException {
        indexer.setupIndex();
    }

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

    public static String getIndexerActorID() {
        return "user/" + IrConfigurationManager.INDEXER_ACTOR;
    }
}
