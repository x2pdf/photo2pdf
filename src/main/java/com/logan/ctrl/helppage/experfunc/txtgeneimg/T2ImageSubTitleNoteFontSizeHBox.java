package com.logan.ctrl.helppage.experfunc.txtgeneimg;

import com.logan.config.SysConfig;
import com.logan.hbox.BaseHBox;
import com.logan.utils.LogUtils;
import com.logan.utils.SingleRowAnchorPaneUtils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.ArrayList;


public class T2ImageSubTitleNoteFontSizeHBox extends BaseHBox {
    @Override
    public String getHBoxCode() {
        return "T2ImageSubTitleNoteFontSizeHBox";
    }

    @Override
    public AnchorPane initPane() {
        ArrayList<String> choices = new ArrayList<>();
        choices.add("8");
        choices.add("12");
        choices.add("16");
        choices.add("20");
        choices.add("30");
        choices.add("36");
        choices.add("40");


        anchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("TextToImgSubTitleNoteFontSize") + ":",
                SysConfig.getLang("Recommend"), "12", choices);
        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {
        ChoiceBox compressChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(anchorPane);
        compressChoiceBox.setOnAction((event) -> {
            int selectedIndex = compressChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "8");
                TextToImageConfig.subTitleNoteFontSize = 8;
            } else if (selectedIndex == 1) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "12");
                TextToImageConfig.subTitleNoteFontSize = 12;
            } else if (selectedIndex == 2) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "16");
                TextToImageConfig.subTitleNoteFontSize = 16;
            } else if (selectedIndex == 3) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "20");
                TextToImageConfig.subTitleNoteFontSize = 20;
            } else if (selectedIndex == 4) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "30");
                TextToImageConfig.subTitleNoteFontSize = 30;
            } else if (selectedIndex == 5) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "36");
                TextToImageConfig.subTitleNoteFontSize = 36;
            } else if (selectedIndex == 6) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "40");
                TextToImageConfig.subTitleNoteFontSize = 40;
            } else {
                // default
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "12");
                TextToImageConfig.subTitleNoteFontSize = 12;
            }

            LogUtils.info("T2ImageSubTitleNoteFontSizeHBox: " + TextToImageConfig.subTitleNoteFontSize);
        });

    }
}
