package com.example.test16k;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;


public class MainActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("-------->","MainActivity onCreate");
        setContentView(R.layout.activity_main);
    }
    static {
        System.loadLibrary("detection_based_tracker");
    }
}
