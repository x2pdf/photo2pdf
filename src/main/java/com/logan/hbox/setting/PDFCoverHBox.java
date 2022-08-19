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
public class PDFCoverHBox extends BaseHBox {
    @Override
    public String getHBoxCode() {
        return "PDFCover";
    }

    @Override
    public AnchorPane initPane() {
        ArrayList<String> coverChoices = new ArrayList<>();
        coverChoices.add("Yes");
        coverChoices.add("No");
        anchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("PDFCover") + ":",
                "Yes", "Yes", coverChoices);

        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {
        ChoiceBox coverChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(anchorPane);
        coverChoiceBox.setOnAction((event) -> {
            if (!new SignatureCtrl().checkFunction("PDFCover")) {
                SingleRowAnchorPaneUtils.getChoiceBox(anchorPane).setValue("Yes");
                return;
            }
            int selectedIndex = coverChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "Yes");
                GeneParamConfig.getPdf().setIsNeedCoverPage("Y");
                LogUtils.info("PDFCover: Yes");
            } else {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "No");
                GeneParamConfig.getPdf().setIsNeedCoverPage("N");
                LogUtils.info("PDFCover: No");
            }
        });
    }
}
