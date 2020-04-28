package com.lpp.lppvideoplayer.player.base.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.lpp.lppvideoplayer.controller.base.IVideoFullScreenDo;
import com.lpp.lppvideoplayer.utils.ScreenUtils;
import com.lpp.lppvideoplayer.utils.SystemUiTools;


public abstract class AbsVideoPlayer extends AbsMediaPlayer implements IVideoFullScreenDo {
    private static final String TAG = "lpp-AbsVideoPlayer";
    protected TextureView mSurfaceView;
    protected SurfaceTexture mSurfaceTexture;
    protected int mVideoWidth = -1,mVideoHeight = -1;
    protected float mVideoRatio;
    protected int mScreenMode = SCREEN_MODE_NORMAL;
    protected static final int SCREEN_MODE_NORMAL = 1,SCREEN_MODE_FULL = 2,SCREEN_MODE_FLOAT = 3;
    private ViewGroup parentView;
    private int SYSTEM_UI = 0;
    protected boolean isFullScreen = false;
    boolean isDefaultScreenDo;
    public AbsVideoPlayer(Context context) {
        super(context);
    }

    public AbsVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void addMidDisplayView(){
        mSurfaceView = createSurfaceView();
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
                , LayoutParams.MATCH_PARENT,Gravity.CENTER);
        addView(mSurfaceView,layoutParams);
    }

    protected TextureView createSurfaceView(){
        TextureView surfaceView = new TextureView(mContext);
        surfaceView.setSurfaceTextureListener(createSurfaceHolderCallback());
        return surfaceView;
    }

    protected abstract TextureView.SurfaceTextureListener createSurfaceHolderCallback();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 如果不是默认的全屏方案，
        if (!isDefaultScreenDo){
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
            return;
        }
        /** 如果是默认的全屏方案，则走默认的测量方案 ，如果不是默认的全屏方案，则需要用户自己去实现测量方案，重写onMeasure方法**/
        Log.e(TAG,"onMeasure,width:height = " +MeasureSpec.getSize(widthMeasureSpec) + ":" + MeasureSpec.getSize(heightMeasureSpec) + ", videoWidth:videoHeight = " + mVideoWidth + ":" + mVideoHeight);
        /**
         * 由于视频的加载是有时延的，但是onMeasure的是立马执行的
         * 这样会导致onMeasure的时候，视频的长宽是取不到的
         * 所以当视频准备就绪的时候，在外面我们要重新调用{@link View #requestLayout 的方法进行重新测量}
          */
        if (mVideoWidth != -1 && mVideoHeight != -1){
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            if (isFullScreen){
                width = ScreenUtils.getScreenWidth(mContext);
                height = ScreenUtils.getScreenHeight(mContext);
                if (width < height){
                    /** 如果是竖着全屏，则判断全屏的左上角的起点是否是状态栏起点 **/
                    int[] location = new int[2];
                    this.getLocationOnScreen(location);
                    /**
                     *  如果在竖着全屏的情况，左上角的坐标的Y值大于0,通常来说这个位置是在状态栏的下方，而且这个手机是刘海屏
                     *  为了让视频居中处理，我们需要重新计算整体的高度，高度为全屏高度减去状态栏的高度
                     **/
                    if (location[1] > 0){
                        height -= location[1];
                    }
                    Log.e(TAG,"onMeasure location: " +location[0] + ":" + location[1]);
                }

            }
            Log.e(TAG,"onMeasure for real,width:height = " +width + ":" + height);
            float containerRatio =  (float) width / (float) height;
            if (mVideoRatio > containerRatio){
                int videoWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY);
                int videoHeightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (width /mVideoRatio),MeasureSpec.EXACTLY);
                View view = getChildAt(0);
                if (view != null){
                    view.measure(videoWidthMeasureSpec,videoHeightMeasureSpec);
                }
            }else {
                int videoWidthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (height * mVideoRatio),MeasureSpec.EXACTLY);
                int videoHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
                View view = getChildAt(0);
                if (view != null){
                    view.measure(videoWidthMeasureSpec,videoHeightMeasureSpec);
                }
            }
            View viewController = getChildAt(1);
            if (viewController != null){
                viewController.measure(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
            }
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
        }else{
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }
    }

    public void setVideoWH(int videoWidth,int videoHeight){
        Log.e(TAG,"setVideoWH,videoWidth:videoHeight = " +videoWidth + ":" + videoHeight);
        this.mVideoWidth = videoWidth;
        this.mVideoHeight = videoHeight;
        this.mVideoRatio = (float) mVideoWidth / (float) mVideoHeight;
    }

    @Override
    public void fullScreen() {
        if (mContext instanceof Activity) {
            isFullScreen = true;
            Activity activity = (Activity) mContext;
            if (parentView == null) {
                parentView = (ViewGroup) getParent();
            }
            if (parentView.indexOfChild(this) != -1) {
                parentView.removeView(this);
            }
            ViewGroup vg = (ViewGroup) activity.getWindow().getDecorView();
            if (vg.indexOfChild(this) == -1) {
                vg.addView(this);
            }
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (mVideoWidth > mVideoHeight) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SystemUiTools.hideStatusBar(mContext);
                SYSTEM_UI = SystemUiTools.hideSystemUI(mContext);
            }
        }
    }

    @Override
    public void notFullScreen() {
        if (mContext instanceof Activity) {
            isFullScreen = false;
//            if (parentView == null) {
//                parentView = (ViewGroup) getParent();
//            }
            Activity activity = (Activity) mContext;
            ViewGroup vg = (ViewGroup) activity.getWindow().getDecorView();
            if (vg.indexOfChild(this) != -1) {
                vg.removeView(this);
            }
            if (parentView.indexOfChild(this) == -1) {
                parentView.addView(this);
            }
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SystemUiTools.showStatusBar(mContext);
                SystemUiTools.showSystemUI(mContext,SYSTEM_UI);
            }
        }
    }

    public IVideoFullScreenDo getDefaultVideoFullScreenDo(){
        isDefaultScreenDo = true;
        return this;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public abstract void startPlay();
}
