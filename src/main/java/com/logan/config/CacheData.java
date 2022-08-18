package com.logan.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logan.ctrl.StatusBarCtrl;
import com.logan.ctrl.ViewGridPaneCtrl;
import com.logan.model.PhotoFileInfo;
import com.logan.utils.LocalFileUtils;
import com.logan.utils.LogUtils;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Logan Qin
 * @date 2021/12/22 14:49
 */
public class CacheData implements Serializable {
    private static final long serialVersionUID = 1L;

    private static CacheData cacheData;

    // 应用是否正在运行
    public static boolean isAppRunning = false;
    // 用来统计已经压缩的图片的数量，jpg等不压缩使用原始图片的也会累加数量
    public static AtomicInteger compressPhotoAmount = new AtomicInteger(0);
    // 标识用户是否要进行点击图片进行移除图片的标记
    public static boolean isClick2RemovePhoto = false;

    public static AtomicInteger convertPhotoAmount = new AtomicInteger(0);
    private static HashMap<String, String> heic2convertPhotoMap = new HashMap<>();


    //  用户选择图片的顺序的绝对路径
    private static ArrayList<String> photosPathUserSelectOrder = new ArrayList<>();
    // 要生成pdf需要的照片的原始绝对路径
    private static ArrayList<String> photosPath = new ArrayList<>();
    // 要生成pdf需要预览的照片的绝对路径，用于预览
    private static ArrayList<String> photosPreviewPath = new ArrayList<>();

    // 要生成pdf需要的照片经过压缩大小之后存放的路径，用于真实生成pdf
    private static ArrayList<String> photosGenePath = new ArrayList<>();
    // 要生成pdf需要的照片文件的信息，key：原始图片的绝对路径，value: PhotoFileInfo
    private static HashMap<String, PhotoFileInfo> photosFileInfoMap = new HashMap<>();
    // 要生成pdf需要的照片文件的信息，key：压缩图片绝对路径，value: 对应的原始图片绝对路径
    private static HashMap<String, String> compressPhoto2OriginalPhotoMap = new HashMap<>();
    // 要生成pdf需要的照片文件的信息，key：对应的原始图片绝对路径，value: 压缩图片绝对路径
    private static HashMap<String, String> originalPhoto2compressPhotoMap = new HashMap<>();
    // 压缩图片的绝对路径, 仅用于压缩过程中记录压缩的图片路径
    private static ArrayList<String> photosCompressPathAndFullName = new ArrayList<>();

    // 随机颜色，用来变换应用运行状态的背景色
    private static String[] randomColor = new String[]{"#90d7ec", "#50b7c1", "#508a88", "#d3c6a6", "#73b9a2"};


    // 要合并的pdf的路径
    private static ArrayList<String> mergePdfPath = new ArrayList<>();
    // 标识用户是否要进行点击图片进行移除图片的标记
    public static boolean isClick2RemovePdf = false;


    // 预览图片的 grid pane
    public static GridPane gridPane;
    // 开发模式刷新页面时使用到
    public static transient VBox settingsVBox;
    // 开发模式的
    public static VBox vmInfo;
    // 状态栏
    public static HBox statusHBox;
    // 状态栏中显示当前App的状态
    public static String appStatus = SysConfig.DEFAULT;


    // ===============Experimental cache data==================
    public static ArrayList<String> experimentalPhotos = new ArrayList<>();
    public static float compressPDFPhotoRatio = 0.7f;
    public static String toFormat = "jpg";
    public static float compressRatio = 0.7f;
    public static float scale = 0.7f;
    public static int width = 1920;
    public static int height = 1080;


    private CacheData() {
    }

    public static CacheData instance() {
        if (cacheData == null) {
            cacheData = new CacheData();
        }
        return cacheData;
    }

