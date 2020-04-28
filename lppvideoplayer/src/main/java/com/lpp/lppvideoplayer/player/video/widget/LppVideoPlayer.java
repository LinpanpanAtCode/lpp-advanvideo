package com.lpp.lppvideoplayer.player.video.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import com.lpp.lppvideoplayer.controller.widget.NormalVideoControllerView;
import com.lpp.lppvideoplayer.player.base.ablity.IMediaAblity;
import com.lpp.lppvideoplayer.player.base.widget.AbsVideoPlayer;
import com.lpp.lppvideoplayer.player.video.ijk.ablity.IjkVideoAblity;
import com.lpp.lppvideoplayer.player.video.ijk.listener.IJKMediaListener;
import com.lpp.lppvideoplayer.utils.audioUtils.IAudioFocusManagerListener;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

public class LppVideoPlayer extends AbsVideoPlayer implements IAudioFocusManagerListener {
    private static final String TAG = "lpp-ijk-VideoPlayer";
    private IjkVideoAblity mIjkMediaAblity;
    public LppVideoPlayer(Context context) {
        super(context);
        setBackgroundColor(context.getResources().getColor(android.R.color.black));
        addAudioFocusManagerListener(this);
    }

    public LppVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(context.getResources().getColor(android.R.color.black));
        addAudioFocusManagerListener(this);
    }

    public LppVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(context.getResources().getColor(android.R.color.black));
        addAudioFocusManagerListener(this);
    }

    @Override
    protected IMediaAblity createMediaAbilty() {
        mIjkMediaAblity = new IjkVideoAblity();
        return mIjkMediaAblity;
    }

    @Override
    public void onHeadSetPlug() {
        if (mMediaController instanceof NormalVideoControllerView){
            ((NormalVideoControllerView) mMediaController).pausedByUser();
        }
    }

    @Override
    public void onBluetoothConnectionStateChanged() {
        if (mMediaController instanceof NormalVideoControllerView){
            ((NormalVideoControllerView) mMediaController).pausedByUser();
        }
    }

    @Override
    public void onAudioBecomingNoisy() {
        if (mMediaController instanceof NormalVideoControllerView){
            ((NormalVideoControllerView) mMediaController).pausedByUser();
        }
    }

    @Override
    public void onAudioFocusGain() {
        onResume();
    }

    @Override
    public void onAudioFocusLoss() {
        onPause();
    }

    @Override
    public void onAudioFocusLossTransient() {
       onPause();
    }

    @Override
    public void onAudioFocusLossTransientCanDuck() {

    }

    @Override
    protected TextureView.SurfaceTextureListener createSurfaceHolderCallback() {
        final IJKMediaListener defaultListener = new IJKMediaListener() {
            @Override
            public void onAsyncListener(IjkMediaPlayer mediaPlayer) {

            }

            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
            }

            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {

            }

            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                return false;
            }

            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                return false;
            }

            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                setVideoWH(iMediaPlayer.getVideoWidth(), iMediaPlayer.getVideoHeight());
                // 直接取秒数代表最大progress
                // 当视频准备就绪的时候重新测量视频区域的宽高
                requestLayout();
            }

            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            }

            @Override
            public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {

            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            }
        };
        return new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if (mSurfaceTexture == null) {
                    mSurfaceTexture = surface;
                    if (mIjkMediaAblity != null) {
                        mIjkMediaAblity.setDisplay(new Surface(surface));
                    }
                    if (mIjkMediaAblity != null) {
                        mIjkMediaAblity.addMediaPlayerListener(defaultListener);
                    }
                } else {
                    mSurfaceView.setSurfaceTexture(mSurfaceTexture);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        };
    }

    @Override
    public void startPlay() {
        if (mMediaController != null){
            mMediaController.restart();
        }
    }

    public void setListener(IJKMediaListener listener) {
        if (mIjkMediaAblity != null) {
            mIjkMediaAblity.addMediaPlayerListener(listener);
        }
    }
}
