package com.beeinc.SQSB.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 文件处理相关操作的工具类
 *
 * 方法目录
 * 1.sdcard 相关
 * 判断是否存在sd卡  isHaveSdcard()
 * 获取内存卡路径     getSDCardPath()
 * 获取SD卡的容量 单位byte getSdCardAllSize()
 * 获取sd卡剩余空间 单位byte getSdCardFreeSpace();
 *
 * 2.文件相关
 * 创建文件 createFile()
 * 公共目录创建Uri createUri() android 10.0以上
 * 调用SAF框架，创建文件 createFileBySAF() android 5.0以后
 * 删除文件 deleteFile()
 * 删除Uri deleteUri();
 * 保存字符串到文件中 saveTextToFile()
 * 保存字符串到文件中 saveTextToUri()
 * 读取本地文件下的txt文件 readTextFromFile()
 * 通过Uri获取文件真实路径  getRealPathFromURI()
 *
 *
 * 3.文件夹相关
 *
 * 获取文件夹大小 getFolderSize();
 * 读取本地文件下的txt文件 readTextFile();
 * 创建文件夹 createFileDirectory()
 * 删除文件夹 deleteDirectory();
 *
 * 4.转换byte长度单位,转成(Gb/Mb/kb) formatBtyes()
 *
 * 读取图片文件到bitmap decodeFile
 * 文件拷贝 copyFile()
 * 拷贝图片到相册 copyFileQToAlbum();
 * 拷贝视频到相册 saveVideoToAlbum()
 * 获取相册路径 getSystemAlbumPath()
 * 20.获取文件后缀名 getFileformat() 如：.jpg
 * 22.androidQ文件拷贝 Uri 拷贝到文件 QCopyfileFromUri()
 * 23.通过Uri获取文件真实路径  getRealFilePath()
 * 25.判断文件夹是否含有文件 isDirectoryHaveFile()
 * 26.获取文件夹下所有文件 getAllFilesFromDirectroy()
 * 27. 计算文件的 MD5 值 getFileMD5()
 * 28.获取图片的旋转角度 readPictureDegree();
 * 29.获取视频的旋转角度 readVideoDegree();
 *
 * 31.通过Uri获取图片bitmap getBitmapFromUri()
 * 32.文件转Uri fileToUri();
 */
public class FileUtil {
    //图片-存放路径
    private final static  String PICTURES = "pictures";
    //视频-存放路径
    private final static  String MOVIES = "movies";
    //assets-本地存放路径
    private final static  String ASSETS = "assets";
    //文本-存放路径
    private final static  String TXT = "txt";
    /**
     * 保存bitmap到文件
     * @param path 文件保存路径
     * @param bm   bitmap
     * 质量压缩百分比 如：90 是压缩率，表示压缩10%; 如果不压缩是100，表示压缩率为0
     */
    public static void saveBitmapToFile(String path, Bitmap bm) {
        Log.e("----------->","path = "+path);
        Log.e("Bitmap", "开始保存");
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.e("Bitmap", "已经保存");
            f = null;
        } catch (FileNotFoundException e) {
            Log.e("Bitmap", "保存失败"+e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Bitmap", "保存失败"+e.getMessage());
            e.printStackTrace();
        }
    }
    // 1.sdcard 相关
    //判断是否存在内存卡  isHaveSdcard()
    public static boolean isHaveSdcard() {
       return FileUtilImplement.isHaveSdcard();
    }
    //获取内存卡路径     getSDCardPath()
    public static String getSdCardPath(){
        return FileUtilImplement.getSdCardPath();
    }
    //转换byte长度单位,转成(Gb/Mb/kb) formatBtyes()
    public static String formatBtyes(long length) {
        return FileUtilImplement.formatBtyes(length);
    }
    //获取SD卡的容量 单位byte getSDCardAllSize()
    public static long getSdCardAllSize(){
        return FileUtilImplement.getSDCardAllSize();
    }
    //获取sd卡剩余空间 单位byte getSdCardFreeSpace();
    public static long getSdCardFreeSpace() {
        return FileUtilImplement.freeSpaceOnSdcard();
    }

