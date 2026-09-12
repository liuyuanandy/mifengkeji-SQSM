package com.beeinc.SQSB.util;

import static android.graphics.Bitmap.createBitmap;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2BGRA;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;

import com.test.RemapHelper;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;

public class OpenCVUtil {
    /**
     *
     * @param isCapture 是否是拍照
     * @param context
     * @param filePath
     * @param Qpath
     * @param maxSize
     * @return
     */
    public static Bitmap getBitmap(boolean isCapture, Context context, String filePath,String Qpath,int maxSize){
        Bitmap bitmap = null;
        if(isCapture){
            bitmap = ImageDealUtil.decodeFile(filePath, maxSize);
        }else{
            int pictureRotateDegree;
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                bitmap = ImageDealUtil.getBitmapFromUri(context , Uri.parse(Qpath),maxSize);
                int count = bitmap.getByteCount()/(bitmap.getWidth()*bitmap.getHeight());
                pictureRotateDegree= SystemUtil.readAndroidQPictureDegree(context,Qpath);
                if(count>4){
                    String tempFilePath = FileUtil.getCacheSaveImagesDir(context)+FileUtil.getNewFileNameByTime()+".jpg";
                    FileUtil.saveBitmapToFile(tempFilePath,bitmap);
                    bitmap.recycle();
                    bitmap = null;
                    Mat mat = Imgcodecs.imread(tempFilePath);
                    bitmap = Bitmap.createBitmap(mat.cols(),mat.rows(), Bitmap.Config.ARGB_8888);
                    Mat temp = new Mat();
                    Imgproc.cvtColor(mat, temp, COLOR_RGBA2BGRA);
                    Utils.matToBitmap(temp,bitmap);
                    mat.release();
                    temp.release();
                }
            }else{
                bitmap = ImageDealUtil.decodeFile(filePath, maxSize);
                int count = bitmap.getByteCount()/(bitmap.getWidth()*bitmap.getHeight());
                pictureRotateDegree = SystemUtil.readPictureDegree(filePath);
                if(count>4){//图片每个像素大于4个字节表示图片位数大于8
                    String tempFilePath = FileUtil.getCacheSaveImagesDir(context)+FileUtil.getNewFileNameByTime()+".jpg";
                    FileUtil.saveBitmapToFile(tempFilePath,bitmap);
                    bitmap.recycle();
                    bitmap = null;
                    Mat mat = Imgcodecs.imread(tempFilePath);
                    bitmap = Bitmap.createBitmap(mat.cols(),mat.rows(), Bitmap.Config.ARGB_8888);
                    Mat temp = new Mat();
                    Imgproc.cvtColor(mat, temp, COLOR_RGBA2BGRA);
                    Utils.matToBitmap(temp,bitmap);
                    mat.release();
                    temp.release();
                }
            }
            if(pictureRotateDegree!=0){
                Mat mat = new Mat();
                Utils.bitmapToMat(bitmap,mat);
                bitmap.recycle();
                Mat temp = new Mat();
                RemapHelper.setRotate(mat.nativeObj,temp.nativeObj,360-pictureRotateDegree);
                bitmap = createBitmap(temp.width(), temp.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(temp,bitmap);
                mat.release();
                temp.release();
            }
        }
        return bitmap;
    }

