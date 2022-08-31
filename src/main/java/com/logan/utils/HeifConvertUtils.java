package com.logan.utils;

import com.logan.config.CacheData;
import com.logan.config.Format;
import com.logan.config.SysConfig;
import com.logan.ctrl.ViewGridPaneCtrl;
import com.logan.model.PhotoFileInfo;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Logan Qin
 * @date 2022/2/14 15:44
 */


public class HeifConvertUtils {


    /**
     * 异步方式转换传入的 selectPhotos 中属于 heif 格式的图片为指定的 targetFormat 图片
     * @param selectPhotos
     * @param targetFormat
     * @param quality -- targetFormat为jpg或jpeg时，这个参数才会有效，设定图片转换质量， 0-1 之间的数值
     * @return
     */
    public static ArrayList<String> asyncConvert(ArrayList<String> selectPhotos, String targetFormat, String quality) {
        if (selectPhotos == null || selectPhotos.size() == 0) {
            return selectPhotos;
        }

        // 找出 heif 图片
        ArrayList<String> heifPhotos = new ArrayList<>();
        for (String selectPhoto : selectPhotos) {
            String fileFullName = selectPhoto.substring(selectPhoto.lastIndexOf(File.separator) + 1);
            String fileFormat = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
            if (Format.heic.getValue().equalsIgnoreCase(fileFormat) || Format.heif.getValue().equalsIgnoreCase(fileFormat)) {
                heifPhotos.add(selectPhoto);
            }
        }

        // 一些温馨提示
        if (heifPhotos.size() >= 10) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Info");
            alert.setContentText(SysConfig.getLang("TooManyHEICFile"));
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.isPresent()) {
                if (buttonType.get() == ButtonType.CANCEL) {
                    LogUtils.info("user cancel convert photos");
                    return new ArrayList<>();
                }
                LogUtils.info("user sure convert photos");
            }
        }

        // 异步线程去处理转换
        if (heifPhotos.size() > 0) {
            for (String heicPhoto : heifPhotos) {
                SysConfig.asyncPool.execute(() -> {
                    try {
                        String s = convert(heicPhoto, targetFormat, quality);
                        CacheData.convertPhotoAmount.addAndGet(1);
                        CacheData.getHeic2convertPhotoMap().put(heicPhoto, s);
                    } catch (Exception | Error e) {
                        CacheData.convertPhotoAmount.addAndGet(1);
                        CacheData.getHeic2convertPhotoMap().put(heicPhoto, heicPhoto);
                        LogUtils.info("convert photo exception");
                        e.printStackTrace();
//                        throw new RuntimeException("asyncWork compress photo exception");
                    }
                });
            }
        }

        long start = System.currentTimeMillis();
        int size = heifPhotos.size();
        LogUtils.info("convert photo amount: " + size);
        // 检测图片是否压缩完成
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(30);
        LocalDateTime firstDetectQueueIsZero = null;
        while (true) {
            if (CacheData.convertPhotoAmount.get() == size) {
                long spend = System.currentTimeMillis() - start;
                LogUtils.info("convert photo spend(ms): " + spend);
                break;
            }
            if (LocalDateTime.now().compareTo(endTime) > 0) {
                LogUtils.error("Wait for more than 30 minutes to convert the image!");
                break;
            }

            if (firstDetectQueueIsZero == null && SysConfig.asyncPool.getQueue().size() == 0) {
                // 借助线程池的队列中缓存任务的数量来判断是否接近结束了
                firstDetectQueueIsZero = LocalDateTime.now();
            }
            if (firstDetectQueueIsZero != null) {
                // 线程池的队列中缓存任务已经没有的情况下，等待核心线程最大等待时间 1 分钟
                if (LocalDateTime.now().minusMinutes(1).compareTo(firstDetectQueueIsZero) > 0) {
                    LogUtils.error("The cached task in the thread pool's queue has completed and has been waiting for more than 2 minutes.");
                    firstDetectQueueIsZero = null;
                    break;
                }
            }
            try {
                // 间隔一定时间轮询
                Thread.sleep(1000);
                LogUtils.info("convert photo %: " + CacheData.convertPhotoAmount.get() + " / " + size);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        CacheData.convertPhotoAmount.set(0);
        LogUtils.info("convert end");


        // 返回所有图片的路径
        ArrayList<String> res = new ArrayList<>();
        for (String selectPhoto : selectPhotos) {
            String fileFullName = selectPhoto.substring(selectPhoto.lastIndexOf(File.separator) + 1);
            String fileFormat = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
            if (Format.heic.getValue().equalsIgnoreCase(fileFormat) || Format.heif.getValue().equalsIgnoreCase(fileFormat)) {
                String s = CacheData.getHeic2convertPhotoMap().get(selectPhoto);
                if (null != s) {
                    res.add(s);
                }
                continue;
            }
            res.add(selectPhoto);
        }
        return res;
    }


    /**
     * heif 图片的转换
     * @param from
     * @param targetFormat
     * @param quality
     * @return
     */
    public static String convert(String from, String targetFormat, String quality) {
        String previewPhotosPath = LocalFileUtils.mkTempDir("previewPhotos");
        String fileFullName = from.substring(from.lastIndexOf(File.separator) + 1);
        String fileFormat = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
        // 僅僅處理 heif 文件
        if (Format.heic.getValue().equalsIgnoreCase(fileFormat) || Format.heif.getValue().equalsIgnoreCase(fileFormat)) {
            if (targetFormat == null || "".equals(targetFormat)) {
                targetFormat = Format.jpeg.getValue();
            }
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
            String fileNameNew = previewPhotosPath + fileName + "." + Format.jpeg.getValue();
//            String fileNameTemp = previewPhotosPath + fileName + System.currentTimeMillis() +  "." + Format.jpeg.getValue();
            if (convert(from, fileNameNew, targetFormat, quality)) {
                //因为 heic 转成的图片可能会变大，所以压缩图片以减小存储空间 todo(感觉作用不是很大)
                // compress(fileNameTemp, fileNameNew, quality);
                PhotoFileInfo photoFileInfo = ViewGridPaneCtrl.getPhotoFileInfo(from);
                PhotoFileInfo photoFileInfoNew = ViewGridPaneCtrl.getPhotoFileInfo(fileNameNew);
                photoFileInfoNew.setCreateTime(photoFileInfo.getCreateTime());
                photoFileInfoNew.setLastModifyTime(photoFileInfo.getLastModifyTime());
                CacheData.getPhotosFileInfoMap().put(from, photoFileInfo);
                return fileNameNew;
            } else {
                return from;
            }
        } else {
            return from;
        }
    }


    public static String convert2JPEG(String from) {
        String previewPhotosPath = LocalFileUtils.mkTempDir("previewPhotos");
        String fileFullName = from.substring(from.lastIndexOf(File.separator) + 1);
        String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
        String fileNameNew = previewPhotosPath + fileName + "." + Format.jpeg.getValue();
        ;
        if (convert(from, fileNameNew, Format.jpeg.getValue(), "1")) {
            PhotoFileInfo photoFileInfo = ViewGridPaneCtrl.getPhotoFileInfo(from);
            PhotoFileInfo photoFileInfoNew = ViewGridPaneCtrl.getPhotoFileInfo(fileNameNew);
            photoFileInfoNew.setCreateTime(photoFileInfo.getCreateTime());
            photoFileInfoNew.setLastModifyTime(photoFileInfo.getLastModifyTime());
            CacheData.getPhotosFileInfoMap().put(from, photoFileInfo);
            return fileNameNew;
        } else {
            return null;
        }
    }

    public static String convert2PNG(String from) {
        String previewPhotosPath = LocalFileUtils.mkTempDir("previewPhotos");
        String fileFullName = from.substring(from.lastIndexOf(File.separator) + 1);
        String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
        String fileNameNew = previewPhotosPath + fileName + "." + Format.png.getValue();
        ;
        if (convert(from, fileNameNew, Format.png.getValue(), "1")) {
            PhotoFileInfo photoFileInfo = ViewGridPaneCtrl.getPhotoFileInfo(from);
            PhotoFileInfo photoFileInfoNew = ViewGridPaneCtrl.getPhotoFileInfo(fileNameNew);
            photoFileInfoNew.setCreateTime(photoFileInfo.getCreateTime());
            photoFileInfoNew.setLastModifyTime(photoFileInfo.getLastModifyTime());
            CacheData.getPhotosFileInfoMap().put(from, photoFileInfo);
            return fileNameNew;
        } else {
            return null;
        }
    }

    // targetFormat: jpeg, png
    // from: 绝对路径和文件全名称
    // to: 绝对路径和文件全名称
    // targetFormat: 转为图片的格式，要和文件名称匹配
    // quality: 如果转换成的图片格式为 jpeg 时，可以设定图片转换质量， 0-1 之间的数值
    public static boolean convert(String from, String to, String targetFormat, String quality) {
        try {
            if (targetFormat == null || "".equals(targetFormat)) {
                targetFormat = Format.jpeg.getValue();
            }
            if (quality == null || "".equals(quality)) {
                quality = "1";
            }
            long start = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();
            String[] command;
            String nodejsPath = SysConfig.NODEJS_PATH;
            // node heic2png.js D:\Logan\code\photo2pdf\xxx.heic=D:\Logan\code\photo2pdf\xxx.png [quality]
            if (Format.jpeg.getValue().equalsIgnoreCase(targetFormat) || Format.jpg.getValue().equalsIgnoreCase(targetFormat)) {
                // 当转换为jpeg时， 可以有质量 quality 的参数设定
                command = new String[]{nodejsPath + "node", "heic2jpeg.js", from + "=" + to, quality};
            } else {
                command = new String[]{nodejsPath + "node", "heic2png.js", from + "=" + to};
            }

            Process process = runtime.exec(command, null, new File(SysConfig.HEIC_CONVERT_JS_NODE));
            String inStr = consumeInputStream(process.getInputStream());
            String errStr = consumeInputStream(process.getErrorStream()); //若有错误信息则输出
            int proc = process.waitFor();
            if (proc == 0) {
                LogUtils.info("heif convert 执行成功. " + inStr);
            } else {
                LogUtils.error("heif convert 执行失败. " + errStr);
            }

            long end = System.currentTimeMillis();
            LogUtils.info("heif convert spend(ms): " + (end - start));
            return proc == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static void compress(String from, String to, String quality) {
        String fileFullName = from.substring(from.lastIndexOf(File.separator) + 1);
        boolean b = PhotoUtils.compressPic(from, to, CacheData.getToFormat(), Float.parseFloat(quality));
    }

    /**
     * 读取 is 中的文本数据
     * @param is
     * @return
     * @throws IOException
     */
    public static String consumeInputStream(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String s;
        StringBuilder sb = new StringBuilder();
        while ((s = br.readLine()) != null) {
            sb.append(", ").append(s);
        }
        return sb.toString();
    }

}
