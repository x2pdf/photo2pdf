package com.logan.ctrl;

import com.logan.config.CacheData;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.utils.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Logan Qin
 * @date 2021/12/28 8:51
 */
public class HelpCtrl {

    public HBox getBoxHelp() {
        Text text = new Text(SysConfig.getLang("Help"));
        text.setStyle("-fx-font-weight:bold");
        Line line = new Line(0, 0, 640, 0);
        VBox title = new VBox(line, text);

        // =============================  lang  ===================================
        ArrayList<String> langChoices = new ArrayList<>();
        langChoices.add("English");
        langChoices.add("中文");

        String lang = "English";
        if (SysConfig.LANG.equalsIgnoreCase("cn")) {
            lang = "中文";
        } else {
            lang = "English";
        }

        AnchorPane langAnchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("Language") + ":", lang, lang, langChoices);
        ChoiceBox langChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(langAnchorPane);
        langChoiceBox.setOnAction((event) -> {
            int selectedIndex = langChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(langAnchorPane, "English");
                SysConfig.LANG = "en";
            } else {
                SingleRowAnchorPaneUtils.getText2AndUpdate(langAnchorPane, "中文");
                SysConfig.LANG = "cn";
            }

            changeLangFile(SysConfig.LANG);

            Alert warning = new Alert(Alert.AlertType.INFORMATION);
            warning.setTitle("Info");
            warning.setContentText(SysConfig.getLang("LanguageSwitch"));
            warning.showAndWait();

            CacheData.refreshGeneConfig("Language setting");
        });


        AnchorPane decryptPDFAnchorPane = SingleRowAnchorPaneUtils.getTextButton(
                SysConfig.getLang("RemovePDFPassword") + ":", SysConfig.getLang("SelectFile"));
        Button decryptPDFButton = SingleRowAnchorPaneUtils.getButton(decryptPDFAnchorPane);
        decryptPDFButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!new SignatureCtrl().checkFunction("DecryptPDF")) {
                    return;
                }
                FileChooserCtrl fileChooserCtrl = new FileChooserCtrl();
                ArrayList<String> pdfs = fileChooserCtrl.selectPDFs();
                if (pdfs == null || pdfs.size() == 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setContentText(SysConfig.getLang("NoFileSelected"));
                    alert.showAndWait();
                    return;
                }

                String removePassword = GeneParamConfig.getPdfSavePath() + "removePassword" + File.separator;
                File file = new File(removePassword);
                if (!file.exists()) {
                    file.mkdirs();
                }
                for (String pdf : pdfs) {
                    try {
                        String fileFullName = pdf.substring(pdf.lastIndexOf(File.separator) + 1);
                        String msg = SysConfig.getLang("Password");
                        String password = AlertUtils.getInputPasswordByOne(fileFullName, msg);
                        if (password == null) {
                            LogUtils.info("encryptButton inputDialog cancel");
                            return;
                        }
                        PDFBoxUtils.removePDFPassword(pdf, password, removePassword, fileFullName);
                    } catch (Exception e) {
                        LogUtils.error(e.getMessage());
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle(SysConfig.getLang("Error"));
                        alert.setContentText(SysConfig.getLang("PasswordError"));
                        alert.showAndWait();
                    }
                }

                AlertUtils.openExplorer(removePassword);
            }
        });

        Button productIntroductionButton = new Button(SysConfig.getLang("ProductIntroduction"));
        productIntroductionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String saveAbsPath = productIntroduction(SysConfig.LANG);
                AlertUtils.openExplorer(GeneParamConfig.getPdfSavePath());
            }
        });

        Button operationManualButton = new Button(SysConfig.getLang("OperationManual"));
        operationManualButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String saveAbsPath = operationManual(SysConfig.LANG);
                AlertUtils.openExplorer(GeneParamConfig.getPdfSavePath());
            }
        });


        // ================================== compress pdf photos =========================================
        String ratioCompressPDF = "0.7";
        ArrayList<String> ratioCompressPDFChoices = new ArrayList<>();
        ratioCompressPDFChoices.add("1.0");
        ratioCompressPDFChoices.add("0.9");
        ratioCompressPDFChoices.add("0.8");
        ratioCompressPDFChoices.add("0.7");
        ratioCompressPDFChoices.add("0.6");
        ratioCompressPDFChoices.add("0.5");
        ratioCompressPDFChoices.add("0.4");
        ratioCompressPDFChoices.add("0.3");
        ratioCompressPDFChoices.add("0.2");
        ratioCompressPDFChoices.add("0.1");
        AnchorPane compressPDFPhotoAnchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBoxButton(
                SysConfig.getLang("CompressPDFPictures") + ":",
                ratioCompressPDF, ratioCompressPDFChoices, SysConfig.getLang("SelectFile"));

        ChoiceBox ratioPDFChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(compressPDFPhotoAnchorPane);
        ratioPDFChoiceBox.setOnAction((event) -> {
            int selectedIndex = ratioPDFChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                CacheData.setCompressPDFPhotoRatio(1.0f);
            } else if (selectedIndex == 1) {
                CacheData.setCompressPDFPhotoRatio(0.9f);
            } else if (selectedIndex == 2) {
                CacheData.setCompressPDFPhotoRatio(0.8f);
            } else if (selectedIndex == 3) {
                CacheData.setCompressPDFPhotoRatio(0.7f);
            } else if (selectedIndex == 4) {
                CacheData.setCompressPDFPhotoRatio(0.6f);
            } else if (selectedIndex == 5) {
                CacheData.setCompressPDFPhotoRatio(0.5f);
            } else if (selectedIndex == 6) {
                CacheData.setCompressPDFPhotoRatio(0.4f);
            } else if (selectedIndex == 7) {
                CacheData.setCompressPDFPhotoRatio(0.3f);
            } else if (selectedIndex == 8) {
                CacheData.setCompressPDFPhotoRatio(0.2f);
            } else if (selectedIndex == 9) {
                CacheData.setCompressPDFPhotoRatio(0.1f);
            } else {
                // default
                CacheData.setCompressPDFPhotoRatio(0.7f);
            }
        });

        Button ratioPDFPhotoButton = SingleRowAnchorPaneUtils.getButton(compressPDFPhotoAnchorPane);
        ratioPDFPhotoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooserCtrl fileChooserCtrl = new FileChooserCtrl();
                ArrayList<String> pdfs = fileChooserCtrl.selectPDFs();
                if (pdfs == null || pdfs.size() == 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setContentText(SysConfig.getLang("NoFileSelected"));
                    alert.showAndWait();
                    return;
                }

                LocalDateTime time = LocalDateTime.now();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH_mm_ss");
                String fmtTime = dtf.format(time);
                String savePath = GeneParamConfig.getPdfSavePath() + "photo2pdf_compress_pdf_" + fmtTime + File.separator;
                for (String pdf : pdfs) {
                    try {
                        String fileFullName = pdf.substring(pdf.lastIndexOf(File.separator) + 1);
                        String msg = SysConfig.getLang("EnterPasswordIfHave");
                        String password = AlertUtils.getInputPasswordByOne(fileFullName, msg);
                        if (password == null) {
                            LogUtils.info("encryptButton inputDialog cancel");
                            return;
                        }
                        PDFBoxUtils.compressPDFImages(new File(pdf), password, savePath, CacheData.getCompressPDFPhotoRatio());
                    } catch (Exception e) {
                        LogUtils.error(e.getMessage());
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle(SysConfig.getLang("Error"));
                        alert.setContentText(SysConfig.getLang("PasswordError"));
                        alert.showAndWait();
                    }
                }

                AlertUtils.openExplorer(savePath);
            }
        });

        // ================================== FormatConversion =========================================
        String format = "jpg";
        ArrayList<String> formatChoices = new ArrayList<>();
        formatChoices.add("jpg");
        formatChoices.add("jpeg");
        formatChoices.add("png");
        AnchorPane formatConversionAnchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBoxButton(SysConfig.getLang("FormatConversion") + ":",
                format, formatChoices, SysConfig.getLang("SelectFile"));
        ChoiceBox formatChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(formatConversionAnchorPane);
        formatChoiceBox.setOnAction((event) -> {
            int selectedIndex = formatChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                CacheData.setToFormat("jpg");
            } else if (selectedIndex == 1) {
                CacheData.setToFormat("jpeg");
            } else if (selectedIndex == 2) {
                CacheData.setToFormat("png");
            } else {
                // default
                CacheData.setToFormat("jpg");
            }
        });

        Button formatButton = SingleRowAnchorPaneUtils.getButton(formatConversionAnchorPane);
        formatButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooserCtrl fileChooserCtrl = new FileChooserCtrl();
                ArrayList<String> selectPhotos = fileChooserCtrl.selectPhotos4Experiment();
                if (selectPhotos == null || selectPhotos.size() == 0) {
                    LogUtils.info("FormatConversion no select photo");
                    return;
                }

                String savePath = GeneParamConfig.getPdfSavePath() + "format_" + TimeUtils.getNow_yyyy_MM_dd_HH_mm_ss() + File.separator;
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                for (String path : selectPhotos) {
                    try {
                        String fileFullName = path.substring(path.lastIndexOf(File.separator) + 1);
                        String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
                        String AbsFileFullName = savePath + fileName + "." + CacheData.getToFormat();
                        boolean b = PhotoUtils.compressPic(path, AbsFileFullName, CacheData.getToFormat(), 1);
                    } catch (Exception e) {
                        LogUtils.error("FormatConversion error: " + e.getMessage());
                    }
                }

                AlertUtils.openExplorer(savePath);
            }
        });


        // ================================== CompressPictures =========================================
        String ratio = "0.7";
        ArrayList<String> ratioChoices = new ArrayList<>();
        ratioChoices.add("1.0");
        ratioChoices.add("0.9");
        ratioChoices.add("0.8");
        ratioChoices.add("0.7");
        ratioChoices.add("0.6");
        ratioChoices.add("0.5");
        ratioChoices.add("0.4");
        ratioChoices.add("0.3");
        ratioChoices.add("0.2");
        ratioChoices.add("0.1");
        AnchorPane compressPhotoAnchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBoxButton(
                SysConfig.getLang("CompressPictures") + ":",
                ratio, ratioChoices, SysConfig.getLang("SelectFile"));
        ChoiceBox ratioChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(compressPhotoAnchorPane);
        ratioChoiceBox.setOnAction((event) -> {
            int selectedIndex = ratioChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                CacheData.setCompressRatio(1.0f);
            } else if (selectedIndex == 1) {
                CacheData.setCompressRatio(0.9f);
            } else if (selectedIndex == 2) {
                CacheData.setCompressRatio(0.8f);
            } else if (selectedIndex == 3) {
                CacheData.setCompressRatio(0.7f);
            } else if (selectedIndex == 4) {
                CacheData.setCompressRatio(0.6f);
            } else if (selectedIndex == 5) {
                CacheData.setCompressRatio(0.5f);
            } else if (selectedIndex == 6) {
                CacheData.setCompressRatio(0.4f);
            } else if (selectedIndex == 7) {
                CacheData.setCompressRatio(0.3f);
            } else if (selectedIndex == 8) {
                CacheData.setCompressRatio(0.2f);
            } else if (selectedIndex == 9) {
                CacheData.setCompressRatio(0.1f);
            } else {
                // default
                CacheData.setCompressRatio(0.7f);
            }
        });

        Button ratioButton = SingleRowAnchorPaneUtils.getButton(compressPhotoAnchorPane);
        ratioButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooserCtrl fileChooserCtrl = new FileChooserCtrl();
                ArrayList<String> selectPhotos = fileChooserCtrl.selectPhotos4Experiment();
                if (selectPhotos == null || selectPhotos.size() == 0) {
                    LogUtils.info("CompressPictures no select photo");
                    return;
                }
                String savePath = GeneParamConfig.getPdfSavePath() + "compress_" + TimeUtils.getNow_yyyy_MM_dd_HH_mm_ss() + File.separator;
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                for (String path : selectPhotos) {
                    try {
                        String fileFullName = path.substring(path.lastIndexOf(File.separator) + 1);
                        String AbsFileFullName = savePath + fileFullName;
                        String fileFormat = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

                        boolean b = PhotoUtils.compressPic(path, AbsFileFullName, fileFormat, CacheData.getCompressRatio());
                    } catch (Exception e) {
                        LogUtils.error("CompressPictures error: " + e.getMessage());
                    }
                }

                AlertUtils.openExplorer(savePath);
            }
        });


        // ================================== ScalingImageResolution =========================================
        String scaling = "0.7";
        ArrayList<String> scalingChoices = new ArrayList<>();
        scalingChoices.add("1.0");
        scalingChoices.add("0.9");
        scalingChoices.add("0.8");
        scalingChoices.add("0.7");
        scalingChoices.add("0.6");
        scalingChoices.add("0.5");
        scalingChoices.add("0.4");
        scalingChoices.add("0.3");
        scalingChoices.add("0.2");
        scalingChoices.add("0.1");

        scalingChoices.add("1.5"); //10
        scalingChoices.add("2.0");
        scalingChoices.add("3.0");
        scalingChoices.add("4.0");
        scalingChoices.add("5.0");
        scalingChoices.add("6.0");
        scalingChoices.add("8.0");
        scalingChoices.add("10");

        AnchorPane scalingAnchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBoxButton(
                SysConfig.getLang("ScalingImageResolution") + ":",
                scaling, scalingChoices, SysConfig.getLang("SelectFile"));
        ChoiceBox scalingChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(scalingAnchorPane);
        scalingChoiceBox.setOnAction((event) -> {
            int selectedIndex = scalingChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                CacheData.setScale(1.0f);
            } else if (selectedIndex == 1) {
                CacheData.setScale(0.9f);
            } else if (selectedIndex == 2) {
                CacheData.setScale(0.8f);
            } else if (selectedIndex == 3) {
                CacheData.setScale(0.7f);
            } else if (selectedIndex == 4) {
                CacheData.setScale(0.6f);
            } else if (selectedIndex == 5) {
                CacheData.setScale(0.5f);
            } else if (selectedIndex == 6) {
                CacheData.setScale(0.4f);
            } else if (selectedIndex == 7) {
                CacheData.setScale(0.3f);
            } else if (selectedIndex == 8) {
                CacheData.setScale(0.2f);
            } else if (selectedIndex == 9) {
                CacheData.setScale(0.1f);
            } else if (selectedIndex == 10) {
                CacheData.setScale(1.5f);
            } else if (selectedIndex == 11) {
                CacheData.setScale(2f);
            } else if (selectedIndex == 12) {
                CacheData.setScale(3f);
            } else if (selectedIndex == 13) {
                CacheData.setScale(4f);
            } else if (selectedIndex == 14) {
                CacheData.setScale(5f);
            } else if (selectedIndex == 15) {
                CacheData.setScale(6f);
            } else if (selectedIndex == 16) {
                CacheData.setScale(8f);
            } else if (selectedIndex == 17) {
                CacheData.setScale(10f);
            } else {
                // default
                CacheData.setScale(0.7f);
            }
        });

        Button scalingButton = SingleRowAnchorPaneUtils.getButton(scalingAnchorPane);
        scalingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooserCtrl fileChooserCtrl = new FileChooserCtrl();
                ArrayList<String> selectPhotos = fileChooserCtrl.selectPhotos4ExperimentCompress();
                if (selectPhotos == null || selectPhotos.size() == 0) {
                    LogUtils.info("CompressPictures no select photo");
                    return;
                }
                String savePath = GeneParamConfig.getPdfSavePath() + "scale_" + TimeUtils.getNow_yyyy_MM_dd_HH_mm_ss() + File.separator;
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                for (String path : selectPhotos) {
                    try {
                        String fileFullName = path.substring(path.lastIndexOf(File.separator) + 1);
                        String AbsFileFullName = savePath + fileFullName;
                        String fileFormat = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
                        boolean b = PhotoUtils.resize(path, AbsFileFullName, fileFormat, CacheData.getScale());
                    } catch (Exception e) {
                        LogUtils.error("CompressPictures error: " + e.getMessage());
                    }
                }

                AlertUtils.openExplorer(savePath);
            }
        });


        // ================================== CustomZoomImageResolution =========================================
        Text previewLabel = new Text(SysConfig.getLang("CustomZoomImageResolution") + ":");
        previewLabel.setWrappingWidth(200);
        previewLabel.setTextAlignment(TextAlignment.RIGHT);
        HBox labelBox = new HBox(previewLabel);
