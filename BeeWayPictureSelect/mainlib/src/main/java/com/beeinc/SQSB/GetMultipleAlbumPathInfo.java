package com.beeinc.SQSB;

public class GetMultipleAlbumPathInfo {
    private String saveDir;
    private int imageNum;
    private String types;
    public GetMultipleAlbumPathInfo(String saveDir, int imageNum, String types){
        this.saveDir = saveDir;
        this.imageNum = imageNum;
        this.types = types;
    }
    public String getSaveDir() {
        return saveDir;
    }

    public int getImageNum() {
        return imageNum;
    }

    public String getTypes() {
        return types;
    }
}
