package com.beeinc.mylibrary.scale;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class FrameScaleUtil {
    //缩放类型
    public static final int
            //基本缩放
            TYPE_BASIC = 1,
    //纯文本缩放
    TYPE_TEXT = 2,
    //文本view缩放
    TYPE_TEXT_VIEW = 3;

    //缩放方式
    public static final int
            //X轴左对齐
            X_LEFT = 1,
    //X轴右对齐
    X_RIGHT = 2,
    //X轴居中
    X_CENTER = 3,
    //Y轴上对齐
    Y_TOP = 4,
    //Y轴下对齐
    Y_BOTTOM = 5,
    //Y轴居中
    Y_CENTER = 6;

    /**
     * 缩放  使用控件自身的LayoutParams
     * @param    views    缩放view集合
     * @param    typeX    X轴缩放方式
     * @param    typeY    Y轴缩放方式
     * @param    type    缩放类型
     */
    public static void scale(ArrayList<View> views, final int typeX, final int typeY, final int type) {
        //遍历所有view进行缩放
        for (final View view : views) {
            Log.e("控件类型", view.getClass().getSimpleName());
            view.post(new Runnable() {
                @Override
                public void run() {
                    //判断缩放类型
                    switch (type) {
                        //基本
                        case FrameScaleUtil.TYPE_BASIC:
                            FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) view.getLayoutParams();
                            p.width = (int) Math.ceil(view.getWidth() * ScreenUtil.SCALE_ZOOM_X);
                            p.height = (int) Math.ceil(view.getHeight() * ScreenUtil.SCALE_ZOOM_Y);
                            p.leftMargin = (int) Math.ceil(view.getLeft() * ScreenUtil.SCALE_FLOAT_X + ScreenUtil.NUM_REVISE_X * FrameScaleUtil.getNum(typeX));
                            p.topMargin = (int) Math.ceil(view.getTop() * ScreenUtil.SCALE_FLOAT_Y + ScreenUtil.NUM_REVISE_Y * FrameScaleUtil.getNum(typeY));
                            view.setLayoutParams(p);
                            break;
                        //纯文本
                        case FrameScaleUtil.TYPE_TEXT:
                            //缩放大小(PX)
                            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, ((TextView) view).getTextSize() * ScreenUtil.SCALE_ZOOM_X);
                            FrameLayout.LayoutParams p1 = (FrameLayout.LayoutParams) view.getLayoutParams();
                            p1.width =  ViewGroup.LayoutParams.WRAP_CONTENT;
                            p1.height =  ViewGroup.LayoutParams.WRAP_CONTENT;

                            p1.leftMargin = (int) Math.ceil(p1.leftMargin * ScreenUtil.SCALE_FLOAT_X + ScreenUtil.NUM_REVISE_X * FrameScaleUtil.getNum(typeX));
                            p1.topMargin = (int) Math.ceil(p1.topMargin * ScreenUtil.SCALE_FLOAT_Y + ScreenUtil.NUM_REVISE_Y * FrameScaleUtil.getNum(typeY));
                            view.setLayoutParams(p1);
                            break;
                        //文本view
                        case FrameScaleUtil.TYPE_TEXT_VIEW:
                            //缩放大小(PX)
                            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, ((TextView) view).getTextSize() * ScreenUtil.SCALE_ZOOM_X);
                            FrameLayout.LayoutParams p2 = (FrameLayout.LayoutParams) view.getLayoutParams();
                            p2.width = (int) Math.ceil(view.getWidth() * ScreenUtil.SCALE_ZOOM_X);
                            p2.height = (int) Math.ceil(view.getHeight() * ScreenUtil.SCALE_ZOOM_Y);
                            p2.leftMargin = (int) Math.ceil(view.getLeft() * ScreenUtil.SCALE_FLOAT_X + ScreenUtil.NUM_REVISE_X * FrameScaleUtil.getNum(typeX));
                            p2.topMargin = (int) Math.ceil(view.getTop()* ScreenUtil.SCALE_FLOAT_Y + ScreenUtil.NUM_REVISE_Y * FrameScaleUtil.getNum(typeY));
                            view.setLayoutParams(p2);
                            break;
                    }
                }
            });
        }
    }
    public static void scale(ArrayList<View> views, int typeX, int typeY) {
        FrameScaleUtil.scale(views, typeX, typeY, FrameScaleUtil.TYPE_BASIC);
    }

    public static void scaleView(View view, int width, int height, int left, int top){
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams((int) Math.ceil(width* ScreenUtil.SCALE_ZOOM_X),(int) Math.ceil(height* ScreenUtil.SCALE_ZOOM_X));
        p.leftMargin = (int) Math.ceil(left* ScreenUtil.SCALE_FLOAT_X + ScreenUtil.NUM_REVISE_X * FrameScaleUtil.getNum(FrameScaleUtil.X_LEFT));
        p.topMargin = (int) Math.ceil(top* ScreenUtil.SCALE_FLOAT_Y + ScreenUtil.NUM_REVISE_Y * FrameScaleUtil.getNum(FrameScaleUtil.Y_TOP));
        view.setLayoutParams(p);
    }
    public static void scaleTextView(TextView textView, int width, int height, int left, int top, int textSize){
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize* ScreenUtil.SCALE_ZOOM_X);
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams((int) Math.ceil(width* ScreenUtil.SCALE_ZOOM_X),(int) Math.ceil(height* ScreenUtil.SCALE_ZOOM_X));
        p.leftMargin = (int) Math.ceil(left* ScreenUtil.SCALE_FLOAT_X + ScreenUtil.NUM_REVISE_X * FrameScaleUtil.getNum(FrameScaleUtil.X_LEFT));
        p.topMargin = (int) Math.ceil(top* ScreenUtil.SCALE_FLOAT_Y + ScreenUtil.NUM_REVISE_Y * FrameScaleUtil.getNum(FrameScaleUtil.Y_TOP));
        textView.setLayoutParams(p);
    }


    /**
     * 设置布局
     *
     * @param view view
     * @param w    宽
     * @param h    高
     * @param x    X坐标
     * @param y    Y坐标
     * @param    typeX        X轴缩放类型
     * @param    typeY        Y轴缩放类型
     */
    public static void setLayout(View view, int w, int h, int x, int y, int typeX, int typeY) {
        FrameLayout.LayoutParams p= new FrameLayout.LayoutParams(
                w == ViewGroup.LayoutParams.FILL_PARENT ? ViewGroup.LayoutParams.FILL_PARENT : w == ViewGroup.LayoutParams.WRAP_CONTENT ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) Math.ceil(w * ScreenUtil.SCALE_ZOOM_X),
                h == ViewGroup.LayoutParams.FILL_PARENT ? ViewGroup.LayoutParams.FILL_PARENT : h == ViewGroup.LayoutParams.WRAP_CONTENT ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) Math.ceil(h * ScreenUtil.SCALE_ZOOM_Y));
                p.leftMargin = (int) Math.ceil(x * ScreenUtil.SCALE_FLOAT_X + ScreenUtil.NUM_REVISE_X * FrameScaleUtil.getNum(typeX));
                p.rightMargin = (int) Math.ceil(y * ScreenUtil.SCALE_FLOAT_Y + ScreenUtil.NUM_REVISE_Y * FrameScaleUtil.getNum(typeY));

        //缩放
        view.setLayoutParams(p);
    }

    /**
     * 获取实际宽
     *
     * @param w 标准宽
     * @return
     */
    public static float getW(float w) {
        return w * ScreenUtil.SCALE_ZOOM_X;
    }

    /**
     * 获取实际高
     *
     * @param h 标准高
     * @return
     */
    public static float getH(float h) {
        return h * ScreenUtil.SCALE_ZOOM_Y;
    }

    /**
     * 获取实际X坐标
     *
     * @param x     标准X坐标
     * @param typeX 缩放方式
     * @return
     */
    public static int getX(float x, int typeX) {
        return (int) Math.ceil(x * ScreenUtil.SCALE_FLOAT_X + ScreenUtil.NUM_REVISE_X * FrameScaleUtil.getNum(typeX));
    }

    public static int getX(float x) {
        return FrameScaleUtil.getX(x, FrameScaleUtil.X_CENTER);
    }


    /**
     * 获取标准X坐标
     *
     * @param x     实际X坐标
     * @param typeX 缩放方式
     * @return
     */
    public static int getStandardX(float x, int typeX) {
        return (int) Math.floor((x - ScreenUtil.NUM_REVISE_X * FrameScaleUtil.getNum(typeX)) / ScreenUtil.SCALE_FLOAT_X);
    }

    public static int getStandardX(float x) {
        return FrameScaleUtil.getStandardX(x, FrameScaleUtil.X_CENTER);
    }

    /**
     * 获取实际Y坐标
     *
     * @param y     标准Y坐标
     * @param typeY 缩放方式
     * @return
     */
    public static int getY(float y, int typeY) {
        return (int) Math.ceil(y * ScreenUtil.SCALE_FLOAT_Y + ScreenUtil.NUM_REVISE_Y * FrameScaleUtil.getNum(typeY));
    }

    public static int getY(float y) {
        return FrameScaleUtil.getY(y, FrameScaleUtil.Y_TOP);
    }


    /**
     * 获取标准Y坐标
     *
     * @param y     实际Y坐标
     * @param typeY 缩放方式
     * @return
     */
    public static int getStandardY(float y, int typeY) {
        return (int) Math.floor((y - ScreenUtil.NUM_REVISE_Y * FrameScaleUtil.getNum(typeY)) / ScreenUtil.SCALE_FLOAT_Y);
    }

    public static int getStandardY(float y) {
        return FrameScaleUtil.getStandardY(y, FrameScaleUtil.Y_TOP);
    }

    /**
     * 获取缩放参数
     *
     * @param type 缩放方式
     * @return
     */
    public static float getNum(int type) {
        float num = 1.0f;

        switch (type) {
            case FrameScaleUtil.X_LEFT:
            case FrameScaleUtil.Y_TOP:
                num = 0.0f;
                break;
            case FrameScaleUtil.X_RIGHT:
            case FrameScaleUtil.Y_BOTTOM:
                num = 1.0f;
                break;
            case FrameScaleUtil.X_CENTER:
            case FrameScaleUtil.Y_CENTER:
                num = 0.5f;
                break;
        }

        return num;
    }

}
