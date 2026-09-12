package com.beeinc.SQSB.activity;

import static org.opencv.imgproc.Imgproc.COLOR_RGBA2BGRA;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.beeinc.SQSB.R;
import com.beeinc.SQSB.dialog.LoadingDialogFragment;
import com.beeinc.SQSB.util.DialogUtil;
import com.beeinc.SQSB.util.FileUtil;
import com.unity3d.player.UnityPlayer;
import com.yanzhenjie.album.AlbumFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.FileDescriptor;

public class VideoPlayActivity extends FragmentActivity {
    private View v_back;
    private TextView tv_sure;
    private VideoView video_view;
    private TextView tv_hint;
    private AlbumFile albumFile;
    private int videoPosition;
    private String savePath;
    private boolean isSaving;//防止重复点击
    private long videoTimeLong;//视频长度
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("-------->","MainActivity onCreate");
        setContentView(R.layout.video_play);
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        albumFile = this.getIntent().getParcelableExtra("albumFile");
        savePath = this.getIntent().getStringExtra("savePath");
        init();
    }
    private void init(){
        getAllViews();
        setParams();
        setListener();
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
            playVideo(albumFile.getUriContentPath());
        }else{
            playVideo(albumFile.getPath());
        }
    }
    private void getAllViews(){
        v_back = findViewById(R.id.v_back);
        tv_sure = findViewById(R.id.tv_sure);
        video_view = findViewById(R.id.video_view);
        tv_hint = findViewById(R.id.tv_hint);
    }
    private void setParams(){
        videoTimeLong = albumFile.getDuration();
        if(videoTimeLong>60*1000){
            tv_hint.setVisibility(View.VISIBLE);
        }
    }
    private Bitmap getVideoThumb(Context context, String path){
        Bitmap bitmap = null;
        try {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            if(context==null){
                media.setDataSource(path);
            }else{
                ParcelFileDescriptor parcelFileDescriptor = null;
                parcelFileDescriptor = context.getContentResolver().openFileDescriptor(Uri.parse(path), "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                media.setDataSource(fileDescriptor);
            }
            bitmap  = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC );
        }catch (Exception ex){
            return null;
        }
        return bitmap;
    }

    private void setListener(){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSaving){
                    return;
                }
                if(v.getId()== R.id.v_back){
                    finish();
                }else if(v.getId()== R.id.tv_sure){
                    if(videoTimeLong>60*1000){
                        Toast.makeText(VideoPlayActivity.this,"视频太长，请将视频剪辑到60秒以内。",Toast.LENGTH_LONG).show();
                    }else{
                        save();
                    }
                }
            }
        };
        v_back.setOnClickListener(listener);
        tv_sure.setOnClickListener(listener);
    }
    private void save(){
        isSaving = true;
        DialogUtil.showLoadingDialog(this,new LoadingDialogFragment());
        new Thread(){
            public void run(){
                try {
                    //复制视频
                    String suffix = albumFile.getPath().substring(albumFile.getPath().lastIndexOf("."));
                    String videoName = System.currentTimeMillis()+suffix;
                    //压缩视频
//                    Uri videoContentUri;
//                    Bitmap bitmap = null;
//                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
//                        videoContentUri = Uri.parse(albumFile.getQPath());
//                        bitmap = getVideoThumb(VideoPlayActivity.this,albumFile.getQPath());
//                    }else{
//                        videoContentUri = Uri.parse(albumFile.getPath());
//                        bitmap = getVideoThumb(null,albumFile.getPath());
//                    }
//                    String videoSavePath;
//                    try {
//                        int maxSize = bitmap.getWidth()>=bitmap.getHeight()?bitmap.getWidth():bitmap.getHeight();
//                        int initMaxSize = 720;
//                        if(maxSize>initMaxSize){
//                            int scale = 1;
//                            while(true){
//                                maxSize = maxSize/2;
//                                scale = scale*2;
//                                if(maxSize<=initMaxSize){
//                                    break;
//                                }
//                            }
//                            Log.e("-------------------->","original width ="+bitmap.getWidth()+"original height = "+bitmap.getHeight());
//                            Log.e("-------------------->","scale = "+scale);
//                            int degree;
//                            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
//                                degree= SystemUtil.readAndroidQVideoDegree(VideoPlayActivity.this,albumFile.getQPath());
//                            }else{
//                                degree = SystemUtil.readVideoDegree(albumFile.getPath());
//                            }
//                            Log.e("-------------------->","degree ="+degree);
//                            Log.e("-------------------->","scale width ="+bitmap.getWidth()/scale+"scale height = "+bitmap.getHeight()/scale);
//                            if(degree%180==90){
//                                videoSavePath =SiliCompressor.with(VideoPlayActivity.this).compressVideo(
//                                        videoContentUri,
//                                        savePath,
//                                        bitmap.getHeight()/scale,
//                                        bitmap.getWidth()/scale,
////                                    1280,720,
//                                        2000000);
//                            }else{
//                                videoSavePath =SiliCompressor.with(VideoPlayActivity.this).compressVideo(
//                                        videoContentUri,
//                                        savePath,
//                                        bitmap.getWidth()/scale,
//                                        bitmap.getHeight()/scale,
////                                    1280,720,
//                                        2000000);
//                            }
//                            File file = new File(videoSavePath);
//                            videoName = file.getName();
//                        }else{
//                            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
//                                FileUtil.QCopyfile(VideoPlayActivity.this,albumFile.getQPath(),new File(savePath+videoName),false);
//                            }else{
//                                FileUtil.copyfile(new File(albumFile.getPath()),new File(savePath+videoName),false);
//                            }
//                        }
//                    } catch (URISyntaxException e) {
//                        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
//                            FileUtil.QCopyfile(VideoPlayActivity.this,albumFile.getQPath(),new File(savePath+videoName),false);
//                        }else{
//                            FileUtil.copyfile(new File(albumFile.getPath()),new File(savePath+videoName),false);
//                        }                    }
                    Bitmap bitmap = null;
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                        //首先拷贝到缓存目录，然后再拷贝到指定目录。
                        FileUtil.copyUriToFile(VideoPlayActivity.this,Uri.parse(albumFile.getUriContentPath()),savePath+videoName,false);
                        bitmap = getVideoThumb(VideoPlayActivity.this,albumFile.getUriContentPath());
                    }else{
                        FileUtil.copyFileToFile(VideoPlayActivity.this,albumFile.getPath(),savePath+videoName,false,false);
                        bitmap = getVideoThumb(null,albumFile.getPath());
                    }
                    //保存缩略图
                    String fileName = System.currentTimeMillis()+".jpg";
                    saveImageFile(bitmap,fileName,false);
                    JSONArray jsonArry = new JSONArray();
                    JSONObject json =new JSONObject();
                    json.put("type",2);
                    json.put("file",videoName);
                    json.put("thumb",fileName);
                    jsonArry.put(json);
                    UnityPlayer.UnitySendMessage("IOsReciveObj","OnNativeContent4ArticleFinish",jsonArry.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void playVideo(String path){
        video_view.setVideoPath(path);
        video_view.start();
        video_view.requestFocus();
        video_view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        // seekTo 方法完成时的回调
                        video_view.start();
                    }
                });
            }
        });
        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                video_view.seekTo(0);
                video_view.start();
            }
        });
    }
    public void onResume(){
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        if(video_view!=null){
            video_view.seekTo(videoPosition);
            Log.e("---------->","videoPosition = "+videoPosition);
        }
    }
    public void onPause(){
        super.onPause();
        if(video_view!=null){
            videoPosition =video_view.getCurrentPosition();
            Log.e("---------->","videoPosition = "+videoPosition);
        }
    }
    /**
     *
     * @param bitmap 原始图片
     * @param isBig  true 大图，false 缩略图
     * @return
     */
    private void saveImageFile(Bitmap bitmap,String fileName,boolean isBig){
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        Bitmap temBitmap = null;
        int maxWidth = 800;//最长边长度
        int scaleWidth = 346;//图片缩放后的宽度
        int scaleHeight = 256;//图片缩放后的高度
        if(isBig){
            maxWidth = 800;
        }else{
            float a  = (float)imageWidth/imageHeight;
            float b = 346f/259;
            if(a>b){//图片宽度比例大于标准的宽度比例
                scaleHeight = 259;
                scaleWidth = 259*imageWidth/imageHeight;
                scaleWidth=scaleWidth>346?scaleWidth:346;
            }else{
                scaleWidth = 346;
                scaleHeight = 346*imageHeight/imageWidth;
                scaleHeight = scaleHeight>259?scaleHeight:259;
            }
        }
        Bitmap temBitmap1=null;
        if(!isBig){
            temBitmap1 = Bitmap.createScaledBitmap(bitmap,scaleWidth,scaleHeight,true);
            temBitmap = Bitmap.createBitmap(temBitmap1,(temBitmap1.getWidth()-346)/2,(temBitmap1.getHeight()-259)/2,346,259);
        }else{
            if(imageWidth>imageHeight){
                temBitmap = Bitmap.createScaledBitmap(bitmap,maxWidth,maxWidth*imageHeight/imageWidth,true);
            }else{
                temBitmap = Bitmap.createScaledBitmap(bitmap,maxWidth*imageWidth/imageHeight,maxWidth,true);
            }
        }
        Mat mat = new Mat();
        Utils.bitmapToMat(temBitmap,mat);
        String path = null;
        path = savePath+fileName;
        Mat temp = new Mat();
        Imgproc.cvtColor(mat, temp, COLOR_RGBA2BGRA);
        Imgcodecs.imwrite(path,temp);
        temBitmap.recycle();
        if(temBitmap1!=null){
            temBitmap1.recycle();
        }
        temp.release();
        mat.release();
        if(!isBig){
            bitmap.recycle();
        }
    }
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
}
