package com.logan.hbox.setting;

import com.logan.config.CacheData;
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
public class PDFLayoutHBox extends BaseHBox {
    @Override
    public String getHBoxCode() {
        return "PDFLayout";
    }

    @Override
    public AnchorPane initPane() {
        ArrayList<String> layoutChoices = new ArrayList<>();
        layoutChoices.add("4 X 2");
        layoutChoices.add("4 X 1");
        layoutChoices.add("2 X 1");
        layoutChoices.add("8 X 4");
        layoutChoices.add("1 X 1");
        layoutChoices.add("1 X 2");

        anchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("PDFLayout") + ":",
                "4 " + SysConfig.getLang("rows") + " X 2 " + SysConfig.getLang("columns"), "4 X 2", layoutChoices);
        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {
        ChoiceBox layoutChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(anchorPane);
        layoutChoiceBox.setOnAction((event) -> {
            if (!new SignatureCtrl().checkFunction("PDFLayout")) {
                SingleRowAnchorPaneUtils.getChoiceBox(anchorPane).setValue("4 X 2");
                return;
            }
            int selectedIndex = layoutChoiceBox.getSelectionModel().getSelectedIndex();
            GeneParamConfig geneParamConfig = GeneParamConfig.instance();
            if (selectedIndex == 0) {
                geneParamConfig.setRow(4);
                geneParamConfig.setColumn(2);
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane,
                        "4 " + SysConfig.getLang("rows") + " X 2 " + SysConfig.getLang("columns"));
            } else if (selectedIndex == 1) {
                geneParamConfig.setRow(4);
                geneParamConfig.setColumn(1);
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane,
                        "4 " + SysConfig.getLang("rows") + " X 1 " + SysConfig.getLang("columns"));
            } else if (selectedIndex == 2) {
                geneParamConfig.setRow(2);
                geneParamConfig.setColumn(1);
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane,
                        "2 " + SysConfig.getLang("rows") + " X 1 " + SysConfig.getLang("columns"));
            } else if (selectedIndex == 3) {
                geneParamConfig.setRow(8);
                geneParamConfig.setColumn(4);
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane,
                        "8 " + SysConfig.getLang("rows") + " X 4 " + SysConfig.getLang("columns"));
            } else if (selectedIndex == 4) {
                geneParamConfig.setRow(1);
                geneParamConfig.setColumn(1);
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane,
                        "1 " + SysConfig.getLang("rows") + " X 1 " + SysConfig.getLang("columns"));
            } else if (selectedIndex == 5) {
                geneParamConfig.setRow(1);
                geneParamConfig.setColumn(2);
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane,
                        "1 " + SysConfig.getLang("rows") + " X 2 " + SysConfig.getLang("columns"));
            } else {
                // 默认
                geneParamConfig.setRow(4);
                geneParamConfig.setColumn(2);
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane,
                        "4 " + SysConfig.getLang("rows") + " X 2 " + SysConfig.getLang("columns"));
            }

            CacheData.refreshGeneConfig("PDF Layout");
            LogUtils.info("PDFLayout: " + GeneParamConfig.instance().getRow() + " X " + GeneParamConfig.instance().getColumn());
        });

    }
}
