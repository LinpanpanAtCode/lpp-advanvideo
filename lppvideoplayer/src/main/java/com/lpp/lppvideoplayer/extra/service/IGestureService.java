package com.lpp.lppvideoplayer.extra.service;

public interface IGestureService {
    /**
     * 根据滑动的距离，滑动的方向，结合当前的亮度，计算滑动之后的亮度
     * @param distance
     * @param direction
     * @param currentLight
     * @return
     */
    int adjustLight(int distance, int direction ,int currentLight);
    /**
     * 手势调整seek的进度
     * 根据滑动的距离,滑动的方向，视频的总长度，当前的位置调整seek的位置
     * @param distance
     * @param direction
     * @param duration
     * @param position
     * @return
     */
    long calSeekPosition(int distance, int direction,int duration,int position);
    /**
     * 根据滑动的距离，滑动的方向，结合当前的音量，计算滑动之后的音量
     * @param distance
     * @param direction
     * @param currentLight
     * @return
     */
    int adjustVolume(int distance, int direction ,int currentLight);
}
