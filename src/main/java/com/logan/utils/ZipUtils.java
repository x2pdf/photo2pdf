package com.logan.utils;

import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {

    /**
     * 解压ZIP文件到指定目录
     * 该方法将ZIP压缩包解压到目标路径，支持自定义保存路径或使用默认路径（ZIP文件所在目录）。
     * 会自动处理目录结构和文件创建，保持原有的文件夹层级关系。
     *
     * @param srcPath ZIP源文件的完整路径
     * @param save2Path 解压后文件的保存目录路径，如果为null则保存到ZIP文件同级目录
     * @throws IOException 当解压过程中发生IO错误时抛出
     */
    public static void unZip(String srcPath, String save2Path) throws IOException {
        int buffer = 8192; // 增大缓冲区大小以提高性能
        String savePath;

        if (save2Path == null) {
            savePath = srcPath.substring(0, srcPath.lastIndexOf(".")) + File.separator;
        } else {
            savePath = save2Path;
        }

        // 确保保存目录存在
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        // 使用try-with-resources确保ZipFile被正确关闭
        try (ZipFile zipFile = new ZipFile(srcPath)) {
            Enumeration<?> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String entryName = entry.getName();

                // 防止Zip Slip漏洞：验证解压路径
                String normalizedPath = new File(savePath, entryName).getCanonicalPath();
                if (!normalizedPath.startsWith(new File(savePath).getCanonicalPath())) {
                    throw new IOException("非法的ZIP条目路径: " + entryName);
                }

                File targetFile = new File(normalizedPath);
                if (entry.isDirectory()) {
                    // 创建目录
                    targetFile.mkdirs();
                } else {
                    // 确保父目录存在
                    File parentDir = targetFile.getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    // 使用try-with-resources确保流被正确关闭
                    try (InputStream is = zipFile.getInputStream(entry);
                         FileOutputStream fos = new FileOutputStream(targetFile);
                         BufferedOutputStream bos = new BufferedOutputStream(fos, buffer)) {

                        byte[] buf = new byte[buffer];
                        int count;
                        while ((count = is.read(buf)) != -1) {
                            bos.write(buf, 0, count);
                        }
                    }
                }
            }
        }
    }



    /**
     * 解压7Z文件到指定目录
     * 该方法将7Z压缩包解压到目标路径，支持自定义保存路径或使用默认路径（7Z文件所在目录）。
     * 会自动处理目录结构和文件创建，保持原有的文件夹层级关系。
     *
     * @param srcPath 7Z源文件的完整路径
     * @param save2Path 解压后文件的保存目录路径，如果为null则保存到7Z文件同级目录
     */
    public static void un7z(String srcPath, String save2Path) {
        // 确定解压后的保存路径
        String savePath = "";
        if (save2Path == null) {
            savePath = srcPath.substring(0, srcPath.lastIndexOf(".")) + File.separator;
        } else {
            savePath = save2Path;
        }

        // 确保保存目录存在
        File saveDir = new File(savePath);
        saveDir.mkdirs();

        // 使用try-with-resources解压7Z文件并处理每个条目
        try (SevenZFile sevenZFile = new SevenZFile(new File(srcPath))) {
            org.apache.commons.compress.archivers.ArchiveEntry entry;
            while ((entry = sevenZFile.getNextEntry()) != null) {
                File outputFile = new File(saveDir, entry.getName());

                // 处理目录条目：直接创建目录
                if (entry.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    // 处理文件条目：确保父目录存在并写入文件内容
                    outputFile.getParentFile().mkdirs();
                    try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = sevenZFile.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }

            LogUtils.info("Successfully extracted(7z): " + srcPath + " to " + savePath);
        } catch (IOException e) {
            LogUtils.error("Failed to extract 7z file: " + srcPath);
            e.printStackTrace();
        }
    }


}
