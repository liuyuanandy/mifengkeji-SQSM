package com.beeinc.SQSB.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.beeinc.SQSB.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//import com.beeway.Genius.activities.InformationContentActivity;


/**
 * 常用工具类
 *
 * @author Administrator
 */
public class common {
    /**
     * 获取字符串字符长度
     *
     * @param str 字符串
     * @return 字符长度 中文占两个字节，英文占一个字节。
     */
    public static int getStringLength(String str) {
        str = str.trim();
        if (str.equals("") || str == null) {
            return 0;
        }

        String chinese = "[\u4e00-\u9fa5]";

        int size = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.substring(i, i + 1).matches(chinese)) {
                size += 2;
            } else {
                size += 1;
            }
        }

        return size;
    }

    /**
     * 随机数
     *
     * @param max 上限数
     * @param min 下限数
     */
    public static int RandomNum(int max, int min) {
        return (int) (Math.floor(Math.random() * (max - min + 1)) + min);
    }
    //设置--铃声的具体方法
    public void setMyRingtone(String path, Context context) {
        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri = context.getContentResolver().insert(uri, values);
        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
    }

    //设置--提示音的具体实现方法
    public void setMyNotification(String path, Context context) {
        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri = context.getContentResolver().insert(uri, values);
        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, newUri);
        Toast.makeText(context.getApplicationContext(), "设置通知铃声成功！", Toast.LENGTH_SHORT).show();
        //System.out.println("setMyNOTIFICATION-----提示音");
    }

    //设置--闹铃音的具体实现方法
    public void setMyAlarm(String path, Context context) {
        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri = context.getContentResolver().insert(uri, values);
        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, newUri);
        Toast.makeText(context.getApplicationContext(), "设置闹钟铃声成功！", Toast.LENGTH_SHORT).show();
        //System.out.println("setMyNOTIFICATION------闹铃音");
    }

    /*
     * 对字符串 进行md5 加密
     */
    public static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] strTemp = s.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    //获取当前版本号
    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            //getPackageName()是你当前类的包名，0代表是获取版本信息
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
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
        } catch (NameNotFoundException e) {
            Log.e("TAG", e.getMessage());
        }
        return verName;
    }

    //显示软键盘
    public static void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, 0);
    }

    //隐藏软键盘  如果输入法打开则关闭，如果没打开则打开
    public static void hiddenSorftInput(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * 打开文件进行安装
     * @param context
     * @param filePath
     * @param provider  使用的7.0文件授权provider
     */

    public static void openFilesInstall(Context context,String filePath,String provider) {
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
    public static Bitmap readBitMap(Context context, int resId, boolean isPng) {
        if (isPng) {
            return readBitMapPng(context, resId);
        } else {
            return readBitMapJpg(context, resId);
        }
    }

    //设置图片毛玻璃
    public static Bitmap fastblur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
//      Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

//      Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    /**
     * 读取本地资源的图片
     *
     * @param context 上下文
     * @param resId   图片ID
     * @return 控件显示宽度
     * width 图片显示的宽
     * height 图片显示的高
     */
    public static Bitmap readBitMapJpg(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        /**
         * 如果该值设为true那么将不返回实际的bitmap对象，
         * 不给其分配内存空间但是可以得到一些解码边界信息即图片大小等信息
         * 。因此我们可以通过设置inJustDecodeBounds为true，
         * 获取到outHeight(图片原始高度)和 outWidth(图片的原始宽度)，
         * 然后计算一个inSampleSize(缩放值)，就可以取图片了，这里要注意的是，inSampleSize 可能等于0，
         * 必须做判断。也就是说先将Options的属性inJustDecodeBounds设为true，
         * 先获取图片的基本大小信息数据(信息没有保存在bitmap里面，而是保存在options里面)，
         * 通过options.outHeight和 options. outWidth获取的大小信息以及自己想要到得图片大小计算出来缩放比例inSampleSize，
         * 然后紧接着将inJustDecodeBounds设为false，就可以根据已经得到的缩放比例得到自己想要的图片缩放图了。
         */
        opt.inJustDecodeBounds = true;
        InputStream input = context.getResources().openRawResource(resId);
        BitmapFactory.decodeStream(input, null, opt);
//		opt.inSampleSize =2; //缩略图大小为原始图片大小的几分之一
//		Log.e("opt.inSampleSize=","opt.outWidth="+opt.outWidth+";opt.outHeight="+opt.outHeight);
        opt.inJustDecodeBounds = false;//

        opt.inPurgeable = true; //设置可回收 系统内存不足时可以被回收
        opt.inInputShareable = true;//设置 是否能够共享一个指向数据源的引用，或者是进行一份拷贝
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap readBitMapPng(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
//		opt.inPreferredConfig = Bitmap.Config.RGB_565;
        /**
         * 如果该值设为true那么将不返回实际的bitmap对象，
         * 不给其分配内存空间但是可以得到一些解码边界信息即图片大小等信息
         * 。因此我们可以通过设置inJustDecodeBounds为true，
         * 获取到outHeight(图片原始高度)和 outWidth(图片的原始宽度)，
         * 然后计算一个inSampleSize(缩放值)，就可以取图片了，这里要注意的是，inSampleSize 可能等于0，
         * 必须做判断。也就是说先将Options的属性inJustDecodeBounds设为true，
         * 先获取图片的基本大小信息数据(信息没有保存在bitmap里面，而是保存在options里面)，
         * 通过options.outHeight和 options. outWidth获取的大小信息以及自己想要到得图片大小计算出来缩放比例inSampleSize，
         * 然后紧接着将inJustDecodeBounds设为false，就可以根据已经得到的缩放比例得到自己想要的图片缩放图了。
         */
        opt.inJustDecodeBounds = true;
        InputStream input = context.getResources().openRawResource(resId);
        BitmapFactory.decodeStream(input, null, opt);
//		opt.inSampleSize =2; //缩略图大小为原始图片大小的几分之一
//		Log.e("opt.inSampleSize=","opt.outWidth="+opt.outWidth+";opt.outHeight="+opt.outHeight);
        opt.inJustDecodeBounds = false;//

        opt.inPurgeable = true; //设置可回收 系统内存不足时可以被回收
        opt.inInputShareable = true;//设置 是否能够共享一个指向数据源的引用，或者是进行一份拷贝
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    //获取屏幕精度
    public static int getDensity(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int dpi = dm.densityDpi;
        return dpi;
    }

    //获取时间 月-日如： 07-01
    public static String getStringDate2(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }
    //获取时间
    public static String getStringDate(Date currentTime) {
//    	   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    // String转Date
    public static Date StringtoData(String str) {
        Log.e("str", "str=" + str);
//    	DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        try {
            date = format1.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //获取时间的长整形值
    public static long getTime(Date d) {
        return d.getTime();
    }

    /**
     * 获取资讯发布时间字符串
     *
     * @param d 当前时间
     * @param D 资讯发布时间
     * @return
     */
    public static String getString(Date d, Date D) {
        long time = d.getTime() - D.getTime();
        if (time / 1000 < 60) {
            return "刚刚";
        } else if (time / 1000 < 60 * 60) {
            return time / 1000 / 60 + "分钟前";
        } else if (time / 1000 < 60 * 60 * 24) {
            return time / 1000 / 60 / 60 + "小时前";
        } else {
            return common.getStringDate(D);
        }
    }

    //文件长度 转换为适当单位。
    public static String formatFileSize(long length) {
        String result = null;
        int sub_string;
        if (length >= 1073741824) {
            sub_string = String.valueOf((float) length / 1073741824).indexOf(".");
            result = ((float) length / 1073741824 + "000").substring(0,
                    sub_string + 3)
                    + "GB";
        } else if (length >= 1048576) {
            sub_string = String.valueOf((float) length / 1048576).indexOf(".");
            result = ((float) length / 1048576 + "000").substring(0,
                    sub_string + 3)
                    + "MB";
        } else if (length >= 1024) {
            sub_string = String.valueOf((float) length / 1024).indexOf(".");
            result = ((float) length / 1024 + "000").substring(0,
                    sub_string + 3)
                    + "KB";
        } else if (length < 1024)
            result = Long.toString(length) + "B";
        return result;
    }

    //根据路径删除文件
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if (item.isDirectory()) {
                    deleteFilesByDirectory(item);
                } else {
                    item.delete();
                }
            }
        }
    }

    /**
     * 启动指定Activity
     *
     * @param cl
     */
    public static void gotoActivity(Activity context, Class<?> cl) {
        Intent in = new Intent();
        in.setClass(context, cl);
        context.startActivity(in);
        context.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 跳转Activity
     * @param con
     * @param cl
     * @param intent
     */
    public static void gotoActivity(Activity con ,Class<?> cl , Intent intent){
        intent.setClass(con,cl);
        con.startActivity(intent);
        con.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 带返回值跳转Activity
     * @param con
     * @param cl
     * @param intent
     * @param requestCode
     */
    public static void gotoActivity(Activity con ,Class<?> cl , Intent intent,int requestCode){
        intent.setClass(con,cl);
        con.startActivityForResult(intent,requestCode);
        con.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 退出指定的Activity
     */
    public static void finishActivity(Activity context) {
        context.finish();
        context.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
    //f 文件    size压缩到的宽度
    public static Bitmap decodeFile(File f, int size) {
        try {
            // 解码图像大小
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // 找到正确的刻度值，它应该是2的幂。
//            final int REQUIRED_SIZE = 70;
            final int REQUIRED_SIZE = size;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
    //判断应用是否正在运行
    public static boolean isRunningApp(Context context, String packageName) {
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

    //判断Context前台运行还是后台运行
    public static boolean isfront(Context context) {
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
    /*
  * 旋转图片
  * @param angle
  * @param bitmap
  * @return Bitmap
  */
    public Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    //返回0 下载成功，返回1，下载失败
    public static int downLoadFile(String filePath,String url){
        String fileinterim = filePath+"a";
        Log.e("--------->","filePath= "+filePath+" ; url = "+url);
        Log.e("开始下载","开始下载");
        HttpURLConnection cn;
        try {
            cn = (HttpURLConnection) new URL(url).openConnection();
            cn.connect();
            float length = cn.getContentLength();//单位字节
            cn.setConnectTimeout(3000);//链接超时时间
            InputStream stream = cn.getInputStream();
            File downloadFile = new File(fileinterim);
            if(downloadFile.exists()){
                downloadFile.delete();
            }
            FileOutputStream out = new FileOutputStream(downloadFile);
            byte buf[] = new byte[4048];
            int len;
            if (cn.getResponseCode() <400) {
                Log.e("开始下载","开始下载");
                long total =0;//总共下载的文件长度
                while ((len = stream.read(buf)) != -1) {
                    total = total +len;
                    out.write(buf, 0, len);
                    Log.e("开始下载","开始下载"+(total*100)/length);
                }
                System.out.println("total = "+total);
                cn.disconnect();
                stream.close();
                out.close();
                downloadFile.renameTo(new File(filePath));
                return 0;
            }
            System.out.println("下载文件成功");
        } catch (MalformedURLException e) {
            return -1;
        } catch (IOException e) {
//            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
    //获取系统版本是否在7.0 以上
    public static boolean getIs7up(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }
    public static void callPhone(Context context,String phoneNum){
        Intent dialIntent = new Intent();//跳转到拨号界面
        dialIntent.setAction(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:"+phoneNum));
        context.startActivity(dialIntent);
    }
    //启动指定应用
    public static void StartApplicationWithPackageName(Context context,String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (NameNotFoundException e) {
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

}
