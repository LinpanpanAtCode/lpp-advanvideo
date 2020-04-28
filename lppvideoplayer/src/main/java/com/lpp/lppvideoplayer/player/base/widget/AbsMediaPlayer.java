package com.lpp.lppvideoplayer.player.base.widget;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.lpp.lppvideoplayer.controller.base.AbsMediaControllView;
import com.lpp.lppvideoplayer.player.base.ablity.IMediaAblity;
import com.lpp.lppvideoplayer.player.base.config.IMediaItemBuilder;
import com.lpp.lppvideoplayer.utils.AppInfo;
import com.lpp.lppvideoplayer.utils.audioUtils.IAudioFocusManagerListener;
import com.lpp.lppvideoplayer.utils.audioUtils.MyAudioFocusManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 定义抽象媒体播放器类
 */
public abstract class AbsMediaPlayer extends FrameLayout {
    protected Context mContext;
    protected IMediaAblity mIMediaAblity;
    protected AbsMediaControllView mMediaController;
    protected IMediaItemBuilder mMediaBuilder;
    private static final String TAG = "lpp-mediaplayer";
    List<IAudioFocusManagerListener> mAudioFocusManagerListenerList;
    protected AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    public AbsMediaPlayer(Context context) {
        super(context);
        init(context);
        registerReceiver();
    }

