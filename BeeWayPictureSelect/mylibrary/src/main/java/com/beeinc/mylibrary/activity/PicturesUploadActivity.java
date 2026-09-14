package com.beeinc.mylibrary.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beeinc.mylibrary.R;
import com.beeinc.mylibrary.bean.CategoryType;
import com.beeinc.mylibrary.bean.FixBean;
import com.beeinc.mylibrary.bean.UploadFile;
import com.beeinc.mylibrary.dialog.InputFixPictureInfoDialog;
import com.beeinc.mylibrary.dialog.QuitFixDialog;
import com.beeinc.mylibrary.receiver.BeeIncReceiver;
import com.beeinc.mylibrary.rxurl.RxRequest;
import com.beeinc.mylibrary.scale.FrameScaleUtil;
import com.beeinc.mylibrary.scale.Layout;
import com.beeinc.mylibrary.scale.ScreenUtil;
import com.beeinc.mylibrary.util.BroadcastReceiverRegisterUtil;
import com.beeinc.mylibrary.util.ConstantData;
import com.beeinc.mylibrary.util.DialogUtil;
import com.beeinc.mylibrary.util.FileUtil;
import com.beeinc.mylibrary.util.ImageDealUtil;
import com.beeinc.mylibrary.util.NativeCallUnity;
import com.beeinc.mylibrary.util.OkHttpUtil;
import com.beeinc.mylibrary.util.OpenCVUtil;
import com.beeinc.mylibrary.util.ShapeUtil;
import com.beeinc.mylibrary.util.common;
import com.beeinc.mylibrary.views.CaiJianView;
import com.beeinc.mylibrary.views.JiaozhenView;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;
import com.yuci.okhttp.rxnet.base.BaseBean;
import com.yuci.okhttp.rxnet.client.RxObserver;
import com.yuci.okhttp.rxnet.client.RxRetrofit;
import com.yuci.okhttp.rxnet.utils.MapUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PicturesUploadActivity extends FragmentActivity {
    private final int HANDLE_HIDE_AUTO_FIX_FAIL = 1;//隐藏自动修图失败提示
    private final int HANDLE_HIDE_AUTO_FIX_SUCCESS = 2;//隐藏自动修图成功弹窗提示
    //顶部
    private View v_top, v_return;
    private View iv_return;
    private TextView tv_return, tv_import;
    //中间图片区部分
    private ImageView iv_picture;
    private TextView tv_rotate;
    private ImageView iv_rotate;
    //底部区域
    private FrameLayout frame_bottom;

    private FrameLayout frame_pictures;
    private ImageView iv_add, iv_picture_1, iv_picture_2, iv_picture_3, iv_picture_4, iv_picture_5, iv_picture_6;
    private View v_picture_1, v_picture_2, v_picture_3, v_picture_4, v_picture_5, v_picture_6;
    private View v_size,v_delete;
    private ImageView iv_size, iv_delete;
    private TextView tv_size,tv_delete;
    //自动修图根view
    private FrameLayout frame_auto;
    //自动修图控件-loading
    private FrameLayout frame_auto_loading;
    private View v_auto_loading_back;
    private ProgressBar progress_auto_loading_bar;
    private TextView tv_auto_loading_text;
    //自动修图控件-success
    private FrameLayout frame_auto_success;
    private FrameLayout frame_auto_success_dialog;
    private View v_auto_success_back, v_auto_success_bottom_back, v_auto_success_options_back, v_auto_success_bottom_quit,
            v_auto_success_bottom_ok, line_auto_success;
    private ImageView iv_auto_success_picture, iv_auto_success_quit, iv_auto_success_ok;
    private TextView tv_auto_success_text, tv_auto_success_quit, tv_auto_success_ok;
    private FrameLayout fl_auto_success_bottom;
    //自动修图控件-失败
    private FrameLayout frame_auto_fail;
    private View v_auto_fail_back;
    private ImageView iv_auto_fail_picture;
    private TextView tv_auto_fail_text, tv_auto_fail_hint;
    //矫正相关控件
    private FrameLayout frame_jiaozhen;
    private JiaozhenView jiaozhenView;
    private FrameLayout frame_jiaozhen_do;
    private ImageView iv_jiaozhen_do;
    private TextView tv_jiaozhen_do;
    private FrameLayout frame_jiaozhen_quit;
    private ImageView iv_jiaozhen_quit_back;
    private ImageView iv_jiaozhen_quit;
    private TextView tv_jiaozhen_quit;

    //裁剪相关控件
    private FrameLayout frame_caijian;
    private CaiJianView caijianView;
    private FrameLayout frame_caijian_do;
    private ImageView iv_caijian_do;
    private TextView tv_caijian_do;
    private FrameLayout frame_caijian_quit;
    private ImageView iv_caijian_quit_back;
    private ImageView iv_caijian_quit;
    private TextView tv_caijian_quit;

    private FrameLayout frame_auto_fix_options;//自动修图根布局
    private FrameLayout frame_manu_options;//手动修图根布局

    //自动修图
    private View v_auto_fix_back, v_auto_fix_line;
    private TextView tv_auto_fix_deal;

    private FrameLayout frame_auto_fix_hint;
    private ImageView iv_auto_fix_hint;
    private TextView tv_auto_fix_hint;




    //手动修图--选项部分
    private FrameLayout frame_manu_options_type;
    private View v_jiaozhen, v_cut, v_tiaose, v_tumo, v_yinyangse;
    private ImageView iv_jiaozhen, iv_cut, iv_tiaose, iv_tumo, iv_yinyangse;
    private TextView tv_jiaozhen, tv_cut, tv_tiaose, tv_tumo, tv_yinyangse;

    private FrameLayout frame_options;
    //手动修图--操作区-矫正部分
    private FrameLayout frame_manu_option_jiaozhen;
    private View v_jiaozheng_back;
    private ImageView iv_jiaozheng_hint;
    private TextView tv_jiaozheng_hint;
    //手动修图--操作区-裁剪部分
    private FrameLayout frame_manu_option_cut;
    private View v_caijian_back;
    private ImageView iv_caijian_hint;
    private TextView tv_caijian_hint;

//    //手动修图--操作区-调色部分
//    private FrameLayout frame_manu_option_tiaose;
//    private View v_tiaose_back,v_tiaose_light_back,v_tiaose_light_progress_back,v_tiaose_light_progress,v_tiaose_light_dot,v_tiaose_white_balance_back,
//            v_tiaose_white_balance_progress_back,v_tiaose_white_balance_progress,v_tiaose_white_balance_dot;
//    private TextView tv_tiaose_light,tv_tiaose_white_balance;
//    //手动修图--操作区-涂抹部分
//    private FrameLayout frame_manu_option_tumo;
//    private View v_tumo_back,v_tumo_size_back,v_tumo_size_progress_back,v_tumo_size_progress,v_tumo_size_dot;
//    private ImageView iv_tumo_hint;
//    TextView tv_tumo_hint,tv_tumo_bold,tv_tumo_small;


    //调色相关控件--临时
    private FrameLayout frame_manu_option_tiaose;
    private View v_tiaose_back;
    private ImageView iv_tiaose_hint;
    private TextView tv_tiaose_hint;
    //涂抹相关控件--临时
    private FrameLayout frame_manu_option_tumo;
    private View v_tumo_back;
    private ImageView iv_tumo_hint;
    private TextView tv_tumo_hint;
    //阴阳色相关控件--临时
    private FrameLayout frame_manu_option_yinyangse;
    private View v_yinyangse_back;
    private ImageView iv_yinyangse_hint;
    private TextView tv_yinyangse_hint;
    //底部
    private FrameLayout frame_auto_and_manu;
    private View v_auto_line, v_manu_line;
    private TextView tv_auto_deal, tv_manu_deal;

    private ArrayList<View> scaleViews = new ArrayList<>();
    private ArrayList<View> scaleTextViews = new ArrayList<>();

    private final int fix_type_auto = 1;//1自动修图
    private final int fix_type_manu = 2;//2手动修图
    private int fix_type = fix_type_auto;//1自动修图 ,2手动修图

    private final int fix_type_manu_jiaozhen = 0;//矫正
    private final int fix_type_manu_cut = 1;//裁剪
    private final int fix_type_manu_tiaose = 2;//调色
    private final int fix_type_manu_tumo = 3;//涂抹
    private final int fix_type_manu_yingyangse = 4;//阴阳色
    private int fix_type_manu_option = fix_type_manu_jiaozhen;

    private JiaozhenView.JiaoZhenListener jiaoZhenListener;
    private CaiJianView.CaijianListener caijianListener;

    private String imagePath;
    private boolean isAutoFixing;//是否正在修图
    private Bitmap mBitmap;
    private Bitmap bitmapAutoFix;//自动修图后的图片
    private Bitmap bitmapJiaoZhen;//矫正后的图
    private Bitmap bitmapCaijian;//裁剪后的图
    private BeeIncReceiver beeIncReceiver;

    private int num;//图片数量
    private String saveDir;//保存地址
    private int maxImageSize;//图片最长边大小
    private ArrayList<CategoryType> categoryTypes;

    private Handler handler;

    private boolean isEdit;//是否已被编辑过了。
    private QuitFixDialog quitFixDialog;

    private ArrayList<UploadFile> allUploadFiles = new ArrayList<UploadFile>();
    private BeeIncReceiver receiver;
    private UploadFile currentUploadFile;//当前操作对象
    private PopupWindow p;

    private boolean isDestroy;

    private ArrayList<AlbumFile> mAlbumFiles;

    private boolean isShowAutoHint = true;
    private final int maxVertulImageSize = 3000;//图片最长边虚拟尺寸大小
    private InputFixPictureInfoDialog inputFixPictureInfoDialog;
    private GradientDrawable manuTypeShape;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_edit);
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        ScreenUtil.setLayoutNum(this);
        hideNavigatonButton();
        ArrayList<UploadFile> uploadFiles = (ArrayList<UploadFile>) this.getIntent().getSerializableExtra("fileSelected");
        categoryTypes = (ArrayList<CategoryType>) getIntent().getSerializableExtra("categoryTypes");
        //保持最多6个图片
        if(uploadFiles.size()>6){
            for(int i=uploadFiles.size()-1;i>=6;i--){
                uploadFiles.remove(i);
            }
        }
        num = getIntent().getIntExtra("num", 6);
        if(num>6){
            num= 6;
        }
        saveDir = getIntent().getStringExtra("saveDir");
        if (saveDir.endsWith(File.separator)) {
            saveDir = saveDir;
        } else {
            saveDir = saveDir + File.separator;
        }
        init();
        allUploadFiles.addAll(uploadFiles);
        setCurrentUploadFile(uploadFiles.get(0));
        setPictures();
        showFixTypeAuto();
    }

    private void init() {
        getAllViews();
        setHandler();
        setParams();
        setScaleViews();
        setListener();
        initReceiver();
    }

    private void getAllViews() {
        //顶部
        v_top = findViewById(R.id.v_top);
        v_return = findViewById(R.id.v_return);
        iv_return = findViewById(R.id.iv_return);
        tv_return = findViewById(R.id.tv_return);
        tv_import = findViewById(R.id.tv_import);
        //中间图片
        iv_picture = findViewById(R.id.iv_picture);
        iv_rotate = findViewById(R.id.iv_rotate);
        tv_rotate = findViewById(R.id.tv_rotate);

        frame_auto = findViewById(R.id.frame_auto);
        //底部
        frame_bottom = findViewById(R.id.frame_bottom);
        //图片列表区
        frame_pictures = findViewById(R.id.frame_pictures);
        iv_add = findViewById(R.id.iv_add);
        iv_picture_1 = findViewById(R.id.iv_picture_1);
        iv_picture_2 = findViewById(R.id.iv_picture_2);
        iv_picture_3 = findViewById(R.id.iv_picture_3);
        iv_picture_4 = findViewById(R.id.iv_picture_4);
        iv_picture_5 = findViewById(R.id.iv_picture_5);
        iv_picture_6 = findViewById(R.id.iv_picture_6);

        v_picture_1 = findViewById(R.id.v_picture_1);
        v_picture_2 = findViewById(R.id.v_picture_2);
        v_picture_3 = findViewById(R.id.v_picture_3);
        v_picture_4 = findViewById(R.id.v_picture_4);
        v_picture_5 = findViewById(R.id.v_picture_5);
        v_picture_6 = findViewById(R.id.v_picture_6);

        v_size = findViewById(R.id.v_size);
        v_delete = findViewById(R.id.v_delete);

        iv_size = findViewById(R.id.iv_size);
        iv_delete = findViewById(R.id.iv_delete);
        tv_size = findViewById(R.id.tv_size);
        tv_delete = findViewById(R.id.tv_delete);

        //自动修图控件-loading
        frame_auto_loading = findViewById(R.id.frame_auto_loading);
        v_auto_loading_back = findViewById(R.id.v_auto_loading_back);
        progress_auto_loading_bar = findViewById(R.id.progress_auto_loading_bar);
        tv_auto_loading_text = findViewById(R.id.tv_auto_loading_text);
        //自动修图控件-success
        frame_auto_success = findViewById(R.id.frame_auto_success);
        frame_auto_success_dialog = findViewById(R.id.frame_auto_success_dialog);
        v_auto_success_back = findViewById(R.id.v_auto_success_back);
        v_auto_success_bottom_back = findViewById(R.id.v_auto_success_bottom_back);
        v_auto_success_options_back = findViewById(R.id.v_auto_success_options_back);
        v_auto_success_bottom_quit = findViewById(R.id.v_auto_success_bottom_quit);
        v_auto_success_bottom_ok = findViewById(R.id.v_auto_success_bottom_ok);
        line_auto_success = findViewById(R.id.line_auto_success);
        iv_auto_success_picture = findViewById(R.id.iv_auto_success_picture);

        iv_auto_success_quit = findViewById(R.id.iv_auto_success_quit);
        iv_auto_success_ok = findViewById(R.id.iv_auto_success_ok);
        tv_auto_success_text = findViewById(R.id.tv_auto_success_text);
        tv_auto_success_quit = findViewById(R.id.tv_auto_success_quit);
        tv_auto_success_ok = findViewById(R.id.tv_auto_success_ok);
        fl_auto_success_bottom = findViewById(R.id.fl_auto_success_bottom);

        //自动修图控件-失败
        frame_auto_fail = findViewById(R.id.frame_auto_fail);
        v_auto_fail_back = findViewById(R.id.v_auto_fail_back);
        iv_auto_fail_picture = findViewById(R.id.iv_auto_fail_picture);
        tv_auto_fail_text = findViewById(R.id.tv_auto_fail_text);
        tv_auto_fail_hint = findViewById(R.id.tv_auto_fail_hint);

        //矫正相关控件
        frame_jiaozhen = findViewById(R.id.frame_jiaozhen);
        jiaozhenView = findViewById(R.id.jiaozhenView);
        frame_jiaozhen_do = findViewById(R.id.frame_jiaozhen_do);
        iv_jiaozhen_do = findViewById(R.id.iv_jiaozhen_do);
        tv_jiaozhen_do = findViewById(R.id.tv_jiaozhen_do);
        frame_jiaozhen_quit = findViewById(R.id.frame_jiaozhen_quit);
        iv_jiaozhen_quit_back = findViewById(R.id.iv_jiaozhen_quit_back);
        iv_jiaozhen_quit = findViewById(R.id.iv_jiaozhen_quit);
        tv_jiaozhen_quit = findViewById(R.id.tv_jiaozhen_quit);

        //裁剪相关控件
        frame_caijian = findViewById(R.id.frame_caijian);
        caijianView = findViewById(R.id.caijianView);
        frame_caijian_do = findViewById(R.id.frame_caijian_do);
        iv_caijian_do = findViewById(R.id.iv_caijian_do);
        tv_caijian_do = findViewById(R.id.tv_caijian_do);
        frame_caijian_quit = findViewById(R.id.frame_caijian_quit);
        iv_caijian_quit_back = findViewById(R.id.iv_caijian_quit_back);
        iv_caijian_quit = findViewById(R.id.iv_caijian_quit);
        tv_caijian_quit = findViewById(R.id.tv_caijian_quit);

        frame_manu_options = findViewById(R.id.frame_manu_options);

        //自动修图
        frame_auto_fix_options = findViewById(R.id.frame_auto_fix_options);
        v_auto_fix_back = findViewById(R.id.v_auto_fix_back);
        v_auto_fix_line = findViewById(R.id.v_auto_fix_line);
        tv_auto_fix_deal = findViewById(R.id.tv_auto_fix_deal);

        frame_auto_fix_hint = findViewById(R.id.frame_auto_fix_hint);
        iv_auto_fix_hint = findViewById(R.id.iv_auto_fix_hint);
        tv_auto_fix_hint = findViewById(R.id.tv_auto_fix_hint);
        //手动修图--选项部分
        frame_manu_options_type = findViewById(R.id.frame_manu_options_type);
        v_jiaozhen = findViewById(R.id.v_jiaozhen);
        v_cut = findViewById(R.id.v_cut);
        v_tiaose = findViewById(R.id.v_tiaose);
        v_tumo = findViewById(R.id.v_tumo);
        v_yinyangse = findViewById(R.id.v_yinyangse);
        iv_jiaozhen = findViewById(R.id.iv_jiaozhen);
        iv_cut = findViewById(R.id.iv_cut);
        iv_tiaose = findViewById(R.id.iv_tiaose);
        iv_tumo = findViewById(R.id.iv_tumo);
        iv_yinyangse = findViewById(R.id.iv_yinyangse);
        tv_jiaozhen = findViewById(R.id.tv_jiaozhen);
        tv_cut = findViewById(R.id.tv_cut);
        tv_tiaose = findViewById(R.id.tv_tiaose);
        tv_tumo = findViewById(R.id.tv_tumo);
        tv_yinyangse = findViewById(R.id.tv_yinyangse);

        frame_options = findViewById(R.id.frame_options);
        //手动修图--操作区-矫正部分
        frame_manu_option_jiaozhen = findViewById(R.id.frame_manu_option_jiaozhen);
        v_jiaozheng_back = findViewById(R.id.v_jiaozheng_back);
        iv_jiaozheng_hint = findViewById(R.id.iv_jiaozheng_hint);
        tv_jiaozheng_hint = findViewById(R.id.tv_jiaozheng_hint);
        //手动修图--操作区-裁剪部分
        frame_manu_option_cut = findViewById(R.id.frame_manu_option_cut);
        v_caijian_back = findViewById(R.id.v_caijian_back);
        iv_caijian_hint = findViewById(R.id.iv_caijian_hint);
        tv_caijian_hint = findViewById(R.id.tv_caijian_hint);
//        //手动修图--操作区-调色部分
//        frame_manu_option_tiaose = findViewById(R.id.frame_manu_option_tiaose);
//        v_tiaose_back = findViewById(R.id.v_tiaose_back);
//        v_tiaose_light_back = findViewById(R.id.v_tiaose_light_back);
//        v_tiaose_light_progress_back = findViewById(R.id.v_tiaose_light_progress_back);
//        v_tiaose_light_progress = findViewById(R.id.v_tiaose_light_progress);
//        v_tiaose_light_dot = findViewById(R.id.v_tiaose_light_dot);
//        v_tiaose_white_balance_back = findViewById(R.id.v_tiaose_white_balance_back);
//        v_tiaose_white_balance_progress_back = findViewById(R.id.v_tiaose_white_balance_progress_back);
//        v_tiaose_white_balance_progress = findViewById(R.id.v_tiaose_white_balance_progress);
//        v_tiaose_white_balance_dot = findViewById(R.id.v_tiaose_white_balance_dot);
//        tv_tiaose_light = findViewById(R.id.tv_tiaose_light);
//        tv_tiaose_white_balance = findViewById(R.id.tv_tiaose_white_balance);
//        //手动修图--操作区-涂抹部分
//        frame_manu_option_tumo = findViewById(R.id.frame_manu_option_tumo);
//        v_tumo_back = findViewById(R.id.v_tumo_back);
//        v_tumo_size_back = findViewById(R.id.v_tumo_size_back);
//        v_tumo_size_progress_back = findViewById(R.id.v_tumo_size_progress_back);
//        v_tumo_size_progress = findViewById(R.id.v_tumo_size_progress);
//        v_tumo_size_dot = findViewById(R.id.v_tumo_size_dot);
//        iv_tumo_hint = findViewById(R.id.iv_tumo_hint);
//        tv_tumo_hint = findViewById(R.id.tv_tumo_hint);
//        tv_tumo_bold = findViewById(R.id.tv_tumo_bold);
//        tv_tumo_small = findViewById(R.id.tv_tumo_small);
        //调色相关控件--临时
        frame_manu_option_tiaose = findViewById(R.id.frame_manu_option_tiaose);
        v_tiaose_back = findViewById(R.id.v_tiaose_back);
        iv_tiaose_hint = findViewById(R.id.iv_tiaose_hint);
        tv_tiaose_hint = findViewById(R.id.tv_tiaose_hint);

        //涂抹相关控件--临时
        frame_manu_option_tumo = findViewById(R.id.frame_manu_option_tumo);
        v_tumo_back = findViewById(R.id.v_tumo_back);
        iv_tumo_hint = findViewById(R.id.iv_tumo_hint);
        tv_tumo_hint = findViewById(R.id.tv_tumo_hint);

        //阴阳色相关控件--临时
        frame_manu_option_yinyangse = findViewById(R.id.frame_manu_option_yinyangse);
        v_yinyangse_back = findViewById(R.id.v_yinyangse_back);
        iv_yinyangse_hint = findViewById(R.id.iv_yinyangse_hint);
        tv_yinyangse_hint = findViewById(R.id.tv_yinyangse_hint);

        //底部
        frame_auto_and_manu = findViewById(R.id.frame_auto_and_manu);
        tv_auto_deal = findViewById(R.id.tv_auto_deal);
        tv_manu_deal = findViewById(R.id.tv_manu_deal);
        v_auto_line = findViewById(R.id.v_auto_line);
        v_manu_line = findViewById(R.id.v_manu_line);
    }
    private void setHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case HANDLE_HIDE_AUTO_FIX_FAIL:
                        frame_auto_fail.setVisibility(View.INVISIBLE);
                        break;
                    case HANDLE_HIDE_AUTO_FIX_SUCCESS:
                        frame_auto_success_dialog.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        };
    }
    private void setParams() {
        LinearLayout.LayoutParams p_bottom = new LinearLayout.LayoutParams(Layout.getScale(1280), Layout.getScale(290));
        frame_bottom.setLayoutParams(p_bottom);

        LinearLayout.LayoutParams p_iv_hint = new LinearLayout.LayoutParams(Layout.getScale(28), Layout.getScale(28));
        p_iv_hint.setMargins(0, Layout.getScale(18), 0, 0);
        iv_jiaozheng_hint.setLayoutParams(p_iv_hint);
        iv_caijian_hint.setLayoutParams(p_iv_hint);
        iv_tiaose_hint.setLayoutParams(p_iv_hint);
        iv_tumo_hint.setLayoutParams(p_iv_hint);
        iv_yinyangse_hint.setLayoutParams(p_iv_hint);
        LinearLayout.LayoutParams p_tv_hint = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, Layout.getScale(62));
        p_tv_hint.setMargins(Layout.getScale(10), 0, 0, 0);
        tv_jiaozheng_hint.setLayoutParams(p_tv_hint);
        tv_caijian_hint.setLayoutParams(p_tv_hint);
        tv_tiaose_hint.setLayoutParams(p_tv_hint);
        tv_tumo_hint.setLayoutParams(p_tv_hint);
        tv_yinyangse_hint.setLayoutParams(p_tv_hint);

        Layout.setTextViewSize(tv_jiaozheng_hint, 20);
        Layout.setTextViewSize(tv_caijian_hint, 20);
        Layout.setTextViewSize(tv_tiaose_hint, 20);
        Layout.setTextViewSize(tv_tumo_hint, 20);
        Layout.setTextViewSize(tv_yinyangse_hint, 20);
        manuTypeShape = ShapeUtil.getTopShape(false,0,0,true,Layout.getScale(4),true,getResources().getColor(R.color.color393939));
        FrameLayout.LayoutParams p_rotate = new FrameLayout.LayoutParams(Layout.getScale(60), Layout.getScale(60));
        p_rotate.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        p_rotate.setMargins(0, 0, Layout.getScale(25), Layout.getScale(25));
        iv_rotate.setLayoutParams(p_rotate);

        FrameLayout.LayoutParams p_rotate_tv = new FrameLayout.LayoutParams(Layout.getScale(50), Layout.getScale(24));
        p_rotate_tv.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        p_rotate_tv.setMargins(0, Layout.getScale(30), Layout.getScale(32), Layout.getScale(33));
        tv_rotate.setLayoutParams(p_rotate_tv);
        Layout.setTextViewSize(tv_rotate,13);
