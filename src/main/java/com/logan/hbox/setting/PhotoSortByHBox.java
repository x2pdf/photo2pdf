package com.logan.hbox.setting;

import com.logan.config.CacheData;
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
public class PhotoSortByHBox extends BaseHBox {
    @Override
    public String getHBoxCode() {
        return "PhotoSortBy";
    }

    @Override
    public AnchorPane initPane() {
        ArrayList<String> sortChoices = new ArrayList<>();
        sortChoices.add(SysConfig.getLang("DateASC"));
        sortChoices.add(SysConfig.getLang("DateDESC"));
        sortChoices.add(SysConfig.getLang("NameASC"));
        sortChoices.add(SysConfig.getLang("NameDESC"));
        sortChoices.add(SysConfig.getLang("Default"));

        anchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("PhotoSortBy") + ":",
                SysConfig.getLang("DateASC"), SysConfig.getLang("DateASC"), sortChoices);
        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {
        ChoiceBox sortByChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(anchorPane);
        sortByChoiceBox.setOnAction((event) -> {
            int selectedIndex = sortByChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                GeneParamConfig.sortPhotosBy = "Date";
                GeneParamConfig.sortPhotosOrderBy = "ASC";
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, SysConfig.getLang("DateASC"));
            } else if (selectedIndex == 1) {
                GeneParamConfig.sortPhotosBy = "Date";
                GeneParamConfig.sortPhotosOrderBy = "DESC";
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, SysConfig.getLang("DateDESC"));
            } else if (selectedIndex == 2) {
                GeneParamConfig.sortPhotosBy = "Name";
                GeneParamConfig.sortPhotosOrderBy = "ASC";
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, SysConfig.getLang("NameASC"));
            } else if (selectedIndex == 3) {
                GeneParamConfig.sortPhotosBy = "Name";
                GeneParamConfig.sortPhotosOrderBy = "DESC";
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, SysConfig.getLang("NameDESC"));
            } else {
                GeneParamConfig.sortPhotosBy = "";
                GeneParamConfig.sortPhotosOrderBy = "";
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, SysConfig.getLang("Default"));
            }

            CacheData.refreshGeneConfig("Photos Sort By");
            LogUtils.info("Photos Sort By: " + GeneParamConfig.getSortPhotosBy());
        });
    }
}
