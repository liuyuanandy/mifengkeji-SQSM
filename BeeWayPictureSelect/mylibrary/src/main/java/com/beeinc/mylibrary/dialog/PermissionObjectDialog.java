package com.beeinc.mylibrary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.beeinc.mylibrary.R;
import com.beeinc.mylibrary.scale.ScreenUtil;
import com.beeinc.mylibrary.util.common;
import com.yanzhenjie.album.util.StatusBarUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

//权限申请，目的提示
public class PermissionObjectDialog extends Dialog {
    public long lasttimeShow;
    private TextView tv_permission,tv_describe;
    private int type;//0相机权限 1媒体权限 2存储权限
    public PermissionObjectDialog(Context context) {
        super(context, R.style.myDialogTheme);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_permission_objective);
        setAttributes();
        tv_permission = findViewById(R.id.tv_permission);
        tv_describe = findViewById(R.id.tv_describe);
        showContent();
    }

    public void setType(int type) {
        this.type = type;
    }
    private void showContent(){
        String permission;
        String permissionObjective;
        switch (type){
            case 0:
                permission = "相机权限使用说明:";
                permissionObjective = "用于拍照";
                break;
            case 1:
                permission = "图片和视频权限使用说明:";
                permissionObjective = "用于选择相册";
                break;
            case 2:
                permission = "存储权限使用说明:";
                permissionObjective = "用于存取图片";
                break;
            default:
                permission = "相机权限使用说明:";
                permissionObjective = "用于拍照";
                break;
        }
        tv_permission.setText(permission);
        tv_describe.setText(permissionObjective);

    }
    //在setContentView()后调用
    public void setAttributes(){
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int)(ScreenUtil.SCREEN_THIS_W*3/5);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.x = 0;
        params.y = 0;
        getWindow().setAttributes(params);
        getWindow().setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
    }
}
