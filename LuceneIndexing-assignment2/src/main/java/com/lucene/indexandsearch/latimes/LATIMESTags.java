package com.lucene.indexandsearch.latimes;

public enum LATIMESTags {
    DOCNO("<DOCNO>"), DOCID("<DOCID>"), DATE("<DATE>"), SECTION("<SECTION>"),
    HEADLINE("<HEADLINE>"), LENGTH("<LENGTH>"), BYLINE("<BYLINE>"), GRAPHIC("<GRAPHIC>"), TEXT("<TEXT>"),
    TYPE("<TYPE>");

    String tag;

    LATIMESTags(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}