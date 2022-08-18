package com.logan.utils;

import com.logan.config.CacheData;
import com.logan.config.SysConfig;
import com.logan.ctrl.ViewGridPaneCtrl;
import com.logan.model.PhotoFileInfo;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Logan Qin
 * @date 2022/2/14 15:44
 */


public class HeicConvertUtils {

    //    public static void main(String[] args) {
//        convert("png", "C:\\Users\\Administrator\\Pictures\\format\\sample.heic",
//                "C:\\Users\\Administrator\\Pictures\\format\\sample.png");
//
//    }


    public static ArrayList<String> heicPhotoFilterMultiThread(ArrayList<String> selectPhotos) {
        if (selectPhotos == null || selectPhotos.size() == 0) {
            return selectPhotos;
        }

        ArrayList<String> heicPhotos = new ArrayList<>();
        for (String selectPhoto : selectPhotos) {
            String fileFullName = selectPhoto.substring(selectPhoto.lastIndexOf(File.separator) + 1);
            String fileFormat = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
            if ("heic".equalsIgnoreCase(fileFormat)) {
                heicPhotos.add(selectPhoto);
            }
        }

        if (heicPhotos.size() >= 10) {
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

        if (heicPhotos.size() > 0) {
            for (String heicPhoto : heicPhotos) {
                SysConfig.asyncPool.execute(() -> {
                    try {
                        String s = heicPhotoFilter(heicPhoto);
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
        int size = heicPhotos.size();
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

        ArrayList<String> res = new ArrayList<>();
        for (String selectPhoto : selectPhotos) {
            String fileFullName = selectPhoto.substring(selectPhoto.lastIndexOf(File.separator) + 1);
            String fileFormat = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
            if ("heic".equalsIgnoreCase(fileFormat)) {
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


    public static ArrayList<String> heicPhotoFilter(ArrayList<String> selectPhotos) {
        if (selectPhotos == null || selectPhotos.size() == 0) {
            return selectPhotos;
        }
        ArrayList<String> res = new ArrayList<>();
        for (String selectPhoto : selectPhotos) {
            String s = heicPhotoFilter(selectPhoto);
            if (s != null) {
                res.add(s);
            }
        }
        return res;
    }


    public static String heicPhotoFilter(String from) {
        String previewPhotosPath = LocalFileUtils.mkTempDir("previewPhotos");
        String fileFullName = from.substring(from.lastIndexOf(File.separator) + 1);
        String fileFormat = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
        if ("heic".equalsIgnoreCase(fileFormat)) {
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
            String fileNameNew = previewPhotosPath + fileName + ".png";
            if (convert("png", from, fileNameNew) == 0) {
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
        String fileNameNew = previewPhotosPath + fileName + ".jpeg";
        ;
        if (convert("jpeg", from, fileNameNew) == 0) {
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
        String fileNameNew = previewPhotosPath + fileName + ".png";
        ;
        if (convert("png", from, fileNameNew) == 0) {
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

    public static int convert2PNG(String from, String to) {
        return convert("png", from, to);
    }

    public static int convert2JPEG(String from, String to) {
        return convert("jpeg", from, to);
    }

    // targetFormat: jpeg, png
    // from: 绝对路径和文件全名称
    // to: 绝对路径和文件全名称
    public static int convert(String targetFormat, String from, String to) {
        try {
            long start = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();
            String[] command;
            String nodejsPath = SysConfig.NODEJS_PATH;
            if ("jpeg".equalsIgnoreCase(targetFormat)) {
                command = new String[]{nodejsPath + "node", "heic2png.js", from + "=" + to};
            } else {
                command = new String[]{nodejsPath + "node", "heic2jpeg.js", from + "=" + to};
            }

            Process process = runtime.exec(command, null, new File(SysConfig.HEIC_CONVERT_JS_NODE));
            String inStr = consumeInputStream(process.getInputStream());
            String errStr = consumeInputStream(process.getErrorStream()); //若有错误信息则输出
            int proc = process.waitFor();
            if (proc == 0) {
                LogUtils.info("heic convert 执行成功. " + inStr);
            } else {
                LogUtils.error("heic convert 执行失败. " + errStr);
            }

            long end = System.currentTimeMillis();
            LogUtils.info("heic convert spend(ms): " + (end - start));
            return proc;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return 1;
    }

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
