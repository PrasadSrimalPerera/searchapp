package ir;

import java.util.Date;

/**
 * Created by prasad on 11/01/16.
 */
public class SystemDocumentResult {
    private String systemdocumentName;
    private Date documentPublishDate;
    private String documentSnippet;

    public SystemDocumentResult(String systemdocumentName,
                                Date documentPublishDate,
                                String documentSnippet) {
        this.systemdocumentName = systemdocumentName;
        this.documentPublishDate = documentPublishDate;
        this.documentSnippet = documentSnippet;
    }

    public String getSystemdocumentName() {
        return systemdocumentName;
    }

    public Date getDocumentPublishDate() {
        return documentPublishDate;
    }

    public String getDocumentSnippet() {
        return documentSnippet;
    }

    public void setDocumentSnippet(String documentSnippet) {
        this.documentSnippet = documentSnippet;
    }
}
