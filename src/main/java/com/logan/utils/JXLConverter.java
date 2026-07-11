package com.logan.utils;

import com.logan.config.SysConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class JXLConverter {

    public static void converter2JXL(String srcFilePath, String descFilePath, float quality){
        try {
            long start = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();

            // 构建命令: SysConfig.JXL_CONVERT + "cjxl.exe" srcFilePath descFilePath
            // TODO ***
            String cjxlPath = SysConfig.JXL_CONVERT + "cjxl.exe";
            String[] command = new String[]{cjxlPath, srcFilePath, descFilePath};

            LogUtils.info("Executing JXL conversion: " + cjxlPath + " " + srcFilePath + " -> " + descFilePath);

            Process process = runtime.exec(command, null, new File(SysConfig.JXL_CONVERT));

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


    /**
     * 读取输入流中的文本数据
     * @param is 输入流
     * @return 读取的文本内容
     * @throws IOException IO异常
     */
    private  static String consumeInputStream(InputStream is) throws IOException {
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
