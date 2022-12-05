package com.lucene.indexandsearch.ft;

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

public class FTIndexer extends DocumentIndexer {

    private static BufferedReader br;
    private static List<Document> ftDocList = new ArrayList<>();
    private static final String[] IGNORE_FILES = {"readchg.txt", "readmefb.txt"};

    public FTIndexer(String indexPath) throws IOException {
        super(indexPath);
        loadFTDocs(Constants.FT_FILESPATH);
        finished();
    }

    public List<Document> loadFTDocs(String ftDirectory) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(ftDirectory));
        for (String ftFile : dir.listAll()) {
            if (!ftFile.equals(IGNORE_FILES[0]) && !ftFile.equals(IGNORE_FILES[1])) {
                br = new BufferedReader(new FileReader(ftDirectory + "/" + ftFile));
                indexDocumentsFromFile();
            }
        }
        return ftDocList;
    }

    private void indexDocumentsFromFile() throws IOException {

        String file = readAFile();
        org.jsoup.nodes.Document document = Jsoup.parse(file);
        List<Element> list = document.getElementsByTag("doc");
        for (Element doc : list) {
            FTData ftData = new FTData();
            if (doc.getElementsByTag(FTTags.DOCNO.name()) != null)
                ftData.setDocNum(removeUnnecessaryTags(doc, FTTags.DOCNO));
            if (doc.getElementsByTag(FTTags.HEADLINE.name()) != null)
                ftData.setHeadLine(removeUnnecessaryTags(doc, FTTags.HEADLINE));
            if (doc.getElementsByTag(FTTags.TEXT.name()) != null)
                ftData.setText(removeUnnecessaryTags(doc, FTTags.TEXT));
            if (doc.getElementsByTag(FTTags.BYLINE.name()) != null)
                ftData.setByLine(removeUnnecessaryTags(doc, FTTags.BYLINE));
            if (doc.getElementsByTag(FTTags.PROFILE.name()) != null)
                ftData.setProfile(removeUnnecessaryTags(doc, FTTags.PROFILE));
            if (doc.getElementsByTag(FTTags.PUB.name()) != null)
                ftData.setPub(removeUnnecessaryTags(doc, FTTags.PUB));
            ftData.setAll(ftData.getText() + " " + ftData.getHeadLine() + " " + ftData.getByLine() + " " + ftData.getPub() + " " + ftData.getProfile());
            addDocToIndex(createFTDocument(ftData));
        }
    }

    private static String removeUnnecessaryTags(Element doc, FTTags tag) {

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

    private static void deleteNestedTags(Elements element, FTTags currTag) {

        for (FTTags tag : FTTags.values()) {
            if (element.toString().contains("<" + tag.name().toLowerCase() + ">") &&
                    element.toString().contains("</" + tag.name().toLowerCase() + ">") && !tag.equals(currTag)) {
                element.select(tag.toString()).remove();
            }
        }
    }

    private static Document createFTDocument(FTData FTData) {
        Document document = new Document();
        document.add(new StringField(Constants.DOCNO_TEXT, FTData.getDocNum(), Field.Store.YES));
        document.add(new TextField(Constants.HEADLINE_TEXT, FTData.getHeadLine(), Field.Store.YES));
        document.add(new TextField(Constants.FIELD_ALL, FTData.getAll(), Field.Store.YES));
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

}