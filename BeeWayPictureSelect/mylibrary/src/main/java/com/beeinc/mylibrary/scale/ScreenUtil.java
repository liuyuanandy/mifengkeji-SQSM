package com.beeinc.mylibrary.scale;

import android.app.Activity;
import android.graphics.Point;

/**
 * Created by haiye on 2018/4/14.
 */

public class ScreenUtil {
    //标准设备屏宽	1280*720
    public static float SCREEN_W = 1280;
    //标准设备屏高
    public static float SCREEN_H = 720;
    //当前设备屏宽
    public static int SCREEN_THIS_W;
    //当前设备屏高
    public static int SCREEN_THIS_H;
    //X轴缩放比例
    public static float SCALE_ZOOM_X;
    //Y轴缩放比例
    public static float SCALE_ZOOM_Y;

    //X轴浮动参数
    public static float SCALE_FLOAT_X;
    //Y轴浮动参数
    public static float SCALE_FLOAT_Y;

    //X轴修正参数
    public static float NUM_REVISE_X;
    //Y轴修正参数
    public static float NUM_REVISE_Y;
    public static void setLayoutNum(Activity context,boolean isPhone){
        Point outSize = new Point();
        context.getWindowManager().getDefaultDisplay().getRealSize(outSize);
        //当前设备屏宽
        SCREEN_THIS_W = outSize.x;
        //当前设备屏高
        SCREEN_THIS_H = outSize.y;
//        //当前设备屏宽
//        SCREEN_THIS_W = context.getWindowManager().getDefaultDisplay().getWidth();
//        //当前设备屏高
//        SCREEN_THIS_H = context.getWindowManager().getDefaultDisplay().getHeight();

        float x = SCREEN_THIS_W / SCREEN_W;
        float y = SCREEN_THIS_H / SCREEN_H;
        //X轴/Y轴浮动比例    X轴/Y轴缩放比例
        SCALE_FLOAT_X = SCALE_FLOAT_Y = SCALE_ZOOM_X = SCALE_ZOOM_Y = x;
        //X轴/Y轴修正参数
        NUM_REVISE_X = SCREEN_THIS_W - SCALE_FLOAT_X * SCREEN_W;
        NUM_REVISE_Y = SCREEN_THIS_H - SCALE_FLOAT_Y * SCREEN_H;
    }
    public static void setLayoutNum(Activity context) {

        Point outSize = new Point();
        context.getWindowManager().getDefaultDisplay().getRealSize(outSize);
        //当前设备屏宽
        SCREEN_THIS_W = outSize.x;
        //当前设备屏高
        SCREEN_THIS_H = outSize.y;
//        //当前设备屏宽
//        SCREEN_THIS_W = context.getWindowManager().getDefaultDisplay().getWidth();
//        //当前设备屏高
//        SCREEN_THIS_H = context.getWindowManager().getDefaultDisplay().getHeight();

        float x = SCREEN_THIS_W / SCREEN_W;
        float y = SCREEN_THIS_H / SCREEN_H;
        //X轴/Y轴浮动比例    X轴/Y轴缩放比例
        SCALE_FLOAT_X = SCALE_FLOAT_Y = SCALE_ZOOM_X = SCALE_ZOOM_Y = x;
        //X轴/Y轴修正参数
        NUM_REVISE_X = SCREEN_THIS_W - SCALE_FLOAT_X * SCREEN_W;
        NUM_REVISE_Y = SCREEN_THIS_H - SCALE_FLOAT_Y * SCREEN_H;
    }
}