    //2.文件相关
    /**
     * 创建文件 createFile();
     * 缓存目录,沙盒目录
     * sdcard外部目录,公共目录(android 10.0以下)
     */
    public static boolean createFile(String filePath,boolean isReplace) {
        return FileUtilImplement.createFile(filePath,isReplace);
    }
    /**
     * 创建Uri createUri()公共目录(android 10.0以上)
     */
    public static Uri createUri(Context context,String dir,String mimeType,String publicType,FileInfo fileInfo,boolean isReplace){
        return FileUtilImplement.createUri(context,dir,mimeType,publicType,fileInfo,isReplace);
    }
    //调用SAF框架，创建文件 createFileBySAF()
    public static void createFileBySAF(Activity context,String mimeType, String fileName,int requesCode) {
        FileUtilImplement.createFileBySAF(context,mimeType,fileName,requesCode);
    }
    //删除文件 deleteFile();
    public static boolean deleteFile(File file) {
        return FileUtilImplement.deleteFile(file);
    }
    //删除文件 deleteFile();
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean deleteUri(Context context, Uri uri){
        return FileUtilImplement.deleteUri(context,uri);
    }
    //保存字符串到文件中 saveTextToFile();
    public static void saveTextToFile(String filePath, String filecontent){
        FileUtilImplement.saveTextToFile(filePath, filecontent);
    }
    //保存字符串到文件中 saveTextToUri();
    public static void saveTextToUri(Context context,Uri uri, String filecontent){
        FileUtilImplement.saveTextToUri(context,uri, filecontent);
    }
    /**
     * 读取本地文件下的txt文件 readTextFromFile();
     * @param filePath
     */
    public static String readTextFromFile(String filePath){
        return FileUtilImplement.readTextFile(filePath);
    }
    /**
     * 读取本地文件下的txt文件 readTextFile();
     */
    public static String readTextFromUri(Context context,Uri uri){
        return FileUtilImplement.readTextFromUri(context,uri);
    }
    /**
     * 读取图片文件到 bitmap decodeFile()
     * Android 10以下可用
     */
    public static Bitmap decodeFile(String filePath, int pixels) {
        return FileUtilImplement.decodeFile(filePath,pixels);
    }

    /**
     *  读取图片文件到bitmap decodeUri()
     */
    public static  Bitmap decodeUri(Context context ,Uri uri,int pixels) throws IOException {
        return FileUtilImplement.decodeUri(context,uri,pixels);

    }
    /**
     * 保存bitmap到文件 saveBitmapToFile()
     * android 10以下可用
     * @param path 文件保存路径
     * @param bm   bitmap
     * @param compressionRatio 质量压缩百分比 如：90 是压缩率，表示压缩10%; 如果不压缩是100，表示压缩率为0
     */
    public static void saveBitmapToFile(String path, Bitmap bm,int compressionRatio) {
        FileUtilImplement.saveBitmapToFile(path,bm,compressionRatio);

    }
    /**
     * 保存bitmap到文件 saveBitmapToUri()
     * @param uri 文件保存路径
     * @param bm   bitmap
     * @param compressionRatio 质量压缩百分比 如：90 是压缩率，表示压缩10%; 如果不压缩是100，表示压缩率为0
     */
    public static boolean saveBitmapToUri(Context context,Uri uri, Bitmap bm,int compressionRatio) {
        return FileUtilImplement.saveBitmapToUri(context,uri,bm,compressionRatio);
    }
    /**
     * 创建文件夹 createFileDirectory()
     * @param type 1.缓存路径(无需权限)
     *             2内部沙盒路径(无需权限),
     *             3.外部路径非公共目录(Android 10以下，Android 10.0后不能使用。需要权限(android 6.0以上需要动态授权))
     *             4.公共目录(Android 10.0以下，Android 10.0后不能使用。需要传publicDirType)
     * @param dir 如：save/picture
     * @param publicType
     * Environment.DIRECTORY_PICTURES，Environment.DIRECTORY_MUSIC,Environment.DIRECTORY_MOVIES
     * Environment.DIRECTORY_DCIM，Environment.DIRECTORY_DOWNLOADS
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String createFileDirectory(@NonNull Context context, String dir, @NonNull int type, String publicType){
        return FileUtilImplement.createFileDirectory(context,dir,type,publicType);
    }
    //获取文件夹大小 getFolderSize();
    public static long getFolderSize(File file) throws Exception {
        return FileUtilImplement.getFolderSize(file);
    }

    //删除文件夹(含子文件夹内容) deleteFileDirectory();
    public static void deleteFileDirectory(File directory) {
        FileUtilImplement.deleteFileDirectory(directory);
    }

    /**
     * 拷贝文件到文件 copyFileToFile()
     * android 10以下任意目录互拷。
     */
    public static boolean copyFileToFile(Context context,String fromFilePath,String toFilePath,boolean reWrite,boolean isDeletFrom){
        return FileUtilImplement.copyFileToFile(context,fromFilePath,toFilePath,reWrite,isDeletFrom);
    }

