import ir.RealTimeIrManager;
import play.Application;
import play.GlobalSettings;
import play.inject.Injector;
import java.io.IOException;

/**
 * Created by prasad on 12/01/16.
 * Global provides an extension of GlobalSettings
 * We use Global to run important setup/teardown
 * of DI created entities
 * (ex. RealTimeIrManager)
 */
public class Global extends GlobalSettings {
    public void onStart(Application app) {
        Injector injector = app.injector();
        RealTimeIrManager realTimeIrManager = injector.instanceOf(RealTimeIrManager.class);
        if (realTimeIrManager != null) {
            try {
                realTimeIrManager.setupRealTimeIrManager();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onStop(Application app) {
        Injector injector = app.injector();
        RealTimeIrManager realTimeIrManager = injector.instanceOf(RealTimeIrManager.class);
        if (realTimeIrManager != null) {
            try {
                realTimeIrManager.shutdownRealTimeIrManager();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
