package com.logan.ctrl;

import com.logan.config.CacheData;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.utils.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Logan Qin
 * @date 2021/12/22 15:27
 */
public class FuncPaneCtrl {

    public HBox getBoxFunc() {
        Text text = new Text(SysConfig.getLang("FunctionalArea"));
        text.setStyle("-fx-font-weight:bold");
        Line line = new Line(0, 0, 640, 0);
        VBox title = new VBox(line, text);

        Button selectButton = new Button(SysConfig.getLang("Step1Select"));
        styleButton(selectButton);
        selectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // 每次重新选择图片或者不断添加时，需要重置
                CacheData.setAppStatus(SysConfig.PICK_FILE);
                CacheData.refreshStatus();
                // 1. 用户选择的图片原始路径
                FileChooserCtrl fileChooserCtrl = new FileChooserCtrl();
                ArrayList<String> selectPhotos = fileChooserCtrl.selectPhotos();
                if (selectPhotos.size() == 0) {
                    return;
                }
                // 过滤并处理其中的 heic 图片
                ArrayList<String> selectPhotosFilter = HeicConvertUtils.heicPhotoFilterMultiThread(selectPhotos);

                // 2. 将选择的图片同步到其他缓存的list
                CacheData.syncSelectPhoto2OtherList(selectPhotosFilter);

                // 3. 刷新预览显示
                GridPane gridPane = CacheData.gridPane;
                // 先用空数据刷新一次
                ViewGridPaneCtrl viewGridPaneCtrl = new ViewGridPaneCtrl();
                viewGridPaneCtrl.photosViewPane(gridPane, new ArrayList<>());
                CacheData.setAppStatus(SysConfig.RENDER_PREVIEW);
                CacheData.refreshStatus();

                // 不用预览的情况
                if (!GeneParamConfig.isIsPreviewPDFLayout()) {
                    return;
                }

                // 从预览list获取预览图片
                ArrayList<String> photosPath = CacheData.getPhotosPreviewPath();
                if (photosPath.size() >= 30) {
                    int compressPhotoSeconds = PhotoUtils.getCompressPhotoSeconds(photosPath.size());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setContentText(SysConfig.getLang("WaitingAndPatient", "spend", String.valueOf(compressPhotoSeconds)));
                    alert.showAndWait();
                }

                // 将真实预览的图片刷新页面
                ViewGridPaneCtrl viewGridPaneCtrl2 = new ViewGridPaneCtrl();
                viewGridPaneCtrl2.photosViewPane(gridPane, photosPath);

                CacheData.setAppStatus(SysConfig.DEFAULT);
                CacheData.refreshStatus();
                LogUtils.info("select photos end");
            }
        });

        Button geneButton = new Button(SysConfig.getLang("Step2Generate"));
        styleButton(geneButton);
        geneButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CacheData.refreshStatus();
                if (CacheData.getPhotosPreviewPath().size() == 0) {
                    // case: no file
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setContentText(SysConfig.getLang("NoFileSelected"));
                    alert.showAndWait();
                } else {
                    if (!new SignatureCtrl().checkFunction("Step2Generate")) {
                        return;
                    }

                    CacheData.setAppStatus(SysConfig.GENERATE_FILE);
                    CacheData.refreshStatus();

                    int size = CacheData.getPhotosPreviewPath().size();
                    if (size >= 50) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Info");
                        alert.setContentText(SysConfig.getLang("TooManyFile"));
                        Optional<ButtonType> buttonType = alert.showAndWait();
                        if (buttonType.isPresent()) {
                            if (buttonType.get() == ButtonType.CANCEL) {
                                LogUtils.info("user cancel Generate pdf");
                                return;
                            }
                            LogUtils.info("user sure Generate pdf");
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    GenePDFCtrl genePDFCtrl = new GenePDFCtrl();
                    String savePath = genePDFCtrl.genePdfAndSave();
                    LocalFileUtils.deleteFolder(LocalFileUtils.mkTempDir("previewPhotos"));

                    CacheData.setAppStatus(SysConfig.DEFAULT);
                    CacheData.refreshStatus();

                    LocalFileUtils.append2Log("pdf save path: " + savePath);
                    LogUtils.info("pdf save path:" + savePath);
                    AlertUtils.openExplorer(GeneParamConfig.getPdfSavePath());
                }
            }

        });


        Button addMergePdfButton = new Button(SysConfig.getLang("SelectPDF"));
        styleButton(addMergePdfButton);
        addMergePdfButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooserCtrl fileChooserCtrl = new FileChooserCtrl();
                ArrayList<String> pdfs = fileChooserCtrl.selectPDFs();

                // 缓存起来
                CacheData.setMergePdfPath(pdfs);
                ViewGridPaneCtrl viewGridPaneCtrl = new ViewGridPaneCtrl();

                // 首先清除图片的预览画面（如果有图片预览时）
                CacheData.clearAllView();
                // 先用空数据刷新一次
                CacheData.gridPane = viewGridPaneCtrl.pdfListPane(CacheData.gridPane, new ArrayList<>());
                CacheData.gridPane = viewGridPaneCtrl.pdfListPane(CacheData.gridPane, pdfs);
            }
        });

        Button mergeButton = new Button(SysConfig.getLang("MergePDF"));
        styleButton(mergeButton);
        mergeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!new SignatureCtrl().checkFunction("MergePDF")) {
                    return;
                }

                ArrayList<String> pdfs = CacheData.getMergePdfPath();
                if (pdfs == null || pdfs.size() == 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setContentText(SysConfig.getLang("NoFileSelected"));
                    alert.showAndWait();
                    return;
                }

                String saveTo = GeneParamConfig.getPdfSavePath()
                        + "photo2pdf_merge_" + DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss").format(LocalDateTime.now()) + ".pdf";
                String savePath = PDFBoxUtils.merge2One(pdfs, saveTo);

                LogUtils.info("Merge PDF end");
                CacheData.refreshStatus();

                AlertUtils.openExplorer(GeneParamConfig.getPdfSavePath());
            }
        });


        Button encryptButton = new Button(SysConfig.getLang("EncryptPDF"));
        styleButton(encryptButton);
        encryptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!new SignatureCtrl().checkFunction("EncryptPDF")) {
                    return;
                }
                FileChooserCtrl fileChooserCtrl = new FileChooserCtrl();
                File pdf = fileChooserCtrl.selectSinglePdf();
                if (pdf == null || pdf.length() == 0) {
                    return;
                }

                String pdfPath = pdf.getAbsolutePath();
                String fileFullName = pdfPath.substring(pdfPath.lastIndexOf(File.separator) + 1);
                String password = AlertUtils.getInputPassword(fileFullName);
                if (password == null) {
                    LogUtils.info("encryptButton inputDialog cancel");
                    return;
                }

                String fmtTime = TimeUtils.getNow_yyyy_MM_dd_HH_mm_ss();
                String passwordSubStr = password.charAt(0) + password.substring(password.length() - 1);

                String savePath = GeneParamConfig.getPdfSavePath();
                String pdfFileName = "photo2pdf_encrypt_" + fmtTime + "_" + passwordSubStr + ".pdf";
                String saveAbsPath = PDFBoxUtils.encryptPDF(pdf, password, savePath, pdfFileName);

                AlertUtils.openExplorer(savePath);
            }
        });


        Button extractButton = new Button(SysConfig.getLang("ExtractPhoto"));
        styleButton(extractButton);
        extractButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!new SignatureCtrl().checkFunction("ExtractPhoto")) {
                    return;
                }
                // 选择pdf文件
                FileChooserCtrl fileChooserCtrl = new FileChooserCtrl();
                File pdf = fileChooserCtrl.selectSinglePdf();
                if (pdf == null || pdf.length() == 0) {
                    return;
                }

                // 10 MB
                if (pdf.length() >= 10000000) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Info");
                    alert.setContentText(SysConfig.getLang("TooManyFile"));
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.isPresent()) {
                        if (buttonType.get() == ButtonType.CANCEL) {
                            LogUtils.info("user cancel ExtractPhoto");
                            return;
                        }
                        LogUtils.info("user sure ExtractPhoto");
                        try {
                            CacheData.gc();
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                CacheData.setAppStatus(SysConfig.EXTRACTING_FILE);
                CacheData.refreshStatus();

                String pdfPath = pdf.getAbsolutePath();
                String fileFullName = pdfPath.substring(pdfPath.lastIndexOf(File.separator) + 1);
                String msg = SysConfig.getLang("EnterPasswordIfHave");
                String password = AlertUtils.getInputPasswordByOne(fileFullName, msg);
                if (password == null) {
                    LogUtils.info("extract Images inputDialog cancel");
                    return;
                }

                LocalDateTime time = LocalDateTime.now();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH_mm_ss");
                String fmtTime = dtf.format(time);
                String savePath = GeneParamConfig.getPdfSavePath() + "photo2pdf_extract_" + fmtTime + File.separator;
                try {
                    PDFBoxUtils.extractImages(pdf, password, savePath);
                } catch (InvalidPasswordException e) {
                    LogUtils.error("extract Images from pdf password error.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(SysConfig.getLang("Error"));
                    alert.setContentText(SysConfig.getLang("PasswordError"));
                    alert.showAndWait();
                    return;
                } catch (IOException e) {
                    LogUtils.error("extract Images from pdf IO error.");
                }


                CacheData.setAppStatus(SysConfig.DEFAULT);
                CacheData.refreshStatus();
                AlertUtils.openExplorer(savePath);
            }
        });

        Text gene = new Text(SysConfig.getLang("Gene"));
        HBox genePdfHBox = new HBox(selectButton, geneButton);
        genePdfHBox.setSpacing(4);
        VBox genePdfVBox = new VBox(gene, genePdfHBox);
        styleVBox(genePdfVBox);

        Text merge = new Text(SysConfig.getLang("Merge"));
        HBox mergePdfHBox = new HBox(addMergePdfButton, mergeButton);
        mergePdfHBox.setSpacing(4);
        VBox mergePdfVBox = new VBox(merge, mergePdfHBox);
        styleVBox(mergePdfVBox);

        Text other = new Text(SysConfig.getLang("Others"));
        HBox otherPdfHBox = new HBox(encryptButton, extractButton);
        otherPdfHBox.setSpacing(4);
        VBox otherPdfVBox = new VBox(other, otherPdfHBox);
        styleVBox(otherPdfVBox);

        HBox buttonHBox = new HBox(genePdfVBox, mergePdfVBox, otherPdfVBox);

        buttonHBox.setSpacing(12);

        VBox vb = new VBox();
        vb.getChildren().addAll(title, buttonHBox);
        vb.setSpacing(4);

        HBox hBoxConfig = new HBox(vb);
        return hBoxConfig;
    }


    private void styleButton(Button button) {
        button.setMinWidth(100);
        button.setMinHeight(30.00);
        button.setStyle("-fx-font-size: 0.9em;");
    }

    private void styleVBox(VBox vBox) {
        vBox.setSpacing(4);
        vBox.setStyle("-fx-background-color: #f2eada");  // #d3d7d4
    }

}
