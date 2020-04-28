package com.lpp.lppvideoplayer.controller.widget.builder;

import android.view.View;

import com.lpp.lppvideoplayer.controller.base.IVideoFullScreenDo;

public class VideoControllerBuilder {
    public static final int CONTROLLER_MODE_NORMAL = 1,CONTROLLER_MODE_FULLSCREEN = 2;
    private boolean isNeedTitleBar = true;
    private boolean isNeedBackView;
    private boolean isNeedFullScreenBtn;
    private boolean isNeedChangeQuality;
    private boolean isNeedMoreView;
    private int controllerMode = CONTROLLER_MODE_NORMAL;
    private View.OnClickListener backDo;
    private View.OnClickListener moreDo;
    private View.OnClickListener changeQualityDo;
    private IVideoFullScreenDo fullScreenDo;
    public boolean isNeedTitleBar() {
        return isNeedTitleBar;
    }

    public int getControllerMode() {
        return controllerMode;
    }

    public VideoControllerBuilder setControllerMode(int controllerMode) {
        this.controllerMode = controllerMode;
        return this;
    }

    public IVideoFullScreenDo getFullScreenDo() {
        return fullScreenDo;
    }

    public VideoControllerBuilder setFullScreenDo(IVideoFullScreenDo fullScreenDo) {
        this.fullScreenDo = fullScreenDo;
        return this;
    }

    public VideoControllerBuilder setNeedTitleBar(boolean needTitleBar) {
        isNeedTitleBar = needTitleBar;
        return this;
    }

    public boolean isNeedBackView() {
        return isNeedBackView;
    }

    public VideoControllerBuilder setNeedBackView(boolean needBackView) {
        isNeedBackView = needBackView;
        return this;
    }

    public boolean isNeedFullScreenBtn() {
        return isNeedFullScreenBtn;
    }

    public VideoControllerBuilder setNeedFullScreenBtn(boolean needFullScreenBtn) {
        isNeedFullScreenBtn = needFullScreenBtn;
        return this;
    }

    public boolean isNeedChangeQuality() {
        return isNeedChangeQuality;
    }

    public VideoControllerBuilder setNeedChangeQuality(boolean needChangeQuality) {
        isNeedChangeQuality = needChangeQuality;
        return this;
    }

    public boolean isNeedMoreView() {
        return isNeedMoreView;
    }

    public VideoControllerBuilder setNeedMoreView(boolean needMoreView) {
        isNeedMoreView = needMoreView;
        return this;
    }

    public View.OnClickListener getBackDo() {
        return backDo;
    }

    public VideoControllerBuilder setBackDo(View.OnClickListener backDo) {
        this.backDo = backDo;
        return this;
    }

    public View.OnClickListener getMoreDo() {
        return moreDo;
    }

    public VideoControllerBuilder setMoreDo(View.OnClickListener moreDo) {
        this.moreDo = moreDo;
        return this;
    }

    public View.OnClickListener getChangeQualityDo() {
        return changeQualityDo;
    }

    public VideoControllerBuilder setChangeQualityDo(View.OnClickListener changeQualityDo) {
        this.changeQualityDo = changeQualityDo;
        return this;
    }
}
