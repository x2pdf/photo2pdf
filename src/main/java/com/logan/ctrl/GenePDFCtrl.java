package com.logan.ctrl;

import com.logan.config.CacheData;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import com.logan.model.PDF;
import com.logan.model.PDFNotes;
import com.logan.model.PhotoFileInfo;
import com.logan.model.PhotoItem;
import com.logan.utils.*;
import javafx.scene.control.Alert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Logan Qin
 * @date 2021/12/22 14:55
 */


public class GenePDFCtrl {

    public String genePdfAndSave() {
        LogUtils.appStatus();
        long start = System.currentTimeMillis();
        compressPreviewPhoto();
        ArrayList<String> photosPath = CacheData.getPhotosGenePath();
        LogUtils.info("Generate pdf with actual number of photos: " + photosPath.size());
        String saveTo;
        if (photosPath.size() >= SysConfig.genePdfByMergeMinAmount) {
            saveTo = genePdfsAndMergeAndSave();
        } else {
            saveTo = geneSinglePdfAndSave();
        }
        CacheData.getPhotosGenePath().clear();
        long spend = System.currentTimeMillis() - start;
        LogUtils.info("genePdfAndSave total spend(ms): " + spend);
        return saveTo;
    }


    public String geneSinglePdfAndSave() {
        LocalFileUtils.append2Log("gene pdf starting");
        HashMap<String, Object> parameters = this.getGeneData();
        byte[] bytes = geneBy(parameters);
        LocalFileUtils.append2Log("gene pdf end");

        String savePath = GeneParamConfig.getPdfSavePath();
        String save = null;
        String fileFullName = getGenePDFFullName();
        try {
            save = LocalFileUtils.save2Path(bytes, savePath, fileFullName);
        } catch (IOException e) {
            LogUtils.error("save pdf exception. savePath: " + savePath);
            e.printStackTrace();
        }
        return save;
    }


    public String genePdfsAndMergeAndSave() {
        LocalFileUtils.append2Log("genePdfsAndMergeAndSave starting");

        // 1. ???????????????????????????
        ArrayList<String> photosPath = getGenePhotoList();
        ArrayList<ArrayList<String>> splitGenePhotoList = splitGenePhotoList(photosPath);

        // 2. ????????????pdf
        LogUtils.info("splitGenePhotoList size: " + splitGenePhotoList.size());
        ArrayList<String> pdfSavePath = new ArrayList<>();
        for (int i = 0; i < splitGenePhotoList.size(); i++) {

            ArrayList<String> photos = splitGenePhotoList.get(i);
            HashMap<String, Object> parameters = new HashMap<>();
            if (i == 0) {
                if (splitGenePhotoList.size() != 1) {
                    combinePdfInfo(parameters, "Y", "N");
                } else {
                    // ??????????????? ??????pdf
                    combinePdfInfo(parameters, "Y", "Y");
                    PDFNotes pdfNotes = new PDFNotes();
                    parameters.put("pdfGeneInfo", pdfNotes.toString());
                }
            } else if (i == splitGenePhotoList.size() - 1) {
                combinePdfInfo(parameters, "N", "Y");

                PDFNotes pdfNotes = new PDFNotes();
                parameters.put("pdfGeneInfo", pdfNotes.toString());
            } else {
                combinePdfInfo(parameters, "N", "N");
            }

            LogUtils.info("gene single pdf config info: " + JSONUtils.toJson(parameters));

            HashMap<String, Object> photoData = getGenePhotoData(photos);
            parameters.putAll(photoData);
            byte[] bytes = geneBy(parameters);

            String savePath = SysConfig.HISTORY_CACHE_PATH;
            String fileFullName = getGenePDFFullName();

            String save = null;
            try {
                save = LocalFileUtils.save2Path(bytes, savePath, fileFullName);
            } catch (IOException e) {
                LogUtils.error("save single pdf exception. savePath: " + savePath);
                e.printStackTrace();
            }

            pdfSavePath.add(save);
        }


        // 3. merge
        String fileFullName = getGenePDFFullName();
        if (!GeneParamConfig.getPdf().getTitle().equals(LocalDate.now().toString())) {
            fileFullName = GeneParamConfig.getPdf().getTitle() + "_" + fileFullName;
        }
        String savePath = GeneParamConfig.getPdfSavePath() + fileFullName;

        for (int i = 0; i < pdfSavePath.size(); i++) {
            System.out.println("*** pdfSavePath path: " + pdfSavePath.get(i));
        }

        String save = PDFBoxUtils.merge2One(pdfSavePath, savePath);

        // 4. ????????????pdf??????
        for (String s : pdfSavePath) {
            LocalFileUtils.deleteFile(s);
        }

        LocalFileUtils.append2Log("genePdfsAndMergeAndSave pdf end");
        return save;
    }

