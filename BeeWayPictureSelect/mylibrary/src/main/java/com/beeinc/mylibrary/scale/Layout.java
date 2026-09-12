package com.beeinc.mylibrary.scale;

import android.util.TypedValue;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Layout {
	//设置觉对布局参数
	public static AbsoluteLayout.LayoutParams getAbsoluteLayoutParams(int width, int height, int layout_x, int layout_y){

		return new AbsoluteLayout.LayoutParams(
				(int)Math.ceil(width* ScreenUtil.SCALE_FLOAT_X),
				(int)Math.ceil(height* ScreenUtil.SCALE_FLOAT_X),
				(int)Math.ceil(layout_x* ScreenUtil.SCALE_FLOAT_X),
				(int)Math.ceil(layout_y* ScreenUtil.SCALE_FLOAT_X));

	}
	public static RelativeLayout.LayoutParams getRelativeLayoutParams(int width, int height, int leftMargin, int rightMargin){
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
				(int)Math.ceil(width* ScreenUtil.SCALE_FLOAT_X),
				(int)Math.ceil(height* ScreenUtil.SCALE_FLOAT_X));
		p.leftMargin = (int)Math.ceil(leftMargin* ScreenUtil.SCALE_FLOAT_X);
		p.rightMargin = (int)Math.ceil(rightMargin* ScreenUtil.SCALE_FLOAT_X);
		return p;
	}
	//设置padding  文本到控件边界的最小距离距离 用于TextView
	public static void setTextViewPadding(TextView view, int left, int top, int right, int bottom){
		view.setPadding((int)Math.ceil(left* ScreenUtil.SCALE_FLOAT_X),
				(int)Math.ceil(top* ScreenUtil.SCALE_FLOAT_X),
				(int)Math.ceil(right* ScreenUtil.SCALE_FLOAT_X),
				(int)Math.ceil(bottom* ScreenUtil.SCALE_FLOAT_X));
	}
	//设置padding  文本到控件边界的最小距离距离 用于EditView
	public static void setEditTextPadding(EditText view, int left, int top, int right, int bottom){
		view.setPadding((int)Math.ceil(left* ScreenUtil.SCALE_FLOAT_X),
				(int)Math.ceil(top* ScreenUtil.SCALE_FLOAT_X),
				(int)Math.ceil(right* ScreenUtil.SCALE_FLOAT_X),
				(int)Math.ceil(bottom* ScreenUtil.SCALE_FLOAT_X));
	}
	//设置文本大小  Px  用于TextView
	public static void setTextViewSize(TextView view , int size){
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size* ScreenUtil.SCALE_FLOAT_X);
	}
	//设置文本大小  Px  用于EditText
	public static void setEditTextSize(EditText view , int size){
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size* ScreenUtil.SCALE_FLOAT_X);
	}
	public static int getScale(int f){
		return (int) Math.ceil(f* ScreenUtil.SCALE_FLOAT_X);
	}
}
