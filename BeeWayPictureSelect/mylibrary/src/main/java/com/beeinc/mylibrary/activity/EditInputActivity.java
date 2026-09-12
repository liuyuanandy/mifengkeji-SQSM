package com.beeinc.mylibrary.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beeinc.mylibrary.R;
import com.beeinc.mylibrary.util.SoftwareUtil;
import com.beeinc.mylibrary.util.StatusBarUtils;
import com.unity3d.player.UnityPlayer;

public class EditInputActivity extends FragmentActivity {
    private Handler handler;
    private boolean isDestroy = false;
    private TextView tv_num;
    private TextView tv_cancel;
    private TextView tv_finish;
    private EditText et_content;

    private int type;//调用入口类型
    private String content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
//        hideNavigatonButton();
        StatusBarUtils.with(this).init();
        type = this.getIntent().getIntExtra("type",0);
        content = this.getIntent().getStringExtra("content");
        init();
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
    }

    private void init(){
        getAllViews();
        setListener();
        setParams();
    }
    private void getAllViews(){
        tv_num = findViewById(R.id.tv_num);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_finish = findViewById(R.id.tv_finish);
        et_content = findViewById(R.id.et_content);
    }
    private void setParams(){
        LinearLayout.LayoutParams  p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, StatusBarUtils.getStatusBarHeight(this));
        findViewById(R.id.status_bar_height_view).setLayoutParams(p);
        if(content!=null){
            et_content.setText(content);
            et_content.setSelection(content.length());
        }
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                et_content.requestFocus();
                SoftwareUtil.showSoftware(et_content, EditInputActivity.this);
            }
        },1000);
    }
    private void setListener(){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()== R.id.tv_cancel){
                    finish();
                }
                if(v.getId()== R.id.tv_finish){
                    String text = et_content.getText().toString().trim();
                    if(text.equals("")){
                        Toast.makeText(EditInputActivity.this,"请输入文本内容",Toast.LENGTH_LONG).show();
                    }else{
                        UnityPlayer.UnitySendMessage("IOsReciveObj","EditInputFinish",text);
                        finish();
                    }
                }
            }
        };
        tv_cancel.setOnClickListener(listener);
        tv_finish.setOnClickListener(listener);
        TextWatcher watcher  =new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_num.setText(s.toString().length()+"字");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        et_content.addTextChangedListener(watcher);
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
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }
}
