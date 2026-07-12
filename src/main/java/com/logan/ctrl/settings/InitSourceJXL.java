package com.logan.ctrl.settings;

import com.logan.utils.LocalFileUtils;
import com.logan.utils.LogUtils;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;


public class InitSourceJXL {


    public void moveJXLWindows64Asset() throws IOException {
        // 移动文件
        String assetPath = LocalFileUtils.mkTempDir("jxl");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jxl/" + "jxl-x64-windows-static.7z"))),
                assetPath, "jxl-x64-windows-static.7z");
        // 解压文件
        un7zJXL(assetPath + "jxl-x64-windows-static.7z", assetPath);
    }


    private static void unZipJXL(String srcPath, String save2Path) {
        String savePath = "";
        if (save2Path == null) {
            savePath = srcPath.substring(0, srcPath.lastIndexOf(".")) + File.separator;
        } else {
            savePath = save2Path;
        }

        new File(savePath).mkdir();

        try {
            ZipFile zipFile = new ZipFile(srcPath);
            zipFile.extractAll(savePath);
            LogUtils.info("Successfully extracted: " + srcPath + " to " + savePath);
        } catch (ZipException e) {
            LogUtils.error("Failed to extract zip file: " + srcPath);
            e.printStackTrace();
        }
    }

    private static void un7zJXL(String srcPath, String save2Path) {
        String savePath = "";
        if (save2Path == null) {
            savePath = srcPath.substring(0, srcPath.lastIndexOf(".")) + File.separator;
        } else {
            savePath = save2Path;
        }

        File saveDir = new File(savePath);
        saveDir.mkdirs();

        try (SevenZFile sevenZFile = new SevenZFile(new File(srcPath))) {
            org.apache.commons.compress.archivers.ArchiveEntry entry;

            while ((entry = sevenZFile.getNextEntry()) != null) {
                File outputFile = new File(saveDir, entry.getName());

                if (entry.isDirectory()) {
                    outputFile.mkdirs();
                } else {
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

            LogUtils.info("Successfully extracted: " + srcPath + " to " + savePath);
        } catch (IOException e) {
            LogUtils.error("Failed to extract 7z file: " + srcPath);
            e.printStackTrace();
        }
    }

}
