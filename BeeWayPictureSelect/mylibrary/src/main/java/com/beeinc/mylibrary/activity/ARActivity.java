package com.beeinc.mylibrary.activity;

import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2BGRA;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.beeinc.mylibrary.R;
import com.beeinc.mylibrary.scale.Layout;
import com.beeinc.mylibrary.scale.ScreenUtil;
import com.beeinc.mylibrary.util.ConstantData;
import com.beeinc.mylibrary.util.FileUtil;
import com.beeinc.mylibrary.util.ImageDealUtil;
import com.beeinc.mylibrary.util.PermissionUtil;
import com.beeinc.mylibrary.views.Tutorial3View;
import com.test.RemapHelper;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ARActivity extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private View v_left,v_right;
    private ImageView iv_ar_fs,iv_ar_kt;
    private Tutorial3View mOpenCvCameraView;
    private String TAG = "ARActivity";
    private FrameLayout frame_options;
    private ImageView iv_close,iv_single,iv_vertical_two,iv_capture,iv_horizontal_two,iv_four;
    private Mat map_x;
    private Mat map_y;

    private int takePictureStatus = -1;// -1不拍照； 0 执行拍照；1正在拍照；3 拍照结束
    //当前类型 type;
    private int type=31;

    private int image_width;//图片显示宽度
    private int image_height;//图片显示高度
    private Handler handler;

    private ArrayList<AlbumFile> mAlbumFiles;
    private String fileKtPath,fileFsPath;

    private Bitmap bitmapFs;
    private Bitmap bitmapKt;
    private boolean isDestroy = false;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_layout);
        ScreenUtil.setLayoutNum(this);
        fileFsPath = this.getIntent().getStringExtra("fileFsPath");
        fileKtPath = this.getIntent().getStringExtra("fileKtPath");
        Log.e("------------>","fileFsPath:"+fileFsPath );
        Log.e("------------>","fileKtPath:"+fileKtPath );
        mOpenCvCameraView = (Tutorial3View) findViewById(R.id.cameraView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        init();
    }
    private void init(){
        getAllViews();
        setParams();
        initHandler();
        setListeners();
        isDestroy = false;
        hideNavigatonButton();
    }
    private void getAllViews(){
        v_left = findViewById(R.id.v_left);
        v_right = findViewById(R.id.v_right);
        iv_ar_kt = findViewById(R.id.iv_ar_kt);
        iv_ar_fs = findViewById(R.id.iv_ar_fs);
        frame_options = findViewById(R.id.frame_options);
        iv_close = findViewById(R.id.iv_close);
        iv_single = findViewById(R.id.iv_single);
        iv_vertical_two = findViewById(R.id.iv_vertical_two);
        iv_capture = findViewById(R.id.iv_capture);
        iv_horizontal_two = findViewById(R.id.iv_horizontal_two);
        iv_four = findViewById(R.id.iv_four);
    }
    private void initHandler(){
        handler = new Handler();
    }
    private void setParams(){
        FrameLayout.LayoutParams p_options = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        p_options.gravity = Gravity.CENTER_VERTICAL|Gravity.RIGHT;
        p_options.setMargins(0,0, Layout.getScale(24),0);
        frame_options.setLayoutParams(p_options);

        FrameLayout.LayoutParams p_close = new FrameLayout.LayoutParams(Layout.getScale(36), Layout.getScale(36));
        p_close.setMargins(Layout.getScale(40), Layout.getScale(40) ,0 ,0 );
        iv_close.setLayoutParams(p_close);
        FrameLayout.LayoutParams p_single = new FrameLayout.LayoutParams(Layout.getScale(50), Layout.getScale(50));
        p_single.gravity = Gravity.CENTER_HORIZONTAL;
        iv_single.setLayoutParams(p_single);

        FrameLayout.LayoutParams p_vertical = new FrameLayout.LayoutParams(Layout.getScale(50), Layout.getScale(50));
        p_vertical.setMargins(0, Layout.getScale(52),0,0);
        p_vertical.gravity = Gravity.CENTER_HORIZONTAL;
        iv_vertical_two.setLayoutParams(p_vertical);

        FrameLayout.LayoutParams p_capture = new FrameLayout.LayoutParams(Layout.getScale(52), Layout.getScale(52));
        p_capture.setMargins(0, Layout.getScale(114),0,0);
        p_capture.gravity = Gravity.CENTER_HORIZONTAL;
        iv_capture.setLayoutParams(p_capture);

        FrameLayout.LayoutParams p_horizontal = new FrameLayout.LayoutParams(Layout.getScale(50), Layout.getScale(50));
        p_horizontal.setMargins(0, Layout.getScale(188),0,0);
        p_horizontal.gravity = Gravity.CENTER_HORIZONTAL;
        iv_horizontal_two.setLayoutParams(p_horizontal);

        FrameLayout.LayoutParams p_four = new FrameLayout.LayoutParams(Layout.getScale(50), Layout.getScale(50));
        p_four.setMargins(0, Layout.getScale(240),0,0);
        p_four.gravity = Gravity.CENTER_HORIZONTAL;
        iv_four.setLayoutParams(p_four);

        iv_close.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_left_close,true ));
        showButton();
        bitmapFs = ImageDealUtil.decodeFile(fileFsPath, ConstantData.maxDecodeImageWidth);
        bitmapKt = ImageDealUtil.decodeFile(fileKtPath, ConstantData.maxDecodeImageWidth);
        iv_ar_fs.setImageBitmap(bitmapFs);
        iv_ar_kt.setImageBitmap(bitmapKt);
    }
    private void setListeners(){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()== R.id.iv_close){
                    finish();
                }
                if(v.getId()== R.id.iv_single){
                    setRemapType(0);
                }
                if(v.getId()== R.id.iv_vertical_two){
                    if(type==11){
                        setRemapType(12);
                    }else{
                        setRemapType(11);
                    }
                }
                if(v.getId()== R.id.iv_capture){
                    takePictureStatus = 0;
                }
                if(v.getId()== R.id.iv_horizontal_two){
                    if(type==21){
                        setRemapType(22);
                    }else{
                        setRemapType(21);
                    }
                }
                if(v.getId()== R.id.iv_four){
                    if(type==31){
                        setRemapType(32);
                    }else if(type==32){
                        setRemapType(33);
                    }else if(type==33){
                        setRemapType(34);
                    }else{
                        setRemapType(31);
                    }
                }
            }
        };
        iv_close.setOnClickListener(listener);
        iv_single.setOnClickListener(listener);
        iv_vertical_two.setOnClickListener(listener);
        iv_capture.setOnClickListener(listener);
        iv_horizontal_two.setOnClickListener(listener);
        iv_four.setOnClickListener(listener);
    }
    //设置映射类型
    private void setRemapType(int type){
        this.type = type;
        if(map_x!=null){
            map_x.release();
            map_y.release();
            map_x = null;
            map_y = null;
        }
        showButton();
    }
    private void doRemap(Mat mat){
        switch(type){
            case 0:
                break;
            case 11:
                if(map_x==null){
                    map_x =new Mat(mat.size(), CV_32FC1);
                    map_y =new Mat(mat.size(), CV_32FC1);
                    RemapHelper.setRemap(mat.getNativeObjAddr(),map_x.getNativeObjAddr(),map_y.getNativeObjAddr(),11);
                }
                Imgproc.remap(mat,mat, map_x, map_y, Imgproc.INTER_LINEAR);
                break;
            case 12:
                if(map_x==null){
                    map_x =new Mat(mat.size(), CV_32FC1);
                    map_y =new Mat(mat.size(), CV_32FC1);
                    RemapHelper.setRemap(mat.getNativeObjAddr(),map_x.getNativeObjAddr(),map_y.getNativeObjAddr(),12);
                }
                Imgproc.remap(mat,mat, map_x, map_y, Imgproc.INTER_LINEAR);
                break;
            case 21:
                if(map_x==null){
                    map_x =new Mat(mat.size(), CV_32FC1);
                    map_y =new Mat(mat.size(), CV_32FC1);
                    RemapHelper.setRemap(mat.getNativeObjAddr(),map_x.getNativeObjAddr(),map_y.getNativeObjAddr(),21);
                }
                Imgproc.remap(mat,mat, map_x, map_y, Imgproc.INTER_LINEAR);
                break;
            case 22:
                if(map_x==null){
                    map_x =new Mat(mat.size(), CV_32FC1);
                    map_y =new Mat(mat.size(), CV_32FC1);
                    RemapHelper.setRemap(mat.getNativeObjAddr(),map_x.getNativeObjAddr(),map_y.getNativeObjAddr(),22);
                }
                Imgproc.remap(mat,mat, map_x, map_y, Imgproc.INTER_LINEAR);
                break;
            case 31:
                if(map_x==null){
                    map_x =new Mat(mat.size(), CV_32FC1);
                    map_y =new Mat(mat.size(), CV_32FC1);
                    RemapHelper.setRemap(mat.getNativeObjAddr(),map_x.getNativeObjAddr(),map_y.getNativeObjAddr(),31);
                }
                Imgproc.remap(mat,mat, map_x, map_y, Imgproc.INTER_LINEAR);
                break;
            case 32:
                if(map_x==null){
                    map_x =new Mat(mat.size(), CV_32FC1);
                    map_y =new Mat(mat.size(), CV_32FC1);
                    RemapHelper.setRemap(mat.getNativeObjAddr(),map_x.getNativeObjAddr(),map_y.getNativeObjAddr(),32);
                }
                Imgproc.remap(mat,mat,map_x, map_y, Imgproc.INTER_LINEAR);
                break;
            case 33:
                if(map_x==null){
                    map_x =new Mat(mat.size(), CV_32FC1);
                    map_y =new Mat(mat.size(), CV_32FC1);
                    RemapHelper.setRemap(mat.getNativeObjAddr(),map_x.getNativeObjAddr(),map_y.getNativeObjAddr(),33);
                }
                Imgproc.remap(mat,mat,map_x, map_y, Imgproc.INTER_LINEAR);
                break;
            case 34:
                if(map_x==null){
                    map_x =new Mat(mat.size(), CV_32FC1);
                    map_y =new Mat(mat.size(), CV_32FC1);
                    RemapHelper.setRemap(mat.getNativeObjAddr(),map_x.getNativeObjAddr(),map_y.getNativeObjAddr(),34);
                }
                Imgproc.remap(mat,mat,map_x, map_y, Imgproc.INTER_LINEAR);
                break;
        }
    }
    static {
        System.loadLibrary("detection_based_tracker");
    }
    private void setCantakePicture(){
        if(takePictureStatus!=1){
            takePictureStatus = 0;
        }
    }
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        isDestroy  = true;
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat mat = inputFrame.rgba();
        try{
            doRemap(mat);
        }catch (Exception e){

        }
        if(image_width==0){
            image_width = mat.cols();
            image_height = mat.rows();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    int fsWidthShow = (int)((float)bitmapFs.getWidth()*image_height/bitmapFs.getHeight());
                    int fsHeightShow = image_height;
                    FrameLayout.LayoutParams p_iv_ar = new FrameLayout.LayoutParams(fsWidthShow,fsHeightShow);
                    p_iv_ar.gravity = Gravity.CENTER;
                    iv_ar_fs.setLayoutParams(p_iv_ar);
                    iv_ar_kt.setLayoutParams(p_iv_ar);
                    if((float)image_width/image_height>(float)bitmapFs.getWidth()/bitmapFs.getHeight()){//相机的宽度比例大于图片的宽度比例
                        v_left.setLayoutParams(new FrameLayout.LayoutParams((ScreenUtil.SCREEN_THIS_W-fsWidthShow)/2,FrameLayout.LayoutParams.MATCH_PARENT ));
                        FrameLayout.LayoutParams p_right = new FrameLayout.LayoutParams((ScreenUtil.SCREEN_THIS_W-fsWidthShow)/2,FrameLayout.LayoutParams.MATCH_PARENT );
                        p_right.gravity = Gravity.RIGHT;
                        v_right.setLayoutParams(p_right);
                    }else{
                        v_left.setLayoutParams(new FrameLayout.LayoutParams((ScreenUtil.SCREEN_THIS_W-image_width)/2,FrameLayout.LayoutParams.MATCH_PARENT ));
                        FrameLayout.LayoutParams p_right = new FrameLayout.LayoutParams((ScreenUtil.SCREEN_THIS_W-image_width)/2,FrameLayout.LayoutParams.MATCH_PARENT );
                        p_right.gravity = Gravity.RIGHT;
                        v_right.setLayoutParams(p_right);
                    }

                }
            });
        }
        if(takePictureStatus==0){
            takePictureStatus = 1;
            String path = FileUtil.getCacheSaveImagesDir(ARActivity.this)+FileUtil.getNewFileNameByTime()+".jpg";
            int fsWidthShow = (int)((float)bitmapFs.getWidth()*image_height/bitmapFs.getHeight());
            int fsHeightShow = image_height;
            Bitmap scaleFs = Bitmap.createScaledBitmap(bitmapFs,fsWidthShow, fsHeightShow,true);
            Bitmap scaleKt = Bitmap.createScaledBitmap(bitmapKt,fsWidthShow, fsHeightShow, true);
            Mat camerMat = new Mat();
            mat.copyTo(camerMat);
            if((float)image_width/image_height>(float)bitmapFs.getWidth()/bitmapFs.getHeight()){//相机的宽度比例大于图片的宽度比例
                //裁剪mat
                Bitmap bitCamera = Bitmap.createBitmap(camerMat.width(), camerMat.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(camerMat, bitCamera);
                camerMat.release();
                camerMat = new Mat();
                bitCamera = Bitmap.createBitmap(bitCamera, (image_width-fsWidthShow)/2, 0, fsWidthShow, fsHeightShow);
                Utils.bitmapToMat(bitCamera, camerMat);
                bitCamera.recycle();
            }else{
                //裁剪bitmap
                scaleFs = Bitmap.createBitmap(scaleFs, (scaleFs.getWidth()-image_width)/2, 0, image_width, image_height);
                scaleKt = Bitmap.createBitmap(scaleKt,(scaleFs.getWidth()-image_width)/2, 0, image_width, image_height);
            }

            Mat mat_fs = new Mat();
            Mat mat_kt = new Mat();
            Utils.bitmapToMat(scaleFs, mat_fs);
            Utils.bitmapToMat(scaleKt, mat_kt);
            RemapHelper.setOverlayImage(mat_fs.getNativeObjAddr(), mat_kt.getNativeObjAddr(), 0, 0);
            RemapHelper.setOverlayImage(camerMat.getNativeObjAddr(), mat_fs.getNativeObjAddr(), 0, 0);

            bitmapFs.recycle();
            bitmapKt.recycle();
            scaleFs.recycle();
            scaleKt.recycle();
            bitmapFs = null;
            bitmapKt = null;
            scaleFs = null;
            scaleKt = null;
            mat_kt.release();
            mat_fs.release();
            Imgproc.cvtColor(camerMat, camerMat, COLOR_RGBA2BGRA);
            Imgcodecs.imwrite(path,camerMat);

            camerMat.release();
            camerMat = null;
            Intent in = new Intent();
            in.putExtra("filePath",path);
            in.putExtra("fileFsPath",fileFsPath);
            in.putExtra("fileKtPath",fileKtPath);
            in.setClass(this, ARPictureActivity.class);
            startActivity(in);
            finish();
        }
        return mat;
    }
