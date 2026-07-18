package com.logan.ctrl.helppage.experfunc.zip;

import com.logan.config.SysConfig;
import com.logan.hbox.BaseHBox;
import com.logan.utils.LogUtils;
import com.logan.utils.SingleRowAnchorPaneUtils;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 为zip设定预设密码功能。
 * 密码是明文保存在本地，以用户体验优先做了这个功能。
 * 基于即使有钥匙也要知道锁在哪里才可以解密的原则，也就是说知道密码但是不知道加密的zip文件在哪里也没有用。
 * 肯定有一定概率导致zip被解密的情况，但那已经不是程序本身的责任了。
 *
 */
public class ZIPPasswordPresetHBox extends BaseHBox {
    @Override
    public String getHBoxCode() {
        return "ZIPPasswordPresetHBox";
    }

    @Override
    public AnchorPane initPane() {
        ArrayList<String> choices = new ArrayList<>();
        // 初始化用户保存的预设密码
        HashMap<String, String> zipPasswordPreset = ZIPPasswordPresetConfig.getZIPPasswordPreset();
        for (Map.Entry<String, String> stringStringEntry : zipPasswordPreset.entrySet()) {
            choices.add(stringStringEntry.getKey());
        }
        choices.add(SysConfig.getLang("AddPasswordPreset"));

        anchorPane = SingleRowAnchorPaneUtils.getTextTextChoiceBox(SysConfig.getLang("PasswordPreset") + ":",
                "", "", choices);
        return anchorPane;
    }


    @Override
    public void setAction(Stage stage) {
        setAction(stage, null);
    }

