package com.logan.ctrl.settings;

import com.logan.config.CacheData;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.hbox.HBoxUtil;
import com.logan.utils.LocalFileUtils;
import com.logan.utils.LogUtils;
import com.logan.utils.SingleRowAnchorPaneUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Logan Qin
 * @date 2021/12/22 15:18
 */
public class SettingsPaneCtrl {

    public HBox getBoxConfig(Stage stage) {
        Text text = new Text(SysConfig.getLang("Setting"));
        text.setStyle("-fx-font-weight:bold");
        Line line = new Line(0, 0, 640, 0);
        VBox title = new VBox(line, text);
        // business order
        String[] businessSeqArr = {"Title", "PDFSavePath", "Preview", "PDFCover", "PDFSummary",
                "PhotoMark", "PDFLayout", "PictureFillPage", "PhotoSortBy",
                "CompressPDFPhoto"};
        HBox hBox = HBoxUtil.getHBox(businessSeqArr, stage);
        return hBox;
    }

}
