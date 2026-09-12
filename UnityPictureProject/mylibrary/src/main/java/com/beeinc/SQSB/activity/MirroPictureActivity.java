package com.beeinc.SQSB.activity;

import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2BGRA;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beeinc.SQSB.R;
import com.beeinc.SQSB.scale.Layout;
import com.beeinc.SQSB.scale.ScreenUtil;
import com.beeinc.SQSB.util.ConstantData;
import com.beeinc.SQSB.util.FileInfo;
import com.beeinc.SQSB.util.FileUtil;
import com.beeinc.SQSB.util.ImageDealUtil;
import com.beeinc.SQSB.util.MimeType;
import com.beeinc.SQSB.util.OpenCVUtil;
import com.beeinc.SQSB.util.WaterMarkUtil;
import com.beeinc.SQSB.views.ImageCutView;
import com.beeinc.SQSB.wxapi.Constants;
import com.beeinc.SQSB.wxapi.Util;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yanzhenjie.album.AlbumFile;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class MirroPictureActivity extends Activity {

    private FrameLayout root_view;
    private ImageCutView cutView;
    private ImageView iv_share;
    private TextView tv_hint_title, tv_hint_desc, tv_cancel, tv_rotate, tv_recapture,tv_save_album,tv_share,tv_preview;
    private View v_bottom_back, v_rotate;
    private ImageView iv_rotate;
    private FrameLayout frame;
    private String filePath;

    private boolean isSaveToAlbum = true;
    private int type;//类型  0 单拍或相册，1 镜像拍
    private final int SHARE_REQUEST_CODE = 10001;

    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;
    private Handler handler;
    private boolean isShare;
    private int shareType;// 1 微信朋友圈 2 微信好友 3 qq
    private String code;
    private String specs;
    private String price;
    private String remark;
    private boolean isDestroy;

    private Tencent mTencent;

    private boolean isChooseAlbum;//是否是相册图片
    private AlbumFile album;//相册文件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mirro_layout);
        isDestroy = false;
        hideNavigatonButton();
        isChooseAlbum = getIntent().getBooleanExtra("isChooseAlbum",false);
        if(isChooseAlbum){
            album = getIntent().getParcelableExtra("album");
        }else{
            filePath = getIntent().getStringExtra("filePath");
        }
        type = getIntent().getIntExtra("type",0);
        init();
    }

    private void init() {
        regToWx();
        initQQshare();
        getAllViews();
        initHandler();
        setParams();
        setListeners();
    }

    private void getAllViews() {
        root_view = findViewById(R.id.root_view);
        cutView = findViewById(R.id.cutView);
        iv_share = findViewById(R.id.iv_share);
        tv_hint_title = findViewById(R.id.tv_hint_title);
        tv_hint_desc = findViewById(R.id.tv_hint_desc);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_rotate = findViewById(R.id.tv_rotate);
        tv_recapture = findViewById(R.id.tv_recapture);
        tv_save_album = findViewById(R.id.tv_save_album);
        tv_share = findViewById(R.id.tv_share);
        tv_preview = findViewById(R.id.tv_preview);
        v_bottom_back = findViewById(R.id.v_bottom_back);
        v_rotate = findViewById(R.id.v_rotate);
        iv_rotate = findViewById(R.id.iv_rotate);
    }
    private void initHandler(){
        handler = new Handler();
    }
    private void setParams() {
        FrameLayout.LayoutParams p_title = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        p_title.setMargins(Layout.getScale(20), Layout.getScale(20), 0, 0);
        tv_hint_title.setLayoutParams(p_title);
        Layout.setTextViewSize(tv_hint_title, 18);

        FrameLayout.LayoutParams p_desc = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        p_desc.setMargins(Layout.getScale(20), Layout.getScale(45), 0, 0);
        tv_hint_desc.setLayoutParams(p_desc);
        Layout.setTextViewSize(tv_hint_desc, 12);

        FrameLayout.LayoutParams p_cancel = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        p_cancel.gravity = Gravity.RIGHT;
        p_cancel.setMargins(0, Layout.getScale(10), 0, 0);
        tv_cancel.setLayoutParams(p_cancel);
        Layout.setTextViewPadding(tv_cancel, 20, 10, 20, 10);
        Layout.setTextViewSize(tv_cancel, 18);

        FrameLayout.LayoutParams p_bottom_back = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Layout.getScale(46));
        v_bottom_back.setLayoutParams(p_bottom_back);

        FrameLayout.LayoutParams p_v_rotate = new FrameLayout.LayoutParams(Layout.getScale(95), Layout.getScale(46));
        v_rotate.setLayoutParams(p_v_rotate);

        FrameLayout.LayoutParams p_iv_rotate = new FrameLayout.LayoutParams(Layout.getScale(20), Layout.getScale(20));
        p_iv_rotate.setMargins(Layout.getScale(20), Layout.getScale(13), 0, 0);
        iv_rotate.setLayoutParams(p_iv_rotate);

        FrameLayout.LayoutParams p_tv_rotate = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, Layout.getScale(46));
        p_tv_rotate.setMargins(Layout.getScale(50), 0, 0, 0);
        tv_rotate.setLayoutParams(p_tv_rotate);
        Layout.setTextViewSize(tv_rotate, 15);


        FrameLayout.LayoutParams p_share = new FrameLayout.LayoutParams(Layout.getScale(70), Layout.getScale(35));
        p_share.setMargins(0, Layout.getScale(6), Layout.getScale(12), 0);
        p_share.gravity = Gravity.RIGHT;
        tv_share.setLayoutParams(p_share);
        Layout.setTextViewSize(tv_share, 15);

        FrameLayout.LayoutParams p_tv_save_album = new FrameLayout.LayoutParams(Layout.getScale(128), Layout.getScale(46));
        p_tv_save_album.setMargins(0, 0, Layout.getScale(100), 0);
        p_tv_save_album.gravity = Gravity.RIGHT;
        tv_save_album.setLayoutParams(p_tv_save_album);
        Layout.setTextViewSize(tv_save_album, 15);

        FrameLayout.LayoutParams p_tv_recapture = new FrameLayout.LayoutParams(Layout.getScale(70), Layout.getScale(46));
        p_tv_recapture.setMargins(0, 0, Layout.getScale(228), 0);
        p_tv_recapture.gravity = Gravity.RIGHT;
        tv_recapture.setLayoutParams(p_tv_recapture);
        Layout.setTextViewSize(tv_recapture, 15);

        FrameLayout.LayoutParams p_tv_preview = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, Layout.getScale(46));
        p_tv_preview.gravity = Gravity.CENTER_HORIZONTAL;
        tv_preview.setLayoutParams(p_tv_preview);
        Layout.setTextViewSize(tv_preview, 15);
        Bitmap bitmap;
        if(isChooseAlbum){
            bitmap = OpenCVUtil.getBitmap(!isChooseAlbum,this,album.getPath(),album.getUriContentPath(), ConstantData.maxDecodeImageWidth);
        }else{
            bitmap = OpenCVUtil.getBitmap(!isChooseAlbum,this,filePath,null, ConstantData.maxDecodeImageWidth);
        }        if(type==0){
            tv_recapture.setVisibility(View.INVISIBLE);
        }else{
            cutView.setDenyCut(true);
            tv_hint_title.setVisibility(View.INVISIBLE);
            tv_hint_desc.setVisibility(View.INVISIBLE);
        }
        cutView.setBitmap(bitmap, 0, Layout.getScale(78), 0, Layout.getScale(66));
        cutView.bindView(tv_preview,null,null);
        iv_rotate.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_rotate, true));
    }

    private void setListeners() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()== R.id.tv_cancel){
                    finish();
                }
                if(v.getId()== R.id.v_rotate){
                    cutView.rotate();
                }
                if(v.getId()== R.id.tv_recapture){
                    Intent in = new Intent();
                    in.setClass(MirroPictureActivity.this,CaptureActivity.class);
                    in.putExtra("start_type",2);
                    startActivity(in);
                    finish();
                }
                if(v.getId()== R.id.tv_share){
                    String path = FileUtil.getCacheSaveImagesDir(MirroPictureActivity.this)+FileUtil.getNewFileNameByTime()+".jpg";
                    cutView.saveBitmap(path);
                    Intent in = new Intent();
                    in.setClass( MirroPictureActivity.this, ShareActivity.class);
                    in.putExtra("filePath",path);
                    startActivityForResult(in,SHARE_REQUEST_CODE);
                }
                if(v.getId()== R.id.tv_save_album){
                    String originalPath = FileUtil.getCacheSaveImagesDir(MirroPictureActivity.this)+System.currentTimeMillis()+".jpg";
                    cutView.saveBitmap(originalPath);
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                        String fileName = FileUtil.getNewFileNameByTime()+".jpg";
                        FileInfo fileInfo = FileInfo.createImageFileInfo(fileName);
                        Uri uri = FileUtil.createUri(MirroPictureActivity.this,"save", MimeType.jpg, Environment.DIRECTORY_PICTURES,fileInfo,true);
                        FileUtil.copyFileToUri(MirroPictureActivity.this,originalPath,uri,false);
                    }else{
                        String path = FileUtil.getAlbumSaveImagesDir(MirroPictureActivity.this,getResources().getString(R.string.app_name))+System.currentTimeMillis()+".jpg";
                        FileUtil.copyFileToFile(MirroPictureActivity.this,originalPath,path,false,false);
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(new File(path));
                        intent.setData(uri);
                        sendBroadcast(intent);
                    }
                    Toast.makeText(MirroPictureActivity.this, "保存成功",Toast.LENGTH_LONG ).show();
                }
                if(v.getId()== R.id.tv_preview){
                    cutView.preViewClick();
                }

            }
        };
        tv_cancel.setOnClickListener(listener);
        v_rotate.setOnClickListener(listener);
        tv_share.setOnClickListener(listener);
        tv_save_album.setOnClickListener(listener);
        tv_recapture.setOnClickListener(listener);
        tv_preview.setOnClickListener(listener);
    }
    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(Constants.APP_ID);
    }
    private void initQQshare(){
        mTencent = Tencent.createInstance(ConstantData.qqAppId, this.getApplicationContext());
    }
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
    }
    public void onActivityResult(int requestCode,int resultCode,final Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SHARE_REQUEST_CODE&&resultCode==RESULT_OK){
            isShare = true;
            String type = data.getStringExtra("type");
            Log.e("---------------->","share type 2 = "+type);
            code = data.getStringExtra("code");
            specs = data.getStringExtra("specs");
            price = data.getStringExtra("price");
            remark = data.getStringExtra("remark");
            if(type.equals("wx_friends")){
                shareType = 1;
            }
            if(type.equals("wx_friend")){
                shareType = 2;
            }
            if(type.equals("qq")){
                shareType = 3;
            }
        }
    }
    /**
     *
     * @param type 1 朋友圈，2 朋友
     */
    private void shareToWeixin(int type, Bitmap bmp, String code, String specs, String price, String remark){
        //初始化 WXImageObject 和 WXMediaMessage 对象
        WXImageObject imgObj = new WXImageObject(bmp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        //设置缩略图
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis()); ;
        req.message = msg;
        if(type==1){
            req.scene = WXSceneTimeline;
        }
        if(type==2){
            req.scene = WXSceneSession;
        }
        //调用api接口，发送数据到微信
        api.sendReq(req);
    }
    private Bitmap getShareBitmap(String filePath, String code, String specs, String price, String remark){
        Bitmap bitmap = ImageDealUtil.decodeFile(filePath, ConstantData.maxDecodeImageWidth);
        Bitmap bmp= WaterMarkUtil.waterMarkAddText(bitmap,code,specs,price,remark);
        bitmap.recycle();
        return bmp;
    }
    private void showImageView( final String filePath, final String code, final String specs, final String price, final String remark){
        final Bitmap picture = getShareBitmap(filePath, code, specs, price, remark);
        int pic_width;
        int pic_height;
        if((float) ScreenUtil.SCREEN_THIS_W/(ScreenUtil.SCREEN_THIS_H- Layout.getScale(78)- Layout.getScale(66))>(float)picture.getWidth()/picture.getHeight()){
            pic_height = 3*(ScreenUtil.SCREEN_THIS_H- Layout.getScale(78)- Layout.getScale(66))/4;
            pic_width =picture.getWidth()*pic_height/picture.getHeight();
        }else{
            pic_width = 3* ScreenUtil.SCREEN_THIS_W/4;
            pic_height = 3* ScreenUtil.SCREEN_THIS_W*picture.getHeight()/4*picture.getWidth();
        }
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(pic_width,pic_height );
        p.gravity = Gravity.CENTER;
        iv_share.setVisibility(View.VISIBLE);
        iv_share.setLayoutParams(p);
        iv_share.setImageBitmap(picture);
        final int toX = (ScreenUtil.SCREEN_THIS_W-pic_width)/2;
        final int toY = (ScreenUtil.SCREEN_THIS_H-pic_height)/2;
        final float scale = (float) Layout.getScale(100)/pic_height;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("------------>","post");
                Animation translateAnimation = new TranslateAnimation(0,-toX ,0 , ScreenUtil.SCREEN_THIS_H- Layout.getScale(100)-toY);
                translateAnimation.setDuration(1000);
                Animation scaleAnimation = new ScaleAnimation(1, scale, 1, scale,Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
                scaleAnimation.setDuration(1000);
                AnimationSet animationSet = new AnimationSet(false);
                animationSet.addAnimation(scaleAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setFillAfter(true);
                animationSet.setDuration(1000);
//                iv_share.setAnimation(animationSet);
                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Log.e("------------>","onAnimationEnd");
                        iv_share.setVisibility(View.INVISIBLE);
                        Log.e("---------------->","share type 4 = "+shareType);
                        if(shareType==1||shareType==2){
                            shareToWeixin(shareType,picture ,code ,specs ,price , remark);
                        }else if(shareType==3){
                            String path = FileUtil.getCacheSaveImagesDir(MirroPictureActivity.this)+FileUtil.getNewFileNameByTime()+".jpg";
                            Mat mat= new Mat();
                            Utils.bitmapToMat(picture,mat);
                            Imgproc.cvtColor(mat, mat, COLOR_RGBA2BGRA);
                            Imgcodecs.imwrite(path,mat);
                            shareLocalImageToQQ(path);
                            mat.release();
                        }
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                iv_share.startAnimation(animationSet);
                Log.e("------------>","animationSet.start()");
            }

        }, 1000);
    }
    public void onResume(){
        super.onResume();
        if(isShare){
            isShare = false;
            String path = FileUtil.getCacheSaveImagesDir(MirroPictureActivity.this)+FileUtil.getNewFileNameByTime()+".jpg";
            cutView.saveBitmap(path);
            Log.e("---------------->","share type 3 = "+shareType);
            showImageView(path, code, specs, price, remark);
        }
    }
    public void onDestroy(){
        super.onDestroy();
        isDestroy = true;

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
    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            Toast.makeText(MirroPictureActivity.this,"分享成功",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(MirroPictureActivity.this,"分享失败",Toast.LENGTH_SHORT).show();

        }
        @Override
        public void onCancel() {
            Toast.makeText(MirroPictureActivity.this,"分享取消",Toast.LENGTH_SHORT).show();

        }
    }
    //分享本地图片路径
    private void shareLocalImageToQQ(String localFilePath) {
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,localFilePath);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "石全石美");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare. SHARE_TO_QQ_TYPE_IMAGE);
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare. SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        mTencent.shareToQQ(MirroPictureActivity.this, params, new BaseUiListener());
    }
    //分享图文消息
    private void shareUrlToQQ(String title,String des,String url,String imgurl) {
        final Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,url);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, des);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,imgurl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "石全石美");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        mTencent.shareToQQ(MirroPictureActivity.this, params, new BaseUiListener());
    }
}
