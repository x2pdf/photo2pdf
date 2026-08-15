package com.logan.utils.convert;

import com.logan.config.PhotoFormat;
import com.logan.utils.JXLConverterUtils;
import com.logan.utils.LogUtils;

/**
 * 其他格式转 JXL 策略
 * author: Logan.qin
 * date: 2026/8/15
 */
public class OthersToJxlStrategy implements ImageConvertStrategy {

    @Override
    public boolean convert(String srcFilePath, String descFilePath, float quality) {
        try {
            JXLConverterUtils.convert2JXLAdaptor(srcFilePath, descFilePath, quality);
            return true;
        } catch (Exception e) {
            LogUtils.error("Others to JXL conversion failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean supports(String srcFilePath, String descFilePath) {
        if (srcFilePath == null || descFilePath == null) {
            return false;
        }
        String descLower = descFilePath.toLowerCase();
        return descLower.endsWith("." + PhotoFormat.JXL.getValue());
    }
}
