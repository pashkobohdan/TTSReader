package com.pashkobohdan.ttsreader.utils;

public class Patterns {

    private static final String SENTENCE_SPLITTER = ".?!";
    public static final String TEXT_CORRECT_SYMBOLS = "[^" + SENTENCE_SPLITTER + "]+[" + SENTENCE_SPLITTER + "]";

    private Patterns() {
        //Utility class
    }
}
