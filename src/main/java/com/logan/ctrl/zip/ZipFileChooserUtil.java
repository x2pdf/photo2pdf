package com.logan.ctrl.zip;

import com.logan.config.SysConfig;
import com.logan.utils.LogUtils;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ZipFileChooserUtil {

    public ArrayList<File> selectFiles() {
        FileChooser fileChooser = new FileChooser();
        //设置标题
        fileChooser.setTitle(SysConfig.getLang("SelectFile"));
        //显示选择窗口,获取选中文件
        ArrayList<File> files = new ArrayList<>();
        List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
        if (list == null || list.size() == 0) {
            return files;
        }
        files.addAll(list);
        return files;
    }

    public File selectDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(SysConfig.getLang("SelectFile"));
        // 可以设置初始目录
        // directoryChooser.setInitialDirectory(new File("/path/to/default"));
        return directoryChooser.showDialog(new Stage());
    }


    public static void processFilesName( DoublePasswordInput pwdComponent){
        if (ZIPConfig.selectZIPFilesPath.size() == 0) {
            return;
        }


        if (ZIPConfig.selectZIPFilesPath.size() == 1) {
            if (ZIPConfig.selectZIPFiles.size() == 1){
                setZipNameByFiles(pwdComponent);

                // 获取用户选择文件所处在的文件夹，用来默认保存生成的zip文件
                ZIPConfig.setZipSavePath(ZIPConfig.selectZIPFiles.get(0).getParentFile().getAbsolutePath());
            }else {
                setZipNameByDir(pwdComponent);

                // 获取用户选择文件所处在的文件夹，用来默认保存生成的zip文件
                ZIPConfig.setZipSavePath(ZIPConfig.selectZIPDirs.get(0).getParentFile().getAbsolutePath());
            }
        } else {
            // 优先使用文件夹名称
            if (ZIPConfig.selectZIPDirs.size() > 0) {
                setZipNameByDir(pwdComponent);
            }else {
                setZipNameByFiles(pwdComponent);
            }

            // 默认列表第一个，获取用户选择文件所处在的文件夹，用来默认保存生成的zip文件
            File file = new File(ZIPConfig.selectZIPFilesPath.get(0));
            ZIPConfig.setZipSavePath(file.getParentFile().getAbsolutePath());
        }

    }


    private static void setZipNameByDir(DoublePasswordInput pwdComponent){
        String nameNote = "";
        if (pwdComponent.isEncryptionEnabled() && pwdComponent.isValid()){
            nameNote = firstAndLast(pwdComponent.getPassword());
        }
        if (pwdComponent.isEncryptionEnabled() && pwdComponent.isValid()){
            ZIPConfig.zipName = getFileBaseName(ZIPConfig.selectZIPFiles.get(0).getName()) + "_" + nameNote + "_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".zip";
        }else {
            ZIPConfig.zipName = getFileBaseName(ZIPConfig.selectZIPFiles.get(0).getName()) + "_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".zip";
        }
    }

    private static void setZipNameByFiles(DoublePasswordInput pwdComponent){
        String nameNote = "";
        if (pwdComponent.isEncryptionEnabled() && pwdComponent.isValid()){
            nameNote = firstAndLast(pwdComponent.getPassword());
        }
        if (pwdComponent.isEncryptionEnabled() && pwdComponent.isValid()){
            ZIPConfig.zipName = getFileBaseName(ZIPConfig.selectZIPFiles.get(0).getName()) + "_" + nameNote + "_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".zip";
        }else {
            ZIPConfig.zipName = getFileBaseName(ZIPConfig.selectZIPFiles.get(0).getName()) + "_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".zip";
        }
    }


    public static ArrayList<String> getFilesPath(List<File> list){
        ArrayList<String> strings = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return strings;
        }

        for (int i = 0; i < list.size(); i++) {
            File file = list.get(i);
            // 添加用户选择的数据，需要顺序
            strings.add(file.getAbsolutePath());
        }

        return strings;
    }


    public static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return (lastDot > 0 && lastDot < fileName.length() - 1)
                ? fileName.substring(lastDot + 1)
                : "";
    }

    public static String getFileBaseName(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return (lastDot > 0)
                ? fileName.substring(0, lastDot)
                : fileName;
    }

    public static String firstAndLast(String s) {
        if (s == null || s.length() < 1) {
            return "";
        }
        return "" + s.charAt(0) + s.charAt(s.length() - 1);
    }
}
