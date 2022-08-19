package com.logan.hbox.setting;

import com.logan.config.SysConfig;
import com.logan.hbox.BaseHBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * author: Logan.qin
 * date: 2022/8/19
 */
public class TitleHBox extends BaseHBox {
    @Override
    public String getHBoxCode() {
        return "Title";
    }

    @Override
    public AnchorPane initPane() {
        Text text = new Text(SysConfig.getLang("Setting"));
        text.setStyle("-fx-font-weight:bold");
        Line line = new Line(0, 0, 640, 0);
        VBox title = new VBox(line, text);
        anchorPane = new AnchorPane(title);
        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {

    }
}
