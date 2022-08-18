package com.logan.utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;

/*
 * @date 2021/12/22 15:04
 */
public class PhotoUtils {

    public static boolean compressPic(String srcFilePath, String descFilePath, String formatName, float quality) {
        FileInputStream file = null;
        BufferedImage src = null;
        FileOutputStream out = null;
        ImageWriter imgWrier;
        ImageWriteParam imgWriteParams;
        // 指定写图片的方式为 jpg
        try {
            imgWriteParams = new javax.imageio.plugins.jpeg.JPEGImageWriteParam(null);
            // 要使用压缩，必须指定压缩方式为 MODE_EXPLICIT
            imgWriteParams.setCompressionMode(imgWriteParams.MODE_EXPLICIT);
            // 这里指定压缩的程度，参数 quality 是取值 0~1 范围内，
            imgWriteParams.setCompressionQuality(quality);
//        imgWriteParams.setProgressiveMode(imgWriteParams.MODE_DISABLED);
            ColorModel colorModel = ImageIO.read(new FileInputStream(srcFilePath)).getColorModel();// ColorModel.getRGBdefault();
            // 指定压缩时使用的色彩模式
            imgWriteParams.setDestinationType(new javax.imageio.ImageTypeSpecifier(
                    colorModel, colorModel.createCompatibleSampleModel(16, 16)));

            file = new FileInputStream(srcFilePath);
//            LogUtils.info(file.length());
            src = ImageIO.read(file);
            out = new FileOutputStream(descFilePath);

            imgWrier = ImageIO.getImageWritersByFormatName(formatName).next();
            imgWrier.reset();
            // 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何
            // OutputStream构造
            imgWrier.setOutput(ImageIO.createImageOutputStream(out));
            // 调用write方法，就可以向输入流写图片
            imgWrier.write(null, new IIOImage(src, null, null), imgWriteParams);
            out.flush();
            out.close();

            file = null;
            src = null;
            out = null;
            imgWrier = null;
            imgWriteParams = null;
        } catch (Exception | Error e) {
            LogUtils.error("compressPic exception path: " + srcFilePath);
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static boolean resize(String src, String to, String formatName, float scale) {
        try {
            File srcFile = new File(src);
            File toFile = new File(to);
            BufferedImage img = ImageIO.read(srcFile);
            int w = img.getWidth();
            int h = img.getHeight();

            int newWidth = new BigDecimal(w).multiply(new BigDecimal(scale)).intValue();
            int newHeight = new BigDecimal(h).multiply(new BigDecimal(scale)).intValue();
            int type = img.getType();
            if (type == 0) {
                LogUtils.error("Unknown image type 0, default process, image path: " + src);
                type = 5;
            }
            BufferedImage dimg = new BufferedImage(newWidth, newHeight, type);
            Graphics2D g = dimg.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(img, 0, 0, newWidth, newHeight, 0, 0, w, h, null);
            g.dispose();
            ImageIO.write(dimg, formatName, toFile);
        } catch (Exception | Error e) {
            e.printStackTrace();
            LogUtils.error("resize exception : " + src + " - " + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean resize(String src, String to, int newWidth, int newHeight, String formatName) {
        try {
            File srcFile = new File(src);
            File toFile = new File(to);
            BufferedImage img = ImageIO.read(srcFile);
            int w = img.getWidth();
            int h = img.getHeight();

            BufferedImage dimg = new BufferedImage(newWidth, newHeight, img.getType());
            Graphics2D g = dimg.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(img, 0, 0, newWidth, newHeight, 0, 0, w, h, null);
            g.dispose();
            ImageIO.write(dimg, formatName, toFile);
        } catch (Exception | Error e) {
            e.printStackTrace();
            LogUtils.error("resize exception : " + src + " - " + e.getMessage());
            return false;
        }
        return true;
    }

    public static String getHEICConvertName(String targetFileFormat, String originalFullName) {
        String fileName = originalFullName.substring(0, originalFullName.lastIndexOf("."));
        return fileName + "_compress_" + getRand() + "." + targetFileFormat;
    }

    public static String getHEICConvertName(String originalFullName) {
        String fileName = originalFullName.substring(0, originalFullName.lastIndexOf("."));
        return fileName + "_compress_" + getRand() + ".jpeg";
    }


    public static String getCompressName(String originalFullName) {
        String fileName = originalFullName.substring(0, originalFullName.lastIndexOf("."));
        String fileFormat = originalFullName.substring(originalFullName.lastIndexOf(".") + 1);
        return fileName + "_compress_" + getRand() + "." + fileFormat;
    }

    public static String getResizeName(String originalFullName) {
        String fileName = originalFullName.substring(0, originalFullName.lastIndexOf("."));
        String fileFormat = originalFullName.substring(originalFullName.lastIndexOf(".") + 1);
        return fileName + "_resize_" + getRand() + "." + fileFormat;
    }

    public static String getRand() {
        double random = Math.random() * 1000000.987;
        int i = String.valueOf(random).indexOf(".");
        return String.valueOf(random).substring(0, i);
    }

    @Deprecated
    public static int getWaitingMaxMinute(int size) {
        // FYI
        // 450 photos -- 55s = 15s(compress) + 40s(render)
        // 100 photos -- 10s
        int compressWaitingMaxMinute = 1;
        if (size < 100) {
            compressWaitingMaxMinute = 3;
        } else if (size < 200) {
            compressWaitingMaxMinute = 5;
        } else if (size < 500) {
            compressWaitingMaxMinute = 8;
        } else if (size < 1000) {
            compressWaitingMaxMinute = 10;
        } else if (size < 2000) {
            compressWaitingMaxMinute = 20;
        } else {
            compressWaitingMaxMinute = 30;
        }

        return compressWaitingMaxMinute;
    }


    public static int getCompressPhotoSeconds(int size) {
        // FYI
        // 450 photos -- 55s = 15s(compress) + 40s(render)
        // 100 photos -- 10s
        // 1000 photos -- 100s = 90s + 10 s
        int compressWaiting = 1;
        if (size < 100) {
            compressWaiting = 10;
        } else if (size < 200) {
            compressWaiting = 20;
        } else if (size < 300) {
            compressWaiting = 40;
        } else if (size < 400) {
            compressWaiting = 60;
        } else if (size < 500) {
            compressWaiting = 80;
        } else if (size < 600) {
            compressWaiting = 90;
        } else if (size < 800) {
            compressWaiting = 100;
        } else if (size < 1000) {
            compressWaiting = 120;
        } else {
            compressWaiting = 600;
        }

        return compressWaiting;
    }

}
