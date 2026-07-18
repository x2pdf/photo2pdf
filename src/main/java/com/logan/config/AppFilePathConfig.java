package com.logan.config;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppFilePathConfig {
    // APP文件存储的根目录
    public static String APP_FILE_ROOT_PATH = System.getProperty("user.home") + File.separator + "photo2pdf" + File.separator;

    // 缓存路径根目录
    public static String APP_CACHE_PATH = System.getProperty("java.io.tmpdir") + AppInfoConfig.APP_NAME + File.separator;
    // 日志的缓存路径
    public static String LOG_CACHE_PATH = APP_CACHE_PATH + "log" + File.separator;
    // 日志的文件名称
    public static String LOG_FILE_NAME = "dev_debug_" + DateTimeFormatter.ofPattern("yyyy_MM_dd").format(LocalDateTime.now()) + ".log";


    // key的缓存路径
    public static String KEY_CACHE_PATH = APP_CACHE_PATH + "key" + File.separator;
    // 使用过key或者其他的历史文件的缓存路径
    public static String HISTORY_CACHE_PATH = APP_CACHE_PATH + "history" + File.separator;
    // 语言文件的缓存路径
    public static String LANG_CACHE_PATH = APP_CACHE_PATH + "language" + File.separator;


    // password preset的缓存路径
    public static String PWD_PRESET_CACHE_PATH = APP_CACHE_PATH + "passwordpreset" + File.separator;

    // heic convert js node 工程的缓存路径
    public static String HEIC_CONVERT_JS_NODE = APP_CACHE_PATH + "heic" + File.separator + "heic-convert-js" + File.separator;


    // jxl convert 工程的缓存路径
    public static String JXL_CONVERT_WINDOWS = APP_CACHE_PATH + "jxl" + File.separator + "jxl-x64-windows-static\\bin" + File.separator;
    public static String JXL_CONVERT_MAC = APP_CACHE_PATH + "jxl" + File.separator + "jxl-arm64-mac-static" + File.separator;
}
