package com.lpp.lppvideoplayer.player.video.config;

import com.lpp.lppvideoplayer.player.base.config.IMediaItemBuilder;

public class VideoBuilder implements IMediaItemBuilder {
    private String url;
    private String videoTitle;
    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getVideoTitle() {
        return videoTitle;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }
}
