package com.yuci.okhttp.rxnet.interceptor;

import android.util.Log;

import com.yuci.okhttp.rxnet.utils.Constant;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hp on 2017/5/22.
 */
public class RquestInterceptor<T extends Map<String, Object>> implements Interceptor {
    private T t;

    public RquestInterceptor(T t) {
        this.t = t;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(getBuilder(chain).build());
    }

    protected Request.Builder getBuilder(Chain chain) {
        Request request = chain.request();
        String s = request.url().toString();    //获取URL
        String method = request.method();

        Log.e("---------->","url = "+s);
        if (null != t && t.size() > 0) {
            if(method.equals("POST")){
                request = initPostBody(request);//添加参数
            }
            if(method.equals("GET")){
                request = initGetBody(request);//添加参数
            }
        }
        Request.Builder builder = setHeader(request.newBuilder());  //设置头部
        return builder;
    }

    /**
     * 设置参数
     * @param request
     * @return
     */
    protected Request initPostBody(Request request) {
        FormBody.Builder builder = new FormBody.Builder();
        Set<String> strings = this.t.keySet();
        for (String body : strings) {
            builder.addEncoded(body, this.t.get(body) + "");
            Log.e("---------->okhttp",body+"="+this.t.get(body));
        }
        request = request.newBuilder().post(builder.build()).build();
        return request;
    }
    /**
     * 设置参数
     * @param request
     * @return
     */
    protected Request initGetBody(Request request) {
        // 添加新的参数
        HttpUrl.Builder builder = request.url().newBuilder();
        Set<String> strings = this.t.keySet();
        for (String body : strings) {
            builder.addQueryParameter(body, ""+this.t.get(body));
            Log.e("---------->okhttp",body+"="+this.t.get(body));
        }
        request = request.newBuilder().url(builder.build()).build();
        return request;
    }

    /**
     * 设置头部
     * @param header
     * @return
     */
    protected  Request.Builder setHeader(Request.Builder header) {
        header.addHeader("platform", "android");//
        header.addHeader("language", "en");//
        if(Constant.getToken()!=null&&!Constant.getToken().equals("")){
            header.addHeader("Authorization","Bearer "+Constant.getToken());
        }
        return header;
    }

}
