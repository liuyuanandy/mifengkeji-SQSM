package com.beeinc.mylibrary.activity;

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

import com.beeinc.mylibrary.R;
import com.beeinc.mylibrary.scale.Layout;
import com.beeinc.mylibrary.scale.ScreenUtil;
import com.beeinc.mylibrary.util.ConstantData;
import com.beeinc.mylibrary.util.FileInfo;
import com.beeinc.mylibrary.util.FileUtil;
import com.beeinc.mylibrary.util.ImageDealUtil;
import com.beeinc.mylibrary.util.MimeType;
import com.beeinc.SQSB.wxapi.Constants;
import com.beeinc.SQSB.wxapi.Util;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;


public class ARPictureActivity extends Activity {

    private ImageView iv_picture, iv_close, iv_share;
    private ImageView iv_show;
    private TextView tv_recapture, tv_save_album;
    private String filePath;
    private String fileKtPath, fileFsPath;
    private final int SHARE_REQUEST_CODE = 10001;


    private IWXAPI api;
    private boolean isShare;
    private int shareType;// 1 微信朋友圈 2 微信好友 3 qq

    private Handler handler;
    private boolean isDestroy;
    private Bitmap share_picture_bitmap;//分享图片的bitmap
    private String share_filePath;//分享图片的文件夹路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_picture);
        isDestroy = false;
        hideNavigatonButton();
        filePath = this.getIntent().getStringExtra("filePath");
        fileFsPath = this.getIntent().getStringExtra("fileFsPath");
        fileKtPath = this.getIntent().getStringExtra("fileKtPath");

