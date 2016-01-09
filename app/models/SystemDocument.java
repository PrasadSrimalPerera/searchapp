package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Date;

/**
 * Created by prasad on 08/01/16.
 */
@Entity
public class SystemDocument extends Model {
    @Id
    private Long systemDocumentID;

    @Constraints.Required
    private String documentName;

    @Constraints.Required
    private Date publishedDate;

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

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public static void persistDocument(SystemDocument systemDocument) {
        SystemDocument documentExist;
        if (finder.fetch("systemDocumentID").where().eq("documentName",
                systemDocument.getDocumentName()).findUnique() != null) {
                systemDocument.update();
        }
        else {
            systemDocument.save();
        }
    }
}
