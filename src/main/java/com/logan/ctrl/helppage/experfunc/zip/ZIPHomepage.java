package com.logan.ctrl.helppage.experfunc.zip;

import com.logan.config.SysConfig;
import com.logan.utils.AlertUtils;
import com.logan.utils.FileUtils;
import com.logan.utils.LogUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class ZIPHomepage {

    public static void openZIPFuncWindow() {
        // 新建一个 Stage，也就是新的窗口
        Stage popupStage = new Stage();
        popupStage.setTitle(SysConfig.getLang("ZIPnEncryption"));

        // 密码组件
        ZIPPasswordInput pwdComponent = new ZIPPasswordInput();

        // zip 压缩率的选择HBox
        ZIPSettingCompressionRatioHBox zipSettingCompressionRatioHBox = new ZIPSettingCompressionRatioHBox();
        zipSettingCompressionRatioHBox.initPane();
        zipSettingCompressionRatioHBox.setAction(popupStage);

        // zip 预设密码的选择HBox
        ZIPPasswordPresetHBox zipPasswordPresetHBox = new ZIPPasswordPresetHBox();
        zipPasswordPresetHBox.initPane();
        zipPasswordPresetHBox.setAction(popupStage, pwdComponent);

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(ZIPConfig.selectZIPFilesPath);
        BorderPane filesPane = new BorderPane();
        filesPane.setCenter(listView);
        processListviewDrag(listView);
        processDeleteListviewItem(listView);


        Button zipSelectFilesButton = new Button(SysConfig.getLang("Step1SelectZIPFiles"));
        zipSelectFilesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ZipFileChooserUtil zipFileChooserUtil = new ZipFileChooserUtil();
                ArrayList<File> files = zipFileChooserUtil.selectFiles();
                for (File file : files) {
                    boolean isAdd = addListviewDragFiles(file);
                    // 成功才添加显示。保证数据一致性。
                    if (isAdd){
                        listView.getItems().add(file.getAbsolutePath());
                    }
                }
            }
        });

        Button zipSelectDirButton = new Button(SysConfig.getLang("Step1SelectZIPDir"));
        zipSelectDirButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ZipFileChooserUtil zipFileChooserUtil = new ZipFileChooserUtil();
                File selectDirectory = zipFileChooserUtil.selectDirectory();
                if (selectDirectory != null){
                    boolean isAdd = addListviewDragDir(selectDirectory);
                    // 成功才添加显示。保证数据一致性。
                    if (isAdd){
                        listView.getItems().add(selectDirectory.getAbsolutePath());
                    }
                }
            }
        });


        Button zipConfirmButton = new Button(SysConfig.getLang("Step2ConfirmZIP"));
        zipConfirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ZipParameters zipParameters = getZipParameters(pwdComponent);

                if (ZIPConfig.selectZIPFilesPath != null && ZIPConfig.selectZIPFilesPath.size() > 0){
                    ArrayList<File> toBeCompressedFiles = new ArrayList<>();
                    for (int i = 0; i < ZIPConfig.selectZIPFiles.size(); i++) {
                        String filePath = ZIPConfig.selectZIPFiles.get(i).getAbsolutePath();
                        File file = new File(filePath);
                        toBeCompressedFiles.add(file);
                    }

                    ArrayList<File> toBeCompressedDirs = new ArrayList<>();
                    for (int i = 0; i < ZIPConfig.selectZIPDirs.size(); i++) {
                        String filePath = ZIPConfig.selectZIPDirs.get(i).getAbsolutePath();
                        File file = new File(filePath);
                        toBeCompressedDirs.add(file);
                    }

                    try {
                        ZipFileChooserUtil.processFilesName(pwdComponent);
                        String zipFileFullPathName = makeZipName(toBeCompressedDirs);

                        ZipFile zipFile = new ZipFile(
                                zipFileFullPathName,
                                zipParameters.isEncryptFiles() ? pwdComponent.getPassword().toCharArray() : null);
                        if (toBeCompressedFiles.size() > 0){
                            zipFile.addFiles(toBeCompressedFiles, zipParameters);
                        }
                        for (File toBeCompressedDir : toBeCompressedDirs) {
                            zipFile.addFolder(toBeCompressedDir, zipParameters);
                        }

                        // zip完成后弹窗提示处理成功。
                        AlertUtils.openExplorer(ZIPConfig.zipSavePath);
                    } catch (ZipException e) {
                        e.printStackTrace();
                        AlertUtils.error("File compression failed. Sorry~");
                    }
                } else {
                    AlertUtils.msg("没有选择文件。 \nNo file was selected.");
                }
            }
        });

        HBox selectFilesButtonWrapper = new HBox(zipSelectFilesButton, zipSelectDirButton);
        selectFilesButtonWrapper.setAlignment(Pos.CENTER);
        selectFilesButtonWrapper.setSpacing(4.0);
        HBox confirmButtonWrapper = new HBox(zipConfirmButton);
        confirmButtonWrapper.setAlignment(Pos.CENTER);

        VBox configVBox = new VBox();
        configVBox.setSpacing(5);

        // 给按钮与上方组件增加间距
        configVBox.getChildren().add(zipSettingCompressionRatioHBox.getAnchorPane());
        configVBox.getChildren().add(zipPasswordPresetHBox.getAnchorPane());
        configVBox.getChildren().add(pwdComponent);
        VBox.setMargin(filesPane, new Insets(10, 0, 0, 0)); // 上方间距10px
        configVBox.getChildren().add(filesPane);

        VBox.setMargin(selectFilesButtonWrapper, new Insets(5, 0, 0, 0)); // 上方间距5px
        configVBox.getChildren().add(selectFilesButtonWrapper);
        VBox.setMargin(confirmButtonWrapper, new Insets(5, 0, 0, 0)); // 上方间距5px
        configVBox.getChildren().add(confirmButtonWrapper);

        AnchorPane textToImgAnchorPane = new AnchorPane();
        textToImgAnchorPane.getChildren().addAll(configVBox);
        AnchorPane.setTopAnchor(configVBox, 5.0);
        AnchorPane.setLeftAnchor(configVBox, 2.0);
        AnchorPane.setRightAnchor(configVBox, 2.0);
        AnchorPane.setBottomAnchor(configVBox, 2.0);

        Scene popupScene = new Scene(textToImgAnchorPane, 840, 600);
        popupStage.setScene(popupScene);
        // 显示窗口
        popupStage.show();

        popupStage.setOnCloseRequest(event -> {
            ZIPConfig.selectZIPFiles.clear();
            ZIPConfig.selectZIPFilesPath.clear();
            listView.getItems().clear();
        });

    }

    public static ZipParameters getZipParameters(ZIPPasswordInput pwdComponent){
        ZipParameters zipParameters = new ZipParameters();
        String pwd = pwdComponent.getPassword();
        // 启用了加密，密码验证通过，密码不为空
        if (pwdComponent.isEncryptionEnabled() && pwdComponent.isValid() && pwd != null && !pwd.trim().isEmpty()) {
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
            zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        } else {
            zipParameters.setEncryptFiles(false);
            zipParameters.setEncryptionMethod(EncryptionMethod.NONE);
        }
        return zipParameters;
    }


    private static void processListviewDrag(ListView<String> listView){
        listView.setOnDragOver(event -> {
            if (event.getGestureSource() != listView && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        listView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                for (File file : db.getFiles()) {
                    // 如果不存在
                    if (!isListviewContainsFile(file)){
                        boolean isAdd = false;
                        if (file.isFile()){
                            isAdd = addListviewDragFiles(file);
                        }
                        if (file.isDirectory()){
                            isAdd = addListviewDragDir(file);
                        }

                        // 成功才添加显示。保证数据一致性。
                        if (isAdd){
                            listView.getItems().add(file.getAbsolutePath());
                        }
                    }

                }
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }

    private static void processDeleteListviewItem(ListView<String> listView){
        listView.setCellFactory(lv -> new ListCell<String>() {
            private final Label label = new Label();
            private final Button removeBtn = new Button("✕");
            private final Region spacer = new Region();
            private final HBox hBox = new HBox(label, spacer, removeBtn);
            {
                HBox.setHgrow(spacer, Priority.ALWAYS);
                removeBtn.setOnAction(e -> {
                    String item = getItem();
                    deleteSelectFileOrDir(item);
                    setGraphic(null);
                    listView.getItems().remove(item);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    setGraphic(hBox);
                }
            }


        });
    }

    private static boolean addListviewDragDir(File file){
        if(file == null){
            return true;
        }
        if (!isListviewContainsFile(file)){
            ZIPConfig.selectZIPDirs.add(file);
            ZIPConfig.selectZIPFilesPath.add(file.getAbsolutePath());
            return true;
        }
        return false;
    }

    private static boolean addListviewDragFiles(File file){
        if(file == null){
            return true;
        }
        if (!isListviewContainsFile(file)){
            ZIPConfig.selectZIPFiles.add(file);
            ZIPConfig.selectZIPFilesPath.add(file.getAbsolutePath());
            return true;
        }
        return false;
    }


    private static boolean isListviewContainsFile(File file){
        boolean isContain = false;
        for (File selectZIPFile : ZIPConfig.selectZIPFiles) {
            if (selectZIPFile.getAbsolutePath().equals(file.getAbsolutePath())){
                LogUtils.info("文件已经存在listview当中：" + file.getAbsolutePath());
                isContain =  true;
            }
        }

        for (File selectZIPDirs : ZIPConfig.selectZIPDirs) {
            if (selectZIPDirs.getAbsolutePath().equals(file.getAbsolutePath())){
                LogUtils.info("文件已经存在listview当中：" + file.getAbsolutePath());
                isContain =  true;
            }
        }
        return isContain;
    }

    private static void deleteSelectFileOrDir(String item){
        ZIPConfig.selectZIPFiles.removeIf(file -> item.equals(file.getAbsolutePath()));
        ZIPConfig.selectZIPDirs.removeIf(file -> item.equals(file.getAbsolutePath()));
        ZIPConfig.selectZIPFilesPath.removeIf(item::equals);
    }


    private static String makeZipName(ArrayList<File> toBeCompressedDirs){
        String zipFileFullPathName = ZIPConfig.zipSavePath + File.separator + ZIPConfig.zipName;
        if (!checkZipName(zipFileFullPathName, toBeCompressedDirs)){
            String userComputerDownloadPath = FileUtils.getUserComputerDownloadPath();
            if (checkZipName(userComputerDownloadPath, toBeCompressedDirs)){
                zipFileFullPathName = userComputerDownloadPath + File.separator + ZIPConfig.zipName;
            }else {
                AlertUtils.error("File compression failed. Sorry~");
                throw new IllegalArgumentException("ZIP的保存位置和压缩文件夹相同，存在循环压缩的bug。");
            }
        }
        return zipFileFullPathName;
    }


    private static boolean checkZipName(String zipFileFullPathName, ArrayList<File> toBeCompressedDirs){
        for (File toBeCompressedDir : toBeCompressedDirs) {
            Path zipPath = Paths.get(zipFileFullPathName).toAbsolutePath().normalize();
            Path dirPath = toBeCompressedDir.toPath().toAbsolutePath().normalize();
            if (zipPath.startsWith(dirPath)) {
                LogUtils.info("checkZipName, ZIP文件不能保存到待压缩目录内部: " + dirPath);
                return false;
            }
        }
        return true;
    }
}
