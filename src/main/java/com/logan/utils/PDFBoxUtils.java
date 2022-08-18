package com.logan.utils;

import com.logan.config.CacheData;
import com.logan.config.GeneParamConfig;
import com.logan.config.SysConfig;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * @author Logan Qin
 * @date 2021/12/30 9:04
 */
public class PDFBoxUtils {

    public static String merge2One(ArrayList<String> pdfs, String savePath) {
        try {
            CacheData.gc();
            long start = System.currentTimeMillis();

            ArrayList<String> pdfsNew = encryptPDFFilter(pdfs);
            logSizeInfo(pdfsNew);
            PDFMergerUtility mergePdf = new PDFMergerUtility();
            for (String pdf : pdfsNew) {
                // 过滤掉无效的pdf文件
                if (new File(pdf).length() == 0) {
                    continue;
                }

                mergePdf.addSource(pdf);
            }
            if (savePath == null) {
                savePath = GeneParamConfig.getPdfSavePath()
                        + "merge_" + DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss").format(LocalDateTime.now()) + ".pdf";
            }

            mergePdf.setDestinationFileName(savePath);

            // 3 * 4GB
            MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting.setupMixed(3 * 4194304000L);
            mergePdf.mergeDocuments(memoryUsageSetting);
            long spend = System.currentTimeMillis() - start;
            LogUtils.info("merge2One spend(ms): " + spend);
            return savePath;
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.error("Sorry, merge pdf error!");
            return null;
        }
    }

    public static void logSizeInfo(ArrayList<String> pdfs) {
        try {
            if (pdfs == null) {
                return;
            }
            long allLength = 0L;
            for (String pdf : pdfs) {
                File file = new File(pdf);
                long length = file.length();
                allLength += length;
                LogUtils.info("**** merge pdf file size(kB): " + length / 1000);
            }

            LogUtils.info("**** merge pdf all file size(kB): " + allLength / 1000);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error("Sorry, logSizeInfo Exception!");
        }
    }


    public static ArrayList<String> encryptPDFFilter(ArrayList<String> pdfs) throws IOException {
        ArrayList<String> res = new ArrayList<>();
        for (String pdf : pdfs) {
            String path = null;
            try (final PDDocument document = PDDocument.load(new File(pdf))) {
                path = pdf;
            } catch (InvalidPasswordException e) {
                String name = pdf.substring(pdf.lastIndexOf(File.separator) + 1);
                String password = AlertUtils.getInputText(name + "\n\n" + SysConfig.getLang("Password"));

                String previewPhotosPath = LocalFileUtils.mkTempDir("previewPhotos");
                String newName = "temp_" + name;
                removePDFPassword(pdf, password, previewPhotosPath, newName);

                path = previewPhotosPath + newName;
            } catch (Exception e) {
                LogUtils.error(e.getMessage());
            }

            if (path != null) {
                res.add(path);
            }
        }

        return res;
    }


    public static void removePDFPassword(String pdf, String password, String saveAbsPath, String pdfFileName) throws IOException {
        try (final PDDocument document = PDDocument.load(new File(pdf), password)) {
            document.setAllSecurityToBeRemoved(true);
            document.save(saveAbsPath + pdfFileName);
        }
    }

