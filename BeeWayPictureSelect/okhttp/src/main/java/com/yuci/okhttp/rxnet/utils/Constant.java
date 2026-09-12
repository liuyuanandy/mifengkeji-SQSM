package com.yuci.okhttp.rxnet.utils;

public class Constant {
    private static String BASE_URL="";
    private static boolean isDebug=true;
    private static String token;
    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

    public static boolean isIsDebug() {
        return isDebug;
    }

    public static void setIsDebug(boolean isDebug) {
        Constant.isDebug = isDebug;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Constant.token = token;
    }
}
