package com.lucene.indexandsearch.latimes;

import com.lucene.indexandsearch.indexer.DocumentIndexer;
import com.lucene.indexandsearch.utils.Constants;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LATIMESIndexer extends DocumentIndexer {

    private static BufferedReader br;
    private static List<Document> latimesDocList = new ArrayList<>();
    private static final String[] IGNORE_FILES = {"readchg.txt", "readmefb.txt"};

    public LATIMESIndexer(String indexPath) throws IOException {
        super(indexPath);
        loadLATIMESDocs(Constants.LATIMES_FILESPATH);
        finished();
    }

    public List<Document> loadLATIMESDocs(String latimesDirectory) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(latimesDirectory));
        for (String latimesFile : dir.listAll()) {
            if (!latimesFile.equals(IGNORE_FILES[0]) && !latimesFile.equals(IGNORE_FILES[1])) {
                br = new BufferedReader(new FileReader(latimesDirectory + "/" + latimesFile));
                indexDocumentsFromFile();
            }
        }
        return latimesDocList;
    }

    private void indexDocumentsFromFile() throws IOException {

        String file = readAFile();
        org.jsoup.nodes.Document document = Jsoup.parse(file);
        List<Element> list = document.getElementsByTag("doc");
        for (Element doc : list) {
            LATIMESData latimesData = new LATIMESData();
            if (doc.getElementsByTag(LATIMESTags.DOCNO.name()) != null)
                latimesData.setDocNum(removeUnnecessaryTags(doc, LATIMESTags.DOCNO));
            if (doc.getElementsByTag(LATIMESTags.HEADLINE.name()) != null)
                latimesData.setHeadLine(removeUnnecessaryTags(doc, LATIMESTags.HEADLINE));
            if (doc.getElementsByTag(LATIMESTags.SECTION.name()) != null)
                latimesData.setSection(removeUnnecessaryTags(doc, LATIMESTags.SECTION));
            if (doc.getElementsByTag(LATIMESTags.TEXT.name()) != null)
                latimesData.setText(removeUnnecessaryTags(doc, LATIMESTags.TEXT));
            if (doc.getElementsByTag(LATIMESTags.BYLINE.name()) != null)
                latimesData.setByLine(removeUnnecessaryTags(doc, LATIMESTags.BYLINE));
            if (doc.getElementsByTag(LATIMESTags.GRAPHIC.name()) != null)
                latimesData.setGraphic(removeUnnecessaryTags(doc, LATIMESTags.GRAPHIC));
            latimesData.setAll(latimesData.getText() + " " + latimesData.getGraphic() + " " + latimesData.getByLine() + " " + latimesData.getHeadLine());
            addDocToIndex(createLATIMESDocument(latimesData));
        }
    }

    private static String removeUnnecessaryTags(Element doc, LATIMESTags tag) {

        Elements element = doc.getElementsByTag(tag.name());
        Elements tempElement = element.clone();
        //remove any nested
        deleteNestedTags(tempElement, tag);
        String data = tempElement.toString();

        //remove any instance of "\n"
        if (data.contains("\n"))
            data = data.replaceAll("\n", "").trim();
        //remove any instance of "<p>"
        if (data.contains("<p>"))
            data = data.replaceAll("<p>", "").trim();
        if (data.contains("</p>"))
            data = data.replaceAll("</p>", "").trim();
        //remove start and end tags
        if (data.contains(("<" + tag.name() + ">").toLowerCase()))
            data = data.replaceAll("<" + tag.name().toLowerCase() + ">", "").trim();
        if (data.contains(("</" + tag.name() + ">").toLowerCase()))
            data = data.replaceAll("</" + tag.name().toLowerCase() + ">", "").trim();
        data = data.trim().replaceAll(" +", " ");
        return data;
    }

    private static void deleteNestedTags(Elements element, LATIMESTags currTag) {

        for (LATIMESTags tag : LATIMESTags.values()) {
            if (element.toString().contains("<" + tag.name().toLowerCase() + ">") &&
                    element.toString().contains("</" + tag.name().toLowerCase() + ">") && !tag.equals(currTag)) {
                element.select(tag.toString()).remove();
            }
        }
    }

    private static Document createLATIMESDocument(LATIMESData latimesData) {
        Document document = new Document();
        document.add(new StringField(Constants.DOCNO_TEXT, latimesData.getDocNum(), Field.Store.YES));
        document.add(new TextField(Constants.HEADLINE_TEXT, latimesData.getHeadLine(), Field.Store.YES));
        document.add(new TextField(Constants.FIELD_ALL, latimesData.getAll(), Field.Store.YES));
        return document;
    }

    private static String readAFile() throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    public static void main(String[] args) throws IOException {
        LATIMESIndexer latimesReader = new LATIMESIndexer(Constants.INDEXPATH);
    }

}