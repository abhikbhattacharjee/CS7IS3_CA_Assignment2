package com.lucene.indexandsearch.ft;

public enum FTTags {
    DOCNO("<DOCNO>"), PROFILE("<PROFILE>"), DATE("<DATE>"), PUB("<PUB>"),
    HEADLINE("<HEADLINE>"), LENGTH("<PAGE>"), BYLINE("<BYLINE>"), TEXT("<TEXT>");

    String tag;

    FTTags(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}