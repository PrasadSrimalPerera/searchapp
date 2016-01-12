package ir;

import akka.actor.UntypedActor;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.util.List;

/**
 * Created by prasad on 11/01/16.
 */
@Singleton
public class SearcherActor extends UntypedActor {
    @Inject
    private Searcher searcher;

    @Override
    public void preStart() throws IOException {
        searcher.setupSearch();
    }

    @Override
    public void postStop() throws IOException {
        searcher.shutdownSearch();
    }

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
            getSender().tell(systemDocumentResultsList, self());
        }
    }

    public static String getSearcherActorID() {
        return "user/" + IrConfigurationManager.SEARCHER_ACTOR;
    }
}
