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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * author: Logan.qin
 * date: 2022/8/19
 */
public class PictureFillPageHBox extends BaseHBox {

    @Override
    public String getHBoxCode() {
        return "PictureFillPage";
    }

    @Override
    public AnchorPane initPane() {
        ArrayList<String> fullCoverChoices = new ArrayList<>();
        fullCoverChoices.add("Yes");
        fullCoverChoices.add("No");
        anchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("PictureFillPage") + ":",
                "No", "No", fullCoverChoices);
        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {
        ChoiceBox fullCoverChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(anchorPane);
        fullCoverChoiceBox.setOnAction((event) -> {
            if (!new SignatureCtrl().checkFunction("PictureFillPage")) {
                SingleRowAnchorPaneUtils.getChoiceBox(anchorPane).setValue("No");
                return;
            }
            int selectedIndex = fullCoverChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == 0) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "Yes");
                GeneParamConfig.getPdf().setIsFullCover("Y");

                // 需要同步更新是否需要图片描述的设置
                if (CacheData.hboxMap.containsKey("PhotoMark")) {
                    AnchorPane photoMark = CacheData.hboxMap.get("PhotoMark").getAnchorPane();
                    // todo 还需要优化，下拉框的选项同步更新
                    SingleRowAnchorPaneUtils.getText2AndUpdate(photoMark, "No");
                }

                GeneParamConfig.setIsNeedPhotoMark(false);
            } else {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "No");
                GeneParamConfig.getPdf().setIsFullCover("N");
            }

            LogUtils.info("PictureFillPage: " + GeneParamConfig.getPdf().getIsFullCover());
        });
    }
}
