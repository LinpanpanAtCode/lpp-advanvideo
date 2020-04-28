package com.lpp.lppvideoplayer.controller.base;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.lpp.lppvideoplayer.player.base.ablity.IMediaAblity;

public abstract class AbsMediaControllView extends FrameLayout {
    protected IMediaAblity mMediaAblity;
    protected Context mContext;
    protected AudioManager.OnAudioFocusChangeListener mAudioFocusManagerListener;
    public AbsMediaControllView(Context context) {
        super(context);
        this.mContext = context;
    }

    public AbsMediaControllView(Context context, IMediaAblity iMediaAblity) {
        super(context);
        this.mContext = context;
        this.mMediaAblity = iMediaAblity;
    }
    public AbsMediaControllView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public AbsMediaControllView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    /**
     * 更换清晰度
     */
    public abstract void changeQuality(String url);

    public abstract void show();

    public abstract void hide();

    public abstract boolean isShowing();

    /**
     * 点击播放
     */
    public abstract void start();

    /**
     * 暂停
     */
    public abstract void pause();

    /**
     * 定点播放
     * @param position
     */
    public abstract void seekTo(long position);

    /**
     * 重新播放
     */
    public abstract void restart();

    /**
     * 更换url播放
     */
    public abstract void changeUrl(String newUrl);

    public abstract void setMaxProgess(int maxProgess);

    public abstract void setSecondProgress(int secondProgress);

    public abstract void setVideoTitle(String videoTitle);

    public abstract void updateProgress();

    public void onStart(){}
    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onDestroy() {
    }

    public void setAudioFocusManagerListener(AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener){
        this.mAudioFocusManagerListener = onAudioFocusChangeListener;
    }
}
