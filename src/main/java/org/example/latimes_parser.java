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

public class latimes_parser {
    public static void latimes_parser_main(String[] args) throws IOException {

        List<String> latimes_docs = new ArrayList<String>();
        Map<String, String> latimes_content = new HashMap<String, String>();
        File[] file_list = new File("../Documents/latimes/").listFiles();

        String OUTPUT_FILE = "./output/parsed_docs/latimes.json";
        for (int i = 0; i < file_list.length; i++) {

            String text = Utils.readFullFile(String.valueOf(file_list[i]));
            Document doc = Jsoup.parse(text);
            Elements content = doc.body().select("doc");

            for (Element element : content) {
                String docno = element.getElementsByTag("DOCNO").text().trim();
                String Date = element.getElementsByTag("DATE").text().trim();
                String Section = element.getElementsByTag("SECTION").text().trim();
                String Headline = element.getElementsByTag("HEADLINE").text().trim();
                String Byline = element.getElementsByTag("BYLINE").text().trim();
                String text_tag = element.getElementsByTag("Text").text().trim();

				/*System.out.println(docno);
                System.out.println(Date);
                System.out.println(Section);
                System.out.println(Headline);
				System.out.println(Byline);*/

                latimes_content.put("docno", docno);
                latimes_content.put("Date", Date);
                latimes_content.put("Section", Section);
                latimes_content.put("Headline", Headline);
                latimes_content.put("ByLine", Byline);
                latimes_content.put("text_tag", text_tag);
            }
            latimes_docs.add(new JSONObject(latimes_content).toString() + ',');
        }
        //System.out.println(latimes_docs);

        File resultFile = new File(OUTPUT_FILE);
        resultFile.getParentFile().mkdirs();
        try {
            PrintWriter writer = new PrintWriter(resultFile, StandardCharsets.UTF_8.name());
            writer.println(latimes_docs);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Documents in latimes folder Parsed! Json File Location: " + OUTPUT_FILE);
    }
}
