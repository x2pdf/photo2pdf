package com.logan.utils.convert;

import com.logan.config.PhotoFormat;
import com.logan.utils.JXLConverterUtils;
import com.logan.utils.LogUtils;

/**
 * JXL 转其他格式策略
 * author: Logan.qin
 * date: 2026/8/15
 */
public class JxlToOthersStrategy implements ImageConvertStrategy {

    @Override
    public boolean convert(String srcFilePath, String descFilePath, float quality) {
        try {
            JXLConverterUtils.convertJXL2OthersAdaptor(srcFilePath, descFilePath, quality);
            return true;
        } catch (Exception e) {
            LogUtils.error("JXL to others conversion failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean supports(String srcFilePath, String descFilePath) {
        if (srcFilePath == null || descFilePath == null) {
            return false;
        }
        String srcLower = srcFilePath.toLowerCase();
        String descLower = descFilePath.toLowerCase();

        return srcLower.endsWith("." + PhotoFormat.JXL.getValue()) &&
                (descLower.endsWith("." + PhotoFormat.PNG.getValue()) ||
                        descLower.endsWith("." + PhotoFormat.JPEG.getValue()) ||
                        descLower.endsWith("." + PhotoFormat.JPG.getValue()));
    }
}