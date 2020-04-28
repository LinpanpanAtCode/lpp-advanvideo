package com.lpp.lppvideodemo;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lpp.lppvideoplayer.controller.factory.MediaControllerFactory;
import com.lpp.lppvideoplayer.controller.widget.builder.VideoControllerBuilder;
import com.lpp.lppvideoplayer.player.video.widget.LppVideoPlayer;


public class MainActivity extends AppCompatActivity {
    LppVideoPlayer videoPlayer;
    String[] videoPath = new String[]{"godzilla2.mp4","七里香.mp4","funny.mp4","复仇者联盟4：终局之战.Avengers.Endgame.2019.BD1080P.英语中英双字.BTDX8.LPP.TEST.VIDEO.TITLE.mp4"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        videoPlayer = findViewById(R.id.player);
        VideoControllerBuilder videoControllerBuilder = new VideoControllerBuilder()
                .setNeedFullScreenBtn(true)
                .setFullScreenDo(videoPlayer.getDefaultVideoFullScreenDo())
                .setControllerMode(VideoControllerBuilder.CONTROLLER_MODE_NORMAL)
                .setNeedBackView(true)
                .setBackDo(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (videoPlayer.isFullScreen()){
                            videoPlayer.notFullScreen();
                        }else{
                            finish();
                        }
                    }
                });
        videoControllerBuilder
                .setNeedMoreView(true)
                .setMoreDo(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this,"click more view",Toast.LENGTH_SHORT).show();
                    }
                });
        videoPlayer.attachVideoController(MediaControllerFactory.getNormalVideoController(MainActivity.this,videoPlayer.getAbility(),videoControllerBuilder));
        String path =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/";
        try {
            videoPlayer.setUrl(path + "godzilla2.mp4");
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlayer.startPlay();
            }
        });
    }

    @Override
    public void finish() {
        Log.e("lpp-videoplayer","finish>>>>>>>>>>>>>>>lpp");
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (videoPlayer.isFullScreen()){
                videoPlayer.notFullScreen();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoPlayer != null){
            videoPlayer.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoPlayer != null){
            videoPlayer.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoPlayer != null){
            videoPlayer.onDestroy();
            videoPlayer = null;
        }
    }
}
