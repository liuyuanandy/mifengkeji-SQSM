package com.yuci.okhttp.rxnet.base;

import com.yuci.okhttp.rxnet.interceptor.RquestInterceptor;
import com.yuci.okhttp.rxnet.utils.Constant;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by hp on 2017/5/20.
 * OKHttp构造类
 */
public class BaseOKHttp {
    public static OkHttpClient getOk(HashMap<String, Object> map) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(120, TimeUnit.SECONDS)    //读取超时
                .writeTimeout(120, TimeUnit.SECONDS)    //写入超时
                .connectTimeout(5, TimeUnit.SECONDS)//连接超时
                .pingInterval(5, TimeUnit.SECONDS)   //websocket 轮训间隔
                .addInterceptor(new RquestInterceptor<Map<String, Object>>(map));
                //--------------------请求拦截------！！！！！！！！！！！！1
//                .addInterceptor(new RquestInterceptor<Map<String, Object>>(map));
                //--------------------缓存拦截------！！！！！！！！！！！！1
//                .addNetworkInterceptor(new CaCheInterceptor())
//                .addInterceptor(new CaCheInterceptor())
                //--------------------缓存路径------！！！！！！！！！！！！1
//                .cache(new Cache(context.getExternalCacheDir(), 20 * 1024));    //设置缓存文件以及缓存大小，单位bytes
        if (Constant.isIsDebug()) {
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        return builder.build();
    }
}
