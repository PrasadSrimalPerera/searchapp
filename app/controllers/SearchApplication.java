package controllers;

import akka.actor.ActorRef;
import com.google.gson.Gson;
import ir.*;
import ops.SystemDocumentOperations;
import play.Logger;
import play.libs.Akka;
import play.libs.F;
import play.mvc.*;
import views.html.*;
import java.util.List;


public class SearchApplication extends Controller {
    private static final int SEARCH_TIMEOUT = 1000;
    private static final String systemDocumentParam = "systemdocument";

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public F.Promise<Result> postDocument() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart systemDocumentFile = body.getFile(systemDocumentParam);
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

    public F.Promise<Result> performSearch(String queryString, int fromPage, int toPage) {
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
                            List<SystemDocumentResult> results = (List<SystemDocumentResult>) o;
                            Gson gson = new Gson();
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
}
