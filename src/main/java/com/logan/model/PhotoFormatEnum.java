package com.logan.model;

public enum PhotoFormatEnum {
    JPEG("jpeg"),
    JPG("jpg"),
    PNG("png"),
    JXL("jxl"),
    GIF("gif"),
    BMP("bmp");

    private final String format;

    PhotoFormatEnum(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
