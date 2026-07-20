package com.logan.ctrl.settings;

import com.logan.utils.LocalFileUtils;
import com.logan.utils.LogUtils;
import com.logan.utils.OSUtils;
import com.logan.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class InitSourceAVIF {

    public void moveAVIFAsset() throws IOException {
        if (OSUtils.isMacOS()) {
            moveAVIFMacAsset();
        } else {
            moveAVIFWindowsAsset();
        }
    }

    public void moveAVIFWindowsAsset() throws IOException {
        // 移动文件
        String assetPath = LocalFileUtils.mkTempDir("avif");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("avif/" + "libavif-windows.7z"))),
                assetPath, "libavif-windows.7z");
        // 解压文件
        ZipUtils.un7z(assetPath + "libavif-windows.7z", assetPath);
    }


    public void moveAVIFMacAsset() throws IOException {
        // 移动文件
        String assetPath = LocalFileUtils.mkTempDir("avif");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("avif/" + "libavif-mac.7z"))),
                assetPath, "libavif-mac.7z");
        // 解压文件
        ZipUtils.un7z(assetPath + "libavif-mac.7z", assetPath);

        // 解压完成后，给目录下所有文件授权（仅mac需要这样子做）
        setChomd2ExecFilesInDirectory(assetPath + "libavif-mac");
    }



    public static void setChomd2ExecFilesInDirectory(String directoryPath) {
        // MacOS 要授权, 才能执行命令行
        if (OSUtils.isMacOS()) {
            LogUtils.info("给目录下所有文件授权：可执行 chmod +x. directory: " + directoryPath);
            try {
                File directory = new File(directoryPath);
                if (!directory.exists() || !directory.isDirectory()) {
                    LogUtils.error("目录不存在或不是目录: " + directoryPath);
                    return;
                }

                // 递归获取目录下所有文件并授权
                grantExecutePermissionRecursively(directory);

            } catch (Exception e) {
                LogUtils.error("给目录下文件授权失败: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private static void grantExecutePermissionRecursively(File fileOrDir) throws IOException {
        if (fileOrDir.isFile()) {
            String fileName = fileOrDir.getName().toLowerCase();

            // 跳过不需要执行权限的文件类型
            if (fileName.endsWith(".ds_store") ||
                    fileName.endsWith(".a") ||
                    fileName.endsWith(".o") ||
                    fileName.endsWith(".lib")) {
//                LogUtils.info("跳过不需要执行权限的文件: " + fileOrDir.getAbsolutePath());
                return;
            }

            // 给可执行文件授权
//            LogUtils.info("给文件授权：chmod +x " + fileOrDir.getAbsolutePath());
            try {
                // 使用数组形式执行命令，避免路径转义问题
                ProcessBuilder pb = new ProcessBuilder("chmod", "+x", fileOrDir.getAbsolutePath());
                pb.redirectErrorStream(true);
                Process process = pb.start();

                // 读取命令输出
                java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                try {
                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        LogUtils.error("文件授权失败，退出码: " + exitCode +
                                ", 文件: " + fileOrDir.getAbsolutePath() +
                                ", 输出: " + output.toString());
                    } else {
//                        LogUtils.info("文件授权成功: " + fileOrDir.getAbsolutePath());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LogUtils.error("等待授权进程被中断: " + fileOrDir.getAbsolutePath());
                }
            } catch (IOException e) {
                LogUtils.error("执行chmod命令失败: " + e.getMessage() +
                        ", 文件: " + fileOrDir.getAbsolutePath());
            }
        } else if (fileOrDir.isDirectory()) {
            // 递归处理目录下的所有文件和子目录
            File[] files = fileOrDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    grantExecutePermissionRecursively(file);
                }
            }
        }
    }
}
