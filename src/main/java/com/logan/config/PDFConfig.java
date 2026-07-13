package com.logan.config;

public class PDFConfig {
    // pdf加密密码最低要求的长度
    public static int pdfPwdLength = 12;

    /** PDF生成警告阈值(提醒用户确认) */
    public static final int LARGE_FILE_WARNING_THRESHOLD = 50;

    /** PDF提取文件大小限制(10MB) */
    public static final long EXTRACT_PDF_MAX_SIZE = 10_000_000L;

    /** 触发分批合并的最小图片数量(避免内存溢出) */
    public static final int GENE_PDF_MERGE_THRESHOLD = 100;

    /** 每批生成的PDF页数(平衡内存和性能) */
    public static final int GENE_PDF_BATCH_PAGE_SIZE = 20;

    // 生成pdf的图片的数量大于这个值时,就分批次生成pdf再合并pdf
    public static int genePdfByMergeMinAmount = GENE_PDF_MERGE_THRESHOLD;

    // 分批生成pdf的过程中，多少页的pdf作为一个小的pdf
    public static int genePdfByMergePageUnit = GENE_PDF_BATCH_PAGE_SIZE;

}
