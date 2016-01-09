package controllers;
import models.SystemDocument;
import org.apache.commons.io.IOUtils;
import play.mvc.*;
import views.html.*;

import java.io.*;
import java.time.Instant;
import java.util.Date;


public class Application extends Controller {
    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public Result postDocument()  {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart systemdocumentFile = body.getFile("systemdocument");

        if (systemdocumentFile != null) {
            String documentName = systemdocumentFile.getFilename();
            try {
                FileInputStream documentFile = new FileInputStream(systemdocumentFile.getFile());
                String fileContent = IOUtils.toString(documentFile);
                SystemDocument newDocument = new SystemDocument(documentName,
                        Date.from(Instant.now()),
                        fileContent);
                newDocument.save();
                return ok();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return badRequest();
    }
}
