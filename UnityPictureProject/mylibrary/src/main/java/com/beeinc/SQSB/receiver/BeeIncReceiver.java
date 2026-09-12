package com.beeinc.SQSB.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.beeinc.SQSB.MainActivity;
import com.beeinc.SQSB.activity.PicturesUploadActivity;
import com.beeinc.SQSB.activity.UploadActivity;

public class BeeIncReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.beeinc.album.takpicture")){//第三方相册调用拍照
            int takePictureType = intent.getIntExtra("takePictureType",1);
            if(context instanceof UploadActivity &&takePictureType==6){
                ((UploadActivity)context).startAlbumCapture(takePictureType);
            }
            if(context instanceof PicturesUploadActivity &&takePictureType==8){
                ((PicturesUploadActivity)context).startAlbumCapture(takePictureType);
            }
            if(context instanceof MainActivity){
                ((MainActivity)context).startAlbumCapture(takePictureType);
            }
        }else if(intent.getAction().equals("com.beeinc.album.takpicture.finish")){//第三方相册调用拍照结束
            int takePictureType = intent.getIntExtra("takePictureType",1);
            if(context instanceof UploadActivity &&takePictureType==6){
                ((UploadActivity)context).albumCaptureFinish(intent);
            }
            if(context instanceof PicturesUploadActivity &&takePictureType==8){
                ((PicturesUploadActivity)context).albumCaptureFinish(intent);
            }
            if(takePictureType==1||takePictureType==2||takePictureType==3||takePictureType==4||takePictureType==5||takePictureType==7){
                if(context instanceof MainActivity){
                    ((MainActivity)context).albumCaptureFinish(intent);
                }
            }
        }else if(intent.getAction().equals("com.beeinc.album.takpicture.album")){//第三方相册调用拍照切换到相册
            int takePictureType = intent.getIntExtra("takePictureType",1);
            Log.e("---------->","onReceive takePictureType = "+takePictureType);
            if(context instanceof UploadActivity &&takePictureType==6){
                ((UploadActivity)context).albumCaptureToAlbum();
            }
            if(context instanceof PicturesUploadActivity &&takePictureType==8){
                ((PicturesUploadActivity )context).albumCaptureToAlbum();
            }
            if(takePictureType==1||takePictureType==2||takePictureType==3||takePictureType==4||takePictureType==5||takePictureType==7){
                if(context instanceof MainActivity){
                    ((MainActivity)context).albumCaptureToAlbum(takePictureType);
                }
            }
        }else if(intent.getAction().equals("com.beeinc.select.video")){//选择了了视频
            if(context instanceof MainActivity){
                ((MainActivity) context).selectToPlayVideo(intent);
            }
        }
    }
}
