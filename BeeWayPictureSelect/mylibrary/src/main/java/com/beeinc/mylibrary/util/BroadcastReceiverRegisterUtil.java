//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.beeinc.mylibrary.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build.VERSION;

public class BroadcastReceiverRegisterUtil {
    public static void registerReceiver(Context context, BroadcastReceiver receiver, IntentFilter filter, int IS_RECEIVER_EXPORTED) {
        if (VERSION.SDK_INT >= 26) {
            context.registerReceiver(receiver, filter, IS_RECEIVER_EXPORTED);
        } else {
            context.registerReceiver(receiver, filter);
        }

    }

    public static void unRegisterReceiver(Context context, BroadcastReceiver receiver) {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            BroadcastReceiver var2 = null;
        }

    }
}
