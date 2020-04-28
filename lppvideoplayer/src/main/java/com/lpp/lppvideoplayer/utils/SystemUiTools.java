package com.lpp.lppvideoplayer.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;

public class SystemUiTools {
    @SuppressLint("RestrictedApi")
    public static void showStatusBar(Context context) {
        if (context instanceof Activity) {
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    //如果是沉浸式的，全屏前就没有状态栏
    @SuppressLint("RestrictedApi")
    public static void hideStatusBar(Context context) {
        if (context instanceof Activity) {
            ((Activity)context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @SuppressLint("NewApi")
    public static int hideSystemUI(Context context) {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        int systemUi = 0;
        if (context instanceof Activity){
            systemUi = ((Activity)context).getWindow().getDecorView().getSystemUiVisibility();
            ((Activity)context).getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
        return systemUi;
    }

    @SuppressLint("NewApi")
    public static void showSystemUI(Context context,int systemUi) {
        ((Activity)context).getWindow().getDecorView().setSystemUiVisibility(systemUi);
    }

    public static void showSystemUI(Context context){
        showSystemUI(context,View.SYSTEM_UI_FLAG_VISIBLE);
    }

}
