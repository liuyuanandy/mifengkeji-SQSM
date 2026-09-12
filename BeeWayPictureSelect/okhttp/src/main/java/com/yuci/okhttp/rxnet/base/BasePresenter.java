package com.yuci.okhttp.rxnet.base;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hp on 2017/5/23.
 */
public abstract class BasePresenter<V extends IBaseView,M extends IBaseModule> {
    protected V view;
    protected M module;
    protected CompositeDisposable mCompositeSubscription;


    public void cancelSubscribe() {
        if(mCompositeSubscription!=null){
            mCompositeSubscription.clear();
        }
    }

    public BasePresenter(V v,M m) {
        this.view = v;
        this.module = m;
        mCompositeSubscription = new CompositeDisposable();
    }
}
