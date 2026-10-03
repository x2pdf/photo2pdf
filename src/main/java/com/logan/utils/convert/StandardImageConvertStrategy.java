package com.logan.utils.convert;


import com.logan.config.PhotoFormat;
import com.logan.utils.LogUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

/**
 * 标准格式转换策略（使用 ImageIO，JPEG 读写由 TwelveMonkeys 插件通过 SPI 增强）
 * author: Logan.qin
 * date: 2026/8/15
 */
public class StandardImageConvertStrategy implements ImageConvertStrategy {

    @Override
    public boolean convert(String srcFilePath, String descFilePath, float quality) {
        try {
            BufferedImage src = ImageIO.read(new File(srcFilePath));
            if (src == null) {
                LogUtils.error("No ImageIO reader found for: " + srcFilePath);
                return false;
            }

            String formatName = getFormatName(descFilePath);
            if (isJpeg(formatName)) {
                return writeJpeg(toOpaqueRgb(src), new File(descFilePath), quality);
            }

            // PNG 等无损格式直接写出，保留透明通道；不支持透明的格式（如 BMP）找不到写入器时，转为 RGB 再写
            if (ImageIO.write(src, formatName, new File(descFilePath))) {
                return true;
            }
            return ImageIO.write(toOpaqueRgb(src), formatName, new File(descFilePath));
        } catch (Exception | Error e) {
            LogUtils.error("Standard image conversion failed for: " + srcFilePath);
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

        boolean isSpecialSrc = srcLower.endsWith("." + PhotoFormat.JXL.getValue()) ||
                srcLower.endsWith("." + PhotoFormat.AVIF.getValue());
        boolean isSpecialDesc = descLower.endsWith("." + PhotoFormat.JXL.getValue()) ||
                descLower.endsWith("." + PhotoFormat.AVIF.getValue());

        return !isSpecialSrc && !isSpecialDesc;
    }

    /**
     * 将任意色彩模型（ARGB、调色板、灰度+透明、16 位等）的图片绘制到白底的 8 位 RGB 画布上。
     * JPEG 不支持透明通道，若直接写入 4 通道数据，看图软件会按 CMYK/YCCK 解码导致整体偏色。
     */
    private BufferedImage toOpaqueRgb(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_RGB) {
            return src;
        }
        BufferedImage rgb = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
            g.drawImage(src, 0, 0, null);
        } finally {
            g.dispose();
        }
        return rgb;
    }

    private boolean writeJpeg(BufferedImage rgb, File descFile, float quality) throws Exception {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            LogUtils.error("No JPEG ImageWriter available");
            return false;
        }
        ImageWriter writer = writers.next();
        ImageWriteParam params = writer.getDefaultWriteParam();
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(Math.max(0f, Math.min(1f, quality)));

        if (descFile.exists()) {
            // ImageOutputStream 不会截断已存在的文件，先删除避免残留旧数据
            descFile.delete();
        }
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(descFile)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(rgb, null, null), params);
            return true;
        } finally {
            writer.dispose();
        }
    }

    private boolean isJpeg(String formatName) {
        return PhotoFormat.JPG.getValue().equals(formatName)
                || PhotoFormat.JPEG.getValue().equals(formatName)
                || PhotoFormat.JFIF.getValue().equals(formatName);
    }

    private String getFormatName(String filePath) {
        String lower = filePath.toLowerCase();
        int dot = lower.lastIndexOf('.');
        String ext = dot >= 0 ? lower.substring(dot + 1) : "";
        if (ext.isEmpty() || !ImageIO.getImageWritersBySuffix(ext).hasNext()) {
            return PhotoFormat.JPG.getValue();
        }
        return ext;
    }
}
