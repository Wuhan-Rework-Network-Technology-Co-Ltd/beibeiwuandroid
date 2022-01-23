package io.agora.chatroom;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yanzhenjie.permission.AndPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.activity.ChatRoomActivity;
import io.agora.chatroom.manager.RtmManager;
import io.agora.chatroom.model.Constant;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.player.VideoViewManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static io.agora.chatroom.ThreadUtils.runOnUiThread;

public class SendBarrageFrameLayout extends FrameLayout {
    private static final String TAG = "SendBarrageFrameLayout";
    public SendBarrageFrameLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SendBarrageFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SendBarrageFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SendBarrageFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private Context mContext;
    private View mView;


    public AppCompatActivity appCompatActivity;

    public void setAppCompatActivity(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.send_barrage_framelayout, this, true);

        ButterKnife.bind(this, mView);


        no_film.setVisibility(VISIBLE);
    }


    @BindView(R2.id.barrage_edit)
    EditText barrage_edit;//添加布局
    @BindView(R2.id.send_barrage)
    Button send_barrage;//添加布局
    @BindView(R2.id.switch_barrage)
    Button switch_barrage;//添加布局
    @BindView(R2.id.floating_btn)
    Button floating_btn;//添加布局
    @BindView(R2.id.player_container)
    FrameLayout playerContainer;//添加布局
    @BindView(R2.id.no_film)
    ImageView no_film;

    MyDanmakuView mMyDanmakuView;


    @BindView(R2.id.film_name)
    TextView film_name;//添加布局
    @BindView(R2.id.choose_film_btn)
    Button choose_film_btn;//添加布局
    @BindView(R2.id.syn_anchor)
    Button syn_anchor;//添加布局


    private RtmManager mRtmManager;
    private RtmClientListener mClientListener;


    FilmTypeFrameLayout film_type_framelayout;

    public FilmTypeFrameLayout getFilm_type_framelayout() {
        return film_type_framelayout;
    }

    public void setFilm_type_framelayout(FilmTypeFrameLayout film_type_framelayout) {
        this.film_type_framelayout = film_type_framelayout;
        choose_film_btn.setOnClickListener(view -> {
            choose_film_btn.setAlpha(0.5f);
            choose_film_btn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    choose_film_btn.setAlpha(1f);
                }
            },500);
            film_type_framelayout.initView(appCompatActivity);
            film_type_framelayout.setVisibility(VISIBLE);
        });
    }

    String channel_id;

    public String getChannel_id() {
        return channel_id;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;

        mRtmManager = RtmManager.instance(mContext);
        mClientListener = new MyRtmClientListenerService();
        mRtmManager.registerListener(mClientListener);

        syn_anchor.setOnClickListener(view -> {
            syn_anchor.setAlpha(0.5f);
            syn_anchor.postDelayed(new Runnable() {
                @Override
                public void run() {
                    syn_anchor.setAlpha(1f);
                }
            },500);
            RtmMessage message = RtmManager.instance(mContext).getRtmClient().createMessage();
            message.setText("syn_anchor_film");
            RtmManager.instance(mContext).getRtmClient().sendMessageToPeer(channel_id, message, RtmManager.instance(mContext).getSendMessageOptions(), new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: 通过声网消息通知对面更新声网用户属性");
                }
                @Override
                public void onFailure(ErrorInfo errorInfo) {
                }
            });
        });

        PIPManager mPIPManager = PIPManager.getInstance();
        if (mPIPManager.isStartFloatWindow()) {
            film_type_framelayout.setVisibility(GONE);

            no_film.setVisibility(GONE);

            VideoView mVideoView = getVideoViewManager().get(Tag.PIP);
//            mVideoView.setPlayerFactory(ExoMediaPlayerFactory.create());
            StandardVideoController controller = new StandardVideoController(mContext);

            controller.addDefaultControlComponent(mCurrentFilm.getName(), false, new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.d(TAG, "onProgressChanged: 点播视频进度"+progress);
                    currentProgress = progress;
                    if (progress%10==0){
                        Log.d(TAG, "onProgressChanged: 获取弹幕"+(progress / 10));
                        OkHttpInstance.getBarrageList(mCurrentFilm.getId(), progress / 10, new OkHttpResponseCallBack() {
                            @Override
                            public void getResponseString(String responseString) {
                                if (!responseString.equals("false")){
                                    barrageLists = JSON.parseArray(responseString,BarrageList.class);
                                }
                            }
                        });
                    }
                    if (barrageLists.size()>0){
                        Log.d(TAG, "onProgressChanged: 1获取弹幕"+progress);
                        for (BarrageList barrageList:barrageLists) {
                            if (barrageList.getProgress().equals(progress+"")){
                                switch (barrageList.getVip()){
                                    case "0":
                                        mMyDanmakuView.addDanmaku(barrageList.getBarrage_text(), true);
                                        break;
                                    case "1":
                                        mMyDanmakuView.addDanmakuWithDrawable(barrageList.getBarrage_text(),R.mipmap.barrage_vip);
                                        break;
                                    case "2":
                                        mMyDanmakuView.addDanmakuWithDrawable(barrageList.getBarrage_text(),R.mipmap.barrage_svip);
                                        break;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            mMyDanmakuView = new MyDanmakuView(mContext);
            controller.addControlComponent(mMyDanmakuView);
            Log.d("TAG", "setVideoController: 设置控制器3");
            mVideoView.setVideoController(controller);
            mPIPManager.stopFloatWindow();
            controller.setPlayerState(mVideoView.getCurrentPlayerState());
            controller.setPlayState(mVideoView.getCurrentPlayState());
            Utils.removeViewFormParent(mVideoView);
            playerContainer.addView(mVideoView);

            send_barrage.setOnClickListener(view -> {
                send_barrage.setAlpha(0.5f);
                send_barrage.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        send_barrage.setAlpha(1f);
                    }
                },500);
                if (!TextUtils.isEmpty(barrage_edit.getText().toString())){
                    mMyDanmakuView.addDanmaku(barrage_edit.getText().toString(), true);
                }
            });
            switch_barrage.setOnClickListener(view -> {
                if (mMyDanmakuView.isShown()){
                    mMyDanmakuView.hide();
                    switch_barrage.setBackground(mContext.getDrawable(R.mipmap.icon_barrage_hide));
                }else {
                    mMyDanmakuView.show();
                    switch_barrage.setBackground(mContext.getDrawable(R.mipmap.icon_barrage_show));
                }
            });

            floating_btn.setOnClickListener(view -> {
                AndPermission
                        .with(appCompatActivity)
                        .overlay()
                        .onGranted(data -> {
                            mPIPManager.startFloatWindow();
                            mPIPManager.resume();
                            appCompatActivity.finish();
                        })
                        .onDenied(data -> {

                        })
                        .start();
            });
        }
    }



    public static FilmList mCurrentFilm;
    int currentProgress = 0;

    List<BarrageList> barrageLists = new ArrayList<>();
    public void initShow(FilmList currentItem){
        Log.d(TAG, "initShow:  选中电影名"+currentItem.getName());
        film_type_framelayout.setVisibility(GONE);

        film_name.setText(currentItem.getName());

        mCurrentFilm = currentItem;
        no_film.setVisibility(GONE);


        PIPManager mPIPManager = PIPManager.getInstance();
        VideoView mVideoView = getVideoViewManager().get(Tag.PIP);

        StandardVideoController controller = new StandardVideoController(appCompatActivity);


        OkHttpInstance.getBarrageList(currentItem.getId(), 0, new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                if (!responseString.equals("false")){
                    barrageLists = JSON.parseArray(responseString,BarrageList.class);
                }
            }
        });
        controller.addDefaultControlComponent(currentItem.getName(), false, new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged: 点播视频进度"+progress);
                currentProgress = progress;
                if (progress%10==0){
                    Log.d(TAG, "onProgressChanged: 获取弹幕"+(progress / 10));
                    OkHttpInstance.getBarrageList(currentItem.getId(), progress / 10, new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {
                            if (!responseString.equals("false")){
                                barrageLists = JSON.parseArray(responseString,BarrageList.class);
                            }
                        }
                    });
                }
                if (barrageLists.size()>0){
                    Log.d(TAG, "onProgressChanged: 1获取弹幕"+progress);
                    for (BarrageList barrageList:barrageLists) {
                        if (barrageList.getProgress().equals(progress+"")){
                            switch (barrageList.getVip()){
                                case "0":
                                    mMyDanmakuView.addDanmaku(barrageList.getBarrage_text(), true);
                                    break;
                                case "1":
                                    mMyDanmakuView.addDanmakuWithDrawable(barrageList.getBarrage_text(),R.mipmap.barrage_vip);
                                    break;
                                case "2":
                                    mMyDanmakuView.addDanmakuWithDrawable(barrageList.getBarrage_text(),R.mipmap.barrage_svip);
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mMyDanmakuView = new MyDanmakuView(appCompatActivity);
        controller.addControlComponent(mMyDanmakuView);

        Log.d("TAG", "setVideoController: 设置控制器4");
        mVideoView.setVideoController(controller);
        if (mPIPManager.isStartFloatWindow()) {
            Log.d(TAG, "initShow: 正在悬浮播放");
            mPIPManager.stopFloatWindow();
            controller.setPlayerState(mVideoView.getCurrentPlayerState());
            controller.setPlayState(mVideoView.getCurrentPlayState());
        } else {
            mVideoView.release();
            Log.d(TAG, "initShow: 选中电影地址"+currentItem.getUrl()+currentItem.getFilmCurrentPosition());
            Intent intent = new Intent(mVideoView.getContext(), ChatRoomActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_ID, channel_id);
            intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_TYPE, ChatRoomActivity.WATCH_FILM_TYPE);
            mPIPManager.setIntent(intent);
            ImageView thumb = controller.findViewById(R.id.thumb);
            Glide.with(this)
                    .load(currentItem.getCover())
                    .placeholder(android.R.color.darker_gray)
                    .into(thumb);
            mVideoView.setUrl(currentItem.getUrl());
            mVideoView.start();
            if (currentItem.getFilmCurrentPosition()!=0){
                Log.d(TAG, "initShow: 执行了跳转");
                mVideoView.seekTo(currentItem.getFilmCurrentPosition());
            }
        }
        Utils.removeViewFormParent(mVideoView);
        playerContainer.addView(mVideoView);

        mVideoView.addOnStateChangeListener(new VideoView.SimpleOnStateChangeListener() {
            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == VideoView.STATE_PREPARED) {
                    //simulateDanmu();
                    if (currentItem.getFilmCurrentPosition()!=0){
                        mVideoView.seekTo(currentItem.getFilmCurrentPosition());
                    }
                } else if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
                    //mHandler.removeCallbacksAndMessages(null);
                }
            }
        });

        send_barrage.setOnClickListener(view -> {
            send_barrage.setClickable(false);
            send_barrage.setAlpha(0.5f);
            send_barrage.postDelayed(() -> {
                send_barrage.setClickable(true);
                send_barrage.setAlpha(1f);
                barrage_edit.setText("");
            },500);
            if (!TextUtils.isEmpty(barrage_edit.getText().toString())){
                String vip = "0";

                Integer current_timestamp = Integer.valueOf((int) (System.currentTimeMillis()/1000));
                int vip_time = Integer.parseInt(Constant.svip+"");
                if (vip_time > current_timestamp) {
                    vip = "2";
                    mMyDanmakuView.addDanmakuWithDrawable(barrage_edit.getText().toString(),R.mipmap.barrage_svip);
                } else {
                    vip_time = Integer.parseInt(Constant.vip+"");
                    if (vip_time > current_timestamp) {
                        vip = "1";
                        mMyDanmakuView.addDanmakuWithDrawable(barrage_edit.getText().toString(),R.mipmap.barrage_vip);
                    } else {
                        mMyDanmakuView.addDanmaku(barrage_edit.getText().toString(), true);
                    }
                }
                OkHttpInstance.sendBarrage(mCurrentFilm.getId(),barrage_edit.getText().toString(),currentProgress,vip);
            }
        });
        switch_barrage.setOnClickListener(view -> {
            if (mMyDanmakuView.isShown()){
                mMyDanmakuView.hide();
                switch_barrage.setBackground(mContext.getDrawable(R.mipmap.icon_barrage_hide));
            }else {
                mMyDanmakuView.show();
                switch_barrage.setBackground(mContext.getDrawable(R.mipmap.icon_barrage_show));
            }
        });

        floating_btn.setOnClickListener(view -> {
            AndPermission
                    .with(appCompatActivity)
                    .overlay()
                    .onGranted(data -> {
                        mPIPManager.startFloatWindow();
                        mPIPManager.resume();
                        appCompatActivity.finish();
                    })
                    .onDenied(data -> {

                    })
                    .start();
        });
    }


    public void unregisteredRtmClientListenerService(){
        if (mClientListener!=null)
           mRtmManager.unregisterListener(mClientListener);
    }

    public void floatingStart(){
        PIPManager mPIPManager = PIPManager.getInstance();
        AndPermission
                .with(appCompatActivity)
                .overlay()
                .onGranted(data -> {
                    mPIPManager.startFloatWindow();
                    mPIPManager.resume();
                    appCompatActivity.finish();
                })
                .onDenied(data -> {

                })
                .start();
    }
    protected VideoViewManager getVideoViewManager() {
        return VideoViewManager.instance();
    }

    class MyRtmClientListenerService implements RtmClientListener {

        @Override
        public void onConnectionStateChanged(int i, int i1) {

        }

        @Override
        public void onMessageReceived(RtmMessage rtmMessage, String s) {
            Log.d(TAG, "onMessageReceived: 同步视频信息"+getVideoViewManager().get(Tag.PIP).getCurrentPosition());
            if (rtmMessage.getText().contains("syn_anchor_film")){
                if (mCurrentFilm ==null){
                    return;
                }
                mCurrentFilm.setFilmCurrentPosition(getVideoViewManager().get(Tag.PIP).getCurrentPosition()+2000);
                RtmMessage message = RtmManager.instance(mContext).getRtmClient().createMessage();
                message.setText(new Gson().toJson(mCurrentFilm));
                RtmManager.instance(mContext).getRtmClient().sendMessageToPeer(s, message, RtmManager.instance(mContext).getSendMessageOptions(), new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: 同步视频信息成功");
                    }
                    @Override
                    public void onFailure(ErrorInfo errorInfo) {
                        Log.d(TAG, "onFailure: 同步视频信息失败"+errorInfo.toString());
                    }
                });
            }else if (rtmMessage.getText().contains("filmCurrentPosition")){
                runOnUiThread(()->initShow(JSON.parseObject(rtmMessage.getText(),FilmList.class)));
            }
        }

        @Override
        public void onImageMessageReceivedFromPeer(RtmImageMessage rtmImageMessage, String s) {

        }

        @Override
        public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {

        }

        @Override
        public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

        }

        @Override
        public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

        }

        @Override
        public void onTokenExpired() {

        }

        @Override
        public void onPeersOnlineStatusChanged(Map<String, Integer> map) {

        }
    }
}
