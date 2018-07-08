package com.pashkobohdan.ttsreader.utils;

public class Constants {

    public static final int DEFAULT_PITCH_RATE = 100;
    public static final int DEFAULT_SPEED_RATE = 100;

    public static final String EMPTY = "";
    public static final String DOT = ".";
    public static final String TXT_FORMAT = ".txt";
    public static final String PDF_FORMAT = ".pdf";
    public static final String FB2_FORMAT = ".fb2";
    public static final String EPUB_FORMAT = ".epub";

    public static String OPEN_BOOK_ACTION = "com.pashkobohdan.ttsreader.action.openbookaction";
    public static String OPEN_BOOK_ACTION_BOOK_ID_KEY = "com.pashkobohdan.ttsreader.action.openbookaction_bookidkey";
    public static String PREV_ACTION = "com.pashkobohdan.ttsreader.action.prev";
    public static String PLAY_ACTION = "com.pashkobohdan.ttsreader.action.play";
    public static String PAUSE_ACTION = "com.pashkobohdan.ttsreader.action.pause";
    public static String NEXT_ACTION = "com.pashkobohdan.ttsreader.action.next";
    public static String STARTFOREGROUND_ACTION = "com.pashkobohdan.ttsreader.action.startforeground";
    public static String STOPFOREGROUND_ACTION = "com.pashkobohdan.ttsreader.action.stopforeground";


    private Constants() {
        //closed constructor
    }
}
