package com.beeinc.mylibrary.util;

//Unity 调用原生方法
public interface UnityCallNative {
    //unity调用拍照和相册 修图后多图上传
    public void GetMultipleAlbumPath(String saveDir, int imageNum, String types);
}
