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
 */
public class SystemDocumentOperations {
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
                Logger.debug("Persisted document: " + documentName);
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
