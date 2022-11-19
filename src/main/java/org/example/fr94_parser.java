package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fr94_parser {
    public static void fr94_parser_main(String[] args) throws IOException {

        List<String> fr94_docs = new ArrayList<String>();
        Map<String, String> fr94_content = new HashMap<String, String>();
        File[] file_list2 = new File("../Documents/fr94/").listFiles(File::isDirectory);

        String OUTPUT_FILE = "./output/parsed_docs/fr94.json";
        for (int i = 0; i < 11; i++) {
            String p = file_list2[i].toString();
            String path = p + "/";
            File[] file_list = new File(path).listFiles();
            for (int j = 0; j < file_list.length; j++) {
                String text = Utils.readFullFile(String.valueOf(file_list[i]));
                Document doc = Jsoup.parse(text);
                Elements content = doc.body().select("doc");

                //System.out.println(file_list[i]);
                //System.out.println(content.text());

                for (Element element: content) {
                    String docno = element.getElementsByTag("DOCNO").text().trim(); //toLowerCase() ??
                    String parent = element.getElementsByTag("PARENT").first().text().trim();
                    String Headline = element.getElementsByTag("!-- PJG ITAG l=01 g=1 f=1 --").text().trim();
                    String Date = element.getElementsByTag("!-- PJG ITAG l=02 g=1 f=1 --").text().trim();
                    String text_tag = element.getElementsByTag("TEXT").text().trim();
                    String publish = element.getElementsByTag("!-- PJG ITAG l=90 g=1 f=4 --").text().trim();


                    fr94_content.put("docno", docno);
                    fr94_content.put("parent", parent);
                    fr94_content.put("headline", Headline);
                    fr94_content.put("date", Date);
                    fr94_content.put("text", text_tag);
                    fr94_content.put("publish", publish);
                }
                fr94_docs.add(new JSONObject(fr94_content).toString() + "\n");
            }
        }

        File resultFile = new File(OUTPUT_FILE);
        resultFile.getParentFile().mkdirs();
        try {
            PrintWriter writer = new PrintWriter(resultFile, StandardCharsets.UTF_8.name());
            writer.println(fr94_docs);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Documents in fr94 folder Parsed! Json File Location: " + OUTPUT_FILE);
    }
}


