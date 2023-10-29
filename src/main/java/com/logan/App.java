package com.logan;

import com.logan.config.CacheData;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.ctrl.*;
import com.logan.utils.LogUtils;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * JavaFX App
 */

/*
 预览图片太多时会出问题的解决方案
-Dprism.order=sw -Xmx4000m -Xmn100m
-Dprism.poolstats=true
-Dprism.maxvram=500m
-Dprism.order=sw
-Djavafx.animation.fullspeed=true

Tag 23.01
 */
public class App extends Application {
    private final static String statement =
            "Please use it for learning purposes only.  --author Logan Qin\n" +
                    "Thank you for your understanding and cooperation.\n" +
                    "All Rights Reserved.\n";

    private static Scene scene;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            LogUtils.info("\n\n***** statement *****\n" + statement);
            CacheData.isAppRunning = true;
            initConfig();
            initStage(stage);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    CacheData.clearAllView();
                    SysConfig.asyncPool.shutdown();
                    LogUtils.appStatus();
                    System.gc();
                }
            });
            postStart();
        } catch (Exception e) {
            LogUtils.error("initConfig exception: " + e.toString());
        } catch (Error error) {
            LogUtils.error("App Error. error info:" + error);
            Alert warning = new Alert(Alert.AlertType.ERROR);
            warning.setTitle("ERROR");
            warning.setContentText("The program runs wrong, sorry!");
            warning.showAndWait();
        }
    }

    public void initConfig() {
        try {
            SysConfig.instance();
            CacheData.instance();
            GeneParamConfig.instance();
            new SignatureCtrl().initKeyInfo();
        } catch (Exception e) {
            LogUtils.error("initConfig exception: " + e.toString());
        }
    }

    public void postStart() {
        SysConfig.asyncPool.execute(() -> {
            InitSource initSource = new InitSource();
            initSource.init();
        });
    }

    public void initStage(Stage stage) {
        TabPane tabPane = new TabPane();
        scene = new Scene(tabPane);

        AnchorPane settingsAnchorPane = getSettingTab(stage);
        AnchorPane homepageAnchorPane = getHomeTab();
        AnchorPane helpAnchorPane = getHelpTab();

        // 组合成分页页面
        Tab tab1 = new Tab(SysConfig.getLang("Homepage"), homepageAnchorPane);
        tab1.setClosable(false);
        tab1.setStyle("-fx-pref-width: 80;");
        Tab tab2 = new Tab(SysConfig.getLang("Settings"), settingsAnchorPane);
        tab2.setClosable(false);
        tab2.setStyle("-fx-pref-width: 80;");
        Tab tab3 = new Tab(SysConfig.getLang("Help"), helpAnchorPane);
        tab3.setClosable(false);
        tab3.setStyle("-fx-pref-width: 40;");

        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        tabPane.getTabs().add(tab3);

        stage.setScene(scene);
        stage.setMinWidth(300);
        stage.setMinHeight(300);
        stage.setTitle("photo2pdf");

        stage.getIcons().add(new Image("photo2pdf_icon.png"));
        stage.show();
    }


    private AnchorPane getSettingTab(Stage stage) {
        AnchorPane settingsAnchorPane = new AnchorPane();
        settingsAnchorPane.setPrefSize(SysConfig.STAGE_WIDTH, SysConfig.STAGE_HEIGHT);
        SettingsPaneCtrl settingsPaneCtrl = new SettingsPaneCtrl();
        HBox hBoxConfig = settingsPaneCtrl.getBoxConfig(stage);
        hBoxConfig.setAlignment(Pos.TOP_CENTER);

        VBox settingsVBox = new VBox();
        CacheData.settingsVBox = settingsVBox;
        settingsVBox.getChildren().addAll(hBoxConfig);
        settingsVBox.setSpacing(10);
        settingsVBox.setPadding(new Insets(SysConfig.STAGE_MARGIN_DEFAULT, 0, SysConfig.STAGE_MARGIN_DEFAULT, 0));
        settingsVBox.setAlignment(Pos.BASELINE_CENTER);
        settingsAnchorPane.getChildren().add(settingsVBox);
        AnchorPane.setTopAnchor(settingsVBox, SysConfig.STAGE_MARGIN_DEFAULT);
        AnchorPane.setLeftAnchor(settingsVBox, SysConfig.STAGE_MARGIN_DEFAULT);
        AnchorPane.setRightAnchor(settingsVBox, SysConfig.STAGE_MARGIN_DEFAULT);
        AnchorPane.setBottomAnchor(settingsVBox, SysConfig.STAGE_MARGIN_DEFAULT);

        return settingsAnchorPane;
    }


    private AnchorPane getHomeTab() {
        AnchorPane homepageAnchorPane = new AnchorPane();
        homepageAnchorPane.setPrefSize(SysConfig.STAGE_WIDTH, SysConfig.STAGE_HEIGHT);

        // 2.1功能区域
        FuncPaneCtrl funcPaneCtrl = new FuncPaneCtrl();
        HBox hBoxFunc = funcPaneCtrl.getBoxFunc();
        hBoxFunc.setAlignment(Pos.TOP_CENTER);

        // 2.2 pdf封面信息区域
        PDFInfoCtrl pdfInfoCtrl = new PDFInfoCtrl();
        HBox pdfInfoHBox = pdfInfoCtrl.getBoxPDFInfo();
        pdfInfoHBox.setAlignment(Pos.TOP_CENTER);

        // 2.3 状态栏显示
        StatusBarCtrl statusBarCtrl = new StatusBarCtrl();
        HBox statusInfoHBox = statusBarCtrl.getStatusInfo();
        CacheData.statusHBox = statusInfoHBox;
        statusInfoHBox.setAlignment(Pos.BOTTOM_CENTER);

        // 2.4 预览图片区域
        PreviewPaneCtrl previewPaneCtrl = new PreviewPaneCtrl();
        HBox previewActionHBox = previewPaneCtrl.getBoxPreviewAction();
        previewActionHBox.setAlignment(Pos.TOP_CENTER);
        HBox previewHBox = previewPaneCtrl.getBoxPreview();
        previewHBox.setAlignment(Pos.TOP_CENTER);

        VBox homepageVBox = new VBox();
        homepageVBox.getChildren().addAll(pdfInfoHBox, hBoxFunc, previewActionHBox, previewHBox);
        homepageVBox.setSpacing(10);
        homepageVBox.setPadding(new Insets(SysConfig.STAGE_MARGIN_DEFAULT, 0, SysConfig.STAGE_MARGIN_DEFAULT, 0));
        homepageVBox.setAlignment(Pos.BASELINE_CENTER);

        AnchorPane.setTopAnchor(homepageVBox, SysConfig.STAGE_MARGIN_DEFAULT);
        AnchorPane.setLeftAnchor(homepageVBox, SysConfig.STAGE_MARGIN_DEFAULT);
        AnchorPane.setRightAnchor(homepageVBox, SysConfig.STAGE_MARGIN_DEFAULT);
        AnchorPane.setBottomAnchor(homepageVBox, 22.0); // 不要和状态栏重叠

        AnchorPane.setLeftAnchor(statusInfoHBox, SysConfig.STAGE_MARGIN_DEFAULT);
        AnchorPane.setRightAnchor(statusInfoHBox, SysConfig.STAGE_MARGIN_DEFAULT);
        AnchorPane.setBottomAnchor(statusInfoHBox, SysConfig.STAGE_MARGIN_DEFAULT);
        homepageAnchorPane.getChildren().addAll(homepageVBox, statusInfoHBox);

        return homepageAnchorPane;
    }


    private AnchorPane getHelpTab() {
        HelpCtrl helpCtrl = new HelpCtrl();
        HBox boxHelp = helpCtrl.getBoxHelp();
        boxHelp.setAlignment(Pos.TOP_CENTER);

        VBox helpVBox = new VBox();
        helpVBox.getChildren().addAll(boxHelp);
        helpVBox.setSpacing(10);
        helpVBox.setPadding(new Insets(SysConfig.STAGE_MARGIN_DEFAULT, 0, SysConfig.STAGE_MARGIN_DEFAULT, 0));
        helpVBox.setAlignment(Pos.BASELINE_CENTER);
        AnchorPane.setTopAnchor(helpVBox, SysConfig.STAGE_MARGIN_DEFAULT);
        AnchorPane.setLeftAnchor(helpVBox, SysConfig.STAGE_MARGIN_DEFAULT);
        AnchorPane.setRightAnchor(helpVBox, SysConfig.STAGE_MARGIN_DEFAULT);
        AnchorPane.setBottomAnchor(helpVBox, SysConfig.STAGE_MARGIN_DEFAULT);

        AnchorPane helpAnchorPane = new AnchorPane();
        helpAnchorPane.setPrefSize(SysConfig.STAGE_WIDTH, SysConfig.STAGE_HEIGHT);
        helpAnchorPane.getChildren().add(helpVBox);

        return helpAnchorPane;
    }

}
