package com.logan.ctrl.gene;

import com.logan.config.SysConfig;
import com.logan.hbox.BaseHBox;
import com.logan.utils.LogUtils;
import com.logan.utils.SingleRowAnchorPaneUtils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.ArrayList;


public class T2ImageTitleFontSizeHBox extends BaseHBox {
    @Override
    public String getHBoxCode() {
        return "T2ImageTitleFontSizeHBox";
    }

    @Override
    public AnchorPane initPane() {
        ArrayList<String> choices = new ArrayList<>();
        choices.add("20");
        choices.add("26");
        choices.add("30");
        choices.add("36");
        choices.add("40");
        choices.add("46");
        choices.add("50");
        choices.add("56");
        choices.add("60");
        choices.add("66");
        choices.add("70");
        choices.add("76");
        choices.add("80");
        choices.add("86");
        choices.add("90");
        choices.add("96");

        anchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("TextToImgTitleFontSize") + ":",
                SysConfig.getLang("Recommend"), "50", choices);
        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {
        ChoiceBox compressChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(anchorPane);
        compressChoiceBox.setOnAction((event) -> {
            int selectedIndex = compressChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "20");
                TextToImageConfig.titleFontSize = 20;
            } else if (selectedIndex == 1) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "26");
                TextToImageConfig.titleFontSize = 26;
            } else if (selectedIndex == 2) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "30");
                TextToImageConfig.titleFontSize = 30;
            } else if (selectedIndex == 3) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "36");
                TextToImageConfig.titleFontSize = 36;
            } else if (selectedIndex == 4) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "40");
                TextToImageConfig.titleFontSize = 40;
            } else if (selectedIndex == 5) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "46");
                TextToImageConfig.titleFontSize = 46;
            } else if (selectedIndex == 6) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "50");
                TextToImageConfig.titleFontSize = 50;
            } else if (selectedIndex == 7) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "56");
                TextToImageConfig.titleFontSize = 56;
            } else if (selectedIndex == 8) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "60");
                TextToImageConfig.titleFontSize = 60;
            } else if (selectedIndex == 9) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "66");
                TextToImageConfig.titleFontSize = 66;
            } else if (selectedIndex == 10) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "70");
                TextToImageConfig.titleFontSize = 70;
            } else if (selectedIndex == 11) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "76");
                TextToImageConfig.titleFontSize = 76;
            } else if (selectedIndex == 12) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "80");
                TextToImageConfig.titleFontSize = 80;
            } else if (selectedIndex == 13) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "86");
                TextToImageConfig.titleFontSize = 86;
            } else if (selectedIndex == 14) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "90");
                TextToImageConfig.titleFontSize = 90;
            } else if (selectedIndex == 15) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "96");
                TextToImageConfig.titleFontSize = 96;
            } else {
                // default
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "50");
                TextToImageConfig.titleFontSize = 50;
            }

            LogUtils.info("T2ImageTitleFontSizeHBox: " + TextToImageConfig.titleFontSize);
        });

    }
}
