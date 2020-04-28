package com.lpp.lppvideoplayer.utils.audioUtils;

import android.content.Context;
import android.media.AudioManager;

public class MyAudioFocusManager {
    public static int requestAudioFocus(Context context, AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }

    public static int abandonAudioFocus(Context context, AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.abandonAudioFocus(onAudioFocusChangeListener);
    }
}
