package ir;

import akka.actor.UntypedActor;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.util.List;

/**
 * Created by prasad on 11/01/16.
 * SearcherActor provides an UntypedActor for searcher related operations
 * for the application (similar to IndexerActor and support async search operations
 * on the application)
 */
@Singleton
public class SearcherActor extends UntypedActor {
    @Inject
    private Searcher searcher;

    /**
     * Provides setup related configurations for the searcher
     * @throws IOException
     */
    @Override
    public void preStart() throws IOException {
        searcher.setupSearch();
    }

    /**
     * Provides shutdown/teardown related operations for the searcher
     * @throws IOException
     */
    @Override
    public void postStop() throws IOException {
        searcher.shutdownSearch();
    }

    /**
     * SearchOperation class provides the search operations to be
     * sent and completed by the SearchActor
     */
    public static class SearchOperation {
        private int fromPage = 0;
        private int toPage = 0;
        private String queryString;

        public SearchOperation(int fromPage, int toPage, String queryString) {
            this.fromPage = fromPage;
            this.toPage = toPage;
            this.queryString = queryString;
        }
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof  SearchOperation) {
            SearchOperation searchOperation = (SearchOperation) message;
            List<SystemDocumentResult> systemDocumentResultsList =
                    this.searcher.performSearch(searchOperation.queryString,
                    searchOperation.fromPage,
                    searchOperation.toPage);
            // Send back search results
            getSender().tell(systemDocumentResultsList, self());
        }
    }

    /**
     * Retrieve the searcher actor ID
     * @return searcher actor ID string
     */
    public static String getSearcherActorID() {
        return "user/" + IrConfigurationManager.SEARCHER_ACTOR;
    }
}
