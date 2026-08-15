package com.logan.utils.convert;

import com.logan.config.PhotoFormat;
import com.logan.utils.AVIFConverterUtils;
import com.logan.utils.LogUtils;

/**
 * 其他格式转 AVIF 策略
 * author: Logan.qin
 * date: 2026/8/15
 */
public class OthersToAvifStrategy implements ImageConvertStrategy {

    @Override
    public boolean convert(String srcFilePath, String descFilePath, float quality) {
        try {
            AVIFConverterUtils.convert2AVIFAdaptor(srcFilePath, descFilePath, (int) quality);
            return true;
        } catch (Exception e) {
            LogUtils.error("Others to AVIF conversion failed: " + e.getMessage());
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
        return descLower.endsWith("." + PhotoFormat.AVIF.getValue());
    }
}