package com.pashkobohdan.ttsreader.utils;

import static com.pashkobohdan.ttsreader.utils.Constants.EMPTY;

public class NullUtils {

    private NullUtils() {
        //closed constructor
    }

    public static boolean isEmptyString(String string) {
        return string == null || string.equals(EMPTY);
    }

    public static boolean isEmptyStringWithTrim(String string) {
        return string == null || string.trim().equals(EMPTY);
    }

}
