package startups;

import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.event.ServerConfigStartup;

/**
 * Created by prasad on 08/01/16.
 * Extending ServerConfigStartup to add Bean listener to receive
 * call backs from db layer changes (system document add/update/delete etc.)
 */
public class SearchAppConfigStartup implements ServerConfigStartup {
    @Override
    public void onStart(ServerConfig serverConfig) {
        serverConfig.add(new SystemDocumentListener());
    }
}
