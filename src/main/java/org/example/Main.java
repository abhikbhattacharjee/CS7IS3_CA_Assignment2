package org.example;

import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        fbis_parser.fbis_parser_main(null);
        ft_parser.ft_parser_main(null);
        latimes_parser.latimes_parser_main(null);
        fr94_parser.fr94_parser_main(null);
        combined_indexing.combined_indexer(null);
        queries.queries(null);
    }
}
