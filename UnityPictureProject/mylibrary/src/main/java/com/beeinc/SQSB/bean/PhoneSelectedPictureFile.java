package com.beeinc.SQSB.bean;

import java.io.Serializable;

public class PhoneSelectedPictureFile implements Serializable {
    private String originalPath;//图片源地址
    private String Q_originalPath;//androidQ文件地址
    private String afterDealPath;//图片矫正后保存地址
    private boolean isDealed;//图片是否已经矫正过

    private boolean isCapture;//是否时调用相机拍摄的
    private String file;//用于返回给Unity 视频或图片文件保存地址
    private String thumb;//用于返回给Unity 缩略图文件
    private int type;//用于返回给Unity 类型
    private boolean isNeedResaveCheck = true;

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getQ_originalPath() {
        return Q_originalPath;
    }

    public void setQ_originalPath(String q_originalPath) {
        Q_originalPath = q_originalPath;
    }

    public String getAfterDealPath() {
        return afterDealPath;
    }

    public void setAfterDealPath(String afterDealPath) {
        this.afterDealPath = afterDealPath;
    }

    public boolean isDealed() {
        return isDealed;
    }

    public void setDealed(boolean dealed) {
        isDealed = dealed;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isCapture() {
        return isCapture;
    }

    public void setCapture(boolean capture) {
        isCapture = capture;
    }

    public boolean isNeedResaveCheck() {
        return isNeedResaveCheck;
    }

    public void setNeedResaveCheck(boolean needResaveCheck) {
        isNeedResaveCheck = needResaveCheck;
    }
}
