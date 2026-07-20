package com.logan.utils;

import com.logan.config.AppFilePathConfig;

import java.io.File;
import java.io.IOException;

import static com.logan.utils.HeifConvertUtils.consumeInputStream;

public class AVIFConverterUtils {

    public static void convert2AVIFAdaptor(String srcFilePath, String descFilePath, int quality) {
        if (!OSUtils.isMacOS()) {
            convert2AVIFWindows(srcFilePath, descFilePath, quality * 100);
        } else {
            convert2AVIFMac(srcFilePath, descFilePath, quality * 100);
        }
    }

    public static void convertAVIF2OthersAdaptor(String srcFilePath, String descFilePath, int quality) {
        if (!OSUtils.isMacOS()) {
            convertAVIF2OthersWindows(srcFilePath, descFilePath, quality * 100);
        } else {
            convertAVIF2OthersMac(srcFilePath, descFilePath, quality * 100);
        }
    }


    public static void convert2AVIFWindows(String srcFilePath, String descFilePath, int quality) {
        try {
            long start = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();
            String avifencPath = AppFilePathConfig.AVIF_CONVERT_WINDOWS + "avifenc.exe";
            // avifenc [options] input.[jpg|jpeg|png|y4m] output.avif
            String[] command = new String[]{avifencPath, "-q", String.valueOf(quality), "-s", "5", srcFilePath, descFilePath};

            LogUtils.info("Executing AVIF conversion: " + avifencPath + " " + srcFilePath + " -> " + descFilePath + "， command: " + String.join(" ", command));
            Process process = runtime.exec(command, null, new File(AppFilePathConfig.AVIF_CONVERT_WINDOWS));
            // 读取标准输出
            String outStr = consumeInputStream(process.getInputStream());
            // 读取错误输出
            String errStr = consumeInputStream(process.getErrorStream());
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LogUtils.info("AVIF convert success. Output: " + outStr);
            } else {
                LogUtils.error("AVIF convert failed. Exit code: " + exitCode + ", Error: " + errStr);
            }

            long end = System.currentTimeMillis();
            LogUtils.info("AVIF convert spend(ms): " + (end - start));
        } catch (IOException | InterruptedException e) {
            LogUtils.error("AVIF convert exception: " + e.getMessage());
            e.printStackTrace();
        }

    }


    public static void convert2AVIFMac(String srcFilePath, String descFilePath, int quality) {
        try {
            long start = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();
            String avifencPath = AppFilePathConfig.AVIF_CONVERT_MAC + "avifenc";
            // avifenc [options] input.[jpg|jpeg|png|y4m] output.avif
            String[] command = new String[]{avifencPath, "-q", String.valueOf(quality), "-s", "5", srcFilePath, descFilePath};

            LogUtils.info("Executing AVIF conversion: " + avifencPath + " " + srcFilePath + " -> " + descFilePath + "， command: " + String.join(" ", command));
            Process process = runtime.exec(command, null, new File(AppFilePathConfig.AVIF_CONVERT_MAC));
            // 读取标准输出
            String outStr = consumeInputStream(process.getInputStream());
            // 读取错误输出
            String errStr = consumeInputStream(process.getErrorStream());
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LogUtils.info("AVIF convert success. Output: " + outStr);
            } else {
                LogUtils.error("AVIF convert failed. Exit code: " + exitCode + ", Error: " + errStr);
            }

            long end = System.currentTimeMillis();
            LogUtils.info("AVIF convert spend(ms): " + (end - start));
        } catch (IOException | InterruptedException e) {
            LogUtils.error("AVIF convert exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void convertAVIF2OthersMac(String srcFilePath, String descFilePath, int quality) {
        try {
            long start = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();

            String avifPath = AppFilePathConfig.AVIF_CONVERT_MAC + "avifdec";
            // avifdec [options] input.avif output.[jpg|jpeg|png|y4m]
            String[] command = new String[]{avifPath, "-q", String.valueOf(quality), "--png-compress", "8", "--progressive", srcFilePath, descFilePath};

            LogUtils.info("Executing AVIF conversion: " + avifPath + " " + srcFilePath + " -> " + descFilePath + "， command: " + String.join(" ", command));
            Process process = runtime.exec(command, null, new File(AppFilePathConfig.AVIF_CONVERT_MAC));
            // 读取标准输出
            String outStr = consumeInputStream(process.getInputStream());
            // 读取错误输出
            String errStr = consumeInputStream(process.getErrorStream());
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LogUtils.info("AVIF convert success. Output: " + outStr);
            } else {
                LogUtils.error("AVIF convert failed. Exit code: " + exitCode + ", Error: " + errStr);
            }

            long end = System.currentTimeMillis();
            LogUtils.info("AVIF convert spend(ms): " + (end - start));
        } catch (IOException | InterruptedException e) {
            LogUtils.error("AVIF convert exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void convertAVIF2OthersWindows(String srcFilePath, String descFilePath, int quality) {
        try {
            long start = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();

            String avifPath = AppFilePathConfig.AVIF_CONVERT_WINDOWS + "avifdec.exe";
            // avifdec [options] input.avif output.[jpg|jpeg|png|y4m]
            String[] command = new String[]{avifPath, "-q", String.valueOf(quality), "--png-compress", "8", "--progressive", srcFilePath, descFilePath};

            LogUtils.info("Executing AVIF conversion: " + avifPath + " " + srcFilePath + " -> " + descFilePath + "， command: " + String.join(" ", command));
            Process process = runtime.exec(command, null, new File(AppFilePathConfig.AVIF_CONVERT_WINDOWS));
            // 读取标准输出
            String outStr = consumeInputStream(process.getInputStream());
            // 读取错误输出
            String errStr = consumeInputStream(process.getErrorStream());
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LogUtils.info("AVIF convert success. Output: " + outStr);
            } else {
                LogUtils.error("AVIF convert failed. Exit code: " + exitCode + ", Error: " + errStr);
            }

            long end = System.currentTimeMillis();
            LogUtils.info("AVIF convert spend(ms): " + (end - start));
        } catch (IOException | InterruptedException e) {
            LogUtils.error("AVIF convert exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
