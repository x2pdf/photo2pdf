package com.logan.hbox;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Set;

/**
 * author: Logan.qin
 * date: 2022/8/19
 */

public abstract class BaseHBox extends HBox {

    public AnchorPane anchorPane;

    public abstract String getHBoxCode();

    public abstract AnchorPane initPane();

    public abstract void setAction(Stage stage);


}
