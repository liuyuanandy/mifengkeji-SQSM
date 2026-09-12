//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.beeinc.mylibrary.bean;

import java.util.ArrayList;

public class ImageUploadData {
    private String albumPath;
    private int num;
    private String types;
    private ArrayList<CategoryType> categoryTypes;

    public ImageUploadData(String albumPath, int num, String types) {
        this.albumPath = albumPath;
        this.num = num;
        this.types = types;
    }

    public String getAlbumPath() {
        return this.albumPath;
    }

    public void setAlbumPath(String albumPath) {
        this.albumPath = albumPath;
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getTypes() {
        return this.types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public ArrayList<CategoryType> getCategoryTypes() {
        return this.categoryTypes;
    }

    public void setCategoryTypes(ArrayList<CategoryType> categoryTypes) {
        this.categoryTypes = categoryTypes;
    }
}