    /**
     * 拷贝文件到Uri
     * android 10.0以上，从缓存或沙盒中拷贝到 公共目录
     */
    public static void copyFileToUri(Context context,String fromFilePath,Uri toUri,boolean isDeleteFrom){
        FileUtilImplement.copyFileToUri(context,fromFilePath,toUri,isDeleteFrom);
    }
    /**
     * 拷贝Uri 到文件
     * android 5.0以上可用,Android 10.0以上可用，将外部文件拷贝到 缓存或沙盒
     */
    public static void copyUriToFile(Context context,Uri fromUri,String toFilePath,boolean isRewrite){
        FileUtilImplement.copyUriToFile(context,fromUri,toFilePath,isRewrite);
    }
    /**
     * 拷贝Uri 到Uri
     * android 10.0以上，将外部文件拷贝到 公共目录
     */
    public static void copyUriToUri(Context context,Uri fromUri,Uri toUri,boolean isReWirte,boolean isDeleteFrom){
        FileUtilImplement.copyUriToUri(context,fromUri,toUri,isReWirte,isDeleteFrom);
    }

    /**
     * 拷贝图片文件到相册 copyImageFileToAlbum()
     * @param context
     * @param fromPath
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void copyImageFileToAlbum(Context context,String appName, String fromPath){
        if(SystemUtil.getIsHigherThanAndroidQ()){
            Uri toUri= FileUtil.createUri(context, FileUtil.getAlbumSaveImagesDir(context,appName),MimeType.jpg,Environment.DIRECTORY_DCIM,FileInfo.createImageFileInfo(getNewFileNameByTime()),true);
            copyFileToUri(context,fromPath,toUri,false);
        }else{
            String toPath = FileUtil.getAlbumSaveImagesDir(context,appName)+FileUtil.getNewFileNameByTime()+FileUtil.getFileSuffix(fromPath,true);
            FileUtil.copyFileToFile(context,fromPath, toPath, true,false);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(toPath));
            intent.setData(uri);
            context.sendBroadcast(intent);
        }
    }

    /**
     * android拷贝图片到相册 copyUriToAlbum()
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void copyImageUriToAlbum(Context context, Uri fromUri,String appName) {
        if(SystemUtil.getIsHigherThanAndroidQ()){
            Uri toUri= FileUtil.createUri(context,FileUtil.getAlbumSaveImagesDir(context,appName),MimeType.jpg,Environment.DIRECTORY_DCIM,FileInfo.createImageFileInfo(getNewFileNameByTime()),true);
            FileUtil.copyUriToUri(context,fromUri,toUri,true,false);
        }else{
            String toPath = FileUtil.getAlbumSaveImagesDir(context,appName)+getFileRealNameFromUri(context,fromUri);
            FileUtil.copyUriToFile(context,fromUri, toPath, true);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(toPath));
            intent.setData(uri);
            context.sendBroadcast(intent);
        }
    }
    //拷贝视频文件到相册 copyImageFileToAlbum()
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void copyVideoFileToAlbum(Context context, String appName, String fromPath){
        if(SystemUtil.getIsHigherThanAndroidQ()){
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(fromPath);
            int nVideoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int nVideoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            int duration = Integer
                    .parseInt(retriever
                            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            File file = new File(fromPath);
            FileInfo fileInfo = FileInfo.createVideoFileInfo(file.getName(),duration,file.length(),nVideoWidth,nVideoHeight);
            Uri toUri= FileUtil.createUri(context, FileUtil.getAlbumSaveVideoDir(context,appName),MimeType.mp4,Environment.DIRECTORY_DCIM,fileInfo,true);
            copyFileToUri(context,fromPath,toUri,false);
        }else{
            File file = new File(fromPath);
            String toPath = FileUtil.getAlbumSaveVideoDir(context,appName)+file.getName();
            FileUtil.copyFileToFile(context,fromPath, toPath, true,false);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(toPath));
            intent.setData(uri);
            context.sendBroadcast(intent);
        }
    }

    //17.android 拷贝视频到相册 saveVideoToAlbum()
    public static synchronized void copyVideoToAlbum(final Activity context, String videoAddress){
//        if(SystemUtil.getIsHigherThanAndroidQ()){
//            FileUtil.copyPrivateVideoToAlbum(context,videoAddress);
//        }else{
//            String publicPath = FileUtil.getSystemAlbumPath()+FileUtil.getNewFileNameByTime()+FileUtil.getFileSuffix(videoAddress,true);
//            FileUtil.copyFileToFile(context,videoAddress, publicPath, true,false);
//            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            Uri uri = Uri.fromFile(new File(publicPath));
//            intent.setData(uri);
//            context.sendBroadcast(intent);
//        }
//        context.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(context,"保存成功",Toast.LENGTH_SHORT).show();
//            }
//        });
    }
    /**
     * 获取文件后缀名 getFileSuffix() 如：.jpg
     * @param isNeedDot 是否需要带上“.”
     */
    public static String getFileSuffix(String filePath,boolean isNeedDot){
        String suffix = null;
        File file = new File(filePath);
        String fileName = file.getName();
        int dotPosition = fileName.lastIndexOf(".");
        if(dotPosition!=-1){
            if(isNeedDot){
                suffix = fileName.substring(dotPosition);
            }else{
                suffix = fileName.substring(dotPosition+1);
            }
        }
       return suffix;
    }

