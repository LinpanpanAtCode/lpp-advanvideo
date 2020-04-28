package com.lpp.lppvideoplayer.player.base.ablity;

import java.io.IOException;

/**
 * 播放器能力层接口
 */
public interface IMediaAblity{
    /**
     * 定点播放
     * @param position
     */
    void seekTo(long position);

    /**
     * 获取视频总时长
     */
    long getDuration();

    /**
     * 获取当前播放位置
     * @return
     */
    long getCurrentPosition();

    /**
     * 开始播放
     */
    void start();

    /**
     * 重新播放
     */
    void restart();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 获取视频的宽度
     * @return
     */
    int getVideoWidth();

    /**
     * 获取视频的高度
     * @return
     */
    int getVideoHeight();

    /**
     * 判断是否是否正在播放
     * @return
     */
    boolean isPlaying();

    /**
     * 判断是否准备就绪
     * @return
     */
    boolean isPrepared();
    /**
     * 释放播放器及其资源
     */
    void release();

    /**
     * 设置播放器链接
     */
    void setUrl(String url) throws Exception;

    /**
     * 更换播放链接
     * @param url
     * @throws IOException
     */
    void changeUrl(String url) throws IOException;

    /**
     * 更换清晰度
     * @param url
     * @throws IOException
     */
    void changeQuality(String url) throws IOException;
}
