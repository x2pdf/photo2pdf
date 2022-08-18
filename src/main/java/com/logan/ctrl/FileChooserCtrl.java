package com.logan.ctrl;

import com.logan.config.CacheData;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.utils.HeicConvertUtils;
import com.logan.utils.LogUtils;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Logan Qin
 * @date 2021/12/22 15:11
 */
public class FileChooserCtrl {

    public ArrayList<String> selectPhotos() {
        FileChooser fileChooser = new FileChooser();
        //设置标题
        fileChooser.setTitle(SysConfig.getLang("SelectFile"));
        //设置打开初始地址
//        fileChooser.setInitialDirectory(new File("D:" + File.separator));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Photo Format",
                        "*.heic", "*.HEIC", "*.Heic",
                        "*.heif", "*.HEIF", "*.Heif",
                        "*.jpeg", "*.JPEG", "*.Jpeg",
                        "*.jpg", "*.JPG", "*.Jpg",
                        "*.png", "*.PNG", "*.Png"
                )
        );

        //过滤选择文件类型
        // 特殊设定下的情形
        // 不支持预览的格式： tif, wmf, tiff, bmp, jfif, 支持加载但是输出pdf有问题的格式： gif
        // 在设定不预览图片的情况下，生成的pdf打不开的格式： "*.tiff"
        if (!GeneParamConfig.isIsPreviewPDFLayout() && GeneParamConfig.getPdfPhotoCompressionQuality() == 1.0f) {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Photo Format",
                            "*.jpeg", "*.JPEG", "*.Jpeg",
                            "*.jpg", "*.JPG", "*.Jpg",
                            "*.png", "*.PNG", "*.Png",
                            "*.tif", "*.TIF",
                            "*.wmf", "*.WMF",
                            "*.jfif", "*.JFIF",
                            "*.bmp", "*.BMP",
                            "*.gif", "*.GIF"
                    )
            );
        }

        //显示选择窗口,获取选中文件
        ArrayList<String> strings = new ArrayList<>();
        List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
        if (list == null) {
            return strings;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            File file = list.get(i);
            // 添加用户选择的数据，需要顺序
            CacheData.getPhotosPathUserSelectOrder().add(file.getAbsolutePath());
            strings.add(file.getAbsolutePath());
        }

        LogUtils.info("selectPhotos photo size: " + CacheData.getPhotosPathUserSelectOrder().size());
        return strings;
    }


    public ArrayList<String> selectPhotos4Experiment() {
        FileChooser fileChooser = new FileChooser();
        //设置标题
        fileChooser.setTitle(SysConfig.getLang("SelectFile"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Photo Format",
                        "*.jpeg", "*.JPEG", "*.Jpeg",
                        "*.jpg", "*.JPG", "*.Jpg",
                        "*.png", "*.PNG", "*.Png",
                        "*.heic", "*.HEIC", "*.Heic",
                        "*.heif", "*.HEIF", "*.Heif",
                        "*.jfif", "*.JFIF",
                        "*.bmp", "*.BMP",
                        "*.gif", "*.GIF"
                )
        );

        //显示选择窗口,获取选中文件
        ArrayList<String> strings = new ArrayList<>();
        List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
        if (list == null) {
            return strings;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            File file = list.get(i);
            strings.add(file.getAbsolutePath());
        }

        ArrayList<String> selectPhotosFilter = HeicConvertUtils.heicPhotoFilterMultiThread(strings);
        LogUtils.info("selectPhotos photo size: " + selectPhotosFilter.size());
        return selectPhotosFilter;
    }

    public ArrayList<String> selectPhotos4ExperimentCompress() {
        FileChooser fileChooser = new FileChooser();
        //设置标题
        fileChooser.setTitle(SysConfig.getLang("SelectFile"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Photo Format",
                        "*.jpeg", "*.JPEG", "*.Jpeg",
                        "*.jpg", "*.JPG", "*.Jpg",
                        "*.png", "*.PNG", "*.Png")
        );

        //显示选择窗口,获取选中文件
        ArrayList<String> strings = new ArrayList<>();
        List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
        if (list == null) {
            return strings;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            File file = list.get(i);
            strings.add(file.getAbsolutePath());
        }

        LogUtils.info("selectPhotos photo size: " + strings.size());
        return strings;
    }


    // todo 做成真正只可以选择一个文件的
    public File selectSinglePhoto() {
        FileChooser fileChooser = new FileChooser();
        //设置标题
        fileChooser.setTitle(SysConfig.getLang("SelectFile"));
        //过滤选择文件类型
        // 生成的pdf打不开的格式： "*.tiff"
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Photo Format",
                        "*.jpeg", "*.JPEG", "*.Jpeg",
                        "*.jpg", "*.JPG", "*.Jpg",
                        "*.png", "*.PNG", "*.Png",
                        "*.tif", "*.TIF",
                        "*.wmf", "*.WMF",
                        "*.jfif", "*.JFIF",
                        "*.bmp", "*.BMP",
                        "*.gif", "*.GIF"
                )
        );
        //显示选择窗口,获取选中文件
        List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
        if (list == null || list.size() == 0) {
            return null;
        }

        return list.get(list.size() - 1);
    }


    public ArrayList<String> selectPDFs() {
        FileChooser fileChooser = new FileChooser();
        //设置标题
        fileChooser.setTitle(SysConfig.getLang("SelectFile"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(SysConfig.getLang("FileFormat"), "*.pdf", "*.PDF", "*.Pdf")
        );

        //显示选择窗口,获取选中文件
        ArrayList<String> strings = new ArrayList<>();
        List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
        if (list == null) {
            return strings;
        }
        for (int i = 0; i < list.size(); i++) {
            File file = list.get(i);
            // 添加用户选择的数据，需要顺序
            CacheData.getPhotosPathUserSelectOrder().add(file.getAbsolutePath());
            strings.add(file.getAbsolutePath());
        }

        LogUtils.info("selectPDFs pdf size: " + strings.size());
        return strings;
    }


    // todo 做成真正只可以选择一个文件的
    public File selectSinglePdf() {
        FileChooser fileChooser = new FileChooser();
        //设置标题
        fileChooser.setTitle(SysConfig.getLang("SelectFile"));
        //过滤选择文件类型
        // 生成的pdf打不开的格式： "*.tiff"
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Photo Format", "*.pdf", "*.PDF", "*.Pdf"));
        //显示选择窗口,获取选中文件
        List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
        if (list == null || list.size() == 0) {
            return null;
        }

        return list.get(list.size() - 1);
    }


    public String chooseFilePath(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(stage);
        if (file == null) {
            return null;
        }
        return file.getAbsolutePath();
    }


}
