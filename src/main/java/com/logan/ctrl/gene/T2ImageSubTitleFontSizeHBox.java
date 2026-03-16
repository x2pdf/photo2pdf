package com.logan.ctrl.gene;

import com.logan.config.SysConfig;
import com.logan.hbox.BaseHBox;
import com.logan.utils.LogUtils;
import com.logan.utils.SingleRowAnchorPaneUtils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.ArrayList;


public class T2ImageSubTitleFontSizeHBox extends BaseHBox {
    @Override
    public String getHBoxCode() {
        return "T2ImageSubTitleFontSizeHBox";
    }

    @Override
    public AnchorPane initPane() {
        ArrayList<String> choices = new ArrayList<>();
        choices.add("12");
        choices.add("16");
        choices.add("20");
        choices.add("26");
        choices.add("30");
        choices.add("36");
        choices.add("40");
        choices.add("46");
        choices.add("50");
        choices.add("56");

        anchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("TextToImgSubTitleFontSize") + ":",
                SysConfig.getLang("Recommend"), "16", choices);
        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {
        ChoiceBox compressChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(anchorPane);
        compressChoiceBox.setOnAction((event) -> {
            int selectedIndex = compressChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "12");
                TextToImageConfig.subTitleFontSize = 12;
            } else if (selectedIndex == 1) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "16");
                TextToImageConfig.subTitleFontSize = 16;
            } else if (selectedIndex == 2) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "20");
                TextToImageConfig.subTitleFontSize = 20;
            } else if (selectedIndex == 3) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "26");
                TextToImageConfig.subTitleFontSize = 26;
            } else if (selectedIndex == 4) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "30");
                TextToImageConfig.subTitleFontSize = 30;
            } else if (selectedIndex == 5) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "36");
                TextToImageConfig.subTitleFontSize = 36;
            } else if (selectedIndex == 6) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "40");
                TextToImageConfig.subTitleFontSize = 40;
            } else if (selectedIndex == 7) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "46");
                TextToImageConfig.subTitleFontSize = 46;
            } else if (selectedIndex == 8) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "50");
                TextToImageConfig.subTitleFontSize = 50;
            } else if (selectedIndex == 9) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "56");
                TextToImageConfig.subTitleFontSize = 56;
            } else {
                // default
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "16");
                TextToImageConfig.subTitleFontSize = 16;
            }

            LogUtils.info("T2ImageTitleFontSizeHBox: " + TextToImageConfig.subTitleFontSize);
        });

    }
}
