# lpp-advanvideo
一个基于ijkPlayer的视频播放器，解耦耦播放器能力，播放器控件以及播放器控制条的，提供大家一种设计的理念。<br>
写这个播放器的原因是基于本人在项目组碰到的一系列有关播放器相关的功能做一个具体的总结，相关的业务主要是项目中有很多视频业务，音频业务以及底层播放能力的选择。<br>
首先这个播放器的设计理念如下入:<br>
`
NormalVideoControllerView \ NormalAudioCotroller  LppVideoPlayer      LppAudioPlayer      IJKPlayer
  extends                                           extends             extends             extends
                                                       AbsVideoPlayer
                                                        extends
    AbsMediaControllView                                  AbsMediaPlayer                      IMediaAblity
`
