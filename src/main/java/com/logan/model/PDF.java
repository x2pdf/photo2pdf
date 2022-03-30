package com.logan.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Logan Qin
 * @date 2021/12/16 15:11
 */
public class PDF implements Serializable {
    private static final long serialVersionUID = 1L;

    // 生成的pdf是否需要封面页
    private String isNeedCoverPage = "Y";
    // 生成的pdf是否需要总结页面
    private String isNeedSummaryPage = "Y";
    // 生成的pdf中图片是否全部铺满页面
    private String isFullCover = "N";

    private String title = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now());
    private String subTitle;
    private String desc;

    private String coverPhoto;

    private String summaryPhoto;
    private String summaryDesc;


    @Override
    public String toString() {
        return "PDF{" +
                "isNeedCoverPage='" + isNeedCoverPage + '\'' +
                ", isNeedSummaryPage='" + isNeedSummaryPage + '\'' +
                ", isFullCover='" + isFullCover + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", desc='" + desc + '\'' +
                ", coverPhoto='" + coverPhoto + '\'' +
                ", summaryPhoto='" + summaryPhoto + '\'' +
                ", summaryDesc='" + summaryDesc + '\'' +
                '}';
    }

    public String getIsFullCover() {
        return isFullCover;
    }

    public void setIsFullCover(String isFullCover) {
        this.isFullCover = isFullCover;
    }

    public String getSummaryPhoto() {
        return summaryPhoto;
    }

    public void setSummaryPhoto(String summaryPhoto) {
        this.summaryPhoto = summaryPhoto;
    }

    public String getSummaryDesc() {
        return summaryDesc;
    }

    public void setSummaryDesc(String summaryDesc) {
        this.summaryDesc = summaryDesc;
    }

    public String getIsNeedCoverPage() {
        return isNeedCoverPage;
    }

    public void setIsNeedCoverPage(String isNeedCoverPage) {
        this.isNeedCoverPage = isNeedCoverPage;
    }

    public String getIsNeedSummaryPage() {
        return isNeedSummaryPage;
    }

    public void setIsNeedSummaryPage(String isNeedSummaryPage) {
        this.isNeedSummaryPage = isNeedSummaryPage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }
}
