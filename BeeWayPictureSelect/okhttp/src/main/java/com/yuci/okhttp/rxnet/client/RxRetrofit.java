package com.yuci.okhttp.rxnet.client;

import com.yuci.okhttp.rxnet.base.BaseRetrofit;

import java.util.HashMap;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hp on 2017/5/22.
 * 网络访问请求类
 */
public class RxRetrofit extends Rx {

    public static <T> T create(HashMap<String,Object> map, Class<T> reqeustClass){
        return BaseRetrofit.getRetrofit(map).create(reqeustClass);
    }
    public static void request(final Observable observable, RxObserver subscriber){
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
