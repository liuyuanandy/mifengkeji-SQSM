package com.beeinc.SQSB.bean;

import java.io.Serializable;

public class CategoryType implements Serializable {
    private int sort;
    private String category;
    private String category_id;

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
}