//        labelBox.setStyle("-fx-background-color: #d3d7d4");
        labelBox.setAlignment(Pos.CENTER_RIGHT);

        Label labelF1 = new Label("  " + SysConfig.getLang("Width"));
        TextField field1 = new TextField("1920");
        field1.setMinWidth(50);
        field1.setMaxWidth(80);

        Label labelF2 = new Label("  " + SysConfig.getLang("Height"));
        TextField field2 = new TextField("1080");
        field2.setMinWidth(50);
        field2.setMaxWidth(80);


        Button button = new Button(SysConfig.getLang("SelectFile"));
        button.setMinWidth(100);
        button.setMaxWidth(100);

        HBox previewBox = new HBox(labelBox, labelF1, field1, labelF2, field2);
        previewBox.setAlignment(Pos.CENTER_RIGHT);

        AnchorPane customScalingAnchorPane = new AnchorPane(previewBox, button);
        AnchorPane.setRightAnchor(button, 2.0);

        field1.textProperty().addListener((observable, oldValue, newValue) -> {
            int parseInt = 1920;
            try {
                parseInt = Integer.parseInt(newValue);
            } catch (Exception e) {
                field1.textProperty().setValue("1920");
            }
            CacheData.setWidth(parseInt);
        });
        field2.textProperty().addListener((observable, oldValue, newValue) -> {
            int parseInt = 1080;
            try {
                parseInt = Integer.parseInt(newValue);
            } catch (Exception e) {
                field2.textProperty().setValue("1080");
            }
            CacheData.setHeight(parseInt);
        });
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooserCtrl fileChooserCtrl = new FileChooserCtrl();
                ArrayList<String> selectPhotos = fileChooserCtrl.selectPhotos4Experiment();
                if (selectPhotos == null || selectPhotos.size() == 0) {
                    LogUtils.info("CustomZoomImageResolution no select photo");
                    return;
                }

                String savePath = GeneParamConfig.getPdfSavePath() + "scale_custom_" + TimeUtils.getNow_yyyy_MM_dd_HH_mm_ss() + File.separator;
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                for (String path : selectPhotos) {
                    try {
                        String fileFullName = path.substring(path.lastIndexOf(File.separator) + 1);
                        String AbsFileFullName = savePath + fileFullName;
                        String fileFormat = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
                        boolean b = PhotoUtils.resize(path, AbsFileFullName, CacheData.getWidth(), CacheData.getHeight(), fileFormat);
                    } catch (Exception e) {
                        LogUtils.error("CustomZoomImageResolution error: " + e.getMessage());
                    }
                }

                AlertUtils.openExplorer(savePath);
            }
        });


        VBox formatConversionVBox = new VBox(compressPDFPhotoAnchorPane,
                formatConversionAnchorPane, compressPhotoAnchorPane, scalingAnchorPane, customScalingAnchorPane);
        formatConversionVBox.setSpacing(4);

        Text experimentalTitle = new Text(SysConfig.getLang("ExperimentalFeature"));
        experimentalTitle.setStyle("-fx-font-weight:bold");
        Text space = new Text(SysConfig.getLang(""));
        VBox experimentalFeatureVBox = new VBox(experimentalTitle, space, formatConversionVBox);
        experimentalFeatureVBox.setSpacing(8);
        experimentalFeatureVBox.setStyle("-fx-background-color: #f2eada");  // #d3d7d4

        AnchorPane experimentalPane = new AnchorPane(experimentalFeatureVBox);
        AnchorPane.setTopAnchor(experimentalFeatureVBox, 100.0);
        AnchorPane.setLeftAnchor(experimentalFeatureVBox, 30.0);
        AnchorPane.setRightAnchor(experimentalFeatureVBox, 30.0);


        Text version = new Text("photo2pdf version 22.01");
        Text allRight = new Text("All Rights Reserved.");
        VBox vBox = new VBox(productIntroductionButton, operationManualButton, version, allRight);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(4);

        AnchorPane statementPane = new AnchorPane(vBox);
        AnchorPane.setTopAnchor(vBox, 200.0);
        AnchorPane.setLeftAnchor(vBox, 2.0);
        AnchorPane.setRightAnchor(vBox, 2.0);

        VBox vb = new VBox();
        vb.getChildren().addAll(title, langAnchorPane, decryptPDFAnchorPane, experimentalPane, statementPane);
        vb.setSpacing(2);

        HBox hBox = new HBox(vb);
        return hBox;

    }

    public void changeLangFile(String lang) {
        try {
            // 先删除之前的文件
            String langTemp1 = LocalFileUtils.mkTempDir("language");
            LocalFileUtils.deleteDirectory(langTemp1);

            String langTemp = LocalFileUtils.mkTempDir("language");

            if (SysConfig.LANG.equalsIgnoreCase("en")) {
                LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                                .getResourceAsStream("lang/" + "en.properties"))),
                        langTemp, "en.properties");
            } else if (SysConfig.LANG.equalsIgnoreCase("cn")) {
                LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                                .getResourceAsStream("lang/" + "cn.properties"))),
                        langTemp, "cn.properties");
            } else {
                LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                                .getResourceAsStream("lang/" + "en.properties"))),
                        langTemp, "en.properties");
            }

            LogUtils.info("lang change to:" + lang);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String productIntroduction(String lang) {
        try {
            String savePath = GeneParamConfig.getPdfSavePath();
            String fileFullName = "photo2pdf_product_introduction_en.pdf";

            if (lang.equalsIgnoreCase("en")) {
                fileFullName = "photo2pdf_product_introduction_en.pdf";
            } else {
                fileFullName = "photo2pdf_product_introduction_cn.pdf";
            }
            LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                            .getResourceAsStream("asset/" + fileFullName))),
                    savePath, fileFullName);

            return savePath + fileFullName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String operationManual(String lang) {
        try {
            String savePath = GeneParamConfig.getPdfSavePath();
            String fileFullName = "operation_manual_en.pdf";

            if (lang.equalsIgnoreCase("en")) {
                fileFullName = "photo2pdf_operation_manual_en.pdf";
            } else {
                fileFullName = "photo2pdf_operation_manual_cn.pdf";
            }

            LocalFileUtils.save2TempDir(LocalFileUtils.is2Byte(Objects.requireNonNull(getClass().getClassLoader()
                            .getResourceAsStream("asset/" + fileFullName))),
                    savePath, fileFullName);

            return savePath + fileFullName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
