package com.beeinc.SQSB.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.beeinc.SQSB.R;
import com.beeinc.SQSB.scale.Layout;
import com.beeinc.SQSB.util.ConstantData;
import com.beeinc.SQSB.util.FileUtil;
import com.beeinc.SQSB.util.ImageDealUtil;
import com.beeinc.SQSB.util.OpenCVUtil;
import com.beeinc.SQSB.views.ImageCutView;
import com.unity3d.player.UnityPlayer;
import com.yanzhenjie.album.AlbumFile;

public class DuiHuaActivity extends Activity {

    private FrameLayout root_view;
    private ImageCutView cutView;
    private ImageView iv_share;
    private TextView tv_hint_title, tv_hint_desc, tv_cancel, tv_rotate,tv_apply,tv_preview;
    private View v_bottom_back, v_rotate;
    private ImageView iv_rotate;
    private FrameLayout frame;
    private String filePath;
    private Handler handler;
    private boolean isDestroy;
    private boolean isChooseAlbum;//是否是相册图片
    private AlbumFile album;//相册文件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.duihua);
        isDestroy = false;
        hideNavigatonButton();
        isChooseAlbum = getIntent().getBooleanExtra("isChooseAlbum",false);
        if(isChooseAlbum){
            album = getIntent().getParcelableExtra("album");
        }else{
            filePath = getIntent().getStringExtra("filePath");
        }
        init();
    }

    private void init() {
        getAllViews();
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
        tv_apply = findViewById(R.id.tv_apply);
        tv_preview = findViewById(R.id.tv_preview);
        v_bottom_back = findViewById(R.id.v_bottom_back);
        v_rotate = findViewById(R.id.v_rotate);
        iv_rotate = findViewById(R.id.iv_rotate);
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
        tv_apply.setLayoutParams(p_share);
        Layout.setTextViewSize(tv_apply, 15);

        FrameLayout.LayoutParams p_tv_preview = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, Layout.getScale(46));
        p_tv_preview.gravity = Gravity.CENTER_HORIZONTAL;
        tv_preview.setLayoutParams(p_tv_preview);
        Layout.setTextViewSize(tv_preview, 15);
        Bitmap bitmap;
        if(isChooseAlbum){
            bitmap = OpenCVUtil.getBitmap(!isChooseAlbum,this,album.getPath(),album.getUriContentPath(), ConstantData.maxDecodeImageWidth);
        }else{
            bitmap = OpenCVUtil.getBitmap(!isChooseAlbum,this,filePath,null, ConstantData.maxDecodeImageWidth);
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
                if(v.getId()== R.id.tv_apply){
                    String path = FileUtil.getCacheSaveImagesDir(DuiHuaActivity.this)+FileUtil.getNewFileNameByTime()+".jpg";
                    cutView.saveBitmap(path);
                    UnityPlayer.UnitySendMessage("IOsReciveObj","GetAlbumPathFinish",path);
                    finish();
                }
                if(v.getId()== R.id.tv_preview){
                    cutView.preViewClick();
                }

            }
        };
        tv_cancel.setOnClickListener(listener);
        v_rotate.setOnClickListener(listener);
        tv_apply.setOnClickListener(listener);
        tv_preview.setOnClickListener(listener);
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
}
