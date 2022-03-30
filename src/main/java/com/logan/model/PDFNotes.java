package com.logan.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Logan Qin
 * @date 2021/12/30 11:06
 */
public class PDFNotes implements Serializable {
    private static final long serialVersionUID = 1L;

    private String createDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private String photoAmount = "0";
    private int row = 4;
    // 每一页pdf页面的照片排布列数
    private int column = 2;


    @Override
    public String toString() {
        return createDateTime + "\n"
//                + "photoAmount=" + photoAmount + "\n"
//                + "layout=" + row + " rows x " + column + " columns" + "\n"
                ;
    }

    // ======================================================================
    public String getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(String createDateTime) {
        this.createDateTime = createDateTime;
    }

    public String getPhotoAmount() {
        return photoAmount;
    }

    public void setPhotoAmount(String photoAmount) {
        this.photoAmount = photoAmount;
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
}
