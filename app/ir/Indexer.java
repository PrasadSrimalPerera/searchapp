package ir;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.SystemDocument;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import play.Logger;
import java.io.IOException;

/**
 * Indexer class provides the functionality to call Lucene APIs in
 * indexing related operations (add/update/delete Document elements)
 */
@Singleton
public class Indexer {
    /**
     * PLay DI references
     */
    @Inject
    private IrConfigurationManager irConfigurationManager;
    @Inject
    private RealTimeIrManager realTimeIrManager;
    @Inject
    public Indexer() {
    }

    /**
     * setup Indexer related configurations
     * Nothing to be included here for now
     */
    public void setupIndex()  {
    }

    /**
     * shutdown/teardown indexer related configurations
     * Nothing to be included here for now
     * @throws IOException
     */
    public void shutdownIndex() throws IOException {
    }

    /**
     * Retrieve Indexing Field type for document content.
     * This includes tokenizing/analyzing/indexing and highlighter generation
     * required parameters
     * @return field type created
     */
    private FieldType getIndexFieldType() {
        FieldType type = new FieldType();
        type.setStored(true);
        type.setTokenized(true);
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        return type;
    }

    /**
     * Add a system document to the index
     * @param systemDocument    input system document
     * @throws IOException
     */
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

    /**
     * Delete system document from index
     * @param systemDocument    System document to be deleted
     * @throws IOException
     */
    public void deleteDocument(SystemDocument systemDocument) throws IOException {
        TrackingIndexWriter trackingIndexWriter = this.realTimeIrManager.getTrackingIndexWriter();
        trackingIndexWriter.deleteDocuments(new Term(IrConfigurationManager.INDEXING_PATH_FIELD,
                systemDocument.getDocumentName()));
    }
}
