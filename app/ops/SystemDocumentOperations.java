package ops;

import models.SystemDocument;
import org.apache.commons.io.IOUtils;
import play.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;

/**
 * Created by prasad on 10/01/16.
 * A utility class to handle system document creation and persisting document
 */
public class SystemDocumentOperations {
    /**
     * Create and persist system document
     * @param documentName  document name
     * @param documentFile  recieved file from client applications
     * @return  true if successful
     */
    public static boolean createAndPersistDocument(String documentName,
                                                   File documentFile) {
        InputStream fileStream = null;
        if (documentFile != null) {
            try {
                fileStream = new FileInputStream(documentFile);
                String fileContent = IOUtils.toString(fileStream);
                SystemDocument newDocument = new SystemDocument(documentName,
                        Date.from(Instant.now()),
                        fileContent);
                SystemDocument.persistDocument(newDocument);
                return true;
            } catch (IOException e) {
                Logger.error("Error persisting document: " + documentName, e);
            }
            finally {
                try {
                    if (fileStream != null)
                        fileStream.close();
                } catch (IOException e) {
                    Logger.error("Error persisting document: " + documentName, e);
                }
            }
        }
        return false;
    }
}
