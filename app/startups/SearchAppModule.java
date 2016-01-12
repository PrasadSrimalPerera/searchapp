package startups;

import com.google.inject.AbstractModule;
import ir.*;
import play.libs.akka.AkkaGuiceSupport;

/**
 * Created by prasad on 10/01/16.
 */
public class SearchAppModule extends AbstractModule implements AkkaGuiceSupport{
    @Override
    protected void configure() {
        bind(IrConfigurationManager.class);
        bind(Indexer.class);
        bind(Searcher.class);
        bindActor(IndexerActor.class, "indexer_actor");
        bindActor(SearcherActor.class, "searcher_actor");
    }
}