    public void setAction(Stage stage, ZIPPasswordInput pwdComponent) {
        ChoiceBox zipPasswordPresetChoiceBox = SingleRowAnchorPaneUtils.getChoiceBox(anchorPane);
        
        addContextMenu(zipPasswordPresetChoiceBox, pwdComponent);
        
        zipPasswordPresetChoiceBox.setOnAction((event) -> {
            int selectedIndex = zipPasswordPresetChoiceBox.getSelectionModel().getSelectedIndex();
            String selectedValue = zipPasswordPresetChoiceBox.getSelectionModel().getSelectedItem().toString();
            if (SysConfig.getLang("AddPasswordPreset").equals(selectedValue) ) {
                PasswordInfo passwordInfo = showAddPasswordDialog();
                if (passwordInfo != null) {
                    zipPasswordPresetChoiceBox.getItems().remove(SysConfig.getLang("AddPasswordPreset"));

                    zipPasswordPresetChoiceBox.getItems().add(passwordInfo.name);
                    zipPasswordPresetChoiceBox.getItems().add(SysConfig.getLang("AddPasswordPreset"));
                    zipPasswordPresetChoiceBox.getSelectionModel().select(passwordInfo.name);

                    SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, passwordInfo.name);
                    
                    // 将密码填充到 ZIPPasswordInput 组件
                    if (pwdComponent != null) {
                        pwdComponent.setPassword(passwordInfo.password);
                    }

                    // 缓存到内存中以及保存到本地文件
                    ZIPPasswordPresetConfig.addZIPPasswordPreset(passwordInfo.name, passwordInfo.password);
                } else {
                    zipPasswordPresetChoiceBox.getSelectionModel().clearSelection();
                    // 将密码填充到 ZIPPasswordInput 组件
                    if (pwdComponent != null) {
                        pwdComponent.setPassword(null);
                    }
                }
            } else if (selectedIndex >= 0 && selectedIndex < zipPasswordPresetChoiceBox.getItems().size() - 1) {
                SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, selectedValue);

                 if (pwdComponent != null) {
                     String zipPasswordPreset = ZIPPasswordPresetConfig.getZIPPasswordPreset(selectedValue);
                     if (zipPasswordPreset != null){
                         pwdComponent.setPassword(zipPasswordPreset);
                         LogUtils.info("选择已有密码: " + selectedValue);
                     }
                 }
            } else {
                LogUtils.info("选择不使用zip密码: " + selectedValue);
            }

        });

    }

    private void addContextMenu(ChoiceBox choiceBox, ZIPPasswordInput pwdComponent) {
        choiceBox.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                String selectedItem = (String) choiceBox.getSelectionModel().getSelectedItem();
                
                if (selectedItem != null && !SysConfig.getLang("AddPasswordPreset").equals(selectedItem)) {
                    ContextMenu contextMenu = new ContextMenu();
                    
                    MenuItem removeItem = new MenuItem(SysConfig.getLang("RemovePasswordPreset"));
                    removeItem.setStyle("-fx-text-fill: red;");
                    removeItem.setOnAction(e -> {
                        showRemoveConfirmationDialog(choiceBox, pwdComponent, selectedItem);
                    });
                    
                    contextMenu.getItems().add(removeItem);
                    contextMenu.show(choiceBox, event.getScreenX(), event.getScreenY());
                }
            }
        });
    }

    private void showRemoveConfirmationDialog(ChoiceBox choiceBox, ZIPPasswordInput pwdComponent, String passwordName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(SysConfig.getLang("ConfirmRemove"));
        alert.setHeaderText(SysConfig.getLang("RemovePasswordPreset"));
        alert.setContentText(SysConfig.getLang("ConfirmRemoveMessage"));
        
        ButtonType confirmButton = new ButtonType(SysConfig.getLang("Yes"));
        ButtonType cancelButton = new ButtonType(SysConfig.getLang("No"));
        
        alert.getButtonTypes().setAll(confirmButton, cancelButton);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                removePasswordPreset(choiceBox, pwdComponent, passwordName);
            }
        });
    }

    private void removePasswordPreset(ChoiceBox choiceBox, ZIPPasswordInput pwdComponent, String passwordName) {
        ZIPPasswordPresetConfig.removeZIPPasswordPreset(passwordName);
        
        choiceBox.getItems().remove(passwordName);
        
        int selectedIndex = choiceBox.getSelectionModel().getSelectedIndex();
        String selectedValue = (String) choiceBox.getSelectionModel().getSelectedItem();
        
        if (passwordName.equals(selectedValue)) {
            choiceBox.getSelectionModel().clearSelection();
            SingleRowAnchorPaneUtils.getText2AndUpdate(anchorPane, "");
            
            if (pwdComponent != null) {
                pwdComponent.setPassword(null);
            }
            
            LogUtils.info("已移除预设密码: " + passwordName + "，并清空当前选择的密码");
        } else {
            LogUtils.info("已移除预设密码: " + passwordName);
        }
    }

    private PasswordInfo showAddPasswordDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(SysConfig.getLang("InputText"));
        dialog.setHeaderText(SysConfig.getLang("AddPasswordPreset"));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText(SysConfig.getLang("PasswordName"));

        PasswordField pwdField = new PasswordField();
        pwdField.setPromptText(SysConfig.getLang("EnterZipPassword"));

        PasswordField pwdConfirmField = new PasswordField();
        pwdConfirmField.setPromptText(SysConfig.getLang("EnterZipPasswordAgain"));

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(
            new Label(SysConfig.getLang("PasswordName") + ":"),
            nameField,
            new Label(SysConfig.getLang("EnterZipPassword") + ":"),
            pwdField,
            new Label(SysConfig.getLang("EnterZipPasswordAgain") + ":"),
            pwdConfirmField
        );

        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().setPrefWidth(600);

        final PasswordInfo[] result = {null};

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String name = nameField.getText();
            String pwd = pwdField.getText();
            String pwdConfirm = pwdConfirmField.getText();

            if (name == null || name.trim().isEmpty()) {
                showAlert(SysConfig.getLang("Waring"), SysConfig.getLang("PasswordNameCannotEmpty"));
                event.consume();
                return;
            }

            if (pwd == null || pwd.length() < 12) {
                showAlert(SysConfig.getLang("Waring"), SysConfig.getLang("PasswordWarning"));
                event.consume();
                return;
            }

            if (!pwd.equals(pwdConfirm)) {
                showAlert(SysConfig.getLang("Waring"), SysConfig.getLang("PasswordNotEqual"));
                event.consume();
                return;
            }

            result[0] = new PasswordInfo(name.trim(), pwd);
            dialog.setResult(ButtonType.OK);
        });

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setOnAction(event -> {
            result[0] = null;
        });

        dialog.showAndWait();
        return result[0];
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class PasswordInfo {
        String name;
        String password;

        PasswordInfo(String name, String password) {
            this.name = name;
            this.password = password;
        }
    }
}
