package com.yuci.okhttp.rxnet.base;

/**
 * Created by hp on 2017/5/22.
 */
public class BaseBeanMusic<T> {
    private int ret;
    private T data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
