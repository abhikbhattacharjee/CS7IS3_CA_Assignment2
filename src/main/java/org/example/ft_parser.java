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

public class ft_parser {
    public static void ft_parser_main(String[] args) throws IOException {

        List<String> ft_docs = new ArrayList<String>();
        Map<String, String> fbis_content = new HashMap<String, String>();
        File[] file_list1 = new File("../Documents/ft/").listFiles(File::isDirectory);
        //System.out.println(file_list1);

        String OUTPUT_FILE = "./output/parsed_docs/ft.json";
        //System.out.println(file_list.length);

        for(int i=0;i<13;i++){
            String p = file_list1[i].toString();
            //System.out.println(p);
            String path = p+"/";
            //System.out.println(path);
            File[] file_list = new File(path).listFiles();
            //System.out.println(file_list);
            for(int j=0;j<file_list.length;j++){
                //System.out.println(file_list[i]);
                String text = Utils.readFullFile(String.valueOf(file_list[i]));
                Document doc = Jsoup.parse(text);
                Elements content = doc.body().select("doc");

                //System.out.println(file_list[i]);
                //System.out.println(content.text());

                for (Element element : content) {
                    String docno = element.getElementsByTag("DOCNO").text().trim();     //toLowerCase() ??
                    String Profile = element.getElementsByTag("PROFILE").first().text().trim();
                    String Headline = element.getElementsByTag("HEADLINE").text().trim();
                    String Date = element.getElementsByTag("DATE").text().trim();
                    String text_tag = element.getElementsByTag("TEXT").text().trim();
                    String publish = element.getElementsByTag("PUB").text().trim();

                /*System.out.println(docno);
                System.out.println(HT);
                System.out.println(AU);
                System.out.println(Date);*/

                    fbis_content.put("docno", docno);
                    fbis_content.put("profile", Profile);
                    fbis_content.put("headline", Headline);
                    fbis_content.put("date", Date);
                    fbis_content.put("text", text_tag);
                    fbis_content.put("publish", publish);
                }
                ft_docs.add(new JSONObject(fbis_content).toString() + "\n");
            }
        }



        File resultFile = new File(OUTPUT_FILE);
        resultFile.getParentFile().mkdirs();
        try {
            PrintWriter writer = new PrintWriter(resultFile, StandardCharsets.UTF_8.name());
            writer.println(ft_docs);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Documents in FT folder Parsed! Json File Location: " + OUTPUT_FILE);
        /*String filePath = "/Users/abhik_bhattacharjee/Desktop/MSc CS IS SEM 1/" +
                "Lucene Assignment 2/Assignment Two/fbis/fb496206";
        String text = Utils.readFullFile(filePath);
        //System.out.println(text);

        Document doc = Jsoup.parse(text);
        Elements content = doc.select("DOCNO");
        System.out.println(content.text());*/
    }
}
