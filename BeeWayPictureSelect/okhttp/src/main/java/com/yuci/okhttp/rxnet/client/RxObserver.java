package com.yuci.okhttp.rxnet.client;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.yuci.okhttp.rxnet.base.BaseBean;
import com.yuci.okhttp.rxnet.base.BaseObserver;
import com.google.gson.Gson;

import io.reactivex.disposables.Disposable;


/**
 * Created by hp on 2017/5/22.
 * Observer实现类，实际数据处理在这里进行
 */
public abstract class RxObserver<T> extends BaseObserver<T> {
    private Context context;
    public RxObserver() {
    }

    public RxObserver(Context dialogContext, boolean isShowDialog) {
        super(dialogContext, isShowDialog);
        context = dialogContext;
    }

    int codes[] = {};       //需要弹Toast的code码总汇

    @Override
    public void onNext(T t) {
        super.onNext(t);
        if (t instanceof BaseBean) {
            if (isShowToast(((BaseBean) t).getErrorCode())) {
                Toast.makeText(con, ((BaseBean) t).getErroMessage(), Toast.LENGTH_SHORT).show();
            }


            /**
             * TOKEN_IS_EMPTY(10010, "token is not authenticated"),
             * TOKEN_IS_INVALID_(10020, "Request unauthorized"),
             * IDENTITY_IS_INVALID(10033,"identity unauthorized"),
             * IDENTITY_IS_EMPTY(10034,"identity is not authenticated"),
             */
            int code = ((BaseBean) t).getErrorCode();
//            if(code==10020){
//
//            }else if(code ==10033){
//            }
            try{
                Gson gson = new Gson();
                String s = gson.toJson(((BaseBean) t));
                Log.e("okhttp","http result = "+s);
                gson = null;
                onSuccess(t);
            }catch (Exception e){
                Log.d("CHEN", "RxObserver onSuccess error ");
                onError(e);
            }
        }else{
            Log.d("CHEN", "t not insanceof BaseBean ");
        }

    }

    @Override
    public void onError(Throwable t) {
        super.onError(t);
        onFailure();
        t.printStackTrace();
    }

    /**
     * 判断是否需要弹Toast
     *
     * @param code
     * @return
     */
    private boolean isShowToast(int code) {
        for (int i = 0; i < codes.length; i++) {
            if (codes[i] == code) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
        onDisposable(d);
    }

    public abstract void onDisposable(Disposable d);

    public abstract void onSuccess(T t);

    public abstract void onFailure();
}
