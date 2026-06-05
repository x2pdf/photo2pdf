package com.logan.ctrl.zip;

import java.io.File;
import java.util.ArrayList;

public class ZIPConfig {
    public static String compressionLevel = "NORMAL";
    public static final int pwdMinLength = 16;
    public static String zipSavePath = "";
    public static String zipName = "";
    public static ArrayList<File> selectZIPFiles = new ArrayList<>();;
    public static ArrayList<String> selectZIPFilesPath = new ArrayList<>();;


    public static void setZipSavePath(String zipSavePath) {
        ZIPConfig.zipSavePath = zipSavePath;
    }

    public static void setCompressionLevel(String compressionLevel) {
        ZIPConfig.compressionLevel = compressionLevel;
    }

    public static void setZipName(String zipName) {
        ZIPConfig.zipName = zipName;
    }

    public static void setSelectZIPFiles(ArrayList<File> selectZIPFiles) {
        ZIPConfig.selectZIPFiles = selectZIPFiles;
    }

    public static void setSelectZIPFilesPath(ArrayList<String> selectZIPFilesPath) {
        ZIPConfig.selectZIPFilesPath = selectZIPFilesPath;
    }
}
