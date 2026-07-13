package com.logan.ctrl.helppage.experfunc.txtgeneimg;

import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.utils.AlertUtils;
import com.logan.utils.TimeFormatUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


public class TextToImageHomepage {

    public static void openTextToImgWindow() {
        // 新建一个 Stage，也就是新的窗口
        Stage popupStage = new Stage();
        popupStage.setTitle(SysConfig.getLang("TextToImg"));

        HBox dataBox = T2ImgDataHBox.getDataBox();
        HBox dataBoxWrapper = new HBox(dataBox);
        dataBoxWrapper.setAlignment(Pos.CENTER);
        // 关键：让 HBox 宽度跟随 VBox 拉伸
        dataBoxWrapper.setMaxWidth(Double.MAX_VALUE);

        T2ImageTitleFontSizeHBox titleFontSizeHBox = new T2ImageTitleFontSizeHBox();
        titleFontSizeHBox.initPane();
        titleFontSizeHBox.setAction(popupStage);
        T2ImageTextFontSizeHBox textFontSizeHBox = new T2ImageTextFontSizeHBox();
        textFontSizeHBox.initPane();
        textFontSizeHBox.setAction(popupStage);
        T2ImageSubTitleFontSizeHBox subTitleFontSizeHBox = new T2ImageSubTitleFontSizeHBox();
        subTitleFontSizeHBox.initPane();
        subTitleFontSizeHBox.setAction(popupStage);
        T2ImageSubTitleNoteFontSizeHBox subTitleNoteFontSizeHBox = new T2ImageSubTitleNoteFontSizeHBox();
        subTitleNoteFontSizeHBox.initPane();
        subTitleNoteFontSizeHBox.setAction(popupStage);


        Button textToImgConfirmButton = new Button(SysConfig.getLang("TextToImgConfirmButton"));
        textToImgConfirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                byte[] image = new byte[0];
                try {
                    // 生成PNG格式的图片
                    image = ImageTextRenderer.renderTextImage(
                            TextToImageDataDTO.title, TextToImageDataDTO.text,
                            TextToImageDataDTO.subTitle, TextToImageDataDTO.subTitleNote,
                            "220,220,220", "255,255,255",
                            20,
                            TextToImageConfig.titleFontSize, TextToImageConfig.textFontSize,
                            TextToImageConfig.subTitleFontSize, TextToImageConfig.subTitleNoteFontSize,
                            2160);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String savePath = GeneParamConfig.getPdfSavePath() + "textToImage" + File.separator;
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                boolean success = ImageTextRenderer.saveImage(image, "textToImage_" + TimeFormatUtil.getNow_yyyy_MM_dd_HH_mm_ss() + ".png", savePath);
                if (success) {
                    AlertUtils.openExplorer(savePath);
                } else {
                    AlertUtils.error("应用出错了！\nThe application is malfunctioning!");
                }
//                popupStage.close();
            }
        });
        HBox buttonWrapper = new HBox(textToImgConfirmButton);
        buttonWrapper.setAlignment(Pos.CENTER);

        VBox configVBox = new VBox();
        configVBox.setSpacing(5);

        // 给按钮与上方组件增加间距
        configVBox.getChildren().add(titleFontSizeHBox.getAnchorPane());
        configVBox.getChildren().add(textFontSizeHBox.getAnchorPane());
        configVBox.getChildren().add(subTitleFontSizeHBox.getAnchorPane());
        configVBox.getChildren().add(subTitleNoteFontSizeHBox.getAnchorPane());
        configVBox.getChildren().add(dataBoxWrapper);

        VBox.setMargin(buttonWrapper, new Insets(5, 0, 0, 0)); // 上方间距5px
        configVBox.getChildren().add(buttonWrapper);

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
    }

}
