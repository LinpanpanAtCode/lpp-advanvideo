package com.lpp.lppvideoplayer.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class ScreenUtils {

  public static int getScreenHeight(@NonNull Context context) {
    if (!isAllScreenDevice(context)) {
      return getCommonScreenHeight(context);
    }
    return getScreenRealHeight(context);
  }

  /**
   * 获得屏幕高度
   * @param context
   * @return
   */
  public static int getScreenWidth(Context context) {
    WindowManager wm = (WindowManager) context
            .getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics outMetrics = new DisplayMetrics();
    wm.getDefaultDisplay().getMetrics(outMetrics);
    return outMetrics.widthPixels;
  }

  private static final int PORTRAIT = 0;
  private static final int LANDSCAPE = 1;
  @NonNull
  private volatile static Point[] mRealSizes = new Point[2];

  public static int getScreenRealHeight(@NonNull Context context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
      return getCommonScreenHeight(context);
    }

    int orientation = context.getResources().getConfiguration().orientation;
    orientation = orientation == Configuration.ORIENTATION_PORTRAIT ? PORTRAIT : LANDSCAPE;

    if (mRealSizes[orientation] == null) {
      WindowManager windowManager = (WindowManager) context
          .getSystemService(Context.WINDOW_SERVICE);
      if (windowManager == null) {
        return getCommonScreenHeight(context);
      }
      Display display = windowManager.getDefaultDisplay();
      Point point = new Point();
      display.getRealSize(point);
      mRealSizes[orientation] = point;
    }
    return mRealSizes[orientation].y;
  }

  private static int getCommonScreenHeight(@NonNull Context context) {
    if (context != null) {
      return context.getResources().getDisplayMetrics().heightPixels;
    }
    return 0;
  }

  private volatile static boolean mHasCheckAllScreen;
  private volatile static boolean mIsAllScreenDevice;

  public static boolean isAllScreenDevice(@NonNull Context context) {
    if (mHasCheckAllScreen) {
      return mIsAllScreenDevice;
    }
    mHasCheckAllScreen = true;
    mIsAllScreenDevice = false;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return false;
    }
    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    if (windowManager != null) {
      Display display = windowManager.getDefaultDisplay();
      Point point = new Point();
      display.getRealSize(point);
      float width, height;
      if (point.x < point.y) {
        width = point.x;
        height = point.y;
      } else {
        width = point.y;
        height = point.x;
      }
      if (height / width >= 1.97f) {
        mIsAllScreenDevice = true;
      }
    }
    return mIsAllScreenDevice;
  }

}