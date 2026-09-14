package com.beeinc.mylibrary;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.beeinc.mylibrary.R;
import com.beeinc.mylibrary.activity.ARActivity;
import com.beeinc.mylibrary.activity.AlbumCaptureActivity;
import com.beeinc.mylibrary.activity.CaptureActivity;
import com.beeinc.mylibrary.activity.EditInputActivity;
import com.beeinc.mylibrary.activity.PicturesUploadActivity;
import com.beeinc.mylibrary.activity.ShareActivity;
import com.beeinc.mylibrary.activity.UploadActivity;
import com.beeinc.mylibrary.activity.VideoPlayActivity;
import com.beeinc.mylibrary.bean.CategoryType;
import com.beeinc.mylibrary.bean.UploadFile;
import com.beeinc.mylibrary.receiver.BeeIncReceiverMain;
import com.beeinc.mylibrary.util.BroadcastReceiverRegisterUtil;
import com.beeinc.mylibrary.util.FileInfo;
import com.beeinc.mylibrary.scale.ScreenUtil;
import com.beeinc.mylibrary.util.FileUtil;
import com.beeinc.mylibrary.util.LiuhaiScreenJudgeUtil;
import com.beeinc.mylibrary.util.MimeType;
import com.beeinc.mylibrary.util.OpenCVUtil;
import com.beeinc.mylibrary.util.PermissionSharePreference;
import com.beeinc.mylibrary.util.PermissionUtil;
import com.beeinc.mylibrary.util.StatusBarUtils;
import com.beeinc.mylibrary.util.SystemUtil;
import com.beeinc.mylibrary.util.UnityCallNative;
import com.beeinc.mylibrary.util.common;
import com.unity3d.player.UnityPlayer;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;
import com.yuci.okhttp.rxnet.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends FragmentActivity implements UnityCallNative {

    Button tv_upload_pictures,testcut,button_jingixangpai,button_ar,cut_capture,duihua_capture,share,upload;
    Button showNavigationButton,hideNavigationButton;
    private String saveDir;//设置相片路径
    private String TAG = "MainActivity";
    private ArrayList<AlbumFile> mAlbumFiles;

    private BeeIncReceiverMain receiver;
    private boolean isInitAlbum;

    private boolean isDestroy = false;
    private Handler handler;
    private long lastTime;//上一次点击返回键按钮的时间。

    private String saveDirUpload;//上传保存路径
    private int imageMaxNumUpload;//上传最大张数
    private ArrayList<CategoryType> categoryTypes;//上传用的分类


    private GetMultipleAlbumPathInfo multipleAlbumPathInfo;
    private String albumPath;//图片裁剪路径
    private int getCameraAndAlbumPermissionFor;//1.多图上传，2.图片裁剪

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
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("-------->","MainActivity onCreate");
        PermissionSharePreference.init(this);
        setContentView(R.layout.activity_main);
        ScreenUtil.setLayoutNum(this,true);
        Constant.setBaseUrl("https://15ux669634.iask.in/");
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        if(!isInitAlbum){
            initAlbum();
        }
        tv_upload_pictures = findViewById(R.id.tv_upload_pictures);
        testcut = findViewById(R.id.testcut);
        button_jingixangpai = findViewById(R.id.button_jingixangpai);
        button_ar = findViewById(R.id.button_ar);
        cut_capture = findViewById(R.id.cut_capture);
        duihua_capture = findViewById(R.id.duihua_capture);
        share = findViewById(R.id.share);
        upload = findViewById(R.id.upload);
        showNavigationButton = findViewById(R.id.showNavigationButton);
        hideNavigationButton = findViewById(R.id.hideNavigationButton);

        initListener();
        deleteCacheFiles();
        initReceiver();
        isDestroy = false;
        RequestLocationPermission();

    }
    private void deleteCacheFiles(){
        String imagesCacheDir = FileUtil.getCacheSaveImagesDir(this);
        String videosCacheDir = FileUtil.getCacheSaveMoviesDir(this);
        File imageDirFile = new File(imagesCacheDir);
        FileUtil.deleteFileDirectory(imageDirFile);
        File videDirFile = new File(videosCacheDir);
        FileUtil.deleteFileDirectory(videDirFile);
    }
    private void initReceiver(){
        receiver =new BeeIncReceiverMain();
        IntentFilter filter= new IntentFilter();
        filter.addAction("com.beeinc.album.takpicture");
        filter.addAction("com.beeinc.album.takpicture.finish");
        filter.addAction("com.beeinc.album.takpicture.album");
        filter.addAction("com.beeinc.select.video");
        filter.addAction("com.beeinc.select.phone.album");
        BroadcastReceiverRegisterUtil.registerReceiver(this,receiver, filter,true);
    }
    public void initListener(){
        OnClickListener listener = new OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View view) {
                if(view.getId()== R.id.tv_upload_pictures){
                    String saveDir = FileUtil.getCacheSaveImagesDir(MainActivity.this);
                    CategoryType categoryType = new CategoryType();
                    categoryType.setCategory("测试");
                    categoryType.setCategory_id("0");
                    categoryType.setSort(1);
                    ArrayList<CategoryType> categoryTypes = new ArrayList<>();
                    categoryTypes.add(categoryType);
                    JSONArray array = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("sort",categoryType.getSort());
                        jsonObject.put("id",categoryType.getCategory_id());
                        jsonObject.put("name",categoryType.getCategory());
                        array.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    GetMultipleAlbumPath(saveDir,6,array.toString());

                }
                if(view.getId()== R.id.testcut){
                    Intent in = new Intent();
//                        in.putExtra("start_type", 1);
//                        in.putExtra("filePath",FileUtil.createAndroidQFilePath(MainActivity.this,"pictures"));
//                        in.setClass(MainActivity.this, CutPictureActivity.class );
//                        startActivity(in);
                }
                if(view.getId()== R.id.button_jingixangpai){
                    Intent jingxiangpai = new Intent();
                    jingxiangpai.putExtra("start_type", 2);
                    jingxiangpai.setClass(MainActivity.this, CaptureActivity.class);
                    startActivity(jingxiangpai);
                }
                if(view.getId()== R.id.button_ar){
                    Intent scale = new Intent();
                    scale.setClass(MainActivity.this, ARActivity.class);
                    scale.putExtra("fileFsPath", Environment.getExternalStorageDirectory()+"/test_fs.png");
                    scale.putExtra("fileKtPath",Environment.getExternalStorageDirectory()+"/test_kt.png");
                    Log.e("---------------------->","路径"+Environment.getExternalStorageDirectory()+"/test_fs.png");
                    startActivity(scale);
                }
                if(view.getId()== R.id.cut_capture){
                    String filePath;
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                        filePath = FileUtil.getCacheSaveImagesDir(MainActivity.this);
                    }else{
                        filePath = FileUtil.getCacheSaveImagesDir(MainActivity.this);
                    }
                    GetAlbumPathFromCut(filePath);
                }
                if(view.getId()== R.id.duihua_capture){
                    Intent duihua = new Intent();
                    duihua.putExtra("start_type", 3);
                    duihua.setClass(MainActivity.this, CaptureActivity.class);
                    startActivity(duihua);
                }
                if(view.getId()== R.id.share){
                    Intent share = new Intent();
                    share.setClass(MainActivity.this, ShareActivity.class);
                    startActivity(share);
                }
                if(view.getId()== R.id.upload){
                    upload();
                }
                if(view.getId()== R.id.showNavigationButton){
                    showSystemUI();
                }
                if(view.getId()== R.id.hideNavigationButton){
                    hideSystemUI();

                }
            }
        };
        tv_upload_pictures.setOnClickListener(listener);
        testcut.setOnClickListener(listener);
        button_jingixangpai.setOnClickListener(listener);
        button_ar.setOnClickListener(listener);
        cut_capture.setOnClickListener(listener);
        duihua_capture.setOnClickListener(listener);
        share.setOnClickListener(listener);
        upload.setOnClickListener(listener);
        showNavigationButton.setOnClickListener(listener);
        hideNavigationButton.setOnClickListener(listener);
    }
    public void upload(){
        try {
            JSONArray testArray = new JSONArray();
            for(int i=0;i<10;i++){
                JSONObject type = new JSONObject();
                type.put("name", "默认分类"+(i+1));
                type.put("id", ""+(i+1));
                type.put("sort", i);
                testArray.put(type);
            }
            try {
                JSONArray array = new JSONArray(testArray.toString());
                categoryTypes = new ArrayList<CategoryType>();
                for(int i=0;i<array.length();i++){
                    JSONObject json = array.getJSONObject(i);
                    CategoryType type = new CategoryType();
                    type.setCategory(json.getString("name"));
                    type.setCategory_id(json.getString("id"));
                    categoryTypes.add(type);
                }
                selectAlbumToUpload(FileUtil.getCacheSaveImagesDir(this), 6);
            } catch (JSONException e) {
                e.printStackTrace();
            };
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void selectAlbumToUpload(String saveDir, int num){
        saveDirUpload = saveDir;
        imageMaxNumUpload = num;
        Album.image(MainActivity.this)
                .multipleChoice()
                .camera(true)
                .columnCount(2)
                .selectCount(num)
                .checkedList(null)
                .setTakePictureType(5)
                .widget(
                        Widget.newDarkBuilder(MainActivity.this)
                                .title("选择图片")
                                .build()
                )
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        if(result.size()>0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayList<UploadFile> files = new ArrayList<UploadFile>();
                                    for(int i=0;i<mAlbumFiles.size();i++) {
                                        UploadFile uploadFile = new UploadFile();
                                        uploadFile.setFilePath(mAlbumFiles.get(i).getPath());
                                        files.add(uploadFile);
                                    }
                                    startUploadActivity(saveDirUpload,imageMaxNumUpload,files);

                                }
                            });
                        }
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toast.makeText(MainActivity.this, R.string.canceled, Toast.LENGTH_LONG).show();
                    }
                })
                .start();
    }

    private void startUploadActivity(String saveDir,int num,ArrayList<UploadFile> files){
        Intent intent = new Intent();
        intent.putExtra("fileSelected", files);
        intent.putExtra("categoryTypes", categoryTypes);
        intent.putExtra("fileDir",saveDir);
        intent.putExtra("num",num);
        intent.setClass(MainActivity.this, UploadActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
    }

    /**
     * 解码图像用来减少内存消耗
     */
    private Bitmap decodeFile(File f) {
        try {
            // 解码图像大小
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // 找到正确的刻度值，它应该是2的幂。
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    private void initAlbum(){
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .setLocale(Locale.getDefault())
                .build()
        );
    }
    public void startAlbumCapture(int type){
        Log.e("-------->","startAlbumCapture");
        //上传调用相册
        if(type==5){
            Intent intent = new Intent(MainActivity.this, AlbumCaptureActivity.class);
            intent.putExtra("takePictureType",type);
            startActivity(intent);
        }
        //上传调用相册 新
        if(type==7){
            Intent intent = new Intent(MainActivity.this, AlbumCaptureActivity.class);
            intent.putExtra("takePictureType",type);
            startActivity(intent);
        }
    }
    //广播调用上传拍照结束
    public void albumCaptureFinish(Intent intent){
        String fileSavePath = intent.getStringExtra("fileSavePath");
        int takePictureType = intent.getIntExtra("takePictureType",1);

        String dir = FileUtil.getCacheSaveImagesDir(MainActivity.this);
        //保存到内部缓存
        String fileName = FileUtil.getNewFileNameByTime()+".jpg";
        String filePath = dir+fileName;
        Uri uri = FileUtil.fileToUri(this,fileSavePath);
        OpenCVUtil.reSaveFile(MainActivity.this,fileSavePath,uri.toString(),filePath,2300);
        ArrayList<UploadFile> files = new ArrayList<UploadFile>();
        UploadFile file = new UploadFile();
        file.setFilePath(fileSavePath);
        files.add(file);

        if(takePictureType ==5){
            startUploadActivity(saveDirUpload,imageMaxNumUpload,files);
        }else if(takePictureType==7){
            startPicturesUploadActivity(saveDirUpload,imageMaxNumUpload,files);
        }
    }
    //广播调用上传拍照切换到相册
    public void albumCaptureToAlbum(int takePictureType){
        if(takePictureType==5){
            selectAlbumToUpload(saveDirUpload, imageMaxNumUpload);
        }
        if(takePictureType==7){
            selectAlbumToUpload(saveDirUpload, imageMaxNumUpload);
        }
    }
    public void onDestroy(){
        super.onDestroy();
        isDestroy = true;
        if(receiver!=null){
            unregisterReceiver(receiver);
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
    private void showSystemUI() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;//全部显示
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            long now= System.currentTimeMillis();
            if(now-lastTime<=1500){
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());//获取PID
                System.exit(0);
            }else{
                lastTime = now;
                Toast.makeText(this,"再按一次退出"+getResources().getString(R.string.app_name),Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //unity调用
    public void _SavePhoto(String pictureAddress){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
            String fileName = FileUtil.getNewFileNameByTime()+".jpg";
            FileInfo fileInfo = FileInfo.createImageFileInfo(fileName);
            Uri uri = FileUtil.createUri(MainActivity.this,"save", MimeType.jpg,Environment.DIRECTORY_PICTURES,fileInfo,true);
            FileUtil.copyFileToUri(MainActivity.this,pictureAddress,uri,false);
        }else{
            String path = FileUtil.getAlbumSaveImagesDir(MainActivity.this,getResources().getString(R.string.app_name))+System.currentTimeMillis()+".jpg";
            FileUtil.copyFileToFile(MainActivity.this,pictureAddress,path,false,false);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(path));
            intent.setData(uri);
            sendBroadcast(intent);
        }
    }
    //unity调用 判断刘海
    public String GetScreenLiuhaiInfo(){
        boolean isLiuhai = LiuhaiScreenJudgeUtil.hasNotchInScreen(this);
        int statusBarHeight = StatusBarUtils.getStatusBarHeight(this);
        if(statusBarHeight>80){
            isLiuhai = true;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("isLiuhaiScreeen",isLiuhai);
            json.put("statusBarHeight",statusBarHeight);
//            UnityPlayer.UnitySendMessage("IOsReciveObj","GetScreenLiuhaiInfoFinish",json.toString());
            return json.toString();
        } catch (JSONException e) {
            return "{\"isLiuhaiScreeen\":"+isLiuhai+",\"statusBarHeight\":"+statusBarHeight+"}";
        }
    }
    //unity调用 显示输入框
    public void OpenEditInputPage(int type,String content){
        Intent in = new Intent();
        in.putExtra("type",type);
        if(content==null){
            content="";
        }
        in.putExtra("content",content);
        common.gotoActivity(this, EditInputActivity.class,in);
    }
    //广播调用播放视频
    public void selectToPlayVideo(Intent intent){
        AlbumFile albumFile = intent.getParcelableExtra("albumFile");
        Intent in = new Intent();
        in.putExtra("albumFile",albumFile);
        in.putExtra("savePath",saveDir);
        common.gotoActivity(MainActivity.this,VideoPlayActivity.class,in);
    }
    //unity调用
    public void GetAlbumPathFromCut(String albumPath){
        this.albumPath = albumPath;
        getCameraAndAlbumPermissionFor = 2;
        File file = new File(albumPath);
        if(!file.exists()||file.isFile()){
            file.mkdirs();
        }
        getPermissionOfCamera();
    }
    private void getPermissionOfCamera() {
        PermissionUtil.startRequestPermission(this, PermissionUtil.TYPE.CAMERA, new PermissionUtil.RequestPermissionListener()
        {
            public void havePermission() {
                Toast.makeText(MainActivity.this, "您已获得此权限", Toast.LENGTH_LONG).show();
            }

            public void canRequestPermission()
            {
            }

            public void notAllowRquestAgain()
            {
                Toast.makeText(MainActivity.this, "请打开相机权限", Toast.LENGTH_LONG).show();
                PermissionUtil.toAppSelfSetting(MainActivity.this);
            }
        });
    }

    private void getPermissionOfStorage() {
        PermissionUtil.startRequestPermission(this, PermissionUtil.TYPE.STORAGE, new PermissionUtil.RequestPermissionListener()
        {
            public void havePermission() {
                Toast.makeText(MainActivity.this, "您已获得此权限", Toast.LENGTH_LONG).show();
            }

            public void canRequestPermission()
            {
            }

            public void notAllowRquestAgain()
            {
                Toast.makeText(MainActivity.this, "请打开存储权限", Toast.LENGTH_LONG).show();
                PermissionUtil.toAppSelfSetting(MainActivity.this);
            } } );
    }
    private void getMediaPermission() {
        PermissionUtil.startRequestPermission(this, PermissionUtil.TYPE.ANDROID_13_MEDIA_IMAGES_AND_VIDEOS, new PermissionUtil.RequestPermissionListener()
        {
            public void havePermission() {
                Toast.makeText(MainActivity.this, "您已获得此权限", Toast.LENGTH_LONG).show();
            }

            public void canRequestPermission()
            {
            }

            public void notAllowRquestAgain()
            {
                Toast.makeText(MainActivity.this, "请打开照片和视频权限", Toast.LENGTH_LONG).show();
                PermissionUtil.toAppSelfSetting(MainActivity.this);
            } } );
    }
    private void cutCapture(String albumPath){
        if(!albumPath.endsWith(File.separator)){
            albumPath = albumPath+File.separator;
        }
        Intent cut_capture = new Intent();
        cut_capture.putExtra("start_type", 1);
        cut_capture.putExtra("fileDir",albumPath);
        cut_capture.setClass(MainActivity.this, CaptureActivity.class);
        startActivity(cut_capture);
    }
    private boolean judgeCameraAndStoragePermission() {
        boolean isCamera = PermissionUtil.isHavePermission(this, PermissionUtil.TYPE.CAMERA);
        boolean isStorage = PermissionUtil.isHavePermission(this, PermissionUtil.TYPE.STORAGE);
        boolean isMediaPermission = PermissionUtil.isHavePermission(this, PermissionUtil.TYPE.ANDROID_13_MEDIA_IMAGES_AND_VIDEOS);
        if (!isCamera) {
            this.getPermissionOfCamera();
            return false;
        } else {
            if (SystemUtil.getIsHigherThanAndroidTIRAMISU()) {
                if (!isMediaPermission) {
                    this.getMediaPermission();
                    return false;
                }
            } else if (!isStorage) {
                this.getPermissionOfStorage();
                return false;
            }

            return true;
        }
    }
    @Override
    public void GetMultipleAlbumPath(String saveDir, int imageNum, String types) {
        multipleAlbumPathInfo = new GetMultipleAlbumPathInfo(saveDir,imageNum,types);
        //获取权限
        getCameraAndAlbumPermissionFor = 1;
        if(judgeCameraAndStoragePermission()){
            GetMultipleAlbumPathV1Implement(multipleAlbumPathInfo.getSaveDir(),multipleAlbumPathInfo.getImageNum() ,multipleAlbumPathInfo.getTypes());

        }
    }
    private void GetMultipleAlbumPathV1Implement(final String saveDir, final int imageNum, final String types) {
        this.saveDirUpload = saveDir;
        this.imageMaxNumUpload = imageNum;
        if (mAlbumFiles != null) {
            mAlbumFiles.clear();
        }
        try {
            JSONArray array = new JSONArray(types);
            categoryTypes = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                CategoryType type = new CategoryType();
                type.setCategory(json.getString("name"));
                type.setCategory_id(json.getString("id"));
                categoryTypes.add(type);
            }
        }catch ( Exception exception){

        }
        Album.image(MainActivity.this)
                .multipleChoice()
                .camera(true)
                .columnCount(2)
                .selectCount(imageNum)
                .checkedList(mAlbumFiles)
                .setTakePictureType(7)
                .widget(
                        Widget.newDarkBuilder(MainActivity.this)
                                .title("选择图片")
                                .build()
                )
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        mAlbumFiles = result;
                        if (result.size() > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayList<UploadFile> files = new ArrayList<UploadFile>();
                                    String dir = FileUtil.getCacheSaveImagesDir(MainActivity.this);
                                    for (int i = 0; i < mAlbumFiles.size(); i++) {
                                        //保存到内部缓存
                                        String fileName = FileUtil.getNewFileNameByTime()+"_"+i+".jpg";
                                        String filePath = dir+fileName;
                                        OpenCVUtil.reSaveFile(MainActivity.this,mAlbumFiles.get(i).getPath(),mAlbumFiles.get(i).getUriContentPath(),filePath,2300);
                                        UploadFile uploadFile = new UploadFile();
                                        uploadFile.setFilePath(filePath);
                                        files.add(uploadFile);
                                    }
                                    startPicturesUploadActivity(saveDir,imageNum,files);
                                }
                            });
                        }
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toast.makeText(MainActivity.this, R.string.canceled, Toast.LENGTH_LONG).show();
                    }
                })
                .start();
    }

    private void  startPicturesUploadActivity(String saveDir,int imageMaxNum,ArrayList<UploadFile> files){
        Intent intent = new Intent();
        intent.putExtra("fileSelected", files);
        intent.putExtra("categoryTypes", categoryTypes);
        intent.putExtra("saveDir",saveDir);
        intent.putExtra("num",imageMaxNum);
        common.gotoActivity(this,PicturesUploadActivity.class,intent);
    }

    //unity调用，判断是否有定位权限
    public void RequestLocationPermission(){
//        if(!PermissionUtil.isLocServiceEnable(this)){
//            Toast.makeText(this,"请开启定位服务",Toast.LENGTH_SHORT).show();
//            LocationPermissionResult("NO");
//            return;
//        }
//        PermissionUtil.startRequestPermission(this, PermissionUtil.TYPE.LOCATION);
    }
    private void LocationPermissionResult(String result){
        //reuslt: OK 获取到权限，NO 未获取到权限,
//        UnityPlayer.UnitySendMessage("IOsReciveObj","LocationPermissionResult",result);
    }
}