        init();
    }

    private void init() {
        getAllViews();
        setParams();
        setListener();
        regToWx();
    }

    private void getAllViews() {
        iv_picture = findViewById(R.id.iv_picture);
        iv_close = findViewById(R.id.iv_close);
        iv_share = findViewById(R.id.iv_share);
        tv_recapture = findViewById(R.id.tv_recapture);
        tv_save_album = findViewById(R.id.tv_save_album);
        iv_show = findViewById(R.id.iv_show);
    }

    private void setParams() {
        FrameLayout.LayoutParams p_close = new FrameLayout.LayoutParams(Layout.getScale(36), Layout.getScale(36));
        p_close.setMargins(Layout.getScale(40), Layout.getScale(40), 0, 0);
        iv_close.setLayoutParams(p_close);

        FrameLayout.LayoutParams p_share = new FrameLayout.LayoutParams(Layout.getScale(36), Layout.getScale(36));
        p_share.setMargins(0, Layout.getScale(40), Layout.getScale(40), 0);
        p_share.gravity = Gravity.RIGHT;
        iv_share.setLayoutParams(p_share);

        FrameLayout.LayoutParams p_recapture = new FrameLayout.LayoutParams(Layout.getScale(80), Layout.getScale(40));
        p_recapture.setMargins(0, 0, Layout.getScale(140), Layout.getScale(18));
        p_recapture.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        tv_recapture.setLayoutParams(p_recapture);
        Layout.setTextViewSize(tv_recapture, 18);

        FrameLayout.LayoutParams p_save_album = new FrameLayout.LayoutParams(Layout.getScale(115), Layout.getScale(40));
        p_save_album.setMargins(0, 0, Layout.getScale(14), Layout.getScale(18));
        p_save_album.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        tv_save_album.setLayoutParams(p_save_album);
        Layout.setTextViewSize(tv_save_album, 18);
        Bitmap bitmap = ImageDealUtil.decodeFile(filePath, ConstantData.maxDecodeImageWidth);

        FrameLayout.LayoutParams p_picture = new FrameLayout.LayoutParams(bitmap.getWidth() * ScreenUtil.SCREEN_THIS_H / bitmap.getHeight(), ScreenUtil.SCREEN_THIS_H);
        p_picture.gravity = Gravity.CENTER_HORIZONTAL;
        iv_picture.setLayoutParams(p_picture);
        iv_picture.setImageBitmap(bitmap);
        iv_close.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_left_close, false));
        iv_share.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_share, false));
    }

    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(Constants.APP_ID);
    }

    public void setListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.iv_close) {
                    finish();
                }
                if (view.getId() == R.id.iv_share) {
                    Intent intent = new Intent();
                    intent.setClass(ARPictureActivity.this, ShareARActivity.class);
                    startActivityForResult(intent, SHARE_REQUEST_CODE);
                }
                if (view.getId() == R.id.tv_recapture) {
                    Intent in = new Intent();
                    in.setClass(ARPictureActivity.this, ARActivity.class);
                    in.putExtra("fileFsPath", fileFsPath);
                    in.putExtra("fileKtPath", fileKtPath);
                    startActivity(in);
                    finish();
                }
                if (view.getId() == R.id.tv_save_album) {
                    getShareBitmap();
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                        String fileName = FileUtil.getNewFileNameByTime() + ".jpg";
                        FileInfo fileInfo = FileInfo.createImageFileInfo(fileName);
                        Uri uri = FileUtil.createUri(ARPictureActivity.this, "save", MimeType.jpg, Environment.DIRECTORY_PICTURES, fileInfo, true);
                        FileUtil.copyFileToUri(ARPictureActivity.this, share_filePath, uri, false);
                    } else {
                        String path = FileUtil.getAlbumSaveImagesDir(ARPictureActivity.this, getResources().getString(R.string.app_name)) + System.currentTimeMillis() + ".jpg";
                        FileUtil.copyFileToFile(ARPictureActivity.this, share_filePath, path, false, false);
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(new File(path));
                        intent.setData(uri);
                        sendBroadcast(intent);
                    }
                    Toast.makeText(ARPictureActivity.this, getResources().getString(R.string.saved), Toast.LENGTH_LONG).show();
                }
            }
        };
        iv_close.setOnClickListener(listener);
        iv_share.setOnClickListener(listener);
        tv_recapture.setOnClickListener(listener);
        tv_save_album.setOnClickListener(listener);
    }

    public void onActivityResult(int re, int result, Intent data) {
        super.onActivityResult(re, result, data);
        if (result == RESULT_OK && re == SHARE_REQUEST_CODE) {
            isShare = true;
            String type = data.getStringExtra("type");
            if (type.equals("wx_friends")) {
                shareType = 1;
            }
            if (type.equals("wx_friend")) {
                shareType = 2;
            }
            if (type.equals("qq")) {
                shareType = 3;
            }
        }
    }

    /**
     *
     * @param type 1 朋友圈，2 朋友
     */
    private void shareToWeixin(int type, Bitmap bmp) {
        //初始化 WXImageObject 和 WXMediaMessage 对象
        WXImageObject imgObj = new WXImageObject(bmp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        //设置缩略图
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        ;
        req.message = msg;
        if (type == 1) {
            req.scene = WXSceneTimeline;
        }
        if (type == 2) {
            req.scene = WXSceneSession;
        }
        //调用api接口，发送数据到微信
        api.sendReq(req);
    }


    private void getShareBitmap() {
        if (share_picture_bitmap == null) {
            Bitmap bitmap = ImageDealUtil.decodeFile(filePath, ConstantData.maxDecodeImageWidth);
//            Bitmap waterMark = ImageDealUtil.readBitMap(this, R.mipmap.icon_mark,true);
//            share_picture_bitmap= WaterMarkUtil.waterMarkAddImage(bitmap,waterMark);
            share_picture_bitmap = bitmap;
            share_filePath = FileUtil.getCacheSaveImagesDir(ARPictureActivity.this) + FileUtil.getNewFileNameByTime() + ".jpg";

            Mat mat = new Mat();
            Utils.bitmapToMat(share_picture_bitmap, mat);
            Imgproc.cvtColor(mat, mat, COLOR_RGBA2BGRA);
            Imgcodecs.imwrite(share_filePath, mat);
            mat.release();
        }
    }

    private void showImageView(final int type) {
        getShareBitmap();
        int pic_width;
        int pic_height;
        if ((float) ScreenUtil.SCREEN_THIS_W / (ScreenUtil.SCREEN_THIS_H - Layout.getScale(78) - Layout.getScale(66)) > (float) share_picture_bitmap.getWidth() / share_picture_bitmap.getHeight()) {
            pic_height = 3 * (ScreenUtil.SCREEN_THIS_H - Layout.getScale(78) - Layout.getScale(66)) / 4;
            pic_width = share_picture_bitmap.getWidth() * pic_height / share_picture_bitmap.getHeight();
        } else {
            pic_width = 3 * ScreenUtil.SCREEN_THIS_W / 4;
            pic_height = 3 * ScreenUtil.SCREEN_THIS_W * share_picture_bitmap.getHeight() / 4 * share_picture_bitmap.getWidth();
        }
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(pic_width, pic_height);
        p.gravity = Gravity.CENTER;
        iv_show.setVisibility(View.VISIBLE);
        iv_show.setLayoutParams(p);
        iv_show.setImageBitmap(share_picture_bitmap);
        final int toX = (ScreenUtil.SCREEN_THIS_W - pic_width) / 2;
        final int toY = (ScreenUtil.SCREEN_THIS_H - pic_height) / 2;
        final float scale = (float) Layout.getScale(100) / pic_height;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("------------>", "post");
                Animation translateAnimation = new TranslateAnimation(0, -toX, 0, ScreenUtil.SCREEN_THIS_H - Layout.getScale(100) - toY);
                translateAnimation.setDuration(1000);
                Animation scaleAnimation = new ScaleAnimation(1, scale, 1, scale, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
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
                        Log.e("------------>", "onAnimationEnd");
                        iv_show.setVisibility(View.INVISIBLE);
                        if (type == 1 || type == 2) {
                            shareToWeixin(type, share_picture_bitmap);
                        } else if (type == 3) {

                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                iv_show.startAnimation(animationSet);
                Log.e("------------>", "animationSet.start()");
            }

        }, 1000);
    }

    public void onResume() {
        super.onResume();
        if (isShare) {
            isShare = false;
            showImageView(shareType);
        }
    }

    public void onDestroy() {
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
}