    public AbsMediaPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        registerReceiver();
    }

    public AbsMediaPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        registerReceiver();
    }

    private void init(Context context) {
        this.mContext = context;
        mIMediaAblity = createMediaAbilty();
        if (AppInfo.applicationContext == null){
            AppInfo.applicationContext = context.getApplicationContext();
        }
        addMidDisplayView();
    }

    protected void addMidDisplayView() {
    }

    public IMediaAblity getAbility() {
        return mIMediaAblity;
    }

    public void attachVideoController(AbsMediaControllView mediaController) {
        if (mediaController != null) {
            View controllView = (View) mMediaController;
            if (indexOfChild(controllView) != -1) {
                removeView(controllView);
            }
            mMediaController = mediaController;
            addView((View) mMediaController);
            if (mMediaController != null && mOnAudioFocusChangeListener != null){
                mediaController.setAudioFocusManagerListener(mOnAudioFocusChangeListener);
            }
        }
    }

    protected abstract IMediaAblity createMediaAbilty();

    public void setMediaBuilder(IMediaItemBuilder mediaBuilder) throws Exception {
        this.mMediaBuilder = mediaBuilder;
        if (mediaBuilder != null && !TextUtils.isEmpty(mMediaBuilder.getUrl())) {
            mIMediaAblity.setUrl(mMediaBuilder.getUrl());
            if (mMediaController != null) {
                mMediaController.setVideoTitle(mMediaBuilder.getVideoTitle());
            }
        }
    }

    public void setUrl(String url) throws Exception {
        if (!TextUtils.isEmpty(url)) {
            IMediaItemBuilder mediaBuilder = createMediaBuilderByUrl(url);
            setMediaBuilder(mediaBuilder);
        }
    }

    public IMediaItemBuilder createMediaBuilderByUrl(final String url) {
        return new IMediaItemBuilder() {
            @Override
            public String getUrl() {
                return url.trim();
            }

            @Override
            public String getVideoTitle() {
                String fName = url.trim();
                String fileName = fName.substring(fName.lastIndexOf("/") + 1);
                return fileName;
            }
        };
    }

    public AbsMediaControllView getMediaController() {
        return mMediaController;
    }

    public void onStart() {
    }

    public void onResume() {
        requestAudioFocus();
        if (mMediaController != null){
            mMediaController.onResume();
        }
    }

    public void onPause() {
        if (mMediaController != null){
            mMediaController.onPause();
        }
        abandonAudioFocus();
    }

    public void onStop() {
    }

    public void onDestroy() {
        if (mMediaController != null){
            mMediaController.onDestroy();
        }
        if (mGlobalReceiver != null){
            mContext.unregisterReceiver(mGlobalReceiver);
            mGlobalReceiver = null;
        }
        if (mOnAudioFocusChangeListener != null){
            MyAudioFocusManager.abandonAudioFocus(mContext,mOnAudioFocusChangeListener);
            mOnAudioFocusChangeListener = null;
        }
        clearAudioFocusManagerListeners();
    }


    public void registerReceiver() {
        try {
            IntentFilter globalFilter = new IntentFilter();
            /**
             * 有线耳机和蓝牙耳机插播的广播
             */
            globalFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            globalFilter.addAction(Intent.ACTION_HEADSET_PLUG);
            globalFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
            mContext.registerReceiver(mGlobalReceiver,globalFilter);
            if (mOnAudioFocusChangeListener == null) {
                mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                        switch (focusChange) {
                            case AudioManager.AUDIOFOCUS_GAIN:
                                //当其他应用申请焦点之后又释放焦点会触发此回调
                                //可重新播放音乐
                                Log.e(TAG, "AUDIOFOCUS_GAIN");
                                if (mAudioFocusManagerListenerList != null && mAudioFocusManagerListenerList.size() > 0) {
                                    for (int i = 0; i < mAudioFocusManagerListenerList.size(); i++) {
                                        IAudioFocusManagerListener listener = mAudioFocusManagerListenerList.get(i);
                                        listener.onAudioFocusGain();
                                    }
                                }
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS:
                                //长时间丢失焦点,当其他应用申请的焦点为 AUDIOFOCUS_GAIN 时，
                                //会触发此回调事件，例如播放 QQ 音乐，网易云音乐等
                                //通常需要暂停音乐播放，若没有暂停播放就会出现和其他音乐同时输出声音
                                Log.e(TAG, "AUDIOFOCUS_LOSS");
                                //释放焦点，该方法可根据需要来决定是否调用
                                //若焦点释放掉之后，将不会再自动获得
                                if (mAudioFocusManagerListenerList != null && mAudioFocusManagerListenerList.size() > 0) {
                                    for (int i = 0; i < mAudioFocusManagerListenerList.size(); i++) {
                                        IAudioFocusManagerListener listener = mAudioFocusManagerListenerList.get(i);
                                        listener.onAudioFocusLoss();
                                    }
                                    MyAudioFocusManager.abandonAudioFocus(mContext,mOnAudioFocusChangeListener);
                                }
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                //短暂性丢失焦点，当其他应用申请 AUDIOFOCUS_GAIN_TRANSIENT 或AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE 时，
                                //会触发此回调事件，例如播放短视频，拨打电话等。
                                //通常需要暂停音乐播放
                                if (mAudioFocusManagerListenerList != null && mAudioFocusManagerListenerList.size() > 0) {
                                    for (int i = 0; i < mAudioFocusManagerListenerList.size(); i++) {
                                        IAudioFocusManagerListener listener = mAudioFocusManagerListenerList.get(i);
                                        listener.onAudioFocusLossTransient();
                                    }
                                }
                                Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                //短暂性丢失焦点并作降音处理
                                Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                                if (mAudioFocusManagerListenerList != null && mAudioFocusManagerListenerList.size() > 0) {
                                    for (int i = 0; i < mAudioFocusManagerListenerList.size(); i++) {
                                        IAudioFocusManagerListener listener = mAudioFocusManagerListenerList.get(i);
                                        listener.onAudioFocusLossTransientCanDuck();
                                    }
                                }
                                break;
                        }
                    }
                };
            }
        } catch (Exception|Error e) {
            e.printStackTrace();
        }
    }

    public int requestAudioFocus(){
        return MyAudioFocusManager.requestAudioFocus(mContext,mOnAudioFocusChangeListener);
    }

    public void abandonAudioFocus(){
        MyAudioFocusManager.abandonAudioFocus(mContext,mOnAudioFocusChangeListener);
    }
    private BroadcastReceiver mGlobalReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equalsIgnoreCase(intent.getAction())) {
                if (mAudioFocusManagerListenerList != null && mAudioFocusManagerListenerList.size() > 0) {
                    for (int i = 0 ; i < mAudioFocusManagerListenerList.size() ; i++) {
                        IAudioFocusManagerListener listener = mAudioFocusManagerListenerList.get(i);
                        listener.onAudioBecomingNoisy();
                    }
                }
            }else  if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (BluetoothProfile.STATE_DISCONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
                    if (mAudioFocusManagerListenerList != null && mAudioFocusManagerListenerList.size() > 0) {
                        for (int i = 0 ; i < mAudioFocusManagerListenerList.size() ; i++) {
                            IAudioFocusManagerListener listener = mAudioFocusManagerListenerList.get(i);
                            listener.onBluetoothConnectionStateChanged();
                        }
                    }
                }
            }else if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                if (mAudioFocusManagerListenerList != null && mAudioFocusManagerListenerList.size() > 0) {
                    for (int i = 0; i < mAudioFocusManagerListenerList.size(); i++) {
                        IAudioFocusManagerListener listener = mAudioFocusManagerListenerList.get(i);
                        listener.onHeadSetPlug();
                    }
                }
            }
        }
    };

    public void addAudioFocusManagerListener(IAudioFocusManagerListener audioFocusManagerListener){
        if (audioFocusManagerListener == null){
            return;
        }
        if (mAudioFocusManagerListenerList == null){
            mAudioFocusManagerListenerList = new ArrayList<>();
        }
        if (mAudioFocusManagerListenerList.indexOf(audioFocusManagerListener) != -1){
            return;
        }
        mAudioFocusManagerListenerList.add(audioFocusManagerListener);
    }

    public void removeAudioFocusManagerListener(IAudioFocusManagerListener audioFocusManagerListener){
        if (audioFocusManagerListener == null){
            return;
        }
        if (mAudioFocusManagerListenerList == null){
            return;
        }
        if (mAudioFocusManagerListenerList.indexOf(audioFocusManagerListener) == -1){
            return;
        }
        mAudioFocusManagerListenerList.remove(audioFocusManagerListener);
    }
    public void clearAudioFocusManagerListeners(){
        if (mAudioFocusManagerListenerList == null){
            return;
        }
        mAudioFocusManagerListenerList.clear();
        mAudioFocusManagerListenerList = null;
    }

}
