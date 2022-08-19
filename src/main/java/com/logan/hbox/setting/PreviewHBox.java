package com.logan.hbox.setting;

import com.logan.config.CacheData;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.ctrl.SignatureCtrl;
import com.logan.ctrl.ViewGridPaneCtrl;
import com.logan.hbox.BaseHBox;
import com.logan.utils.LogUtils;
import com.logan.utils.SingleRowAnchorPaneUtils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * author: Logan.qin
 * date: 2022/8/19
 */
public class PreviewHBox extends BaseHBox {

    @Override
    public String getHBoxCode() {
        return "Preview";
    }

    @Override
    public AnchorPane initPane() {
        ArrayList<String> previewChoices = new ArrayList<>();
        previewChoices.add("Yes");
        previewChoices.add("No");
        anchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("Preview") + ":",
                "Yes", "Yes", previewChoices);

        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {
        ChoiceBox previewChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(anchorPane);
        previewChoiceBox.setOnAction((event) -> {
            if (!new SignatureCtrl().checkFunction("Preview")) {
                SingleRowAnchorPaneUtils.getChoiceBox(anchorPane).setValue("Yes");
                return;
            }
            int selectedIndex = previewChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "Yes");
                GeneParamConfig.setIsPreviewPDFLayout(true);
                CacheData.refreshGeneConfig("need preview");
                LogUtils.info("Preview: Yes");
            } else {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "No");
                GeneParamConfig.setIsPreviewPDFLayout(false);
                // 清除预览
                CacheData.clearPreviewPhoto();

                GridPane gridPane = CacheData.gridPane;
                ArrayList<String> photosPath = CacheData.getPhotosPreviewPath();
                ViewGridPaneCtrl viewGridPaneCtrl = new ViewGridPaneCtrl();
                viewGridPaneCtrl.photosViewPane(gridPane, photosPath);
                LogUtils.info("Preview: No");
            }
        });
    }
}
