package com.yuci.okhttp.rxnet.rxurl;
import com.yuci.okhttp.rxnet.base.BaseBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by Jason Chen on 2017/8/1.
 */

public interface RxRequest {
    //获取定位位置信息
    @GET("https://maps.googleapis.com/maps/api/geocode/json?")
    Observable<BaseBean> getGoogleLoationAddress();
}
