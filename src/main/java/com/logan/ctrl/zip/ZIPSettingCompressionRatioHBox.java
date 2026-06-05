package com.logan.ctrl.zip;

import com.logan.config.SysConfig;
import com.logan.hbox.BaseHBox;
import com.logan.utils.LogUtils;
import com.logan.utils.SingleRowAnchorPaneUtils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.ArrayList;


public class ZIPSettingCompressionRatioHBox extends BaseHBox {
    @Override
    public String getHBoxCode() {
        return "ZIPSettingCompressionRatioHBox";
    }

    @Override
    public AnchorPane initPane() {
        ArrayList<String> choices = new ArrayList<>();
        choices.add(SysConfig.getLang("NO_COMPRESSION"));
        choices.add(SysConfig.getLang("FAST"));
        choices.add(SysConfig.getLang("NORMAL"));
        choices.add(SysConfig.getLang("MAXIMUM"));
        choices.add(SysConfig.getLang("ULTRA"));

        anchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("CompressionLevel") + ":",
                SysConfig.getLang("Recommend"), SysConfig.getLang("NORMAL"), choices);
        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {
        ChoiceBox compressChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(anchorPane);
        compressChoiceBox.setOnAction((event) -> {
            int selectedIndex = compressChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, SysConfig.getLang("NO_COMPRESSION"));
                ZIPConfig.setCompressionLevel("NO_COMPRESSION");
            } else if (selectedIndex == 1) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, SysConfig.getLang("FAST"));
                ZIPConfig.setCompressionLevel("FAST");
            } else if (selectedIndex == 2) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, SysConfig.getLang("NORMAL"));
                ZIPConfig.setCompressionLevel("NORMAL");
            } else if (selectedIndex == 3) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, SysConfig.getLang("MAXIMUM"));
                ZIPConfig.setCompressionLevel("MAXIMUM");
            } else if (selectedIndex == 4) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, SysConfig.getLang("ULTRA"));
                ZIPConfig.setCompressionLevel("ULTRA");
            } else {
                // default:NO_COMPRESSION
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, SysConfig.getLang("NO_COMPRESSION"));
                ZIPConfig.setCompressionLevel("NO_COMPRESSION");
            }

            LogUtils.info("zip compressionLevel: " + ZIPConfig.compressionLevel);
        });

    }
}
