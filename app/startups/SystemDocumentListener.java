package startups;

import akka.actor.ActorRef;
import com.avaje.ebean.event.BeanPersistListener;
import ir.IndexerActor;
import models.SystemDocument;
import play.libs.Akka;
import java.util.Set;

/**
 * Created by prasad on 08/01/16.
 * An extension of BeanPersistListener which implements proper action with
 * the index when system documents are changed (added/updated etc.)
 */
public class SystemDocumentListener implements BeanPersistListener {

    @Override
    public boolean isRegisterFor(Class<?> aClass) {
        if (aClass == SystemDocument.class)
            return true;
        else
            return false;
    }

    @Override
    public boolean inserted(Object o) {
        SystemDocument document = (SystemDocument) o;
        ActorRef indexerActor = Akka.system().actorFor(IndexerActor.getIndexerActorID());
        indexerActor.tell(new IndexerActor.IndexOperation.AddIndex(document), ActorRef.noSender());
        return true;
    }

    @Override
    public boolean updated(Object o, Set<String> set) {
        SystemDocument document = (SystemDocument) o;
        ActorRef indexerActor = Akka.system().actorFor(IndexerActor.getIndexerActorID());
        indexerActor.tell(new IndexerActor.IndexOperation.AddIndex(document), ActorRef.noSender());
        return true;
    }

    @Override
    public boolean deleted(Object o) {
        return false;
    }

    @Override
    public void remoteInsert(Object o) {

    }

    @Override
    public void remoteUpdate(Object o) {

    }

    @Override
    public void remoteDelete(Object o) {

    }
}
