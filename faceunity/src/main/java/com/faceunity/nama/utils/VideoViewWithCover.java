package com.faceunity.nama.utils;

import static xyz.doikki.videoplayer.player.VideoView.STATE_PLAYING;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xin.banghua.faceunity.R;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.player.VideoView;

public class VideoViewWithCover extends FrameLayout {
    private static final String TAG = "FrameLayoutExample";

    public VideoViewWithCover(@NonNull Context context) {
        super(context);
    }

    public VideoViewWithCover(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VideoViewWithCover(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private Context mContext;
    private View mView;

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.video_view_with_cover, this, true);

        video_view = mView.findViewById(R.id.video_view);
        video_cover = mView.findViewById(R.id.video_cover);
        video_view.setPlayerFactory(ExoMediaPlayerFactory.create());
        video_view.setOnStateChangeListener(new VideoView.OnStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                Log.d(TAG, "onPlayStateChanged: 开始播放状态"+playState);
                if (playState == STATE_PLAYING){
                    Log.d(TAG, "onPlayStateChanged: 开始播放");
                    video_cover.setVisibility(GONE);
                }
            }
        });
    }


    public VideoView video_view;
    public ImageView video_cover;
    public void initShow(){
        //自定义部分


    }
}