    public String getGenePDFFullName() {
        String fileFullName = "photo2pdf_gene_" + DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS").format(LocalDateTime.now()) + ".pdf";
        if (!GeneParamConfig.getPdf().getTitle().equals(LocalDate.now().toString())) {
            fileFullName = GeneParamConfig.getPdf().getTitle() + "_" + fileFullName;
        }
        return fileFullName;
    }


    public void compressPreviewPhoto() {
        // ???????????????, ??????????????????????????????????????????
        if (!GeneParamConfig.isIsPreviewPDFLayout() && GeneParamConfig.getPdfPhotoCompressionQuality() == 1.0f) {
            // ?????????????????????????????????????????????
            CacheData.syncCompressPhoto2GeneList(CacheData.getPhotosPreviewPath());
            CacheData.getPhotosCompressPathAndFullName().clear();
            // ??????
            return;
        }

        // 2. ??????????????????????????????
        CacheData.setAppStatus(SysConfig.COMPRESS_FILE);
        CacheData.refreshStatus();
        long start = System.currentTimeMillis();
        CacheData.getPhotosCompressPathAndFullName().clear();
        CompressPhotoCtrl compressPhotoCtrl = new CompressPhotoCtrl();
        compressPhotoCtrl.compressPhotos(CacheData.getPhotosPreviewPath());

        int size = CacheData.getPhotosPreviewPath().size();
        LogUtils.info("Compress photo amount: " + size);
        // ??????????????????????????????
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(30);
        LocalDateTime firstDetectQueueIsZero = null;
        while (true) {
            if (CacheData.compressPhotoAmount.get() == size) {
                // ???????????????????????????????????????????????? List ???????????????????????????????????????????????????
                CacheData.syncCompressPhoto2GeneList(CacheData.getPhotosCompressPathAndFullName());
                // ??????????????????????????????????????????????????????????????????
                CacheData.getPhotosCompressPathAndFullName().clear();
                long spend = System.currentTimeMillis() - start;
                LogUtils.info("compress photo spend(ms): " + spend);
                break;
            }
            if (LocalDateTime.now().compareTo(endTime) > 0) {
                // ????????????????????????????????????????????????List???????????????????????????????????????????????????
                CacheData.syncCompressPhoto2GeneList(CacheData.getPhotosCompressPathAndFullName());
                // ??????????????????????????????????????????????????????????????????
                CacheData.getPhotosCompressPathAndFullName().clear();
                LogUtils.error("Wait for more than 30 minutes to compress the image!");
                break;
            }

            if (firstDetectQueueIsZero == null && SysConfig.asyncPool.getQueue().size() == 0) {
                // ??????????????????????????????????????????????????????????????????????????????
                firstDetectQueueIsZero = LocalDateTime.now();
            }
            if (firstDetectQueueIsZero != null) {
                // ???????????????????????????????????????????????????????????????????????????????????????????????? 1 ??????
                if (LocalDateTime.now().minusMinutes(1).compareTo(firstDetectQueueIsZero) > 0) {
                    // ????????????????????????????????????????????????List???????????????????????????????????????????????????
                    CacheData.syncCompressPhoto2GeneList(CacheData.getPhotosCompressPathAndFullName());
                    // ??????????????????????????????????????????????????????????????????
                    CacheData.getPhotosCompressPathAndFullName().clear();
                    LogUtils.error("The cached task in the thread pool's queue has completed and has been waiting for more than 2 minutes.");
                    firstDetectQueueIsZero = null;
                    break;
                }
            }

            try {
                // ????????????????????????????????? todo
//                CacheData.setAppStatus("Compress " + CacheData.compressPhotoAmount.get() + "/" + size);
//                CacheData.refreshStatus();

                // ????????????????????????
                Thread.sleep(1000);
                LogUtils.info("Compress photo %: " + CacheData.compressPhotoAmount.get() + " / " + size);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        CacheData.compressPhotoAmount.set(0);
        LogUtils.info("compressPhotos end");
    }

    public byte[] geneBy(HashMap<String, Object> parameters) {
        // ?????? pdf ??????
        byte[] bytes = new byte[0];
        InputStream templateInputStream = null;
        try {
            LogUtils.info("gene by jasper starting");
            if (GeneParamConfig.getPdf().getIsFullCover().equals("Y")) {
                parameters.put("isFullCover", "Y");
            } else {
                parameters.put("isFullCover", "N");
            }
            // 1 ??????????????????
            String jasperTempPath = LocalFileUtils.mkTempDir("jasperTemplate");
            parameters.put("SUBREPORT_DIR", jasperTempPath);
            // 2 ?????????????????????
            templateInputStream = new ByteArrayInputStream(LocalFileUtils.load(jasperTempPath + "Main.jasper"));
            // 3 gene report by utils
            bytes = GeneReportUtils.generateReportByte(templateInputStream, parameters, GeneReportUtils.ReportFormat.PDF);
            if (templateInputStream != null) {
                templateInputStream.close();
            }

            LogUtils.info("gene by jasper end");
        } catch (Exception e) {
            LogUtils.error("gene pdf utils exception. info: " + e);
        } catch (Error error) {
            LogUtils.error("gene pdf utils Error. error info:" + error);
            error.printStackTrace();
            if (error instanceof OutOfMemoryError) {
                Alert warning = new Alert(Alert.AlertType.ERROR);
                warning.setTitle("ERROR");
                warning.setContentText(SysConfig.getLang("OutOfMemoryError"));
                warning.showAndWait();
            }
        }

        return bytes;
    }


    public HashMap<String, Object> getGeneData() {
        HashMap<String, Object> parameters = new HashMap<>();

        ArrayList<String> photosPath = CacheData.getPhotosGenePath();
        // ???????????????????????????
        if (GeneParamConfig.getPdfPhotoCompressionQuality() == 1.0f) {
            photosPath = CacheData.getPhotosPath();
        }
        if (GeneParamConfig.sortPhotosBy.equals("")) {
            ArrayList<String> strings = new ArrayList<>();
            ArrayList<String> photosGenePath = CacheData.getPhotosGenePath();
            if (GeneParamConfig.getPdfPhotoCompressionQuality() == 1.0f) {
                // ???????????????????????????
                photosGenePath = CacheData.getPhotosPath();
            }
            for (String path : CacheData.getPhotosPathUserSelectOrder()) {
                String fileFullName = path.substring(path.lastIndexOf(File.separator) + 1);
                for (String pathGene : photosGenePath) {
                    String fileFullNameGene = path.substring(path.lastIndexOf(File.separator) + 1);
                    if (fileFullName.equals(fileFullNameGene)) {
                        strings.add(pathGene);
                    }
                }
            }
            photosPath = strings;
        }

        GeneParamConfig geneParamConfig = GeneParamConfig.instance();
        PDF pdf = GeneParamConfig.getPdf();

        String assetPath = LocalFileUtils.mkTempDir("asset");
        pdf.setSummaryPhoto(assetPath + GeneParamConfig.getPdfSummaryPhoto());
        pdf.setSummaryDesc(summaryDesc);

        parameters.put("isNeedCoverPage", pdf.getIsNeedCoverPage());
        parameters.put("isNeedSummaryPage", pdf.getIsNeedSummaryPage());

        parameters.put("title", pdf.getTitle());
        parameters.put("subtitle", pdf.getSubTitle());
        parameters.put("desc", pdf.getDesc());
        parameters.put("coverPhoto", pdf.getCoverPhoto());


        PDFNotes pdfNotes = new PDFNotes();
        parameters.put("pdfGeneInfo", pdfNotes.toString());

        parameters.put("summaryPhoto", pdf.getSummaryPhoto());
        parameters.put("summaryDesc", pdf.getSummaryDesc());

        LogUtils.info("gene pdf config info: " + JSONUtils.toJson(parameters));


        // ?????????????????? 4 * 2
        if (geneParamConfig.getRow() == 4 && geneParamConfig.getColumn() == 2) {
            ArrayList<PhotoItem> photo = new ArrayList<>();
            for (String s : photosPath) {
                setData(geneParamConfig, photo, s);
            }
            parameters.put("fourbytwoList", photo);
        }

        // ?????????????????? 2 * 1
        if (geneParamConfig.getRow() == 2 && geneParamConfig.getColumn() == 1) {
            ArrayList<PhotoItem> photo = new ArrayList<>();
            for (String s : photosPath) {
                setData(geneParamConfig, photo, s);
            }
            parameters.put("twobyoneList", photo);
        }

        // ?????????????????? 4 * 1
        if (geneParamConfig.getRow() == 4 && geneParamConfig.getColumn() == 1) {
            ArrayList<PhotoItem> photo = new ArrayList<>();
            for (String s : photosPath) {
                setData(geneParamConfig, photo, s);
            }
            parameters.put("fourbyoneList", photo);
        }

        // ?????????????????? 8 * 4
        if (geneParamConfig.getRow() == 8 && geneParamConfig.getColumn() == 4) {
            ArrayList<PhotoItem> photo = new ArrayList<>();
            for (String s : photosPath) {
                setData(geneParamConfig, photo, s);
            }
            parameters.put("eightbyfourList", photo);
        }

        // ?????????????????? 1 * 1
        if (geneParamConfig.getRow() == 1 && geneParamConfig.getColumn() == 1) {
            ArrayList<PhotoItem> photo = new ArrayList<>();
            for (String s : photosPath) {
                setData(geneParamConfig, photo, s);
            }
            parameters.put("onebyoneList", photo);
        }

        // ?????????????????? 1 * 2
        if (geneParamConfig.getRow() == 1 && geneParamConfig.getColumn() == 2) {
            ArrayList<PhotoItem> photo = new ArrayList<>();
            for (String s : photosPath) {
                setData(geneParamConfig, photo, s);
            }
            parameters.put("onebytwoList", photo);
        }

        return parameters;
    }


    public ArrayList<ArrayList<String>> splitGenePhotoList(ArrayList<String> photosList) {
        // 1. ?????????????????????
        ArrayList<String> photosPath = CacheData.getPhotosGenePath();
        // ???????????????????????????
        if (GeneParamConfig.getPdfPhotoCompressionQuality() == 1.0f) {
            photosPath = CacheData.getPhotosPath();
        }
        if (GeneParamConfig.sortPhotosBy.equals("")) {
            ArrayList<String> strings = new ArrayList<>();
            ArrayList<String> photosGenePath = CacheData.getPhotosGenePath();
            if (GeneParamConfig.getPdfPhotoCompressionQuality() == 1.0f) {
                // ???????????????????????????
                photosGenePath = CacheData.getPhotosPath();
            }

            for (String path : CacheData.getPhotosPathUserSelectOrder()) {
                String fileFullName = path.substring(path.lastIndexOf(File.separator) + 1);
                for (String pathGene : photosGenePath) {
                    String fileFullNameGene = path.substring(path.lastIndexOf(File.separator) + 1);
                    if (fileFullName.equals(fileFullNameGene)) {
                        strings.add(pathGene);
                    }
                }
            }
            photosPath = strings;
        }

        // ???????????????????????????
        // ????????????pdf??????????????????????????????????????????
        int singlePagePhotos = GeneParamConfig.instance().getColumn() * GeneParamConfig.instance().getRow();
        int pageUnit = SysConfig.genePdfByMergePageUnit;
        ArrayList<ArrayList<String>> resList = new ArrayList<>();
        int splitListSize = singlePagePhotos * pageUnit;
        ArrayList<String> strings = null;
        int i = 0;
        for (String photo : photosPath) {
            if (strings == null) {
                strings = new ArrayList<>();
            }
            strings.add(photo);
            // ??????????????????
            if (strings.size() == splitListSize) {
                resList.add(strings);
                strings = null;
            }
            // ???????????????????????????
            if (i == photosPath.size() - 1) {
                if (strings != null) {
                    resList.add(strings);
                    strings = null;
                }
            }
            i++;
        }

        return resList;
    }

    public ArrayList<String> getGenePhotoList() {
        ArrayList<String> photosPath = CacheData.getPhotosGenePath();
        // ???????????????????????????
        if (GeneParamConfig.getPdfPhotoCompressionQuality() == 1.0f) {
            photosPath = CacheData.getPhotosPath();
        }
        if (GeneParamConfig.sortPhotosBy.equals("")) {
            ArrayList<String> strings = new ArrayList<>();
            ArrayList<String> photosGenePath = CacheData.getPhotosGenePath();
            if (GeneParamConfig.getPdfPhotoCompressionQuality() == 1.0f) {
                // ???????????????????????????
                photosGenePath = CacheData.getPhotosPath();
            }

            for (String path : CacheData.getPhotosPathUserSelectOrder()) {
                String fileFullName = path.substring(path.lastIndexOf(File.separator) + 1);
                for (String pathGene : photosGenePath) {
                    String fileFullNameGene = path.substring(path.lastIndexOf(File.separator) + 1);
                    if (fileFullName.equals(fileFullNameGene)) {
                        strings.add(pathGene);
                    }
                }
            }

            photosPath = strings;
        }

        return photosPath;
    }

    public void combinePdfInfo(HashMap<String, Object> parameters, String isNeedCoverPage, String isNeedSummaryPage) {
        PDF pdf = GeneParamConfig.getPdf();

        String assetPath = LocalFileUtils.mkTempDir("asset");
        pdf.setSummaryPhoto(assetPath + GeneParamConfig.getPdfSummaryPhoto());
        pdf.setSummaryDesc(summaryDesc);


        parameters.put("isNeedCoverPage", isNeedCoverPage);
        parameters.put("isNeedSummaryPage", isNeedSummaryPage);

        parameters.put("title", pdf.getTitle());
        parameters.put("subtitle", pdf.getSubTitle());
        parameters.put("desc", pdf.getDesc());
        parameters.put("coverPhoto", pdf.getCoverPhoto());
        parameters.put("summaryPhoto", pdf.getSummaryPhoto());
        parameters.put("summaryDesc", pdf.getSummaryDesc());

    }


    public HashMap<String, Object> getGenePhotoData(ArrayList<String> photosPath) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("pdfGeneInfo", null);

        GeneParamConfig geneParamConfig = GeneParamConfig.instance();
        // ?????????????????? 4 * 2
        if (geneParamConfig.getRow() == 4 && geneParamConfig.getColumn() == 2) {
            ArrayList<PhotoItem> photo = new ArrayList<>();
            for (String s : photosPath) {
                setData(geneParamConfig, photo, s);
            }
            parameters.put("fourbytwoList", photo);
        }

        // ?????????????????? 2 * 1
        if (geneParamConfig.getRow() == 2 && geneParamConfig.getColumn() == 1) {
            ArrayList<PhotoItem> photo = new ArrayList<>();
            for (String s : photosPath) {
                setData(geneParamConfig, photo, s);
            }
            parameters.put("twobyoneList", photo);
        }

        // ?????????????????? 4 * 1
        if (geneParamConfig.getRow() == 4 && geneParamConfig.getColumn() == 1) {
            ArrayList<PhotoItem> photo = new ArrayList<>();
            for (String s : photosPath) {
                setData(geneParamConfig, photo, s);
            }
            parameters.put("fourbyoneList", photo);
        }

        // ?????????????????? 8 * 4
        if (geneParamConfig.getRow() == 8 && geneParamConfig.getColumn() == 4) {
            ArrayList<PhotoItem> photo = new ArrayList<>();
            for (String s : photosPath) {
                setData(geneParamConfig, photo, s);
            }
            parameters.put("eightbyfourList", photo);
        }

        // ?????????????????? 1 * 1
        if (geneParamConfig.getRow() == 1 && geneParamConfig.getColumn() == 1) {
            ArrayList<PhotoItem> photo = new ArrayList<>();
            for (String s : photosPath) {
                setData(geneParamConfig, photo, s);
            }
            parameters.put("onebyoneList", photo);
        }

        // ?????????????????? 1 * 2
        if (geneParamConfig.getRow() == 1 && geneParamConfig.getColumn() == 2) {
            ArrayList<PhotoItem> photo = new ArrayList<>();
            for (String s : photosPath) {
                setData(geneParamConfig, photo, s);
            }
            parameters.put("onebytwoList", photo);
        }

        return parameters;
    }


    private void setData(GeneParamConfig geneParamConfig, ArrayList<PhotoItem> photoItems, String path) {
        if (photoItems.size() == 0) {
            PhotoItem photoItem = new PhotoItem();
            photoItems.add(photoItem);
        }

        // ????????????mark?????????????????????mark
        PhotoFileInfo photoFileInfo = CacheData.getPhotosFileInfoMap().get(path);
        String name = "";
        if (photoFileInfo != null && GeneParamConfig.isIsNeedPhotoMark()) {
            name = photoFileInfo.getName();
            if (photoFileInfo.getMark() != null && !"".equals(photoFileInfo.getMark())) {
                name = photoFileInfo.getMark();
            }
        }

        PhotoItem photoItem = photoItems.get(photoItems.size() - 1);
        // ?????????????????? 4 * 2
        if (geneParamConfig.getRow() == 4 && geneParamConfig.getColumn() == 2) {
            if (photoItem.getPhoto2() != null) {
                PhotoItem photoItemNew = new PhotoItem();
                photoItems.add(photoItemNew);
                photoItem = photoItems.get(photoItems.size() - 1);
            }

            if (photoItem.getPhoto1() == null) {
                photoItem.setPhotoSize(1);
                photoItem.setPhoto1(path);
                photoItem.setPhoto1Mark(name);
                return;
            } else {
                photoItem.setPhotoSize(0);
                photoItem.setPhoto2(path);
                photoItem.setPhoto2Mark(name);
                return;
            }
        }

        // ?????????????????? 8 * 4
        if (geneParamConfig.getRow() == 8 && geneParamConfig.getColumn() == 4) {
            if (photoItem.getPhoto4() != null) {
                PhotoItem photoItemNew = new PhotoItem();
                photoItems.add(photoItemNew);
                photoItem = photoItems.get(photoItems.size() - 1);
            }

            if (photoItem.getPhoto1() == null) {
                photoItem.setPhotoSize(1);
                photoItem.setPhoto1(path);
                photoItem.setPhoto1Mark(name);
                return;
            } else if (photoItem.getPhotoSize() == 1) {
                photoItem.setPhotoSize(2);
                photoItem.setPhoto2(path);
                photoItem.setPhoto2Mark(name);
                return;
            } else if (photoItem.getPhotoSize() == 2) {
                photoItem.setPhotoSize(3);
                photoItem.setPhoto3(path);
                photoItem.setPhoto3Mark(name);
                return;
            } else {
                photoItem.setPhotoSize(0);
                photoItem.setPhoto4(path);
                photoItem.setPhoto4Mark(name);
                return;
            }
        }

        // ?????????????????? 4 * 1
        if (geneParamConfig.getRow() == 4 && geneParamConfig.getColumn() == 1) {
            if (photoItem.getPhoto1() != null) {
                PhotoItem photoItemNew = new PhotoItem();
                photoItems.add(photoItemNew);
                photoItem = photoItems.get(photoItems.size() - 1);
            }

            if (photoItem.getPhoto1() == null) {
                photoItem.setPhotoSize(1);
                photoItem.setPhoto1(path);
                photoItem.setPhoto1Mark(name);
            }
        }

        // ?????????????????? 1 * 1
        if (geneParamConfig.getRow() == 1 && geneParamConfig.getColumn() == 1) {
            // ??????????????????
            if (photoItem.getPhoto1() != null) {
                PhotoItem photoItemNew = new PhotoItem();
                photoItems.add(photoItemNew);
                photoItem = photoItems.get(photoItems.size() - 1);
            }
            // ?????????????????????
            if (photoItem.getPhoto1() == null) {
                photoItem.setPhotoSize(1);
                photoItem.setPhoto1(path);
                photoItem.setPhoto1Mark(name);
            }
        }

        // ?????????????????? 1 * 2
        if (geneParamConfig.getRow() == 1 && geneParamConfig.getColumn() == 2) {
            // ??????????????????
            if (photoItem.getPhoto1() != null) {
                if (photoItem.getPhoto2() == null) {
                    photoItem.setPhoto2(path);
                    photoItem.setPhoto2Mark(name);
                } else {
                    PhotoItem photoItemNew = new PhotoItem();
                    photoItemNew.setPhoto1(path);
                    photoItemNew.setPhoto1Mark(name);
                    photoItems.add(photoItemNew);
                }
            } else {
                photoItem.setPhoto1(path);
                photoItem.setPhoto1Mark(name);
            }

        }


        // ?????????????????? 2 * 1
        if (geneParamConfig.getRow() == 2 && geneParamConfig.getColumn() == 1) {
            // ??????????????????
            if (photoItem.getPhoto1() != null) {
                PhotoItem photoItemNew = new PhotoItem();
                photoItems.add(photoItemNew);
                photoItem = photoItems.get(photoItems.size() - 1);
            }
            // ?????????????????????
            if (photoItem.getPhoto1() == null) {
                photoItem.setPhotoSize(1);
                photoItem.setPhoto1(path);
                photoItem.setPhoto1Mark(name);
            }

        }

    }

    private static final String summaryDesc = "photo2pdf version 22.01.\n" +
            "For the acquisition, content, use, dissemination of files processed by this application, \n" +
            "and other user personal behaviors not listed yet, the user shall bear the corresponding legal responsibilities, \n" +
            "and this application shall not bear any responsibility.\n" +
            "The rights declared by external libraries or packages that this application relies on belong to the original author.\n" +
            "Please contact if there is any infringement.\n" +
            "All Rights Reserved.\n" +
            "For enquiries, please contact Email: x2pdf@outlook.com\n";

}
