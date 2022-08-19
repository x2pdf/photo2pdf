package com.logan.hbox.setting;

import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.ctrl.SignatureCtrl;
import com.logan.hbox.BaseHBox;
import com.logan.utils.LogUtils;
import com.logan.utils.SingleRowAnchorPaneUtils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * author: Logan.qin
 * date: 2022/8/19
 */
public class CompressPDFPhotoHBox extends BaseHBox {
    @Override
    public String getHBoxCode() {
        return "CompressPDFPhoto";
    }

    @Override
    public AnchorPane initPane() {
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

        anchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("CompressPDFPhoto") + ":",
                SysConfig.getLang("Recommend"), "0.7", compressChoices);
        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {
        ChoiceBox compressChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(anchorPane);
        compressChoiceBox.setOnAction((event) -> {
            if (!new SignatureCtrl().checkFunction("PDFSummary")) {
                SingleRowAnchorPaneUtils.getChoiceBox(anchorPane).setValue("0.7");
                return;
            }
            int selectedIndex = compressChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, SysConfig.getLang("OriginalPicture"));
                GeneParamConfig.setPdfPhotoCompressionQuality(1.0f);
            } else if (selectedIndex == 1) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "0.9");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.9f);
            } else if (selectedIndex == 2) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "0.8");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.8f);
            } else if (selectedIndex == 3) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, SysConfig.getLang("Recommend"));
                GeneParamConfig.setPdfPhotoCompressionQuality(0.7f);
            } else if (selectedIndex == 4) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "0.6");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.6f);
            } else if (selectedIndex == 5) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "0.5");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.5f);
            } else if (selectedIndex == 6) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "0.4");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.4f);
            } else if (selectedIndex == 7) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "0.3");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.3f);
            } else if (selectedIndex == 8) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "0.2");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.2f);
            } else if (selectedIndex == 9) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "0.1");
                GeneParamConfig.setPdfPhotoCompressionQuality(0.1f);
            } else {
                // default
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, SysConfig.getLang("Recommend"));
                GeneParamConfig.setPdfPhotoCompressionQuality(0.7f);
            }

            LogUtils.info("CompressPDFPhoto: " + GeneParamConfig.getPdfPhotoCompressionQuality());
        });

    }
}
