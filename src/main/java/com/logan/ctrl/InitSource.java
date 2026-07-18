package com.logan.ctrl;

import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.ctrl.settings.InitSourceJXL;
import com.logan.utils.LocalFileUtils;
import com.logan.utils.LogUtils;
import com.logan.utils.ZipUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Logan Qin
 * @date 2022/1/18 10:35
 */
public class InitSource {

    public void init() {
        try {
            LogUtils.info("InitSource start");
            LogUtils.info("os.name:" + System.getProperty("os.name"));
            LogUtils.info("os.arch:" + System.getProperty("os.arch"));
            moveTemplate();
            moveAsset();
            initNodeJSEnv();
            moveJavaScriptHeicAsset();
            InitSourceJXL initSourceJXL = new InitSourceJXL();
            initSourceJXL.moveJXLAsset();
            LogUtils.info("InitSource end");
        } catch (Exception e) {
            LogUtils.error("initSource exception. info: " + e);
        }
    }

    // 将 jar 包中的 jasper文模板件复制出来保存到临时目录当中
    private void moveTemplate() throws IOException {
        String jasperTempPath = LocalFileUtils.mkTempDir("jasperTemplate");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jasper/photo2pdf/" + "Main.jasper"))),
                jasperTempPath, "Main.jasper");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jasper/photo2pdf/subreports/" + "fourbytwo.jasper"))),
                jasperTempPath, "fourbytwo.jasper");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jasper/photo2pdf/subreports/" + "fourbyone.jasper"))),
                jasperTempPath, "fourbyone.jasper");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jasper/photo2pdf/subreports/" + "twobyone.jasper"))),
                jasperTempPath, "twobyone.jasper");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jasper/photo2pdf/subreports/" + "eightbyfour.jasper"))),
                jasperTempPath, "eightbyfour.jasper");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jasper/photo2pdf/subreports/" + "onebyone.jasper"))),
                jasperTempPath, "onebyone.jasper");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jasper/photo2pdf/subreports/" + "onebytwo.jasper"))),
                jasperTempPath, "onebytwo.jasper");

        // 全部铺满的模板，不含文件mark信息
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jasper/photo2pdf/subreports/" + "fourbytwo_full.jasper"))),
                jasperTempPath, "fourbytwo_full.jasper");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jasper/photo2pdf/subreports/" + "fourbyone_full.jasper"))),
                jasperTempPath, "fourbyone_full.jasper");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jasper/photo2pdf/subreports/" + "twobyone_full.jasper"))),
                jasperTempPath, "twobyone_full.jasper");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jasper/photo2pdf/subreports/" + "eightbyfour_full.jasper"))),
                jasperTempPath, "eightbyfour_full.jasper");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jasper/photo2pdf/subreports/" + "onebyone_full.jasper"))),
                jasperTempPath, "onebyone_full.jasper");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("jasper/photo2pdf/subreports/" + "onebytwo_full.jasper"))),
                jasperTempPath, "onebytwo_full.jasper");
    }


    private void moveAsset() throws IOException {
        String assetPath = LocalFileUtils.mkTempDir("asset");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("asset/" + GeneParamConfig.getPdfSummaryPhoto()))),
                assetPath, GeneParamConfig.getPdfSummaryPhoto());
    }


    private void initNodeJSEnv() throws IOException {
        String nodejsPath;
        if (System.getProperty("os.name").toLowerCase().contains("mac")){
            // 移动文件
            nodejsPath = LocalFileUtils.mkTempDir("nodejs");
            LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                            .getResourceAsStream("heic/" + "nodejs-mac-x64.7z"))),
                    nodejsPath, "nodejs-mac-x64.7z");
            // 解压文件
            ZipUtils.un7z(nodejsPath + "nodejs-mac-x64.7z", nodejsPath);
            SysConfig.NODEJS_PATH = nodejsPath + "nodejs-mac-x64" + File.separator + "bin" + File.separator ;

            try {
                Runtime.getRuntime().exec( "chmod u+x " +  SysConfig.NODEJS_PATH + "node" );
                LogUtils.info("initNodeJSEnv chmod node success.");
            } catch (IOException e) {
                LogUtils.error("initNodeJSEnv chmod exception: " + e.toString());
                e.printStackTrace();
            }
        } else {
            // 移动文件
            nodejsPath = LocalFileUtils.mkTempDir("nodejs");
            LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                            .getResourceAsStream("heic/" + "nodejs.7z"))),
                    nodejsPath, "nodejs.7z");
            // 解压文件
            ZipUtils.un7z(nodejsPath + "nodejs.7z", nodejsPath);
            SysConfig.NODEJS_PATH = nodejsPath;
        }


    }

    private void moveJavaScriptHeicAsset() throws IOException {
        // 移动文件
        String assetPath = LocalFileUtils.mkTempDir("heic");
        LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("heic/" + "heic-convert-js.zip"))),
                assetPath, "heic-convert-js.zip");
        // 解压文件
        ZipUtils.unZip(assetPath + "heic-convert-js.zip", assetPath);
    }




}
