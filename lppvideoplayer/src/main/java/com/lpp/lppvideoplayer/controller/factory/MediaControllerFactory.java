package com.lpp.lppvideoplayer.controller.factory;

import android.content.Context;

import com.lpp.lppvideoplayer.controller.base.AbsMediaControllView;
import com.lpp.lppvideoplayer.controller.widget.NormalVideoControllerView;
import com.lpp.lppvideoplayer.controller.widget.builder.VideoControllerBuilder;
import com.lpp.lppvideoplayer.player.base.ablity.IMediaAblity;

public class MediaControllerFactory {
    public static AbsMediaControllView getNormalVideoController(Context context, IMediaAblity mediaAblity,VideoControllerBuilder videoControllerBuilder){
        AbsMediaControllView mediaControllView = new NormalVideoControllerView(context,mediaAblity,videoControllerBuilder);
        return mediaControllView;
    }

}
