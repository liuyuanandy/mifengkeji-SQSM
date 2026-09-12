package com.beeinc.SQSB;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import com.beeinc.mylibrary.MediaLoader;
import com.beeinc.mylibrary.util.PermissionSharePreference;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import java.util.Locale;

public class SQSBApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        PermissionSharePreference.init(this);
        initAlbum();
    }
    private void initAlbum(){
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .setLocale(Locale.getDefault())
                .build()
        );
    }
}