    /**
     *
     * @param context
     * @param filePath
     * @param Qpath
     * @param maxSize
     * @return
     */
    public static void reSaveFile(Context context, String filePath,String Qpath,String toFilePath,int maxSize){
        Bitmap bitmap = null;
        int pictureRotateDegree;
        int count;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
            bitmap = ImageDealUtil.getBitmapFromUri(context , Uri.parse(Qpath),maxSize);
            count = bitmap.getByteCount()/(bitmap.getWidth()*bitmap.getHeight());
            pictureRotateDegree= SystemUtil.readAndroidQPictureDegree(context,Qpath);
        }else{
            bitmap = null;
            bitmap = ImageDealUtil.decodeFile(filePath, maxSize);
            count= bitmap.getByteCount()/(bitmap.getWidth()*bitmap.getHeight());
            pictureRotateDegree = SystemUtil.readPictureDegree(filePath);
        }
        if(count>4){
            String tempFilePath = FileUtil.getCacheSaveImagesDir(context)+FileUtil.getNewFileNameByTime()+".jpg";
            OpenCVUtil.saveBitmapToFile(bitmap,tempFilePath);
            bitmap.recycle();
            bitmap = null;

            if(pictureRotateDegree!=0){
                Mat mat = Imgcodecs.imread(tempFilePath);
                bitmap = Bitmap.createBitmap(mat.cols(),mat.rows(), Bitmap.Config.ARGB_8888);
                Mat temp = new Mat();
                Imgproc.cvtColor(mat, temp, COLOR_RGBA2BGRA);
                Utils.matToBitmap(temp,bitmap);
                mat.release();
                temp.release();
                mat = new Mat();
                Utils.bitmapToMat(bitmap,mat);
                bitmap.recycle();
                temp = new Mat();
                RemapHelper.setRotate(mat.nativeObj,temp.nativeObj,360-pictureRotateDegree);
                bitmap = createBitmap(temp.width(), temp.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(temp,bitmap);
                mat.release();
                temp.release();
                OpenCVUtil.saveBitmapToFile(bitmap,toFilePath);
                bitmap.recycle();
            }else{
                Mat mat = Imgcodecs.imread(tempFilePath);
                bitmap = Bitmap.createBitmap(mat.cols(),mat.rows(), Bitmap.Config.ARGB_8888);
                Mat temp = new Mat();
                Imgproc.cvtColor(mat, temp, COLOR_RGBA2BGRA);
                OpenCVUtil.saveBitmapToFile(bitmap,toFilePath);
            }
        }else{
            if(pictureRotateDegree!=0){
                Mat mat = new Mat();
                Utils.bitmapToMat(bitmap,mat);
                Mat temp = new Mat();
                RemapHelper.setRotate(mat.nativeObj,temp.nativeObj,360-pictureRotateDegree);
                Imgproc.cvtColor(temp, temp, COLOR_RGBA2BGRA);
                Imgcodecs.imwrite(toFilePath,temp);
                mat.release();
                mat = null;
                temp.release();
                temp = null;
            }else{
                OpenCVUtil.saveBitmapToFile(bitmap,toFilePath);
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap ){
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Mat dst = new Mat();
        RemapHelper.setRotate(mat.getNativeObjAddr(), dst.getNativeObjAddr(),90);
        bitmap.recycle();
        bitmap = null;
        bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, bitmap);
        mat.release();
        dst.release();
        return bitmap;
    }
    /**
     * @param bitmap
     */
    public static Bitmap getJiaoZhenBitmap(Bitmap bitmap, ArrayList<Point> pointsBerforeJiaozhen, ArrayList<Point> pointsAfterJiaozhen){
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);
        Mat warp_dst = new Mat();
        Mat warp_x = Converters.vector_Point_to_Mat(pointsBerforeJiaozhen, CvType.CV_32F);
        Mat warp_y = Converters.vector_Point_to_Mat(pointsAfterJiaozhen, CvType.CV_32F);

        Mat warp_mat = Imgproc.getPerspectiveTransform(warp_x,warp_y);
        Imgproc.warpPerspective(mat, warp_dst ,warp_mat, new Size(pointsAfterJiaozhen.get(3)));
        Bitmap jiaozhenBitmap = Bitmap.createBitmap(warp_dst.width(), warp_dst.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(warp_dst, jiaozhenBitmap);
        mat.release();
        warp_dst.release();
        warp_x.release();
        warp_y.release();
        mat = null;
        warp_dst = null;
        warp_x = null;
        warp_y = null;
        return jiaozhenBitmap;
    }
    public static Mat bitmapToMat(Bitmap b){
        Mat mat= new Mat();
        Utils.bitmapToMat(b,mat);
        Imgproc.cvtColor(mat, mat, COLOR_RGBA2BGRA);
        return mat;
    }
    public static void saveBitmapToFile(Bitmap b,String filePath){
        Mat mat= new Mat();
        Utils.bitmapToMat(b,mat);
        Imgproc.cvtColor(mat, mat, COLOR_RGBA2BGRA);
        Imgcodecs.imwrite(filePath,mat);
        mat.release();
        mat=null;
    }
    /**
     * 图片压缩 到指定尺寸 并保存
     */

    public static void reSizePicture(Bitmap b,String filePath,int maxPictureSize){
        Mat mat= new Mat();
        Utils.bitmapToMat(b,mat);
        int width = mat.width();
        int height = mat.height();

        int max;
        if(width>height){
            max = width;
        }else{
            max = height;
        }
        boolean needResize=false;
        while (max>maxPictureSize){
            needResize = true;
            width = width/2;
            height = height/2;
            if(width>height){
                max = width;
            }else{
                max = height;
            }
        }
        if(needResize){
            Mat temp =new Mat();
            Imgproc.resize(mat,temp,new Size(width,height));
            Imgproc.cvtColor(temp, temp, COLOR_RGBA2BGRA);
            Imgcodecs.imwrite(filePath,temp);
            temp.release();
            temp =null;
        }else{
            Imgproc.cvtColor(mat, mat, COLOR_RGBA2BGRA);
            Imgcodecs.imwrite(filePath,mat);
        }
        mat.release();
        mat=null;

    }
    //
    public static Bitmap bitmapScale(Bitmap bitmap ,int maxPictureSize){
        Mat mat= new Mat();
        Utils.bitmapToMat(bitmap,mat);
        int width = mat.width();
        int height = mat.height();
        int max;
        if(width>height){
            max = width;
        }else{
            max = height;
        }
        boolean needResize=false;
        while (max>maxPictureSize){
            needResize = true;
            width = width/2;
            height = height/2;
            if(width>height){
                max = width;
            }else{
                max = height;
            }
        }
        Bitmap toBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        if(needResize){
            Mat temp =new Mat();
            Imgproc.resize(mat,temp,new Size(width,height));
//            Imgproc.cvtColor(temp, temp, COLOR_RGBA2BGRA);
            Utils.matToBitmap(temp,toBitmap);
            mat.release();
            mat = null;
            temp.release();
            temp =null;
        }else{
            Utils.matToBitmap(mat,toBitmap);
            mat.release();
            mat = null;
        }
        return toBitmap;
    }
    public static Bitmap getBitmapFromFile(String filePath){
        Mat mat = Imgcodecs.imread(filePath);
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(),mat.rows(), Bitmap.Config.ARGB_8888);
        Mat temp = new Mat();
        Imgproc.cvtColor(mat, temp, COLOR_RGBA2BGRA);
        Utils.matToBitmap(temp,bitmap);
        mat.release();
        temp.release();
        return bitmap;
    }
}
