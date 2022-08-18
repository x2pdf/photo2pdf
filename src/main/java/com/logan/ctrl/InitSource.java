package com.logan.ctrl;

import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.utils.LocalFileUtils;
import com.logan.utils.LogUtils;

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

    public void moveAsc() {
        try {
            String keyTempPath = LocalFileUtils.mkTempDir("key");
            LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                            .getResourceAsStream("keys/" + "pub.asc"))),
                    keyTempPath, "pub.asc");
            LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                            .getResourceAsStream("keys/" + "secret2.asc"))),
                    keyTempPath, "secret2.asc");
        } catch (Exception e) {
            LogUtils.error("initSource moveAsc exception. info: " + e);
        }
    }

    private void initNodeJSEnv() throws IOException {

        // /Users/megan
        // /Users/megan/code/photo2pdf-dev
//        System.out.println(System.getProperty("user.home"));//user.home    用户的主目录
//        System.out.println(System.getProperty("user.dir"));//user.dir    用户的当前工作目录

//        String jsEvnPath = System.getProperty("user.dir")  + File.separator + "photo2pdf/nodejs" + File.separator;
        String nodejsPath;
        if (System.getProperty("os.name").toLowerCase().contains("mac")){
            // 移动文件
            nodejsPath = LocalFileUtils.mkTempDir("nodejs");
            LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                            .getResourceAsStream("heic/" + "nodejs-mac-x64.zip"))),
                    nodejsPath, "nodejs-mac-x64.zip");
            // 解压文件
            unZip(nodejsPath + "nodejs-mac-x64.zip", nodejsPath);
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
                            .getResourceAsStream("heic/" + "nodejs.zip"))),
                    nodejsPath, "nodejs.zip");
            // 解压文件
            unZip(nodejsPath + "nodejs.zip", nodejsPath);
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
        unZip(assetPath + "heic-convert-js.zip", assetPath);
    }


    public static void unZip(String srcPath, String save2Path) {
        int buffer = 2048;
        int count = -1;
        String savePath = "";
        File file = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        if (save2Path == null) {
            savePath = srcPath.substring(0, srcPath.lastIndexOf(".")) + File.separator; //保存解压文件目录
        } else {
            savePath = save2Path;
        }

        new File(savePath).mkdir(); //创建保存目录
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcPath);
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                byte[] buf = new byte[buffer];
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String filename = entry.getName();
                boolean ismkdir = false;
                if (filename.lastIndexOf("/") != -1) { //检查此文件是否带有文件夹
                    ismkdir = true;
                }
                filename = savePath + filename;
                if (entry.isDirectory()) { //如果是文件夹先创建
                    file = new File(filename);
                    file.mkdirs();
                    continue;
                }
                file = new File(filename);
                if (!file.exists()) { //如果是目录先创建
                    if (ismkdir) {
                        new File(filename.substring(0, filename.lastIndexOf("/"))).mkdirs(); //目录先创建
                    }
                }
                file.createNewFile(); //创建文件
                is = zipFile.getInputStream(entry);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, buffer);
                while ((count = is.read(buf)) > -1) {
                    bos.write(buf, 0, count);
                }
                bos.flush();
                bos.close();
                fos.close();
                is.close();
            }
            zipFile.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
