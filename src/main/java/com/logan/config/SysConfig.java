package com.logan.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logan.ctrl.HelpCtrl;
import com.logan.utils.LocalFileUtils;
import com.logan.utils.LogUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Logan Qin
 * @date 2021/12/15 9:12
 */


public class SysConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private static SysConfig sysConfig;

    // 程序名称
    public static String APP_NAME = "photo2pdf";
    // 应用版本号
    public static String APP_VERSION = "22.01";
    // 应用语言,默认cn
    public static String LANG = "cn";
    // 是否处于开发模式
    public static boolean IS_DEV_MODE = false;
    // 现在日期
    public static LocalDate TODAY = LocalDate.now();
    // 缓存路径根目录
    public static String APP_CACHE_PATH = System.getProperty("java.io.tmpdir") + SysConfig.APP_NAME + File.separator;
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
    // heic convert js 文件的缓存路径
    public static String HEIC_CONVERT_JS_ZIP = APP_CACHE_PATH + "heic" + File.separator;
    // heic convert js node 工程的缓存路径
    public static String HEIC_CONVERT_JS_NODE = APP_CACHE_PATH + "heic" + File.separator + "heic-convert-js" + File.separator;
    // 语言map，映射语言使用
    public static HashMap<String, String> LANG_MAP = new HashMap<>();

    // 高度
    public static int STAGE_HEIGHT = 496;
    // 宽度
    public static int STAGE_WIDTH = 800;
    // 边缘宽度默认值
    public static double STAGE_MARGIN_DEFAULT = 2.0;

    // 线程池--用来压缩图片使用
    public static ThreadPoolExecutor asyncPool;

    // 无需 key 即可使用的功能
    public static HashMap<String, String> func = new HashMap<>();

    // 系统当前所处的状态
    // 默认状态
    public static String DEFAULT = "Default";
    // 正在选择文件
    public static String PICK_FILE = "Pick File";
    // 压缩文件
    public static String COMPRESS_FILE = "Compress File";
    // 渲染页面
    public static String RENDER_PREVIEW = "Render Preview";
    // 生成pdf中
    public static String GENERATE_FILE = "PDF Generating";
    // 提取pdf文件的图片中
    public static String EXTRACTING_FILE = "Extracting file";
    // 清除图片
    public static String CLEAR = "Clear";

    // 生成pdf的图片的数量大于这个值时,就分批次生成pdf再合并pdf
    public static int genePdfByMergeMinAmount = 100;
    // 分批生成pdf的过程中，多少页的pdf作为一个小的pdf
    public static int genePdfByMergePageUnit = 20;
    // 图片文件小于 200 kB 的不压缩了
    public static int skipCompressPhotoSize = 200000;
    // nodejs程序根路径
    public static String NODEJS_PATH = "";

    private SysConfig() {
        LogUtils.info("APP_CACHE_PATH: " + APP_CACHE_PATH);
        asyncExecutor();
        initLang();

        // 功能使用的配置
        func.put("PDFSavePath", "");
//        func.put("Preview", "");
        func.put("PDFCover", "");
        func.put("PDFSummary", "");
        func.put("CompressPDFPhoto", "");
        func.put("PhotoMark", "");
//        func.put("PictureFillPage", "");
        func.put("PDFLayout", "");
        func.put("PhotoSortBy", "");
        func.put("DecryptPDF", "");
//        func.put("MergePDF", "");
//        func.put("Step2Generate", "");
//        func.put("EncryptPDF", "");
//        func.put("ExtractPhoto", "");
    }

    public static SysConfig instance() {
        if (sysConfig == null) {
            sysConfig = new SysConfig();
        }
        return sysConfig;
    }

    public ThreadPoolExecutor asyncExecutor() {
        if (asyncPool == null) {
            int corePoolSize = 4;
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            if (availableProcessors > corePoolSize) {
                corePoolSize = availableProcessors;
            }
            asyncPool = new ThreadPoolExecutor(
                    corePoolSize, corePoolSize * 2, 5, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(corePoolSize * 100), new ThreadPoolExecutor.CallerRunsPolicy());
        }

        LogUtils.info("asyncPool init. corePoolSize: " + asyncPool.getCorePoolSize() + " queue size: " + asyncPool.getQueue().size());
        return asyncPool;
    }

    public static String getLogFileName(LocalDate localDate) {
        return "dev_debug_" + DateTimeFormatter.ofPattern("yyyy_MM_dd").format(localDate) + ".log";
    }


    public void initLang() {
        try {
            // 1. 是否存在缓存文件
            ArrayList<File> filesInFold = LocalFileUtils.getFilesInFold(LANG_CACHE_PATH);
            if (filesInFold == null) {
                HelpCtrl helpCtrl = new HelpCtrl();
                helpCtrl.changeLangFile(SysConfig.LANG);
            }

            // 2. 读取文件到内存
            ArrayList<File> filesInFold2 = LocalFileUtils.getFilesInFold(LANG_CACHE_PATH);
            // filesInFold2 不应当没有文件， 步骤 1 已经初始化了
            for (File file : filesInFold2) {
                String name = file.getName();
                LogUtils.info("current lang: " + name);
                updateLang(name.split("\\.")[0]);
                Properties pps = new Properties();
                InputStreamReader in = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                pps.load(in);
                Enumeration<?> en = pps.propertyNames();
                while (en.hasMoreElements()) {
                    String strKey = (String) en.nextElement();
                    String strValue = pps.getProperty(strKey);
                    LANG_MAP.put(strKey, strValue);
                }

                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtils.info("Lang init");
    }


    private void updateLang(String lang) {
        SysConfig.LANG = lang;
    }

    public static String getLang(String key) {
        String value = SysConfig.LANG_MAP.get(key);
        if (value == null) {
            return key;
        }
        return value;
    }

    public static String getLang(String key, String replaceKey, String newValue) {
        String value = SysConfig.LANG_MAP.get(key);
        if (value == null) {
            return key;
        }
        if (newValue == null) {
            newValue = "Nil";
        }
        String res = value.replace("{" + replaceKey + "}", newValue);
        return res;
    }

    @Override
    public String toString() {
        String res = null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("APP_NAME", APP_NAME);
        map.put("APP_VERSION", APP_VERSION);
        map.put("LANG", LANG);
        map.put("IS_DEV_MODE", IS_DEV_MODE);
        map.put("TODAY", TODAY.toString());
        map.put("APP_CACHE_PATH", APP_CACHE_PATH);
        map.put("LOG_CACHE_PATH", LOG_CACHE_PATH);
        map.put("LOG_FILE_NAME", LOG_FILE_NAME);
        map.put("KEY_CACHE_PATH", KEY_CACHE_PATH);
        map.put("HISTORY_CACHE_PATH", HISTORY_CACHE_PATH);
        map.put("LANG_CACHE_PATH", LANG_CACHE_PATH);
        map.put("STAGE_HEIGHT", STAGE_HEIGHT);
        map.put("STAGE_WIDTH", STAGE_WIDTH);
        map.put("func", func);
        map.put("genePdfByMergeMinAmount", genePdfByMergeMinAmount);
        map.put("genePdfByMergePageUnit", genePdfByMergePageUnit);
        map.put("skipCompressPhotoSize", skipCompressPhotoSize);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            res = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return res;
    }

}
