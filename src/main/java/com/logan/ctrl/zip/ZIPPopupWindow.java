package com.logan.ctrl.zip;

import com.logan.config.SysConfig;
import com.logan.utils.AlertUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.util.ArrayList;


public class ZIPPopupWindow {

    public static void openZIPFuncWindow() {

        // 新建一个 Stage，也就是新的窗口
        Stage popupStage = new Stage();
        popupStage.setTitle(SysConfig.getLang("ZIPnEncryption"));

        ZIPSettingCompressionRatioHBox zipSettingCompressionRatioHBox = new ZIPSettingCompressionRatioHBox();
        zipSettingCompressionRatioHBox.initPane();
        zipSettingCompressionRatioHBox.setAction(popupStage);

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(ZIPConfig.selectZIPFilesPath);
        BorderPane filesPane = new BorderPane();
        filesPane.setCenter(listView);

        // 密码组件
        DoublePasswordInput pwdComponent = new DoublePasswordInput();

        Button zipSelectFilesButton = new Button(SysConfig.getLang("Step1:SelectZIPFiles"));
        zipSelectFilesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ZipFileChooserUtil zipFileChooserUtil = new ZipFileChooserUtil();
                ArrayList<File> files = zipFileChooserUtil.selectFiles();
                // 再次选择文件时，用户没有选择新文件时不用清空处理。有选择文件时才清空已有的数据。
                if (files.size() > 0){
                    ZIPConfig.setSelectZIPFiles(files);
                    ZIPConfig.selectZIPFilesPath = ZipFileChooserUtil.getFilesPath(files);
                    listView.getItems().clear();
                    listView.getItems().setAll(ZIPConfig.selectZIPFilesPath);
                }
            }
        });


        Button zipConfirmButton = new Button(SysConfig.getLang("Step2:ConfirmZIP"));
        zipConfirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ZipParameters zipParameters = getZipParameters(pwdComponent);

                if (ZIPConfig.selectZIPFilesPath != null && ZIPConfig.selectZIPFilesPath.size() > 0){
                    ArrayList<File> files = new ArrayList<>();
                    for (int i = 0; i < ZIPConfig.selectZIPFilesPath.size(); i++) {
                        String filePath = ZIPConfig.selectZIPFilesPath.get(i);
                        File file = new File(filePath);
                        files.add(file);
                    }

                    try {
                        // 名字还是需要来这里处理和刷新，因为用户重复zip时，zip的名字没有改变，会覆盖原先的文件名。
                        ZipFileChooserUtil.processFilesName(ZIPConfig.selectZIPFiles, pwdComponent);
                        String zipFileFullPathName = ZIPConfig.zipSavePath + File.separator + ZIPConfig.zipName;

                        ZipFile zipFile = new ZipFile(
                                zipFileFullPathName,
                                zipParameters.isEncryptFiles() ? pwdComponent.getPassword().toCharArray() : null);
                        zipFile.addFiles(files, zipParameters);
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

        HBox selectFilesButtonWrapper = new HBox(zipSelectFilesButton);
        selectFilesButtonWrapper.setAlignment(Pos.CENTER);
        HBox confirmButtonWrapper = new HBox(zipConfirmButton);
        confirmButtonWrapper.setAlignment(Pos.CENTER);

        VBox configVBox = new VBox();
        configVBox.setSpacing(5);

        // 给按钮与上方组件增加间距
        configVBox.getChildren().add(zipSettingCompressionRatioHBox.getAnchorPane());
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
            listView.getItems().clear();
        });

    }

    public static ZipParameters getZipParameters(DoublePasswordInput pwdComponent){
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


}
