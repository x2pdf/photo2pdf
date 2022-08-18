package com.logan.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logan.model.PDF;
import com.logan.utils.LogUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Logan Qin
 * @date 2021/12/13 14:19
 */

public class GeneParamConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    // pdf 要保存的目标路径，默认为用户桌面路径
    private static PDF pdf = new PDF();
    // pdf summary的图片全名称
    private static String pdfSummaryPhoto = "summary_photo_2022_01.png";
    // 是否需要预览pdf的排版，默认 true
    private static boolean isPreviewPDFLayout = true;
    // 用户是否正在拖动的照片
    private static boolean isUserDragPhoto = false;
    // 用户正在拖动的照片在 photosPath 中的 index
    private static int userDragPhotoOffset = 0;
    // 用户正在拖动的照片在 photosPath 中的 value
    private static String userDragPhoto = "";
    //
    private static float pdfPhotoCompressionQuality = 0.7f;
    // 用户设定是否需要图片的标注
    private static boolean isNeedPhotoMark = false;

    // 获取key的方法，预期写在 github 的说明当中 todo
    private static String keyInfoURL = "https://github.com/x2pdf/photo2pdf";
    // 用户是否有key
    private static boolean isAppHasKey = false;
    // 用户的 key 的过期时间（如果有key），格式： yyyy-MM-dd HH:mm:ss
    private static String appKeyExpireTime = "";


    // 用户是否正在拖动的pdf
    private static boolean isUserDragPdf = false;
    // 用户正在拖动的pdf的绝对路径
    private static String userDragPdf = "";
    // pdf 要保存的目标路径，默认为用户桌面路径
    private static String pdfSavePath = "";
    // 每一页pdf页面的照片排布行数
    private int row = 4;
    // 每一页pdf页面的照片排布列数
    private int column = 2;
    // pdf页面照片的排序方式
    public static String sortPhotosBy = "DATE";
    public static String sortPhotosOrderBy = "ASC";

    private static GeneParamConfig GENE_PARAM_CONFIG;

    private GeneParamConfig() {
        LogUtils.info("GeneParamConfig init");
        File home = FileSystemView.getFileSystemView().getHomeDirectory();
        pdfSavePath = home.getAbsolutePath() + File.separator + "photo2pdf" + File.separator;
        File file = new File(pdfSavePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static GeneParamConfig instance() {
        if (GENE_PARAM_CONFIG == null) {
            GENE_PARAM_CONFIG = new GeneParamConfig();
        }
        return GENE_PARAM_CONFIG;
    }

    public static String getPdfSavePath() {
        // 用户手动输入的情况
        if (!pdfSavePath.endsWith(File.separator)) {
            pdfSavePath = pdfSavePath + File.separator;
        }
        // 解决程序打开后，用户删除了程序创建的文件夹
        File file = new File(pdfSavePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return pdfSavePath;
    }


    @Override
    public String toString() {
        String res = null;
        HashMap<String, Object> map = new HashMap<>();

        map.put("pdf", pdf);
        map.put("pdfSummaryPhoto", pdfSummaryPhoto);
        map.put("isPreviewPDFLayout", isPreviewPDFLayout);
        map.put("isUserDragPhoto", isUserDragPhoto);
        map.put("userDragPhotoOffset", userDragPhotoOffset);
        map.put("userDragPhoto", userDragPhoto);
        map.put("pdfPhotoCompressionQuality", pdfPhotoCompressionQuality);
        map.put("isNeedPhotoMark", isNeedPhotoMark);
        map.put("keyInfoURL", keyInfoURL);
        map.put("isAppHasKey", isAppHasKey);
        map.put("appKeyExpireTime", appKeyExpireTime);
        map.put("isUserDragPdf", isUserDragPdf);
        map.put("userDragPdf", userDragPdf);
        map.put("pdfSavePath", pdfSavePath);
        map.put("row", row);
        map.put("column", column);
        map.put("sortPhotosBy", sortPhotosBy);
        map.put("sortPhotosOrderBy", sortPhotosOrderBy);


        ObjectMapper objectMapper = new ObjectMapper();
        try {
            res = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return res;
    }

    //=================================================

    public static String getKeyInfoURL() {
        return keyInfoURL;
    }

    public static void setKeyInfoURL(String keyInfoURL) {
        GeneParamConfig.keyInfoURL = keyInfoURL;
    }

    public static String getAppKeyExpireTime() {
        return appKeyExpireTime;
    }

    public static void setAppKeyExpireTime(String appKeyExpireTime) {
        GeneParamConfig.appKeyExpireTime = appKeyExpireTime;
    }

    public static boolean isIsUserDragPdf() {
        return isUserDragPdf;
    }

    public static void setIsUserDragPdf(boolean isUserDragPdf) {
        GeneParamConfig.isUserDragPdf = isUserDragPdf;
    }

    public static String getUserDragPdf() {
        return userDragPdf;
    }

    public static void setUserDragPdf(String userDragPdf) {
        GeneParamConfig.userDragPdf = userDragPdf;
    }

    public static boolean isIsAppHasKey() {
        return isAppHasKey;
    }

    public static void setIsAppHasKey(boolean isAppHasKey) {
        GeneParamConfig.isAppHasKey = isAppHasKey;
    }

    public static boolean isIsNeedPhotoMark() {
        return isNeedPhotoMark;
    }

    public static void setIsNeedPhotoMark(boolean isNeedPhotoMark) {
        GeneParamConfig.isNeedPhotoMark = isNeedPhotoMark;
    }

    public static float getPdfPhotoCompressionQuality() {
        return pdfPhotoCompressionQuality;
    }

    public static void setPdfPhotoCompressionQuality(float pdfPhotoCompressionQuality) {
        GeneParamConfig.pdfPhotoCompressionQuality = pdfPhotoCompressionQuality;
    }

    public static PDF getPdf() {
        return pdf;
    }

    public static void setPdf(PDF pdf) {
        GeneParamConfig.pdf = pdf;
    }

    public static String getPdfSummaryPhoto() {
        return pdfSummaryPhoto;
    }

    public static void setPdfSummaryPhoto(String pdfSummaryPhoto) {
        GeneParamConfig.pdfSummaryPhoto = pdfSummaryPhoto;
    }

    public static boolean isIsPreviewPDFLayout() {
        return isPreviewPDFLayout;
    }

    public static void setIsPreviewPDFLayout(boolean isPreviewPDFLayout) {
        GeneParamConfig.isPreviewPDFLayout = isPreviewPDFLayout;
    }

    public static boolean isIsUserDragPhoto() {
        return isUserDragPhoto;
    }

    public static void setIsUserDragPhoto(boolean isUserDragPhoto) {
        GeneParamConfig.isUserDragPhoto = isUserDragPhoto;
    }

    public static int getUserDragPhotoOffset() {
        return userDragPhotoOffset;
    }

    public static void setUserDragPhotoOffset(int userDragPhotoOffset) {
        GeneParamConfig.userDragPhotoOffset = userDragPhotoOffset;
    }

    public static String getUserDragPhoto() {
        return userDragPhoto;
    }

    public static void setUserDragPhoto(String userDragPhoto) {
        GeneParamConfig.userDragPhoto = userDragPhoto;
    }

    public static void setPdfSavePath(String pdfSavePath) {
        GeneParamConfig.pdfSavePath = pdfSavePath;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public static String getSortPhotosBy() {
        return sortPhotosBy;
    }

    public static void setSortPhotosBy(String sortPhotosBy) {
        GeneParamConfig.sortPhotosBy = sortPhotosBy;
    }

    public static String getSortPhotosOrderBy() {
        return sortPhotosOrderBy;
    }

    public static void setSortPhotosOrderBy(String sortPhotosOrderBy) {
        GeneParamConfig.sortPhotosOrderBy = sortPhotosOrderBy;
    }
}
