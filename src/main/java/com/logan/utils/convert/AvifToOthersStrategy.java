package com.logan.utils.convert;


import com.logan.config.PhotoFormat;
import com.logan.utils.AVIFConverterUtils;
import com.logan.utils.LogUtils;

/**
 * AVIF 转其他格式策略
 * author: Logan.qin
 * date: 2026/8/15
 */
public class AvifToOthersStrategy implements ImageConvertStrategy {

    @Override
    public boolean convert(String srcFilePath, String descFilePath, float quality) {
        try {
            AVIFConverterUtils.convertAVIF2OthersAdaptor(srcFilePath, descFilePath, (int) quality);
            return true;
        } catch (Exception e) {
            LogUtils.error("AVIF to others conversion failed: " + e.getMessage());
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

        return srcLower.endsWith("." + PhotoFormat.AVIF.getValue()) &&
                (descLower.endsWith("." + PhotoFormat.PNG.getValue()) ||
                        descLower.endsWith("." + PhotoFormat.JPEG.getValue()) ||
                        descLower.endsWith("." + PhotoFormat.JPG.getValue()));
    }
}
