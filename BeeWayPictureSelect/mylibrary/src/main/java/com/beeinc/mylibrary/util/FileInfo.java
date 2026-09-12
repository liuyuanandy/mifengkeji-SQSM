package com.beeinc.mylibrary.util;

public class FileInfo {
    private String fileName;//文件名
    private long length;//文件长度
    private long duration;//文件时长 音频/视频文件
    private int width;//文件宽度     图片/视频
    private int height;//文件高度    图片/视频

    private FileInfo(){
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    //创建文本文件需要的信息
    public static FileInfo createTxtFileInfo(String fileName){
        FileInfo fileInfo = new FileInfo();
        fileInfo.fileName = fileName;
        return fileInfo;
    }
    //创建图片文件需要的信息
    public static FileInfo createImageFileInfo(String fileName){
        FileInfo fileInfo = new FileInfo();
        fileInfo.fileName = fileName;
        return fileInfo;
    }
    //创建音频文件需要的信息
    public static FileInfo createAudioFileInfo(String fileName, long duration, long length){
        FileInfo fileInfo = new FileInfo();
        fileInfo.fileName = fileName;
        fileInfo.duration = duration;
        fileInfo.length = length;
        return fileInfo;
    }
    //创建视频文件需要的信息
    public static FileInfo createVideoFileInfo(String fileName, long duration, long length, int width, int height){
        FileInfo fileInfo = new FileInfo();
        fileInfo.fileName = fileName;
        fileInfo.duration = duration;
        fileInfo.length = length;
        fileInfo.width = width;
        fileInfo.height = height;
        return fileInfo;
    }
}
