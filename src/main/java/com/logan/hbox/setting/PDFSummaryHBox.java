package com.logan.hbox.setting;

import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
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
public class PDFSummaryHBox extends BaseHBox {
    @Override
    public String getHBoxCode() {
        return "PDFSummary";
    }

    @Override
    public AnchorPane initPane() {
        ArrayList<String> summaryChoices = new ArrayList<>();
        summaryChoices.add("Yes");
        summaryChoices.add("No");
        anchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("PDFSummary") + ":",
                "Yes", "Yes", summaryChoices);
        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {
        ChoiceBox summaryChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(anchorPane);
        summaryChoiceBox.setOnAction((event) -> {
            int selectedIndex = summaryChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "Yes");
                GeneParamConfig.getPdf().setIsNeedSummaryPage("Y");
                LogUtils.info("PDFSummary: Yes");
            } else {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "No");
                GeneParamConfig.getPdf().setIsNeedSummaryPage("N");
                LogUtils.info("PDFSummary: No");
            }
        });
    }
}
