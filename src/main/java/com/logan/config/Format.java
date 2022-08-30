package com.logan.config;

/**
 * author: Logan.qin
 * date: 2022/8/30
 */
public enum Format {
    jpg("jpg", "jpg"),
    jpeg("jpeg", "jpeg"),
    png("png", "png"),
    tif("tif", "tif"),
    wmf("wmf", "wmf"),

    jfif("jfif", "jfif"),
    bmp("bmp", "bmp"),
    gif("gif", "gif"),
    heic("heic", "heic"),
    heif("heif", "heif"),
    ;

    private String value;
    private String desc;
    Format(String value, String desc) {
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
