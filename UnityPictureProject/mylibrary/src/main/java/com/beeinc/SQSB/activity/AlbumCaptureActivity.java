package com.beeinc.SQSB.activity;

import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2BGRA;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.beeinc.SQSB.R;
import com.beeinc.SQSB.scale.Layout;
import com.beeinc.SQSB.scale.ScreenUtil;
import com.beeinc.SQSB.util.FileUtil;
import com.beeinc.SQSB.util.ImageDealUtil;
import com.beeinc.SQSB.util.PermissionUtil;
import com.beeinc.SQSB.views.Tutorial3View;
import com.test.RemapHelper;
import com.yanzhenjie.album.AlbumFile;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumCaptureActivity extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    private static final String TAG = "OCVSample::Activity";

    private Tutorial3View mOpenCvCameraView;
    private FrameLayout frame_options;
    private ImageView iv_single,iv_vertical_two,iv_capture,iv_horizontal_two,iv_four,iv_album;
    private TextView tv_close;
    private Mat map_x;
    private Mat map_y;
    //当前类型 type;
    //0正常拍摄 ；11垂直镜像 12 垂直镜像且垂直翻转；21 水平镜像 22水平镜像且水平翻转；
    // 31 四拼镜像 32四拼镜像且水平翻转 33四拼镜像且垂直翻转 44 四拼镜像 且水平垂直翻转
    private int type;
    private Size size;
    private int cols;
    private int rows;
    private int threadFinisishedNum = 0;
    private long time;
    private int takePictureStatus = -1;// -1不拍照； 0 执行拍照；1正在拍照；3 拍照结束
    private int calculatedStatus = 0;//0未开始计算 ；1 计算中 ；2 计算结束
    private int START_TYPE;//1 切割 ；2 镜像拍;3 对花
    private FrameLayout.LayoutParams p_single,p_vertical,p_horizontal,p_four;
    private String filePath;
    private ArrayList<AlbumFile> mAlbumFiles;

    private boolean isDestroy = false;
    private Handler handler;

    private int takePictureType;//1调用相册，2 切割调用相册，3 镜像拍调用相册 ,4 对花调用相册，5 MainActivity upload掉用相册,6 UploadActivity upload调用相册,7 upload掉用相册新 ,8 PicturesUploadActivity upload调用相册

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.cut_capture);
        hideNavigatonButton();
        isDestroy = false;
        ScreenUtil.setLayoutNum(this);
