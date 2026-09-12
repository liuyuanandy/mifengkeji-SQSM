package com.beeinc.SQSB.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.beeinc.SQSB.R;
import com.beeinc.SQSB.bean.CategoryType;
import com.beeinc.SQSB.bean.UploadFile;
import com.beeinc.SQSB.receiver.BeeIncReceiver;
import com.beeinc.SQSB.scale.Layout;
import com.beeinc.SQSB.scale.ScreenUtil;
import com.beeinc.SQSB.util.ImageDealUtil;
import com.beeinc.SQSB.util.NativeCallUnity;
import com.beeinc.SQSB.views.CutViewForUpload;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;

public class UploadActivity extends Activity {

    private View v_bottom_back,v_rotate;
    private CutViewForUpload cutView;
    private ImageView iv_rotate,iv_delete,iv_down_sward;
    private TextView tv_hint_title,tv_hint_desc,tv_cancel,tv_rotate,tv_preview,tv_params_title,tv_name_hint,tv_type_hint,tv_type,tv_height,
            tv_height_unit,tv_width,tv_width_unit,tv_save;
    private EditText et_name,et_height,et_width;
    private ScrollView scroll_bottom;
    private FrameLayout frame_params,frame_name,frame_type,frame_height,frame_width;
    private LinearLayout lienar_image;
    private ArrayList<UploadFile> allUploadFiles = new ArrayList<UploadFile>();
    private ArrayList<CategoryType> categoryTypes;
    private ArrayList<AlbumFile> mAlbumFiles;
    private int num;
    private CategoryType type;//
    private PopupWindow p;

    private boolean isDestroy = false;
    private Handler handler;
    private String dir;

