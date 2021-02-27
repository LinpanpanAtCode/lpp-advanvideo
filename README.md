# lpp-advanvideo
一个基于ijkPlayer的视频播放器，解耦耦播放器能力，播放器控件以及播放器控制条的，提供大家一种设计的理念。<br>
写这个播放器的原因是基于本人在项目组碰到的一系列有关播放器相关的功能做一个具体的总结，相关的业务主要是项目中有很多视频业务，音频业务以及底层播放能力的选择。<br>
## 前言
由于在我们项目单中，多媒体的业务场景特别的多，所以提供可自主定制以及扩展的播放器是一个根基基础。在参阅许多优秀的播放器实现以及结合自己的业务场景，我讲播放器的设计做如下分层：<br>
1.能力和行为的解耦。定义能力解耦，通过能力层去实现该接口，但是不关心具体的业务逻辑和调用逻辑，仅提供能力实现。<br>
2.行为和业务的解耦。定义抽象媒体控制器，根据具体的业务去实现对应的视频控制器，音频控制器。<br>

这个播放器的设计理念如下入:<br>
```
  /** 
   * AbsMediaControllerView是一个抽象的播放器的控制器，他声明了一些必须实现的方法（例如：快进，后退，暂停，播放等等）
   * NormalVideoControllerView 继承AbsMediaControllerView，实现了播放器里面声明的抽象方法，并且提供了视频播放所需的一些功能
   * NormalAudioCotroller 继承AbsMediaControllView，实现了播放器里面声明的抽象方法，并且提供了音乐播放器所需的一些功能
   **/
  NormalVideoControllerView \ NormalAudioCotrollerView
    extends
      AbsMediaControllView
  /**
   * AbsMediaPlayer是一个抽象的播放器，他使用模板模式来规定子类播放器必须实现的方法以及提供的播放控制器和媒体能力
   * AbsVideoPlayer是继承AbsMediaPlayer的一个抽象的视频播放器，他里面声明了视频所需的展示视图以及提供了全屏和非全屏之前切换的能力
   * LppVideoPlayer是继承了AbsVideoPlayer的一个视频播放器，注入了视频播放器所需的播放器的能力以及播放控制器
   * LppAudioPlayer是继承了AbsMediaPlayer的一个视频播放器，注入了音乐播放器所需的播放器的能力以及播放控制器
   **/
  LppVideoPlayer      LppAudioPlayer
    extends
      AbsVideoPlayer
        extends         extends
          AbsMediaPlayer
  /**
   * IMediaAblity是一个接口能力，他讲具体的能力层和播放器分离，定义了媒体能力具体需要实现的方法
   * IjkMediaAblity实现了IMediaAblity，并且使用ijkPlayer提供必要的能力基础
   **/
  IJKPlayer
    extends
       IMediaAblity
  /**
   * AbsMediaPlayer持有一个抽象的播放器，使用IMediaAblity进行播放能力的使用
   **/
  AbsMediaPlayer{
    AbsMediaControllView mediaControllerView
    IMediaAblity  mediaAbility;
  }
```
## v1.0.0
定义基础播放能力接口、播放器抽象类、播放器控制器高度解耦，使得开发者可高度定制符合业务需求的各类控制器。
  ```
   首先，在你的xml布局里面添加如下代码
   <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <com.lpp.lppvideoplayer.player.video.widget.LppVideoPlayer
            android:id="@+id/player"
            android:layout_width="match_parent"
            android:layout_height="@dimen/dp_220"
            android:background="#000000"
            android:visibility="visible" />
    </FrameLayout>
   其次，通过构建者模式添加播放器相应的配置
   VideoControllerBuilder videoControllerBuilder = new VideoControllerBuilder()
                .setNeedFullScreenBtn(true)// 设置是否需要全屏切换按钮
                .setFullScreenDo(videoPlayer.getDefaultVideoFullScreenDo())// 设置全屏点击事件，这里使用了自带的全屏点击事件
                .setControllerMode(VideoControllerBuilder.CONTROLLER_MODE_NORMAL)// 设置控制器的模式
                .setNeedBackView(true)// 设置是否需要返回按钮
                .setBackDo(new View.OnClickListener() {// 设置返回按钮的点击事件
                    @Override
                    public void onClick(View v) {
                        if (videoPlayer.isFullScreen()){
                            videoPlayer.notFullScreen();
                        }else{
                            finish();
                        }
                    }
                })
                .setNeedMoreView(true)// 设置是否需要更多按钮
                .setMoreDo(new View.OnClickListener() {// 设置更多按钮点击事件
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this,"click more view",Toast.LENGTH_SHORT).show();
                    }
                });
   // 注入控制器
   videoPlayer.attachVideoController(
      MediaControllerFactory.getNormalVideoController(MainActivity.this,videoPlayer.getAbility(),videoControllerBuilder));
   // 设置播放链接
   videoPlayer.setUrl(videoUrl);
   // 开始播放
   videoPlayer.startPlay();
   // 添加事件监听
   videoPlayer.setListener();
  ```
  
  ##Log
  v1.0.0 定义了Lpp-AdvancePlayer的接口框架，并使用ijkplayer提供了一个LppVideoPlayer的使用，实现了NormalVideoControllerView的视频播放控制器<br>
  ps:如果需要定制自己的视频控制器，请使用继承AbsMediaControllerView方式,可参考
  {@link com.lpp.lppvideoplayer.controller.widget.NormalVideoControllerView}
  ```
    继承实现自己的播放器
    public class YourSelfControllerView extends AbsMediaControllView {...}
    注入播放器控制器
    videoPlayer.attachVideoController(YourSelfControllerView);
  ```
  后续：状态转移播放器，悬浮播放器，仿网易云音乐音乐播放器
   
  ##Log 2021/02/27
  在这里解释播放器几个重要的点：
  1. 如何统一的进行全屏和非全屏的切换，在项目中可能有这么几个场景。视频详情页，上方是视频，下方是视频简介。视频列表，一个页面有多个视频源。ViewPager+fragment的视频。那么不同场景的视频的全屏缩放来说，在业务层面的处理似乎是不一样的，我们为了统一管理这个全屏和非全屏之间的切换。这里应用到了activity对应的视图树。我们都知道，每一个activity对应一个window，具体的实现是PhoneWindow，PhoneWindow下面有一个根视图叫做decorView，decorView是一个framenLayout的子类，所以后加上去的view会呈现在最上方，所以当点击全屏和非全屏切换的时候，将对应的videoView从源父控件移除，再添加到decorView当中，切换非全屏的时候再重新移回原来的父控件当中。
  2. 如何做到声音和画面分离的。ijkplayer本身只是提供了多媒体的解码操作，当解码之后的播放，它是仅仅播放声音而不播放画面的，画面的数据是通过设置sufaceView或者textureView来进行接收的。这里注意一个点，为了防止画面的横竖屏切换的时候会重复创建对应的surfaceView，这里需要将surfaceView进行缓存，在每一次的回调中都使用同一个surfaceView，这样能解决画面黑屏一闪的问题。
  
