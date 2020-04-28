package com.lpp.lppvideoplayer.controller.widget;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lpp.lppvideoplayer.R;
import com.lpp.lppvideoplayer.controller.base.AbsMediaControllView;
import com.lpp.lppvideoplayer.controller.base.IVideoFullScreenDo;
import com.lpp.lppvideoplayer.controller.base.IVideoGesture;
import com.lpp.lppvideoplayer.controller.widget.builder.VideoControllerBuilder;
import com.lpp.lppvideoplayer.player.base.ablity.IMediaAblity;
import com.lpp.lppvideoplayer.player.video.ijk.ablity.IjkVideoAblity;
import com.lpp.lppvideoplayer.player.video.ijk.listener.IJKMediaListener;
import com.lpp.lppvideoplayer.utils.MyBrightnessUtil;
import com.lpp.lppvideoplayer.utils.audioUtils.MyAudioFocusManager;
import com.lpp.lppvideoplayer.utils.audioUtils.MyAudioManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

public class NormalVideoControllerView extends AbsMediaControllView implements
        IVideoGesture,
        View.OnClickListener,
        View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener, IJKMediaListener {
    private static final String TAG = "lpp-videocontroll";
    /**
     * view相关
     **/
    private ConstraintLayout mCLVideoContent;
    private ImageView mIvPlay;
    private ImageView mIvFullScreen;
    private RelativeLayout mRlFullScreen;
    private RelativeLayout mRlPlay;
    public SeekBar mSeekBar;
    private TextView mTvCurrentTime;
    private TextView mTvTotalTime;
    private MargueeTextView mTvTitle;
    private TextView mTvGestureSeek;
    private LinearLayout mLlGestureVolume;
    private TextView mTvVolume;
    private LinearLayout mLlGestureBrightness;
    private TextView mTvBrightness;
    private ProgressBar mProgressBarLoading;
    private RelativeLayout mRlBack;
    private RelativeLayout mRlMoreView;
    private ImageView mIvMore;
    private ConstraintLayout mClTitle;
    /**
     * 逻辑相关
     **/
    private static final int SECONT_TIMEMILLIS = 1000;
    private static final int AUTO_HIDE_TIME = 4500;
    private static final int UPDATE_PROCESS = 2;
    private static final int AUTO_HIDE_CONTROLLER = 1;
    private VideoHandler mVideoHandler;
    private boolean isFullScreen;
    private boolean mIsLoading = false;
    private boolean isCompleted = false;
    private boolean isPausedByUser = false;
    private boolean isInit = true;
    /**
     * 控制变量
     **/
    // 判断当前的是否正在控制进度条，如果用户正在控制进度条，则不通过handler刷新进度条
    private boolean isDragging = false;
    /**
     * 事件监听
     **/
    private IVideoFullScreenDo mVideoFullScreenDo;
    private VideoControllerBuilder mVideoControllerBuilder;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener;
    public NormalVideoControllerView(Context context, IMediaAblity iMediaAblity) {
        this(context,iMediaAblity,new VideoControllerBuilder());
    }

    public NormalVideoControllerView(Context context, IMediaAblity iMediaAblity,VideoControllerBuilder videoControllerBuilder) {
        super(context, iMediaAblity);
        initView();
        initHandler();
        if (iMediaAblity instanceof IjkVideoAblity) {
            ((IjkVideoAblity) iMediaAblity).addMediaPlayerListener(this);
        }
        setVideoBuilder(videoControllerBuilder);
    }

    public NormalVideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NormalVideoControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_video_controller, this);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mIvPlay = findViewById(R.id.iv_play);
        mTvGestureSeek = findViewById(R.id.tv_gesture_seek);
        mTvTitle = findViewById(R.id.tv_video_title);
        mCLVideoContent = findViewById(R.id.cl_video_controller);
        mClTitle = findViewById(R.id.cl_title);
        mRlMoreView = findViewById(R.id.rl_more);
        mIvMore = findViewById(R.id.iv_more);
        mProgressBarLoading = findViewById(R.id.progress_loading);
        mLlGestureVolume = findViewById(R.id.ll_gesture_volume);
        mRlFullScreen = findViewById(R.id.rl_fullscreen);
        mRlFullScreen.setOnClickListener(this);
        mRlPlay = findViewById(R.id.rl_play);
        mRlPlay.setOnClickListener(this);
        mTvVolume = findViewById(R.id.tv_volume);
        mLlGestureBrightness = findViewById(R.id.ll_gesture_brightness);
        mTvBrightness = findViewById(R.id.tv_brightness);
        mIvPlay.setOnClickListener(this);
        mIvFullScreen = findViewById(R.id.iv_fullscreen);
        mIvFullScreen.setOnClickListener(this);
        mRlBack = findViewById(R.id.rl_back);
        mSeekBar = findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(this);
        mTvCurrentTime = findViewById(R.id.tv_current_time);
        mTvTotalTime = findViewById(R.id.tv_total_time);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        mBrightness = MyBrightnessUtil.getWindowBrightness((Activity) mContext);
    }

    public void setVideoBuilder(VideoControllerBuilder videoControllerBuilder){
        if (videoControllerBuilder == null){
            videoControllerBuilder = new VideoControllerBuilder();
        }
        this.mVideoControllerBuilder = videoControllerBuilder;
        if (mClTitle != null){
            mClTitle.setVisibility(mVideoControllerBuilder.isNeedTitleBar() ? View.VISIBLE : View.GONE);
        }

        if (mRlBack != null){
            mRlBack.setVisibility(mVideoControllerBuilder.isNeedBackView() ? View.VISIBLE : View.GONE);
            mRlBack.setOnClickListener(mVideoControllerBuilder.getBackDo());
        }
        if (mRlFullScreen != null){
            if (mVideoControllerBuilder.getControllerMode() == VideoControllerBuilder.CONTROLLER_MODE_NORMAL){
                mRlFullScreen.setVisibility(mVideoControllerBuilder.isNeedFullScreenBtn() ? View.VISIBLE : View.GONE);
                mVideoFullScreenDo = mVideoControllerBuilder.getFullScreenDo();
                if (mIvFullScreen != null){
                    isFullScreen = false;
                    updateControllBottomUi();
                }
            }else{
                mRlFullScreen.setVisibility(View.GONE);
            }
        }


        if (mIvMore != null && mRlMoreView != null){
            mRlMoreView.setVisibility(mVideoControllerBuilder.isNeedMoreView() ? View.VISIBLE : View.GONE);
            mRlMoreView.setOnClickListener(mVideoControllerBuilder.getMoreDo());
        }
    }

    private void initHandler() {
        mVideoHandler = new VideoHandler(this);
    }

    @Override
    public void changeQuality(String url) {
        if (!TextUtils.isEmpty(url)) {
            changeUrl(url);
            if (mAudioFocusManagerListener != null){
                MyAudioFocusManager.requestAudioFocus(mContext,mAudioFocusManagerListener);
            }
        }
    }

    @Override
    public void show() {
        show(AUTO_HIDE_TIME);
    }

    public void show(int autoHideTimeMillis) {
        if (autoHideTimeMillis < 0) {
            autoHideTimeMillis = AUTO_HIDE_TIME;
        }
        if (mCLVideoContent != null) {
            mCLVideoContent.setVisibility(View.VISIBLE);
            if (mSeekBar != null){
                mSeekBar.setVisibility(isCompleted ? View.GONE : View.VISIBLE);
            }
            if (!isCompleted) {
                if (mVideoHandler != null) {
                    // 发送3s后自动隐藏
                    hideControllerAfterTimes(autoHideTimeMillis);
                    // 发送更新progress
                    updateProgress();
                }
                updatePlayOrPause(mMediaAblity.isPlaying());
            }
        }
    }

    private void hideControllerAfterTimes(int autoHideTimeMillis) {
        mVideoHandler.removeMessages(AUTO_HIDE_CONTROLLER);
        Message message = mVideoHandler.obtainMessage();
        message.what = AUTO_HIDE_CONTROLLER;
        mVideoHandler.sendMessageDelayed(message, autoHideTimeMillis);
    }


    public void updateProgress() {
        if (mMediaAblity != null) {
            long position = mMediaAblity.getCurrentPosition();
            Log.e(TAG, "updateProgress :" + position / SECONT_TIMEMILLIS);
            // 如果用户正在手动拖动进度条，那么就不自动设值进度条的值和当前播放的时间
            if (!isDragging) {
                mSeekBar.setProgress((int) (position / SECONT_TIMEMILLIS));
                mTvCurrentTime.setText(stringForTime((int) mMediaAblity.getCurrentPosition() / SECONT_TIMEMILLIS));
                mTvTotalTime.setText(stringForTime((int) mMediaAblity.getDuration() / SECONT_TIMEMILLIS));
            }
        }
        mVideoHandler.removeMessages(UPDATE_PROCESS);
        Message messageUpdateProgress = mVideoHandler.obtainMessage();
        messageUpdateProgress.what = UPDATE_PROCESS;
        mVideoHandler.sendMessageDelayed(messageUpdateProgress, SECONT_TIMEMILLIS);
    }

    public void updateProgressByGesture(long position) {
        int progress = (int) (position / 1000);
        setSeekTime(progress);
        if (mSeekBar != null) {
            mSeekBar.setProgress(progress);
        }
    }

    private void setSeekTime(int position) {
        mTvCurrentTime.setText(stringForTime(position));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setSeekTime(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isDragging = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        start();
        seekTo(seekBar.getProgress() * SECONT_TIMEMILLIS);
        isDragging = false;
        show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            toggle();
        }
        return false;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        isCompleted = true;
        mIsLoading = false;
        updatePlayOrPause(false);
        show();
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
        setMaxProgess((int) (iMediaPlayer.getDuration() / 1000));
        isCompleted = false;
        mVideoHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsLoading = false;
                show();
            }
        }, 500);
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        updateProgress();
        mVideoHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsLoading = false;
                show();
            }
        }, 500);

    }

    @Override
    public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {

    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onAsyncListener(IjkMediaPlayer mediaPlayer) {
        mIsLoading = true;
        show();
    }

    private static class VideoHandler extends Handler {
        WeakReference<NormalVideoControllerView> controllViewRef;

        public VideoHandler(NormalVideoControllerView normalVideoControllerView) {
            controllViewRef = new WeakReference<>(normalVideoControllerView);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (controllViewRef.get() == null) {
                return;
            }
            NormalVideoControllerView controllerView = controllViewRef.get();
            switch (msg.what) {
                case AUTO_HIDE_CONTROLLER:
                    controllerView.hide();
                    break;
                case UPDATE_PROCESS:
                    if (controllerView.mSeekBar != null) {
                        controllerView.updateProgress();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public void toggle() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }


    @Override
    public void hide() {
        // 如果正在拖动进度条，则不隐藏控制器
        if (isDragging) {
            return;
        }
        if (mCLVideoContent != null) {
            mCLVideoContent.setVisibility(View.GONE);
        }
        if (mVideoHandler != null) {
            mVideoHandler.removeMessages(UPDATE_PROCESS);
        }
    }

    @Override
    public boolean isShowing() {
        if (mCLVideoContent != null) {
            return mCLVideoContent.getVisibility() == View.VISIBLE;
        }
        return false;
    }

    @Override
    public void start() {
        if (mMediaAblity != null) {
            isPausedByUser = false;
            mMediaAblity.start();
            if (mAudioFocusManagerListener != null){
                MyAudioFocusManager.requestAudioFocus(mContext,mAudioFocusManagerListener);
            }
            updatePlayOrPause(true);
        }
    }

    @Override
    public void pause() {
        if (mMediaAblity != null) {
            mMediaAblity.pause();
            updatePlayOrPause(false);
        }
    }

    @Override
    public void seekTo(long position) {
        Log.e(TAG, "seekTo:" + position);
        if (mMediaAblity != null) {
            mIsLoading = true;
            mMediaAblity.seekTo(position);
            if (mAudioFocusManagerListener != null){
                MyAudioFocusManager.requestAudioFocus(mContext,mAudioFocusManagerListener);
            }
        }
        show();
    }

    @Override
    public void pauseOrContinue(View v) {
        if (mMediaAblity.isPlaying()) {
            mMediaAblity.pause();
        } else {
            mMediaAblity.start();
        }
    }

    @Override
    public void setVolume(int volume) {
        int volumeManageValue = (int) (((float) volume / 100.0f) * MyAudioManager.MAX_VOLUME);
        MyAudioManager.getInstance(mContext.getApplicationContext()).setVolume(volumeManageValue);
    }

    @Override
    public void setBrightness(int brightness) {

    }

    @Override
    public void restart() {
        if (mMediaAblity != null) {
            mMediaAblity.restart();
            isPausedByUser = false;
            if (mAudioFocusManagerListener != null){
                MyAudioFocusManager.requestAudioFocus(mContext,mAudioFocusManagerListener);
            }
        }
    }

    @Override
    public void changeUrl(String newUrl) {
        if (mMediaAblity != null) {
            try {
                mMediaAblity.changeUrl(newUrl);
                if (mAudioFocusManagerListener != null){
                    MyAudioFocusManager.requestAudioFocus(mContext,mAudioFocusManagerListener);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setMaxProgess(int maxProgess) {
        if (mSeekBar != null) {
            mSeekBar.setMax(maxProgess);
        }
    }

    @Override
    public void setSecondProgress(int secondProgress) {
        if (mSeekBar != null) {
            mSeekBar.setSecondaryProgress(secondProgress + mSeekBar.getProgress());
        }
    }

    @Override
    public void setVideoTitle(String videoTitle) {
        if (!TextUtils.isEmpty(videoTitle) && mTvTitle != null) {
            mTvTitle.setText(videoTitle);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_play || v.getId() == R.id.rl_play) {
            // TODO play or pause
            if (mMediaAblity != null) {
                if (mMediaAblity.isPlaying()) {
                    isPausedByUser = true;
                    pause();

                } else {
                    isPausedByUser = false;
                    start();

                }
                updateControllBottomUi();
            }
        } else if (v.getId() == R.id.iv_fullscreen || v.getId() == R.id.rl_fullscreen) {
            // TODO full or nor full screen
            if (mVideoFullScreenDo != null) {
                if (isFullScreen) {
                    mVideoFullScreenDo.notFullScreen();
                    isFullScreen = false;
                } else {
                    mVideoFullScreenDo.fullScreen();
                    isFullScreen = true;
                }
                updateControllBottomUi();
            }
        } else if (v.getId() == R.id.fl_root) {
            toggle();
        }
    }

    /**
     * 将时间戳转换成对应的时分秒hh:mm:ss
     *
     * @param totalSeconds
     * @return
     */
    public static String stringForTime(int totalSeconds) {
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder formatBuilder = new StringBuilder();
        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
        formatBuilder.setLength(0);
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private float mTouchY = -1f;
    private float mTouchX = -1f;
    private int mVideoYDisplayRange = -1;
    private int mVideoXDisplayRange = -1;
    private static final int TOUCH_ENENT_CHANGE_VOLUME = 1, TOUCH_ENENT_CHANGE_BRIGHTERNESS = 2, TOUCH_ENENT_CHANGE_VIDEO_POSITION = 3;
    private int touchEvent = -1;
    private long newPosition = -1;
    private int mVolume, mBrightness;
    private WindowManager mWindowManager;
    private int mTouchSlop;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xChanged;
        float yChanged;
        if (mTouchX != -1 && mTouchY != -1) {
            xChanged = event.getRawX() - mTouchX;
            yChanged = event.getRawY() - mTouchY;
        } else {
            xChanged = 0f;
            yChanged = 0f;
        }
        DisplayMetrics screen = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(screen);
        if (mVideoYDisplayRange == -1) {
            mVideoYDisplayRange = Math.min(screen.widthPixels, screen.heightPixels);
        }
        if (mVideoXDisplayRange == -1) {
            mVideoXDisplayRange = Math.max(screen.widthPixels, screen.heightPixels);
        }
        float coef = Math.abs(yChanged / xChanged);
        mTouchX = event.getRawX();
        mTouchY = event.getRawY();
        Log.e(TAG, "onTouchEvent()");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //业务代码
                return true;
            case MotionEvent.ACTION_MOVE:
                // 只有当滑动事件x轴上的距离大于y轴上距离的2倍时才认为是左右滑动
                if (coef < 1) {
                    // 左右手势滑动前进或者后退
                    if (isCompleted) return false;
                    if (touchEvent == -1) {
                        int touchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
                        // 当x轴的移动距离布局整体屏幕宽度的0.05则不调整进度
                        if (Math.abs(xChanged / mVideoXDisplayRange) < 0.010 || Math.abs(xChanged) < touchSlop) {
                            return false;
                        }
                        if (newPosition == -1) {
                            newPosition = mMediaAblity.getCurrentPosition();
                            if (newPosition <= 0) {
                                return false;
                            }
                        }
                    }
                    if ((touchEvent == -1 || touchEvent == TOUCH_ENENT_CHANGE_VIDEO_POSITION)) {
                        touchEvent = TOUCH_ENENT_CHANGE_VIDEO_POSITION;
                        isDragging = true;
                        newPosition = calSeekPosition(xChanged < 0 ? 0 : 1, mMediaAblity.getDuration(), newPosition);
                        updateProgressByGesture(newPosition);
                        showOrHideSeekGestureView(true, newPosition);
                        return true;
                    }
                } else {
                    // 触摸在右侧，控制音量
                    if (mTouchX > screen.widthPixels / 2) {

                        if (touchEvent == -1) {
                            if (Math.abs(yChanged) < mTouchSlop) {
                                return false;
                            }
                        }
                        if ((touchEvent == -1 || touchEvent == TOUCH_ENENT_CHANGE_VOLUME)) {
                            if (touchEvent == TOUCH_ENENT_CHANGE_VOLUME && (Math.abs(yChanged / mVideoYDisplayRange) < 0.018 || Math.abs(yChanged) < mTouchSlop)) {
                                return true;
                            }
                            touchEvent = TOUCH_ENENT_CHANGE_VOLUME;
                            float delta = -(yChanged / mVideoYDisplayRange);
                            mVolume = MyAudioManager.getInstance(mContext).setVolume(delta > 0);
                            int volumePer = (int) (((float) mVolume / (float) MyAudioManager.MAX_VOLUME) * 100);
                            Log.e(TAG, "set volume:" + volumePer);
                            showOrHideVolumeView(true, volumePer);
                            return true;
                        }
                    } else {

                        if (touchEvent == -1) {
                            if (Math.abs(yChanged) < mTouchSlop) {
                                return false;
                            }
                        }
                        if ((touchEvent == -1 || touchEvent == TOUCH_ENENT_CHANGE_BRIGHTERNESS)) {
                            touchEvent = TOUCH_ENENT_CHANGE_BRIGHTERNESS;
                            float delta = -(yChanged / mVideoYDisplayRange) * 255f;
                            mBrightness += delta;
                            if (mBrightness < 0) {
                                mBrightness = 0;
                            }
                            if (mBrightness > 255) {
                                mBrightness = 255;
                            }
                            MyBrightnessUtil.setWindowBrightness((Activity) mContext, mBrightness);
                            float brightnessPer = (float) (mBrightness / 255.0f * 100.0);
                            Log.e(TAG, "set mBrightness:" + mBrightness + ",delta:" + delta);
                            showOrHideBrightnessView(true, (int) brightnessPer);
                            return true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isDragging = false;
                if (touchEvent == TOUCH_ENENT_CHANGE_VIDEO_POSITION) {
                    start();
                    seekTo(newPosition);
                }
                newPosition = -1;
                touchEvent = -1;
                mTouchX = -1;
                mTouchY = -1;
                showOrHideSeekGestureView(false, 0);
                showOrHideVolumeView(false, 0);
                showOrHideBrightnessView(false, 0);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void updateControllBottomUi() {
        if (mIvFullScreen != null) {
            mIvFullScreen.setImageResource(isFullScreen ? R.mipmap.not_fullscreen : R.mipmap.fullscreen);
        }
    }

    private void updatePlayOrPause(boolean isPlaying) {
        if (!isCompleted) {
            if (mIvPlay != null) {
                mIvPlay.setImageResource(isPlaying ? R.mipmap.pause : R.mipmap.play);
            }

        }else{
            if (mIvPlay != null) {
                mIvPlay.setImageResource(R.mipmap.replay);
            }
        }
        if (mProgressBarLoading != null) {
            mProgressBarLoading.setVisibility(mIsLoading ? View.VISIBLE : View.GONE);
        }

    }

    private void showOrHideSeekGestureView(boolean show, long position) {
        if (show) {
            if (mTvGestureSeek != null) {
                String seekTime = stringForTime((int) (position / 1000));
                mTvGestureSeek.setText(seekTime);
                mTvGestureSeek.setVisibility(View.VISIBLE);
            }
        } else {
            if (mTvGestureSeek != null) {
                mTvGestureSeek.setVisibility(View.GONE);
            }
        }
    }

    private void showOrHideVolumeView(boolean show, int volumePer) {
        Log.e(TAG, "showOrHideVolumeView:" + show);
        if (mLlGestureVolume != null) {
            mLlGestureVolume.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (mTvVolume != null) {
            mTvVolume.setText(volumePer + "%");
        }
    }

    private void showOrHideBrightnessView(boolean show, int brightnessPer) {
        Log.e(TAG, "showOrHideVolumeView:" + show);
        if (mLlGestureBrightness != null) {
            mLlGestureBrightness.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (mTvBrightness != null) {
            mTvBrightness.setText(brightnessPer + "%");
        }
    }

    /**
     * 手势调整seek的进度
     * 根据滑动的距离,滑动的方向，视频的总长度，当前的位置调整seek的位置
     *
     * @param direction 0为向左滑动，1为向右滑动
     * @param duration
     * @param position
     * @return
     */
    long calSeekPosition(int direction, long duration, long position) {
        long changeAbsProgress;
        if (duration <= 0) {
            changeAbsProgress = 0;
        } else if (duration < 1 * 60 * 1000) {
            changeAbsProgress = 1 * 1000;
        } else if (duration < 2 * 60 * 1000) {
            changeAbsProgress = 2 * 1000;
        } else if (duration < 5 * 60 * 1000) {
            changeAbsProgress = 3 * 1000;
        } else if (duration < 10 * 60 * 1000) {
            changeAbsProgress = 10 * 1000;
        } else {
            changeAbsProgress = 15 * 1000;
        }
        if (direction == 0) {
            position -= changeAbsProgress;
        } else {
            position += changeAbsProgress;
        }

        if (position < 0) {
            position = 0;
        }
        if (position >= duration) {
            position = duration;
        }
        return position;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isPausedByUser && !isInit){
            start();
            updatePlayOrPause(true);
        }else {
            if (mAudioFocusManagerListener != null) {
                MyAudioFocusManager.requestAudioFocus(mContext, mAudioFocusManagerListener);
            }
        }
        isInit = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
        updatePlayOrPause(false);
        if (mAudioFocusManagerListener != null){
            MyAudioFocusManager.abandonAudioFocus(mContext,mAudioFocusManagerListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaAblity != null){
            mMediaAblity.release();
            mMediaAblity = null;
        }
        if (mAudioFocusManagerListener != null){
            MyAudioFocusManager.abandonAudioFocus(mContext,mAudioFocusManagerListener);
        }
        if (mVideoHandler != null){
            mVideoHandler.removeCallbacksAndMessages(null);
            mVideoHandler = null;
        }
    }

    public void pausedByUser(){
        isPausedByUser = true;
        onPause();
    }
}
