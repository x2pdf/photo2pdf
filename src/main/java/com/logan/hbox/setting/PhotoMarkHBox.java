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
public class PhotoMarkHBox extends BaseHBox {
    @Override
    public String getHBoxCode() {
        return "PhotoMark";
    }

    @Override
    public AnchorPane initPane() {
        ArrayList<String> markChoices = new ArrayList<>();
        markChoices.add("Yes");
        markChoices.add("No");
        anchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("PhotoMark") + ":",
                "No", "No", markChoices);
        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {
        ChoiceBox pdfPhotoMarkChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(anchorPane);
        pdfPhotoMarkChoiceBox.setOnAction((event) -> {
            if (!new SignatureCtrl().checkFunction("PhotoMark")) {
                SingleRowAnchorPaneUtils.getChoiceBox(anchorPane).setValue("No");
                return;
            }
            int selectedIndex = pdfPhotoMarkChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "Yes");
                GeneParamConfig.setIsNeedPhotoMark(true);
                CacheData.refreshGeneConfig("anchorPane");
            } else {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "No");
                GeneParamConfig.setIsNeedPhotoMark(false);
            }

            LogUtils.info("PhotoMark: " + GeneParamConfig.isIsNeedPhotoMark());
        });
    }


}
