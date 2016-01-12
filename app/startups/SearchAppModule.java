package startups;

import com.google.inject.AbstractModule;
import ir.Indexer;
import ir.Searcher;
import ir.RealTimeIrManager;
import ir.IrConfigurationManager;
import ir.SearcherActor;
import ir.IndexerActor;
import play.libs.akka.AkkaGuiceSupport;

/**
 * Created by prasad on 10/01/16.
 */
public class SearchAppModule extends AbstractModule implements AkkaGuiceSupport{
    @Override
    protected void configure() {
        bind(IrConfigurationManager.class);
        bind(RealTimeIrManager.class);
        bind(Indexer.class);
        bind(Searcher.class);
        bindActor(IndexerActor.class, IrConfigurationManager.INDEXER_ACTOR);
        bindActor(SearcherActor.class, IrConfigurationManager.SEARCHER_ACTOR);
    }
}
