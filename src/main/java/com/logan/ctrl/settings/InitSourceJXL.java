package com.logan.ctrl.settings;

import com.logan.utils.LocalFileUtils;
import com.logan.utils.LogUtils;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class InitSourceJXL {


    public void moveJXLWindows64Asset() throws IOException {
        // 移动文件
        String assetPath = LocalFileUtils.mkTempDir("jxl");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jxl/" + "jxl-x64-windows-static.zip"))),
                assetPath, "jxl-x64-windows-static.zip");
        // 解压文件
        unZipJXL(assetPath + "jxl-x64-windows-static.zip", assetPath);
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
}
