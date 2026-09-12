package com.beeinc.SQSB.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FileUtilImplement {
    public static boolean isHaveSdcard() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static String getSdCardPath(){
        if(!isHaveSdcard()){
            return null;
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }
    public static String formatBtyes(long length) {
        String result = null;
        StringBuilder sb = new StringBuilder();
        long G,M,K,B;
        G=M=K=B=0;
        if(length>=1024*1024*1024){//计算有多少G
            G = (int)(length/(1024*1024*1024));
        }
        long g_surplus = length%(1024*1024*1024);
        if(g_surplus>=1024*1024){//计算有多少兆
            M = (int)(g_surplus/(1024*1024));
        }
        long m_surplus = g_surplus%(1024*1024);
        if(m_surplus>=1024){//计算有多少兆
            K = (int)(m_surplus/1024);
        }
        B = (int)(m_surplus%1024);
        if(G>0){
            sb.append(G);
            sb.append("G");
        }
        if(M>0){
            sb.append(M);
            sb.append("M");
        }
        if(K>0){
            sb.append(K);
            sb.append("K");
        }
        if(B>0){
            sb.append(B);
            sb.append("B");
        }
        return sb.toString();
    }
    public static long getSDCardAllSize(){
        if (isHaveSdcard()){
            StatFs stat = new StatFs(getSdCardPath());
            // 获取数据块的数量
            long tatalBlocks= stat.getBlockCount();
//            long tatalBlocks= stat.getBlockCountLong();//本方法也可用
            // 获取单个数据块的大小（byte）
            long blockSize=stat.getBlockSize();
//            long blockSize=stat.getBlockSizeLong();//本方法也可用
            return blockSize * tatalBlocks;
        }
        return 0;
    }
    public static long freeSpaceOnSdcard() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
                .getPath());
        // 获取空闲数据块的数量
        long totalFreeBlocks= stat.getAvailableBlocks();
//            long tatalBlocks= stat.getBlockCountLong();//本方法也可用
        // 获取单个数据块的大小（byte）
        long blockSize=stat.getBlockSize();
//            long blockSize=stat.getBlockSizeLong();//本方法也可用
        long sdFree = totalFreeBlocks*blockSize;
        return (long) sdFree;
    }

    /**
     * @param  isReplace 是否覆盖文件
     */
    public static boolean createFile(String filePath,boolean isReplace) {
        File file = new File(filePath);
        if(file.exists()&&file.isFile()&&isReplace){
            file.delete();
        }
        if(!file.exists()){
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            return false;
        }
        return false;
    }

    /**
     * 创建Uri createUri()公共目录(android 10.0以上)
     * @param dir 如：save/picture
     * @param mimeType 如:MimeType.mp4；MimeType.jpg
     * @param publicType 如:
     * Environment.DIRECTORY_PICTURES，Environment.DIRECTORY_MUSIC,Environment.DIRECTORY_MOVIES
     * Environment.DIRECTORY_DCIM，Environment.DIRECTORY_DOWNLOADS
     */
    public static Uri createUri(Context context, String dir, String mimeType, String publicType, FileInfo fileInfo, boolean isReplace){
        switch(mimeType){
            //图片
            case MimeType.image_all:
            case MimeType.gif:
            case MimeType.jpg:
            case MimeType.png:
            case MimeType.webp:
            case MimeType.svg:
            case MimeType.tiff:
                //图片类型使用的公共目录只能是：Environment.DIRECTORY_DCIM或Environment.DIRECTORY_PICTURES
                return createAndroidQPublicDirImage(context,dir,mimeType,publicType,fileInfo);
            //文本
            case MimeType.txt:
                return createAndroidQPublicDirTXT(context,dir,mimeType,publicType,fileInfo);
            //音频
            case MimeType.audio_mp4:
            case MimeType.audio_mpeg:
            case MimeType.audio_ogg:
            case MimeType.audio_vorbis:
            case MimeType.audio_realaudio:
            case MimeType.audio_wav:
            case MimeType.audio_webm:
            case MimeType.audio_flac:
                return createAndroidQPublicDirMusic(context,dir,mimeType,publicType,fileInfo,isReplace);
            //视频
            case MimeType.mpeg:
            case MimeType.mp4:
            case MimeType.ogg:
            case MimeType.quicktime:
            case MimeType.webm:
            case MimeType.matroska:
            case MimeType.wmv:
            case MimeType.flv:
                return createAndroidQPublicDirVideo(context,dir,mimeType,publicType, fileInfo,isReplace);
        }
        return null;
    }

    private static Uri createAndroidQPublicDirTXT(Context context, String dir, String mimeType, String publicType, FileInfo fileInfo){
        long dateTaken = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileInfo.getFileName());//名称
        values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
        values.put(MediaStore.Downloads.TITLE, fileInfo.getFileName());
        values.put(MediaStore.Downloads.DATE_TAKEN, dateTaken);
        values.put(MediaStore.Downloads.DATE_ADDED, dateTaken/1000);
        values.put(MediaStore.DownloadColumns.DATE_MODIFIED, dateTaken/1000);
        values.put(MediaStore.Downloads.RELATIVE_PATH, publicType+File.separator+dir);
        Uri external = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            external = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        }
        ContentResolver resolver = context.getContentResolver();
        Uri insertUri = resolver.insert(external, values);
        return insertUri;
    }
    private static Uri createAndroidQPublicDirMusic(Context context, String dir, String mimeType, String publicType, FileInfo fileInfo,boolean isReplace){
        long dateTaken = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.TITLE, fileInfo.getFileName());
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileInfo.getFileName());
        values.put(MediaStore.Audio.Media.DATE_TAKEN, dateTaken);
        values.put(MediaStore.Audio.Media.DATE_ADDED, dateTaken/1000);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, dateTaken/1000);
        values.put(MediaStore.Audio.Media.MIME_TYPE, mimeType);
        values.put(MediaStore.Audio.Media.SIZE, fileInfo.getLength());
        values.put(MediaStore.Audio.Media.DURATION, fileInfo.getDuration());
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, publicType+File.separator+dir);
        ContentResolver contentResolver = context.getContentResolver();
        String VIDEO_BASE_URI = "content://media/external/audio/media";
        Uri videoTable = Uri.parse(VIDEO_BASE_URI);
        Uri insertUri = contentResolver.insert(videoTable, values);
        return insertUri;
    }
    private static Uri createAndroidQPublicDirImage(Context context, String dir, String mimeType, String publicType, FileInfo fileInfo){
        long dateTaken = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileInfo.getFileName());//图片名称
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
        values.put(MediaStore.Images.Media.DATE_ADDED, dateTaken/1000);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, dateTaken/1000);
        values.put(MediaStore.Images.Media.TITLE, fileInfo.getFileName());
        values.put(MediaStore.Images.Media.RELATIVE_PATH, publicType+File.separator+dir);
        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        Uri insertUri = resolver.insert(external, values);
        return insertUri;
    }
    private static Uri createAndroidQPublicDirVideo(Context context, String dir, String mimeType, String publicType, FileInfo fileInfo,boolean isReplace){
        long dateTaken = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, fileInfo.getFileName());
        values.put(MediaStore.Video.Media.DESCRIPTION, "这是一个视频文件");
        values.put(MediaStore.Video.Media.DISPLAY_NAME, fileInfo.getFileName());
        values.put(MediaStore.Video.Media.DATE_TAKEN, dateTaken);
        values.put(MediaStore.Video.Media.DATE_ADDED, dateTaken/1000);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, dateTaken/1000);
        values.put(MediaStore.Video.Media.MIME_TYPE, mimeType);
        values.put(MediaStore.Video.Media.WIDTH, fileInfo.getWidth());
        values.put(MediaStore.Video.Media.HEIGHT, fileInfo.getHeight());
        values.put(MediaStore.Video.Media.RESOLUTION, fileInfo.getWidth()+ "x" + fileInfo.getHeight());
        values.put(MediaStore.Video.Media.SIZE, fileInfo.getLength());
        values.put(MediaStore.Video.Media.DURATION, fileInfo.getDuration());
        values.put(MediaStore.Video.Media.RELATIVE_PATH, publicType+File.separator+dir);
        ContentResolver contentResolver = context.getContentResolver();
        Uri external = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Uri insertUri = contentResolver.insert(external, values);
        return insertUri;
    }
    //调用SAF框架，创建文件
    public static void createFileBySAF(Activity context, String mimeType, String fileName,int requestCode) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Create a file with the requested MIME type.
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        context.startActivityForResult(intent, requestCode);
    }
    //删除文件 deleteFile();
    public static boolean deleteFile(File file) {
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    //删除文件 deleteFile();
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean deleteUri(Context context, Uri uri){
        try {
            return DocumentsContract.deleteDocument(context.getContentResolver(), uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * android 10以下任意目录互拷。
     * @param fromFilePath 源文件
     * @param toFilePath 目标文件
     * @param reWrite 覆盖同名文件
     * @param isDeletFrom 是否删除源文件
     */
    public static boolean copyFileToFile(Context context,String fromFilePath,String toFilePath,boolean reWrite,boolean isDeletFrom){
        if(SystemUtil.getIsHigherThanAndroidN()){
            try {
                ParcelFileDescriptor parcelFileDescriptor = null;
                Uri uri = FileUtil.fileToUri(context,fromFilePath);
                parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                File toFile = new File(toFilePath);
                if (!toFile.getParentFile().exists()) {
                    toFile.getParentFile().mkdirs();
                }
                if (toFile.exists() && reWrite) {
                    toFile.delete();
                }
                try {
                    FileInputStream fosfrom = new FileInputStream(fileDescriptor);
                    FileOutputStream fosto = new FileOutputStream(toFile);
                    byte bt[] = new byte[1024];
                    int c;
                    while ((c = fosfrom.read(bt)) > 0) {
                        fosto.write(bt, 0, c); //将内容写到新文件当中
                    }
                    fosfrom.close();
                    fosto.close();
                    if(isDeletFrom){
                        FileUtil.deleteUri(context,uri);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }else{
            File fromFile = new File(fromFilePath);
            File toFile = new File(toFilePath);
            if (!fromFile.exists()||!fromFile.isFile()||!fromFile.canRead()) {
                return false;
            }
            if (!toFile.getParentFile().exists()) {
                toFile.getParentFile().mkdirs();
            }
            if (toFile.exists() && reWrite) {
                toFile.delete();
            }
            try {
                FileInputStream fosfrom = new FileInputStream(fromFile);
                FileOutputStream fosto = new FileOutputStream(toFile);
                byte bt[] = new byte[1024];
                int c;
                while ((c = fosfrom.read(bt)) > 0) {
                    fosto.write(bt, 0, c); //将内容写到新文件当中
                }
                fosfrom.close();
                fosto.close();
                if(isDeletFrom){
                    fromFile.delete();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
    /**
     * 拷贝文件到Uri
     */
    public static boolean copyFileToUri(Context context,String fromFilePath,Uri toUri,boolean isDeletFrom){
        if(SystemUtil.getIsHigherThanAndroidN()){
            try {
                Uri uri = FileUtil.fileToUri(context,fromFilePath);
                ParcelFileDescriptor fromParcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
                FileDescriptor fromFileDescriptor = fromParcelFileDescriptor.getFileDescriptor();
                ParcelFileDescriptor toParcelFileDescriptor = context.getContentResolver().openFileDescriptor(toUri, "rw");
                FileDescriptor toFileDescriptor = toParcelFileDescriptor.getFileDescriptor();
                try {
                    FileInputStream fosfrom = new FileInputStream(fromFileDescriptor);
                    FileOutputStream fosto = new FileOutputStream(toFileDescriptor);
                    byte bt[] = new byte[1024];
                    int c;
                    while ((c = fosfrom.read(bt)) > 0) {
                        fosto.write(bt, 0, c); //将内容写到新文件当中
                    }
                    fosfrom.close();
                    fosto.close();
                    if(isDeletFrom){
                        FileUtil.deleteUri(context,uri);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }else{
            File fromFile = new File(fromFilePath);
            ContentResolver resolver = context.getContentResolver();
            if (!fromFile.exists()||!fromFile.isFile()||!fromFile.canRead()) {
                return false;
            }
            try {
                FileInputStream fosfrom = new FileInputStream(fromFile);
                OutputStream fosto = resolver.openOutputStream(toUri);
                byte bt[] = new byte[1024];
                int c;
                while ((c = fosfrom.read(bt)) > 0) {
                    fosto.write(bt, 0, c); //将内容写到新文件当中
                }
                fosfrom.close();
                fosto.close();
                if(isDeletFrom){
                    fromFile.delete();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
    /**
     * 拷贝Uri 到文件
     * android 10.0以上，将外部文件拷贝到 缓存或沙盒
     */
    public static void copyUriToFile(Context context,Uri fromUri,String toFilePath,boolean isRewrite){
        try {
            ParcelFileDescriptor parcelFileDescriptor = null;
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(fromUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            File toFile = new File(toFilePath);
            if (!toFile.getParentFile().exists()) {
                toFile.getParentFile().mkdirs();
            }
            if (toFile.exists() && isRewrite) {
                toFile.delete();
            }
            try {
                FileInputStream fosfrom = new FileInputStream(fileDescriptor);
                FileOutputStream fosto = new FileOutputStream(toFile);
                byte bt[] = new byte[1024];
                int c;
                while ((c = fosfrom.read(bt)) > 0) {
                    fosto.write(bt, 0, c); //将内容写到新文件当中
                }
                fosfrom.close();
                fosto.close();
            } catch (Exception ex) {
                Log.e("readfile", ex.getMessage());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 拷贝Uri 到Uri
     * android 10.0以上，将外部文件拷贝到 公共目录
     */
    public static void copyUriToUri(Context context,Uri fromUri,Uri toUri,boolean isReWirte,boolean isDeleteFrom){
        try {
            ParcelFileDescriptor fromParcelFileDescriptor = context.getContentResolver().openFileDescriptor(fromUri, "r");
            FileDescriptor fromFileDescriptor = fromParcelFileDescriptor.getFileDescriptor();
            ParcelFileDescriptor toParcelFileDescriptor = context.getContentResolver().openFileDescriptor(toUri, "rw");
            FileDescriptor toFileDescriptor = toParcelFileDescriptor.getFileDescriptor();
            try {
                FileInputStream fosfrom = new FileInputStream(fromFileDescriptor);
                FileOutputStream fosto = new FileOutputStream(toFileDescriptor);
                byte bt[] = new byte[1024];
                int c;
                while ((c = fosfrom.read(bt)) > 0) {
                    fosto.write(bt, 0, c); //将内容写到新文件当中
                }
                fosfrom.close();
                fosto.close();
            } catch (Exception ex) {
                Log.e("readfile", ex.getMessage());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }
        return size;
    }
    public static void saveTextToFile(String filePath, String filecontent) {
        File file = new File(filePath);
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            outStream.write(filecontent.getBytes());
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveTextToUri(Context context,Uri uri, String filecontent) {
        FileOutputStream outStream = null;
        try {
            outStream = (FileOutputStream) context.getContentResolver().openOutputStream(uri);
            outStream.write(filecontent.getBytes());
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String readTextFile(String filePath){
        String str;
        File file = new File(filePath);
        try {
            FileInputStream in = new FileInputStream(file);
            // size  为字串的长度 ，这里一次性读完
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            str = new String(buffer, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return str;
    }
    public static String readTextFromUri(Context context,Uri uri){
        String str;
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        try {
            FileInputStream in = new FileInputStream(fileDescriptor);
            // size  为字串的长度 ，这里一次性读完
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            str = new String(buffer, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return str;
    }
    public static String readTextFromUriWay2(Context context,Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        inputStream.close();
        return stringBuilder.toString();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String createFileDirectory(@NonNull Context context, String dir, @NonNull int type, String publicDirType){
        switch (type){
            case 1:
                return FileUtilImplement.createFileDirectoryCache(context,dir);
            case 2:
                return createFileDirectoryFiles(context,dir,publicDirType);
            case 3:
                return createFileDirectory(context,dir);
            case 4:
                return createPublicDirectory(context,dir,publicDirType);
            default:
            break;
        }
        return null;
    }
    /*
     * 创建缓存文件夹  createFileDirectoryCache()
     * android 11前用户可见，可访问,android 11后用户不可见，不可访问
     * 真实路径：/Android/data/应用包名/cache 卸载时被删除，不需要权限
     * @param dir 如：save/picture
     */
    private static String createFileDirectoryCache(Context context, String dir) {
        String path;
        if (isHaveSdcard()) {
            if(dir==null||dir.equals("")){
                path = context.getExternalCacheDir()+File.separator;
            }else{
                path = context.getExternalCacheDir()+File.separator + dir + File.separator;
            }
            File file = new File(path);
            //如果文件夹不存在则创建
            if (!file.exists() ||!file.isDirectory()) {
                file.mkdirs();
            }
        } else {
            if(dir==null||dir.equals("")){
                path = context.getCacheDir()+File.separator;
            }else{
                path = context.getCacheDir()+File.separator + dir + File.separator;
            }
            File file = new File(path);
            //如果文件夹不存在则创建
            if (!file.exists() ||!file.isDirectory()) {
                file.mkdirs();
            }
        }
        return path;
    }
    /*
     * 创建沙河文件夹  createFileDirectoryFiles()
     * 用户不可见，不能访问
     * 真实路径：/Android/data/应用包名/files 卸载时被删除，不需要权限
     * context.getExternalFilesDir(publicDirType) publicDirType 可以传null
     * context.getFilesDir() /data/user/0/com.test.fileprocessingdemo/files/
     * @param dir 如：save/picture
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String createFileDirectoryFiles(Context context, String dir,String publicDirType) {
        String path;
        if(isHaveSdcard()){
            File filesDir = context.getExternalFilesDir(publicDirType);
            if(dir==null||dir.equals("")){
                path = filesDir.getAbsolutePath()+File.separator;
            }else{
                path = filesDir.getAbsolutePath()+File.separator + dir + File.separator;
            }
            File file = new File(path);
            //如果文件夹不存在则创建
            if (!file.exists() ||!file.isDirectory()) {
                file.mkdirs();
            }
        }else{
            if(dir==null||dir.equals("")){
                path = context.getFilesDir()+File.separator;
            }else{
                path = context.getFilesDir()+File.separator + dir + File.separator;
            }
            File file = new File(path);
            //如果文件夹不存在则创建
            if (!file.exists() ||!file.isDirectory()) {
                file.mkdirs();
            }
        }
        return path;
    }
    /*
     * 创建文件夹 createFileDirectory();
     * @param dir 如：save/picture
     * android 10.0以后不可用
     */
    private static String createFileDirectory(Context context, String dir) {
        String directorypath;
        if(isHaveSdcard()){
            directorypath = Environment.getExternalStorageDirectory() + File.separator+dir + File.separator;
            File file = new File(directorypath);
            //如果文件夹不存在则创建
            if (!file.exists() || !file.isDirectory()) {
                file.mkdirs();
            }
        }else{
            directorypath = Environment.getDataDirectory() +File.separator+ dir +File.separator;
            File file = new File(directorypath);
            //如果文件夹不存在则创建
            if (!file.exists() || !file.isDirectory()) {
                file.mkdirs();
            }
        }
        return directorypath;
    }
    /**
     * 获取公共目录路径(相册，音乐，影视，下载，相机文件夹等) getSystemPublicDirectory()
     * @param context
     * @param dir 如：save/picture
     * @param pubicType
     * Environment.DIRECTORY_PICTURES，Environment.DIRECTORY_MUSIC,Environment.DIRECTORY_MOVIES
     * Environment.DIRECTORY_DCIM，Environment.DIRECTORY_DOWNLOADS
     * @return
     */

    public static String createPublicDirectory(Context context,String dir, String pubicType){
        String directoryPath = Environment.getExternalStoragePublicDirectory(pubicType).getAbsolutePath()+File.separator+dir+File.separator;
        File file = new File(directoryPath);
        //如果文件夹不存在则创建
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return directoryPath;
    }
    public static void deleteFileDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if (item.isDirectory()) {
                    deleteFileDirectory(item);
                } else {
                    deleteFile(item);
                }
            }
        }
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
     * 保存bitmap到文件 saveBitmapToFile()
     * android 10以下可用
     * @param path 文件保存路径
     * @param bm   bitmap
     * @param compressionRatio 质量压缩百分比 如：90 是压缩率，表示压缩10%; 如果不压缩是100，表示压缩率为0
     */
    public static void saveBitmapToFile(String path, Bitmap bm,int compressionRatio) {
        Log.e("----------->","path = "+path);
        Log.e("Bitmap", "开始保存");
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, compressionRatio, out);
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
    /**
     * 保存bitmap到文件 saveBitmapToFile()
     * @param uri 文件保存路径
     * @param bm   bitmap
     * @param compressionRatio 质量压缩百分比 如：90 是压缩率，表示压缩10%; 如果不压缩是100，表示压缩率为0
     */
    public static boolean saveBitmapToUri(Context context,Uri uri, Bitmap bm,int compressionRatio) {
        OutputStream outputStream = null;
        try {
            outputStream = context.getContentResolver().openOutputStream(uri,"rw");
            bm.compress(Bitmap.CompressFormat.JPEG, compressionRatio, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //通过URi 获取文件真实路径 仅限媒体文件和应用自身创建的文件
    public static String getRealPathFromURI(Context context, Uri uri) {
        String path = null;
        Log.e("------------->","uri.getScheme()="+uri.getScheme());
        Log.e("------------->","ContentResolver.SCHEME_FILE="+ContentResolver.SCHEME_FILE);
        //file: 开头的
        if(ContentResolver.SCHEME_FILE.equals(uri.getScheme())){
            path = uri.getPath();
        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        }else if(ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())){
            //android 4.4 以下
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
                Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        if (columnIndex > -1) {
                            path = cursor.getString(columnIndex);
                        }
                    }
                    cursor.close();
                }
            //android 4.4 以上
            }else{
                if (DocumentsContract.isDocumentUri(context, uri)) {
                    if (isExternalStorageDocument(uri)) {
                        // ExternalStorageProvider
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];
                        if ("primary".equalsIgnoreCase(type)) {
                            path = Environment.getExternalStorageDirectory() + "/" + split[1];
                        }
                    } else if (isDownloadsDocument(uri)) {
                        // DownloadsProvider
                        final String id = DocumentsContract.getDocumentId(uri);
                        final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                                Long.valueOf(id));
                        path = getDataColumn(context, contentUri, null, null);
                    } else if (isMediaDocument(uri)) {
                        // MediaProvider
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];
                        Uri contentUri = null;
                        if ("image".equals(type)) {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(type)) {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(type)) {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }
                        final String selection = "_id=?";
                        final String[] selectionArgs = new String[]{split[1]};
                        path = getDataColumn(context, contentUri, selection, selectionArgs);
                    }
                }else if ("content".equals(uri.getScheme())){
                    if(isGooglePhotosUri(uri)){
                        return uri.getLastPathSegment();
                    }else if(isCachesUri(uri)){
                        String cachePath = createFileDirectory(context,null,1,null);
                        return uri.getPath().replace("/external-cache-path/",cachePath);
                    }else if(isFilesUri(uri)){
                        String cachePath = createFileDirectory(context,null,2,null);
                        return uri.getPath().replace("/filePath/",cachePath);
                    }else{
                        return getDataColumn(context, uri, null, null);
                    }
                }
            }
        }
        return null;
    }
    //获取文件真实路径
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    //通过URi 获取文件真实路径
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    //通过URi 获取文件真实路径
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    //通过URi 获取文件真实路径
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri){
        return "com.google.android.apps.photos.content" == uri.getAuthority();
    }
    /**
     * 缓存文件
     * @param uri The Uri to check.
     */
    private static boolean isCachesUri(Uri uri){
        return uri.getPath().startsWith("/external-cache-path/");
    }
    /**
     * 沙盒文件
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isFilesUri(Uri uri){
        return uri.getPath().startsWith("/filePath/");
    }



//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is Google Photos.
//     */
//    private static boolean isCachesUri(Uri uri){
//        return uri.getPath().startsWith("/external-cache-path/");
//    }

}
