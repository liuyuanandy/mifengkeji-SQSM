package com.beeinc.SQSB.util;

import android.support.v4.app.FragmentActivity;

import com.beeinc.SQSB.bean.CategoryType;
import com.beeinc.SQSB.bean.UploadFile;
import com.beeinc.SQSB.dialog.InputFixPictureInfoDialog;
import com.beeinc.SQSB.dialog.LoadingDialogFragment;
import com.beeinc.SQSB.dialog.QuitFixDialog;

import java.util.ArrayList;

public class DialogUtil {
    //显示正在加载dialog AppCompatActivity
    public synchronized static LoadingDialogFragment showLoadingDialog(FragmentActivity context, LoadingDialogFragment dialog){
        if(dialog!=null&&System.currentTimeMillis()-dialog.lasttimeShow<200){//防止快速点击
            return dialog;
        }
        if(dialog==null){
            dialog = new LoadingDialogFragment();
            dialog.show(context.getSupportFragmentManager(),"dialog");
        }else{
            if(dialog.isAdded()){
                if(!dialog.isVisible()&&!dialog.isRemoving()){
                    context.getSupportFragmentManager().beginTransaction().remove(dialog);
                    dialog.show(context.getSupportFragmentManager(),"dialog");
                }
            }else{
                if(!dialog.isVisible()&&!dialog.isRemoving()){
                    dialog.show(context.getSupportFragmentManager(),"dialog");
                }
            }
        }
        dialog.lasttimeShow = System.currentTimeMillis();
        return dialog;
    }
    //显示修图完成导入弹窗
    public synchronized static InputFixPictureInfoDialog showFixImport(FragmentActivity context, InputFixPictureInfoDialog dialog, UploadFile uploadFile, ArrayList<CategoryType> categoryTypes){
        if(dialog!=null&&System.currentTimeMillis()-dialog.lasttimeShow<200){//防止快速点击
            return dialog;
        }
        if(dialog==null){
            dialog = new InputFixPictureInfoDialog();
            dialog.set(uploadFile);
            dialog.setCategoryTypes(categoryTypes);
            dialog.show(context.getSupportFragmentManager(),"dialog");
        }else{
            if(dialog.isAdded()){
                if(!dialog.isVisible()&&!dialog.isRemoving()){
                    context.getSupportFragmentManager().beginTransaction().remove(dialog);
                    dialog.set(uploadFile);
                    dialog.setCategoryTypes(categoryTypes);
                    dialog.show(context.getSupportFragmentManager(),"dialog");
                }
            }else{
                if(!dialog.isVisible()&&!dialog.isRemoving()){
                    dialog.set(uploadFile);
                    dialog.setCategoryTypes(categoryTypes);
                    dialog.show(context.getSupportFragmentManager(),"dialog");
                }
            }
        }
        dialog.lasttimeShow = System.currentTimeMillis();
        return dialog;
    }
    //显示放弃修图弹窗
    public synchronized static QuitFixDialog showQuitFixDialog(FragmentActivity context, QuitFixDialog dialog){
        if(dialog!=null&&System.currentTimeMillis()-dialog.lasttimeShow<200){//防止快速点击
            return dialog;
        }
        if(dialog==null){
            dialog = new QuitFixDialog();
            dialog.show(context.getSupportFragmentManager(),"dialog");
        }else{
            if(dialog.isAdded()){
                if(!dialog.isVisible()&&!dialog.isRemoving()){
                    context.getSupportFragmentManager().beginTransaction().remove(dialog);
                    dialog.show(context.getSupportFragmentManager(),"dialog");
                }
            }else{
                if(!dialog.isVisible()&&!dialog.isRemoving()){
                    dialog.show(context.getSupportFragmentManager(),"dialog");
                }
            }
        }
        dialog.lasttimeShow = System.currentTimeMillis();
        return dialog;
    }
}

