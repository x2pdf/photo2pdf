package com.logan.utils;


import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Properties;


/**
 * @author Logan Qin
 * @since 2020/4/10 10:52
 */


/**
 * 使用 jaspersoft 的主要工具类
 */

public class GeneReportUtils {

    private static String cachePath = System.getProperty("java.io.tmpdir") + "Jasper" + File.separator;
    private static String cacheNamePrefix = "JasperTemp";


    public enum ReportFormat {
        XLSX,
        DOCX,
        PDF
    }

    /**
     * 适合小文件生成
     *
     * @param templateStream
     * @param parameters
     * @param reportFormat
     * @return
     * @throws JRException
     */
    public static byte[] generateReportByte(InputStream templateStream, HashMap<String, Object> parameters,
                                            ReportFormat reportFormat) throws JRException {
        // 1. 检查参数以及配置默认值
        if (templateStream == null) {
            throw new JRException("error generating futures report, error massage: can't find report template file, template file is null!!");
        }
        HashMap<String, Object> parametersTemp = new HashMap<String, Object>();
        if (parameters != null) {
            parametersTemp = parameters;
        }
        ReportFormat reportFormatTemp = ReportFormat.PDF;
        if (reportFormat != null) {
            reportFormatTemp = reportFormat;
        }
        // 2. 填充模板
        System.gc();
        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parametersTemp, new JREmptyDataSource());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRAbstractExporter exporter = getExporter(reportFormatTemp);
        // 3. 导出结单
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
        System.gc();
        return outputStream.toByteArray();
    }


    private static JRAbstractExporter getExporter(ReportFormat reportFormat) {
        switch (reportFormat) {
            case DOCX: {
                return new JRDocxExporter();
            }
            case XLSX: {
                JRXlsxExporter jrXlsxExporter = new JRXlsxExporter();

                SimpleXlsxReportConfiguration conf = new SimpleXlsxReportConfiguration();
                conf.setWhitePageBackground(false);
                conf.setDetectCellType(true);
                conf.setOnePagePerSheet(false);
                conf.setWrapText(true);

                conf.setRemoveEmptySpaceBetweenRows(true);
                conf.setIgnoreGraphics(true);
                conf.setIgnorePageMargins(true);

                jrXlsxExporter.setConfiguration(conf);
                return jrXlsxExporter;
            }
            case PDF: {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                configuration.setCompressed(false);
                exporter.setConfiguration(configuration);
                return exporter;
            }
            default:
                throw new IllegalArgumentException("Unknown export type " + reportFormat);
        }
    }

}