//        START_TYPE = this.getIntent().getIntExtra("start_type",1);
        takePictureType = this.getIntent().getIntExtra("takePictureType",1);
        START_TYPE = 1;
        if(START_TYPE==1){
            filePath = this.getIntent().getStringExtra("filePath");
        }
        if(START_TYPE == 2){
            type = 31;
        }
        init();
        mOpenCvCameraView = (Tutorial3View) findViewById(R.id.cameraView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        boolean isHaveSdcardPermission = PermissionUtil.isHavePermission(this, PermissionUtil.TYPE.STORAGE);
        if(!isHaveSdcardPermission){
            PermissionUtil.startRequestPermission(this, PermissionUtil.TYPE.STORAGE);
        }

    }
    private void init(){
        ScreenUtil.setLayoutNum(this);
        getAllViews();
        setParams();
        setListeners();
    }
    private void setParams(){
        FrameLayout.LayoutParams p_options = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        p_options.gravity = Gravity.CENTER_VERTICAL|Gravity.RIGHT;
        p_options.setMargins(0,0, Layout.getScale(24),0);
        frame_options.setLayoutParams(p_options);

        FrameLayout.LayoutParams p_close = new FrameLayout.LayoutParams(Layout.getScale(100), Layout.getScale(40));
        p_close.setMargins(0, Layout.getScale(30),0,0);
        p_close.gravity = Gravity.RIGHT;
        tv_close.setLayoutParams(p_close);
        Layout.setTextViewSize(tv_close, 20);

        p_single = new FrameLayout.LayoutParams(Layout.getScale(50), Layout.getScale(50));
        p_single.gravity = Gravity.CENTER_HORIZONTAL;
        iv_single.setLayoutParams(p_single);

        p_vertical = new FrameLayout.LayoutParams(Layout.getScale(50), Layout.getScale(50));
        p_vertical.setMargins(0, Layout.getScale(52),0,0);
        p_vertical.gravity = Gravity.CENTER_HORIZONTAL;
        iv_vertical_two.setLayoutParams(p_vertical);

        FrameLayout.LayoutParams p_capture = new FrameLayout.LayoutParams(Layout.getScale(52), Layout.getScale(52));
        p_capture.setMargins(0, Layout.getScale(114),0,0);
        p_capture.gravity = Gravity.CENTER_HORIZONTAL;
        iv_capture.setLayoutParams(p_capture);

        p_horizontal = new FrameLayout.LayoutParams(Layout.getScale(50), Layout.getScale(50));
        p_horizontal.setMargins(0, Layout.getScale(188),0,0);
        p_horizontal.gravity = Gravity.CENTER_HORIZONTAL;
        iv_horizontal_two.setLayoutParams(p_horizontal);

        p_four = new FrameLayout.LayoutParams(Layout.getScale(50), Layout.getScale(50));
        p_four.setMargins(0, Layout.getScale(240),0,0);
        p_four.gravity = Gravity.CENTER_HORIZONTAL;
        iv_four.setLayoutParams(p_four);

        FrameLayout.LayoutParams p_album = new FrameLayout.LayoutParams(Layout.getScale(60), Layout.getScale(60));
        p_album.setMargins(0,0, Layout.getScale(20), Layout.getScale(100));
        p_album.gravity = Gravity.BOTTOM|Gravity.RIGHT;
        iv_album.setLayoutParams(p_album);
        showButton();
        if(START_TYPE==3){
            iv_single.setVisibility(View.INVISIBLE);
            iv_vertical_two.setVisibility(View.INVISIBLE);
            iv_horizontal_two.setVisibility(View.INVISIBLE);
            iv_four.setVisibility(View.INVISIBLE);
        }
    }
    private void getAllViews(){
        frame_options = findViewById(R.id.frame_options);
        tv_close = findViewById(R.id.tv_close);
        iv_single = findViewById(R.id.iv_single);
        iv_vertical_two = findViewById(R.id.iv_vertical_two);
        iv_capture = findViewById(R.id.iv_capture);
        iv_horizontal_two = findViewById(R.id.iv_horizontal_two);
        iv_four = findViewById(R.id.iv_four);
        iv_album = findViewById(R.id.iv_album);
    }
    private void setListeners(){
        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()== R.id.tv_close){
                    finish();
                }
                if(v.getId()== R.id.iv_single){
                    setRemapType(0);
                }
                if(v.getId()== R.id.iv_vertical_two){
                    if(type!=11&&type!=12){
                        setRemapType(11);
                    }else{
                        if(type==11){
                            setRemapType(12);
                        }else{
                            setRemapType(11);
                        }
                    }
                }
                if(v.getId()== R.id.iv_capture){
                    setCantakePicture();
                }
                if(v.getId()== R.id.iv_horizontal_two){
                    if(type!=21&&type!=22){
                        setRemapType(21);
                    }else{
                        if(type==21){
                            setRemapType(22);
                        }else{
                            setRemapType(21);
                        }
                    }
                }
                if(v.getId()== R.id.iv_four){
                    if(type!=31&&type!=32&&type!=33&&type!=34){
                        setRemapType(31);
                    }else{
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
                if(v.getId()== R.id.iv_album){
                    selectAlbum();
                }
            }
        };
        tv_close.setOnClickListener(listener);
        iv_single.setOnClickListener(listener);
        iv_vertical_two.setOnClickListener(listener);
        iv_capture.setOnClickListener(listener);
        iv_horizontal_two.setOnClickListener(listener);
        iv_four.setOnClickListener(listener);
        iv_album.setOnClickListener(listener);

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
        isDestroy = true;
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
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
        if(takePictureStatus==0){
            takePictureStatus = 1;
            String path = FileUtil.getCacheSaveImagesDir(AlbumCaptureActivity.this)+FileUtil.getNewFileNameByTime()+".jpg";
            Mat temp = new Mat();
            Imgproc.cvtColor(mat, temp, COLOR_RGBA2BGRA);
            Imgcodecs.imwrite(path,temp);
            temp.release();
            temp = null;

            Intent intent = new Intent();
            intent.putExtra("takePictureType",takePictureType);
            intent.putExtra("fileSavePath",path);
            intent.setAction("com.beeinc.album.takpicture.finish");
            sendBroadcast(intent);
            finish();
        }
        return mat;
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
    //    static {
//        System.loadLibrary("detection_based_tracker");
//    }
    private void setCantakePicture(){
        if(takePictureStatus!=1){
            takePictureStatus = 0;
        }
    }
    private void selectAlbum(){
        Intent in = new Intent();
        in.setAction("com.beeinc.album.takpicture.album");
        in.putExtra("takePictureType", takePictureType);
        sendBroadcast(in);
        finish();
    }
    private void showButton( ){
        iv_single.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_singal_unselected, false));
        iv_vertical_two.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_vertical_unselected, false));
        iv_capture.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_capture, false));
        iv_horizontal_two.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_horizontal_unselected, false));
        iv_four.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_four_unslected, false));
        iv_album.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_album, false));
        switch(type){
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
        if(handler == null){
            handler = new Handler();
        }
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
