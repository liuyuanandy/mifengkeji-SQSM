package com.beeinc.mylibrary.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by haiye on 2018/4/14.
 * 系统相关工具类
 */

public class SystemUtil {
    //1.1获取系统版本是否在4.4以上 getIsHigherThanAndroidKITKAT()
    public static boolean getIsHigherThanAndroidKITKAT(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
    //1.2获取系统版本是否在5.0以上 getIsHigherThanAndroidLOLLIPOP()
    public static boolean getIsHigherThanAndroidLOLLIPOP(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
    //1.3获取系统版本是否在6.0以上 getIsHigherThanAndroidM()
    public static boolean getIsHigherThanAndroidM(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
    //1.4获取系统版本是否在7.0以上 getIsHigherThanAndroidN()
    public static boolean getIsHigherThanAndroidN(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }
    //1.4获取系统版本是否在8.0以上 getIsHigherThanAndroidN()
    public static boolean getIsHigherThanAndroidO(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
    //1.5获取系统版本是否在10.0以上 getIsHigherThanAndroidQ()
    public static boolean getIsHigherThanAndroidQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }
    //1.7获取系统版本是否在13.0以上 getIsHigherThanAndroidTIRAMISU()
    public static boolean getIsHigherThanAndroidTIRAMISU() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }
    //获取当前版本号
    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            //getPackageName()是你当前类的包名，0代表是获取版本信息
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TAG", e.getMessage());
        }
        return verCode;
    }

    //获取版本名称
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TAG", e.getMessage());
        }
        return verName;
    }

    /**
     * 获取系统版本是否在7.0 以上
     */
    public static boolean getIs7up(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }
    /**
     * 调用拨打电话
     */
    public static void callPhone(Context context, String phoneNum){
        Intent dialIntent = new Intent();//跳转到拨号界面
        dialIntent.setAction(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:"+phoneNum));
        context.startActivity(dialIntent);
    }
    //判断Context是否前台运行
    public static boolean isFront(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(context.getPackageName())) {
                Log.e("运行", "前端运行");
                return true;
            } else {
                Log.e("运行", "后台运行");
                return false;
            }
        }
        return false;
    }
    /**
     * 判断应用是否正在运行
     */
    public static boolean isAppRunning(Context context, String packageName) {
        boolean isAppRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runninglist = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : runninglist) {
            if (info.topActivity.getPackageName().equals(packageName) && info.baseActivity.getPackageName().equals(packageName)) {
                isAppRunning = true;
                break;
            }
        }
        return isAppRunning;
    }
    /**
     * 打开文件进行安装
     * @param context
     * @param filePath
     * @param provider  使用的7.0文件授权provider
     */

    public static void openFilesInstall(Context context, String filePath, String provider) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, provider,
                    new File(filePath));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
        }
        if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            context.startActivity(intent);
        }
    }
    /**
     * 启动指定应用
     */
    public static void StartApplicationWithPackageName(Context context, String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }
    /**
     * 显示或隐藏软键盘
     */
    public static void hiddenSorftInput(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }
    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 通过应用包名启动应用
     * @param pakageName
     * //被启动应用接受传递数据的方法如下
     *     Intent intent =getIntent();
     *     Bundle bundle = intent.getExtras();
     *       if (bundle != null) {
     *          String name=(String) bundle.get("name");
     *          ed.setText(name);
     *       }
     */
    public static void startAppFirstWay(Context context, String pakageName){
        PackageManager p=context.getPackageManager();
//        Intent in=p.getLaunchIntentForPackage("com.example.secondapp");
        Intent in=p.getLaunchIntentForPackage(pakageName);
        if(in!=null)
        {
            in.putExtra("name","zp");
            context.startActivity(in);
        }
        else
        {
            Toast.makeText(context, "哟，赶紧下载安装这个APP吧", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 通过应用包名和主Activity全路径启动应用.
     * @param pakageName
     * //被启动应用接受传递数据的方法如下
     *     Intent intent =getIntent();
     *     Bundle bundle = intent.getExtras();
     *       if (bundle != null) {
     *          String name=(String) bundle.get("name");
     *          ed.setText(name);
     *       }
     */
    public static void startAppSecondWay(Context context, String pakageName, String mainActivityPath){
        Intent intent2 = new Intent("android.intent.action.MAIN");
        intent2.addCategory("android.intent.category.LAUNCHER");
        ComponentName cn = new ComponentName(pakageName, mainActivityPath);
        intent2.setComponent(cn);intent2.putExtra("name","zp");
        context.startActivity(intent2);
    }
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
            Log.e("TAG", "原图被旋转角度： ========== " + degree );

        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    public static int readAndroidQPictureDegree(Context context,String path) {
        int degree = 0;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ParcelFileDescriptor parcelFileDescriptor =
                        context.getContentResolver().openFileDescriptor(Uri.parse(path), "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                ExifInterface exifInterface = null;
                exifInterface = new ExifInterface(fileDescriptor);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
                Log.e("TAG", "原图被旋转角度： ========== " + degree );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    private static  String uriToPath(Context context,Uri uri) {
        String path=null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                path = getImagePath(context,MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                path = getImagePath(context,contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            path = getImagePath(context,uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            path = uri.getPath();
        }
        return  path;
    }
    private static String getImagePath(Context context,Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
