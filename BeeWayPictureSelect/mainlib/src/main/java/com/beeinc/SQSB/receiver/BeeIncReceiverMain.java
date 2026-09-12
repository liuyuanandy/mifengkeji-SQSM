package com.beeinc.SQSB.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.beeinc.SQSB.MainActivity;

public class BeeIncReceiverMain extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.beeinc.album.takpicture")){//第三方相册调用拍照
            int takePictureType = intent.getIntExtra("takePictureType",1);
            if(context instanceof MainActivity){
                ((MainActivity)context).startAlbumCapture(takePictureType);
            }
        }else if(intent.getAction().equals("com.beeinc.album.takpicture.finish")){//第三方相册调用拍照结束
            int takePictureType = intent.getIntExtra("takePictureType",1);
            if(takePictureType==1||takePictureType==2||takePictureType==3||takePictureType==4||takePictureType==5||takePictureType==7){
                if(context instanceof MainActivity){
                    ((MainActivity)context).albumCaptureFinish(intent);
                }
            }
        }else if(intent.getAction().equals("com.beeinc.album.takpicture.album")){//第三方相册调用拍照切换到相册
            int takePictureType = intent.getIntExtra("takePictureType",1);
            Log.e("---------->","onReceive takePictureType = "+takePictureType);
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
