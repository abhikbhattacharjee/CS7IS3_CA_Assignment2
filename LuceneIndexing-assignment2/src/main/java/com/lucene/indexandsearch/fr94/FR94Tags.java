package com.lucene.indexandsearch.fr94;

public enum FR94Tags {
    DOCNO("<DOCNO>"), PARENT("<PARENT>"), TEXT("<TEXT>"), HEADLINE("<DOCTITLE>"), USDEPT("<USDEPT>"), AGENCY("<AGENCY>"), USBUREAU("<USBUREAU>"),
    ADDRESS("<ADDRESS>"), FURTHER("<FURTHER>"), SUMMARY("<SUMMARY>"), ACTION("<ACTION>"), SIGNER("<SIGNER>"), SIGNJOB("<SIGNJOB>"), SUPPLEM("<SUPPLEM>"),
    BILLING("<BILLING>"), FRFILING("<FRFILING>"), DATE("<DATE>"), CFRNO("<CFRNO>"), RINDOCK("<RINDOCK>"), TABLE("<TABLE>"), FOOTNOTE("<FOOTNOTE>"),
    FOOTCITE("<FOOTCITE>"), FOOTNAME("<FOOTNAME>"), ALL("<ALL>");

    String tag;

    FR94Tags (String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
