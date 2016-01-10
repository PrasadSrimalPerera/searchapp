package ir;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.SystemDocument;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import play.Logger;
import java.io.IOException;
import java.nio.file.Paths;

@Singleton
public class Indexer {
    private Directory indexDirectory;
    private IndexWriterConfig indexWriterConfig;
    private IndexWriter indexWriter;

    @Inject
    private IrConfigurationManager irConfigurationManager;

    @Inject
    public Indexer() {
    }

    public void setupIndex()  {
        try {
            this.indexDirectory = FSDirectory.open(Paths.get(irConfigurationManager.getIndexPath()));
            this.indexWriterConfig = new IndexWriterConfig(irConfigurationManager.getSystemDocumentAnalyzer());
            this.indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            this.indexWriter = new IndexWriter(this.indexDirectory, this.indexWriterConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdownIndex() throws IOException {
        this.indexDirectory.close();
        this.indexWriter.close();
    }

    public void addDocument(SystemDocument systemDocument) throws IOException {
        Document newDocument = new Document();
        Field keyField = new StringField("path", systemDocument.getDocumentName(), Field.Store.YES);
        newDocument.add(keyField);

        Field contentField = new TextField("content", systemDocument.getDocumentContent(),
                Field.Store.NO);
        newDocument.add(contentField);

        Field dateField = new StringField("date", DateTools.dateToString(systemDocument.getPublishedDate(),
                DateTools.Resolution.SECOND), Field.Store.YES);
        newDocument.add(dateField);
        this.indexWriter.updateDocument(new Term(systemDocument.getDocumentName()), newDocument);
        Logger.debug("Document " + systemDocument.getDocumentName() + "add/update successfully");
    }

    public void deleteDocument(SystemDocument systemDocument) throws IOException {
        this.indexWriter.deleteDocuments(new Term(systemDocument.getDocumentName()));
    }
}
