package com.beeinc.SQSB;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.beeinc.SQSB.receiver.BeeIncReceiverMain;
import com.beeinc.mylibrary.R;
import com.beeinc.mylibrary.activity.ARActivity;
import com.beeinc.mylibrary.activity.AlbumCaptureActivity;
import com.beeinc.mylibrary.activity.CaptureActivity;
import com.beeinc.mylibrary.activity.EditInputActivity;
import com.beeinc.mylibrary.activity.PicturesUploadActivity;
import com.beeinc.mylibrary.activity.UploadActivity;
import com.beeinc.mylibrary.activity.VideoPlayActivity;
import com.beeinc.mylibrary.bean.CategoryType;
import com.beeinc.mylibrary.bean.UploadFile;
import com.beeinc.mylibrary.receiver.BeeIncReceiver;
import com.beeinc.mylibrary.scale.ScreenUtil;
import com.beeinc.mylibrary.util.BroadcastReceiverRegisterUtil;
import com.beeinc.mylibrary.util.ConstantData;
import com.beeinc.mylibrary.util.FileInfo;
import com.beeinc.mylibrary.util.FileUtil;
import com.beeinc.mylibrary.util.ImageDealUtil;
import com.beeinc.mylibrary.util.LiuhaiScreenJudgeUtil;
import com.beeinc.mylibrary.util.MimeType;
import com.beeinc.mylibrary.util.OpenCVUtil;
import com.beeinc.mylibrary.util.PermissionUtil;
import com.beeinc.mylibrary.util.StatusBarUtils;
import com.beeinc.mylibrary.util.SystemUtil;
import com.beeinc.mylibrary.util.UnityCallNative;
import com.beeinc.mylibrary.util.common;
import com.beeinc.SQSB.wxapi.Constants;
import com.beeinc.SQSB.wxapi.Util;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.ImageMultipleWrapper;
import com.yanzhenjie.album.api.widget.Widget;
import com.yuci.okhttp.rxnet.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;

import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

public class MainActivity extends UnityPlayerActivity implements UnityCallNative {

    private ArrayList<AlbumFile> mAlbumFiles;
    private ArrayList<CategoryType> categoryTypes;

    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;

    private int uploadMaxNum;

    private BeeIncReceiverMain receiver;
    private boolean isInitAlbum;

    private long lastTime;//上一次点击返回键按钮的时间。
    private String saveDir;//设置相片路径

    private String saveDirUpload;//上传保存路径
    private int imageMaxNumUpload;//上传最大张数
    private boolean isQuit;//是否是退出界面

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtil.setLayoutNum(this);
        Constant.setBaseUrl("https://15ux669634.iask.in/");
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        initReceiver();
        deleteCacheFiles();
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

