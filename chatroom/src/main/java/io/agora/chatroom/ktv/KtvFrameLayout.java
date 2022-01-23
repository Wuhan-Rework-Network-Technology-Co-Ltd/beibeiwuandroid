package io.agora.chatroom.ktv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.ObjectsCompat;
import androidx.lifecycle.Lifecycle;

import com.alibaba.fastjson.JSON;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle3.LifecycleProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.baselibrary.util.ToastUtile;
import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.chatroom.activity.ChatRoomActivity;
import io.agora.chatroom.manager.ChatRoomManager;
import io.agora.chatroom.model.Channel;
import io.agora.chatroom.model.Constant;
import io.agora.lrcview.LrcLoadUtils;
import io.agora.lrcview.bean.LrcData;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class KtvFrameLayout extends FrameLayout {
    private static final String TAG = "KtvFrameLayout";
    public KtvFrameLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public KtvFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KtvFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public KtvFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private AppCompatActivity mContext;
    private View mView;



    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.ktv_framelayout, this, true);

        ButterKnife.bind(this, mView);
    }


    public static MusicPlayer mMusicPlayer;

    public void setmMusicPlayer(MusicPlayer mMusicPlayer) {
        this.mMusicPlayer = mMusicPlayer;
    }

    public MusicPlayer getmMusicPlayer() {
        return mMusicPlayer;
    }


    private MusicPlayer.Callback mMusicCallback = new MusicPlayer.Callback() {

        @Override
        public void onPrepareResource() {
            Log.d(TAG, "onPrepareResource: ");
            lrcControlView.onPrepareStatus();
        }

        @Override
        public void onResourceReady(@NonNull MemberMusicModel music) {
            Log.d(TAG, "onResourceReady: ");
            File lrcFile = music.getFileLrc();
            LrcData data = LrcLoadUtils.parse(lrcFile);
            lrcControlView.getLrcView().setLrcData(data);
            lrcControlView.getPitchView().setLrcData(data);
        }

        @Override
        public void onMusicOpening() {
            Log.d(TAG, "onMusicOpening: ");
        }

        @Override
        public void onMusicOpenCompleted(int duration) {
            Log.d(TAG, "onMusicOpenCompleted: ");
            lrcControlView.getLrcView().setTotalDuration(duration);
        }

        @Override
        public void onMusicOpenError(int error) {
            Log.d(TAG, "onMusicOpenError: "+error);
        }

        @Override
        public void onMusicPlaing() {
            Log.d(TAG, "onMusicPlaing: ");
            lrcControlView.onPlayStatus();
        }

        @Override
        public void onMusicPause() {
            Log.d(TAG, "onMusicPause: ");
            lrcControlView.onPauseStatus();
        }

        @Override
        public void onMusicStop() {
            Log.d(TAG, "onMusicStop: ");

        }

        @Override
        public void onMusicCompleted() {
            Log.d(TAG, "onMusicCompleted: ");
            lrcControlView.getLrcView().reset();

            if (memberMusicModels.size()==0){
                playNewSong();
            }else {
                OkHttpInstance.deleteSong(memberMusicModels.get(0).getSongId(),memberMusicModels.get(0).getRoomId(), new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        Log.d(TAG, "getResponseString: 播放新歌3");
                        playNewSong();
                    }
                });
            }
        }

        @Override
        public void onMusicPositionChanged(long position) {
            Log.d(TAG, "onMusicPositionChanged: ");
            lrcControlView.getLrcView().updateTime(position);
            lrcControlView.getPitchView().updateTime(position);
        }
    };


    public void playNewSong(){
        Log.d(TAG, "playNewSong: 获取房间音乐1");
        OkHttpInstance.getSongList(ChatRoomActivity.currentChannel.getId(), new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                if (!responseString.equals("false")){
                    memberMusicModels = JSON.parseArray(responseString,MemberMusicModel.class);
                    onMusicChanged(memberMusicModels.get(0));
                }else {
                    Log.d(TAG, "getResponseString: 1剩余歌曲"+ChatRoomActivity.ktvView.memberMusicModels.size());
                    memberMusicModels.clear();
                    if (mMusicPlayer!=null)
                        mMusicPlayer.stop();
                }
            }
        });
    }

    private void onMusicChanged(@NonNull MemberMusicModel music) {
        Log.d(TAG, "onMusicChanged: 歌曲改变");
        lrcControlView.setMusic(music);

        if (ObjectsCompat.equals(music.getUserId(), Constant.sUserId+"")) {
            lrcControlView.setRole(LrcControlView.Role.Singer);
        } else {
            lrcControlView.setRole(LrcControlView.Role.Listener);
        }

        lrcControlView.onPrepareStatus();

        if (ObjectsCompat.equals(music.getUserId(),Constant.sUserId+"")) {
            preperMusic(music, true);
        } else {
            preperMusic(music, false);
        }
    }

    LifecycleProvider<Lifecycle.Event> mLifecycleProvider;
    private void preperMusic(final MemberMusicModel musicModel, boolean isSinger) {
        Log.d(TAG, "preperMusic: 触发准备");
        mLifecycleProvider = AndroidLifecycle.createLifecycleProvider(mContext);
        mMusicCallback.onPrepareResource();
        MusicResourceManager.Instance(mContext)
                .prepareMusic(musicModel, !isSinger)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mLifecycleProvider.bindToLifecycle())
                .subscribe(new SingleObserver<MemberMusicModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull MemberMusicModel musicModel) {
                        mMusicCallback.onResourceReady(musicModel);

                        if (isSinger) {
                            mMusicPlayer.open(musicModel);
                        } else {
                            mMusicPlayer.playByListener(musicModel);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtile.toastShort(mContext, R.string.ktv_lrc_load_fail);
                    }
                });
    }


    @BindView(R2.id.lrcControlView)
    LrcControlView lrcControlView;//添加布局

    ChatRoomManager mManager;

    public List<MemberMusicModel> memberMusicModels = new ArrayList<>();
    public void initShow(AppCompatActivity appCompatActivity, ChatRoomManager manager, Channel channel){
        //自定义部分
        mContext = appCompatActivity;
        mManager = manager;

        if (mMusicPlayer==null){
            mMusicPlayer = new MusicPlayer(mContext, manager.getRtcManager().getmRtcEngine());
        }

        mMusicPlayer.registerPlayerObserver(mMusicCallback);

        lrcControlView.setOnLrcClickListener(new LrcControlView.OnLrcActionListener() {
            @Override
            public void onProgressChanged(long time) {
                mMusicPlayer.seek(time);
            }

            @Override
            public void onStartTrackingTouch() {

            }

            @Override
            public void onStopTrackingTouch() {

            }

            @Override
            public void onSwitchOriginalClick() {
                toggleOriginal();
            }

            @Override
            public void onMenuClick() {
                showMusicMenuDialog();
            }

            @Override
            public void onPlayClick() {
                toggleStart();
            }

            @Override
            public void onChangeMusicClick() {
                showChangeMusicDialog();
            }
        });
        Log.d(TAG, "onMessageAdded: 播放新歌4");

        if (mMusicPlayer == null) {
            return;
        }else {
            if (!mMusicPlayer.isPlaying()){
                playNewSong();
            }
        }
    }

    private void toggleOriginal() {
        if (mMusicPlayer == null) {
            return;
        }

        if (mMusicPlayer.hasAccompaniment()) {
            mMusicPlayer.toggleOrigle();
        } else {
            lrcControlView.setSwitchOriginalChecked(true);
            ToastUtile.toastShort(mContext, R.string.ktv_error_cut);
        }
    }


    private boolean isEar = false;
    private int volMic = 100;
    private int volMusic = 100;
    private void showMusicMenuDialog() {
        if (mMusicPlayer == null) {
            return;
        }

        new MusicSettingDialog(mContext).initShow(isEar, volMic, volMusic, new MusicSettingDialog.Callback() {
            @Override
            public void onEarChanged(boolean isEar) {
                KtvFrameLayout.this.isEar = isEar;
                mManager.getRtcManager().getmRtcEngine().enableInEarMonitoring(isEar);
            }

            @Override
            public void onMicVolChanged(int vol) {
                KtvFrameLayout.this.volMic = vol;
                mMusicPlayer.setMicVolume(vol);
            }

            @Override
            public void onMusicVolChanged(int vol) {
                KtvFrameLayout.this.volMusic = vol;
                mMusicPlayer.setMusicVolume(vol);
            }
        });
    }

    private void toggleStart() {
        if (mMusicPlayer == null) {
            return;
        }

        mMusicPlayer.togglePlay();
    }

    public void showChangeMusicDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.ktv_room_change_music_title)
                .setMessage(R.string.ktv_room_change_music_msg)
                .setNegativeButton(R.string.ktv_cancel, null)
                .setPositiveButton(R.string.ktv_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //changeMusic();

                        if (mMusicPlayer == null) {
                            return;
                        }

                        mMusicPlayer.stop();
                    }
                })
                .show();
    }
}
