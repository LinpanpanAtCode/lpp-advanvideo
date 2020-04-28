package com.lpp.lppvideoplayer.player.video.ijk.ablity;

import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.lpp.lppvideoplayer.player.base.ablity.IMediaAblity;
import com.lpp.lppvideoplayer.player.video.ijk.listener.IJKMediaListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

public class IjkMediaAblity implements IMediaAblity {
    protected IjkMediaPlayer mIjkMediaPlayer;
    private static final String TAG = "lpp-ijkmediaablity";
    private boolean mIsPrepared;
    private List<IJKMediaListener> mIjkMediaListenerList;
    private boolean mIsCompleted = false;
    private String mUrl;
    private Surface mSurface;
    private boolean isNeedSeekWhenPrepared = false;
    private long seekPositionWhenPrepared = 0;
    public IjkMediaAblity() {
        mIjkMediaPlayer = createPlayer();
        setListener();
//        ijkMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
        mIjkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void setListener() {
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    Log.e(TAG, "onPrepared");
                    mIsPrepared = true;
                    mIsCompleted = false;
                    if (mIjkMediaListenerList != null && mIjkMediaListenerList.size() > 0) {
                        for (int pos = 0 ;pos < mIjkMediaListenerList.size() ; pos++){
                            IJKMediaListener ijkMediaListener = mIjkMediaListenerList.get(pos);
                            ijkMediaListener.onPrepared(iMediaPlayer);
                        }
                    }
                    iMediaPlayer.start();
                    if (isNeedSeekWhenPrepared){
                        seekTo(seekPositionWhenPrepared);
                    }
                }
            });
            mIjkMediaPlayer.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
                    Log.e(TAG, "onBufferingUpdate:" + i);
                    if (mIjkMediaListenerList != null && mIjkMediaListenerList.size() > 0) {
                        for (int pos = 0 ;pos < mIjkMediaListenerList.size() ; pos++){
                            IJKMediaListener ijkMediaListener = mIjkMediaListenerList.get(pos);
                            ijkMediaListener.onBufferingUpdate(iMediaPlayer, i);
                        }
                    }
                }
            });
            mIjkMediaPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer iMediaPlayer) {
                    Log.e(TAG, "onCompletion:");
                    mIsCompleted = true;
                    if (mIjkMediaListenerList != null && mIjkMediaListenerList.size() > 0) {
                        for (int pos = 0 ;pos < mIjkMediaListenerList.size() ; pos++){
                            IJKMediaListener ijkMediaListener = mIjkMediaListenerList.get(pos);
                            ijkMediaListener.onCompletion(iMediaPlayer);
                        }
                    }
                }
            });
            mIjkMediaPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                    Log.e(TAG, "onError:var1:" + i + ",var2:" + i1);
                    if (mIjkMediaListenerList != null && mIjkMediaListenerList.size() > 0) {
                        for (int pos = 0 ;pos < mIjkMediaListenerList.size() ; pos++){
                            IJKMediaListener ijkMediaListener = mIjkMediaListenerList.get(pos);
                            ijkMediaListener.onError(iMediaPlayer, i, i1);
                        }
                    }
                    return false;
                }
            });
            mIjkMediaPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                    Log.e(TAG, "onInfo:var1:" + i + ",var2:" + i1);
                    if (mIjkMediaListenerList != null && mIjkMediaListenerList.size() > 0) {
                        for (int pos = 0 ;pos < mIjkMediaListenerList.size() ; pos++){
                            IJKMediaListener ijkMediaListener = mIjkMediaListenerList.get(pos);
                            ijkMediaListener.onInfo(iMediaPlayer, i, i1);
                        }
                    }
                    return false;
                }
            });
            mIjkMediaPlayer.setOnTimedTextListener(new IMediaPlayer.OnTimedTextListener() {
                @Override
                public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {
                    Log.e(TAG, "onTimedText:" + ijkTimedText.getText());
                    if (mIjkMediaListenerList != null && mIjkMediaListenerList.size() > 0) {
                        for (int pos = 0 ;pos < mIjkMediaListenerList.size() ; pos++){
                            IJKMediaListener ijkMediaListener = mIjkMediaListenerList.get(pos);
                            ijkMediaListener.onTimedText(iMediaPlayer, ijkTimedText);
                        }
                    }

                }
            });
            mIjkMediaPlayer.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
                    Log.e(TAG, "onVideoSizeChanged:" + i + ":" + i1 + ":" + i2 + ":" + i3);
                    if (mIjkMediaListenerList != null && mIjkMediaListenerList.size() > 0) {
                        for (int pos = 0 ;pos < mIjkMediaListenerList.size() ; pos++){
                            IJKMediaListener ijkMediaListener = mIjkMediaListenerList.get(pos);
                            ijkMediaListener.onVideoSizeChanged(iMediaPlayer, i, i1, i2, i3);
                        }
                    }
                }
            });
            mIjkMediaPlayer.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                    Log.e(TAG, "onSeekComplete");
                    if (mIjkMediaListenerList != null && mIjkMediaListenerList.size() > 0) {
                        for (int pos = 0 ;pos < mIjkMediaListenerList.size() ; pos++){
                            IJKMediaListener ijkMediaListener = mIjkMediaListenerList.get(pos);
                            ijkMediaListener.onSeekComplete(iMediaPlayer);
                        }
                    }
                }
            });
        }
    }
    public void setDisplay(Surface surface) {
        if (surface != null && mIjkMediaPlayer != null) {
            mSurface = surface;
            mIjkMediaPlayer.setSurface(mSurface);
        }
    }

    //创建一个ijk的player
    private IjkMediaPlayer createPlayer() {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        ////1为硬解 0为软解
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        //使用opensles把文件从java层拷贝到native层
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        //视频格式
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        //跳帧处理（-1~120）。CPU处理慢时，进行跳帧处理，保证音视频同步
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        //0为一进入就播放,1为进入时不播放
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        ////域名检测
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        //设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        //最大缓冲大小,单位kb
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 1024 * 1024);
        //某些视频在SeekTo的时候，会跳回到拖动前的位置，这是因为视频的关键帧的问题，通俗一点就是FFMPEG不兼容，视频压缩过于厉害，seek只支持关键帧，出现这个情况就是原始的视频文件中i 帧比较少
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        //是否重连
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 3);
        //http重定向https
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
        //设置seekTo能够快速seek到指定位置并播放
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek");
        //播放前的探测Size，默认是1M, 改小一点会出画面更快
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024 * 10);
        ijkMediaPlayer.setVolume(1.0f, 1.0f);
        setEnableMediaCodec(ijkMediaPlayer, true);
        return ijkMediaPlayer;
    }

    private IjkConfig configIjk() {
        IjkConfig ijkConfig = new IjkConfig();
        ijkConfig.opensles = 1;
        ijkConfig.overlayFormat = IjkMediaPlayer.SDL_FCC_RV32;
        ijkConfig.framedrop = 1;
        ijkConfig.startOnPrepared = 0;
        ijkConfig.httpDetectRangeSupport = 1;
//        ijkConfig.skipLoopFilter = 48;
        ijkConfig.skipLoopFilter = 0;
        ijkConfig.minFrames = 100;
        ijkConfig.enableAccurateSeek = 1;
        ijkConfig.reconnect = 3;
        ijkConfig.isPacketBuffering = false;
        ijkConfig.isDnsCacheClear = false;
        ijkConfig.isEnableMediaCodec = true;
        return ijkConfig;
    }

    //设置是否开启硬解码
    private void setEnableMediaCodec(IjkMediaPlayer ijkMediaPlayer, boolean isEnable) {
        int value = isEnable ? 1 : 0;
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, IjkConfig.MEDIACODEC, value);//开启硬解码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, IjkConfig.MEDIACODEC_AUTO_ROTATE, value);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, IjkConfig.MEDIACODEC_HANDLE_RESOLUTION_CHANGE, value);
    }

    @Override
    public void seekTo(long position) {
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.seekTo(position);
        }
    }

    @Override
    public long getDuration() {
        return mIjkMediaPlayer == null ? 0 : mIjkMediaPlayer.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        return mIjkMediaPlayer == null ? 0 : mIjkMediaPlayer.getCurrentPosition();
    }

    @Override
    public void start() {
        if (mIsPrepared) {
            if (mIsCompleted) {
                restart();
            } else {
                mIjkMediaPlayer.start();
            }
        } else {
            restart();
        }
    }

    @Override
    public void restart() {
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.reset();
            mIjkMediaPlayer.release();
            mIjkMediaPlayer = null;
        }
        mIjkMediaPlayer = createPlayer();
        setListener();
        setDisplay(mSurface);
        mIjkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mIjkMediaPlayer.setDataSource(mUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mIjkMediaPlayer.prepareAsync();
        if (mIjkMediaListenerList != null && mIjkMediaListenerList.size() > 0) {
            for (int pos = 0 ;pos < mIjkMediaListenerList.size() ; pos++){
                IJKMediaListener ijkMediaListener = mIjkMediaListenerList.get(pos);
                ijkMediaListener.onAsyncListener(mIjkMediaPlayer);
            }
        }
    }

    @Override
    public void pause() {
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.pause();
        }
    }

    @Override
    public void stop() {
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.stop();
        }
    }

    @Override
    public int getVideoWidth() {
        return mIjkMediaPlayer == null ? 0 : mIjkMediaPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mIjkMediaPlayer == null ? 0 : mIjkMediaPlayer.getVideoHeight();
    }

    @Override
    public boolean isPlaying() {
        return mIjkMediaPlayer == null ? false : mIjkMediaPlayer.isPlaying();
    }

    @Override
    public boolean isPrepared() {
        return mIsPrepared;
    }

    @Override
    public void release() {
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.release();
        }
    }

    @Override
    public void setUrl(String url) throws IOException {
        if (TextUtils.isEmpty(url) || url.equals(mUrl)) {
            return;
        }
        mUrl = url;
    }

    @Override
    public void changeUrl(String url) throws IOException {
        setUrl(url);
        restart();
    }

    @Override
    public void changeQuality(String url) throws IOException {
        setUrl(url);
        restart();

    }

    public void addMediaPlayerListener(IJKMediaListener listener) {
        if (mIjkMediaListenerList == null){
            mIjkMediaListenerList = new ArrayList<>();
        }
        if (mIjkMediaListenerList.contains(listener)){
            return;
        }
        mIjkMediaListenerList.add(listener);
    }

    public void removeMediaPlayerListener(IJKMediaListener listener){
        if (listener == null){
            return;
        }
        if (mIjkMediaListenerList == null || mIjkMediaListenerList.size() <= 0){
            return;
        }
        if (mIjkMediaListenerList.indexOf(listener) != -1){
            mIjkMediaListenerList.remove(listener);
        }
    }

    public void clearMediaPlayerListener(){
        if (mIjkMediaListenerList == null){
            return;
        }
        mIjkMediaListenerList.clear();
        mIjkMediaListenerList = null;
    }
    /**
     * ijk播放器的配置builder
     */
    public static class IjkConfig {
        public static final String OPENSLES = "opensles";
        public static final String OVERLAY_FORMAT = "overlay-format";
        public static final String FRAMEDROP = "framedrop";
        public static final String START_ON_PREPARED = "start-on-prepared";
        public static final String HTTP_DETECT_RANGE_SUPPORT = "http-detect-range-support";
        public static final String SKIP_LOOP_FILTER = "skip_loop_filter";
        public static final String MIN_FRAMES = "min-frames";
        public static final String ENABLE_ACCURATE_SEEK = "enable-accurate-seek";
        public static final String MEDIACODEC = "mediacodec";
        public static final String MEDIACODEC_AUTO_ROTATE = "mediacodec-auto-rotate";
        public static final String MEDIACODEC_HANDLE_RESOLUTION_CHANGE = "mediacodec-handle-resolution-change";
        public static final String RECONNECT = "reconnect";
        public static final String PACKET_BUFFERING = "packet-buffering";
        public static final String DNS_CACHE_CLEAR = "dns_cache_clear";
        private int opensles;
        private int overlayFormat;
        private int framedrop;
        private int startOnPrepared;
        private int httpDetectRangeSupport;
        private int skipLoopFilter;
        private int minFrames;
        private int enableAccurateSeek;
        private boolean isEnableMediaCodec;
        private int reconnect;
        private boolean isPacketBuffering;
        private boolean isDnsCacheClear;
    }
}