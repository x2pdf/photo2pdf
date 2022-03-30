package com.logan.ctrl;

import com.logan.config.GeneParamConfig;
import com.logan.utils.LocalFileUtils;
import com.logan.utils.LogUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Logan Qin
 * @date 2022/1/18 10:35
 */
public class InitSource {

    public void init() {
        try {
            LogUtils.info("InitSource start");
            moveTemplate();
            moveAsset();
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

}
