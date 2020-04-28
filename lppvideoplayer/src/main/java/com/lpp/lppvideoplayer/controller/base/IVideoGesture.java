package com.lpp.lppvideoplayer.controller.base;

import android.view.View;

/**
 * 视频手势相关的接口
 */
public interface IVideoGesture {
    /**
     * 定点播放
     * @param position
     */
    void seekTo(long position);

    /**
     * 双击暂停或者继续
     * @param v
     */
    void pauseOrContinue(View v);

    /**
     * 设置音量
     * @param volume
     */
    void setVolume(int volume);

    /**
     * 设置亮度
     * @param brightness
     */
    void setBrightness(int brightness);
}
