package startups;

import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.event.ServerConfigStartup;

/**
 * Created by prasad on 08/01/16.
 */
public class SearchAppConfigStartup implements ServerConfigStartup {
    @Override
    public void onStart(ServerConfig serverConfig) {
        serverConfig.add(new SystemDocumentListener());
    }
}
