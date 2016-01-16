package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Date;

/**
 * Created by prasad on 08/01/16.
 * SystemDocument model to persist documents posted on application
 */
@Entity
public class SystemDocument extends Model {
    @Id
    private Long systemDocumentID;

    @Constraints.Required
    private String documentName;

    @Constraints.Required
    private Date publishedDate;

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    @Constraints.Required
    @Lob
    private String documentContent;

    public static Finder<Long, SystemDocument> finder = new Finder<>(SystemDocument.class);

    public SystemDocument(String documentName,
                          Date publishedDate,
                          String documentContent) {
        this.documentContent = documentContent;
        this.documentName = documentName;
        this.publishedDate = publishedDate;
    }

    public Long getSystemDocumentID() {
        return systemDocumentID;
    }

    public String getDocumentName() {
        return documentName;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    /**
     * Persist document on db provided. If the document already exists,
     * we update the relevant document fields
     * @param systemDocument  system document to be saved
     */
    public static void persistDocument(SystemDocument systemDocument) {
        SystemDocument documentExist;
        if ((documentExist = finder.where().eq("documentName",
                systemDocument.getDocumentName()).findUnique()) != null) {
            documentExist.setPublishedDate(systemDocument.getPublishedDate());
            documentExist.setDocumentContent(systemDocument.getDocumentContent());
            documentExist.update();
        } else {
            systemDocument.save();
        }
    }

    /**
     * Delete a document by document ID
     * @param systemDocumentID  document ID to be deleted
     */
    public static boolean deleteDocument(long systemDocumentID) {
        SystemDocument document = finder.byId(systemDocumentID);
        if (document != null) {
            document.delete();
            return true;
        }
        return false;
    }
}
