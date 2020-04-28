package com.lpp.lppvideoplayer.utils;

import android.app.Activity;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

public class MyBrightnessUtil {
    /**
     * 设置当前activity的亮度
     *
     * @param activity
     * @param brightNum （0~255 其中255位最大值）
     */
    public static void setWindowBrightness(Activity activity, int brightNum) {
        if (brightNum <= 0){
            brightNum = 1;
        }else if (brightNum >= 255){
            brightNum = 255;
        }

        Window window = activity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = brightNum / 255f;
        window.setAttributes(layoutParams);
    }

    /**
     * 获取系统亮度
     *
     * @param activity
     * @return
     */
    public static int getWindowBrightness(Activity activity) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }
}
