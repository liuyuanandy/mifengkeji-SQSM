package com.beeinc.mylibrary.bean;

import java.io.Serializable;

public class UploadFile implements Serializable {
    private int TAG_NUM;
    private String filePath;//文件的路径
    boolean isSave;//是否已经保存
    private String name;
    private String picture;//保存后的路径
    private int width;//用户填写的宽高
    private int height;//用户填写的宽高
    private String nameDefault;
    private int widthSuggest;//建议宽度
    private int heightSuggest;//建议宽度

    private CategoryType category;

    public int getTAG_NUM() {
        return TAG_NUM;
    }

    public void setTAG_NUM(int TAG_NUM) {
        this.TAG_NUM = TAG_NUM;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        isSave = save;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getNameDefault() {
        return nameDefault;
    }

    public void setNameDefault(String nameDefault) {
        this.nameDefault = nameDefault;
    }

    public int getWidthSuggest() {
        return widthSuggest;
    }

    public void setWidthSuggest(int widthSuggest) {
        this.widthSuggest = widthSuggest;
    }

    public int getHeightSuggest() {
        return heightSuggest;
    }

    public void setHeightSuggest(int heightSuggest) {
        this.heightSuggest = heightSuggest;
    }

    public CategoryType getCategory() {
        return category;
    }

    public void setCategory(CategoryType category) {
        this.category = category;
    }
}
