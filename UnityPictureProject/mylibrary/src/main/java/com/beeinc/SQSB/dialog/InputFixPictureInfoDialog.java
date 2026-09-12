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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.beeinc.SQSB.R;
import com.beeinc.SQSB.bean.CategoryType;
import com.beeinc.SQSB.bean.UploadFile;
import com.beeinc.SQSB.scale.FrameScaleUtil;
import com.beeinc.SQSB.scale.Layout;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class InputFixPictureInfoDialog extends DialogFragment {
    public long lasttimeShow;
    private View view;
    private View v_back,v_name,v_type,v_width,v_height;
    private TextView tv_title,tv_name,tv_type,tv_type_content,tv_width,tv_height,tv_cancel,tv_import;
    private EditText et_name,et_width,et_height;
    private ArrayList<View> scaleViews = new ArrayList<>();
    private ArrayList<View> scaleTextViews = new ArrayList<>();

    private UploadFile uploadFile;
    private ArrayList<CategoryType> categoryTypes;
    private PopupWindow p_catgory;
    private boolean isCreate;
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
                R.layout.dialog_fix_import, null);
        init();
        isCreate = true;
        view.invalidate();
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
    private void init(){
        getAllViews();
//        setParams();
        setScaleViews();
        setListener();
    }
    private void getAllViews(){
        v_back = view.findViewById(R.id.v_back);
        v_name = view.findViewById(R.id.v_name);
        v_type = view.findViewById(R.id.v_type);
        v_width = view.findViewById(R.id.v_width);
        v_height = view.findViewById(R.id.v_height);
        tv_title = view.findViewById(R.id.tv_title);
        tv_name = view.findViewById(R.id.tv_name);
        tv_type = view.findViewById(R.id.tv_type);
        tv_width = view.findViewById(R.id.tv_width);
        tv_height = view.findViewById(R.id.tv_height);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_import = view.findViewById(R.id.tv_import);
        et_name = view.findViewById(R.id.et_name);
        tv_type_content = view.findViewById(R.id.tv_type_content);
        et_width = view.findViewById(R.id.et_width);
        et_height = view.findViewById(R.id.et_height);

    }
    private void setParams(){

        et_name.setHint(uploadFile.getNameDefault());
        if(et_name!=null&&!et_name.equals("")){
            et_name.setText(uploadFile.getName());
        }
        tv_type_content.setHint(uploadFile.getCategory().getCategory());
        et_width.setHint(uploadFile.getWidthSuggest()+"");
        et_height.setHint(uploadFile.getHeightSuggest()+"");
        Log.e("----------->","width = "+uploadFile.getWidth());

        if(uploadFile.getWidth()>0){
            et_width.setText(""+uploadFile.getWidth());
        }else{
            et_width.setText("");
        }
        if(uploadFile.getHeight()>0){
            et_height.setText(""+uploadFile.getHeight());
        }else{
            et_height.setText("");
        }
    }
    private void setScaleViews(){
        scaleViews.add(v_back);
        scaleViews.add(v_name);
        scaleViews.add(v_type);
        scaleViews.add(v_width);
        scaleViews.add(v_height);
        scaleTextViews.add(tv_title);
        scaleTextViews.add(tv_name);
        scaleTextViews.add(tv_type);
        scaleTextViews.add(tv_width);
        scaleTextViews.add(tv_height);
        scaleTextViews.add(tv_cancel);
        scaleTextViews.add(tv_import);
        scaleTextViews.add(et_name);
        scaleTextViews.add(tv_type_content);
        scaleTextViews.add(et_width);
        scaleTextViews.add(et_height);

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
                } else if (id == R.id.tv_import) {
                    doImport();
                    dismiss();
                }else if(id == R.id.tv_type_content){
                    startPopupWindow();
                }
            }
        };
        tv_type_content.setOnClickListener(listener);
        tv_cancel.setOnClickListener(listener);
        tv_import.setOnClickListener(listener);
    }
    private void doImport(){
        String name = et_name.getText().toString().trim();
        String widthStr = et_width.getText().toString().trim();
        String heightStr= et_height.getText().toString().trim();
        int width;
        int height;
        if(!name.equals("")){
            uploadFile.setName(name);
        }
        if(!widthStr.equals("")&&Integer.valueOf(widthStr)>0){
            width = Integer.valueOf(widthStr);
            uploadFile.setWidth(width);
        }
        if(!widthStr.equals("")&&Integer.valueOf(widthStr)>0){
            height = Integer.valueOf(heightStr);
            uploadFile.setHeight(height);
        }
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
    public void set(UploadFile uploadFile){
        this.uploadFile = uploadFile;
    }
    public void setCategoryTypes(ArrayList<CategoryType> categoryTypes) {
        this.categoryTypes = categoryTypes;
        if(isCreate){
            setParams();
        }
    }

    //设置参数
    private void setDialog(){
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.width = Layout.getScale(540);
//        params.height = Layout.getScale(580);
        params.dimAmount = 0.2f;//设置dialog以外背景的透明度
        window.setAttributes(params);
        setCancelable(true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
//    @Override
//    public void show(FragmentManager manager, String tag) {
//        try {
//            Class c=Class.forName("android.support.v4.app.DialogFragment");
//            Constructor con = c.getConstructor();
//            Object obj = con.newInstance();
//            Field dismissed = c.getDeclaredField("mDismissed");
//            dismissed.setAccessible(true);
//            dismissed.set(obj,false);
//            Field shownByMe = c.getDeclaredField("mShownByMe");
//            shownByMe.setAccessible(true);
//            shownByMe.set(obj,false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        FragmentTransaction ft = manager.beginTransaction();
//        ft.add(this, tag);
//        ft.commitAllowingStateLoss();
//    }



    private void startPopupWindow(){
        if(p_catgory ==null){
            p_catgory =new PopupWindow(getActivity());
            p_catgory.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            if(categoryTypes.size()>8){
                p_catgory.setHeight(Layout.getScale(35)*8);
            }else{
                p_catgory.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            p_catgory.setOutsideTouchable(true);
            p_catgory.setClippingEnabled(false);
            View v= LayoutInflater.from(getActivity()).inflate(R.layout.category_type,null);
            LinearLayout linear_types = v.findViewById(R.id.linear_types);
            for(int i=0;i<categoryTypes.size();i++){
                View v_line = new View(getActivity());
                v_line.setLayoutParams(new FrameLayout.LayoutParams(Layout.getScale(278), Layout.getScale(1)));
                v_line.setBackgroundColor(getResources().getColor(R.color.color1C1C1C));
                linear_types.addView(v_line);
                TextView tv = new TextView(getActivity());
                LinearLayout.LayoutParams  p_tv = new LinearLayout.LayoutParams(Layout.getScale(278), Layout.getScale(48));
                tv.setLayoutParams(p_tv);
                tv.setText(categoryTypes.get(i).getCategory());
                tv.setTag(categoryTypes.get(i));
                if(i==categoryTypes.size()-1){
                    tv.setBackgroundResource(R.drawable.shape_5);
                }else{
                    tv.setBackgroundColor(getResources().getColor(R.color.color2B2B2B));
                }
                tv.setPadding(Layout.getScale(96), 0, 0, 0);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setTextColor(Color.parseColor("#ffffff"));
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CategoryType categoryType = (CategoryType)v.getTag();
                        uploadFile.setCategory(categoryType);
                        tv_type_content.setText(categoryType.getCategory());
                        p_catgory.dismiss();
                    }
                });
                Layout.setTextViewSize(tv,18);
                linear_types.addView(tv);
            }
            p_catgory.setContentView(v);
            p_catgory.setOutsideTouchable(true);//设置外部能否点击
//            p.setAnimationStyle(R.style.dialogAnim);//设置动画
            p_catgory.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_shape));//必须设置，否则不显示
//            p.setWidth(720);//设置popupwindow 的宽度，有时不能全屏可在这里设置
            p_catgory.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                }
            });
            p_catgory.showAsDropDown(v_type,0,0);//显示在控件的下方
        }else{
            p_catgory.dismiss();
            p_catgory.showAsDropDown(v_type,0,0);//显示在控件的下方
        }
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
    public void onResume(){
        super.onResume();
        setParams();
    }
}