    private BeeIncReceiver receiver;
    private UploadFile currentUploadFile;//当前操作对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_cut);
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        ScreenUtil.setLayoutNum(this);
        hideNavigatonButton();
        isDestroy = false;
        ArrayList<UploadFile> uploadFiles = (ArrayList<UploadFile>) this.getIntent().getSerializableExtra("fileSelected");
        categoryTypes = (ArrayList<CategoryType>) getIntent().getSerializableExtra("categoryTypes");
        num = getIntent().getIntExtra("num",6);
        String path = getIntent().getStringExtra("fileDir");
        if(path.endsWith(File.separator)){
            dir = path;
        }else{
            dir = path+File.separator;
        }
        init();
        initUploadFiles(uploadFiles);
    }
    private void init(){
        getAllViews();
        setParams();
        setListener();
        initReceiver();
    }
    private void getAllViews(){
        v_bottom_back = findViewById(R.id.v_bottom_back);
        v_rotate = findViewById(R.id.v_rotate);
        cutView = findViewById(R.id.cutView);
        iv_rotate = findViewById(R.id.iv_rotate);
        iv_delete = findViewById(R.id.iv_delete);
        iv_down_sward = findViewById(R.id.iv_down_sward);
        tv_hint_title = findViewById(R.id.tv_hint_title);
        tv_hint_desc = findViewById(R.id.tv_hint_desc);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_rotate = findViewById(R.id.tv_rotate);
        tv_preview = findViewById(R.id.tv_preview);
        tv_params_title = findViewById(R.id.tv_params_title);
        tv_name_hint = findViewById(R.id.tv_name_hint);
        tv_type_hint = findViewById(R.id.tv_type_hint);
        tv_type = findViewById(R.id.tv_type);
        tv_height = findViewById(R.id.tv_height);
        tv_height_unit = findViewById(R.id.tv_height_unit);
        tv_width = findViewById(R.id.tv_width);
        tv_width_unit = findViewById(R.id.tv_width_unit);
        tv_save = findViewById(R.id.tv_save);
        et_name = findViewById(R.id.et_name);
        et_height = findViewById(R.id.et_height);
        et_width = findViewById(R.id.et_width);
        scroll_bottom = findViewById(R.id.scroll_bottom);
        frame_params = findViewById(R.id.frame_params);
        frame_name = findViewById(R.id.frame_name);
        frame_type = findViewById(R.id.frame_type);
        frame_height = findViewById(R.id.frame_height);
        frame_width = findViewById(R.id.frame_width);
        lienar_image = findViewById(R.id.lienar_image);
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

        FrameLayout.LayoutParams p_bottom_back= new FrameLayout.LayoutParams(ScreenUtil.SCREEN_THIS_W- Layout.getScale(296), Layout.getScale(46));
        p_bottom_back.setMargins(0,0 , 0, Layout.getScale(118));
        p_bottom_back.gravity = Gravity.BOTTOM;
        v_bottom_back.setLayoutParams(p_bottom_back);

        FrameLayout.LayoutParams p_v_rotate= new FrameLayout.LayoutParams(Layout.getScale(95), Layout.getScale(46));
        v_rotate.setLayoutParams(p_v_rotate);

        FrameLayout.LayoutParams p_iv_rotate= new FrameLayout.LayoutParams(Layout.getScale(20), Layout.getScale(20));
        p_iv_rotate.setMargins(Layout.getScale(20), Layout.getScale(13),0 , 0 );
        iv_rotate.setLayoutParams(p_iv_rotate);
        iv_rotate.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_rotate, true));

        FrameLayout.LayoutParams p_tv_rotate= new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, Layout.getScale(46));
        p_tv_rotate.setMargins(Layout.getScale(50),0,0 , 0 );
        tv_rotate.setLayoutParams(p_tv_rotate);
        Layout.setTextViewSize(tv_rotate, 15);

        FrameLayout.LayoutParams p_tv_preview= new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, Layout.getScale(46));
        p_tv_preview.gravity = Gravity.CENTER_HORIZONTAL;
        tv_preview.setLayoutParams(p_tv_preview);
        Layout.setTextViewSize(tv_preview, 15);

        FrameLayout.LayoutParams p_scroll = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Layout.getScale(108));
        p_scroll.gravity = Gravity.BOTTOM;
        scroll_bottom.setLayoutParams(p_scroll);
        FrameLayout.LayoutParams p_params = new FrameLayout.LayoutParams(Layout.getScale(296),FrameLayout.LayoutParams.WRAP_CONTENT);
        p_params.gravity=Gravity.CENTER_VERTICAL|Gravity.RIGHT;
        frame_params.setLayoutParams(p_params);

        FrameLayout.LayoutParams p_param_title = new FrameLayout.LayoutParams(Layout.getScale(296), Layout.getScale(36));
        tv_params_title.setLayoutParams(p_param_title);
        Layout.setTextViewSize(tv_params_title,15);

        FrameLayout.LayoutParams p_frame_name = new FrameLayout.LayoutParams(Layout.getScale(236), Layout.getScale(34));
        p_frame_name.setMargins(0, Layout.getScale(42), 0, 0);
        p_frame_name.gravity = Gravity.CENTER_HORIZONTAL;
        frame_name.setLayoutParams(p_frame_name);
        FrameLayout.LayoutParams p_name_hint = new FrameLayout.LayoutParams(Layout.getScale(70), Layout.getScale(34));
        p_name_hint.setMargins(Layout.getScale(14), 0, 0, 0);
        p_name_hint.gravity = Gravity.CENTER_VERTICAL;
        tv_name_hint.setLayoutParams(p_name_hint);
        Layout.setTextViewSize(tv_name_hint, 14);

        FrameLayout.LayoutParams p_name = new FrameLayout.LayoutParams(Layout.getScale(120), Layout.getScale(34));
        p_name.setMargins(Layout.getScale(86), 0, 0, 0);
        p_name.gravity = Gravity.CENTER_VERTICAL;
        et_name.setLayoutParams(p_name);
        Layout.setEditTextSize(et_name, 14);

        FrameLayout.LayoutParams p_delete = new FrameLayout.LayoutParams(Layout.getScale(18), Layout.getScale(18));
        p_delete.setMargins(Layout.getScale(210), 0, 0, 0);
        p_delete.gravity = Gravity.CENTER_VERTICAL;
        iv_delete.setLayoutParams(p_delete);

        FrameLayout.LayoutParams p_frame_type = new FrameLayout.LayoutParams(Layout.getScale(236), Layout.getScale(34));
        p_frame_type.setMargins(0, Layout.getScale(85), 0, 0);
        p_frame_type.gravity = Gravity.CENTER_HORIZONTAL;
        frame_type.setLayoutParams(p_frame_type);

        FrameLayout.LayoutParams p_type_hint = new FrameLayout.LayoutParams(Layout.getScale(70), Layout.getScale(34));
        p_type_hint.setMargins(Layout.getScale(14), 0, 0, 0);
        p_type_hint.gravity = Gravity.CENTER_VERTICAL;
        tv_type_hint.setLayoutParams(p_type_hint);
        Layout.setTextViewSize(tv_type_hint, 14);

        FrameLayout.LayoutParams p_type = new FrameLayout.LayoutParams(Layout.getScale(120), Layout.getScale(34));
        p_type.setMargins(Layout.getScale(86), 0, 0, 0);
        p_type.gravity = Gravity.CENTER_VERTICAL;
        tv_type.setLayoutParams(p_type);
        Layout.setTextViewSize(tv_type, 14);

        FrameLayout.LayoutParams p_down = new FrameLayout.LayoutParams(Layout.getScale(16), Layout.getScale(16));
        p_down.setMargins(Layout.getScale(212), 0, 0, 0);
        p_down.gravity = Gravity.CENTER_VERTICAL;
        iv_down_sward.setLayoutParams(p_down);

        FrameLayout.LayoutParams p_frame_width = new FrameLayout.LayoutParams(Layout.getScale(236), Layout.getScale(34));
        p_frame_width.setMargins(0, Layout.getScale(128), 0, 0);
        p_frame_width.gravity = Gravity.CENTER_HORIZONTAL;
        frame_width.setLayoutParams(p_frame_width);

        FrameLayout.LayoutParams p_width_hint = new FrameLayout.LayoutParams(Layout.getScale(70), Layout.getScale(34));
        p_width_hint.setMargins(Layout.getScale(14), 0, 0, 0);
        p_width_hint.gravity = Gravity.CENTER_VERTICAL;
        tv_width.setLayoutParams(p_width_hint);
        Layout.setTextViewSize(tv_width, 14);

        FrameLayout.LayoutParams p_width = new FrameLayout.LayoutParams(Layout.getScale(120), Layout.getScale(34));
        p_width.setMargins(Layout.getScale(86), 0, 0, 0);
        p_width.gravity = Gravity.CENTER_VERTICAL;
        et_width.setLayoutParams(p_width);
        Layout.setEditTextSize(et_width, 14);

        FrameLayout.LayoutParams p_width_unit = new FrameLayout.LayoutParams(Layout.getScale(70), Layout.getScale(34));
        p_width_unit.setMargins(Layout.getScale(160), 0, 0, 0);
        p_width_unit.gravity = Gravity.CENTER_VERTICAL;
        tv_width_unit.setLayoutParams(p_width_unit);
        Layout.setTextViewSize(tv_width_unit, 14);

        FrameLayout.LayoutParams p_frame_height = new FrameLayout.LayoutParams(Layout.getScale(236), Layout.getScale(34));
        p_frame_height.setMargins(0, Layout.getScale(171), 0, 0);
        p_frame_height.gravity = Gravity.CENTER_HORIZONTAL;
        frame_height.setLayoutParams(p_frame_height);

        FrameLayout.LayoutParams p_height_hint = new FrameLayout.LayoutParams(Layout.getScale(70), Layout.getScale(34));
        p_height_hint.setMargins(Layout.getScale(14), 0, 0, 0);
        p_height_hint.gravity = Gravity.CENTER_VERTICAL;
        tv_height.setLayoutParams(p_height_hint);
        Layout.setTextViewSize(tv_height, 14);

        FrameLayout.LayoutParams p_height = new FrameLayout.LayoutParams(Layout.getScale(120), Layout.getScale(34));
        p_height.setMargins(Layout.getScale(86), 0, 0, 0);
        p_height.gravity = Gravity.CENTER_VERTICAL;
        et_height.setLayoutParams(p_height);
        Layout.setEditTextSize(et_height, 14);

        FrameLayout.LayoutParams p_height_unit = new FrameLayout.LayoutParams(Layout.getScale(70), Layout.getScale(34));
        p_height_unit.setMargins(Layout.getScale(160), 0, 0, 0);
        p_height_unit.gravity = Gravity.CENTER_VERTICAL;
        tv_height_unit.setLayoutParams(p_height_unit);
        Layout.setTextViewSize(tv_height_unit, 14);

        FrameLayout.LayoutParams p_save = new FrameLayout.LayoutParams(Layout.getScale(196), Layout.getScale(50));
        p_save.setMargins(Layout.getScale(50), Layout.getScale(300), 0, 0);
        tv_save.setLayoutParams(p_save);
        Layout.setTextViewSize(tv_save,15);
        type = getDefaultType();
        tv_type.setText(type.getCategory());
        et_name.setText("");
    }
    private CategoryType getDefaultType(){
        int defaultPosition = 0;
        for(int i=0;i<categoryTypes.size();i++){
            if(categoryTypes.get(i).getSort()==0){
                defaultPosition = i;
                break;
            }
        }
        return categoryTypes.get(defaultPosition);
    }
    private void setListener(){
        cutView.setListener(new CutViewForUpload.CutViewListener() {
            @Override
            public void setDefault() {
                setPreviewDefault();
                if(currentUploadFile!=null&&currentUploadFile.isSave()){
                    currentUploadFile.setSave(false);
                    int currentPoisiton = getFilePosition(currentUploadFile);
                    ((FrameLayout)lienar_image.getChildAt(currentPoisiton)).getChildAt(1).setVisibility(View.INVISIBLE);
                    setSavedNumBer(false);
                }
            }

            @Override
            public void setPreView() {
                tv_preview.setVisibility(View.VISIBLE);
                tv_preview.setText("预览");
                currentUploadFile.setSave(false);
                setSavedNumBer(false);
            }

            @Override
            public void setJiaozhengAgin() {
                setJiaoZhenAgain();
            }

            @Override
            public void setWidthAndHeight(int width, int height) {
                et_width.setText(width+"");
                et_height.setText(height+"");
            }
        });
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()== R.id.tv_cancel){
                    finish();
                }
                if(v.getId()== R.id.v_rotate){
                    if(currentUploadFile!=null){
                        currentUploadFile.setSave(false);
                        setSavedNumBer(false);
                        int currentPosition = getFilePosition(currentUploadFile);
                        ((FrameLayout)lienar_image.getChildAt(currentPosition)).getChildAt(1).setVisibility(View.INVISIBLE);
                    }
                    cutView.rotate();

                }
                if(v.getId()== R.id.tv_preview){
                    cutView.preViewClick();
                }
                if(v.getId()== R.id.tv_save){
                    saveButtonClick();
                }
                if(v.getId()== R.id.frame_type){
                    startPopupWindow();
                }
                if(v.getId()== R.id.iv_delete){
                    et_name.setText("");
                }
            }
        };
        tv_cancel.setOnClickListener(listener);
        v_rotate.setOnClickListener(listener);
        tv_preview.setOnClickListener(listener);
        tv_save.setOnClickListener(listener);
        frame_type.setOnClickListener(listener);
        iv_delete.setOnClickListener(listener);
    }
    private void initReceiver(){
        receiver =new BeeIncReceiver();
        IntentFilter filter= new IntentFilter();
        filter.addAction("com.beeinc.album.takpicture");
        filter.addAction("com.beeinc.album.takpicture.finish");
        filter.addAction("com.beeinc.album.takpicture.album");
        registerReceiver(receiver,filter);
    }
    private void initUploadFiles(ArrayList<UploadFile> uploadFiles){
        addViews(uploadFiles,true);
    }
    /**
     * @param uploadFiles 文件列表
     * @param isFirst 是否是第一次添加
     */
    private void addViews(ArrayList<UploadFile> uploadFiles, boolean isFirst){
//        boolean isNeedShowPicture = false;
//        if(isFirst){
//            lienar_image.removeAllViews();
//            currentUploadFile = uploadFiles.get(0);
//            isNeedShowPicture = true;
//        }else{
//            if(allUploadFiles.size()==0){
//                lienar_image.removeAllViews();
//                currentUploadFile = uploadFiles.get(0);
//                isNeedShowPicture = true;
//            }else{
//                lienar_image.removeViewAt(allUploadFiles.size());
//            }
//        }
//        for(int i=0;i<uploadFiles.size();i++){
//            FrameLayout layout = new FrameLayout(this);
//            LinearLayout.LayoutParams p_layout = new LinearLayout.LayoutParams(Layout.getScale(100), Layout.getScale(108));
//            layout.setLayoutParams(p_layout);
//            ImageView iv_file = new ImageView(this);
//            FrameLayout.LayoutParams p_img = new FrameLayout.LayoutParams(Layout.getScale(80), Layout.getScale(80));
//            p_img.setMargins(Layout.getScale(10), Layout.getScale(14),0,0);
//            iv_file.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            iv_file.setBackgroundColor(Color.parseColor("#ffffff"));
//            iv_file.setLayoutParams(p_img);
//            iv_file.setTag(uploadFiles.get(i));
//            iv_file.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    UploadFile uploadFile  =(UploadFile) v.getTag();
//                    changeImagePosition(uploadFile);
//                }
//            });
//            layout.addView(iv_file);
//            Bitmap bitmap = OpenCVUtil.getBitmap(!uploadFiles.get(i).isChooseAlbum(),this,uploadFiles.get(i).getFilePath(),uploadFiles.get(i).getUriContentPath(), 200);
//            iv_file.setImageBitmap(bitmap);
//            ImageView iv_complete = new ImageView(this);
//            iv_complete.setLayoutParams(p_img);
//            if(uploadFiles.get(i).isSave()){
//                iv_complete.setVisibility(View.VISIBLE);
//            }else{
//                iv_complete.setVisibility(View.INVISIBLE);
//            }
//            iv_complete.setScaleType(ImageView.ScaleType.FIT_XY);
//            iv_complete.setImageBitmap(ImageDealUtil.readBitMap(this, R.mipmap.icon_selected_wancheng,true));
//            layout.addView(iv_complete);
//            View borderView = new View(this);
//            borderView.setLayoutParams(p_img);
//            borderView.setBackgroundResource(R.drawable.shape_8);
//            borderView.setVisibility(View.INVISIBLE);
//            layout.addView(borderView);
//            ImageView iv_delete = new ImageView(this);
//            FrameLayout.LayoutParams p_delete = new FrameLayout.LayoutParams(Layout.getScale(20), Layout.getScale(20));
//            p_delete.setMargins(Layout.getScale(80), Layout.getScale(4),0,0);
//            iv_delete.setScaleType(ImageView.ScaleType.FIT_XY);
//            iv_delete.setTag(uploadFiles.get(i));
//            iv_delete.setImageResource(R.mipmap.icon_delete);
//            iv_delete.setLayoutParams(p_delete);
//            iv_delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    UploadFile file = (UploadFile)v.getTag();
//                    deleteFile(file);
//                }
//            });
//            layout.addView(iv_delete);
//            lienar_image.addView(layout);
//        }
//        allUploadFiles.addAll(uploadFiles);
//        if(uploadFiles.size()<num){
//            //添加添加按钮
//            addAddButton();
//        }
//        if(isNeedShowPicture){
//            showPic();
//        }
//        setSavedNumBer(false);
    }
    private void changeImagePosition(UploadFile uploadFile){
        if(uploadFile==currentUploadFile){
            return;
        }else{
            currentUploadFile = uploadFile;
            showPic();
        }
    }
    private void showSelectPosition(){
        if(allUploadFiles.size()<=0){
            return;
        }
        int position = getFilePosition(currentUploadFile);
        for(int i=0;i<allUploadFiles.size();i++){
            if(position==i){
                ((FrameLayout)lienar_image.getChildAt(i)).getChildAt(2).setVisibility(View.VISIBLE);
            }else{
                ((FrameLayout)lienar_image.getChildAt(i)).getChildAt(2).setVisibility(View.INVISIBLE);
            }
        }
    }
    private void setPreviewDefault(){
        tv_preview.setVisibility(View.INVISIBLE);
        tv_preview.setText("预览");
    }
    private void showPic(){
//        if(currentUploadFile.isSave()){
//            Bitmap bitmap = OpenCVUtil.getBitmap(!currentUploadFile.isChooseAlbum(),this,currentUploadFile.getFilePath(),currentUploadFile.getUriContentPath(),ConstantData.maxDecodeImageWidth);
//            Bitmap preViewBitmaip = ImageDealUtil.decodeFile(dir+currentUploadFile.getPicture(),1200);
//            cutView.setBitmap(bitmap,preViewBitmaip,0, Layout.getScale(78), Layout.getScale(296), Layout.getScale(165));
//            setJiaoZhenAgain();
//        }else{
//            Bitmap bitmap = OpenCVUtil.getBitmap(!currentUploadFile.isChooseAlbum(),this,currentUploadFile.getFilePath(),currentUploadFile.getUriContentPath(),ConstantData.maxDecodeImageWidth);
//            cutView.setBitmap(bitmap,0, Layout.getScale(78), Layout.getScale(296), Layout.getScale(165));
//            setPreviewDefault();
//        }
//        showSelectPosition();
    }
    public void setJiaoZhenAgain(){
        tv_preview.setVisibility(View.VISIBLE);
        tv_preview.setText("再次矫正");
    }
    //选择图片后
    private void addfiles(){
//        ArrayList<UploadFile> uploadFiles = new ArrayList<>();
//        for(int i=0;i<mAlbumFiles.size();i++) {
//            UploadFile uploadFile = new UploadFile();
//            uploadFile.setFilePath(mAlbumFiles.get(i).getPath());
//            uploadFile.setUriContentPath(mAlbumFiles.get(i).getUriContentPath());
//            uploadFile.setChooseAlbum(true);
//            uploadFiles.add(uploadFile);
//        }
//        addViews(uploadFiles,false);
//        setSavedNumBer(false);
    }
    /**
     * @param isSavedCurrrent 是否是当前图片保存结束后的操作
     */
    private void  setSavedNumBer(boolean isSavedCurrrent){
        int savedPictureNum = getSavedPicNum();
        if(getSavedPicNum()== allUploadFiles.size()){
            tv_save.setText("确认添加  ("+savedPictureNum+"/"+allUploadFiles.size()+")  >>");
        }else{
            tv_save.setText("保存并继续  ("+(savedPictureNum+1)+"/"+allUploadFiles.size()+")  >>");
            if(isSavedCurrrent){
                boolean isFindUnsaved = false;
                int currentPosition = getFilePosition(currentUploadFile);
                for(int i=0;i<allUploadFiles.size()-(currentPosition+1);i++){
                    if(!allUploadFiles.get(i+currentPosition+1).isSave()){
                        currentUploadFile=allUploadFiles.get(i+currentPosition+1);
                        showPic();
                        isFindUnsaved = true;
                        break;
                    }
                }
                if(!isFindUnsaved){
                    for(int i=0;i<currentPosition;i++){
                        if(!allUploadFiles.get(i).isSave()){
                            currentUploadFile=allUploadFiles.get(i);
                            showPic();
                            isFindUnsaved = true;
                            break;
                        }
                    }
                }
            }
        }
    }
    private int getSavedPicNum(){
        int savedPictureNum = 0;
        for(int i=0;i<allUploadFiles.size();i++){
            if(allUploadFiles.get(i).isSave()){
                savedPictureNum=savedPictureNum+1;
            }
        }
        return savedPictureNum;
    }
    private int getFilePosition(UploadFile uploadFile){
        int position = 0;
        for(int i=0;i<allUploadFiles.size();i++){
            if(allUploadFiles.get(i)==uploadFile){
                position = i;
                break;
            }
        }
        return position;
    }
    private void deleteFile(UploadFile uploadFile){
        //获取删除的位置
        int position=0;
        for(int i=0;i<allUploadFiles.size();i++){
            if(allUploadFiles.get(i)==uploadFile) {
                position = i;
            }
        }
        if(allUploadFiles.size()==6){//添加添加按钮
            addAddButton();
        }
        //执行删除
        allUploadFiles.remove(uploadFile);
        lienar_image.removeViewAt(position);
        setSavedNumBer(false);
        //设置图片的显示
        if(allUploadFiles.size()==0){
            currentUploadFile=null;
            cutView.setShowNone();//不显示图片
        }else{
            if(currentUploadFile==uploadFile){
                if(position+1<allUploadFiles.size()){//显示下一张图 即position 位置的图
                    currentUploadFile=allUploadFiles.get(position);
                    showPic();
                }else{
                    if(position==0){
                        currentUploadFile=allUploadFiles.get(0);
                        showPic();
                    }else{
                        currentUploadFile=allUploadFiles.get(position-1);
                        showPic();
                    }
                }
            }
        }
    }
    private void addAddButton(){
        //添加添加按钮
        FrameLayout layout = new FrameLayout(this);
        LinearLayout.LayoutParams p_layout = new LinearLayout.LayoutParams(Layout.getScale(100), Layout.getScale(108));
        layout.setLayoutParams(p_layout);
        ImageView iv_file = new ImageView(this);
        FrameLayout.LayoutParams p_img = new FrameLayout.LayoutParams(Layout.getScale(80), Layout.getScale(80));
        p_img.setMargins(Layout.getScale(10), Layout.getScale(14),0,0);
        iv_file.setScaleType(ImageView.ScaleType.FIT_XY);
        iv_file.setLayoutParams(p_img);
        iv_file.setImageResource(R.mipmap.icon_upload_add);
        layout.addView(iv_file);
        iv_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAlbum();
            }
        });
        lienar_image.addView(layout);
    }
    private void startAlbum(){
        if(mAlbumFiles!=null){
            mAlbumFiles.clear();
        }else{
            mAlbumFiles= new ArrayList<AlbumFile>();
        }
        Album.image(UploadActivity.this)
                .multipleChoice()
                .camera(true)
                .columnCount(2)
                .selectCount(num-allUploadFiles.size())
                .setTakePictureType(6)
                .checkedList(mAlbumFiles)
                .widget(
                        Widget.newDarkBuilder(UploadActivity.this)
                                .title("选择图片")
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
                        Toast.makeText(UploadActivity.this, R.string.canceled, Toast.LENGTH_LONG).show();
                    }
                })
                .start();
    }

    private void startPopupWindow(){
        if(p ==null){
            p =new PopupWindow(this);
            p.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            if(categoryTypes.size()>8){
                p.setHeight(Layout.getScale(35)*8);
            }else{
                p.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            p.setOutsideTouchable(true);
            p.setClippingEnabled(false);
            View v= LayoutInflater.from(this).inflate(R.layout.category_type,null);
            LinearLayout linear_types = v.findViewById(R.id.linear_types);
            for(int i=0;i<categoryTypes.size();i++){
                View v_line = new View(this);
                v_line.setLayoutParams(new FrameLayout.LayoutParams(Layout.getScale(236), Layout.getScale(1)));
                v_line.setBackgroundColor(getResources().getColor(R.color.color1C1C1C));
                linear_types.addView(v_line);
                TextView tv = new TextView(this);
                LinearLayout.LayoutParams  p_tv = new LinearLayout.LayoutParams(Layout.getScale(236), Layout.getScale(34));
                tv.setLayoutParams(p_tv);
                tv.setText(categoryTypes.get(i).getCategory());
                tv.setTag(categoryTypes.get(i));
                if(i==categoryTypes.size()-1){
                    tv.setBackgroundResource(R.drawable.shape_5);
                }else{
                    tv.setBackgroundColor(getResources().getColor(R.color.color2B2B2B));
                }
                tv.setPadding(Layout.getScale(86), 0, 0, 0);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setTextColor(Color.parseColor("#ffffff"));
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = (CategoryType)v.getTag();
                        changedType(type);
                        p.dismiss();
                    }
                });
                Layout.setTextViewSize(tv,12);
                linear_types.addView(tv);
            }
            p.setContentView(v);
            p.setOutsideTouchable(true);//设置外部能否点击
