package com.lpp.lppvideoplayer.player.video.ijk.listener;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public interface IJKMediaListener extends
        IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnSeekCompleteListener,
        IMediaPlayer.OnTimedTextListener,
        IMediaPlayer.OnVideoSizeChangedListener{
    void onAsyncListener(IjkMediaPlayer mediaPlayer);
}
