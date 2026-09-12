package com.beeinc.SQSB.dialog;

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
import android.widget.TextView;

import com.beeinc.SQSB.R;
import com.beeinc.SQSB.scale.FrameScaleUtil;
import com.beeinc.SQSB.util.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class QuitFixDialog extends DialogFragment {
    public long lasttimeShow;
    private View view;
    private View v_back,v_horizontal,v_vertical;
    private TextView tv_hint_title,tv_hint_content,tv_cancel,tv_quit;
    private ArrayList<View> scaleViews = new ArrayList<>();
    private ArrayList<View> scaleTextViews = new ArrayList<>();
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
        view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_quit_fix, null);
        init();
        return view;
    }
    private void init(){
        getAllViews();
        setScaleViews();
        setListener();
    }
    private void getAllViews(){
        v_back = view.findViewById(R.id.v_back);
        v_horizontal = view.findViewById(R.id.v_horizontal);
        v_vertical = view.findViewById(R.id.v_vertical);
        tv_hint_title = view.findViewById(R.id.tv_hint_title);
        tv_hint_content = view.findViewById(R.id.tv_hint_content);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_quit = view.findViewById(R.id.tv_quit);

    }
    private void setScaleViews(){
        scaleViews.add(v_back);
        scaleViews.add(v_horizontal);
        scaleViews.add(v_vertical);
        scaleTextViews.add(tv_hint_title);
        scaleTextViews.add(tv_hint_content);
        scaleTextViews.add(tv_cancel);
        scaleTextViews.add(tv_quit);
        FrameScaleUtil.scale(scaleViews, FrameScaleUtil.X_CENTER, FrameScaleUtil.Y_TOP);
        FrameScaleUtil.scale(scaleTextViews, FrameScaleUtil.X_CENTER, FrameScaleUtil.Y_TOP, FrameScaleUtil.TYPE_TEXT_VIEW);
    }
    private void setListener(){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                if (id == R.id.tv_cancel) {
                    dismiss();
                } else if (id == R.id.tv_quit) {
                    dismiss();
                    common.finishActivity(getActivity());
                }
            }
        };
        tv_cancel.setOnClickListener(listener);
        tv_quit.setOnClickListener(listener);
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
        setCancelable(true);
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
