package com.logan.hbox;

import com.logan.config.CacheData;
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
public class HBoxUtil {

    public static HBox getHBox(String[] businessSeqArr, Stage stage) {
        try {
            VBox vb = new VBox();
            Reflections reflections = new Reflections("com.logan.hbox");
            Set<Class<? extends BaseHBox>> subTypes = reflections.getSubTypesOf(BaseHBox.class);

            // 找到做对应业务的实例化对象
            ArrayList<BaseHBox> hBoxes = new ArrayList<>();
            for (String businessTypeCode : businessSeqArr) {
                System.out.println("businessTypeCode: " + businessTypeCode);
                for (Class<? extends BaseHBox> subType : subTypes) {
                    BaseHBox hBox = subType.newInstance();
                    if (hBox.getHBoxCode().equals(businessTypeCode)) {
                        hBoxes.add(hBox);
                        CacheData.hboxMap.put(businessTypeCode,hBox);
                    }
                }
            }

            // 按顺序处理每一个定义的业务
            for (BaseHBox hBox : hBoxes) {
                AnchorPane anchorPane = hBox.initPane();
                hBox.setAction(stage);
                vb.getChildren().add(anchorPane);
            }

            vb.setSpacing(0);
            HBox hBoxConfig = new HBox(vb);
            return hBoxConfig;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
