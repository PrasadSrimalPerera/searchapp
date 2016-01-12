package ir;

import java.util.Date;

/**
 * Created by prasad on 11/01/16.
 */
public class SystemDocumentResult {
    private String systemDocumentName;
    private Date documentPublishDate;
    private String documentSnippet;

    public SystemDocumentResult(String systemdocumentName,
                                Date documentPublishDate,
                                String documentSnippet) {
        this.systemDocumentName = systemdocumentName;
        this.documentPublishDate = documentPublishDate;
        this.documentSnippet = documentSnippet;
    }

    public String getsystemDocumentName() {
        return systemDocumentName;
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
