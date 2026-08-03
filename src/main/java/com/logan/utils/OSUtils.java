package com.logan.utils;

import java.io.File;

public class OSUtils {
    public static boolean isMacOS() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return false;
        }
        return true;
    }


    public static String getUserComputerDownloadPath(){
        String home = System.getProperty("user.home");
        return home + File.separator + "Downloads";
    }

}
