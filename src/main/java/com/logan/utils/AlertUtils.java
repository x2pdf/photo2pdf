package com.logan.utils;

import com.logan.config.SysConfig;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * @author Logan Qin
 * @date 2022/1/21 13:40
 */
public class AlertUtils {

    public static void openExplorer(String savePath) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Info");
        alert.setContentText(SysConfig.getLang("FileSavePath") + "\n" + savePath);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.isPresent()) {
            if (buttonType.get() == ButtonType.CANCEL) {
                return;
            }
            try {
//                        Runtime.getRuntime().exec("explorer " + savePath);
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(new File(savePath));
                }
                System.gc();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void error(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Error");
        alert.setContentText(msg);
        Optional<ButtonType> buttonType = alert.showAndWait();
    }

    public static String getInputText(String msg) {

        TextInputDialog inputDialog = new TextInputDialog("");
        inputDialog.setTitle(SysConfig.getLang("InputText"));
        inputDialog.setHeaderText(msg);
        Optional<String> stringOptional = inputDialog.showAndWait();
        if (stringOptional.isPresent()) {
            return stringOptional.get();
        } else {
            LogUtils.info("inputDialog cancel");
        }

        return null;
    }


    // 返回 null 则表示用户取消输入密码
    public static String getInputPassword(String fileFullName) {
        String res = null;
        for (int i = 0; i < 300; i++) {
            String label = SysConfig.getLang("FileYourChoice") + ": " + fileFullName + "\n\n";
            // ref: https://stackoverflow.com/questions/53825323/javafx-textinputdialog-for-password-masking
            javafx.scene.control.Dialog<String> dialog = new javafx.scene.control.Dialog<>();
            dialog.setTitle(SysConfig.getLang("InputText"));
            dialog.setHeaderText(label + SysConfig.getLang("EnterPassword"));
            dialog.setGraphic(new Circle(15, Color.PALEGOLDENROD)); // Custom graphic
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            PasswordField pwd = new PasswordField();
            PasswordField pwd2 = new PasswordField();
            VBox content = new VBox();
            content.setAlignment(Pos.CENTER_LEFT);
            content.setSpacing(4);
            content.getChildren().addAll(new Label(SysConfig.getLang("Password") + ":"), pwd);
            content.getChildren().addAll(new Label(SysConfig.getLang("PasswordAgain") + ":"), pwd2);
            dialog.getDialogPane().setContent(content);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    if (pwd.getText().equals(pwd2.getText())) {
                        return pwd.getText();
                    } else {
                        return "!=";
                    }
                }
                return "null";
            });


            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String password = result.get();
                if (password.equals("null")) {
                    res = null;
                    break;
                }
                if (password.equals("!=")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle(SysConfig.getLang("Waring"));
                    alert.setContentText(SysConfig.getLang("PasswordNotEqual"));
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    LogUtils.info("Password length must be greater than 8 digits!");
                    continue;
                }
                if (password.length() < 8) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle(SysConfig.getLang("Waring"));
                    alert.setContentText(SysConfig.getLang("PasswordWarning"));
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    LogUtils.info("Password length must be greater than 8 digits!");
                    continue;
                }

                res = password;
                break;
            } else {
                res = null;
                break;
            }
        }

        return res;
    }


    // 返回 null 则表示用户取消输入密码
    public static String getInputPasswordByOne(String fileFullName, String msg) {
        String res = null;

        String label = SysConfig.getLang("FileYourChoice") + ": \n" + fileFullName + "\n\n";
        javafx.scene.control.Dialog<String> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle(SysConfig.getLang("InputText"));
        dialog.setHeaderText(label);
        dialog.setGraphic(new Circle(15, Color.PALEGOLDENROD)); // Custom graphic
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField pwd = new PasswordField();
        VBox content = new VBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(4);
        content.getChildren().addAll(new Label(msg), pwd);

        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return pwd.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            res = result.get();
        }

        return res;
    }

}
