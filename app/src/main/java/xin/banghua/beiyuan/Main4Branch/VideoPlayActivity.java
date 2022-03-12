package xin.banghua.beiyuan.Main4Branch;

import static xyz.doikki.videoplayer.player.VideoView.STATE_IDLE;
import static xyz.doikki.videoplayer.player.VideoView.STATE_PLAYBACK_COMPLETED;
import static xyz.doikki.videoplayer.player.VideoView.STATE_PREPARED;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import xin.banghua.beiyuan.Adapter.LuntanList;
import xin.banghua.beiyuan.R;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.player.VideoView;

public class VideoPlayActivity extends AppCompatActivity {
    private static final String TAG = "Cannot invoke method length() on null object";
    VideoView videoView;
    LuntanList luntanList;
    ImageView back_img;
    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.release();
    }


    @Override
    public void onBackPressed() {
        if (!videoView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    int currentPosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        back_img = findViewById(R.id.back_img);
        back_img.setOnClickListener(v -> {
            onBackPressed();
        });

        luntanList = (LuntanList) getIntent().getSerializableExtra("luntanList");
        videoView = findViewById(R.id.player);
        //videoView.setUrl("https://oss.banghua.xin/audios/99999/2022/02/bxgA1v3XEOOU16eXazX3u3OkEEuvxO.mp4"); //设置视频地址
        videoView.setUrl(luntanList.getPostvideo()); //设置视频地址
        StandardVideoController controller = new StandardVideoController(this);
        final Boolean[] isMoreThanFiveSeconds = {false};
        controller.addDefaultControlComponent(new IControlComponent() {
            @Override
            public void attach(@NonNull ControlWrapper controlWrapper) {

            }

            @Nullable
            @Override
            public View getView() {
                return null;
            }

            @Override
            public void onVisibilityChanged(boolean isVisible, Animation anim) {

            }

            @Override
            public void onPlayStateChanged(int playState) {

            }

            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void setProgress(int duration, int position) {
                Log.d(TAG, "setProgress: 进度监听");
                currentPosition = position;
                if (position > 5000 && !isMoreThanFiveSeconds[0]) {
                    isMoreThanFiveSeconds[0] = true;
                    Log.d(TAG, "onProgressChanged: 播放视频进度超5秒" + position);
                    OkHttpInstance.moreThanFiveSeconds(luntanList.getId(), new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {

                        }
                    });
                }
            }

            @Override
            public void onLockStateChanged(boolean isLocked) {

            }
        },luntanList.getPosttitle(), false, new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        videoView.setVideoController(controller); //设置控制器
        videoView.setOnStateChangeListener(new VideoView.OnStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                Log.d(TAG, "onPlayStateChanged: 播放状态"+playState);
                if (playState == STATE_IDLE){
                    isMoreThanFiveSeconds[0] = false;
                    if (currentPosition >= 5000){
                        OkHttpInstance.playTime(luntanList.getId(),currentPosition+"",new OkHttpResponseCallBack() {
                            @Override
                            public void getResponseString(String responseString) {

                            }
                        });
                    }
                }else if (playState == STATE_PREPARED){
                    Log.d(TAG, "onPlayStateChanged: 播放一次");
                    OkHttpInstance.playOnce(luntanList.getId(),new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {

                        }
                    });
                }else if (playState == STATE_PLAYBACK_COMPLETED){
                    Log.d(TAG, "onPlayStateChanged: 播放完了");
                    isMoreThanFiveSeconds[0] = false;
                    OkHttpInstance.playCompleted(luntanList.getId(),videoView.getDuration()+"",new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {

                        }
                    });
                }
            }
        });
        currentPosition = 0;
        videoView.start();
    }
}