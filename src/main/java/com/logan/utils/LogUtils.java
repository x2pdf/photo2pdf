package com.logan.utils;

import com.logan.config.CacheData;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Logan Qin
 * @date 2021/12/27 13:58
 */


public class LogUtils {

    public static void info(String content) {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String fmtTime = dtf.format(time);
        System.out.println(fmtTime + " ==== [INFO] " + content);
        LocalFileUtils.append2Log(fmtTime + " ==== [INFO] " + content, SysConfig.LOG_FILE_NAME);
        cleanLog();
    }

    public static void error(String content) {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String fmtTime = dtf.format(time);
        System.err.println(fmtTime + " ==== [ERROR] " + content);
        LocalFileUtils.append2Log(fmtTime + " ==== [ERROR] " + content, SysConfig.LOG_FILE_NAME);
        cleanLog();
    }

    public static String appStatus() {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String fmtTime = dtf.format(time);
        String sysConfig = SysConfig.instance().toString();
        String cacheData = CacheData.instance().toString();
        String geneParamConfig = GeneParamConfig.instance().toString();

        System.out.println(fmtTime + " ==== [INFO] sysConfig: " + sysConfig);
        System.out.println(fmtTime + " ==== [INFO] cacheData: " + cacheData);
        System.out.println(fmtTime + " ==== [INFO] geneParamConfig: " + geneParamConfig);
        LocalFileUtils.append2Log(fmtTime + " ==== [INFO] sysConfig: " + sysConfig, SysConfig.LOG_FILE_NAME);
        LocalFileUtils.append2Log(fmtTime + " ==== [INFO] cacheData: " + cacheData, SysConfig.LOG_FILE_NAME);
        LocalFileUtils.append2Log(fmtTime + " ==== [INFO] geneParamConfig: " + geneParamConfig, SysConfig.LOG_FILE_NAME);

        return sysConfig + "\n" + cacheData + "\n" + geneParamConfig + "\n";
    }


    public static void cleanLog() {
        if (Math.random() < 0.999999) {
            return;
        }
        // 十万分之一的概率移除日志文件
        String logPath = LocalFileUtils.getLogPath();
        for (int i = 30; i < 388; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            String logFileName = SysConfig.getLogFileName(date);
            File file = new File(logPath + logFileName);
            // 路径为文件且不为空则进行删除
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        }
    }
}
