package com.logan.ctrl;

import com.logan.config.CacheData;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.math.BigDecimal;


/**
 * @author Logan Qin
 * @date 2021/12/24 15:54
 */
public class StatusBarCtrl {

    public HBox getStatusInfo() {
        Line line = new Line(0, 0, 640, 0);
        VBox title = new VBox(line);


        Text appStatus = new Text(SysConfig.getLang("AppStatus") + "= ");
        HBox appStatusValue = new HBox(new Text(CacheData.getAppStatus() + ", "));
        appStatusValue.setStyle("-fx-background-color:" + CacheData.getRandomColor() + ";");
        HBox appStatusHBox = new HBox(appStatus, appStatusValue);


        Text photoAmt = new Text(SysConfig.getLang("PhotoAmount") + "=" + CacheData.getPhotosPreviewPath().size() + ", ");
        Text photoLayout = new Text(SysConfig.getLang("Layout") + "= " + GeneParamConfig.instance().getRow() + "X" + GeneParamConfig.instance().getColumn() + ", ");
        Text pdfPageSize = new Text(SysConfig.getLang("PDFPageSize") + "=" + getPageSize());


        HBox statusHBox = new HBox(appStatusHBox, photoAmt, photoLayout, pdfPageSize);
        statusHBox.setSpacing(6.0);

        AnchorPane pdfAnchorPane = new AnchorPane(statusHBox);

        VBox vb = new VBox();
        vb.getChildren().addAll(title, pdfAnchorPane);
        vb.setSpacing(2);

        HBox hBoxConfig = new HBox(vb);
        hBoxConfig.setStyle("-fx-background-color:#B6B6B4");
        return hBoxConfig;
    }

    private int getPageSize() {
        int columnSet = GeneParamConfig.instance().getColumn();
        int rowSet = GeneParamConfig.instance().getRow();
        int amtPerPage = columnSet * rowSet;

        int size = CacheData.getPhotosPreviewPath().size();
        return new BigDecimal(size).divide(new BigDecimal(amtPerPage), 0, BigDecimal.ROUND_UP).intValue();
    }

}
