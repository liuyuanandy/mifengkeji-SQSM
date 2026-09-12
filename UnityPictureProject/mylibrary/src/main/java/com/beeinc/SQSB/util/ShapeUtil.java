package com.beeinc.SQSB.util;

import android.graphics.drawable.GradientDrawable;

import com.beeinc.SQSB.scale.Layout;

public class ShapeUtil {

    /**
     * @return
     */
    public static GradientDrawable getShape(boolean isStroke,int strokeWidth,int strokeColor,boolean isCorner,int cornerRadius,boolean isSolid,int solidColor){
        GradientDrawable gd = new GradientDrawable();//创建drawable

        if(isStroke){
            gd.setStroke(strokeWidth, strokeColor);
        }
        if(isCorner){
            gd.setCornerRadius(cornerRadius);
        }
        if(isSolid){
            gd.setColor(solidColor);
        }
        return gd;
    }
    public static GradientDrawable getLeftShape(boolean isStroke,int strokeWidth,int strokeColor,boolean isCorner,int cornerRadius,boolean isSolid,int solidColor){
        GradientDrawable gd = new GradientDrawable();//创建drawable

        if(isStroke){
            gd.setStroke(strokeWidth, strokeColor);
        }
        if(isCorner){
            float[] corner = new float[8];
            corner[0] = Layout.getScale(cornerRadius);
            corner[1] = Layout.getScale(cornerRadius);
            corner[2] = 0;
            corner[3] = 0;
            corner[4] = 0;
            corner[5] = 0;
            corner[6] = Layout.getScale(cornerRadius);
            corner[7] = Layout.getScale(cornerRadius);
            gd.setCornerRadii(corner);
        }
        if(isSolid){
            gd.setColor(solidColor);
        }
        return gd;
    }

    public static GradientDrawable getRightShape(boolean isStroke,int strokeWidth,int strokeColor,boolean isCorner,int cornerRadius,boolean isSolid,int solidColor){
        GradientDrawable gd = new GradientDrawable();//创建drawable

        if(isStroke){
            gd.setStroke(strokeWidth, strokeColor);
        }
        if(isCorner){
            float[] corner = new float[8];
            corner[0] = 0;
            corner[1] = 0;
            corner[2] = Layout.getScale(cornerRadius);
            corner[3] = Layout.getScale(cornerRadius);
            corner[4] = Layout.getScale(cornerRadius);
            corner[5] = Layout.getScale(cornerRadius);
            corner[6] = 0;
            corner[7] = 0;
            gd.setCornerRadii(corner);
        }
        if(isSolid){
            gd.setColor(solidColor);
        }
        return gd;
    }

    public static GradientDrawable getTopShape(boolean isStroke,int strokeWidth,int strokeColor,boolean isCorner,int cornerRadius,boolean isSolid,int solidColor){
        GradientDrawable gd = new GradientDrawable();//创建drawable

        if(isStroke){
            gd.setStroke(strokeWidth, strokeColor);
        }
        if(isCorner){
            float[] corner = new float[8];
            corner[0] = Layout.getScale(cornerRadius);
            corner[1] = Layout.getScale(cornerRadius);
            corner[2] = Layout.getScale(cornerRadius);
            corner[3] = Layout.getScale(cornerRadius);
            corner[4] = 0;
            corner[5] = 0;
            corner[6] = 0;
            corner[7] = 0;
            gd.setCornerRadii(corner);
        }
        if(isSolid){
            gd.setColor(solidColor);
        }
        return gd;
    }
    public static GradientDrawable getBottomShape(boolean isStroke,int strokeWidth,int strokeColor,boolean isCorner,int cornerRadius,boolean isSolid,int solidColor){
        GradientDrawable gd = new GradientDrawable();//创建drawable

        if(isStroke){
            gd.setStroke(strokeWidth, strokeColor);
        }
        if(isCorner){
            float[] corner = new float[8];
            corner[0] = 0;
            corner[1] = 0;
            corner[2] = 0;
            corner[3] = 0;
            corner[4] = Layout.getScale(cornerRadius);
            corner[5] = Layout.getScale(cornerRadius);
            corner[6] = Layout.getScale(cornerRadius);
            corner[7] = Layout.getScale(cornerRadius);
            gd.setCornerRadii(corner);
        }
        if(isSolid){
            gd.setColor(solidColor);
        }
        return gd;
    }
}