//        mBitmap = FileUtil.decodeFile(imagePath,3000);
//        iv_picture.setImageBitmap(mBitmap);

//        //调色
//        GradientDrawable progress_tiaose_back= ShapeUtil.getShape(false,0,0,true, Layout.getScale(3),true,getResources().getColor(R.color.colorFFFFFF));
//        v_tiaose_light_progress_back.setBackground(progress_tiaose_back);
//        v_tiaose_white_balance_progress_back.setBackground(progress_tiaose_back);
//        GradientDrawable progress_tiaose= ShapeUtil.getShape(false,0,0,true, Layout.getScale(3),true,getResources().getColor(R.color.color00BABA));
//        v_tiaose_light_progress.setBackground(progress_tiaose);
//        v_tiaose_white_balance_progress.setBackground(progress_tiaose);
//        GradientDrawable dot_tiaose= ShapeUtil.getShape(true,2,getResources().getColor(R.color.color00BABA),true, Layout.getScale(20),true,getResources().getColor(R.color.colorFFFFFF));
//        v_tiaose_light_dot.setBackground(dot_tiaose);
//        v_tiaose_white_balance_dot.setBackground(dot_tiaose);
//        //涂抹
//        GradientDrawable progress_tumo_back= ShapeUtil.getShape(false,0,0,true, Layout.getScale(3),true,getResources().getColor(R.color.colorFFFFFF));
//        v_tumo_size_progress_back.setBackground(progress_tumo_back);
//        GradientDrawable progress_tumo= ShapeUtil.getShape(false,0,0,true, Layout.getScale(3),true,getResources().getColor(R.color.color00BABA));
//        v_tumo_size_progress.setBackground(progress_tumo);
//        GradientDrawable dot_tumo= ShapeUtil.getShape(true,2,getResources().getColor(R.color.color00BABA),true, Layout.getScale(20),true,getResources().getColor(R.color.colorFFFFFF));
//        v_tumo_size_dot.setBackground(dot_tumo);
    }

    private CategoryType getDefaultType() {
        int defaultPosition = 0;
        for (int i = 0; i < categoryTypes.size(); i++) {
            if (categoryTypes.get(i).getSort() == 0) {
                defaultPosition = i;
                break;
            }
        }
        return categoryTypes.get(defaultPosition);
    }

    private void setScaleViews() {
        //--------------顶部
        scaleViews.add(v_top);
        scaleViews.add(v_return);
        scaleViews.add(iv_return);
        scaleTextViews.add(tv_return);
        scaleTextViews.add(tv_import);
        //底部 图片列表区
        scaleViews.add(frame_pictures);
        scaleViews.add(iv_add);
        scaleViews.add(iv_picture_1);
        scaleViews.add(iv_picture_2);
        scaleViews.add(iv_picture_3);
        scaleViews.add(iv_picture_4);
        scaleViews.add(iv_picture_5);
        scaleViews.add(iv_picture_6);
        scaleViews.add(v_picture_1);
        scaleViews.add(v_picture_2);
        scaleViews.add(v_picture_3);
        scaleViews.add(v_picture_4);
        scaleViews.add(v_picture_5);
        scaleViews.add(v_picture_6);
        scaleViews.add(v_size);
        scaleViews.add(v_delete);
        scaleViews.add(iv_size);
        scaleViews.add(iv_delete);
        scaleTextViews.add(tv_size);
        scaleTextViews.add(tv_delete);

        //自动修图
        scaleViews.add(v_auto_fix_back);
        scaleViews.add(v_auto_fix_line);
        scaleTextViews.add(tv_auto_fix_deal);

        scaleViews.add(frame_auto_fix_hint);
        scaleViews.add(iv_auto_fix_hint);
        scaleTextViews.add(tv_auto_fix_hint);

        //自动修图控件-loading
        scaleViews.add(v_auto_loading_back);
        scaleViews.add(progress_auto_loading_bar);
        scaleTextViews.add(tv_auto_loading_text);
        //自动修图控件-success

        scaleViews.add(v_auto_success_back);
        scaleViews.add(v_auto_success_bottom_back);
        scaleViews.add(v_auto_success_options_back);
        scaleViews.add(v_auto_success_bottom_quit);
        scaleViews.add(v_auto_success_bottom_ok);
        scaleViews.add(line_auto_success);
        scaleViews.add(iv_auto_success_picture);
        scaleViews.add(iv_auto_success_quit);
        scaleViews.add(iv_auto_success_ok);


        scaleTextViews.add(tv_auto_success_text);
        scaleTextViews.add(tv_auto_success_quit);
        scaleTextViews.add(tv_auto_success_ok);

        //自动修图控件-失败
        scaleViews.add(v_auto_fail_back);
        scaleViews.add(iv_auto_fail_picture);
        scaleTextViews.add(tv_auto_fail_text);
        scaleTextViews.add(tv_auto_fail_hint);
        frame_auto_fail = findViewById(R.id.frame_auto_fail);




        //矫正相关控件
        scaleViews.add(iv_jiaozhen_do);
        scaleViews.add(iv_jiaozhen_quit_back);
        scaleViews.add(iv_jiaozhen_quit);
        scaleTextViews.add(tv_jiaozhen_do);
        scaleTextViews.add(tv_jiaozhen_quit);

        //裁剪相关控件
        scaleViews.add(iv_caijian_do);
        scaleViews.add(iv_caijian_quit_back);
        scaleViews.add(iv_caijian_quit);
        scaleTextViews.add(tv_caijian_do);
        scaleTextViews.add(tv_caijian_quit);

        //手动修图--选项部分
        scaleViews.add(frame_manu_options_type);
        scaleViews.add(v_jiaozhen);
        scaleViews.add(v_cut);
        scaleViews.add(v_tiaose);
        scaleViews.add(v_tumo);
        scaleViews.add(v_yinyangse);
        scaleViews.add(iv_jiaozhen);
        scaleViews.add(iv_cut);
        scaleViews.add(iv_tiaose);
        scaleViews.add(iv_tumo);
        scaleViews.add(iv_yinyangse);
        scaleTextViews.add(tv_jiaozhen);
        scaleTextViews.add(tv_cut);
        scaleTextViews.add(tv_tiaose);
        scaleTextViews.add(tv_tumo);
        scaleTextViews.add(tv_yinyangse);

        scaleViews.add(frame_options);

        //手动修图--操作区-矫正部分
        scaleViews.add(v_jiaozheng_back);
        //手动修图--操作区-裁剪部分
        scaleViews.add(v_caijian_back);
//        //手动修图--操作区-调色部分
//        scaleViews.add(v_tiaose_back);
//        scaleViews.add(v_tiaose_light_back);
//        scaleViews.add(v_tiaose_light_progress_back);
//        scaleViews.add(v_tiaose_light_progress);
//        scaleViews.add(v_tiaose_light_dot);
//        scaleViews.add(v_tiaose_white_balance_back);
//        scaleViews.add(v_tiaose_white_balance_progress_back);
//        scaleViews.add(v_tiaose_white_balance_progress);
//        scaleViews.add(v_tiaose_white_balance_dot);
//
//        scaleTextViews.add(tv_tiaose_light);
//        scaleTextViews.add(tv_tiaose_white_balance);
//
//        //手动修图--操作区-涂抹部分
//        scaleViews.add(v_tumo_back);
//        scaleViews.add(v_tumo_size_back);
//        scaleViews.add(v_tumo_size_progress_back);
//        scaleViews.add(v_tumo_size_progress);
//        scaleViews.add(v_tumo_size_dot);
//        scaleViews.add(iv_tumo_hint);
//
//        scaleTextViews.add(tv_tumo_hint);
//        scaleTextViews.add(tv_tumo_bold);
//        scaleTextViews.add(tv_tumo_small);

        //调色相关控件--临时
        scaleViews.add(v_tiaose_back);

        //涂抹相关控件--临时
        scaleViews.add(v_tumo_back);

        //阴阳色相关控件--临时
        scaleViews.add(v_yinyangse_back);

        //--------------底部
        scaleViews.add(frame_auto_and_manu);
        scaleViews.add(v_auto_line);
        scaleViews.add(v_manu_line);
        scaleTextViews.add(tv_auto_deal);
        scaleTextViews.add(tv_manu_deal);

        FrameScaleUtil.scale(scaleViews, FrameScaleUtil.X_CENTER, FrameScaleUtil.Y_TOP);
        FrameScaleUtil.scale(scaleTextViews, FrameScaleUtil.X_CENTER, FrameScaleUtil.Y_TOP, FrameScaleUtil.TYPE_TEXT_VIEW);
    }

    private void setListener() {

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.v_return) {
                    setReturn();
                }
                if (v.getId() == R.id.tv_import) {
                    doImport();
                }
                if (v.getId() == R.id.iv_rotate) {
                    rotate();
                }
                if (v.getId() == R.id.iv_add) {
                    startAlbum();
                }
                if(v.getId()== R.id.iv_picture_1){
                    changePosition(0);
                }
                if(v.getId()== R.id.iv_picture_2){
                    changePosition(1);
                }
                if(v.getId()== R.id.iv_picture_3){
                    changePosition(2);
                }
                if(v.getId()== R.id.iv_picture_4){
                    changePosition(3);
                }
                if(v.getId()== R.id.iv_picture_5){
                    changePosition(4);
                }
                if(v.getId()== R.id.iv_picture_6){
                    changePosition(5);
                }
                if(v.getId()== R.id.iv_size){
                    showSetInfoDialog();
                }
                if(v.getId()== R.id.iv_delete){
                    deleteFile();
                }
                if (v.getId() == R.id.tv_auto_fix_deal) {
                    isEdit = true;
                    autoFix();
                }
                if (v.getId() == R.id.tv_auto_deal) {
                    selectFixType(fix_type_auto);
                }
                if (v.getId() == R.id.tv_manu_deal) {
                    selectFixType(fix_type_manu);
                }
                if (v.getId() == R.id.tv_jiaozhen_do) {
                    if (jiaozhenView.JIAOZHENG_STATUS == jiaozhenView.JIAOZHENG_MOVED_CIRCLES) {
                        jiaozhenView.setJiaozhenStart();
                        endJiaoZhengUI();
                        bitmapJiaoZhen = OpenCVUtil.getJiaoZhenBitmap(mBitmap, jiaozhenView.getPointsBeforeJiaozheng(), jiaozhenView.getPointsAfterJiaozhen());
                        jiaozhenView.setJiaozhengEnd();
                        saveCurrentBitmap(bitmapJiaoZhen);
                        setListCurrentPictureBitmap(bitmapJiaoZhen);

                        iv_picture.setImageBitmap(bitmapJiaoZhen);
                        frame_jiaozhen_do.setVisibility(View.INVISIBLE);
                        frame_jiaozhen_quit.setVisibility(View.VISIBLE);
                    }
                }
                if (v.getId() == R.id.iv_jiaozhen_quit_back) {
                    if (jiaozhenView.JIAOZHENG_STATUS == jiaozhenView.JIAOZHENG_ENDED) {
                        bitmapJiaoZhen.recycle();
                        bitmapJiaoZhen = null;
                        frame_jiaozhen_quit.setVisibility(View.INVISIBLE);
                        tv_jiaozheng_hint.setText(getResources().getString(R.string.jiaozheng_hint_1));
                        saveCurrentBitmap(mBitmap);
                        setListCurrentPictureBitmap(mBitmap);
                        iv_picture.setImageBitmap(mBitmap);
                        jiaozhenView.setImageBitmap(mBitmap);
                    }
                }
                if (v.getId() == R.id.tv_caijian_do) {
                    if (caijianView.CAIJIAN_STATUS == jiaozhenView.JIAOZHENG_MOVED_CIRCLES) {
                        endJiaoZhengUI();
                        caijianView.setCaiJianStart();
                        bitmapCaijian = OpenCVUtil.getJiaoZhenBitmap(mBitmap, caijianView.getPointsBeforeCaijian(), caijianView.getPointsAfterCaijian());
                        caijianView.setCaiJianEnd();
                        saveCurrentBitmap(bitmapCaijian);
                        setListCurrentPictureBitmap(bitmapCaijian);
                        iv_picture.setImageBitmap(bitmapCaijian);
                        frame_caijian_do.setVisibility(View.INVISIBLE);
                        frame_caijian_quit.setVisibility(View.VISIBLE);
                    }
                }
                if (v.getId() == R.id.iv_caijian_quit_back) {
                    if (caijianView.CAIJIAN_STATUS == caijianView.CAIJIAN_END) {
                        tv_caijian_hint.setText(getResources().getString(R.string.caijian_hint_1));
                        bitmapCaijian.recycle();
                        bitmapCaijian = null;
                        frame_caijian_quit.setVisibility(View.INVISIBLE);
                        saveCurrentBitmap(mBitmap);
                        setListCurrentPictureBitmap(mBitmap);

                        iv_picture.setImageBitmap(mBitmap);
                        caijianView.setImageBitmap(mBitmap);
                    }
                }
                if (v.getId() == R.id.v_jiaozhen) {
                    if(fix_type_manu_option==fix_type_manu_jiaozhen){
                        return;
                    }
                    showManuType(fix_type_manu_jiaozhen, false);
                }
                if (v.getId() == R.id.v_cut) {
                    if(fix_type_manu_option==fix_type_manu_cut){
                        return;
                    }
                    showManuType(fix_type_manu_cut, false);

                }
                if (v.getId() == R.id.v_tiaose) {
                    if(fix_type_manu_option==fix_type_manu_tiaose){
                        return;
                    }
                    showManuType(fix_type_manu_tiaose, false);

                }
                if (v.getId() == R.id.v_tumo) {
                    if(fix_type_manu_option==fix_type_manu_tumo){
                        return;
                    }
                    showManuType(fix_type_manu_tumo, false);

                }
                if (v.getId() == R.id.v_yinyangse) {
                    if(fix_type_manu_option==fix_type_manu_yingyangse){
                        return;
                    }
                    showManuType(fix_type_manu_yingyangse, false);
                }
            }
        };
        v_return.setOnClickListener(listener);
        tv_import.setOnClickListener(listener);
        iv_rotate.setOnClickListener(listener);
        iv_add.setOnClickListener(listener);
        iv_picture_1.setOnClickListener(listener);
        iv_picture_2.setOnClickListener(listener);
        iv_picture_3.setOnClickListener(listener);
        iv_picture_4.setOnClickListener(listener);
        iv_picture_5.setOnClickListener(listener);
        iv_picture_6.setOnClickListener(listener);
        iv_size.setOnClickListener(listener);
        iv_delete.setOnClickListener(listener);
        tv_auto_fix_deal.setOnClickListener(listener);
        tv_auto_deal.setOnClickListener(listener);
        tv_manu_deal.setOnClickListener(listener);
        v_jiaozhen.setOnClickListener(listener);
        v_cut.setOnClickListener(listener);
        v_tiaose.setOnClickListener(listener);
        v_tumo.setOnClickListener(listener);
        v_yinyangse.setOnClickListener(listener);

        tv_jiaozhen_do.setOnClickListener(listener);
        iv_jiaozhen_quit_back.setOnClickListener(listener);
        tv_caijian_do.setOnClickListener(listener);
        iv_caijian_quit_back.setOnClickListener(listener);

        //自动修图
        setAutoFixListener();
        //调色
        setTiaoSeListener();
        //涂抹
        setTumoListener();


        jiaoZhenListener = new JiaozhenView.JiaoZhenListener() {
            @Override
            public void isMovedJiaoZhenCircles(boolean isMoved) {
                Log.e("-------------->","isMoved" + isMoved);
                if(isMoved){
                    startJiaoZhengUI();
                    isEdit = true;
                    frame_jiaozhen_do.setVisibility(View.VISIBLE);
                    tv_jiaozheng_hint.setText(getResources().getString(R.string.jiaozheng_hint_2));
                }else{
                    endJiaoZhengUI();
                    tv_jiaozheng_hint.setText(getResources().getString(R.string.jiaozheng_hint_1));
                    frame_jiaozhen_do.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void jiaoZhengStart() {

            }

            @Override
            public void jiaoZhengEnd() {
                tv_jiaozheng_hint.setText(getResources().getString(R.string.jiaozheng_hint_3));
            }
        };
        jiaozhenView.setJiaoZhenListener(jiaoZhenListener);
        caijianListener = new CaiJianView.CaijianListener() {
            @Override
            public void isMovedCaijianCircles(boolean isMoved) {
                Log.e("-------------->","isMoved" + isMoved);
                isEdit = true;
                if(isMoved){
                    startJiaoZhengUI();
                    tv_caijian_hint.setText(getResources().getString(R.string.caijian_hint_2));
                    frame_caijian_do.setVisibility(View.VISIBLE);
                }else{
                    endJiaoZhengUI();
                    tv_caijian_hint.setText(getResources().getString(R.string.caijian_hint_1));
                    frame_caijian_quit.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void caiJianStart() {

            }

            @Override
            public void caiJianEnd() {
                tv_caijian_hint.setText(getResources().getString(R.string.caijian_hint_3));
            }
        };
        caijianView.setCaijianListener(caijianListener);
    }

    private void setReturn() {
        if (!isEdit) {
            common.finishActivity(PicturesUploadActivity.this);
        } else {
            quitFixDialog = DialogUtil.showQuitFixDialog(PicturesUploadActivity.this, quitFixDialog);
        }
    }

    private void rotate() {
        if(currentUploadFile==null){
            Toast.makeText(this,getResources().getString(R.string.please_select_an_image),Toast.LENGTH_SHORT).show();
            return;
        }

        if (fix_type == fix_type_auto) {
            mBitmap = OpenCVUtil.rotateBitmap(mBitmap);
            saveCurrentBitmap(mBitmap);
            setListCurrentPictureBitmap(mBitmap);
            iv_picture.setImageBitmap(mBitmap);

        } else {
            switch (fix_type_manu_option) {
                case fix_type_manu_jiaozhen:
                    if (jiaozhenView.JIAOZHENG_STATUS == jiaozhenView.JIAOZHENG_UNSTART ||
                            jiaozhenView.JIAOZHENG_STATUS == jiaozhenView.JIAOZHENG_MOVED_CIRCLES) {
                        frame_jiaozhen_do.setVisibility(View.INVISIBLE);
                        mBitmap = OpenCVUtil.rotateBitmap(mBitmap);
                        saveCurrentBitmap(mBitmap);
                        setListCurrentPictureBitmap(mBitmap);
                        iv_picture.setImageBitmap(mBitmap);
                        jiaozhenView.setImageBitmap(mBitmap);
                    } else if (jiaozhenView.JIAOZHENG_STATUS == jiaozhenView.JIAOZHENG_ENDED) {
                        frame_jiaozhen_quit.setVisibility(View.INVISIBLE);
                        mBitmap = OpenCVUtil.rotateBitmap(bitmapJiaoZhen);
                        bitmapJiaoZhen.recycle();
                        bitmapJiaoZhen = null;
                        saveCurrentBitmap(mBitmap);
                        setListCurrentPictureBitmap(mBitmap);
                        jiaozhenView.setImageBitmap(mBitmap);
                        iv_picture.setImageBitmap(mBitmap);
                    }
                    break;
                case fix_type_manu_cut:
                    if (caijianView.CAIJIAN_STATUS == caijianView.CAIJIAN_UNSTART ||
                            caijianView.CAIJIAN_STATUS == caijianView.CAIJIAN_MOVED_CIRCLES) {
                        frame_caijian_do.setVisibility(View.INVISIBLE);
                        mBitmap = OpenCVUtil.rotateBitmap(mBitmap);
                        saveCurrentBitmap(mBitmap);
                        setListCurrentPictureBitmap(mBitmap);
                        iv_picture.setImageBitmap(mBitmap);
                        caijianView.setImageBitmap(mBitmap);
                    } else if (caijianView.CAIJIAN_STATUS == caijianView.CAIJIAN_END) {
                        frame_caijian_quit.setVisibility(View.INVISIBLE);
                        mBitmap = OpenCVUtil.rotateBitmap(bitmapCaijian);
                        setListCurrentPictureBitmap(mBitmap);
                        saveCurrentBitmap(mBitmap);
                        bitmapCaijian.recycle();
                        bitmapCaijian = null;
                        caijianView.setImageBitmap(mBitmap);
                        iv_picture.setImageBitmap(mBitmap);
                    }
                    break;
                case fix_type_manu_tiaose:
                case fix_type_manu_tumo:
                case fix_type_manu_yingyangse:
                    mBitmap = OpenCVUtil.rotateBitmap(mBitmap);
                    saveCurrentBitmap(mBitmap);
                    setListCurrentPictureBitmap(mBitmap);
                    iv_picture.setImageBitmap(mBitmap);
                    break;
            }
        }
    }
    private void saveCurrentBitmap(Bitmap bitmap){
        String path = FileUtil.getCacheSaveImagesDir(this)+FileUtil.getNewFileNameByTime()+".jpg";
        Mat mat = OpenCVUtil.bitmapToMat(bitmap);
        Imgcodecs.imwrite(path,mat);
        currentUploadFile.setFilePath(path);
    }
    private void setListCurrentPictureBitmap(Bitmap bitmap){
        Bitmap thumbBitmap;//列表中的图
        thumbBitmap = OpenCVUtil.bitmapScale(bitmap,150);
        int currentPosition = getCurrentPostion();
        setListPictureBitmap(currentPosition,thumbBitmap);
    }
    private void setListPictureBitmap(int position,Bitmap bitmap){
        switch (position){
            case 0:
                iv_picture_1.setImageBitmap(bitmap);
                break;
            case 1:
                iv_picture_2.setImageBitmap(bitmap);
                break;
            case 2:
                iv_picture_3.setImageBitmap(bitmap);
                break;
            case 3:
                iv_picture_4.setImageBitmap(bitmap);
                break;
            case 4:
                iv_picture_5.setImageBitmap(bitmap);
                break;
            case 5:
                iv_picture_6.setImageBitmap(bitmap);
                break;
        }
    }
    private void changePosition(int position){
        int currentPosition = getCurrentPostion();
        if(position==currentPosition){
            return;
        }
        if(mBitmap!=null){
            mBitmap.recycle();
            mBitmap = null;
        }
        if(position<allUploadFiles.size()){
            if(fix_type==fix_type_manu){
                //保存当前数据
                switch (fix_type_manu_option){
                    case fix_type_manu_jiaozhen:
                        if(bitmapJiaoZhen!=null){
                            bitmapJiaoZhen.recycle();
                            bitmapJiaoZhen = null;
                        }

                        break;
                    case fix_type_manu_cut:
                        if(bitmapCaijian!=null){
                            bitmapCaijian.recycle();
                            bitmapCaijian = null;
                        }
                        break;
                }
            }

            setCurrentUploadFile(allUploadFiles.get(position));
            setCurrentSelectBoardShow();
            resetStatus();
        }


    }
    private int getCurrentPostion(){
        if(currentUploadFile==null){
            return -1;
        }
        for(int i=0;i<allUploadFiles.size();i++){
            if(currentUploadFile==allUploadFiles.get(i)){
                return i;
            }
        }
        return -1;
    }
    private void showJiaozhengView() {
        frame_jiaozhen.setVisibility(View.VISIBLE);
        frame_jiaozhen_do.setVisibility(View.INVISIBLE);
        frame_jiaozhen_quit.setVisibility(View.INVISIBLE);
        jiaozhenView.setImageBitmap(mBitmap);
    }

    private void showcaijianView() {
        frame_caijian.setVisibility(View.VISIBLE);
        frame_caijian_do.setVisibility(View.INVISIBLE);
        frame_caijian_quit.setVisibility(View.INVISIBLE);
        caijianView.setImageBitmap(mBitmap);
    }

    private void changeManuUI(int postion) {
        iv_jiaozhen.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.b_icon_jiaozhen_un, false));
        iv_cut.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.b_icon_cut_un, false));
        iv_tiaose.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.b_icon_tiaose_un, false));
        iv_tumo.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.b_icon_mochu_un, true));
        iv_yinyangse.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.b_icon_yingyangse_un, false));
        tv_jiaozhen.setTextColor(getResources().getColor(R.color.colorFFFFFF));
        tv_cut.setTextColor(getResources().getColor(R.color.colorFFFFFF));
        tv_tiaose.setTextColor(getResources().getColor(R.color.color393939));
        tv_tumo.setTextColor(getResources().getColor(R.color.color393939));
        tv_yinyangse.setTextColor(getResources().getColor(R.color.color393939));

        v_jiaozhen.setBackgroundColor(getResources().getColor(R.color.color000000));
        v_cut.setBackgroundColor(getResources().getColor(R.color.color000000));
        v_tiaose.setBackgroundColor(getResources().getColor(R.color.color000000));
        v_tumo.setBackgroundColor(getResources().getColor(R.color.color000000));
        v_yinyangse.setBackgroundColor(getResources().getColor(R.color.color000000));

        switch (postion) {
            case fix_type_manu_jiaozhen:
                iv_jiaozhen.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.b_icon_jiaozheng_selected, false));
                tv_jiaozhen.setTextColor(getResources().getColor(R.color.color00BABA));
                v_jiaozhen.setBackground(manuTypeShape);
                break;
            case fix_type_manu_cut:
                iv_cut.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.b_icon_cut_selected, false));
                tv_cut.setTextColor(getResources().getColor(R.color.color00BABA));
                v_cut.setBackground(manuTypeShape);

                break;
            case fix_type_manu_tiaose:
                iv_tiaose.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.b_icon_tiaose_selected, false));
                tv_tiaose.setTextColor(getResources().getColor(R.color.color00BABA));
                v_tiaose.setBackground(manuTypeShape);

                break;
            case fix_type_manu_tumo:
                iv_tumo.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.b_icon_mochu_selected, false));
                tv_tumo.setTextColor(getResources().getColor(R.color.color00BABA));
                v_tumo.setBackground(manuTypeShape);

                break;
            case fix_type_manu_yingyangse:
                iv_yinyangse.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.b_icon_yingyangse_selected, false));
                tv_yinyangse.setTextColor(getResources().getColor(R.color.color00BABA));
                v_yinyangse.setBackground(manuTypeShape);

                break;
        }
    }

    private void quitCurrentAutoOptions() {
        if (bitmapAutoFix != null) {
            mBitmap.recycle();
            mBitmap = null;
            mBitmap = bitmapAutoFix;
        }
    }

    //取消当前手动修图操作
    private void quitCurrentManuOptions() {
        switch (fix_type_manu_option) {
            case fix_type_manu_jiaozhen:
                if (jiaozhenView.JIAOZHENG_STATUS == jiaozhenView.JIAOZHENG_UNSTART ||
                        jiaozhenView.JIAOZHENG_STATUS == jiaozhenView.JIAOZHENG_MOVED_CIRCLES) {
                    frame_jiaozhen.setVisibility(View.INVISIBLE);
                } else {
                    frame_jiaozhen.setVisibility(View.INVISIBLE);
                    mBitmap.recycle();
                    mBitmap = null;
                    mBitmap = bitmapJiaoZhen;
                }
                break;
            case fix_type_manu_cut:
                if (caijianView.CAIJIAN_STATUS == caijianView.CAIJIAN_UNSTART ||
                        caijianView.CAIJIAN_STATUS == caijianView.CAIJIAN_MOVED_CIRCLES) {
                    frame_caijian.setVisibility(View.INVISIBLE);
                } else {
                    frame_caijian.setVisibility(View.INVISIBLE);
                    mBitmap.recycle();
                    mBitmap = null;
                    mBitmap = bitmapCaijian;
                }
                break;
            case fix_type_manu_tiaose:

                break;
            case fix_type_manu_tumo:

                break;
            case fix_type_manu_yingyangse:

                break;
        }
    }

    private void setAutoFixListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.v_auto_success_bottom_quit) {
                    if (bitmapAutoFix != null) {
                        bitmapAutoFix.recycle();
                        bitmapAutoFix = null;
                    }
                    iv_picture.setImageBitmap(mBitmap);
                    setListCurrentPictureBitmap(mBitmap);
                    showAutoFixViews();
                    setAutoFixEndUI();
                } else if (view.getId() == R.id.v_auto_success_bottom_ok) {
                    if (mBitmap != null) {
                        mBitmap.recycle();
                        mBitmap = null;
                    }
                    mBitmap = bitmapAutoFix;
                    bitmapAutoFix = null;
                    saveCurrentBitmap(mBitmap);
                    setListCurrentPictureBitmap(mBitmap);
                    showAutoFixViews();
                    setAutoFixEndUI();
                }
            }
        };
        v_auto_success_bottom_quit.setOnClickListener(listener);
        v_auto_success_bottom_ok.setOnClickListener(listener);
    }

    private void setTiaoSeListener() {
//        v_tiaose_light_back.setOnTouchListener(new View.OnTouchListener() {
//            int x;
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        x = (int)motionEvent.getRawX();
//                        showLightLocation(x);
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        x = (int)motionEvent.getRawX();
//                        showLightLocation(x);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        x = (int)motionEvent.getRawX();
//                        showLightLocation(x);
//                        break;
//                }
//                return true;
//            }
//        });
//        v_tiaose_white_balance_back.setOnTouchListener(new View.OnTouchListener() {
//            int x;
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        x = (int)motionEvent.getRawX();
//                        showWhiteBalanceLocation(x);
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        x = (int)motionEvent.getRawX();
//                        showWhiteBalanceLocation(x);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        x = (int)motionEvent.getRawX();
//                        showWhiteBalanceLocation  (x);
//                        break;
//                }
//                return true;
//            }
//        });
    }

    private void setTumoListener() {
//        v_tumo_size_back.setOnTouchListener(new View.OnTouchListener() {
//            int x;
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        x = (int)motionEvent.getRawX();
//                        showSizeLocation(x);
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        x = (int)motionEvent.getRawX();
//                        showSizeLocation(x);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        x = (int)motionEvent.getRawX();
//                        showSizeLocation(x);
//                        break;
//                }
//                return true;
//            }
//        });
    }

    private void hideAutoFixViews() {
        frame_auto.setVisibility(View.INVISIBLE);
    }

    private void showAutoFixViews() {
        frame_auto.setVisibility(View.VISIBLE);
        frame_auto_loading.setVisibility(View.INVISIBLE);
        frame_auto_success.setVisibility(View.INVISIBLE);
        frame_auto_fail.setVisibility(View.INVISIBLE);
        frame_auto_and_manu.setBackgroundColor(getResources().getColor(R.color.color000000));

    }

    private void showAutoFixViewFixing() {
        frame_auto_loading.setVisibility(View.VISIBLE);
        frame_auto_success.setVisibility(View.INVISIBLE);
        frame_auto_fail.setVisibility(View.INVISIBLE);
    }

    private void showAutoFixViewFixSuccess() {
        frame_auto_loading.setVisibility(View.INVISIBLE);
        frame_auto_success.setVisibility(View.VISIBLE);
        frame_auto_success_dialog.setVisibility(View.VISIBLE);
        frame_auto_fail.setVisibility(View.INVISIBLE);
        handler.sendEmptyMessageDelayed(HANDLE_HIDE_AUTO_FIX_SUCCESS, 2000);

    }

    private void showAutoFixViewFail() {
        frame_auto_loading.setVisibility(View.INVISIBLE);
        frame_auto_success.setVisibility(View.INVISIBLE);
        frame_auto_fail.setVisibility(View.VISIBLE);
        handler.sendEmptyMessageDelayed(HANDLE_HIDE_AUTO_FIX_FAIL, 2000);
    }

    private void initReceiver() {
        receiver = new BeeIncReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.beeinc.album.takpicture");
        filter.addAction("com.beeinc.album.takpicture.finish");
        filter.addAction("com.beeinc.album.takpicture.album");
        BroadcastReceiverRegisterUtil.registerReceiver(this,receiver, filter,true);
    }

    private void setCurrentUploadFile(UploadFile currentUploadFile){
        this.currentUploadFile = currentUploadFile;
        if(iv_picture.getVisibility()==View.INVISIBLE){
            iv_picture.setVisibility(View.VISIBLE);
        }
        if(mBitmap!=null){
            mBitmap.recycle();
            mBitmap = null;
        }
        mBitmap = OpenCVUtil.getBitmapFromFile(currentUploadFile.getFilePath());
        iv_picture.setImageBitmap(mBitmap);
    }
    private void setPictures() {
        //显示图片
        for(int i=0;i<allUploadFiles.size();i++){
            Bitmap bitmap = OpenCVUtil.getBitmapFromFile(allUploadFiles.get(i).getFilePath());
//            setListPictureBitmap(i,bitmap);
            setListPictureBitmap(i,OpenCVUtil.bitmapScale(bitmap,150));
            bitmap.recycle();
            bitmap = null;
        }
        //设置没有图片的显示
        if(allUploadFiles.size()<6){
            for(int i=allUploadFiles.size();i<6;i++){
                switch (i){
                    case 0:
                        iv_picture_1.setImageDrawable(getResources().getDrawable(R.drawable.shape_27));
                        break;
                    case 1:
                        iv_picture_2.setImageDrawable(getResources().getDrawable(R.drawable.shape_27));
                        break;
                    case 2:
                        iv_picture_3.setImageDrawable(getResources().getDrawable(R.drawable.shape_27));
                        break;
                    case 3:
                        iv_picture_4.setImageDrawable(getResources().getDrawable(R.drawable.shape_27));
                        break;
                    case 4:
                        iv_picture_5.setImageDrawable(getResources().getDrawable(R.drawable.shape_27));
                        break;
                    case 5:
                        iv_picture_6.setImageDrawable(getResources().getDrawable(R.drawable.shape_27));
                        break;
                }
            }
        }
        //设置选中的边框
        setCurrentSelectBoardShow();
    }

    private void setCurrentSelectBoardShow(){
        int curentPostion = getCurrentPostion();
        v_picture_1.setVisibility(View.INVISIBLE);
        v_picture_2.setVisibility(View.INVISIBLE);
        v_picture_3.setVisibility(View.INVISIBLE);
        v_picture_4.setVisibility(View.INVISIBLE);
        v_picture_5.setVisibility(View.INVISIBLE);
        v_picture_6.setVisibility(View.INVISIBLE);
        switch (curentPostion){
            case 0:
                v_picture_1.setVisibility(View.VISIBLE);
                break;
            case 1:
                v_picture_2.setVisibility(View.VISIBLE);
                break;
            case 2:
                v_picture_3.setVisibility(View.VISIBLE);
                break;
            case 3:
                v_picture_4.setVisibility(View.VISIBLE);
                break;
            case 4:
                v_picture_5.setVisibility(View.VISIBLE);
                break;
            case 5:
                v_picture_6.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void changeImagePosition(UploadFile uploadFile) {
        if (uploadFile == currentUploadFile) {
            return;
        } else {
            currentUploadFile = uploadFile;
//            showPic();
        }
    }

    private void showSelectPosition() {
//        if(allUploadFiles.size()<=0){
//            return;
//        }
//        int position = getFilePosition(currentUploadFile);
//        for(int i=0;i<allUploadFiles.size();i++){
//            if(position==i){
//                ((FrameLayout)lienar_image.getChildAt(i)).getChildAt(2).setVisibility(View.VISIBLE);
//            }else{
//                ((FrameLayout)lienar_image.getChildAt(i)).getChildAt(2).setVisibility(View.INVISIBLE);
//            }
//        }
    }

    //选择图片后
    private void addfiles() {
        ArrayList<UploadFile> uploadFiles = new ArrayList<>();
        String dir = FileUtil.getCacheSaveImagesDir(PicturesUploadActivity.this);
        for(int i=0;i<mAlbumFiles.size();i++) {
            UploadFile uploadFile = new UploadFile();
            //保存到内部缓存
            String fileName = FileUtil.getNewFileNameByTime()+"_"+i+".jpg";
            String filePath = dir+fileName;
            OpenCVUtil.reSaveFile(PicturesUploadActivity.this,mAlbumFiles.get(i).getPath(),mAlbumFiles.get(i).getUriContentPath(),filePath,2300);
            uploadFile.setFilePath(filePath);
            uploadFiles.add(uploadFile);
        }
        allUploadFiles.addAll(uploadFiles);
        if(currentUploadFile==null){
            setCurrentUploadFile(allUploadFiles.get(0));
        }
        setPictures();
    }
    private int getSavedPicNum() {
        int savedPictureNum = 0;
        for (int i = 0; i < allUploadFiles.size(); i++) {
            if (allUploadFiles.get(i).isSave()) {
                savedPictureNum = savedPictureNum + 1;
            }
        }
        return savedPictureNum;
    }
    private void showSetInfoDialog(){
        if(currentUploadFile==null){
            Toast.makeText(this,getResources().getString(R.string.please_select_an_image),Toast.LENGTH_SHORT).show();
            return;
        }
        int bitmapWidth = mBitmap.getWidth();
        int bitmapHeight = mBitmap.getHeight();
        int width ;
        int height;
        if(bitmapWidth>bitmapHeight){
            width = maxVertulImageSize;
            height = maxVertulImageSize*bitmapHeight/bitmapWidth;
        }else{
            height = maxVertulImageSize;
            width = maxVertulImageSize*bitmapWidth/bitmapHeight;
        }
        int position = getCurrentPostion()+1;
        currentUploadFile.setNameDefault(getResources().getString(R.string.name_hint)+position);
        currentUploadFile.setWidthSuggest(width);
        currentUploadFile.setHeightSuggest(height);
        if(currentUploadFile.getCategory()==null){
            currentUploadFile.setCategory(categoryTypes.get(0));
        }
        Log.e("----------->","filePath = "+currentUploadFile.getFilePath());
        Log.e("----------->","width = "+currentUploadFile.getWidth());

        inputFixPictureInfoDialog = DialogUtil.showFixImport(PicturesUploadActivity.this,inputFixPictureInfoDialog,currentUploadFile,categoryTypes);
    }
    private void deleteFile() {
        int currentPositon = getCurrentPostion();
        if(currentPositon==-1){
            return;
        }
        mBitmap.recycle();
        mBitmap=null;
        if(allUploadFiles.size()==1){
            currentUploadFile=null;
            allUploadFiles.remove(currentPositon);
            iv_picture.setVisibility(View.INVISIBLE);
        }else{
            if(currentPositon<allUploadFiles.size()-1){
                allUploadFiles.remove(currentPositon);
                setCurrentUploadFile(allUploadFiles.get(currentPositon));
            }else{
                allUploadFiles.remove(currentPositon);
                setCurrentUploadFile(allUploadFiles.get(currentPositon-1));
            }
        }
        setPictures();
        resetStatus();
    }

    private void resetStatus(){
        switch (fix_type){
            case fix_type_auto:
                showAutoFixViews();
                break;
            case fix_type_manu:
                switch (fix_type_manu_option){
                    case fix_type_manu_jiaozhen:
                        showJiaozhengView();
                        break;
                    case fix_type_manu_cut:
                        showcaijianView();
                        break;
                }
        }
    }

    private void startAlbum() {
        if(num-allUploadFiles.size()==0){
            Toast.makeText(this, getResources().getString(R.string.you_can_only_select_up), Toast.LENGTH_SHORT).show();
            return;
        }
        if(mAlbumFiles!=null){
            mAlbumFiles.clear();
        }else{
            mAlbumFiles= new ArrayList<AlbumFile>();
        }
        Album.image(PicturesUploadActivity.this)
                .multipleChoice()
                .camera(true)
                .columnCount(2)
                .selectCount(num-allUploadFiles.size())
                .setTakePictureType(8)
                .checkedList(mAlbumFiles)
                .widget(
                        Widget.newDarkBuilder(PicturesUploadActivity.this)
                                .title(this.getResources().getString(com.yanzhenjie.album.R.string.album_album))
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
                                    addfiles();
                                }
                            });
                        }
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toast.makeText(PicturesUploadActivity.this, R.string.canceled, Toast.LENGTH_LONG).show();
                    }
                })
                .start();
    }

    public void saveButtonClick() {
//        if(allUploadFiles.size()<=0){
//            Toast.makeText(this,"请选择图片",Toast.LENGTH_SHORT).show();
//            return;
//        }
//        String name = et_name.getText().toString().trim();
//        String height = et_height.getText().toString().trim();
//        String width = et_width.getText().toString().trim();
//        if(name.equals("")){
//            et_name.requestFocus();
//            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//            imm.showSoftInput(et_name, InputMethodManager.SHOW_FORCED);
//            Toast.makeText(this,"请输入产品名称",Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if(height.equals("")){
//            Toast.makeText(this,"请输入产品高度",Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if(width.equals("")){
//            Toast.makeText(this,"请输入产品宽度",Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if(getSavedPicNum()==allUploadFiles.size()){
//            try {
//                JSONArray array = new JSONArray();
//                for(int i=0;i<allUploadFiles.size();i++){
//                    JSONObject json  =new JSONObject();
//                    json.put("name", allUploadFiles.get(i).getName());
//                    json.put("width",allUploadFiles.get(i).getWidth());
//                    json.put("height",allUploadFiles.get(i).getHeight());
//                    json.put("picture",allUploadFiles.get(i).getPicture());
//                    json.put("category",allUploadFiles.get(i).getCategory());
//                    json.put("category_id",allUploadFiles.get(i).getCategory_id());
//                    array.put(json);
//                }
//                UnityPlayer.UnitySendMessage("IOsReciveObj","GetMultipleAlbumPathFinish",array.toString());
//                finish();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }else{
//            if(!currentUploadFile.isSave()){
//                String pictureName = System.currentTimeMillis()+".jpg";
//                String path = dir+pictureName;
//                cutView.saveBitmap(path);
//                currentUploadFile.setSave(true);
//                currentUploadFile.setPicture(pictureName);
//                currentUploadFile.setName(name);
//                currentUploadFile.setHeight(height);
//                currentUploadFile.setWidth(width);
//                currentUploadFile.setCategory(type.getCategory());
//                currentUploadFile.setCategory_id(type.getCategory_id());
//                int currentPosition = getFilePosition(currentUploadFile);
//                ImageView iv_complete = (ImageView) ((FrameLayout)lienar_image.getChildAt(currentPosition)).getChildAt(1);
//                iv_complete.setVisibility(View.VISIBLE);
//                setSavedNumBer(true);
//            }
//        }
    }

    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

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

    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        BroadcastReceiverRegisterUtil.unRegisterReceiver(this, this.receiver);
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

    //调用自动修图
    private void autoFix() {
        if(allUploadFiles.size()==0){
            Toast.makeText(this,getResources().getString(R.string.please_select_an_image),Toast.LENGTH_SHORT).show();
            return;
        }
        isShowAutoHint = false;
        if (isAutoFixing) {
            return;
        }

        frame_auto_fix_hint.setVisibility(View.INVISIBLE);
        isAutoFixing = true;
        showAutoFixViewFixing();
        setAutoFixStartUI();
        String path = FileUtil.getCacheSaveImagesDir(PicturesUploadActivity.this) + FileUtil.getNewFileNameByTime() + ".jpg";
        OpenCVUtil.reSizePicture(mBitmap,path,2800);
        uploadToFastFix(path);
    }

    private void setAutoFixStartUI() {
        tv_import.setEnabled(false);
        tv_import.setBackground(getResources().getDrawable(R.drawable.shape_22));
        iv_rotate.setEnabled(false);
        tv_auto_deal.setEnabled(false);
        tv_manu_deal.setEnabled(false);
        tv_auto_fix_deal.setEnabled(false);
        tv_auto_fix_deal.setBackground(getResources().getDrawable(R.drawable.shape_22));
        v_return.setEnabled(false);

        iv_add.setEnabled(false);
        iv_picture_1.setEnabled(false);
        iv_picture_2.setEnabled(false);
        iv_picture_3.setEnabled(false);
        iv_picture_4.setEnabled(false);
        iv_picture_5.setEnabled(false);
        iv_picture_6.setEnabled(false);
        v_size.setEnabled(false);
        v_delete.setEnabled(false);
        iv_size.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_edit_pictures_size_un,false));
        iv_delete.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_edit_pictures_delete_un,false));
        tv_size.setTextColor(getResources().getColor(R.color.color393939));
        tv_delete.setTextColor(getResources().getColor(R.color.color393939));

    }

    private void startJiaoZhengUI(){
        tv_import.setEnabled(false);
        tv_import.setBackground(getResources().getDrawable(R.drawable.shape_22));
        iv_rotate.setEnabled(false);
        v_size.setEnabled(false);
        v_delete.setEnabled(false);
        iv_size.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_edit_pictures_size_un,false));
        iv_delete.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_edit_pictures_delete_un,false));
        tv_size.setTextColor(getResources().getColor(R.color.color393939));
        tv_delete.setTextColor(getResources().getColor(R.color.color393939));
    }
    private void endJiaoZhengUI(){
        tv_import.setEnabled(true);
        tv_import.setBackground(getResources().getDrawable(R.drawable.shape_19));
        iv_rotate.setEnabled(true);
        v_size.setEnabled(true);
        v_delete.setEnabled(true);
        iv_size.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_edit_pictures_size,false));
        iv_delete.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_edit_pictures_delete,false));
        tv_size.setTextColor(getResources().getColor(R.color.colorFFFFFF));
        tv_delete.setTextColor(getResources().getColor(R.color.colorFFFFFF));
    }
    private void setAutoFixEndUI() {
        tv_import.setEnabled(true);
        tv_import.setBackground(getResources().getDrawable(R.drawable.shape_19));
        iv_rotate.setEnabled(true);
        tv_auto_deal.setEnabled(true);
        tv_manu_deal.setEnabled(true);
        tv_auto_fix_deal.setEnabled(true);
        tv_auto_fix_deal.setBackground(getResources().getDrawable(R.drawable.shape_19));
        v_return.setEnabled(true);
        iv_add.setEnabled(true);
        iv_picture_1.setEnabled(true);
        iv_picture_2.setEnabled(true);
        iv_picture_3.setEnabled(true);
        iv_picture_4.setEnabled(true);
        iv_picture_5.setEnabled(true);
        iv_picture_6.setEnabled(true);
        v_size.setEnabled(true);
        v_delete.setEnabled(true);
        iv_size.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_edit_pictures_size,false));
        iv_delete.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_edit_pictures_delete,false));
        tv_size.setTextColor(getResources().getColor(R.color.colorFFFFFF));
        tv_delete.setTextColor(getResources().getColor(R.color.colorFFFFFF));
    }

    //自动修图
    private void uploadToFastFix(String filePath) {
        File useFile = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("file/*"), useFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", useFile.getName(), requestFile);
        Map<String, RequestBody> params = new HashMap<>();
        params.put("data", convertToRequestBody("{\"name\":\"SQSM\"}"));
        RxRetrofit.request(RxRetrofit.create(new MapUtils()
                .builder(), RxRequest.class).fastFix(params, body), new RxObserver<BaseBean<FixBean>>() {
            @Override
            public void onDisposable(Disposable d) {

            }

            @Override
            public void onSuccess(BaseBean<FixBean> o) {
                Log.e("----------->", "success");
//                DialogUtil.dismissDialogFragment(loadingDialogFragment);
                if (o.getErrorCode() == 0) {
                    String newPath = FileUtil.getCacheSaveImagesDir(PicturesUploadActivity.this) + FileUtil.getNewFileNameByTime() + ".jpg";
                    OkHttpUtil.downLoadFile(o.getData().getDownload_url(), newPath, new OkHttpUtil.DownloadCallBack() {
                        @Override
                        public void success(final String filePath) {
                            Log.e("------------->", "filePath = " + filePath);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    isAutoFixing = false;
                                    File file = new File(filePath);
                                    Log.e("------------->", "file.exists() = " + file.exists());
                                    if (file.exists()) {
                                        Log.e("------------->", "file.length() = " + file.length());
                                    }
                                    bitmapAutoFix = FileUtil.decodeFile(filePath, ConstantData.maxDecodeImageWidth);
                                    iv_picture.setImageBitmap(bitmapAutoFix);
                                    setListCurrentPictureBitmap(bitmapAutoFix);
                                    showAutoFixViewFixSuccess();
                                }
                            });

                        }

                        @Override
                        public void failed() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showAutoFixViewFail();
                                    setAutoFixEndUI();
                                    isAutoFixing = false;
                                }
                            });
                        }
                    });
                } else {
                    showAutoFixViewFail();
                    setAutoFixEndUI();
                    isAutoFixing = false;
                }
            }

            @Override
            public void onFailure() {
                Log.e("----------->", "onFailure");
                showAutoFixViewFail();
                setAutoFixEndUI();
                isAutoFixing = false;
            }
        });
    }

    private RequestBody convertToRequestBody(String param) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), param);
        return requestBody;
    }

    private void selectFixType(int fix_type) {
        if (this.fix_type == fix_type) {
            return;
        }
        if (fix_type == fix_type_auto) {
            this.fix_type = fix_type;
            quitCurrentManuOptions();
            showFixTypeAuto();
        } else if (fix_type == fix_type_manu) {
            hideAutoFixViews();
            this.fix_type = fix_type;
            showFixTypeManu();
        }
    }

    private void showFixTypeAuto() {
        changeFixTypeUI();
        showAutoFixViews();
        if(isShowAutoHint){
            frame_auto_fix_hint.setVisibility(View.VISIBLE);
        }
        isAutoFixing = false;
        frame_auto_fix_options.setVisibility(View.VISIBLE);
        frame_manu_options.setVisibility(View.INVISIBLE);
        frame_manu_options_type.setVisibility(View.INVISIBLE);
    }

    private void showFixTypeManu() {
        changeFixTypeUI();
        fix_type_manu_option = fix_type_manu_jiaozhen;
        frame_auto_fix_options.setVisibility(View.INVISIBLE);
        frame_manu_options.setVisibility(View.VISIBLE);
        frame_manu_options_type.setVisibility(View.VISIBLE);
        frame_auto_fix_hint.setVisibility(View.INVISIBLE);
        frame_auto_and_manu.setBackgroundColor(getResources().getColor(R.color.color393939));

        quitCurrentAutoOptions();
        showManuType(fix_type_manu_option, true);
    }


    private void showManuType(int manu_option, boolean isAutoToManu) {
        endJiaoZhengUI();
        //底部操作项目
        showManuBottomOption(manu_option);
        //按键切换
        changeManuUI(manu_option);
        if (!isAutoToManu) {
            quitCurrentManuOptions();
        }
        //中间操作项目
        switch (manu_option) {
            case fix_type_manu_jiaozhen:
                showJiaozhengView();
                break;
            case fix_type_manu_cut:
                showcaijianView();
                break;
        }
        fix_type_manu_option = manu_option;
    }

    private void showManuBottomOption(int manu_option) {
        if (manu_option == fix_type_manu_jiaozhen) {
            frame_manu_option_jiaozhen.setVisibility(View.VISIBLE);
            tv_jiaozheng_hint.setText(getResources().getString(R.string.jiaozheng_hint_1));

        } else {
            frame_manu_option_jiaozhen.setVisibility(View.INVISIBLE);
        }
        if (manu_option == fix_type_manu_cut) {
            frame_manu_option_cut.setVisibility(View.VISIBLE);
            tv_caijian_hint.setText(getResources().getString(R.string.caijian_hint_1));
        } else {
            frame_manu_option_cut.setVisibility(View.INVISIBLE);
        }
        if (manu_option == fix_type_manu_tiaose) {
            frame_manu_option_tiaose.setVisibility(View.VISIBLE);
        } else {
            frame_manu_option_tiaose.setVisibility(View.INVISIBLE);
        }
        if (manu_option == fix_type_manu_tumo) {
            frame_manu_option_tumo.setVisibility(View.VISIBLE);
        } else {
            frame_manu_option_tumo.setVisibility(View.INVISIBLE);
        }
        if (manu_option == fix_type_manu_yingyangse) {
            frame_manu_option_yinyangse.setVisibility(View.VISIBLE);
        } else {
            frame_manu_option_yinyangse.setVisibility(View.INVISIBLE);
        }
    }

    private void changeFixTypeUI() {
        if (fix_type == fix_type_auto) {
            tv_auto_deal.setTextColor(getResources().getColor(R.color.colorFFFFFF));
            v_auto_line.setBackgroundColor(getResources().getColor(R.color.colorFFFFFF));
            tv_manu_deal.setTextColor(getResources().getColor(R.color.colorB3B3B3));
            v_auto_line.setVisibility(View.VISIBLE);
            v_manu_line.setVisibility(View.INVISIBLE);
        } else {
            tv_auto_deal.setTextColor(getResources().getColor(R.color.colorB3B3B3));
            tv_manu_deal.setTextColor(getResources().getColor(R.color.colorFFFFFF));
            v_manu_line.setBackgroundColor(getResources().getColor(R.color.colorFFFFFF));
            v_auto_line.setVisibility(View.INVISIBLE);
            v_manu_line.setVisibility(View.VISIBLE);
        }

    }

    //广播调用上传拍照
    public void startAlbumCapture(int type) {
        Intent intent = new Intent(PicturesUploadActivity.this, AlbumCaptureActivity.class);
        intent.putExtra("takePictureType", type);
        startActivity(intent);
    }

    //广播调用上传拍照结束
    public void albumCaptureFinish(Intent intent) {
        String fileSavePath = intent.getStringExtra("fileSavePath");
        String dir = FileUtil.getCacheSaveImagesDir(PicturesUploadActivity.this);
        //保存到内部缓存
        String fileName = FileUtil.getNewFileNameByTime()+".jpg";
        String filePath = dir+fileName;
        Uri uri = FileUtil.fileToUri(this,fileSavePath);
        OpenCVUtil.reSaveFile(PicturesUploadActivity.this,fileSavePath,uri.toString(),filePath,2300);
        UploadFile file = new UploadFile();
        file.setFilePath(filePath);
        allUploadFiles.add(file);
        if(currentUploadFile==null){
            setCurrentUploadFile(allUploadFiles.get(0));
        }
        setPictures();
    }

    //广播调用相册
    public void albumCaptureToAlbum() {
        startAlbum();
    }

    private void doImport(){
        if(allUploadFiles.size()==0){
            Toast.makeText(this,getResources().getString(R.string.please_select_an_image),Toast.LENGTH_SHORT).show();
            return;
        }
        //将布片保存到指定路路径
        try {
            JSONArray array = new JSONArray();
            for(int i=0;i<allUploadFiles.size();i++){
                String pictureName = i+".jpg";
                String picturePath= saveDir+pictureName;
                FileUtil.copyFileToFile(this,allUploadFiles.get(i).getFilePath(),picturePath,true,false);
                JSONObject json  =new JSONObject();
                String name;
                int width;
                int height;

                if(allUploadFiles.get(i).getName()==null||allUploadFiles.equals("")){
                    name = getResources().getString(R.string.name_hint)+"0"+(1+i);
                }else{
                    name = allUploadFiles.get(i).getName();
                }
                if(allUploadFiles.get(i).getWidthSuggest()==0){
                    int[] params = ImageDealUtil.decodeUriWidthAndHeight(this,FileUtil.fileToUri(this,picturePath));
                    allUploadFiles.get(i).setWidthSuggest(params[0]);
                    allUploadFiles.get(i).setHeightSuggest(params[1]);
                }
                if(allUploadFiles.get(i).getWidth()==0){
                    width = allUploadFiles.get(i).getWidthSuggest();
                }else{
                    width = allUploadFiles.get(i).getWidth();
                }
                if(allUploadFiles.get(i).getHeight()==0){
                    height = allUploadFiles.get(i).getHeightSuggest();
                }else{
                    height = allUploadFiles.get(i).getHeight();
                }
                if(allUploadFiles.get(i).getCategory()==null){
                    allUploadFiles.get(i).setCategory(categoryTypes.get(0));
                }
                json.put("name", name);
                json.put("width",width);
                json.put("height",height);
                json.put("picture",pictureName);
                json.put("category",allUploadFiles.get(i).getCategory().getCategory());
                json.put("category_id",allUploadFiles.get(i).getCategory().getCategory_id());
                array.put(json);
            }
            NativeCallUnity.GetMultipleAlbumPathFinish(array.toString());
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