    // ref: https://pdfbox.apache.org/1.8/cookbook/encryption.html
    public static String encryptPDF(File pdf, String password, String saveAbsPath, String pdfFileName) {
        try {
            LogUtils.info("encrypt pdf starting");
            long start = System.currentTimeMillis();
            if (pdf.length() == 0) {
                return null;
            }
            PDDocument doc = PDDocument.load(pdf);
            // Define the length of the encryption key.
            // Possible values are 40 or 128 (256 will be available in PDFBox 2.0).
            // 当前（2021-12-28）版本为： 2.0.25
            int keyLength = 256;

            // todo 优化设置权限
            AccessPermission ap = new AccessPermission();
            // Disable printing, everything else is allowed
            ap.setCanPrint(false);

            // Owner password (to open the file with all permissions) is "12345"
            // User password (to open the file but with restricted permissions, is empty here)
            StandardProtectionPolicy spp = new StandardProtectionPolicy(password, password, ap);
            spp.setEncryptionKeyLength(keyLength);
            spp.setPermissions(ap);
            spp.setPreferAES(true);
            doc.protect(spp);

            doc.save(saveAbsPath + pdfFileName);
            doc.close();

            LogUtils.info("encrypt pdf end");
            long spend = System.currentTimeMillis() - start;
            LogUtils.info("encryptPdf spend(ms): " + spend);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return saveAbsPath + pdfFileName;
    }

    public static void extractImages(File pdf, String password, String outFold) throws IOException {
        System.gc();
        LogUtils.info("extractImages starting");
        long start = System.currentTimeMillis();
        int i = 1;
        try (final PDDocument document = PDDocument.load(pdf, password)) {
            File file = new File(outFold);
            if (!file.exists()) {
                file.mkdirs();
            }
            PDPageTree list = document.getPages();
            for (PDPage page : list) {
                PDResources pdResources = page.getResources();
                for (COSName name : pdResources.getXObjectNames()) {
                    PDXObject o = pdResources.getXObject(name);
                    if (o instanceof PDImageXObject) {
                        PDImageXObject image = (PDImageXObject) o;
                        String filename = outFold + "image_" + String.format("%05d", i) + "_photo2pdf" + ".png";
                        ImageIO.write(image.getImage(), "png", new File(filename));
                        i++;
                        if (i % 10 == 0) {
                            LogUtils.info("extracted Images amount: " + i);
                            LogUtils.info("extractImages gc starting.");
                            CacheData.gc();
                        }
                    }
                }
            }
        }
        long spend = System.currentTimeMillis() - start;
        LogUtils.info("extractImages size: " + i);
        LogUtils.info("extractImages spend(ms): " + spend);
        System.gc();
    }


    public static void compressPDFImages(File pdf, String password, String outFold, float compressRatio) throws IOException {
        System.gc();
        LogUtils.info("compressPDFImages starting");
        long start = System.currentTimeMillis();
        int i = 1;
        try (final PDDocument document = PDDocument.load(pdf, password)) {
            File file = new File(outFold);
            if (!file.exists()) {
                file.mkdirs();
            }
            ArrayList<String> tempPhotos = new ArrayList<>();
            PDPageTree list = document.getPages();
            for (PDPage page : list) {
                PDResources pdResources = page.getResources();
                for (COSName name : pdResources.getXObjectNames()) {
                    PDXObject o = pdResources.getXObject(name);
                    if (o instanceof PDImageXObject) {
                        try {
                            PDImageXObject image = (PDImageXObject) o;

                            // 优化图片的格式，pdf中图片的原始格式 todo
//                            InputStream inputStream = image.createInputStream();
//                            ImageInputStream iis = ImageIO.createImageInputStream(inputStream);
//                            Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
//                            if (!iter.hasNext()) {
//                                throw new RuntimeException("No readers found!");
//                            }
//                            ImageReader reader = iter.next();
//                            String formatName = reader.getFormatName();
//                            System.out.println("formatName   " + formatName);

                            String filename = outFold + "image_" + String.format("%05d", i) + "_photo2pdf" + ".png";
                            ImageIO.write(image.getImage(), "png", new File(filename));

                            String AbsFileFullName = outFold + "image_compress" + String.format("%05d", i) + "_photo2pdf" + ".jpg";
                            boolean b = PhotoUtils.compressPic(filename, AbsFileFullName, "jpg", compressRatio);
                            if (b) {
                                PDImageXObject replacement_img = PDImageXObject.createFromFile(AbsFileFullName, document);
                                pdResources.put(name, replacement_img);
                            } else {
                                LogUtils.error("compressPDFImages fail, i: " + i);
                            }
                            tempPhotos.add(filename);
                            tempPhotos.add(AbsFileFullName);
                            i++;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            String name = "photo2pdf_compress_pdf_" + pdf.getName();
//            System.out.println("compressRatio: " + compressRatio);
            document.save(outFold + name + ".pdf");

            // 清理数据
            for (String tempPhoto : tempPhotos) {
                LocalFileUtils.deleteFile(tempPhoto);
            }
        }

        long spend = System.currentTimeMillis() - start;
        LogUtils.info("compressPDFImages size: " + i);
        LogUtils.info("compressPDFImages spend(ms): " + spend);
        System.gc();
    }

}
