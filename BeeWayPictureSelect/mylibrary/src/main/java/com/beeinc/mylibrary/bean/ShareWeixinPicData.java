//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.beeinc.mylibrary.bean;

public class ShareWeixinPicData {
    private String miniPicPath;
    private String picPath;
    private int type;

    public ShareWeixinPicData(int type, String miniPicPath, String picPath) {
        this.type = type;
        this.miniPicPath = miniPicPath;
        this.picPath = picPath;
    }

    public String getMiniPicPath() {
        return this.miniPicPath;
    }

    public void setMiniPicPath(String miniPicPath) {
        this.miniPicPath = miniPicPath;
    }

    public String getPicPath() {
        return this.picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
