package com.localagent.utils;


public class Utils {
    public static String trimming(String text){
        return text == null || "".equals(text)? "" : text.replaceAll("\\s+", "");
    }

    public static boolean isEmptyString(String text){
        return trimming(text).length() == 0;
    }

}