    //判断文件夹是否含有文件 isDirectoryHaveFile()
    public static boolean isDirectoryHaveFile(String dir){
        File file = new File(dir);
        if(file.isDirectory()){
            File[] listFiles = file.listFiles();
            for(int i=0;i<listFiles.length;i++){
                if(listFiles[i].isDirectory()){
                    return isDirectoryHaveFile(listFiles[i].getAbsolutePath());
                }else{
                    return true;
                }
            }
        }
        return false;
    }
    //获取文件夹下所有文件 getAllFilesFromDirectroy()
    public static ArrayList<File> getAllFilesFromDirectroy(String fileDir){
        ArrayList<File> files = new ArrayList<>();
        File dirFile = new File(fileDir);
        if(dirFile.exists()&&dirFile.isDirectory()){
            File[] listFiles = dirFile.listFiles();
            for(int i=0;i<listFiles.length;i++){
                if(listFiles[i].isFile()){
                    files.add(listFiles[i]);
                }else{
                    files.addAll(getAllFilesFromDirectroy(listFiles[i].getAbsolutePath()));
                }
            }
        }
        return files;
    }

    /**
     * 获取图片的旋转角度 readPictureDegree();
     */
    public static int readImageFileDegree(Context context, String path){
        int degree = 0;
        if(SystemUtil.getIsHigherThanAndroidN()){
            degree = readImageFileDegreeN(context,path);
        }else{
            degree = readImageFileDegree(path);
        }
        return degree;
    }
    /**
     * 获取图片的旋转角度 readPictureDegree();//逆时针旋转角度
     */
    public static int readImageFileDegree(Context context, Uri uri){
        int degree = 0;
        if(SystemUtil.getIsHigherThanAndroidN()){
            degree = readImageFileDegreeN(context,uri);
        }else{
            degree = readImageFileDegree(uri.getPath());
        }
        return degree;
    }
    /**
     * 获取图片的旋转角度 readPictureDegree();
     * @param path 文件路径  android 7.0(N)以上可用
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static int readImageFileDegreeN(Context context, String path){
        int degree = 0;
        try {
            Uri uri = FileUtil.fileToUri(context,path);
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    /**
     * 获取图片的旋转角度 readPictureDegree();
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static int readImageFileDegreeN(Context context, Uri uri){
        int degree = 0;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    //28.获取图片的旋转角度-android N以下可用
    private static int readImageFileDegree(String path) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 29.获取视频的旋转角度 readVideoDegree();
     * @param context 仅androidQ以上传参
     * @param path
     * @return
     */
    public static int readVideoDegree(Context context,String path){
        int deegree;
        if(SystemUtil.getIsHigherThanAndroidQ()){
            deegree = readVideoDegreeHigherThanAndroidN(context,path);
        }else{
            deegree = readVideoDegreeLessThanAndroidQ(path);
        }
        return deegree;
    }
    //29.获取视频的旋转角度 android 7.0(N)以上可用
    private static int readVideoDegreeHigherThanAndroidN(Context context,String filePath){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ParcelFileDescriptor parcelFileDescriptor =
                    null;
            try {
                Uri uri = FileUtil.fileToUri(context,filePath);
                parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                MediaMetadataRetriever retr = new MediaMetadataRetriever();
                retr.setDataSource(filePath);
                String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                return Integer.valueOf(rotation);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    //29.获取视频的旋转角度-androidQ以下
    private static int readVideoDegreeLessThanAndroidQ(String filePath){
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(filePath);
        String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        Log.e("----------->","rotate = "+rotation);
        return Integer.valueOf(rotation);
    }


    /**
     * 通过URi 获取文件真实路径 仅限媒体文件和应用自身创建的文件
     */

    public static String getRealPathFromURI(Context context, Uri uri) {
        return FileUtilImplement.getRealPathFromURI(context,uri);
    }

    /**
     * 文件转Uri fileToUri()
     * android 7.0以上需要使用FileProvider
     * @return
     */
    public static Uri fileToUri(Context context,String filePath) {
        if (SystemUtil.getIsHigherThanAndroidN()) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(filePath));
        }
        return Uri.fromFile(new File(filePath));
    }
    //根据当前时间获得一个新文件的文件名称
    public static String getNewFileNameByTime(){
        return "picture_"+getNowTimeStr("yyyyMMdd_HHmmss");
    }
    //标准时间格式 yyyy-MM-dd HH:mm:ss
    private static String getNowTimeStr(String dateformat) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);// 可以方便地修改日期格式
        String hehe = dateFormat.format(now);
        return hehe;
    }
    //通过Document写入文本信息到文档
    public static void writeContentToDocument(Context context,Uri uri,String content) {
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().
                    openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            fileOutputStream.write(content.getBytes());
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileRealNameFromUri(Context context, Uri fileUri) {
        if (context == null || fileUri == null) return null;
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, fileUri);
        if (documentFile == null) return null;
        return documentFile.getName();
    }
    /**
     * 保存文本到downloads-存放路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getDownloadSaveTxt(Context context, String appName){
        if(SystemUtil.getIsHigherThanAndroidQ()){
            String path = null;
            if(TXT==null||TXT.equals("")){
                path = appName;
            }else{
                path = appName+ File.separator+TXT;
            }
            return path;
        }else{
            String path = null;
            if(TXT==null||TXT.equals("")){
                path = FileUtil.createFileDirectory(context,appName,4, Environment.DIRECTORY_DOWNLOADS)+ File.separator+appName+ File.separator;
            }else{
                path = FileUtil.createFileDirectory(context,appName,4, Environment.DIRECTORY_DOWNLOADS)+ File.separator+appName+ File.separator+TXT+File.separator;
            }
            File file = new File(path);
            if(!file.exists()||file.isFile()){
                file.mkdirs();
            }
            return path;
        }
    }
    /**
     * 保存图片到相册-存放路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getAlbumSaveImagesDir(Context context, String appName){
        if(SystemUtil.getIsHigherThanAndroidQ()){
            String path = null;
            if(PICTURES==null||PICTURES.equals("")){
                path = appName;
            }else{
                path = appName+ File.separator+PICTURES;
            }
            return path;
        }else{
            String path = null;
            if(PICTURES==null||PICTURES.equals("")){
                path = FileUtil.createFileDirectory(context,appName,4, Environment.DIRECTORY_DCIM)+ File.separator+appName+ File.separator;
            }else{
                path = FileUtil.createFileDirectory(context,appName,4, Environment.DIRECTORY_DCIM)+ File.separator+appName+ File.separator+PICTURES+File.separator;
            }
            File file = new File(path);
            if(!file.exists()||file.isFile()){
                file.mkdirs();
            }
            return path;
        }
    }
    /**
     * 保存图片到相册-存放路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getAlbumSaveVideoDir(Context context, String appName){
        if(SystemUtil.getIsHigherThanAndroidQ()){
            String path = null;
            if(MOVIES==null||MOVIES.equals("")){
                path = appName;
            }else{
                path = appName+ File.separator+MOVIES;
            }
            return path;
        }else{
            String path = null;
            if(MOVIES==null||MOVIES.equals("")){
                path = FileUtil.createFileDirectory(context,appName,4, Environment.DIRECTORY_DCIM)+ File.separator+appName+ File.separator;
            }else{
                path = FileUtil.createFileDirectory(context,appName,4, Environment.DIRECTORY_DCIM)+ File.separator+appName+ File.separator+MOVIES+File.separator;
            }
            File file = new File(path);
            if(!file.exists()||file.isFile()){
                file.mkdirs();
            }
            return path;
        }
    }
    /**
     * 保存Assets到缓存-存放路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getCacheSaveAssetsDir(Context context){
        String path = null;
        path = FileUtil.createFileDirectory(context,ASSETS,2, null)+ File.separator;
        File file = new File(path);
        if(!file.exists()||file.isFile()){
            file.mkdirs();
        }
        return path;
    }
    /**
     * 保存文本到缓存-存放路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getCacheSaveTxtDir(Context context){
        String path = null;
        path = FileUtil.createFileDirectory(context,TXT,1, null)+ File.separator;
        File file = new File(path);
        if(!file.exists()||file.isFile()){
            file.mkdirs();
        }
        return path;
    }
    /**
     * 保存图片到缓存-存放路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getCacheSaveImagesDir(Context context){
        String path = null;
        path = FileUtil.createFileDirectory(context,PICTURES,1, null);
        File file = new File(path);
        if(!file.exists()||file.isFile()){
            file.mkdirs();
        }
        return path;
    }
    /**
     * 保存视频到缓存-存放路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getCacheSaveMoviesDir(Context context){
        String path = null;
        path = FileUtil.createFileDirectory(context,PICTURES,1, null)+ File.separator;
        File file = new File(path);
        if(!file.exists()||file.isFile()){
            file.mkdirs();
        }
        return path;
    }

    /**
     * 保存文本到沙盒-存放路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getFilesSaveTxtDir(Context context){
        String path = null;
        path = FileUtil.createFileDirectory(context,TXT,2, null)+ File.separator;
        File file = new File(path);
        if(!file.exists()||file.isFile()){
            file.mkdirs();
        }
        return path;
    }
    /**
     * 保存图片到沙盒-存放路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getFilesSaveImageDir(Context context){
        String path = null;
        path = FileUtil.createFileDirectory(context,PICTURES,2, null)+ File.separator;
        File file = new File(path);
        if(!file.exists()||file.isFile()){
            file.mkdirs();
        }
        return path;
    }
    /**
     * 保存视频到沙盒-存放路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getFilesSaveMoviesDir(Context context){
        String path = null;
        path = FileUtil.createFileDirectory(context,MOVIES,2, null)+ File.separator;
        File file = new File(path);
        if(!file.exists()||file.isFile()){
            file.mkdirs();
        }
        return path;
    }
    /**
     * 保存文本到外部文件夹-存放路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getOuterSaveTxtDir(Context context,String appName){
        String path = null;
        if(TXT==null||TXT.equals("")){
            path = FileUtil.createFileDirectory(context,appName,3, null)+ File.separator;
        }else{
            path = FileUtil.createFileDirectory(context,appName+File.separator+TXT,3, null)+ File.separator;
        }        File file = new File(path);
        if(!file.exists()||file.isFile()){
            file.mkdirs();
        }
        return path;
    }
    /**
     * 保存图片到外部文件夹-存放路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getOuterSaveImageDir(Context context,String appName){
        String path = null;
        if(PICTURES==null||PICTURES.equals("")){
            path = FileUtil.createFileDirectory(context,appName,3, null)+ File.separator;
        }else{
            path = FileUtil.createFileDirectory(context,appName+File.separator+PICTURES,3, null)+ File.separator;
        }
        File file = new File(path);
        if(!file.exists()||file.isFile()){
            file.mkdirs();
        }
        return path;
    }
    /**
     * 保存视频到外部文件夹-存放路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getOuterSaveMoviesDir(Context context,String appName){
        String path = null;
        if(MOVIES==null||MOVIES.equals("")){
            path = FileUtil.createFileDirectory(context,appName,3, null)+ File.separator;
        }else{
            path = FileUtil.createFileDirectory(context,appName+File.separator+MOVIES,3, null)+ File.separator;
        }
        File file = new File(path);
        if(!file.exists()||file.isFile()){
            file.mkdirs();
        }
        return path;
    }
}