    /**
     * 用户修改了配置之后，重新按照新的设定刷新页面
     */
    public static void refreshGeneConfig(String from) {
        try {
            if (from == null) {
                from = "";
            }
            LogUtils.info(from + " - refreshGeneConfig starting");
            GridPane gridPane = CacheData.gridPane;
            // 从预览list中获取图片信息
            ArrayList<String> photosPath = CacheData.getPhotosPreviewPath();
            // 用户可能改变了排序方式，所以需要重新排序,然后放回预览的list去
            ArrayList<String> sortPhotos = ViewGridPaneCtrl.sortPhotos(photosPath);
            CacheData.setPhotosPreviewPath(sortPhotos);
            // 需要清除已有的图片
            gridPane.getChildren().clear();
            ViewGridPaneCtrl viewGridPaneCtrl = new ViewGridPaneCtrl();
            viewGridPaneCtrl.photosViewPane(gridPane, sortPhotos);

            CacheData.refreshStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.info(from + " - refreshGeneConfig end");
    }

    public static void gc() {
        System.gc();
        LogUtils.info("Manual garbage collection end");
    }


    public static void refreshStatus() {
        StatusBarCtrl statusBarCtrl = new StatusBarCtrl();
        HBox statusInfoHBox = statusBarCtrl.getStatusInfo();
        CacheData.statusHBox.getChildren().clear();
        CacheData.statusHBox.getChildren().add(statusInfoHBox);
    }


    public static synchronized void swapPhoto(int offset1, int offset2) {
        String offset1Photo = CacheData.getPhotosPath().get(offset1);
        String offset2Photo = CacheData.getPhotosPath().get(offset2);
        CacheData.getPhotosPath().set(offset1, offset2Photo);
        CacheData.getPhotosPath().set(offset2, offset1Photo);

        String offset1PhotoPre = CacheData.getPhotosPreviewPath().get(offset1);
        String offset2PhotoPre = CacheData.getPhotosPreviewPath().get(offset2);
        CacheData.getPhotosPreviewPath().set(offset1, offset2PhotoPre);
        CacheData.getPhotosPreviewPath().set(offset2, offset1PhotoPre);
    }


    public static void syncSelectPhoto2OtherList(ArrayList<String> selectPhotos) {
        // 先清除数据
        if (CacheData.getPhotosPreviewPath().size() == 0) {
            clearPreviewPhoto();
        }
        if (selectPhotos == null || selectPhotos.size() == 0) {
            return;
        }
        // 记录用户的选择图片的顺序
        photosPathUserSelectOrder.addAll(selectPhotos);

        // 先将已有的预览照片和新添加的照片集中，然后在排序
        ArrayList<String> AllPhotoPath = new ArrayList<>();
        AllPhotoPath.addAll(selectPhotos);
        AllPhotoPath.addAll(CacheData.getPhotosPreviewPath());

        // 排序, 注意：排序的过程中会将照片的一些创建时间信息添加到缓存中
        ArrayList<String> sortPhotos = ViewGridPaneCtrl.sortPhotos(AllPhotoPath);

        // 将排序好的照片，同步到其他所有List中
        CacheData.getPhotosPath().clear();
        CacheData.setPhotosPath(sortPhotos);

        // 因为后面有移除list元素的操作，浅拷贝会导致移除元素时异常
        ArrayList<String> copyList = copyList(sortPhotos);
        CacheData.getPhotosPreviewPath().clear();
        CacheData.setPhotosPreviewPath(copyList);
    }

    public static void syncCompressPhoto2GeneList(ArrayList<String> photosCompressPath) {
        // 先清除数据
        if (photosCompressPath == null || photosCompressPath.size() == 0) {
            return;
        }
        // 按照预览顺序添加压缩的图片路径
        CacheData.getPhotosGenePath().clear();
        for (String previewPhoto : CacheData.getPhotosPreviewPath()) {
            String compressPhoto = CacheData.getOriginalPhoto2compressPhotoMap().get(previewPhoto);
            if (compressPhoto == null) {
                continue;
            }
            try {
                // 异常的空文件，过滤掉
                if (new File(compressPhoto).length() == 0) {
                    continue;
                }
            } catch (Throwable e) {
                continue;
            }

            setPhotoFileInfo(compressPhoto);
            CacheData.getPhotosGenePath().add(compressPhoto);
        }
    }

    public static void setPhotoFileInfo(String path) {
        try {
            // 如果是压缩图片先找好原始图片路径
            String originalPhoto = CacheData.getCompressPhoto2OriginalPhotoMap().get(path);
            if (originalPhoto != null) {
                // 原始图片路径已有信息的,将原始图片的信息直接缓存
                PhotoFileInfo photoFileInfo = CacheData.getPhotosFileInfoMap().get(originalPhoto);
                if (photoFileInfo != null) {
                    CacheData.getPhotosFileInfoMap().put(path, photoFileInfo);
                    return;
                }
            }

            FileTime fileTime = Files.readAttributes(Paths.get(path), BasicFileAttributes.class).creationTime();
            long timestamp = fileTime.toMillis();
            LocalDateTime createTime = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
            String fileFullName = path.substring(path.lastIndexOf(File.separator) + 1);
            String fileFullPath = path.substring(0, path.lastIndexOf(File.separator) + 1);
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
            String fileFormat = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

            if (fileName.contains("_")) {
                String fileName2 = fileName.substring(0, fileName.lastIndexOf("_"));
                fileName = fileName2.substring(0, fileName2.lastIndexOf("_"));
            }

            PhotoFileInfo photoFileInfo = new PhotoFileInfo();
            photoFileInfo.setAbsolutePath(path);
            photoFileInfo.setFormat(fileFormat);
            photoFileInfo.setName(fileName);
            photoFileInfo.setCreateTime(createTime);

            // 如果图片是在压缩路径中的情况，找回该压缩图片对应的原始图片的创建信息
            if (fileFullPath.endsWith("photo2pdf" + File.separator + "previewPhotos" + File.separator)) {
                String originalPhoto2 = CacheData.getCompressPhoto2OriginalPhotoMap().get(path);
                if (originalPhoto2 != null) {
                    FileTime fileTimeOriginal = Files.readAttributes(Paths.get(originalPhoto2), BasicFileAttributes.class).creationTime();
                    long timestampOriginal = fileTimeOriginal.toMillis();
                    LocalDateTime createTimeOriginal = Instant.ofEpochMilli(timestampOriginal).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                    photoFileInfo.setCreateTime(createTimeOriginal);
                }
            }

            CacheData.getPhotosFileInfoMap().put(path, photoFileInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void removeByOffset(int photoOffset) {
        LogUtils.info("pathShouldRemove offset: " + photoOffset);
        // 1. 先直接根据 offset 移除预览和生成的照片
        String remove = CacheData.getPhotosPreviewPath().remove(photoOffset);
        CacheData.getPhotosPath().remove(photoOffset);

        // 需要同步删除两个list中是图片，以避免选择 default排序时删除的照片依旧显示
        // todo 优化 removeIf 同名的图片将会全部删除
        CacheData.getPhotosPathUserSelectOrder().removeIf(remove::equals);
        LogUtils.info("pathShouldRemove path: " + remove);
    }


    public static void clearPreviewPhoto() {
        CacheData.getPhotosPath().clear();
        CacheData.getPhotosPreviewPath().clear();
        CacheData.getPhotosGenePath().clear();
        CacheData.getPhotosPathUserSelectOrder().clear();
        CacheData.getPhotosFileInfoMap().clear();
    }

    public static ArrayList<String> copyList(List<String> src) {
        ArrayList<String> strings = new ArrayList<>();
        for (String s : src) {
            strings.add(s);
        }
        return strings;
    }

    public static String getRandomColor() {
        int length = randomColor.length;
        int offset = Integer.parseInt(String.valueOf(System.currentTimeMillis()).substring(9)) % length;
        return randomColor[offset];
    }

    public static void clearAllView() {
        // 1. 做各种缓存数据的清除
        LocalFileUtils.deleteFolder(LocalFileUtils.mkTempDir("previewPhotos"));
        // 清除图片数据
        CacheData.clearPreviewPhoto();
        // 2. 刷新页面
        GridPane gridPane = CacheData.gridPane;
        ArrayList<String> photosPath = CacheData.getPhotosPreviewPath();
        ViewGridPaneCtrl viewGridPaneCtrl = new ViewGridPaneCtrl();
        viewGridPaneCtrl.photosViewPane(gridPane, photosPath);

        CacheData.setAppStatus(SysConfig.DEFAULT);
        CacheData.refreshStatus();
        CacheData.gc();
        LogUtils.info("clear photos end");
    }

    @Override
    public String toString() {
        String res = null;
        HashMap<String, Object> map = new HashMap<>();

        map.put("isAppRunning", isAppRunning);
        map.put("compressPhotoAmount", compressPhotoAmount);
        map.put("isClick2RemovePhoto", isClick2RemovePhoto);
//        map.put("photosPathUserSelectOrder", photosPathUserSelectOrder);
//        map.put("photosPath", photosPath);
//        map.put("photosPreviewPath", photosPreviewPath);
//        map.put("photosGenePath", photosGenePath);
//        map.put("photosFileInfoMap", photosFileInfoMap);
        map.put("isClick2RemovePdf", isClick2RemovePdf);
        map.put("toFormat", toFormat);
        map.put("compressRatio", compressRatio);
        map.put("scale", scale);
        map.put("width", width);
        map.put("height", height);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            res = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return res;
    }
    // =============================================================


    public static float getCompressPDFPhotoRatio() {
        return compressPDFPhotoRatio;
    }

    public static void setCompressPDFPhotoRatio(float compressPDFPhotoRatio) {
        CacheData.compressPDFPhotoRatio = compressPDFPhotoRatio;
    }

    public static HashMap<String, String> getHeic2convertPhotoMap() {
        return heic2convertPhotoMap;
    }

    public static void setHeic2convertPhotoMap(HashMap<String, String> heic2convertPhotoMap) {
        CacheData.heic2convertPhotoMap = heic2convertPhotoMap;
    }

    public static ArrayList<String> getExperimentalPhotos() {
        return experimentalPhotos;
    }

    public static void setExperimentalPhotos(ArrayList<String> experimentalPhotos) {
        CacheData.experimentalPhotos = experimentalPhotos;
    }

    public static String getToFormat() {
        return toFormat;
    }

    public static void setToFormat(String toFormat) {
        CacheData.toFormat = toFormat;
    }

    public static float getCompressRatio() {
        return compressRatio;
    }

    public static void setCompressRatio(float compressRatio) {
        CacheData.compressRatio = compressRatio;
    }

    public static float getScale() {
        return scale;
    }

    public static void setScale(float scale) {
        CacheData.scale = scale;
    }

    public static int getWidth() {
        return width;
    }

    public static void setWidth(int width) {
        CacheData.width = width;
    }

    public static int getHeight() {
        return height;
    }

    public static void setHeight(int height) {
        CacheData.height = height;
    }

    public static ArrayList<String> getMergePdfPath() {
        return mergePdfPath;
    }

    public static void setMergePdfPath(ArrayList<String> mergePdfPath) {
        CacheData.mergePdfPath = mergePdfPath;
    }

    public static String getAppStatus() {
        return appStatus;
    }

    public static void setAppStatus(String appStatus) {
        CacheData.appStatus = appStatus;
    }

    public static ArrayList<String> getPhotosPath() {
        return photosPath;
    }

    public static void setPhotosPath(ArrayList<String> photosPath) {
        CacheData.photosPath = photosPath;
    }

    public static ArrayList<String> getPhotosPreviewPath() {
        return photosPreviewPath;
    }

    public static void setPhotosPreviewPath(ArrayList<String> photosPreviewPath) {
        CacheData.photosPreviewPath = photosPreviewPath;
    }

    public static ArrayList<String> getPhotosGenePath() {
        return photosGenePath;
    }

    public static void setPhotosGenePath(ArrayList<String> photosGenePath) {
        CacheData.photosGenePath = photosGenePath;
    }

    public static ArrayList<String> getPhotosCompressPathAndFullName() {
        return photosCompressPathAndFullName;
    }

    public static void setPhotosCompressPathAndFullName(ArrayList<String> photosCompressPathAndFullName) {
        CacheData.photosCompressPathAndFullName = photosCompressPathAndFullName;
    }

    public static HashMap<String, PhotoFileInfo> getPhotosFileInfoMap() {
        return photosFileInfoMap;
    }

    public static void setPhotosFileInfoMap(HashMap<String, PhotoFileInfo> photosFileInfoMap) {
        CacheData.photosFileInfoMap = photosFileInfoMap;
    }

    public static HashMap<String, String> getCompressPhoto2OriginalPhotoMap() {
        return compressPhoto2OriginalPhotoMap;
    }

    public static void setCompressPhoto2OriginalPhotoMap(HashMap<String, String> compressPhoto2OriginalPhotoMap) {
        CacheData.compressPhoto2OriginalPhotoMap = compressPhoto2OriginalPhotoMap;
    }

    public static HashMap<String, String> getOriginalPhoto2compressPhotoMap() {
        return originalPhoto2compressPhotoMap;
    }

    public static void setOriginalPhoto2compressPhotoMap(HashMap<String, String> originalPhoto2compressPhotoMap) {
        CacheData.originalPhoto2compressPhotoMap = originalPhoto2compressPhotoMap;
    }

    public static ArrayList<String> getPhotosPathUserSelectOrder() {
        return photosPathUserSelectOrder;
    }

    public static void setPhotosPathUserSelectOrder(ArrayList<String> photosPathUserSelectOrder) {
        CacheData.photosPathUserSelectOrder = photosPathUserSelectOrder;
    }
}
