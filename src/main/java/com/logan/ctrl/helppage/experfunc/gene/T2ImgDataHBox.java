package com.logan.ctrl.helppage.experfunc.gene;

import com.logan.config.SysConfig;
import com.logan.utils.SingleRowAnchorPaneUtils;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;


public class T2ImgDataHBox {

    public static HBox getDataBox() {
        Text text = new Text(SysConfig.getLang("TextToImgTextInput"));
        text.setStyle("-fx-font-weight:bold");
        Line line = new Line(0, 0, 2160, 0);
        VBox title = new VBox(line, text);

        AnchorPane titleAnchorPane = SingleRowAnchorPaneUtils.getText2ImgTextTextField(
                SysConfig.getLang("TextToImgTitle") + ":", "");
        TextField titleTextField = SingleRowAnchorPaneUtils.getTextField(titleAnchorPane);
        titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            TextToImageDataDTO.title = newValue;
        });

        AnchorPane textAnchorPane = SingleRowAnchorPaneUtils.getText2ImgTextTextArea(
                SysConfig.getLang("TextToImgText") + ":", "");
        TextArea textTextArea = SingleRowAnchorPaneUtils.getTextArea(textAnchorPane);
        textTextArea.setWrapText(true);      // 自动换行
        textTextArea.setPrefRowCount(16);    // 可视行数
        textTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("textTextArea newValue: " + newValue);
            TextToImageDataDTO.text = newValue;
        });

        AnchorPane subTitleAnchorPane = SingleRowAnchorPaneUtils.getText2ImgTextTextField(
                SysConfig.getLang("TextToImgSubTitle") + ":", "");
        TextField subTitleTextField = SingleRowAnchorPaneUtils.getTextField(subTitleAnchorPane);
        subTitleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            TextToImageDataDTO.subTitle = newValue;
        });

        AnchorPane subTitleNoteAnchorPane = SingleRowAnchorPaneUtils.getText2ImgTextTextField(
                SysConfig.getLang("TextToImgSubTitleNote") + ":", "");
        TextField subTitleNoteTextField = SingleRowAnchorPaneUtils.getTextField(subTitleNoteAnchorPane);
        subTitleNoteTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            TextToImageDataDTO.subTitleNote = newValue;
        });


        VBox vb = new VBox();
        vb.getChildren().addAll(title, titleAnchorPane, textAnchorPane, subTitleAnchorPane, subTitleNoteAnchorPane);
        vb.setSpacing(2);
        // 关键：设置子节点居中
        vb.setAlignment(Pos.CENTER);

        HBox hBoxConfig = new HBox(vb);
        // 关键：让 HBox 宽度跟随 VBox 拉伸
        hBoxConfig.setMaxWidth(Double.MAX_VALUE);
        return hBoxConfig;
    }


}
