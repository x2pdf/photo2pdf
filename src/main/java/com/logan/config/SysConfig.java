package com.logan.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logan.ctrl.helppage.HelpCtrl;
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

    // 是否处于开发模式
    public static boolean IS_DEV_MODE = false;
    // 现在日期
    public static LocalDate TODAY = LocalDate.now();

    // 语言map，映射语言使用
    public static HashMap<String, String> LANG_MAP = new HashMap<>();

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

    // ========== 常量定义 ==========
    /** 跳过压缩的图片大小阈值(200KB,小图无需压缩) */
    public static final int SKIP_COMPRESS_SIZE_THRESHOLD = 200_000;
    /** 图片压缩超时时间(分钟) */
    public static final int COMPRESS_TIMEOUT_MINUTES = 30;
    /** 线程池队列空闲检测等待时间(分钟) */
    public static final int QUEUE_IDLE_WAIT_MINUTES = 1;
    // 图片文件小于 200 kB 的不压缩了
    public static int skipCompressPhotoSize = SKIP_COMPRESS_SIZE_THRESHOLD;
    // nodejs程序根路径
    public static String NODEJS_PATH = "";

    private SysConfig() {
        LogUtils.info("APP_CACHE_PATH: " + AppFilePathConfig.APP_CACHE_PATH);
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
        if (System.getProperty("os.name").toLowerCase().contains("windows")){
            AppInfoConfig.APP_VERSION = AppInfoConfig.APP_VERSION + " win";
        }else {
            AppInfoConfig.APP_VERSION = AppInfoConfig.APP_VERSION + " mac";
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
            ArrayList<File> filesInFold = LocalFileUtils.getFilesInFold(AppFilePathConfig.LANG_CACHE_PATH);
            if (filesInFold == null) {
                HelpCtrl helpCtrl = new HelpCtrl();
                helpCtrl.changeLangFile(AppInfoConfig.LANG);
            }

            // 2. 读取文件到内存
            ArrayList<File> filesInFold2 = LocalFileUtils.getFilesInFold(AppFilePathConfig.LANG_CACHE_PATH);
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
        LogUtils.info("Lang loading finished");
    }


    private void updateLang(String lang) {
        AppInfoConfig.LANG = lang;
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
        map.put("APP_NAME", AppInfoConfig.APP_NAME);
        map.put("APP_VERSION", AppInfoConfig.APP_VERSION);
        map.put("LANG", AppInfoConfig.LANG);
        map.put("IS_DEV_MODE", IS_DEV_MODE);
        map.put("TODAY", TODAY.toString());
        map.put("APP_CACHE_PATH", AppFilePathConfig.APP_CACHE_PATH);
        map.put("LOG_CACHE_PATH", AppFilePathConfig.LOG_CACHE_PATH);
        map.put("LOG_FILE_NAME", AppFilePathConfig.LOG_FILE_NAME);
        map.put("KEY_CACHE_PATH", AppFilePathConfig.KEY_CACHE_PATH);
        map.put("HISTORY_CACHE_PATH", AppFilePathConfig.HISTORY_CACHE_PATH);
        map.put("LANG_CACHE_PATH", AppFilePathConfig.LANG_CACHE_PATH);
        map.put("STAGE_HEIGHT", AppUIConfig.STAGE_HEIGHT);
        map.put("STAGE_WIDTH", AppUIConfig.STAGE_WIDTH);
        map.put("func", func);
        map.put("genePdfByMergeMinAmount", PDFConfig.genePdfByMergeMinAmount);
        map.put("genePdfByMergePageUnit", PDFConfig.genePdfByMergePageUnit);
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
