package com.logan.utils.convert;


import com.logan.config.PhotoFormat;
import com.logan.utils.LogUtils;

import javax.imageio.*;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 标准格式转换策略（使用 ImageIO）
 * author: Logan.qin
 * date: 2026/8/15
 */
public class StandardImageConvertStrategy implements ImageConvertStrategy {

    @Override
    public boolean convert(String srcFilePath, String descFilePath, float quality) {
        FileInputStream file = null;
        BufferedImage src = null;
        FileOutputStream out = null;
        ImageWriter imgWrier = null;
        ImageWriteParam imgWriteParams = null;

        try {
            imgWriteParams = new JPEGImageWriteParam(null);
            imgWriteParams.setCompressionMode(imgWriteParams.MODE_EXPLICIT);
            imgWriteParams.setCompressionQuality(quality);

            ColorModel colorModel = ImageIO.read(new FileInputStream(srcFilePath)).getColorModel();
            imgWriteParams.setDestinationType(new ImageTypeSpecifier(
                    colorModel, colorModel.createCompatibleSampleModel(16, 16)));

            file = new FileInputStream(srcFilePath);
            src = ImageIO.read(file);
            out = new FileOutputStream(descFilePath);

            String formatName = getFormatName(descFilePath);
            imgWrier = ImageIO.getImageWritersByFormatName(formatName).next();
            imgWrier.reset();
            imgWrier.setOutput(ImageIO.createImageOutputStream(out));
            imgWrier.write(null, new IIOImage(src, null, null), imgWriteParams);
            out.flush();

            return true;
        } catch (Exception | Error e) {
            LogUtils.error("Standard image conversion failed for: " + srcFilePath);
            e.printStackTrace();
            return false;
        } finally {
            closeResources(file, src, out, imgWrier, imgWriteParams);
        }
    }

    @Override
    public boolean supports(String srcFilePath, String descFilePath) {
        if (srcFilePath == null || descFilePath == null) {
            return false;
        }
        String srcLower = srcFilePath.toLowerCase();
        String descLower = descFilePath.toLowerCase();

        boolean isSpecialSrc = srcLower.endsWith("." + PhotoFormat.JXL.getValue()) ||
                srcLower.endsWith("." + PhotoFormat.AVIF.getValue());
        boolean isSpecialDesc = descLower.endsWith("." + PhotoFormat.JXL.getValue()) ||
                descLower.endsWith("." + PhotoFormat.AVIF.getValue());

        return !isSpecialSrc && !isSpecialDesc;
    }

    private String getFormatName(String filePath) {
        String lower = filePath.toLowerCase();
        if (lower.endsWith("." + PhotoFormat.JPG.getValue())) {
            return PhotoFormat.JPG.getValue();
        } else if (lower.endsWith("." + PhotoFormat.JPEG.getValue())) {
            return PhotoFormat.JPEG.getValue();
        } else if (lower.endsWith("." + PhotoFormat.PNG.getValue())) {
            return PhotoFormat.PNG.getValue();
        }
        return PhotoFormat.JPG.getValue();
    }

    private void closeResources(FileInputStream file, BufferedImage src,
                                FileOutputStream out, ImageWriter imgWrier,
                                ImageWriteParam imgWriteParams) {
        try {
            if (out != null) out.close();
            if (file != null) file.close();
        } catch (Exception e) {
            LogUtils.error("Close resource error: " + e.getMessage());
        }
    }
}
