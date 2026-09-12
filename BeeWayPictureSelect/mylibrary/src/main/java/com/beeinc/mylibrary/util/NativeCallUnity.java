package com.beeinc.mylibrary.util;

import com.unity3d.player.UnityPlayer;

public class NativeCallUnity {
    public static void GetMultipleAlbumPathFinish(String fileInfos){
        UnityPlayer.UnitySendMessage("IOsReciveObj","GetMultipleAlbumPathFinish",fileInfos);
    }

}
