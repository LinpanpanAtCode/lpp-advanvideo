package com.lpp.lppvideoplayer.utils.audioUtils;

public interface IAudioFocusManagerListener {

    void onHeadSetPlug();

    void onBluetoothConnectionStateChanged();

    void onAudioBecomingNoisy();

    void onAudioFocusGain();

    void onAudioFocusLoss();

    void onAudioFocusLossTransient();

    void onAudioFocusLossTransientCanDuck();
}
