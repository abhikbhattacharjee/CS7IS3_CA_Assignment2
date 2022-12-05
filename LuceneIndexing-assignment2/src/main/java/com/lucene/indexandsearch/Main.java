package com.lucene.indexandsearch;

import com.lucene.indexandsearch.fbis.FBISIndexer;
import com.lucene.indexandsearch.fr94.FR94Indexer;
import com.lucene.indexandsearch.ft.FTIndexer;
import com.lucene.indexandsearch.indexer.DocumentIndexer;
import com.lucene.indexandsearch.latimes.LATIMESIndexer;
import com.lucene.indexandsearch.searcher.Searcher;
import com.lucene.indexandsearch.utils.Constants;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.codehaus.plexus.util.FileUtils; //write in report

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

import static com.lucene.indexandsearch.utils.Constants.*;


public class Main {

    private DocumentModel docModel;
    public DocumentIndexer diFbis;
    public DocumentIndexer diLatimes;
    public DocumentIndexer diFr94;
    public DocumentIndexer diFt;

    public Main(String docType) throws IOException {
        System.out.println("About to Index Files for Document type: " + docType);
        setDocParser(docType);
        selectDocumentParser(docModel);
    }

    private enum DocumentModel {
        CRAN, FBIS, LATTIMES, FR94, FT
    }

    private void setDocParser(String val) {
        try {
            docModel = DocumentModel.valueOf(val.toUpperCase());
        } catch (Exception e) {
            System.out.println("Document Parser Not Recognized - Setting to Default");
            System.out.println("Possible Document Parsers are:");
            for (DocumentModel value : DocumentModel.values()) {
                System.out.println("<indexType>" + value.name() + "</indexType>");
            }
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void selectDocumentParser(DocumentModel dm) throws IOException {
        docModel = dm;
        diFbis = null;
        diLatimes = null;
        diFr94 = null;
        diFt = null;

        if (dm == DocumentModel.FBIS) {
            new FBISIndexer(Constants.INDEXPATH);
        } else if (dm == DocumentModel.LATTIMES) {
            new LATIMESIndexer(Constants.INDEXPATH);
        } else if (dm == DocumentModel.FR94) {
            new FR94Indexer(INDEXPATH);
        } else if (dm == DocumentModel.FT) {
            new FTIndexer(Constants.INDEXPATH);
        } else {
            System.out.println("Default Document Parser");
        }
    }

    public void finished() {
        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(Constants.INDEXPATH)));
            long numDocs = reader.numDocs();
            System.out.println("Number of docs indexed: " + numDocs);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void createIndex(String indexData, String indexType) throws IOException {
        Instant startTime = Instant.now();
        Main indexer = new Main(indexType);
        try {
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            indexer.finished();
        }
        Instant finishTime = Instant.now();
        long timeElapsed = Duration.between(startTime, finishTime).toMillis();
        System.out.println("Done building Index for " + indexType);
    }

    public static void main(String[] args) throws Exception {
        String[] argToSearcher;
        System.out.println("Running Indexer");
        FileUtils.cleanDirectory(INDEXPATH);
        createIndex(FT_FILESPATH, FTINDEXTYPE);
        createIndex(FBISFILESPATH, FBISINDEXTYPE);
        createIndex(LATIMES_FILESPATH, LATTIMESINDEXTYPE);
        createIndex(FR94FILESPATH, FR94INDEXTYPE);
        System.out.println("Indexing Completed");
        Constants.MODELUSED = MODELBM25;
        argToSearcher = new String[]{Constants.MODELUSED};
        Searcher.main(argToSearcher);

    }
}