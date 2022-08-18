package com.logan.ctrl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.model.Key;
import com.logan.utils.LocalFileUtils;
import com.logan.utils.LogUtils;
import com.logan.utils.PGPUtils;
import com.logan.utils.TimeUtils;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Logan Qin
 * @date 2021/12/29 13:36
 */

public class SignatureCtrl {

    public void initKeyInfo() {
        // 初始化 key 信息
        InitSource initSource = new InitSource();
        initSource.moveAsc();
        SignatureCtrl signatureCtrl = new SignatureCtrl();
        String keyIfHave = signatureCtrl.getKeyIfHave();
        if (!"".equals(keyIfHave)) {
            Key key = signatureCtrl.getKey(keyIfHave);
            if (key == null) {
                return;
            }
            if (signatureCtrl.checkKeyExpireTime(key)) {
                GeneParamConfig.setIsAppHasKey(true);
                GeneParamConfig.setAppKeyExpireTime(key.getExpireTime());
            } else {
                GeneParamConfig.setAppKeyExpireTime("Expired Key");
            }
        } else {

        }
    }

    public boolean checkFunction(String funcName) {
        boolean isVerify = true;
        HashMap<String, String> func = SysConfig.func;
        if (!GeneParamConfig.isIsAppHasKey()) {
            if (func.containsKey(funcName)) {
                isVerify = false;
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Warning");
                warning.setContentText(SysConfig.getLang("FuncNeedKey"));
                warning.showAndWait();
            }
        }
        return isVerify;
    }

    public void clearKey() {
        ArrayList<File> filesInFold = LocalFileUtils.getFilesInFold(SysConfig.KEY_CACHE_PATH);
        if (filesInFold != null) {
            // 存在就转移或者删除
            for (File file : filesInFold) {
                if (file.exists() && file.isFile()) {
                    if (file.getAbsolutePath().endsWith(".asc")) {
                        continue;
                    }
                    boolean b = file.renameTo(new File(SysConfig.HISTORY_CACHE_PATH + TimeUtils.getNow_yyyy_MM_dd_HH_mm_ss() + "_key.txt"));
                    if (!b) {
                        file.delete();
                    }
                }
            }
        }
    }


    public boolean checkKey(String inputKey) {
        Key key = getKey(inputKey);
        boolean isVerify = true;
        if (!checkKey4Name(key)) {
            isVerify = false;
        }
        if (!checkKeyExpireTime(key)) {
            isVerify = false;
        }

        return isVerify;
    }

    public Key getKey(String inputKey) {
        Key key = null;
        if (inputKey == null || "".equals(inputKey)) {
            return key;
        }
        try {
            PGPUtils pgpUtils = new PGPUtils();
            String decode = pgpUtils.decode(inputKey);
            if (decode == null) {
                LogUtils.error("IncorrectKey: decode == null");
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            key = objectMapper.readValue(decode, Key.class);
            if (key == null) {
                throw new RuntimeException("checkKey parse json error.");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LogUtils.error("getKey Exception, inputKey:{" + inputKey + " }, " + e.getMessage());
        }

        return key;
    }


    public boolean checkKey4Name(Key key) {
        if (key == null) {
            return false;
        }
        String keyFor = key.getKeyFor();
        if (!"photo2pdf".equalsIgnoreCase(keyFor)) {
            LogUtils.error("The key is incorrect, the key {" + keyFor + "} is not for this app.");
            return false;
        }
        return true;
    }

    public boolean checkKeyExpireTime(Key key) {
        if (key == null) {
            return false;
        }

        String expireTimeStr = key.getExpireTime();

        LocalDateTime expireTime = TimeUtils.parseryyyyMMddHHmmss(expireTimeStr);
        LocalDateTime localDateTime = getLocalDateTime();
        if (expireTime.compareTo(localDateTime) < 0) {
            LogUtils.error("The key is incorrect, the key has expired.");
            LogUtils.error("the key has expired. expireTime:" + expireTime + ",localDateTime:" + localDateTime);
            return false;
        }

        return true;
    }


    public String getKeyIfHave() {
        try {
            String absPath = SysConfig.KEY_CACHE_PATH + "key.txt";
            if (new File(absPath).exists()) {
                byte[] load = LocalFileUtils.load(absPath);
                return new String(load);
            }
            // mac系统可能会清除缓存路径的数据，多做一份备份使用
            String KEY_CACHE_PATH2 = GeneParamConfig.getPdfSavePath() + "key" + File.separator;
            String absPath2 = KEY_CACHE_PATH2 + "key.txt";
            if (new File(absPath2).exists()) {
                byte[] load = LocalFileUtils.load(absPath2);
                return new String(load);
            }
        } catch (IOException e) {
            LogUtils.error("getKeyIfHave error: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }


    public LocalDateTime getLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        // 获取日志文件的创建时间中最新的日期
        LocalDateTime freshLogDateTime = LocalDateTime.now();
        ArrayList<File> filesInFold = LocalFileUtils.getFilesInFold(SysConfig.LOG_CACHE_PATH);
        if (filesInFold != null) {
            for (File file : filesInFold) {
                if (file.exists() && file.isFile()) {
                    String absolutePath = file.getAbsolutePath();
                    FileTime fileTime = null;
                    try {
                        fileTime = Files.readAttributes(Paths.get(absolutePath), BasicFileAttributes.class).creationTime();
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }
                    long timestamp = fileTime.toMillis();
                    LocalDateTime createTime = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                    if (freshLogDateTime.compareTo(createTime) < 0) {
                        freshLogDateTime = createTime;
                    }
                }
            }
        }
        // 取最新时间
        if (freshLogDateTime.compareTo(now) > 0) {
            now = freshLogDateTime;
        }

        return now;
    }

}
