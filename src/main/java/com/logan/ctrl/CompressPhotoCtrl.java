package com.logan.ctrl;

import com.logan.config.CacheData;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.utils.LocalFileUtils;
import com.logan.utils.LogUtils;
import com.logan.utils.PhotoUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * @author Logan Qin
 * @date 2021/12/22 15:03
 */
public class CompressPhotoCtrl {

    public boolean compressPhotos(ArrayList<String> photosPath) {
        LogUtils.info("compressPhotos starting");
        try {
            if (photosPath == null || photosPath.size() == 0) {
                return true;
            }
            CacheData.compressPhotoAmount.set(0);
            float quality = GeneParamConfig.getPdfPhotoCompressionQuality();
            LogUtils.info("compressPhotos Quality: " + quality);
            for (String photoPath : photosPath) {
                asyncWork(photoPath, quality);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            LogUtils.info("compressPhotos assignment end");
        }
        return true;
    }

    public CompletableFuture<Void> asyncWork(String photoPath, float quality) {
        SysConfig.asyncPool.execute(() -> {
            try {
//                System.out.println("asyncWork start working.");
//                LogUtils.info("Thread name: " + Thread.currentThread().getName());
                String previewPhotosPath = LocalFileUtils.mkTempDir("previewPhotos");
                String fileFullName = photoPath.substring(photoPath.lastIndexOf(File.separator) + 1);
                String fileFormat = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
                String fileFullNameNew = PhotoUtils.getCompressName(fileFullName);
                // 增加识别的后缀，已识别同一张图片被用户选择两次的图片
                String filePathFullNameCompress = previewPhotosPath + fileFullNameNew;

                // 图片文件小于 200 kB 的不压缩了
                boolean isNeedScale = true;
                // 不压缩的图片格式（压缩后文件变得更大了）, 直接 copy 到目标位置
                if (fileFormat.equalsIgnoreCase("jpg") || fileFormat.equalsIgnoreCase("jpeg")) {
                    byte[] load = LocalFileUtils.load(photoPath);
                    String s = LocalFileUtils.save2Path(load, previewPhotosPath, fileFullNameNew);
//                    System.out.println("使用   原始图片");
                } else if (new File(photoPath).length() <= SysConfig.skipCompressPhotoSize) {
                    isNeedScale = false;
                    byte[] load = LocalFileUtils.load(photoPath);
                    String s = LocalFileUtils.save2Path(load, previewPhotosPath, fileFullNameNew);
//                    System.out.println("使用   原始图片2  " + photoPath);
                } else {
                    boolean b = PhotoUtils.compressPic(photoPath, filePathFullNameCompress, fileFormat, quality);
                    if (!b) {
                        byte[] load = LocalFileUtils.load(photoPath);
                        String s = LocalFileUtils.save2Path(load, previewPhotosPath, fileFullNameNew);
                        LogUtils.info("compress exception, copy original file to: " + s);
                    }
                }

                // 获取小文件
                String filePathFullName = filePathFullNameCompress;
                if (isNeedScale) {
                    // 调整图片的分辨率大小
                    String fileFullNameResize = PhotoUtils.getResizeName(fileFullName);
                    // 增加识别的后缀，已识别同一张图片被用户选择两次的图片
                    String filePathFullNameResize = previewPhotosPath + fileFullNameResize;
                    boolean b = PhotoUtils.resize(filePathFullNameCompress, filePathFullNameResize, fileFormat, quality);
                    if (!b) {
                        filePathFullNameResize = filePathFullNameCompress;
                    }

                    long compressSize = getFileSize(filePathFullNameCompress);
                    long resizeSize = getFileSize(filePathFullNameResize);
                    if (compressSize < resizeSize) {
//                        System.out.println("使用   压缩图片");
                        filePathFullName = filePathFullNameCompress;
                    } else {
                        filePathFullName = filePathFullNameResize;
//                        System.out.println("使用resize图片");
                    }
                }

                // 缓存压缩图片路径与原始图片路径的信息
                CacheData.getOriginalPhoto2compressPhotoMap().put(photoPath, filePathFullName);
                CacheData.getCompressPhoto2OriginalPhotoMap().put(filePathFullName, photoPath);
                // 添加到 list
                CacheData.getPhotosCompressPathAndFullName().add(filePathFullName);
                int i = CacheData.compressPhotoAmount.addAndGet(1);
            } catch (Exception | Error e) {
                // 异常也当完成了一张图片的压缩，要不然后面等待完成的地方有问题
                CacheData.compressPhotoAmount.addAndGet(1);
                LogUtils.info("compress photo exception");
                e.printStackTrace();
                throw new RuntimeException("asyncWork compress photo exception");
            } finally {
                if (CacheData.compressPhotoAmount.get() % 10 == 0) {
                    LogUtils.info("corePoolSize: " + SysConfig.asyncPool.getCorePoolSize() + ", queue size: " + SysConfig.asyncPool.getQueue().size());
                    LogUtils.info("compress photo gc starting");
                    CacheData.gc();
                }
            }
        });

        return null;
    }


    public static long getFileSize(String filename) {
        File file = new File(filename);
        if (!file.exists() || !file.isFile()) {
            LogUtils.error("文件不存在: " + filename);
            return 123456789012345L;
        }
        return file.length();
    }

}
