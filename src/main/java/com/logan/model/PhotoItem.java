package com.logan.model;


import java.io.Serializable;

/**
 * @author Logan Qin
 * @date 2021/12/7 16:59
 */


public class PhotoItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private int photoSize = 0;

    private String photo1;
    private String photo1Mark;

    private String photo2;
    private String photo2Mark;


    private String photo3;
    private String photo3Mark;

    private String photo4;
    private String photo4Mark;

    public int getPhotoSize() {
        return photoSize;
    }

    public void setPhotoSize(int photoSize) {
        this.photoSize = photoSize;
    }

    public String getPhoto1() {
        return photo1;
    }

    public void setPhoto1(String photo1) {
        this.photo1 = photo1;
    }

    public String getPhoto1Mark() {
        return photo1Mark;
    }

    public void setPhoto1Mark(String photo1Mark) {
        this.photo1Mark = photo1Mark;
    }

    public String getPhoto2() {
        return photo2;
    }

    public void setPhoto2(String photo2) {
        this.photo2 = photo2;
    }

    public String getPhoto2Mark() {
        return photo2Mark;
    }

    public void setPhoto2Mark(String photo2Mark) {
        this.photo2Mark = photo2Mark;
    }

    public String getPhoto3() {
        return photo3;
    }

    public void setPhoto3(String photo3) {
        this.photo3 = photo3;
    }

    public String getPhoto3Mark() {
        return photo3Mark;
    }

    public void setPhoto3Mark(String photo3Mark) {
        this.photo3Mark = photo3Mark;
    }

    public String getPhoto4() {
        return photo4;
    }

    public void setPhoto4(String photo4) {
        this.photo4 = photo4;
    }

    public String getPhoto4Mark() {
        return photo4Mark;
    }

    public void setPhoto4Mark(String photo4Mark) {
        this.photo4Mark = photo4Mark;
    }
}
