package com.yuci.okhttp.rxnet.utils;

import java.util.HashMap;

/**
 * Created by hp on 2017/5/3.
 */
public class MapUtils {
    private HashMap<String, Object> map;

    public MapUtils() {
        map = new HashMap<>();
    }

    public static MapUtils Builder() {
        return new MapUtils();
    }

    public MapUtils put(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

    public HashMap<String, Object> builder() {
        return this.map;
    }
}
