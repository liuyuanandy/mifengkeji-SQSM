package com.yuci.okhttp.rxnet.base;

import com.yuci.okhttp.rxnet.utils.Constant;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import java.util.HashMap;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hp on 2017/5/20.
 */
public class BaseRetrofit {
    public static Retrofit getRetrofit(HashMap<String, Object> map) {
        return new Retrofit.Builder()
                .baseUrl(Constant.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(BaseOKHttp.getOk(map))
                .build();
    }
}
