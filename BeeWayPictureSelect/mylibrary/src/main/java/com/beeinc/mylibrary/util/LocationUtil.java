//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.beeinc.mylibrary.util;

import android.content.Context;
import android.location.LocationManager;

public class LocationUtil {
    public static boolean isLocServiceEnable(Context context) {

        LocationManager locationManager = (LocationManager)context.getSystemService("location");
        boolean gps = locationManager.isProviderEnabled("gps");
        boolean network = locationManager.isProviderEnabled("network");
        return gps || network;
    }
}
