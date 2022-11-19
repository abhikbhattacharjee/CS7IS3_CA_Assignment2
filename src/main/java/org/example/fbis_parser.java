package org.example;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fbis_parser {
    public static void fbis_parser_main(String[] args) throws IOException {

        List<String> fbis_docs = new ArrayList<String>();
        Map<String, String> fbis_content = new HashMap<String, String>();
        File[] file_list = new File("../Documents/fbis/").listFiles();

        String OUTPUT_FILE = "./output/parsed_docs/fbis.json";
        //System.out.println(file_list.length);
        for (int i = 0; i < file_list.length; i++) {
            //System.out.println(file_list[i]);

            String text = Utils.readFullFile(String.valueOf(file_list[i]));
            Document doc = Jsoup.parse(text);
            Elements content = doc.body().select("doc");

            //System.out.println(file_list[i]);
            //System.out.println(content.text());

            for (Element element : content) {
                String docno = element.getElementsByTag("DOCNO").text().trim();     //toLowerCase() ??
                String HT = element.getElementsByTag("HT").first().text().trim();
                String AU = element.getElementsByTag("AU").text().trim();
                String Date = element.getElementsByTag("DATE1").text().trim();
                String f = element.getElementsByTag("f").select("*").not("phrase").eachText() + "\n	";
                String text_tag = element.getElementsByTag("Text").text().trim();

                /*System.out.println(docno);
                System.out.println(HT);
                System.out.println(AU);
                System.out.println(Date);*/

                fbis_content.put("docno", docno);
                fbis_content.put("ht", HT);
                fbis_content.put("au", AU);
                fbis_content.put("date", Date);
                fbis_content.put("f", f);
                fbis_content.put("text", text);
            }
            fbis_docs.add(new JSONObject(fbis_content).toString() + ',');
        }
        //System.out.println(fbis_docs);

        File resultFile = new File(OUTPUT_FILE);
        resultFile.getParentFile().mkdirs();
        try {
            PrintWriter writer = new PrintWriter(resultFile, StandardCharsets.UTF_8.name());
            writer.println(fbis_docs);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Documents in FBIS folder Parsed! Json File Location: " + OUTPUT_FILE);
        /*String filePath = "/Users/abhik_bhattacharjee/Desktop/MSc CS IS SEM 1/" +
                "Lucene Assignment 2/Assignment Two/fbis/fb496206";
        String text = Utils.readFullFile(filePath);
        //System.out.println(text);

        Document doc = Jsoup.parse(text);
        Elements content = doc.select("DOCNO");
        System.out.println(content.text());*/
    }
}
