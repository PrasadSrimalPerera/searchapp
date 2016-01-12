package ir;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.SystemDocument;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import play.Logger;
import java.io.IOException;

@Singleton
public class Indexer {
    @Inject
    private IrConfigurationManager irConfigurationManager;
    @Inject
    private RealTimeIrManager realTimeIrManager;

    @Inject
    public Indexer() {
    }

    public void setupIndex()  {
    }

    public void shutdownIndex() throws IOException {
    }

    private FieldType getIndexFieldType() {
        FieldType type = new FieldType();
        type.setStored(true);
        type.setTokenized(true);
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        return type;
    }

    public void addDocument(SystemDocument systemDocument) throws IOException {
        TrackingIndexWriter trackingIndexWriter = this.realTimeIrManager.getTrackingIndexWriter();
        Document newDocument = new Document();
        Field keyField = new StringField(IrConfigurationManager.INDEX_PATH,
                systemDocument.getDocumentName(),
                Field.Store.YES);
        newDocument.add(keyField);

        Field contentField = new Field(IrConfigurationManager.INDEXING_CONTENT_FIELD,
                systemDocument.getDocumentContent(),
                getIndexFieldType());
        newDocument.add(contentField);

        Field dateField = new StringField(IrConfigurationManager.INDEXING_DATE_FIELD,
                DateTools.dateToString(systemDocument.getPublishedDate(),
                DateTools.Resolution.SECOND), Field.Store.YES);
        newDocument.add(dateField);
        trackingIndexWriter.updateDocument(new Term(IrConfigurationManager.INDEXING_PATH_FIELD,
                systemDocument.getDocumentName()),
                newDocument);

        Logger.debug("Document " + systemDocument.getDocumentName() + "add/update successfully");
    }

    public void deleteDocument(SystemDocument systemDocument) throws IOException {
        TrackingIndexWriter trackingIndexWriter = this.realTimeIrManager.getTrackingIndexWriter();
        trackingIndexWriter.deleteDocuments(new Term(IrConfigurationManager.INDEXING_PATH_FIELD,
                systemDocument.getDocumentName()));
    }
}
