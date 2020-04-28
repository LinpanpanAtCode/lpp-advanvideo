package com.lpp.lppvideoplayer.utils.audioUtils;

import android.content.Context;
import android.media.AudioManager;

import com.lpp.lppvideoplayer.utils.AppInfo;

import static android.content.Context.AUDIO_SERVICE;

public class MyAudioManager {
    //初始化音频管理器
    AudioManager audioManager;
    //获取系统最大音量
    public static int MAX_VOLUME = -1;
    private MyAudioManager(Context context){
        this.audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        MAX_VOLUME = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    enum SingletonEnum{
        INSTANCE;
        private MyAudioManager audioManager;
        private SingletonEnum() {
            audioManager = new MyAudioManager(AppInfo.applicationContext);
        }

        public MyAudioManager getAudioManager() {
            return audioManager;
        }
    }

    public static MyAudioManager getInstance(Context context){
        if (AppInfo.applicationContext == null){
            AppInfo.applicationContext = context.getApplicationContext();
        }
        return SingletonEnum.INSTANCE.getAudioManager();
    }

    public int getCurrentVolume(){
        return this.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public int setVolume(boolean raise){
        if (audioManager != null){
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, raise ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER ,AudioManager.FLAG_PLAY_SOUND);
            return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    public int setVolume(int volume){
        if (this.audioManager != null){
            int currentVolume = this.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume == currentVolume){
                return currentVolume;
            }
            if (volume < currentVolume){
                while (volume < currentVolume){
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER ,AudioManager.FLAG_PLAY_SOUND);
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                }
            }else {
                while (volume > currentVolume){
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE ,AudioManager.FLAG_PLAY_SOUND);
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                }
            }
        }
        return volume;
    }

}
