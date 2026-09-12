package com.beeinc.SQSB.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.beeinc.mylibrary.R;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

//import com.beeinc.SQSBMM.wxapi.Constants;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;
    private String type;//ARShare 分享AR图片
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = this.getIntent().getStringExtra("type");
        regToWx();
    }
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(getIntent(), this);
    }
    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }
    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        String result="";
        Log.e("------------>","resp.errCode = "+resp.errCode);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                getResources().getString(R.string.errcode_success);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                getResources().getString(R.string.errcode_cancel);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                getResources().getString(R.string.errcode_deny);
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                getResources().getString(R.string.errcode_unsupported);
                break;
            default:
                getResources().getString(R.string.errcode_unknown);
                break;
        }
        finish();
//        Toast.makeText(this,result, Toast.LENGTH_SHORT).show();
    }
}