//    static {
//        System.loadLibrary("detection_based_tracker");
//    }

    private void selectAlbum(){
        Album.image(ARActivity.this)
                .multipleChoice()
                .camera(true)
                .columnCount(2)
                .selectCount(1)
                .checkedList(mAlbumFiles)
                .widget(
                        Widget.newDarkBuilder(ARActivity.this)
                                .title("选择图片")
                                .build()
                )
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        mAlbumFiles = result;
                        if(result.size()>0){
                            Intent in = new Intent();
                            in.putExtra("filePath",result.get(0).getPath() );
                            in.setClass(ARActivity.this, ARPictureActivity.class );
                            startActivity(in);
                        }
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toast.makeText(ARActivity.this, R.string.canceled, Toast.LENGTH_LONG).show();
                    }
                })
                .start();
    }
    private void showButton( ) {
        iv_single.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_singal_unselected, false));
        iv_vertical_two.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_vertical_unselected, false));
        iv_capture.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_capture, false));
        iv_horizontal_two.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_horizontal_unselected, false));
        iv_four.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_four_unslected, false));
        switch (type) {
            case 0:
                iv_single.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_singal_selected, false));
                break;
            case 11:
                iv_vertical_two.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_vertical_1, false));
                break;
            case 12:
                iv_vertical_two.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_vertical_2, false));
                break;
            case 21:
                iv_horizontal_two.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_horizontal_1, false));
                break;
            case 22:
                iv_horizontal_two.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_horizontal_2, false));
                break;
            case 31:
                iv_four.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_four_1, false));
                break;
            case 32:
                iv_four.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_four_2, false));
                break;
            case 33:
                iv_four.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_four_3, false));
                break;
            case 34:
                iv_four.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_four_4, false));
                break;

        }
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
    private void hideNavigatonButton(){
        if(!isDestroy){
            hideSystemUI();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideNavigatonButton();
                }
            }, 5000);
        }
    }
}