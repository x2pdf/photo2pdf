package com.logan.model;

import java.io.Serializable;

/**
 * @author Logan Qin
 * @date 2021/12/21 16:29
 */
public class Key implements Serializable {
    private static final long serialVersionUID = 1L;

    // 签发方信息
    private String issuer = "";
    // yyyy-MM-dd HH:mm:ss
    private String issuanceTime = "";
    private String version = "";

    // 授予的权限
    private String keyFor = "";
    private String keyType = "";

    // 权限的限制
    // yyyy-MM-dd HH:mm:ss
    private String expireTime = "";
    private String checkCode = "";

    // notes
    private String notes = "";


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIssuanceTime() {
        return issuanceTime;
    }

    public void setIssuanceTime(String issuanceTime) {
        this.issuanceTime = issuanceTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getKeyFor() {
        return keyFor;
    }

    public void setKeyFor(String keyFor) {
        this.keyFor = keyFor;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
