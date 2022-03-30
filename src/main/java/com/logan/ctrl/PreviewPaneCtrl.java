package com.logan.ctrl;

import com.logan.config.CacheData;
import com.logan.config.SysConfig;
import com.logan.utils.LogUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Logan Qin
 * @date 2021/12/22 15:27
 */
public class PreviewPaneCtrl {

    public HBox getBoxPreviewAction() {
        Text text = new Text(SysConfig.getLang("Preview"));
        text.setStyle("-fx-font-weight:bold");
        Line line = new Line(0, 0, 640, 0);
        VBox title = new VBox(line, text);

        Button clearButton = new Button(SysConfig.getLang("ClearAll"));
        clearButton.setMinWidth(60);
        clearButton.setStyle("-fx-font-size: 0.8em;");
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // 1. 先提示
                CacheData.setAppStatus(SysConfig.CLEAR);
                CacheData.refreshStatus();
                Alert warning = new Alert(Alert.AlertType.CONFIRMATION);
                warning.setTitle("Warning");
                warning.setContentText(SysConfig.getLang("ClearAllPreview"));
                Optional<ButtonType> buttonType = warning.showAndWait();
                if (buttonType.isPresent()) {
                    if (buttonType.get() == ButtonType.CANCEL) {
                        LogUtils.info("user cancel clear all");
                        return;
                    }
                    LogUtils.info("user sure clear all");
                }

                CacheData.clearAllView();
            }
        });


        Button deleteSinglePhotoButton = new Button(SysConfig.getLang("ClearClicked"));
        deleteSinglePhotoButton.setMinWidth(60);
        deleteSinglePhotoButton.setStyle("-fx-font-size: 0.8em;");
        deleteSinglePhotoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String style = deleteSinglePhotoButton.getStyle();
                if (style.contains("-fx-background-color: red")) {
                    deleteSinglePhotoButton.setStyle("-fx-font-size: 0.8em;");
                    CacheData.isClick2RemovePhoto = false;
                    CacheData.isClick2RemovePdf = false;
                    CacheData.setAppStatus(SysConfig.DEFAULT);
                } else {
                    // todo 当预览的是pdf列表时不用提醒
                    Alert warning = new Alert(Alert.AlertType.CONFIRMATION);
                    warning.setTitle("Warning");
                    warning.setContentText(SysConfig.getLang("ClearClickedWarning"));
                    Optional<ButtonType> buttonType = warning.showAndWait();
                    if (buttonType.isPresent()) {
                        if (buttonType.get() == ButtonType.CANCEL) {
                            LogUtils.info("user cancel ClearClicked");
                            return;
                        }
                        LogUtils.info("user sure ClearClicked");
                    }

                    CacheData.setAppStatus(SysConfig.CLEAR);
                    deleteSinglePhotoButton.setStyle("-fx-font-size: 0.8em; -fx-background-color: red");
                    CacheData.isClick2RemovePhoto = true;
                    CacheData.isClick2RemovePdf = true;
                }

                CacheData.refreshStatus();
            }
        });

        HBox previewHBox = new HBox(clearButton, deleteSinglePhotoButton);
        previewHBox.setSpacing(4);

        VBox viewAction = new VBox();
        viewAction.getChildren().addAll(title, previewHBox);

        VBox vb = new VBox();
        vb.getChildren().addAll(viewAction);
        vb.setSpacing(5);
        vb.setAlignment(Pos.CENTER);

        HBox hBoxConfig = new HBox(vb);
        hBoxConfig.setMinWidth(800);

        return hBoxConfig;
    }

    public HBox getBoxPreview() {
        // 获取照片的预览 pane
        ArrayList<String> photosPath = CacheData.getPhotosPreviewPath();
        GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color:#fffef9;");
        ViewGridPaneCtrl viewGridPaneCtrl2 = new ViewGridPaneCtrl();
        CacheData.gridPane = viewGridPaneCtrl2.photosViewPane(gridPane, photosPath);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(CacheData.gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setMinWidth(800);
        scrollPane.setMinHeight(220);

        VBox vb = new VBox();
        vb.getChildren().addAll(scrollPane);
        vb.setSpacing(5);
        vb.setAlignment(Pos.CENTER);

        HBox hBoxConfig = new HBox(vb);
        hBoxConfig.setMinWidth(800);

        return hBoxConfig;
    }

}
