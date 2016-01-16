package controllers;

import akka.actor.ActorRef;
import com.google.gson.Gson;
import ir.*;
import models.SystemDocument;
import ops.SystemDocumentOperations;
import play.Logger;
import play.data.Form;
import play.libs.Akka;
import play.libs.F;
import play.mvc.*;
import views.html.*;
import java.util.List;

/**
 * Controller class that exposes Rest API functionalities to
 * POST system documents to the system and GET system document search results
 */
public class SearchApplication extends Controller {
    /**
     * search results timeout for akka asynchronous search result calls
     */
    private static final int SEARCH_TIMEOUT = 1000;

    /**
     * form parameters
     */
    private static final String SYSTEM_DOCUMENT_PARAM = "systemdocument";
    private static final String QUERY_STRING = "queryString";
    private static final String FROM_PAGE = "fromPage";
    private static final String TO_PAGE = "toPage";

    /**
     * convert system result objects to JSON to be send to the client apps
     */
    private static Gson gson = new Gson();

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    /**
     * POST system document
     * @return  Result with status of persisting system document
     */
    public F.Promise<Result> postDocument() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart systemDocumentFile = body.getFile(SYSTEM_DOCUMENT_PARAM);
        return persistDocument(systemDocumentFile);
    }

    /**
     * GET search for document results.
     * @return  Promise containing system document search results.
     */
    public F.Promise<Result> performSearch() {
        String queryString = Form.form().bindFromRequest().get(QUERY_STRING);
        int fromPage = Integer.valueOf(Form.form().bindFromRequest().get(FROM_PAGE));
        int toPage = Integer.valueOf(Form.form().bindFromRequest().get(TO_PAGE));
        if (!queryString.isEmpty() && fromPage >= 0 && toPage > 0) {

            Logger.debug("Receive search query:" + queryString);

            ActorRef searcherActor = Akka.system().actorFor(SearcherActor.getSearcherActorID());
            return F.Promise.wrap(akka.pattern.Patterns.ask(searcherActor,
                    new SearcherActor.SearchOperation(fromPage,
                            toPage,
                            queryString), SEARCH_TIMEOUT)).
                    map(new F.Function<Object, Result>() {
                        @Override
                        public Result apply(Object o) throws Throwable {
                            @SuppressWarnings("unchecked")
                            List<SystemDocumentResult> results = (List<SystemDocumentResult>) o;
                            return ok(gson.toJson(results));
                        }
                    });
        }
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                return badRequest("Invalid Parameters");
            }
        });
    }

    /**
     * DELETE operation to remove System document from repository & search
     * @param systemDocumentID  document ID to be deleted
     * @return  delete operation status
     */
    public Result deleteSystemDocument(long systemDocumentID) {
        if (SystemDocument.deleteDocument(systemDocumentID))
            return ok("SUCCESS");
        return badRequest("Invalid Parameter");
    }

    /**
     * persist document async with returning a promise
     * @param documentFile  the form multi-part data
     * @return  Promise containing persisting result
     */
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
