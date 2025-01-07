package com.logan.hbox.setting;

import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.ctrl.FileChooserCtrl;
import com.logan.hbox.BaseHBox;
import com.logan.utils.LogUtils;
import com.logan.utils.SingleRowAnchorPaneUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;

/**
 * author: Logan.qin
 * date: 2022/8/19
 */
public class PDFSavePathHBox extends BaseHBox {


    @Override
    public String getHBoxCode() {
        return "PDFSavePath";
    }

    @Override
    public AnchorPane initPane() {
        anchorPane = SingleRowAnchorPaneUtils.getTextTextFieldButton(SysConfig.getLang("PDFSavePath") + ":",
                GeneParamConfig.getPdfSavePath(), SysConfig.getLang("ChangePath"));
        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {
        Button pathButton = SingleRowAnchorPaneUtils.getButton(anchorPane);
        pathButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooserCtrl fileChooserCtrl = new FileChooserCtrl();
                String savePath = fileChooserCtrl.chooseFilePath(stage);
                if (savePath != null) {
                    savePath = savePath + File.separator;
                    LogUtils.info("savePath refresh: " + savePath);
                    GeneParamConfig.setPdfSavePath(savePath);
                    SingleRowAnchorPaneUtils.getTextFieldAndUpdate(anchorPane, savePath);
                }
            }
        });
    }
}
