package com.beeinc.mylibrary.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * 动态申请权限辅助工具
 */
public class PermissionUtil {
    /**
     * 动态申请所需的权限集合 9组权限
     */
    //调用相机权限
    public final static String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    //调用读写sdCard权限
    public static final String[] STORAGE_PERMISSION = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};

    //调用联系人权限
    public final static String[] CONTACTS_PERMISSION = new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CONTACTS};
    //调用拨打电话等权限
    public final static String[] PHONE_PERMISSION = new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.USE_SIP, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.ADD_VOICEMAIL};
    //调用Calendar时间读写权限
    public final static String[] CALENDAR_PERMISSION = new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};
    //调用传感器权限
    public final static String[] SENSORS_PERMISSION=new String[]{Manifest.permission.BODY_SENSORS};
    //调用位置信息权限
    public final static String[] LOCATION_PERMISSION=new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
    //后台调用位置信息权限 不能与定位权限一起申请，只能在已获取到定位权限后进行申请
    public final static String[] LOCATION_BACK_PERMISSION=new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION};
    //调用录音录像权限
    public final static String[] RECORD_AUDIO_PERMISSION=new String[]{Manifest.permission.RECORD_AUDIO};
    //调用短信读写权限
    public final static String[] SMS_PERMISSION=new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS};
    //android 13 通知权限
    public static final String[] POST_NOTIFICATIONS = new String[]{Manifest.permission.POST_NOTIFICATIONS};
    //android 13 媒体权限
    public static final String[] ANDROID_13_MEDIA_IMAGES_AND_VIDEOS_PERMISSSION = new String[]{Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.READ_MEDIA_VIDEO};
    public static final String[] ANDROID_13_MEDIA_AUDIOS_PERMISSSION = new String[]{Manifest.permission.READ_MEDIA_AUDIO};
    /**
     * 动态申请权限对应code设置
     */
    //调用相机权限
    private final static int CAMERA_CODE=101;
    //调用读写sdCard权限
    private final static int STORAGE_CODE=102;
    //调用联系人权限
    private final static int CONTACTS_CODE=103;
    //调用拨打电话等权限
    private final static int PHONE_CODE = 104;
    //调用Calendar时间读写权限
    private final static int CALENDAR_CODE = 105;
    //调用传感器权限
    private final static int SENSORS_CODE = 106;
    //调用位置信息权限
    private final static int LOCATION_CODE = 107;
    //后台调用位置信息权限
    private static final int LOCATION_BACK_CODE = 108;
    //调用录音录像权限
    private static final int RECORD_AUDIO_CODE = 109;
    //调用短信读写权限
    private static final int SMS_CODE = 110;

    //调用通知权限
    private static final int POST_NOTIFICATIONS_CODE = 111;
    //调用android 13 媒体权限
    private static final int ANDROID_13_MEDIA_IMAGES_AND_VIDEOS_CODE = 112;
    private static final int ANDROID_13_MEDIA_AUDIOS_CODE = 113;
    private static boolean shouldShowRequestPermissionRationale;

    //获取权限类型
    public enum TYPE{
        CAMERA(1),  //调用相机权限
        STORAGE(2),  //调用读写sdCard权限
        CONTACTS(3), //调用联系人权限
        PHONE(4), //调用拨打电话等权限
        CALENDAR(5), //调用Calendar时间读写权限
        SENSORS(6), //调用传感器权限
        LOCATION(7),//调用位置信息权限
        LOCATION_BACK(8),//后台调用位置信息权限
        RECORD_AUDIO(9),//调用录音录像权限
        SMS(10), //调用短信读写权限
        POST_NOTIFICATIONS(11),
        ANDROID_13_MEDIA_IMAGES_AND_VIDEOS(12),
        ANDROID_13_MEDIA_AUDIOS(13);
        int value;
        TYPE(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }
    /**
     * 判断是否已获取到权限
     */
    public static Boolean isHavePermission(Context context, TYPE type){
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            String[] permissions = getPerMissions(type);
            int i = ContextCompat.checkSelfPermission(context, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                return false;
            }
        }
        PermissionSharePreference.saveBoolean("" + type.getValue(), false);
        return true;
    }
    public static void isHavePermission(Context context, TYPE type, IsHavePermissionListener listener) {
        boolean havePermission = isHavePermission(context, type);
        if (listener != null) {
            listener.isHavePermsssion(havePermission);
        }

    }
    private static void startRequestPermission(Activity context, TYPE type) {
        shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale(context, type);
        String[] permissions = getPerMissions(type);
        ActivityCompat.requestPermissions(context, permissions, getRequestCode(type));
    }
    /**
     * 开始提交请求权限
     * @param context 上下文
     * @param type 权限类型
     */
    public static void startRequestPermission(Activity context, TYPE type, RequestPermissionListener listener) {
        boolean b = isHavePermission(context, type);
        if (b) {
            if (listener != null) {
                listener.havePermission();
            }
        } else {
            b = PermissionSharePreference.getBoolean("" + type.value, false);
            if (b) {
                if (listener != null) {
                    listener.notAllowRquestAgain();
                }
            } else {
                startRequestPermission(context, type);
                if (listener != null) {
                    listener.canRequestPermission();
                }
            }
        }

    }

    /***
     * 权限
     * @param requestCode
     * @param permissions
     * @param grantResults
     *  // 用户权限 申请 的回调方法
     * @Override
     * public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
     * super.onRequestPermissionsResult(requestCode, permissions, grantResults);
     */
    public static void onRequestPermissionsResult(Activity context, int requestCode, String[] permissions, int[] grantResults, TYPE type, PermissionGrantResutListener listener) {
        int code = getRequestCode(type);
        if (requestCode == code && Build.VERSION.SDK_INT >= 23) {
            Log.e("--------->", "myreQuestCode = " + code);
            if (grantResults[0] != 0) {
                if (shouldShowRequestPermissionRationale) {
                    boolean now = shouldShowRequestPermissionRationale(context, type);
                    if (!now) {
                        PermissionSharePreference.saveBoolean("" + type.getValue(), true);
                        if (listener != null) {
                            listener.grantFailedAndNotAllowRequest(type);
                        }

                        return;
                    }
                }

                if (listener != null) {
                    listener.grantFailed(type);
                }
            } else if (listener != null) {
                listener.grantSuccess(type);
            }
        }

    }
    private static String[]  getPerMissions(TYPE type){
        String[] permissions =null;
        switch (type.getValue()){
            case 1:
                permissions =CAMERA_PERMISSION;
                break;
            case 2:
                permissions = STORAGE_PERMISSION;
                break;
            case 3:
                permissions = CONTACTS_PERMISSION;
                break;
            case 4:
                permissions = PHONE_PERMISSION;
                break;
            case 5:
                permissions = CALENDAR_PERMISSION;
                break;
            case 6:
                permissions = SENSORS_PERMISSION;
                break;
            case 7:
                permissions = LOCATION_PERMISSION;
                break;
            case 8:
                permissions = LOCATION_BACK_PERMISSION;
                break;
            case 9:
                permissions = RECORD_AUDIO_PERMISSION;
                break;
            case 10:
                permissions = SMS_PERMISSION;
                break;
            case 11:
                permissions = POST_NOTIFICATIONS;
                break;
            case 12:
                permissions = ANDROID_13_MEDIA_IMAGES_AND_VIDEOS_PERMISSSION;
                break;
            case 13:
                permissions = ANDROID_13_MEDIA_AUDIOS_PERMISSSION;
        }
        return permissions;
    }
    private static int getRequestCode(TYPE type){
        int code = 1;
        switch (type.getValue()){
            case 1:
                code =CAMERA_CODE;
                break;
            case 2:
                code =STORAGE_CODE ;
                break;
            case 3:
                code = CONTACTS_CODE;
                break;
            case 4:
                code = PHONE_CODE;
                break;
            case 5:
                code = CALENDAR_CODE;
                break;
            case 6:
                code = SENSORS_CODE;
                break;
            case 7:
                code = LOCATION_CODE;
                break;
            case 10:
                code = SMS_CODE;
                break;
            case 11:
                code = POST_NOTIFICATIONS_CODE;
                break;
            case 12:
                code = ANDROID_13_MEDIA_IMAGES_AND_VIDEOS_CODE;
                break;
            case 13:
                code = ANDROID_13_MEDIA_AUDIOS_CODE;
        }
        return code;
    }
    /**
     * 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
     * @return
     */
    public static boolean shouldShowRequestPermissionRationale(Activity context, TYPE type){
        String[] permissions = getPerMissions(type);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.shouldShowRequestPermissionRationale(permissions[0]);
        }
        return false;
    }
    /**
     * 授权结果监听
     */
    public interface PermissionGrantResutListener {
        void grantSuccess(TYPE var1);

        void grantFailed(TYPE var1);

        void grantFailedAndNotAllowRequest(TYPE var1);
    }

    /**
     * 跳转到app设置 用户拒绝授权后跳转
     * @param context
     */
    public static void toAppSelfSetting(Context context) {
        Intent mIntent = new Intent();
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            mIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            mIntent.setAction(Intent.ACTION_VIEW);
            mIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            mIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(mIntent);
    }
    public interface IsHavePermissionListener {
        void isHavePermsssion(boolean var1);
    }



    public interface RequestPermissionListener {
        void havePermission();

        void canRequestPermission();

        void notAllowRquestAgain();
    }
}
