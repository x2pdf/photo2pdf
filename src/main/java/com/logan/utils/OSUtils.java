package com.logan.utils;

public class OSUtils {
    public static boolean isMacOS() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return false;
        }
        return true;
    }
}