//            p.setAnimationStyle(R.style.dialogAnim);//设置动画
            p.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_shape));//必须设置，否则不显示
//            p.setWidth(720);//设置popupwindow 的宽度，有时不能全屏可在这里设置
            p.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    frame_type.setBackgroundResource(R.drawable.shape_3);
                }
            });
            p.showAsDropDown(frame_type,0,0);//显示在控件的下方
            frame_type.setBackgroundResource(R.drawable.shape_4);
        }else{
            p.dismiss();
            p.showAsDropDown(frame_type,0,0);//显示在控件的下方
            frame_type.setBackgroundResource(R.drawable.shape_4);
        }
    }
    public void changedType(CategoryType type){
        tv_type.setText(type.getCategory());
    }
    public void saveButtonClick(){
        if(allUploadFiles.size()<=0){
            Toast.makeText(this,"请选择图片",Toast.LENGTH_SHORT).show();
            return;
        }
        String name = et_name.getText().toString().trim();
        String height = et_height.getText().toString().trim();
        String width = et_width.getText().toString().trim();
        if(name.equals("")){
            et_name.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_name, InputMethodManager.SHOW_FORCED);
            Toast.makeText(this,"请输入产品名称",Toast.LENGTH_SHORT).show();
            return;
        }
        if(height.equals("")){
            Toast.makeText(this,"请输入产品高度",Toast.LENGTH_SHORT).show();
            return;
        }
        if(width.equals("")){
            Toast.makeText(this,"请输入产品宽度",Toast.LENGTH_SHORT).show();
            return;
        }
        if(getSavedPicNum()==allUploadFiles.size()){
            try {
                JSONArray array = new JSONArray();
                for(int i=0;i<allUploadFiles.size();i++){
                    JSONObject json  =new JSONObject();
                    json.put("name", allUploadFiles.get(i).getName());
                    json.put("width",allUploadFiles.get(i).getWidth());
                    json.put("height",allUploadFiles.get(i).getHeight());
                    json.put("picture",allUploadFiles.get(i).getPicture());
                    json.put("category",allUploadFiles.get(i).getCategory().getCategory());
                    json.put("category_id",allUploadFiles.get(i).getCategory().getCategory_id());
                    array.put(json);
                }
                NativeCallUnity.GetMultipleAlbumPathFinish(array.toString());
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            if(!currentUploadFile.isSave()){
                String pictureName = System.currentTimeMillis()+".jpg";
                String path = dir+pictureName;
                cutView.saveBitmap(path);
                currentUploadFile.setSave(true);
                currentUploadFile.setPicture(pictureName);
                currentUploadFile.setName(name);

                currentUploadFile.setHeight(Integer.valueOf(height));
                currentUploadFile.setWidth(Integer.valueOf(width));
                currentUploadFile.setCategory(type);
                int currentPosition = getFilePosition(currentUploadFile);
                ImageView iv_complete = (ImageView) ((FrameLayout)lienar_image.getChildAt(currentPosition)).getChildAt(1);
                iv_complete.setVisibility(View.VISIBLE);
                setSavedNumBer(true);
            }
        }
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
                case LoaderCallbackInterface.SUCCESS:
                {
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
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


    //广播调用上传拍照
    public void startAlbumCapture(int type){
        Intent intent = new Intent(UploadActivity.this, AlbumCaptureActivity.class);
        intent.putExtra("takePictureType",type);
        startActivity(intent);
    }
    //广播调用上传拍照结束
    public void albumCaptureFinish(Intent intent){
        String fileSavePath = intent.getStringExtra("fileSavePath");
        UploadFile file = new UploadFile();
        file.setFilePath(fileSavePath);
        ArrayList<UploadFile> uploadFiles = new ArrayList<>();
        uploadFiles.add(file);
        addViews(uploadFiles,false);
        setSavedNumBer(false);
    }
    //广播调用相册
    public void albumCaptureToAlbum(){
        startAlbum();
    }
}
