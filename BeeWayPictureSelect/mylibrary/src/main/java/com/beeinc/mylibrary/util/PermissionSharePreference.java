//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.beeinc.mylibrary.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PermissionSharePreference {
    private static final String tableName = "project_permission";
    private static Context context;

    public static void init(Context context) {
        PermissionSharePreference.context = context;
    }

    public static void saveBoolean(String key, boolean value) {
        SharedPreferences sharedPre = context.getSharedPreferences("project_permission", 0);
        SharedPreferences.Editor editor = sharedPre.edit();
        editor.putBoolean(key, value);
        editor.commit();
        editor.clear();
        sharedPre = null;
    }

    public static boolean getBoolean(String key, boolean value_default) {
        SharedPreferences sharedPre = context.getSharedPreferences("project_permission", 0);
        Boolean s = sharedPre.getBoolean(key, value_default);
        sharedPre = null;
        return s;
    }
}