package com.yuci.okhttp.rxnet.base;


import android.content.Context;

import com.yuci.okhttp.rxnet.client.Rx;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by hp on 2017/5/22.
 * Observer基类
 */
public abstract class BaseObserver<T> extends Rx implements Observer<T> {
    private boolean isShowDialog;
//    private LoadingDialog loadDialog;
    private Context dialogContext;

    public BaseObserver() {

    }

    public BaseObserver(Context dialogContext, boolean isShowDialog ) {
        this.isShowDialog = isShowDialog;
        this.dialogContext = dialogContext;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if(isShowDialog && dialogContext!=null){
//            loadDialog = new LoadingDialog(dialogContext);
//            loadDialog.show();
//            loadDialog.setCancelable(true);
        }
    }

    @Override
    public void onNext(T value) {
        closeDialog();
    }

    @Override
    public void onError(Throwable e) {
        closeDialog();
    }

    @Override
    public void onComplete() {
        closeDialog();
    }
    private void closeDialog(){
//        if(loadDialog !=null ){
//            //            loadDialog.dismissWithAnimation();
//            loadDialog.dismiss();
//            loadDialog=null;
//            Runtime.getRuntime().gc();
//        }
    }
}
