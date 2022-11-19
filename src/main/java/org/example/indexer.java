package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.nodes.Document;

import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

public class combined_indexing {
    public static void combined_indexer(String[] args) throws IOException {
        String[] parsed_docs = {"./output/parsed_docs/fbis.json", "./output/parsed_docs/fr94.json",
                "./output/parsed_docs/ft.json", "./output/parsed_docs/latimes.json"};
        String INDEX_DIRECTORY = "./index";


        Analyzer analyzer  = new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        config.setSimilarity(new BM25Similarity());
        config.setRAMBufferSizeMB(50);
        IndexWriter iwriter = new IndexWriter(directory, config);

        for (String docs : parsed_docs)
        {
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = null;

            try {
                jsonArray = (JSONArray) jsonParser.parse(new FileReader(docs));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println("Reading done.\n");
            System.out.println("Creating index of " + docs + " using English analyzer and BM25 similarity...");

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
                Set<String> jsonElements = jsonObject.keySet();
                for (String element : jsonElements){
                    document.add(new TextField(element, (String) jsonObject.get(element), Field.Store.YES));
                }
                iwriter.addDocument(document);
            }
            System.out.println("Indexing done of " + docs + ", and saved on disk.");
        }
        iwriter.close();
        directory.close();
        System.out.println("Resultant Indices Stored in Location: " + INDEX_DIRECTORY);
    }
}

