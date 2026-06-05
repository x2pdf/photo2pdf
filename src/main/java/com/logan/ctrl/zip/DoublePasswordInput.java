package com.logan.ctrl.zip;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class DoublePasswordInput extends HBox {

    private final int pwdMinLength = 4;
    private final PasswordField pwd1 = new PasswordField();
    private final PasswordField pwd2 = new PasswordField();

    private final Text label = new Text("输入加密密码:");
    private final CheckBox enableEncryptCheckBox = new CheckBox("启用加密");

    // 对外暴露状态
    private final BooleanProperty encryptionEnabled = new SimpleBooleanProperty(false);

    private static final String DEFAULT_BORDER =
            "-fx-border-color: #9aa0a6;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 4;" +
                    "-fx-background-color: transparent;" +
                    "-fx-background-radius: 4;";


    public DoublePasswordInput() {
        super(10);
        this.setAlignment(Pos.CENTER_RIGHT);

        HBox.setMargin(label, new Insets(0, 0, 0, 0));
        label.setWrappingWidth(130);
        label.setTextAlignment(TextAlignment.RIGHT);
        HBox labelBox = new HBox(label);
        labelBox.setAlignment(Pos.CENTER_RIGHT);
        labelBox.setStyle("-fx-background-color: #d3d7d4");

        pwd1.setStyle(DEFAULT_BORDER);
        pwd2.setStyle(DEFAULT_BORDER);

        HBox.setMargin(enableEncryptCheckBox, new Insets(0, 0, 0, 0));
        enableEncryptCheckBox.setStyle("-fx-background-color: #d3d7d4;");
        enableEncryptCheckBox.setPrefWidth(100);
        enableEncryptCheckBox.setMinWidth(100);
        enableEncryptCheckBox.setMaxWidth(100);

        pwd1.setPromptText("输入密码(>=16位)");
        pwd2.setPromptText("再次输入密码");

        HBox.setHgrow(pwd1, Priority.ALWAYS);
        HBox.setHgrow(pwd2, Priority.ALWAYS);

        // 密码变化监听
        pwd1.textProperty().addListener((obs, o, n) -> validate());
        pwd2.textProperty().addListener((obs, o, n) -> validate());

        // checkbox 监听
        enableEncryptCheckBox.selectedProperty().addListener((obs, oldV, newV) -> {
            encryptionEnabled.set(newV);
        });

        this.getChildren().addAll(
                labelBox,
                pwd1,
                pwd2,
                enableEncryptCheckBox
        );

        validate();
    }

    private void validate() {
        String p1 = pwd1.getText();
        String p2 = pwd2.getText();

        boolean lengthOk = p1 != null && p1.length() >= pwdMinLength;
        boolean match = p1 != null && p1.equals(p2);

        if (p1 == null || p1.isEmpty() || p2 == null || p2.isEmpty()) {
            clearStyle();
            return;
        }

        if (lengthOk && match) {
            apply(pwd1, Color.rgb(144, 238, 144, 0.35));
            apply(pwd2, Color.rgb(144, 238, 144, 0.35));
        } else {
            apply(pwd1, Color.rgb(255, 160, 160, 0.35));
            apply(pwd2, Color.rgb(255, 160, 160, 0.35));
        }
    }

    private void apply(PasswordField field, Color color) {
        field.setBackground(new Background(
                new BackgroundFill(color, new CornerRadii(4), Insets.EMPTY)
        ));
    }

    private void clearStyle() {
        pwd1.setBackground(Background.EMPTY);
        pwd2.setBackground(Background.EMPTY);
    }

    // ---------------- 对外接口 ----------------

    public String getPassword() {
        return pwd1.getText();
    }

    public boolean isValid() {
        String p1 = pwd1.getText();
        return p1 != null && p1.length() >= pwdMinLength && p1.equals(pwd2.getText());
    }

    // 当前 checkbox 状态
    public boolean isEncryptionEnabled() {
        return encryptionEnabled.get();
    }

    // 可绑定监听
    public BooleanProperty encryptionEnabledProperty() {
        return encryptionEnabled;
    }
}