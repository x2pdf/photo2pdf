package com.logan.ctrl.helppage.experfunc.zip;

import com.logan.config.SysConfig;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;


public class ZIPSettingDataHBox {

    public static HBox getDataBox() {
        Text text = new Text(SysConfig.getLang("ZIPFunc"));
        text.setStyle("-fx-font-weight:bold");
        Line line = new Line(0, 0, 2160, 0);
        VBox title = new VBox(line, text);




        VBox vb = new VBox();
        vb.getChildren().addAll(title);
        vb.setSpacing(2);
        // 关键：设置子节点居中
        vb.setAlignment(Pos.CENTER);

        HBox hBoxConfig = new HBox(vb);
        // 关键：让 HBox 宽度跟随 VBox 拉伸
        hBoxConfig.setMaxWidth(Double.MAX_VALUE);
        return hBoxConfig;
    }


}
