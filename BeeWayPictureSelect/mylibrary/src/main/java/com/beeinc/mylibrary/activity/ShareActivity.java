package com.beeinc.mylibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beeinc.mylibrary.R;
import com.beeinc.mylibrary.scale.FrameScaleUtil;
import com.beeinc.SQSB.wxapi.Constants;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;

public class ShareActivity extends Activity {

    private View root_view,v_back,v_line_title_left,v_line_title_right,v_content,v_line_share_left,v_line_share_right,v_wx_friend,v_wx_friends;
    private TextView tv_title,tv_code_hint,tv_guige,tv_price_hint,tv_remark_hint,tv_share,tv_wx_friend,tv_wx_friends,tv_cancel;
    private EditText et_code,et_guige,et_price,et_ramark;
    private ImageView iv_wx_friend,iv_wx_friends;
    private ArrayList<View> scaleViews = new ArrayList<View>();
    private ArrayList<View> scaleTextViews = new ArrayList<View>();
    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;
    private String filePath;

    private boolean isDestroy = false;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);
        isDestroy = false;
        hideNavigatonButton();
        filePath = getIntent().getStringExtra("filePath");
        init();
    }
    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(Constants.APP_ID);
    }
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
    }
    private void init(){
        regToWx();
        getAllViews();
        setParams();
        setScaleViews();
        setListeners();
    }
    private void getAllViews(){
        root_view = findViewById(R.id.root_view);
        v_back = findViewById(R.id.v_back);
        v_line_title_left = findViewById(R.id.v_line_title_left);
        v_line_title_right = findViewById(R.id.v_line_title_right);
        v_content = findViewById(R.id.v_content);
        v_line_share_left = findViewById(R.id.v_line_share_left);
        v_line_share_right = findViewById(R.id.v_line_share_right);
        v_wx_friend = findViewById(R.id.v_wx_friend);
        v_wx_friends = findViewById(R.id.v_wx_friends);
        tv_title = findViewById(R.id.tv_title);
        tv_code_hint = findViewById(R.id.tv_code_hint);
        tv_guige = findViewById(R.id.tv_guige);
        tv_price_hint = findViewById(R.id.tv_price_hint);
        tv_remark_hint = findViewById(R.id.tv_remark_hint);
        tv_share = findViewById(R.id.tv_share);
        tv_wx_friend = findViewById(R.id.tv_wx_friend);
        tv_wx_friends = findViewById(R.id.tv_wx_friends);
        tv_cancel = findViewById(R.id.tv_cancel);
        et_code = findViewById(R.id.et_code);
        et_guige = findViewById(R.id.et_guige);
        et_price = findViewById(R.id.et_price);
        et_ramark = findViewById(R.id.et_ramark);
        iv_wx_friend = findViewById(R.id.iv_wx_friend);
        iv_wx_friends = findViewById(R.id.iv_wx_friends);
    }
    private void setParams(){

    }
    private void setScaleViews(){
        scaleViews.add(v_back);
        scaleViews.add(v_line_title_left);
        scaleViews.add(v_line_title_right);
        scaleViews.add(v_content);
        scaleViews.add(v_line_share_left);
        scaleViews.add(v_line_share_right);
        scaleViews.add(v_wx_friend);
        scaleViews.add(v_wx_friends);

        scaleViews.add(iv_wx_friend);
        scaleViews.add(iv_wx_friends);
        scaleTextViews.add(tv_title);
        scaleTextViews.add(tv_code_hint);
        scaleTextViews.add(tv_guige);
        scaleTextViews.add(tv_price_hint);
        scaleTextViews.add(tv_remark_hint);
        scaleTextViews.add(tv_share);
        scaleTextViews.add(tv_wx_friend);
        scaleTextViews.add(tv_wx_friends);
        scaleTextViews.add(tv_cancel);
        scaleTextViews.add(et_code);
        scaleTextViews.add(et_guige);
        scaleTextViews.add(et_price);
        scaleTextViews.add(et_ramark);
        FrameScaleUtil.scale(scaleViews, FrameScaleUtil.X_LEFT, FrameScaleUtil.Y_TOP, FrameScaleUtil.TYPE_BASIC);
        FrameScaleUtil.scale(scaleTextViews, FrameScaleUtil.X_LEFT, FrameScaleUtil.Y_TOP, FrameScaleUtil.TYPE_TEXT_VIEW);
    }
    private void setListeners(){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()== R.id.v_wx_friend){
                    Log.e("-------->","api.getWXAppSupportAPI()："+api.getWXAppSupportAPI());
                    if (api.getWXAppSupportAPI() >= Build.TIMELINE_SUPPORTED_SDK_INT) {//是否支持发送到朋友圈
                        share(1);
                    }else{//
                        Toast.makeText(ShareActivity.this,"当前设备不支持分享",Toast.LENGTH_SHORT ).show();
                        return;
                    }
                }
                if(v.getId()== R.id.v_wx_friends){
                    if (api.getWXAppSupportAPI() >= Build.TIMELINE_SUPPORTED_SDK_INT) {//是否支持发送到朋友圈
                        share(2);
                    }else{//
                        Toast.makeText(ShareActivity.this,"当前设备不支持分享",Toast.LENGTH_SHORT ).show();
                        return;
                    }
                }
                if(v.getId()== R.id.tv_cancel){
                    finish();
                }
                if(v.getId()== R.id.v_back){//不处理

                }
                if(v.getId()== R.id.root_view){
                    finish();
                }
            }
        };
        v_wx_friend.setOnClickListener(listener);
        v_wx_friends.setOnClickListener(listener);
        tv_cancel.setOnClickListener(listener);
        v_back.setOnClickListener(listener);
        root_view.setOnClickListener(listener);
    }

    /**
     *
     * @param type 1 微信朋友，2朋友圈
     */
    private void share(int type){
        String code = et_code.getText().toString().trim();
        String specs = et_guige.getText().toString().trim();
        String price  = et_price.getText().toString().trim();
        String remark = et_ramark.getText().toString().trim();
        Intent intent = new Intent();
        if(type==1){
            intent.putExtra("type", "wx_friend");
        }else if(type==2){
            intent.putExtra("type", "wx_friends");
        }
        Log.e("---------------->","share type 1 = "+type);
        intent.putExtra("code", code);
        intent.putExtra("specs", specs);
        intent.putExtra("price", price);
        intent.putExtra("remark", remark);
        setResult(RESULT_OK,intent);
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void onDestroy(){
        super.onDestroy();
        isDestroy = true;
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
