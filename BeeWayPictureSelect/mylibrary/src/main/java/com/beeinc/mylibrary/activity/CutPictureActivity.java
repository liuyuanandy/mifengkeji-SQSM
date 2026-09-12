package com.beeinc.mylibrary.activity;

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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beeinc.mylibrary.R;
import com.beeinc.mylibrary.scale.Layout;
import com.beeinc.mylibrary.util.ConstantData;
import com.beeinc.mylibrary.util.FileInfo;
import com.beeinc.mylibrary.util.FileUtil;
import com.beeinc.mylibrary.util.ImageDealUtil;
import com.beeinc.mylibrary.util.MimeType;
import com.beeinc.mylibrary.util.OpenCVUtil;
import com.beeinc.mylibrary.views.ImageCutView;
import com.unity3d.player.UnityPlayer;
import com.yanzhenjie.album.AlbumFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class CutPictureActivity extends Activity {

    private ImageCutView cutView;
    private TextView tv_hint_title,tv_hint_desc,tv_cancel,tv_with,tv_width_unit,tv_height,tv_height_unit,tv_rotate,tv_apply,tv_save_album,tv_preview;
    private EditText et_width,et_height;
    private View v_bottom_back,v_rotate,v_save_album;
    private ImageView iv_rotate,iv_save_album;
    private FrameLayout frame,frame_width,frame_height;
    private String filePath;//文件路径
    private String fileDir;//文件保存路径
    private boolean isSaveToAlbum = true;

    private boolean isDestroy = false;
    private boolean isChooseAlbum;//是否是相册图片
    private AlbumFile album;//相册文件
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cut);
        hideNavigatonButton();
        isDestroy = false;
        isChooseAlbum = getIntent().getBooleanExtra("isChooseAlbum",false);
        if(isChooseAlbum){
            album = getIntent().getParcelableExtra("album");
        }else{
            filePath = getIntent().getStringExtra("filePath");
        }
        fileDir = getIntent().getStringExtra("fileDir");
        Log.e("----------->","fileDir:"+fileDir);
        init();
    }
    private void init(){
        getAllViews();
        setParams();
        setListeners();
    }
    private void getAllViews(){
        cutView =findViewById(R.id.cutView);
        tv_hint_title =findViewById(R.id.tv_hint_title);
        tv_hint_desc =findViewById(R.id.tv_hint_desc);
        tv_cancel =findViewById(R.id.tv_cancel);
        tv_with =findViewById(R.id.tv_with);
        tv_width_unit = findViewById(R.id.tv_width_unit);
        tv_height =findViewById(R.id.tv_height);
        tv_height_unit = findViewById(R.id.tv_height_unit);
        tv_rotate =findViewById(R.id.tv_rotate);
        tv_apply =findViewById(R.id.tv_apply);
        tv_save_album =findViewById(R.id.tv_save_album);
        tv_preview = findViewById(R.id.tv_preview);
        et_width = findViewById(R.id.et_with);
        et_height = findViewById(R.id.et_height);
        v_bottom_back =findViewById(R.id.v_bottom_back);
        v_rotate =findViewById(R.id.v_rotate);
        v_save_album =findViewById(R.id.v_save_album);

        iv_rotate =findViewById(R.id.iv_rotate);
        iv_save_album =findViewById(R.id.iv_save_album);

        frame_width = findViewById(R.id.frame_width);
        frame_height = findViewById(R.id.frame_height);
    }
    private void setParams(){
        FrameLayout.LayoutParams p_title= new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        p_title.setMargins(Layout.getScale(20), Layout.getScale(20), 0, 0);
        tv_hint_title.setLayoutParams(p_title);
        Layout.setTextViewSize(tv_hint_title, 18);

        FrameLayout.LayoutParams p_desc= new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        p_desc.setMargins(Layout.getScale(20), Layout.getScale(45), 0, 0);
        tv_hint_desc.setLayoutParams(p_desc);
        Layout.setTextViewSize(tv_hint_desc, 12);

        FrameLayout.LayoutParams p_cancel= new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        p_cancel.gravity = Gravity.RIGHT;
        p_cancel.setMargins(0, Layout.getScale(10), 0, 0);
        tv_cancel.setLayoutParams(p_cancel);
        Layout.setTextViewPadding(tv_cancel, 20, 10, 20, 10);
        Layout.setTextViewSize(tv_cancel,18 );


        FrameLayout.LayoutParams p_frame_width= new FrameLayout.LayoutParams(Layout.getScale(338), Layout.getScale(34));
        p_frame_width.gravity = Gravity.CENTER_HORIZONTAL;
        frame_width.setLayoutParams(p_frame_width);


        FrameLayout.LayoutParams p_frame_height= new FrameLayout.LayoutParams(Layout.getScale(338), Layout.getScale(34));
        p_frame_height.setMargins(0, Layout.getScale(44),0 , 0 );
        p_frame_height.gravity = Gravity.CENTER_HORIZONTAL;
        frame_height.setLayoutParams(p_frame_height);

        FrameLayout.LayoutParams p_tv_width= new FrameLayout.LayoutParams(Layout.getScale(80), Layout.getScale(34));
        p_tv_width.setMargins(Layout.getScale(14),0,0 , 0 );
        tv_with.setLayoutParams(p_tv_width);
        Layout.setTextViewSize(tv_with,15 );
        FrameLayout.LayoutParams p_tv_height= new FrameLayout.LayoutParams(Layout.getScale(80), Layout.getScale(34));
        p_tv_height.setMargins(Layout.getScale(14),0,0 , 0 );
        tv_height.setLayoutParams(p_tv_height);
        Layout.setTextViewSize(tv_height,15 );

        FrameLayout.LayoutParams p_tv_width_unit= new FrameLayout.LayoutParams(Layout.getScale(50), Layout.getScale(34));
        p_tv_width_unit.setMargins(Layout.getScale(270),0,0 , 0 );
        tv_width_unit.setLayoutParams(p_tv_width_unit);
        Layout.setTextViewSize(tv_width_unit,15 );

        FrameLayout.LayoutParams p_tv_height_unit= new FrameLayout.LayoutParams(Layout.getScale(50), Layout.getScale(34));
        p_tv_height_unit.setMargins(Layout.getScale(270),0,0 , 0 );
        tv_height_unit.setLayoutParams(p_tv_height_unit);
        Layout.setTextViewSize(tv_height_unit,15 );

        FrameLayout.LayoutParams p_et_width= new FrameLayout.LayoutParams(Layout.getScale(200), Layout.getScale(34));
        p_et_width.setMargins(Layout.getScale(96),0,0 , 0 );
        et_width.setLayoutParams(p_et_width);
        Layout.setTextViewSize(et_width, 15);
        FrameLayout.LayoutParams p_et_height= new FrameLayout.LayoutParams(Layout.getScale(200), Layout.getScale(34));
        p_et_height.setMargins(Layout.getScale(96),0,0 , 0 );
        et_height.setLayoutParams(p_et_height);
        Layout.setTextViewSize(et_height, 15);

        FrameLayout.LayoutParams p_bottom_back= new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Layout.getScale(46));
        p_bottom_back.setMargins(0, Layout.getScale(100) , 0, 0);
        v_bottom_back.setLayoutParams(p_bottom_back);

        FrameLayout.LayoutParams p_v_rotate= new FrameLayout.LayoutParams(Layout.getScale(95), Layout.getScale(46));
        p_v_rotate.setMargins(0, Layout.getScale(100),0 , 0 );
        v_rotate.setLayoutParams(p_v_rotate);

        FrameLayout.LayoutParams p_iv_rotate= new FrameLayout.LayoutParams(Layout.getScale(20), Layout.getScale(20));
        p_iv_rotate.setMargins(Layout.getScale(20), Layout.getScale(113),0 , 0 );
        iv_rotate.setLayoutParams(p_iv_rotate);

        FrameLayout.LayoutParams p_tv_rotate= new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, Layout.getScale(46));
        p_tv_rotate.setMargins(Layout.getScale(50), Layout.getScale(100),0 , 0 );
        tv_rotate.setLayoutParams(p_tv_rotate);
        Layout.setTextViewSize(tv_rotate, 15);


        FrameLayout.LayoutParams p_apply= new FrameLayout.LayoutParams(Layout.getScale(70), Layout.getScale(35));
        p_apply.setMargins(0, Layout.getScale(106), Layout.getScale(12) , 0 );
        p_apply.gravity = Gravity.RIGHT;
        tv_apply.setLayoutParams(p_apply);
        Layout.setTextViewSize(tv_apply, 15);

        FrameLayout.LayoutParams p_v_save_album= new FrameLayout.LayoutParams(Layout.getScale(155), Layout.getScale(46));
        p_v_save_album.setMargins(0, Layout.getScale(100), Layout.getScale(92) , 0 );
        p_v_save_album.gravity = Gravity.RIGHT;
        v_save_album.setLayoutParams(p_v_save_album);

        FrameLayout.LayoutParams p_tv_save_album= new FrameLayout.LayoutParams(Layout.getScale(128), Layout.getScale(46));
        p_tv_save_album.setMargins(0, Layout.getScale(100), Layout.getScale(80) , 0 );
        p_tv_save_album.gravity = Gravity.RIGHT;
        tv_save_album.setLayoutParams(p_tv_save_album);
        Layout.setTextViewSize(tv_save_album, 15);

        FrameLayout.LayoutParams p_iv_save_album= new FrameLayout.LayoutParams(Layout.getScale(20), Layout.getScale(20));
        p_iv_save_album.setMargins(0, Layout.getScale(113), Layout.getScale(220) , 0 );
        p_iv_save_album.gravity = Gravity.RIGHT;
        iv_save_album.setLayoutParams(p_iv_save_album);

        FrameLayout.LayoutParams p_tv_preview= new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, Layout.getScale(46));
        p_tv_preview.setMargins(0, Layout.getScale(100),0 , 0 );
        p_tv_preview.gravity = Gravity.CENTER_HORIZONTAL;
        tv_preview.setLayoutParams(p_tv_preview);
        Layout.setTextViewSize(tv_preview, 15);
        Bitmap bitmap;
        if(isChooseAlbum){
            bitmap = OpenCVUtil.getBitmap(!isChooseAlbum,this,album.getPath(),album.getUriContentPath(), ConstantData.maxDecodeImageWidth);
        }else{
            bitmap = OpenCVUtil.getBitmap(!isChooseAlbum,this,filePath,null, ConstantData.maxDecodeImageWidth);
        }
        cutView.bindView(tv_preview,et_width,et_height);
        cutView.setBitmap(bitmap, 0, Layout.getScale(78), 0, Layout.getScale(166));
        iv_rotate.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_rotate, true));
        iv_save_album.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_album_selected, true));
    }
    private void setListeners(){
        View.OnClickListener listener  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()== R.id.tv_cancel){
                    finish();
                }
                if(v.getId()== R.id.v_rotate){
                    cutView.rotate();
                }
                if(v.getId()== R.id.tv_apply){
                    String fileName = System.currentTimeMillis()+".jpg";
                    String path = fileDir+fileName;
                    Log.e("切割","切割后保存路径："+path);
                    cutView.saveBitmap(path);
                    if(isSaveToAlbum){
                        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                            FileInfo fileInfo = FileInfo.createImageFileInfo(fileName);
                            Uri uri = FileUtil.createUri(CutPictureActivity.this,"save", MimeType.jpg, Environment.DIRECTORY_PICTURES,fileInfo,true);
                            FileUtil.copyFileToUri(CutPictureActivity.this,path,uri,false);
                        }else{
                            String toPath = FileUtil.getAlbumSaveImagesDir(CutPictureActivity.this,getResources().getString(R.string.app_name))+System.currentTimeMillis()+".jpg";
                            FileUtil.copyFileToFile(CutPictureActivity.this,path,toPath,false,false);
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri = Uri.fromFile(new File(toPath));
                            intent.setData(uri);
                            sendBroadcast(intent);
                        }
                        Toast.makeText(CutPictureActivity.this, "保存成功",Toast.LENGTH_LONG ).show();
                    }
                    try {
                        JSONArray array = new JSONArray();
                        JSONObject json = new JSONObject();
                        json.put("width", et_width.getText().toString().trim());
                        json.put("height",et_height.getText().toString().trim());
                        json.put("picture",path);
                        array.put(json);
                        UnityPlayer.UnitySendMessage("IOsReciveObj","GetAlbumPathFromCutFinish",array.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
                if(v.getId()== R.id.v_save_album){
                    if(isSaveToAlbum){
                        isSaveToAlbum = false;
                        iv_save_album.setImageBitmap(ImageDealUtil.readBitMap(CutPictureActivity.this, R.mipmap.icon_album_unselected , false));
                    }else{
                        isSaveToAlbum  =true;
                        iv_save_album.setImageBitmap(ImageDealUtil.readBitMap(CutPictureActivity.this, R.mipmap.icon_album_selected , false));
                    }
                }
                if(v.getId()== R.id.tv_preview){
                    cutView.preViewClick();
                }
            }
        };
        tv_cancel.setOnClickListener(listener);
        v_rotate.setOnClickListener(listener);
        tv_apply.setOnClickListener(listener);
        v_save_album.setOnClickListener(listener);
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
