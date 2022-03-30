package com.logan.ctrl;

import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.utils.LogUtils;
import com.logan.utils.SingleRowAnchorPaneUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Logan Qin
 * @date 2021/12/22 15:59
 */
public class PDFInfoCtrl {

    public HBox getBoxPDFInfo() {
        Text text = new Text(SysConfig.getLang("CoverSetting"));
        text.setStyle("-fx-font-weight:bold");
        Line line = new Line(0, 0, 640, 0);
        VBox title = new VBox(line, text);

        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fmtTime = dtf.format(time);

        AnchorPane titleAnchorPane = SingleRowAnchorPaneUtils.getTextTextFieldButton(
                SysConfig.getLang("Title") + ":", fmtTime, null);
        TextField pdfTitleTextField = SingleRowAnchorPaneUtils.getTextField(titleAnchorPane);
        pdfTitleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            GeneParamConfig.getPdf().setTitle(newValue);
        });

        AnchorPane subTitleAnchorPane = SingleRowAnchorPaneUtils.getTextTextFieldButton(
                SysConfig.getLang("SubTitle") + ":", "", null);
        TextField pdfSubTitleTextField = SingleRowAnchorPaneUtils.getTextField(subTitleAnchorPane);
        pdfSubTitleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            GeneParamConfig.getPdf().setSubTitle(newValue);
        });


        AnchorPane descAnchorPane = SingleRowAnchorPaneUtils.getTextTextFieldButton(
                SysConfig.getLang("Description") + ":", "", null);
        TextField pdfDescTextField = SingleRowAnchorPaneUtils.getTextField(descAnchorPane);
        pdfDescTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            GeneParamConfig.getPdf().setDesc(newValue);

            if (newValue != null) {
                TestCtrl testCtrl = new TestCtrl();
                testCtrl.devModeDetect(newValue);
            }
        });


        AnchorPane coverAnchorPane = SingleRowAnchorPaneUtils.getTextTextFieldButton(
                SysConfig.getLang("CoverImage") + ":", "", SysConfig.getLang("ChangePath"));
        Button coverButton = SingleRowAnchorPaneUtils.getButton(coverAnchorPane);
        coverButton.setStyle("-fx-font-size: 0.8em;");
        coverButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooserCtrl fileChooserCtrl = new FileChooserCtrl();
                File coverPhotoFile = fileChooserCtrl.selectSinglePhoto();

                if (coverPhotoFile == null) {
                    LogUtils.info("cover Path no select photo");
                    return;
                }
                String savePath = coverPhotoFile.getAbsolutePath();
                LogUtils.info("cover Path refresh: " + savePath);
                GeneParamConfig.getPdf().setCoverPhoto(savePath);
                SingleRowAnchorPaneUtils.getTextFieldAndUpdate(coverAnchorPane, savePath);
            }
        });


        VBox vb = new VBox();
        vb.getChildren().addAll(title, titleAnchorPane, subTitleAnchorPane, descAnchorPane, coverAnchorPane);
        vb.setSpacing(0);

        HBox hBoxConfig = new HBox(vb);
        return hBoxConfig;
    }


}
