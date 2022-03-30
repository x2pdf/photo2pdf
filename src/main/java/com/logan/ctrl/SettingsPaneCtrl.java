package com.logan.ctrl;

import com.logan.config.CacheData;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.utils.LocalFileUtils;
import com.logan.utils.LogUtils;
import com.logan.utils.SingleRowAnchorPaneUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Logan Qin
 * @date 2021/12/22 15:18
 */
public class SettingsPaneCtrl {

    public HBox getBoxConfig(Stage stage) {
        Text text = new Text(SysConfig.getLang("Setting"));
        text.setStyle("-fx-font-weight:bold");
        Line line = new Line(0, 0, 640, 0);
        VBox title = new VBox(line, text);

        // =============================  PDF Save Path  ===================================
        AnchorPane pathAnchorPane = SingleRowAnchorPaneUtils.getTextTextFieldButton(SysConfig.getLang("PDFSavePath") + ":",
                GeneParamConfig.getPdfSavePath(), SysConfig.getLang("ChangePath"));
        Button pathButton = SingleRowAnchorPaneUtils.getButton(pathAnchorPane);
        pathButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!new SignatureCtrl().checkFunction("PDFSavePath")) {
                    SingleRowAnchorPaneUtils.getTextFieldAndUpdate(pathAnchorPane, GeneParamConfig.getPdfSavePath());
                    return;
                }
                FileChooserCtrl fileChooserCtrl = new FileChooserCtrl();
                String savePath = fileChooserCtrl.chooseFilePath(stage);
                if (savePath != null) {
                    savePath = savePath + File.separator;
                    LogUtils.info("savePath refresh: " + savePath);
                    GeneParamConfig.setPdfSavePath(savePath);
                    SingleRowAnchorPaneUtils.getTextFieldAndUpdate(pathAnchorPane, savePath);
                }
            }
        });


        // =============================  Preview  ===================================
        ArrayList<String> previewChoices = new ArrayList<>();
        previewChoices.add("Yes");
        previewChoices.add("No");
        AnchorPane previewAnchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("Preview") + ":",
                "Yes", "Yes", previewChoices);
        ChoiceBox previewChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(previewAnchorPane);
        previewChoiceBox.setOnAction((event) -> {
            if (!new SignatureCtrl().checkFunction("Preview")) {
                SingleRowAnchorPaneUtils.getChoiceBox(previewAnchorPane).setValue("Yes");
                return;
            }
            int selectedIndex = previewChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(previewAnchorPane, "Yes");
                GeneParamConfig.setIsPreviewPDFLayout(true);
                CacheData.refreshGeneConfig("need preview");
                LogUtils.info("Preview: Yes");
            } else {
                SingleRowAnchorPaneUtils.getText2AndUpdate(previewAnchorPane, "No");
                GeneParamConfig.setIsPreviewPDFLayout(false);
                // 清除预览
                CacheData.clearPreviewPhoto();

                GridPane gridPane = CacheData.gridPane;
                ArrayList<String> photosPath = CacheData.getPhotosPreviewPath();
                ViewGridPaneCtrl viewGridPaneCtrl = new ViewGridPaneCtrl();
                viewGridPaneCtrl.photosViewPane(gridPane, photosPath);
                LogUtils.info("Preview: No");
            }
        });


        // =============================  cover  ===================================
        ArrayList<String> coverChoices = new ArrayList<>();
        coverChoices.add("Yes");
        coverChoices.add("No");
        AnchorPane coverAnchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("PDFCover") + ":",
                "Yes", "Yes", coverChoices);
        ChoiceBox coverChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(coverAnchorPane);
        coverChoiceBox.setOnAction((event) -> {
            if (!new SignatureCtrl().checkFunction("PDFCover")) {
                SingleRowAnchorPaneUtils.getChoiceBox(coverAnchorPane).setValue("Yes");
                return;
            }
            int selectedIndex = coverChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(coverAnchorPane, "Yes");
                GeneParamConfig.getPdf().setIsNeedCoverPage("Y");
                LogUtils.info("PDFCover: Yes");
            } else {
                SingleRowAnchorPaneUtils.getText2AndUpdate(coverAnchorPane, "No");
                GeneParamConfig.getPdf().setIsNeedCoverPage("N");
                LogUtils.info("PDFCover: No");
            }
        });


        // =============================  summary  ===================================
        ArrayList<String> summaryChoices = new ArrayList<>();
        summaryChoices.add("Yes");
        summaryChoices.add("No");
        AnchorPane summaryAnchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("PDFSummary") + ":",
                "Yes", "Yes", summaryChoices);
        ChoiceBox summaryChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(summaryAnchorPane);
        summaryChoiceBox.setOnAction((event) -> {
            if (!new SignatureCtrl().checkFunction("PDFSummary")) {
                SingleRowAnchorPaneUtils.getChoiceBox(summaryAnchorPane).setValue("Yes");
                return;
            }
            int selectedIndex = summaryChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(summaryAnchorPane, "Yes");
                GeneParamConfig.getPdf().setIsNeedSummaryPage("Y");
                LogUtils.info("PDFSummary: Yes");
            } else {
                SingleRowAnchorPaneUtils.getText2AndUpdate(summaryAnchorPane, "No");
                GeneParamConfig.getPdf().setIsNeedSummaryPage("N");
                LogUtils.info("PDFSummary: No");
            }
        });


        // =============================  Compress PDF Photo  ===================================
        ArrayList<String> compressChoices = new ArrayList<>();
        compressChoices.add("1");
        compressChoices.add("0.9");
        compressChoices.add("0.8");
        compressChoices.add("0.7");
        compressChoices.add("0.6");
        compressChoices.add("0.5");
        compressChoices.add("0.4");
        compressChoices.add("0.3");
        compressChoices.add("0.2");
        compressChoices.add("0.1");

        AnchorPane compressAnchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("CompressPDFPhoto") + ":",
                SysConfig.getLang("Recommend"), "0.7", compressChoices);
        ChoiceBox compressChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(compressAnchorPane);
        compressChoiceBox.setOnAction((event) -> {
            if (!new SignatureCtrl().checkFunction("PDFSummary")) {
                SingleRowAnchorPaneUtils.getChoiceBox(compressAnchorPane).setValue("0.7");
                return;
            }
            int selectedIndex = compressChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(compressAnchorPane, SysConfig.getLang("OriginalPicture"));
                GeneParamConfig.setPdfPhotoCompressionQuality(1.0f);
            } else if (selectedIndex == 1) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(compressAnchorPane, "0.9");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.9f);
            } else if (selectedIndex == 2) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(compressAnchorPane, "0.8");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.8f);
            } else if (selectedIndex == 3) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(compressAnchorPane, SysConfig.getLang("Recommend"));
                GeneParamConfig.setPdfPhotoCompressionQuality(0.7f);
            } else if (selectedIndex == 4) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(compressAnchorPane, "0.6");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.6f);
            } else if (selectedIndex == 5) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(compressAnchorPane, "0.5");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.5f);
            } else if (selectedIndex == 6) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(compressAnchorPane, "0.4");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.4f);
            } else if (selectedIndex == 7) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(compressAnchorPane, "0.3");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.3f);
            } else if (selectedIndex == 8) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(compressAnchorPane, "0.2");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.2f);
            } else if (selectedIndex == 9) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(compressAnchorPane, "0.1");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.1f);
            } else {
                // default
                SingleRowAnchorPaneUtils.getText2AndUpdate(compressAnchorPane, SysConfig.getLang("Recommend"));
                GeneParamConfig.setPdfPhotoCompressionQuality(0.7f);
            }

            LogUtils.info("CompressPDFPhoto: " + GeneParamConfig.getPdfPhotoCompressionQuality());
        });


        // =============================  Photo Mark  ===================================
        ArrayList<String> markChoices = new ArrayList<>();
        markChoices.add("Yes");
        markChoices.add("No");
        AnchorPane pdfPhotoMarkAnchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("PhotoMark") + ":",
                "No", "No", markChoices);
        ChoiceBox pdfPhotoMarkChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(pdfPhotoMarkAnchorPane);
        pdfPhotoMarkChoiceBox.setOnAction((event) -> {
            if (!new SignatureCtrl().checkFunction("PhotoMark")) {
                SingleRowAnchorPaneUtils.getChoiceBox(pdfPhotoMarkAnchorPane).setValue("No");
                return;
            }
            int selectedIndex = pdfPhotoMarkChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(pdfPhotoMarkAnchorPane, "Yes");
                GeneParamConfig.setIsNeedPhotoMark(true);
                CacheData.refreshGeneConfig("pdfPhotoMarkAnchorPane");
            } else {
                SingleRowAnchorPaneUtils.getText2AndUpdate(pdfPhotoMarkAnchorPane, "No");
                GeneParamConfig.setIsNeedPhotoMark(false);
            }

            LogUtils.info("PhotoMark: " + GeneParamConfig.isIsNeedPhotoMark());
        });


        // =============================  Key  ===================================
        String keyText = getKeyText();
        AnchorPane keyAnchorPane = SingleRowAnchorPaneUtils.getTextTextFieldButton(
                SysConfig.getLang("Key") + ":", keyText, SysConfig.getLang("EnterKey"));
        HBox keyTextHBox = SingleRowAnchorPaneUtils.getTextHBox(keyAnchorPane);
        keyTextHBox.setStyle("-fx-background-color: #84bf96");
        TextField ketTextField = SingleRowAnchorPaneUtils.getTextField(keyAnchorPane);

        ketTextField.setDisable(true);


        Button button = SingleRowAnchorPaneUtils.getButton(keyAnchorPane);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TextInputDialog inputDialog = new TextInputDialog("");
                inputDialog.setResizable(true);
                inputDialog.setWidth(800);
                inputDialog.setHeight(400);

                inputDialog.setTitle(SysConfig.getLang("InputText"));
                inputDialog.setHeaderText(SysConfig.getLang("PleaseEnterKey"));
                inputDialog.setContentText(SysConfig.getLang("Key") + ": ");
                Optional<String> stringOptional = inputDialog.showAndWait();
                String value = null;
                if (stringOptional.isPresent()) {
                    value = stringOptional.get();
                } else {
                    return;
                }
                try {
                    SignatureCtrl signatureCtrl = new SignatureCtrl();
                    boolean checkKey = signatureCtrl.checkKey(value);
                    if (checkKey) {
                        Alert warning = new Alert(Alert.AlertType.CONFIRMATION);
                        warning.setTitle("CONFIRMATION");
                        warning.setContentText(SysConfig.getLang("ChangeKey"));
                        Optional<ButtonType> buttonType = warning.showAndWait();
                        if (buttonType.isPresent()) {
                            if (buttonType.get() == ButtonType.OK) {
                                new SignatureCtrl().clearKey();
                                // 保存key
                                LocalFileUtils.save2Path(value.getBytes(StandardCharsets.UTF_8), SysConfig.KEY_CACHE_PATH, "key.txt");
                                GeneParamConfig.setIsAppHasKey(true);
                                ketTextField.setText(value);
                                LogUtils.info("user ChangeKey: " + value);
                            }
                        }
                    } else {
                        Alert warning = new Alert(Alert.AlertType.WARNING);
                        warning.setTitle("Warning");
                        warning.setContentText(SysConfig.getLang("ReplaceNewKey", "key", value));
                        Optional<ButtonType> buttonType = warning.showAndWait();
                        if (buttonType.isPresent()) {
                            if (buttonType.get() == ButtonType.CANCEL) {
                                LogUtils.info("user cancel ReplaceNewKey");
                                return;
                            }
                            LogUtils.info("The user is sure to use the wrong key");
                        }

                        GeneParamConfig.setIsAppHasKey(false);
                        ketTextField.setText(getKeyText());

                        new SignatureCtrl().clearKey();
                        LocalFileUtils.save2Path(value.getBytes(StandardCharsets.UTF_8), SysConfig.KEY_CACHE_PATH, "key.txt");
                    }

                    CacheData.refreshStatus();
                } catch (Exception e) {
                    Alert warning = new Alert(Alert.AlertType.ERROR);
                    warning.setTitle("ERROR");
                    warning.setContentText(e.getMessage());
                    warning.showAndWait();
                }
            }
        });


        // =============================  is full cover pdf ===================================
        ArrayList<String> fullCoverChoices = new ArrayList<>();
        fullCoverChoices.add("Yes");
        fullCoverChoices.add("No");
        AnchorPane fullCoverAnchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("PictureFillPage") + ":",
                "No", "No", fullCoverChoices);
        ChoiceBox fullCoverChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(fullCoverAnchorPane);
        fullCoverChoiceBox.setOnAction((event) -> {
            if (!new SignatureCtrl().checkFunction("PictureFillPage")) {
                SingleRowAnchorPaneUtils.getChoiceBox(fullCoverAnchorPane).setValue("No");
                return;
            }
            int selectedIndex = fullCoverChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(fullCoverAnchorPane, "Yes");
                GeneParamConfig.getPdf().setIsFullCover("Y");

                // 需要同步更新是否需要图片描述的设置
                SingleRowAnchorPaneUtils.getText2AndUpdate(pdfPhotoMarkAnchorPane, "No");
                GeneParamConfig.setIsNeedPhotoMark(false);
            } else {
                SingleRowAnchorPaneUtils.getText2AndUpdate(fullCoverAnchorPane, "No");
                GeneParamConfig.getPdf().setIsFullCover("N");
            }

            LogUtils.info("PictureFillPage: " + GeneParamConfig.getPdf().getIsFullCover());
        });


        // =============================  PDF Layout  ===================================
        ArrayList<String> layoutChoices = new ArrayList<>();
        layoutChoices.add("4 X 2");
        layoutChoices.add("4 X 1");
        layoutChoices.add("2 X 1");
        layoutChoices.add("8 X 4");
        layoutChoices.add("1 X 1");
        layoutChoices.add("1 X 2");

        AnchorPane layoutAnchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("PDFLayout") + ":",
                "4 " + SysConfig.getLang("rows") + " X 2 " + SysConfig.getLang("columns"), "4 X 2", layoutChoices);
        ChoiceBox layoutChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(layoutAnchorPane);
        layoutChoiceBox.setOnAction((event) -> {
            if (!new SignatureCtrl().checkFunction("PDFLayout")) {
                SingleRowAnchorPaneUtils.getChoiceBox(layoutAnchorPane).setValue("4 X 2");
                return;
            }
            int selectedIndex = layoutChoiceBox.getSelectionModel().getSelectedIndex();
            GeneParamConfig geneParamConfig = GeneParamConfig.instance();
            if (selectedIndex == 0) {
                geneParamConfig.setRow(4);
                geneParamConfig.setColumn(2);
                SingleRowAnchorPaneUtils.getText2AndUpdate(layoutAnchorPane,
                        "4 " + SysConfig.getLang("rows") + " X 2 " + SysConfig.getLang("columns"));
            } else if (selectedIndex == 1) {
                geneParamConfig.setRow(4);
                geneParamConfig.setColumn(1);
                SingleRowAnchorPaneUtils.getText2AndUpdate(layoutAnchorPane,
                        "4 " + SysConfig.getLang("rows") + " X 1 " + SysConfig.getLang("columns"));
            } else if (selectedIndex == 2) {
                geneParamConfig.setRow(2);
                geneParamConfig.setColumn(1);
                SingleRowAnchorPaneUtils.getText2AndUpdate(layoutAnchorPane,
                        "2 " + SysConfig.getLang("rows") + " X 1 " + SysConfig.getLang("columns"));
            } else if (selectedIndex == 3) {
                geneParamConfig.setRow(8);
                geneParamConfig.setColumn(4);
                SingleRowAnchorPaneUtils.getText2AndUpdate(layoutAnchorPane,
                        "8 " + SysConfig.getLang("rows") + " X 4 " + SysConfig.getLang("columns"));
            } else if (selectedIndex == 4) {
                geneParamConfig.setRow(1);
                geneParamConfig.setColumn(1);
                SingleRowAnchorPaneUtils.getText2AndUpdate(layoutAnchorPane,
                        "1 " + SysConfig.getLang("rows") + " X 1 " + SysConfig.getLang("columns"));
            } else if (selectedIndex == 5) {
                geneParamConfig.setRow(1);
                geneParamConfig.setColumn(2);
                SingleRowAnchorPaneUtils.getText2AndUpdate(layoutAnchorPane,
                        "1 " + SysConfig.getLang("rows") + " X 2 " + SysConfig.getLang("columns"));
            } else {
                // 默认
                geneParamConfig.setRow(4);
                geneParamConfig.setColumn(2);
                SingleRowAnchorPaneUtils.getText2AndUpdate(layoutAnchorPane,
                        "4 " + SysConfig.getLang("rows") + " X 2 " + SysConfig.getLang("columns"));
            }

            CacheData.refreshGeneConfig("PDF Layout");
            LogUtils.info("PDFLayout: " + GeneParamConfig.instance().getRow() + " X " + GeneParamConfig.instance().getColumn());
        });


        // =============================  Sort  ===================================
        ArrayList<String> sortChoices = new ArrayList<>();
        sortChoices.add(SysConfig.getLang("DateASC"));
        sortChoices.add(SysConfig.getLang("DateDESC"));
        sortChoices.add(SysConfig.getLang("NameASC"));
        sortChoices.add(SysConfig.getLang("NameDESC"));
        sortChoices.add(SysConfig.getLang("Default"));

        AnchorPane sortByAnchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("PhotoSortBy") + ":",
                SysConfig.getLang("DateASC"), SysConfig.getLang("DateASC"), sortChoices);
        ChoiceBox sortByChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(sortByAnchorPane);
        sortByChoiceBox.setOnAction((event) -> {
            if (!new SignatureCtrl().checkFunction("PhotoSortBy")) {
                SingleRowAnchorPaneUtils.getChoiceBox(sortByAnchorPane).setValue("DateASC");
                return;
            }
            int selectedIndex = sortByChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                GeneParamConfig.sortPhotosBy = "Date";
                GeneParamConfig.sortPhotosOrderBy = "ASC";
                SingleRowAnchorPaneUtils.getText2AndUpdate(sortByAnchorPane, SysConfig.getLang("DateASC"));
            } else if (selectedIndex == 1) {
                GeneParamConfig.sortPhotosBy = "Date";
                GeneParamConfig.sortPhotosOrderBy = "DESC";
                SingleRowAnchorPaneUtils.getText2AndUpdate(sortByAnchorPane, SysConfig.getLang("DateDESC"));
            } else if (selectedIndex == 2) {
                GeneParamConfig.sortPhotosBy = "Name";
                GeneParamConfig.sortPhotosOrderBy = "ASC";
                SingleRowAnchorPaneUtils.getText2AndUpdate(sortByAnchorPane, SysConfig.getLang("NameASC"));
            } else if (selectedIndex == 3) {
                GeneParamConfig.sortPhotosBy = "Name";
                GeneParamConfig.sortPhotosOrderBy = "DESC";
                SingleRowAnchorPaneUtils.getText2AndUpdate(sortByAnchorPane, SysConfig.getLang("NameDESC"));
            } else {
                GeneParamConfig.sortPhotosBy = "";
                GeneParamConfig.sortPhotosOrderBy = "";
                SingleRowAnchorPaneUtils.getText2AndUpdate(sortByAnchorPane, SysConfig.getLang("Default"));
            }

            CacheData.refreshGeneConfig("Photos Sort By");
            LogUtils.info("Photos Sort By: " + GeneParamConfig.getSortPhotosBy());
        });


        VBox vb = new VBox();
        vb.getChildren().addAll(title, pathAnchorPane, previewAnchorPane, coverAnchorPane, summaryAnchorPane,
                pdfPhotoMarkAnchorPane, layoutAnchorPane, fullCoverAnchorPane, sortByAnchorPane, compressAnchorPane, keyAnchorPane);
        vb.setSpacing(0);

        HBox hBoxConfig = new HBox(vb);
        return hBoxConfig;
    }

    private String getKeyText() {
        String keyText = "";
        SignatureCtrl signatureCtrl = new SignatureCtrl();
        String keyIfHave = signatureCtrl.getKeyIfHave();
        if (GeneParamConfig.isIsAppHasKey() && !"".equalsIgnoreCase(keyIfHave)) {
            keyText = keyIfHave;
        } else {
            if ("".equalsIgnoreCase(keyIfHave)) {
                keyText = SysConfig.getLang("GetKey") + " -->  " + GeneParamConfig.getKeyInfoURL();
            }
        }

        return keyText;
    }

}
