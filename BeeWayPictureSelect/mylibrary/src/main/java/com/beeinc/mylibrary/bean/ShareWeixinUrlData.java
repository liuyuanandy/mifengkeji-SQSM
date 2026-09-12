//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.beeinc.mylibrary.bean;

public class ShareWeixinUrlData {
    private int type;
    private String WXSharetitle;
    private String WXSharedescription;
    private String imagepath;
    private String WXShareUrl;

    public ShareWeixinUrlData(int type, String WXSharetitle, String WXSharedescription, String imagepath, String WXShareUrl) {
        this.type = type;
        this.WXSharetitle = WXSharetitle;
        this.WXSharedescription = WXSharedescription;
        this.imagepath = imagepath;
        this.WXShareUrl = WXShareUrl;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getWXSharetitle() {
        return this.WXSharetitle;
    }

    public void setWXSharetitle(String WXSharetitle) {
        this.WXSharetitle = WXSharetitle;
    }

    public String getWXSharedescription() {
        return this.WXSharedescription;
    }

    public void setWXSharedescription(String WXSharedescription) {
        this.WXSharedescription = WXSharedescription;
    }

    public String getImagepath() {
        return this.imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getWXShareUrl() {
        return this.WXShareUrl;
    }

    public void setWXShareUrl(String WXShareUrl) {
        this.WXShareUrl = WXShareUrl;
    }
}
