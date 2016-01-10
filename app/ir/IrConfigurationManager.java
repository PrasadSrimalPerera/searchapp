package ir;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import play.Configuration;

/**
 * Created by prasad on 10/01/16.
 */
@Singleton
public class IrConfigurationManager {
    private  String indexPath;
    private  Analyzer systemDocumentAnalyzer;

    @Inject
    private Configuration configuration;

    @Inject
    public IrConfigurationManager() {
    }

    public  synchronized  Analyzer getSystemDocumentAnalyzer() {
        if (systemDocumentAnalyzer == null) {
            systemDocumentAnalyzer = new StandardAnalyzer();
        }
        return systemDocumentAnalyzer;
    }

    public synchronized String getIndexPath() {
        if (indexPath == null)
            indexPath = configuration.getString("index.path", "index");
        return indexPath;
    }

}
