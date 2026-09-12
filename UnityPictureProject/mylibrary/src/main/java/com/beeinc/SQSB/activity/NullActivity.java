package com.beeinc.SQSB.activity;

import static org.opencv.imgproc.Imgproc.COLOR_RGBA2BGRA;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.beeinc.SQSB.bean.PhoneSelectedPictureFile;
import com.beeinc.SQSB.dialog.LoadingDialogFragment;
import com.beeinc.SQSB.scale.ScreenUtil;
import com.beeinc.SQSB.util.ConstantData;
import com.beeinc.SQSB.util.DialogUtil;
import com.beeinc.SQSB.util.ImageDealUtil;
import com.beeinc.SQSB.util.OpenCVUtil;
import com.unity3d.player.UnityPlayer;

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

import java.util.ArrayList;

public class NullActivity extends FragmentActivity {
    private Handler handler;
    private boolean isDestroy = false;
    private ArrayList<PhoneSelectedPictureFile> pictures;
    private String savePath;
    private LoadingDialogFragment dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtil.setLayoutNum(this);
        hideNavigatonButton();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        isDestroy = false;
        pictures = (ArrayList<PhoneSelectedPictureFile>) this.getIntent().getSerializableExtra("pictures");
        savePath = this.getIntent().getStringExtra("savePath");
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                save(pictures);
            }
        },500);
    }
    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
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
    private void hideNavigatonButton() {
        if (handler == null) {
            handler = new Handler();
        }
        if (!isDestroy) {
            hideSystemUI();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideNavigatonButton();
                }
            }, 5000);
        }
    }
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }
    public void save(final ArrayList<PhoneSelectedPictureFile> pictures) {
        dialog = new LoadingDialogFragment();
        dialog = DialogUtil.showLoadingDialog(NullActivity.this,dialog);
        new Thread(){
            public void run(){
                for(int i=0;i<pictures.size();i++){
                    Bitmap bitmap =null;
                    if(pictures.get(i).isDealed()){
                        bitmap = ImageDealUtil.decodeFile(pictures.get(i).getAfterDealPath(), ConstantData.maxDecodeImageWidth);
                    }else{
                        bitmap = OpenCVUtil.getBitmap(false, NullActivity.this,pictures.get(i).getOriginalPath(),pictures.get(i).getQ_originalPath(), ConstantData.maxDecodeImageWidth);
                    }
                    //存储Unity所需信息
                    pictures.get(i).setType(1);
                    //保存大图
                    String fileName = System.currentTimeMillis()+".jpg";
                    saveImageFile(bitmap,fileName,true);
                    pictures.get(i).setFile(fileName);
                    //保存缩略图
                    String fileName2 = System.currentTimeMillis()+".jpg";
                    if(bitmap.isRecycled()){
                        if(pictures.get(i).isDealed()){
                            bitmap = ImageDealUtil.decodeFile(pictures.get(i).getAfterDealPath(), ConstantData.maxDecodeImageWidth);
                        }else{
                            bitmap = OpenCVUtil.getBitmap(false, NullActivity.this,pictures.get(i).getOriginalPath(),pictures.get(i).getQ_originalPath(), ConstantData.maxDecodeImageWidth);
                        }
                    }
                    saveImageFile(bitmap,fileName2,false);
                    pictures.get(i).setThumb(fileName2);
                }
                try {
                    JSONArray jsonArry = new JSONArray();
                    for(int i=0;i<pictures.size();i++){
                        JSONObject json =new JSONObject();
                        json.put("type",pictures.get(i).getType());
                        json.put("file",pictures.get(i).getFile());
                        json.put("thumb",pictures.get(i).getThumb());
                        jsonArry.put(json);
                    }
                    UnityPlayer.UnitySendMessage("IOsReciveObj","OnNativeContent4ArticleFinish",jsonArry.toString());
                    try {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                NullActivity.this.finish();
                            }
                        });
                    } catch (InterruptedException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                NullActivity.this.finish();
                            }
                        });
                    }

                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NullActivity.this.finish();
                        }
                    });
                }
            }
        }.start();
    }
    /**
     *
     * @param bitmap 原始图片
     * @param isBig  true 大图，false 缩略图
     * @return
     */
    private void saveImageFile(Bitmap bitmap, String fileName, boolean isBig){
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
}

