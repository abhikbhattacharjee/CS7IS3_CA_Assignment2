package org.example;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.et.EstonianAnalyzer;
import org.apache.lucene.analysis.standard.ClassicAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import java.util.Scanner;

public class queries {

    // the location of the search index
    private static String INDEX_DIRECTORY = "./index";
    private static String QUERY_DIRECTORY = "../topics.txt";
    private static String WRITE_PATH = "./output";
    // Limit the number of search results we get


    public static void queries(String[] args) throws IOException, ParseException {
        // Make sure we were given something to index

        String queryString = null;
        int hitsPerPage = 1000;
        // Analyzer used by the query parser.
        // Must be the same as the one used when creating the index
//          Analyzer analyzer = new ClassicAnalyzer();
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer  = new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
//        Analyzer analyzer = new SimpleAnalyzer();
//        Analyzer analyzer = new WhitespaceAnalyzer();
//        Analyzer analyzer = new EstonianAnalyzer();
//        if(args.length!=0) {
//            if (args[1].equals("0")) {
//                analyzer= new StandardAnalyzer();
//                System.out.println("using StandardAnalyzer");
//            }
//            if (args[1].equals("1")) {
//                analyzer = new StopAnalyzer();
//                System.out.println("using StopAnalyzer");
//            }
//            if (args[1].equals("2")) {
//                analyzer = new SimpleAnalyzer();
//                System.out.println("using SimpleAnalyzer");
//            }
//            if (args[1].equals("3")) {
//                analyzer = new KeywordAnalyzer();
//                System.out.println("using KeywordAnalyzer");
//            }
//            if (args[1].equals("4")) {
//                analyzer = new EnglishAnalyzer();
//                System.out.println("using EnglishAnalyzer");
//            }
//        }
        // Open the folder that contains our search index
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // create objects to read and search across the index
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        isearcher.setSimilarity(new ClassicSimilarity());
//        isearcher.setSimilarity(new BooleanSimilarity());
//        isearcher.setSimilarity(new LMDirichletSimilarity());
//        isearcher.setSimilarity(new LMJelinekMercerSimilarity((float) 0.7));

//        if(args.length!=0) {
//            if (args[0].equals("ClassicSimilarity")) {
//                isearcher.setSimilarity(new ClassicSimilarity());
//                System.out.println("using ClassicSimilarity");
//            }
//            if (args[0].equals("BM25")) {
//                isearcher.setSimilarity(new BM25Similarity());
//                System.out.println("using BM25Similarity");
//            }
//            if (args[0].equals("Boolean")) {
//                isearcher.setSimilarity(new BooleanSimilarity());
//                System.out.println("using BooleanSimilarity");
//            }
//            if (args[0].equals("LMD")) {
//                isearcher.setSimilarity(new LMDirichletSimilarity());
//                System.out.println("using LMDirichletSimilarity");
//            }
//            if (args[0].equals("LMJ")) {
//                isearcher.setSimilarity(new LMJelinekMercerSimilarity((float) 0.7));
//                System.out.println("using LMJelinekMercerSimilarity");
//            }
//        }
//        isearcher.setSimilarity(new BM25Similarity());

        BufferedReader in = null;
        in = Files.newBufferedReader(Paths.get(QUERY_DIRECTORY), StandardCharsets.UTF_8);

        HashMap<String, Float> boostedScores = new HashMap<String, Float>();
        boostedScores.put("ht", 0.35f);
        boostedScores.put("au", 0.02f);
        boostedScores.put("date", 0.02f);
        boostedScores.put("f", 0.02f);
        boostedScores.put("text", 0.65f);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(
                new String[]{"ht", "au", "date", "f", "text"}, analyzer, boostedScores);

        String line = in.readLine();
        String nextLine = "";
        int queryNumber = 401;
        PrintWriter writer;
        writer = new PrintWriter(WRITE_PATH + "/outputs1.txt", "UTF-8");
        System.out.println("Creating the final result in: ./output/outputs1.txt");


        while (true) {
            if (line == null || line.length() == -1) {
                break;
            }

            if (line.length() == 0) {
                break;
            }

            if (line.substring(0, 2).equals(".I")) {
                line = in.readLine();
                if (line.equals(".W")) {
                    line = in.readLine();
                }
                nextLine = "";
                while (!line.substring(0, 2).equals(".I")) {
                    nextLine = nextLine + " " + line;
                    line = in.readLine();
                    if (line == null) break;
                }
            }
            Query query = parser.parse(QueryParser.escape(nextLine.trim()));
//            System.out.println(query);

            doPagingSearch(queryNumber, in, isearcher, query, hitsPerPage, writer);
            queryNumber++;
            System.out.println(queryNumber);
            if (queryString != null) {
                break;
            }

        }
        // close everything and quit
        writer.close();
        ireader.close();
        directory.close();
    }


    public static void doPagingSearch(int queryNumber, BufferedReader in, IndexSearcher searcher, Query query,
                                      int hitsPerPage,  PrintWriter writer) throws IOException {
        TopDocs results = searcher.search(query, 5 * hitsPerPage);
        int numTotalHits = Math.toIntExact(results.totalHits.value);
//        int numTotalHits = Math.toIntExact(results.TotalHitCountCollector.value);
        results = searcher.search(query, numTotalHits);
//        System.out.println(numTotalHits +" "+ results.totalHits);
        ScoreDoc[] hits = results.scoreDocs;
        //System.out.println(numTotalHits + " total matching documents");
        int start = 0;
//        int end = Math.min(numTotalHits, hitsPerPage);


//        end = Math.min(hits.length, start + hitsPerPage);
        for (int i = start; i < numTotalHits; i++) {
            Document doc = searcher.doc(hits[i].doc);
            String path = doc.get("docno");
//            System.out.println(path);

            if (path != null) {

//                System.out.println(queryNumber + " 0 " + path + " " +(i+1)+ " " + hits[i].score);
                writer.println(queryNumber+" Q0 " + path.replace("\n", "")  + " " + (i+1) + " " + hits[i].score +" EnglishAnalyzerBM25");
            }
        }

    }
}
