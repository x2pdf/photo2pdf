package com.logan.ctrl;

import com.logan.config.CacheData;
import com.logan.config.SysConfig;
import com.logan.utils.JSONUtils;
import com.logan.utils.LocalFileUtils;
import com.logan.utils.LogUtils;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Logan Qin
 * @date 2021/12/22 19:03
 */
public class TestCtrl {

    public VBox getVMInfo() {
        Text text = new Text("Dev Info");
        text.setStyle("-fx-font-weight:bold");
        Line line = new Line(0, 0, 640, 0);
        VBox title = new VBox(line, text);


        HashMap<String, Object> map = new HashMap<>();
        long maxMem = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        long freeMem = Runtime.getRuntime().freeMemory() / 1024 / 1024;
        long totalMem = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long usedMem = maxMem - freeMem;

        map.put("MaxMemory", maxMem + "MB");
        map.put("UsedMemory", usedMem + "MB");
        map.put("FreeMemory", freeMem + "MB");
        map.put("TotalMemory", totalMem + "MB");

        LogUtils.appStatus();
        List<String> logLines = LocalFileUtils.readFileLine(SysConfig.LOG_CACHE_PATH + SysConfig.LOG_FILE_NAME);
        map.put("LogLines", logLines);

        String json = JSONUtils.toJson(map);
        TextArea path = new TextArea(json);

        VBox vb = new VBox();
        vb.getChildren().addAll(title, path);
        vb.setSpacing(2);

        AnchorPane vmInfoAnchorPane = new AnchorPane(vb);
        AnchorPane.setLeftAnchor(vb, 2.0);
        AnchorPane.setRightAnchor(vb, 2.0);

        VBox vBox = new VBox(vmInfoAnchorPane);
        return vBox;
    }


    public void devModeDetect(String privateContent) {
        try {
            if (privateContent == null) {
                return;
            }
            if (privateContent.toLowerCase().startsWith("**##devmode=true")) {
                SysConfig.IS_DEV_MODE = true;
                LogUtils.info("IS_DEV_MODE: true");
            }
            if (privateContent.toLowerCase().startsWith("**##devmode=false")) {
                SysConfig.IS_DEV_MODE = false;
                LogUtils.info("IS_DEV_MODE: false");
            }

            if (SysConfig.IS_DEV_MODE) {
                LogUtils.info("IS_DEV_MODE: true");
                if (CacheData.vmInfo == null) {
                    TestCtrl testCtrl = new TestCtrl();
                    VBox vmInfo = testCtrl.getVMInfo();
                    CacheData.settingsVBox.getChildren().add(vmInfo);

                    CacheData.vmInfo = vmInfo;
                } else {
                    CacheData.vmInfo.setVisible(true);
                    ObservableList<Node> children = CacheData.settingsVBox.getChildren();

                    ArrayList<Node> nodes = new ArrayList<>();
                    for (Node child : children) {
                        if (child.equals(CacheData.vmInfo)) {
                            continue;
                        }
                        nodes.add(child);
                    }

                    CacheData.settingsVBox.getChildren().clear();
                    CacheData.settingsVBox.getChildren().addAll(nodes);
                }
            }

            // 配置线程池的核心数
            if (privateContent.toLowerCase().startsWith("**##threadpoolcore=")) {
                LogUtils.info("threadPoolCore change: " + privateContent);
                String s = privateContent.toLowerCase();
                String[] split = s.split("=");
                if (split.length > 1) {
                    String coreSize = split[1];
                    int corePoolSize = Integer.parseInt(coreSize);
                    int corePoolSizeOld = SysConfig.asyncPool.getCorePoolSize();
                    SysConfig.asyncPool.setCorePoolSize(corePoolSize);
                    LogUtils.info("threadPool core Size: " + corePoolSizeOld + " --> " + corePoolSize);
                }
            }

        } catch (Exception e) {
            LogUtils.error("devModeDetect Exception: " + e);
        }
    }

}
