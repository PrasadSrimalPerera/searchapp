package controllers;

import com.google.inject.Inject;
import ir.Indexer;
import ir.IrConfigurationManager;
import ops.SystemDocumentOperations;
import play.Logger;
import play.libs.F;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {
    @Inject
    Indexer indexer;
    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public F.Promise<Result> postDocument()  {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart systemDocumentFile = body.getFile("systemdocument");
        return persistDocument(systemDocumentFile);
    }

    private static F.Promise<Result> persistDocument(Http.MultipartFormData.FilePart documentFile) {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                if (SystemDocumentOperations.createAndPersistDocument(documentFile.getFilename(),
                        documentFile.getFile())) {
                    return ok("SUCCESS");
                }
                return status(BAD_REQUEST);
            }
        });
    }
}
