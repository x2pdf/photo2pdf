package com.logan.ctrl.helppage.experfunc.zip;

import com.logan.config.AppFilePathConfig;
import com.logan.utils.LogUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ZIPPasswordPresetConfig {

    // ZIP密码预设, key为密码名称，value为密码
    public static HashMap<String, String> zipPasswordPresetMap = new HashMap<>();
    
    
    public static void addZIPPasswordPreset(String name, String password) {
        zipPasswordPresetMap.put(name, password);
        saveZIPPasswordPreset2File(zipPasswordPresetMap);
    }

    public static String getZIPPasswordPreset(String name) {
        return zipPasswordPresetMap.get(name);
    }

    public static HashMap<String, String>  getZIPPasswordPreset() {
        return zipPasswordPresetMap;
    }
    
    public static void removeZIPPasswordPreset(String name) {
        zipPasswordPresetMap.remove(name);
        saveZIPPasswordPreset2File(zipPasswordPresetMap);
    }
    
    public static void saveZIPPasswordPreset2File(HashMap<String, String> zipPasswordPresetMap) {
        try {
            File dir = new File(AppFilePathConfig.PWD_PRESET_CACHE_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            String filePath = AppFilePathConfig.PWD_PRESET_CACHE_PATH + "passwordpreset.properties";
            Properties properties = new Properties();
            
            for (Map.Entry<String, String> entry : zipPasswordPresetMap.entrySet()) {
                properties.setProperty(entry.getKey(), entry.getValue());
            }
            
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(filePath), StandardCharsets.UTF_8);
            properties.store(writer, "ZIP Password Preset Configuration");
            writer.close();
            
            LogUtils.info("ZIP password preset saved successfully to: " + filePath);
        } catch (IOException e) {
            LogUtils.error("Failed to save ZIP password preset: " + e.toString());
            e.printStackTrace();
        }
    }

    public static void initZIPPasswordPresetFromFile() {
        try {
            String filePath = AppFilePathConfig.PWD_PRESET_CACHE_PATH + "passwordpreset.properties";
            File file = new File(filePath);
            
            if (!file.exists()) {
                LogUtils.info("ZIP password preset file does not exist, skipping initialization");
                return;
            }
            
            Properties properties = new Properties();
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8);
            properties.load(reader);
            reader.close();
            
            zipPasswordPresetMap.clear();
            for (String key : properties.stringPropertyNames()) {
                zipPasswordPresetMap.put(key, properties.getProperty(key));
            }
            
            LogUtils.info("ZIP password preset loaded successfully from: " + filePath);
            LogUtils.info("Loaded " + zipPasswordPresetMap.size() + " password presets");
        } catch (IOException e) {
            LogUtils.error("Failed to load ZIP password preset: " + e.toString());
            e.printStackTrace();
        }
    }
}
