package com.beeinc.SQSB.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class WaterMarkUtil {

    //分享宽度为1600 给图片加文字水印
    public static Bitmap waterMarkAddText(Bitmap bitmap,String code,String specs,String price,String remark){
        int padding = 10;
        int imageWidth = 1600;
        int imageHeight = (int)(bitmap.getHeight()*imageWidth/bitmap.getWidth());
        Bitmap bitmapForShare = Bitmap.createScaledBitmap(bitmap,imageWidth,imageHeight,false);
        bitmap.recycle();
        int width =imageWidth+2*padding;
        int height = imageHeight+2*padding;
        boolean isHaveInfo = true;
        boolean isMark = true;
        if(code.equals("")&&specs.equals("")&&price.equals("")){
            isHaveInfo = false;
        }
        if(remark.equals("")){
            isMark = false;
        }
        Paint paint = new Paint();
        paint.setTextSize(25);
        paint.setColor(Color.parseColor("#333333"));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL); //用于设置字体填充的类型
        String text = "Text";
        Rect rect = new Rect();
        paint.getTextBounds(text,0,text.length(),rect);
        int textHeight = rect.bottom-rect.top;

        if(isHaveInfo){
            height = height+textHeight+padding;
        }
        if(isMark){
            height = height+textHeight+padding;
        }
        Bitmap bitmapNew = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        bitmapNew.eraseColor(Color.parseColor("#ffffff")); // 填充颜色
        Canvas canvas = new Canvas(bitmapNew);
        canvas.drawBitmap(bitmapForShare, padding, padding, paint);
        if(isHaveInfo){
            canvas.drawText("编号:"+code, padding,imageHeight+2*padding+textHeight, paint);
            canvas.drawText("规格:"+specs,padding+250,imageHeight+2*padding+textHeight,paint);
            paint.setColor(Color.parseColor("#cf4444"));
            canvas.drawText("价格:"+price,padding+550,imageHeight+2*padding+textHeight,paint);
        }
        if(isMark){
            paint.setColor(Color.parseColor("#333333"));
            if(!isHaveInfo){
                canvas.drawText("备注:"+remark, padding,imageHeight+2*padding+textHeight, paint);
            }else{
                canvas.drawText("备注:"+remark, padding,imageHeight+3*padding+textHeight+textHeight, paint);
            }
        }
        return bitmapNew;
    }
    //分享宽度为800 给图片加图片水印
    public static Bitmap waterMarkAddImage(Bitmap bitmap,Bitmap mark){
        int padding = 0;
        int imageWidth = 1600;
        int imageHeight = (int)(bitmap.getHeight()*imageWidth/bitmap.getWidth());
        Bitmap bitmapForShare = Bitmap.createScaledBitmap(bitmap,imageWidth,imageHeight,true);
        bitmap.recycle();
        int width =imageWidth+2*padding;
        int height = imageHeight+2*padding;

        Paint paint = new Paint();
        paint.setTextSize(25);
        paint.setColor(Color.parseColor("#333333"));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL); //用于设置字体填充的类型

        Bitmap bitmapNew = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        bitmapNew.eraseColor(Color.parseColor("#ffffff")); // 填充颜色
        Canvas canvas = new Canvas(bitmapNew);
        canvas.drawBitmap(bitmapForShare, padding, padding, paint);
        int scale = 10;//图片与水印宽度的比例
        int markWidth = imageWidth/scale;
        int markHeight = mark.getHeight()*markWidth/mark.getWidth();
        Bitmap markNew = Bitmap.createScaledBitmap(mark,markWidth,markHeight,true);
        mark.recycle();
        canvas.drawBitmap(markNew,width-padding-30-markWidth,height-padding-30-markHeight,paint);
        return bitmapNew;
    }
}