    private boolean judgeStoragePermission() {
        boolean isStorage = PermissionUtil.isHavePermission(this, PermissionUtil.TYPE.STORAGE);
        boolean isMediaPermission = PermissionUtil.isHavePermission(this, PermissionUtil.TYPE.ANDROID_13_MEDIA_IMAGES_AND_VIDEOS);
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
    private void deleteCacheFiles(){
        boolean isHaveStoragePermission = PermissionUtil.isHavePermission(this, PermissionUtil.TYPE.STORAGE);
        if(isHaveStoragePermission) {
            String imagesCacheDir = FileUtil.getCacheSaveImagesDir(this);
            String videosCacheDir = FileUtil.getCacheSaveMoviesDir(this);
            File imageDirFile = new File(imagesCacheDir);
            FileUtil.deleteFileDirectory(imageDirFile);
            File videDirFile = new File(videosCacheDir);
            FileUtil.deleteFileDirectory(videDirFile);
        }
    }
    private void regToWx() {
        if (this.judgeStoragePermission() && this.api == null) {
            this.api = WXAPIFactory.createWXAPI(this, "wxee89030b39718286", true);
            this.api.registerApp("wxee89030b39718286");
        }
    }
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
    }
    //unity调用
    public void GetAlbumPath(String json){
        getAlbumPathDuihua(json);
//        getAlbumPath("");
    }
    //unity调用
    public void _SavePhoto(String pictureAddress){
        if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P){
            String fileName = FileUtil.getNewFileNameByTime()+".jpg";
            FileInfo fileInfo = FileInfo.createImageFileInfo(fileName);
            Uri uri = FileUtil.createUri(MainActivity.this,"save", MimeType.jpg, Environment.DIRECTORY_PICTURES,fileInfo,true);
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
    //unity调用
    public void SaveVideo(final String videoAddress){
        new Thread(){
            public void run(){
                FileUtil.copyVideoFileToAlbum(MainActivity.this,getResources().getString(R.string.app_name),videoAddress);
            }
        }.start();
    }
    //unity调用
    public void TakeJingXiangPhoto(String json){
        jingXiangPai(json);
    }
//    //unity调用 老
//    public void GetMultipleAlbumPath(String albumpath,int number,String types){
//        upload(albumpath,number,types);
//    }

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
    //unit调用
    public void ARAction(String kt,String fs){
        arAction(kt,fs);
    }
    //unity调用
    public void  _ShareToWXSession(String WXSharetitle, String WXSharedescription, String imagepath, String WXShareUrl){
        regToWx();
        if (this.judgeStoragePermission()) {
            this.shareUrlToWeixin(2, WXSharetitle, WXSharedescription, imagepath, WXShareUrl);
        }
    }
    //unity调用
    public void  _ShareToWXTimeline(String WXSharetitle, String WXSharedescription, String imagepath, String WXShareUrl){
        regToWx();
        if (this.judgeStoragePermission()) {
            this.shareUrlToWeixin(1, WXSharetitle, WXSharedescription, imagepath, WXShareUrl);
        }
    }
    //unity调用
    public void  _SharePicToWXTimeline(String miniPicPath, String picPath){
        regToWx();
        if (this.api.getWXAppSupportAPI() >= Build.TIMELINE_SUPPORTED_SDK_INT) {
            if (this.judgeStoragePermission()) {
                this.shareImageToWeixin(1, picPath);
            }

        } else {
            Toast.makeText(this, "当前设备不支持分享", Toast.LENGTH_SHORT).show();
        }
    }
    //unity调用
    public void  _SharePicToWXSession(String miniPicPath, String picPath){
        regToWx();
        if (this.api.getWXAppSupportAPI() >= Build.TIMELINE_SUPPORTED_SDK_INT) {
            if (this.judgeStoragePermission()) {
                this.shareImageToWeixin(2, picPath);
            }
        } else {
            Toast.makeText(this, "当前设备不支持分享", Toast.LENGTH_SHORT).show();
        }
    }

    //unity调用显示状态栏和导航栏
    public void showNavigation(){
        showSystemUI();
    }
    //unity调用隐藏状态栏和导航栏
    public void hideNavigation(){
        hideSystemUI();
    }
    private void getAlbumPathDuihua(String filePath){
        if (this.judgeCameraAndStoragePermission()) {
            Intent in = new Intent();
            in.setClass(this, CaptureActivity.class);
            in.putExtra("start_type", 3);
            this.startActivity(in);
        }
    }
    private void getAlbumPath(String filePath){
        if (this.judgeCameraAndStoragePermission()) {
            ((ImageMultipleWrapper)((ImageMultipleWrapper)((ImageMultipleWrapper)((ImageMultipleWrapper)((ImageMultipleWrapper)((ImageMultipleWrapper)Album.image(this).multipleChoice()).camera(true)).columnCount(2)).selectCount(1).checkedList(this.mAlbumFiles).widget(Widget.newDarkBuilder(this).title(this.getResources().getString(com.yanzhenjie.album.R.string.album_album)).build())).onResult(new Action<ArrayList<AlbumFile>>() {
                public void onAction(@NonNull ArrayList<AlbumFile> result) {
                    MainActivity.this.mAlbumFiles = result;
                    if (result.size() > 0) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                UnityPlayer.UnitySendMessage("UniReciveObj", "GetAlbumPathFinish", ((AlbumFile)MainActivity.this.mAlbumFiles.get(0)).getPath());
                            }
                        });
                    }

                }
            })).onCancel(new Action<String>() {
                public void onAction(@NonNull String result) {
                    Toast.makeText(MainActivity.this, R.string.canceled, Toast.LENGTH_LONG).show();
                    
                }
            })).start();
        }
    }
    private void jingXiangPai(String json){
        if (this.judgeCameraAndStoragePermission()) {
            Intent jingxiangpai = new Intent();
            jingxiangpai.putExtra("start_type", 2);
            jingxiangpai.setClass(this, CaptureActivity.class);
            this.startActivity(jingxiangpai);
        }
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
    private void shareUrlToWeixin(int type,String title,String des,String imagePath,String url){
        Log.e("----------->","api.getWXAppSupportAPI()="+api.getWXAppSupportAPI());
        Log.e("----------->","Build.TIMELINE_SUPPORTED_SDK_INT="+Build.TIMELINE_SUPPORTED_SDK_INT);

        if (api.getWXAppSupportAPI() >= Build.TIMELINE_SUPPORTED_SDK_INT) {//是否支持发送到朋友圈
            //do share
        }else{//
            Toast.makeText(this,"当前设备不支持分享",Toast.LENGTH_SHORT ).show();
            return;
        }
        //初始化一个WXWebpageObject，填写url
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl =url;

        //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title =title;
        msg.description =des;
        Bitmap bitmap = ImageDealUtil.decodeFile(imagePath, ConstantData.maxDecodeImageWidth);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap,150,150,true);
        msg.thumbData =Util.bmpToByteArray(thumbBmp, true);

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message =msg;
        if(type==1){
            req.scene = WXSceneTimeline;
        }
        if(type==2){
            req.scene = WXSceneSession;
        }
        //调用api接口，发送数据到微信
        api.sendReq(req);
    }
    /**
     *
     * @param type 1 朋友圈，2 朋友
     */
    private void shareImageToWeixin(int type,String picPath){

        Bitmap bitmap = ImageDealUtil.decodeFile(picPath, 4000);
        WXImageObject imgObj = new WXImageObject(bitmap);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
        bitmap.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        if (type == 1) {
            req.scene = 1;
        }

        if (type == 2) {
            req.scene = 0;
        }

        this.api.sendReq(req);
    }
    private void upload(String albumPath,int num,String types){
        if (this.judgeCameraAndStoragePermission()) {
            this.saveDirUpload = albumPath;
            this.uploadMaxNum = num;

            try {
                JSONArray array = new JSONArray(types);
                this.categoryTypes = new ArrayList();

                for(int i = 0; i < array.length(); ++i) {
                    JSONObject json = array.getJSONObject(i);
                    CategoryType type = new CategoryType();
                    type.setCategory(json.getString("name"));
                    type.setCategory_id(json.getString("id"));
                    this.categoryTypes.add(type);
                }

                this.selectAlbumToUpload(albumPath, num);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private void selectAlbumToUpload(String filePath, int num){
        saveDirUpload = filePath;
        uploadMaxNum = num;
        if(mAlbumFiles!=null){
            mAlbumFiles.clear();
        }
        Album.image(MainActivity.this)
                .multipleChoice()
                .camera(true)
                .columnCount(2)
                .selectCount(uploadMaxNum)
                .checkedList(mAlbumFiles)
                .setTakePictureType(5)
                .widget(
                        Widget.newDarkBuilder(MainActivity.this)
                                .title(getResources().getString(com.yanzhenjie.album.R.string.album_album))
                                .build()
                )
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        mAlbumFiles = result;
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
                                    startUploadActivity(saveDirUpload,uploadMaxNum,files);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.PermissionGrantResutListener listener = new PermissionUtil.PermissionGrantResutListener() {
            @Override
            public void grantSuccess(PermissionUtil.TYPE type) {
                if (type.equals(PermissionUtil.TYPE.CAMERA) && MainActivity.this.judgeCameraAndStoragePermission()) {
                    if (MainActivity.this.getCameraAndAlbumPermissionFor == 1) {
                        MainActivity.this.GetMultipleAlbumPathV1Implement(MainActivity.this.multipleAlbumPathInfo.getSaveDir(), MainActivity.this.multipleAlbumPathInfo.getImageNum(), MainActivity.this.multipleAlbumPathInfo.getTypes());
                    }

                    if (MainActivity.this.getCameraAndAlbumPermissionFor == 2) {
                        MainActivity.this.cutCapture(MainActivity.this.albumPath);
                    }
                }

                if (type.equals(PermissionUtil.TYPE.STORAGE) && MainActivity.this.judgeCameraAndStoragePermission()) {
                    if (MainActivity.this.getCameraAndAlbumPermissionFor == 1) {
                        MainActivity.this.GetMultipleAlbumPathV1Implement(MainActivity.this.multipleAlbumPathInfo.getSaveDir(), MainActivity.this.multipleAlbumPathInfo.getImageNum(), MainActivity.this.multipleAlbumPathInfo.getTypes());
                    }

                    if (MainActivity.this.getCameraAndAlbumPermissionFor == 2) {
                        MainActivity.this.cutCapture(MainActivity.this.albumPath);
                    }
                }

                if (type.equals(PermissionUtil.TYPE.ANDROID_13_MEDIA_IMAGES_AND_VIDEOS) && MainActivity.this.judgeCameraAndStoragePermission()) {
                    if (MainActivity.this.getCameraAndAlbumPermissionFor == 1) {
                        MainActivity.this.GetMultipleAlbumPathV1Implement(MainActivity.this.multipleAlbumPathInfo.getSaveDir(), MainActivity.this.multipleAlbumPathInfo.getImageNum(), MainActivity.this.multipleAlbumPathInfo.getTypes());
                    }

                    if (MainActivity.this.getCameraAndAlbumPermissionFor == 2) {
                        MainActivity.this.cutCapture(MainActivity.this.albumPath);
                    }
                }

            }
            @Override
            public void grantFailed(PermissionUtil.TYPE type) {
                if (type.equals(PermissionUtil.TYPE.CAMERA)) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.camera_permission_disabled), Toast.LENGTH_LONG).show();
                }

                if (type.equals(PermissionUtil.TYPE.STORAGE)) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.storage_permissions_disabled), Toast.LENGTH_LONG).show();
                }

                if (type.equals(PermissionUtil.TYPE.ANDROID_13_MEDIA_IMAGES_AND_VIDEOS)) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.media_permissions_disabled), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void grantFailedAndNotAllowRequest(PermissionUtil.TYPE type) {
                if (type.equals(PermissionUtil.TYPE.CAMERA)) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.please_open_camera_permissions), Toast.LENGTH_LONG).show();
                    PermissionUtil.toAppSelfSetting(MainActivity.this);
                }

                if (type.equals(PermissionUtil.TYPE.STORAGE)) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.please_open_storage_permissions), Toast.LENGTH_LONG).show();
                    PermissionUtil.toAppSelfSetting(MainActivity.this);
                }

                if (type.equals(PermissionUtil.TYPE.ANDROID_13_MEDIA_IMAGES_AND_VIDEOS)) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.please_open_media_permissions), Toast.LENGTH_LONG).show();
                    PermissionUtil.toAppSelfSetting(MainActivity.this);
                }

                if (type.equals(PermissionUtil.TYPE.POST_NOTIFICATIONS)) {
                    Toast.makeText(MainActivity.this, "Please open notification permissions", Toast.LENGTH_LONG).show();
                    PermissionUtil.toAppSelfSetting(MainActivity.this);
                }
            }
        };
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults, PermissionUtil.TYPE.CAMERA, listener);
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults, PermissionUtil.TYPE.STORAGE, listener);
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults, PermissionUtil.TYPE.ANDROID_13_MEDIA_IMAGES_AND_VIDEOS, listener);
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults, PermissionUtil.TYPE.POST_NOTIFICATIONS, listener);


    }
    public void arAction(String kt,String fs){
        if (this.judgeCameraAndStoragePermission()) {
            Intent in = new Intent();
            in.setClass(this, ARActivity.class);
            in.putExtra("fileFsPath", fs);
            in.putExtra("fileKtPath", kt);
            this.startActivity(in);
        }
    }
    private void unityMessageList(){
        UnityPlayer.UnitySendMessage("IOsReciveObj","GetAlbumPathFinish",mAlbumFiles.get(0).getPath());
        UnityPlayer.UnitySendMessage("IOsReciveObj","GetMultipleAlbumPathFinish",mAlbumFiles.get(0).getPath());
        UnityPlayer.UnitySendMessage("IOsReciveObj","GetAlbumPathFromCutFinish",mAlbumFiles.get(0).getPath());
        UnityPlayer.UnitySendMessage("IOsReciveObj","GetJingXiangPhotoFinish",mAlbumFiles.get(0).getPath());
        UnityPlayer.UnitySendMessage("IOsReciveObj","OnGetNativeContent4ArticleFinish",mAlbumFiles.get(0).getPath());

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
    private void initReceiver(){
        receiver =new BeeIncReceiverMain();
        IntentFilter filter= new IntentFilter();
        filter.addAction("com.beeinc.album.takpicture");
        filter.addAction("com.beeinc.album.takpicture.finish");
        filter.addAction("com.beeinc.album.takpicture.album");
        filter.addAction("com.beeinc.select.video");
        filter.addAction("com.beeinc.select.phone.album");
        BroadcastReceiverRegisterUtil.registerReceiver(this, this.receiver, filter, 2);
    }
    public void onDestroy(){
        super.onDestroy();
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            long now= System.currentTimeMillis();
            if(now-lastTime<=1500){
                isQuit =true;
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());//获取PID
                System.exit(0);
            }else{
                lastTime = now;
                Toast.makeText(this,getResources().getString(R.string.press_again_to_exit_stone_idea),Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void hideSystemUI() {
        if (android.os.Build.VERSION.SDK_INT > 11 && android.os.Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (android.os.Build.VERSION.SDK_INT >= 19) {
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
        if (android.os.Build.VERSION.SDK_INT > 11 && android.os.Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (android.os.Build.VERSION.SDK_INT >= 19) {
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
    //unity调用 显示输入框 type 用于Unity判断入口位置，cotent 用于Unity给输入框带入内容
    public void OpenEditInputPage(int type,String content){
        Intent in = new Intent();
        in.putExtra("type",type);
        if(content==null){
            content="";
        }
        in.putExtra("content",content);
        common.gotoActivity(this, EditInputActivity.class,in);
    }
    //Unity调用 判断是否是平板
    public boolean isPad(){
        boolean b= SystemUtil.isPad(MainActivity.this);
        return b;
    }
    //广播调用播放视频
    public void selectToPlayVideo(Intent intent){
        AlbumFile albumFile = intent.getParcelableExtra("albumFile");
        Intent in = new Intent();
        in.putExtra("albumFile",albumFile);
        in.putExtra("savePath",saveDir);
        common.gotoActivity(MainActivity.this,VideoPlayActivity.class,in);
    }
    private void  startPicturesUploadActivity(String saveDir,int imageMaxNum,ArrayList<UploadFile> files){
        Intent intent = new Intent();
        intent.putExtra("fileSelected", files);
        intent.putExtra("categoryTypes", categoryTypes);
        intent.putExtra("saveDir",saveDir);
        intent.putExtra("num",imageMaxNum);
        common.gotoActivity(this,PicturesUploadActivity.class,intent);
    }
    @Override
    public void onResume(){
        super.onResume();
        isQuit = false;
    }
    @Override
    public void onStop(){
        super.onStop();
//        if(!isQuit){
//            this.mUnityPlayer.start();
//            this.mUnityPlayer.resume();
//        }
    }
    private void getPermissionOfStorage() {
        PermissionUtil.startRequestPermission(this, PermissionUtil.TYPE.STORAGE, new PermissionUtil.RequestPermissionListener() {
            public void havePermission() {
            }

            public void canRequestPermission() {
            }

            public void notAllowRquestAgain() {
                Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.please_open_storage_permissions), Toast.LENGTH_LONG).show();
                PermissionUtil.toAppSelfSetting(MainActivity.this);
            }
        });
    }

    private void getMediaPermission() {
        PermissionUtil.startRequestPermission(this, PermissionUtil.TYPE.ANDROID_13_MEDIA_IMAGES_AND_VIDEOS, new PermissionUtil.RequestPermissionListener() {
            public void havePermission() {
            }

            public void canRequestPermission() {
            }

            public void notAllowRquestAgain() {
                Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.please_open_media_permissions), Toast.LENGTH_LONG).show();
                PermissionUtil.toAppSelfSetting(MainActivity.this);
            }
        });
    }

    private void getPermissionOfCamera() {
        PermissionUtil.startRequestPermission(this, PermissionUtil.TYPE.CAMERA, new PermissionUtil.RequestPermissionListener() {
            public void havePermission() {
            }

            public void canRequestPermission() {
            }

            public void notAllowRquestAgain() {
                Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.please_open_camera_permissions), Toast.LENGTH_LONG).show();
                PermissionUtil.toAppSelfSetting(MainActivity.this);
            }
        });
    }
    @Override
    public void GetMultipleAlbumPath(String saveDir, int imageNum, String types) {
        multipleAlbumPathInfo = new GetMultipleAlbumPathInfo(saveDir,imageNum,types);
        //获取权限
        getCameraAndAlbumPermissionFor = 1;
        if (this.judgeCameraAndStoragePermission()) {
            this.GetMultipleAlbumPathV1Implement(this.multipleAlbumPathInfo.getSaveDir(), this.multipleAlbumPathInfo.getImageNum(), this.multipleAlbumPathInfo.getTypes());
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
                                .title(getResources().getString(com.yanzhenjie.album.R.string.album_album))
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
    public void RequestLocationPermission() {
    }

    private void LocationPermissionResult(String result) {
    }

    public void getNotificationForUnity() {
        if (SystemUtil.getIsHigherThanAndroidTIRAMISU() && !PermissionUtil.isHavePermission(this, PermissionUtil.TYPE.POST_NOTIFICATIONS)) {
            this.getPermissionOfNotification();
        }

    }
    private void getPermissionOfNotification() {
        PermissionUtil.startRequestPermission(this, PermissionUtil.TYPE.POST_NOTIFICATIONS, new PermissionUtil.RequestPermissionListener() {
            public void havePermission() {
            }

            public void canRequestPermission() {
            }

            public void notAllowRquestAgain() {
                Toast.makeText(MainActivity.this, "Please open notification permissions", Toast.LENGTH_LONG).show();
                PermissionUtil.toAppSelfSetting(MainActivity.this);
            }
        });
    }
}
