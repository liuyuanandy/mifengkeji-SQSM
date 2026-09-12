package com.yuci.okhttp.rxnet.base;

/**
 * Created by hp on 2017/5/22.
 */
public class BaseBean<T> {
    private int errorCode;
    private String erroMessage;
    private T data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErroMessage() {
        return erroMessage;
    }

    public void setErroMessage(String erroMessage) {
        this.erroMessage = erroMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
