package com.logan.hbox.setting;

import com.logan.config.CacheData;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.ctrl.SignatureCtrl;
import com.logan.hbox.BaseHBox;
import com.logan.utils.LocalFileUtils;
import com.logan.utils.LogUtils;
import com.logan.utils.SingleRowAnchorPaneUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * author: Logan.qin
 * date: 2022/8/19
 */
public class EnterKeyHBox extends BaseHBox {

    @Override
    public String getHBoxCode() {
        return "EnterKey";
    }

    @Override
    public AnchorPane initPane() {
        String keyText = getKeyText();
        anchorPane = SingleRowAnchorPaneUtils.getTextTextFieldButton(
                SysConfig.getLang("Key") + ":", keyText, SysConfig.getLang("EnterKey"));
        HBox keyTextHBox = SingleRowAnchorPaneUtils.getTextHBox(anchorPane);
        keyTextHBox.setStyle("-fx-background-color: #84bf96");
        TextField ketTextField = SingleRowAnchorPaneUtils.getTextField(anchorPane);
        ketTextField.setDisable(true);
        return anchorPane;
    }

    @Override
    public void setAction(Stage stage) {
        Button button = SingleRowAnchorPaneUtils.getButton(anchorPane);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TextInputDialog inputDialog = new TextInputDialog("");
                inputDialog.setResizable(true);
                inputDialog.setWidth(800);
                inputDialog.setHeight(400);
                inputDialog.setTitle(SysConfig.getLang("InputText"));
                inputDialog.setHeaderText(SysConfig.getLang("PleaseEnterKey"));
                inputDialog.setContentText(SysConfig.getLang("Key") + ": ");
                Optional<String> stringOptional = inputDialog.showAndWait();
                String value = null;
                if (stringOptional.isPresent()) {
                    value = stringOptional.get();
                } else {
                    return;
                }
                try {
                    SignatureCtrl signatureCtrl = new SignatureCtrl();
                    boolean checkKey = signatureCtrl.checkKey(value);
                    if (checkKey) {
                        Alert warning = new Alert(Alert.AlertType.CONFIRMATION);
                        warning.setTitle("CONFIRMATION");
                        warning.setContentText(SysConfig.getLang("ChangeKey"));
                        Optional<ButtonType> buttonType = warning.showAndWait();
                        if (buttonType.isPresent()) {
                            if (buttonType.get() == ButtonType.OK) {
                                new SignatureCtrl().clearKey();
                                // 保存 key
                                LocalFileUtils.save2Path(value.getBytes(StandardCharsets.UTF_8), SysConfig.KEY_CACHE_PATH, "key.txt");
                                // key的缓存路径2
                                String KEY_CACHE_PATH2 = GeneParamConfig.getPdfSavePath() + "key" + File.separator;
                                LocalFileUtils.save2Path(value.getBytes(StandardCharsets.UTF_8), KEY_CACHE_PATH2, "key.txt");
                                LocalFileUtils.save2Path(getKeyReadmeText().getBytes(StandardCharsets.UTF_8), KEY_CACHE_PATH2, "readme.txt");
                                GeneParamConfig.setIsAppHasKey(true);
                                TextField ketTextField = SingleRowAnchorPaneUtils.getTextField(anchorPane);
                                ketTextField.setText(value);
                                LogUtils.info("user ChangeKey: " + value);
                            }
                        }
                    } else {
                        Alert warning = new Alert(Alert.AlertType.WARNING);
                        warning.setTitle("Warning");
                        warning.setContentText(SysConfig.getLang("ReplaceNewKey", "key", value));
                        Optional<ButtonType> buttonType = warning.showAndWait();
                        if (buttonType.isPresent()) {
                            if (buttonType.get() == ButtonType.CANCEL) {
                                LogUtils.info("user cancel ReplaceNewKey");
                                return;
                            }
                            LogUtils.info("The user is sure to use the wrong key");
                        }

                        GeneParamConfig.setIsAppHasKey(false);
                        TextField ketTextField = SingleRowAnchorPaneUtils.getTextField(anchorPane);
                        ketTextField.setText(getKeyText());

                        new SignatureCtrl().clearKey();
                        LocalFileUtils.save2Path(value.getBytes(StandardCharsets.UTF_8), SysConfig.KEY_CACHE_PATH, "key.txt");
                        String KEY_CACHE_PATH2 = GeneParamConfig.getPdfSavePath() + "key" + File.separator;
                        LocalFileUtils.save2Path(value.getBytes(StandardCharsets.UTF_8), KEY_CACHE_PATH2, "key.txt");
                        LocalFileUtils.save2Path(getKeyReadmeText().getBytes(StandardCharsets.UTF_8), KEY_CACHE_PATH2, "readme.txt");
                    }

                    CacheData.refreshStatus();
                } catch (Exception e) {
                    Alert warning = new Alert(Alert.AlertType.ERROR);
                    warning.setTitle("ERROR");
                    warning.setContentText(e.getMessage());
                    warning.showAndWait();
                }
            }
        });

    }

    private String getKeyText() {
        String keyText = "";
        SignatureCtrl signatureCtrl = new SignatureCtrl();
        String keyIfHave = signatureCtrl.getKeyIfHave();
        if (GeneParamConfig.isIsAppHasKey() && !"".equalsIgnoreCase(keyIfHave)) {
            keyText = keyIfHave;
        } else {
            if ("".equalsIgnoreCase(keyIfHave)) {
                keyText = SysConfig.getLang("GetKey") + " -->  " + GeneParamConfig.getKeyInfoURL();
            }
        }

        return keyText;
    }

    private String getKeyReadmeText() {
        return "# readme\n"
                + SysConfig.getLang("GetKey") + " -->  " + GeneParamConfig.getKeyInfoURL() + "\n"
                + "\n\n"
                + "All Rights Reserved.\n"
                ;
    }
}
