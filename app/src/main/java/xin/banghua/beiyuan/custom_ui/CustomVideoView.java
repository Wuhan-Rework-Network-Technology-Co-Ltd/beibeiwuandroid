package xin.banghua.beiyuan.custom_ui;

import static xyz.doikki.videoplayer.player.VideoView.STATE_IDLE;
import static xyz.doikki.videoplayer.player.VideoView.STATE_PLAYBACK_COMPLETED;
import static xyz.doikki.videoplayer.player.VideoView.STATE_PREPARED;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import xin.banghua.beiyuan.Adapter.LuntanList;
import xin.banghua.beiyuan.App;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.custom_ui.ad.UIUtils;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.player.VideoView;


public class CustomVideoView extends FrameLayout {
    private static final String TAG = "CustomVideoView";

    private static CustomVideoView instance;

    public static CustomVideoView getInstance(Context context,LuntanList list){
        if (instance == null){
            instance = new CustomVideoView(context);
        }
        UIUtils.removeFromParent(instance);
        instance.setData(list);
        return instance;
    }
    public static CustomVideoView getInstance(Context context){
        if (instance == null){
            instance = new CustomVideoView(context);
        }
        UIUtils.removeFromParent(instance);
        if (instance.videoView!=null)
            instance.videoView.release();

        return instance;
    }

    public CustomVideoView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CustomVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public Context mContext;
    public View mView;


    int currentPosition = 0;
    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.custom_video_view, this, true);

        videoView = mView.findViewById(R.id.video_view);
        final Boolean[] isMoreThanFiveSeconds = {false};

        StandardVideoController controller = new StandardVideoController(mContext);
        controller.setVisibility(GONE);
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
                if (position >= 5000 && !isMoreThanFiveSeconds[0]) {
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
        }, "", false, new SeekBar.OnSeekBarChangeListener() {
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
                    isMoreThanFiveSeconds[0] = false;
                    Log.d(TAG, "onPlayStateChanged: 播放完了");
                    OkHttpInstance.playCompleted(luntanList.getId(),videoView.getDuration()+"",new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {

                        }
                    });
                }
            }
        });
        cover_img = mView.findViewById(R.id.cover_img);
    }

    public VideoView videoView;


    LuntanList luntanList;
    NiceImageView cover_img;
    public void setData(LuntanList currentItem){
        cover_img.setVisibility(VISIBLE);
        if (Integer.parseInt(currentItem.getHeight())>=Integer.parseInt(currentItem.getWidth())){
            cover_img.getLayoutParams().height = 700;
            int width = Math.round(700*((Float.parseFloat(currentItem.getWidth())/Float.parseFloat(currentItem.getHeight()))));
            cover_img.getLayoutParams().width = width;
            Log.d(TAG, "onBindViewHolder: kuandu"+width);
        }else {
            cover_img.getLayoutParams().width = 700;
            int height = Math.round(700*((Float.parseFloat(currentItem.getHeight())/Float.parseFloat(currentItem.getWidth()))));
            cover_img.getLayoutParams().height = height;
            Log.d(TAG, "onBindViewHolder: 高度"+height);
        }
        Glide.with(App.getApplication()).load(currentItem.getCover()).into(cover_img);
        luntanList = currentItem;
        start();
        cover_img.postDelayed(new Runnable() {
            @Override
            public void run() {
                cover_img.setVisibility(GONE);
            }
        },500);
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                if (onPreparedListener!=null){
//                    onPreparedListener.onPrepared(mp);
//                }
//            }
//        });
//
//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                if (onCompletionListener!=null){
//                    onCompletionListener.onCompletion(mp);
//                    film_cover_img.setVisibility(VISIBLE);
//                }
//            }
//        });
    }

    public void setVideoPath(String path) {

    }

    public void start(){
        Log.d(TAG, "start: 1播放了"+luntanList.getPostvideo());
        videoView.release();
        currentPosition = 0;
        videoView.setUrl(luntanList.getPostvideo()); //设置视频地址
        //videoView.setLooping(true);
        videoView.start();
        Log.d(TAG, "start: 2播放了"+luntanList.getPostvideo());
    }

    public void pause(){
        Log.d(TAG, "pause: 暂停了"+luntanList.getPostvideo());
        videoView.pause();
    }

    MediaPlayer.OnPreparedListener onPreparedListener;
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l)
    {
        onPreparedListener = l;
    }

    MediaPlayer.OnCompletionListener onCompletionListener;
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l)
    {
        onCompletionListener = l;
    }
}
