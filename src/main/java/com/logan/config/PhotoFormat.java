package com.logan.config;

/**
 * author: Logan.qin
 * date: 2022/8/30
 */
public enum PhotoFormat {
    JPG("jpg", "jpg"),
    JPEG("jpeg", "jpeg"),
    PNG("png", "png"),
    TIF("tif", "tif"),
    WMF("wmf", "wmf"),

    JFIF("jfif", "jfif"),
    BMP("bmp", "bmp"),
    GIF("gif", "gif"),
    HEIC("heic", "heic"),
    HEIF("heif", "heif"),
    JXL("jxl", "jxl"),
    AVIF("avif", "avif"),
    ;

    private String value;
    private String desc;
    PhotoFormat(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
