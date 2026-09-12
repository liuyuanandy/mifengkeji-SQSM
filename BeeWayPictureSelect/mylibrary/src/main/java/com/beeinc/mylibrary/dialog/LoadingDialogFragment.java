package com.beeinc.mylibrary.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.beeinc.mylibrary.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class LoadingDialogFragment extends DialogFragment {
    public long lasttimeShow;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        super.onCreateView(inflater, container, savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无标题栏
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_loading, null);
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onStart() {
        super.onStart();
        setDialog();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {//viewpager+fragment 时会执行此方法
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            //可以调用刷新数据 或进行控件缩放
        }
        Log.e("--------------->", "ViewPagerFragment1 setUserVisibleHint"+isVisibleToUser);
    }
    @Override
    public void onHiddenChanged(boolean hidden) {//transcation.hide和transcation.show会执行此方法
        super.onHiddenChanged(hidden);
        if (!isHidden()) {
            //可以调用刷新数据 或进行控件缩放
        }
        Log.e("--------------->", "ViewPagerFragment1 onHiddenChanged" + hidden);
    }
    //设置参数
    private void setDialog(){
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.dimAmount = 0.2f;//设置dialog以外背景的透明度
        window.setAttributes(params);
        setCancelable(false);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            Class c=Class.forName("android.support.v4.app.DialogFragment");
            Constructor con = c.getConstructor();
            Object obj = con.newInstance();
            Field dismissed = c.getDeclaredField("mDismissed");
            dismissed.setAccessible(true);
            dismissed.set(obj,false);
            Field shownByMe = c.getDeclaredField("mShownByMe");
            shownByMe.setAccessible(true);
            shownByMe.set(obj,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }
}
