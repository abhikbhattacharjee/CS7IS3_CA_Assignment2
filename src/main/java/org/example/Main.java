package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        fbis_parser.fbis_parser_main(null);
        indexer.indexer(null);
    }
}