package com.logan.utils;

import com.logan.config.SysConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;


/**
 * JXL格式图片和其他格式图片（JPEG/PNG）的转换器
 * 支持的系统平台：windows 64位，macOS ARM64位
 * 不支持的系统平台：windows 32位，windows ARM，macOS intel chip
 */
public class JXLConverterUtils {

    public static void convert2JXLAdaptor(String srcFilePath, String descFilePath, float quality) {
        if (!isMacOS()) {
            convert2JXLWindowsX64(srcFilePath, descFilePath, quality);
        } else {
            convert2JXLMacARM64(srcFilePath, descFilePath, quality);
        }
    }

    public static void convertJXL2OthersAdaptor(String srcFilePath, String descFilePath, float quality) {
        if (!isMacOS()) {
            convertJXL2OthersWindowsX64(srcFilePath, descFilePath, quality);
        } else {
            convertJXL2OthersMacARM64(srcFilePath, descFilePath, quality);
        }
    }

    public static void convert2JXLWindowsX64(String srcFilePath, String descFilePath, float quality) {
        try {
            long start = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();

            // 构建命令: SysConfig.JXL_CONVERT + "cjxl.exe" srcFilePath descFilePath
            String cjxlPath = SysConfig.JXL_CONVERT_WINDOWS + "cjxl.exe";
            String[] command = new String[]{cjxlPath, srcFilePath, descFilePath, "-d", "0.0", "-e", "9", "-p", "-j", "1"};

            LogUtils.info("Executing JXL conversion: " + cjxlPath + " " + srcFilePath + " -> " + descFilePath);

            Process process = runtime.exec(command, null, new File(SysConfig.JXL_CONVERT_WINDOWS));
            // 读取标准输出
            String outStr = consumeInputStream(process.getInputStream());
            // 读取错误输出
            String errStr = consumeInputStream(process.getErrorStream());
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LogUtils.info("JXL convert success. Output: " + outStr);
            } else {
                LogUtils.error("JXL convert failed. Exit code: " + exitCode + ", Error: " + errStr);
            }

            long end = System.currentTimeMillis();
            LogUtils.info("JXL convert spend(ms): " + (end - start));
        } catch (IOException | InterruptedException e) {
            LogUtils.error("JXL convert exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void convertJXL2OthersWindowsX64(String srcFilePath, String descFilePath, float quality) {
        try {
            long start = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();

            // 构建命令: SysConfig.JXL_CONVERT + "cjxl.exe" srcFilePath descFilePath
            String cjxlPath = SysConfig.JXL_CONVERT_WINDOWS + "djxl.exe";
            String[] command = new String[]{cjxlPath, srcFilePath, descFilePath};

            LogUtils.info("Executing JXL conversion: " + cjxlPath + " " + srcFilePath + " -> " + descFilePath);

            Process process = runtime.exec(command, null, new File(SysConfig.JXL_CONVERT_WINDOWS));
            // 读取标准输出
            String outStr = consumeInputStream(process.getInputStream());
            // 读取错误输出
            String errStr = consumeInputStream(process.getErrorStream());
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LogUtils.info("JXL convert success. Output: " + outStr);
            } else {
                LogUtils.error("JXL convert failed. Exit code: " + exitCode + ", Error: " + errStr);
            }

            long end = System.currentTimeMillis();
            LogUtils.info("JXL convert spend(ms): " + (end - start));
        } catch (IOException | InterruptedException e) {
            LogUtils.error("JXL convert exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void convert2JXLMacARM64(String srcFilePath, String descFilePath, float quality) {
        try {
            long start = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();

            // 构建命令: SysConfig.JXL_CONVERT + "cjxl.exe" srcFilePath descFilePath
            String cjxlPath = SysConfig.JXL_CONVERT_MAC + "cjxl";
            String[] command = new String[]{cjxlPath, srcFilePath, descFilePath, "-d", "0.0", "-e", "9", "-p", "-j", "1"};

            LogUtils.info("Executing JXL conversion: " + cjxlPath + " " + srcFilePath + " -> " + descFilePath);

            Process process = runtime.exec(command, null, new File(SysConfig.JXL_CONVERT_MAC));
            // 读取标准输出
            String outStr = consumeInputStream(process.getInputStream());
            // 读取错误输出
            String errStr = consumeInputStream(process.getErrorStream());
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LogUtils.info("JXL convert success. Output: " + outStr);
            } else {
                LogUtils.error("JXL convert failed. Exit code: " + exitCode + ", Error: " + errStr);
            }

            long end = System.currentTimeMillis();
            LogUtils.info("JXL convert spend(ms): " + (end - start));
        } catch (IOException | InterruptedException e) {
            LogUtils.error("JXL convert exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void convertJXL2OthersMacARM64(String srcFilePath, String descFilePath, float quality) {
        try {
            long start = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();

            // 构建命令: SysConfig.JXL_CONVERT + "cjxl.exe" srcFilePath descFilePath
            String cjxlPath = SysConfig.JXL_CONVERT_MAC + "djxl";
            String[] command = new String[]{cjxlPath, srcFilePath, descFilePath};

            LogUtils.info("Executing JXL conversion: " + cjxlPath + " " + srcFilePath + " -> " + descFilePath);

            Process process = runtime.exec(command, null, new File(SysConfig.JXL_CONVERT_MAC));
            // 读取标准输出
            String outStr = consumeInputStream(process.getInputStream());
            // 读取错误输出
            String errStr = consumeInputStream(process.getErrorStream());
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LogUtils.info("JXL convert success. Output: " + outStr);
            } else {
                LogUtils.error("JXL convert failed. Exit code: " + exitCode + ", Error: " + errStr);
            }

            long end = System.currentTimeMillis();
            LogUtils.info("JXL convert spend(ms): " + (end - start));
        } catch (IOException | InterruptedException e) {
            LogUtils.error("JXL convert exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void compressJXLAdaptor(String srcFilePath, String descFilePath, float quality) {
        if (!isMacOS()) {
            compressJXLWindowsX64(srcFilePath, descFilePath, quality);
        } else {
            compressJXLMacARM64(srcFilePath, descFilePath, quality);
        }
    }

    public static void compressJXLWindowsX64(String srcFilePath, String descFilePath, float quality) {
        try {
            long start = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();

            // 构建命令: SysConfig.JXL_CONVERT + "cjxl.exe" srcFilePath descFilePath
            String cjxlPath = SysConfig.JXL_CONVERT_WINDOWS + "cjxl.exe";
            float jxlCompressRatio = getJXLCompressRatio(quality);
            String[] command = new String[]{cjxlPath, srcFilePath, descFilePath, "-d", String.valueOf(jxlCompressRatio), "-e", "9", "-p"};

            LogUtils.info("Executing JXL compression: " + cjxlPath + " " + srcFilePath + " -> " + descFilePath + ", jxlCompressRatio: "+  jxlCompressRatio);

            Process process = runtime.exec(command, null, new File(SysConfig.JXL_CONVERT_WINDOWS));
            // 读取标准输出
            String outStr = consumeInputStream(process.getInputStream());
            // 读取错误输出
            String errStr = consumeInputStream(process.getErrorStream());
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LogUtils.info("JXL compression success. Output: " + outStr);
            } else {
                LogUtils.error("JXL compression failed. Exit code: " + exitCode + ", Error: " + errStr);
            }

            long end = System.currentTimeMillis();
            LogUtils.info("JXL compression spend(ms): " + (end - start));
        } catch (IOException | InterruptedException e) {
            LogUtils.error("JXL compression exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void compressJXLMacARM64(String srcFilePath, String descFilePath, float quality) {
        try {
            long start = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();

            // 构建命令: SysConfig.JXL_CONVERT + "cjxl.exe" srcFilePath descFilePath
            String cjxlPath = SysConfig.JXL_CONVERT_MAC + "cjxl";
            float jxlCompressRatio = getJXLCompressRatio(quality);
            String[] command = new String[]{cjxlPath, srcFilePath, descFilePath, "-d", String.valueOf(jxlCompressRatio), "-e", "9", "-p"};

            LogUtils.info("Executing JXL compression: " + cjxlPath + " " + srcFilePath + " -> " + descFilePath + ", jxlCompressRatio: "+  jxlCompressRatio);

            Process process = runtime.exec(command, null, new File(SysConfig.JXL_CONVERT_MAC));
            // 读取标准输出
            String outStr = consumeInputStream(process.getInputStream());
            // 读取错误输出
            String errStr = consumeInputStream(process.getErrorStream());
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LogUtils.info("JXL compression success. Output: " + outStr);
            } else {
                LogUtils.error("JXL compression failed. Exit code: " + exitCode + ", Error: " + errStr);
            }

            long end = System.currentTimeMillis();
            LogUtils.info("JXL compression spend(ms): " + (end - start));
        } catch (IOException | InterruptedException e) {
            LogUtils.error("JXL compression exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // TODO *** 需要细化。
    private static float getJXLCompressRatio(float quality){
        float compressRatio = 1.0f;
        if (0 < quality && quality <= 0.3){
            compressRatio = 5;
        } else if (0.3 < quality && quality <= 0.5){
            compressRatio = 4;
        } else if (0.5 < quality && quality <= 0.6){
            compressRatio = 3;
        } else if (0.6 < quality && quality <= 0.7){
            compressRatio = 2;
        } else if (0.7 < quality && quality <= 0.8){
            compressRatio = 1;
        } else if (0.8 < quality && quality <= 0.9){
            compressRatio = 0.5f;
        }else {
            compressRatio = 0.0f;
        }
        return compressRatio;
    }


    private static boolean isMacOS() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return false;
        }
        return true;
    }

    /**
     * 读取输入流中的文本数据
     *
     * @param is 输入流
     * @return 读取的文本内容
     * @throws IOException IO异常
     */
    private static String consumeInputStream(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(line);
        }
        return sb.toString();
    }


}
