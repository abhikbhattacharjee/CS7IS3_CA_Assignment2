package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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

public class indexer {
    public static void indexer(String[] args) throws IOException {
        String fbis_parsed_doc = "./output/parsed_docs/fbis.json";
        String INDEX_DIRECTORY = "./index";


        Analyzer analyzer  = new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter iwriter = new IndexWriter(directory, config);

        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = null;

        try {
            jsonArray = (JSONArray) jsonParser.parse(new FileReader(fbis_parsed_doc));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        //System.out.println(jsonArray);

        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            //System.out.println(jsonObject);
            org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
            Set<String> jsonElements = jsonObject.keySet();
            //System.out.println(jsonElements);
            for (String element : jsonElements){
                /*System.out.println("*************");
                System.out.println(element);
                System.out.println(jsonObject.get(element));*/
                document.add(new TextField(element, (String) jsonObject.get(element), Field.Store.YES));
                //System.out.println(document);
                iwriter.addDocument(document);
            }
        }

        iwriter.close();
        directory.close();

        System.out.println("Resultant Indices Stored in Location: " + INDEX_DIRECTORY);
    }
}
