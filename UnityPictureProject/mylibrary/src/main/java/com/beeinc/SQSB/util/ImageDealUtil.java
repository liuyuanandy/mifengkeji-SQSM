package com.beeinc.SQSB.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by haiye on 2018/4/14.
 */

public class ImageDealUtil {
    /**
    * 旋转图片
    * @param angle 旋转角度
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
    /**
     * @param filePath
     * @param pixels 最大宽高，像素范围
     */
    public static Bitmap decodeFile(String filePath, int pixels) {
        try {
            // 解码图像大小
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            File file = new File(filePath);
            BitmapFactory.decodeStream(new FileInputStream(file), null, o);
            // 找到正确的刻度值，它应该是2的幂。
            final int REQUIRED_SIZE = pixels;
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
            o=null;
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o2.inPurgeable = true; //设置可回收 系统内存不足时可以被回收
            final Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
            return b;
        } catch (FileNotFoundException e) {
        }
        return null;
    }
    public static Bitmap readBitMap(Context context, int resId, boolean isPng) {
        if (isPng) {
            return readBitMapPng(context, resId);
        } else {
            return readBitMapJpg(context, resId);
        }
    }

    /**
     * 读取本地资源的图片
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

    /**
     * 叠加图片
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMapPng(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
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
    public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if( background == null ) {
            return null;
        }

        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        //int fgWidth = foreground.getWidth();
        //int fgHeight = foreground.getHeight();
        //create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        //draw bg into
        cv.drawBitmap(background, 0, 0, null);//在 0，0坐标开始画入bg
        //draw fg into
        cv.drawBitmap(foreground, 0, 0, null);//在 0，0坐标开始画入fg ，可以从任意位置画入
        //save all clip
        cv.save();//保存
        //store
        cv.restore();//存储
        return newbmp;
    }
    public static Bitmap getBitmapFromUri(Context context, Uri uri,int size) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inPreferredConfig = Bitmap.Config.ARGB_8888;
            o.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(new FileInputStream(fileDescriptor), null, o);
            BitmapFactory.decodeFileDescriptor(fileDescriptor,null, o);
            int REQUIRED_SIZE = size;
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
            o.inPreferredConfig = Bitmap.Config.ARGB_8888;
            o2.inJustDecodeBounds = false;//
//            o2.inPurgeable = true; //设置可回收 系统内存不足时可以被回收
//            o2.inInputShareable = true;//设置 是否能够共享一个指向数据源的引用，或者是进行一份拷贝
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor,null, o2);;
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //读取图片文件到bitmap decodeUri()
    public static  Bitmap decodeUri(Context context ,Uri uri,int pixels) throws IOException {
        // 解码图像大小
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        o.inPreferredConfig = Bitmap.Config.ARGB_8888;

        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        BitmapFactory.decodeFileDescriptor(fileDescriptor,null, o);
        // 找到正确的刻度值，它应该是2的幂。
        final int REQUIRED_SIZE = pixels;
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
        o=null;
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
        o2.inSampleSize = scale;
        o2.inPurgeable = true; //设置可回收 系统内存不足时可以被回收
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor,null, o2);
        parcelFileDescriptor.close();

        //方法 2
//        Bitmap bmp = null;
//        try {
//            bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
//        } catch (FileNotFoundException e) {
//        } catch (IOException e) {
//        }

        return image;
    }
    /**
     * 文件转Uri fileToUri()
     * android 7.0以上需要使用FileProvider
     * @return
     */
    public static Uri fileToUri(Context context,String filePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(filePath));
        }
        return Uri.fromFile(new File(filePath));
    }

    public static int[] decodeUriWidthAndHeight(Context context, Uri uri){
        // 解码图像大小
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        o.inPreferredConfig = Bitmap.Config.ARGB_8888;
        ParcelFileDescriptor parcelFileDescriptor =
                null;
        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        BitmapFactory.decodeFileDescriptor(fileDescriptor,null, o);
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        o=null;
        return new int[]{width_tmp,height_tmp};
    }
}
