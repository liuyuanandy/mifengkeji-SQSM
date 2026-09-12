package com.test;

public class RemapHelper {

    //1.垂直双拼；2.水平双拼；3.四pin
    public static void setRemap(long srcAddress,long mapXAddress,long mapyAddress,int type){
        nativeRemap(srcAddress,mapXAddress,mapyAddress,type);
    }
    //
    public static void setRotate(long srcAddress,long dstAddress,int angle){
        nativeRotate(srcAddress,dstAddress,angle);
    }
    public static void setOverlayImage(long srcAddress,long overlyAddress,int x,int y){
        overlayImage(srcAddress,overlyAddress,x,y);
    }
    private static native void nativeRemap(long srcAddress,long map_x,long map_y,int type);
    private static native void nativeRotate(long srcAddress,long dstAddress,int angle);
    private static native void overlayImage(long srcAddress,long dstAddress,int x,int y);
    static {
        System.loadLibrary("detection_based_tracker");
    }
}
