package io.agora.chatroom.manager;

import static io.agora.chatroom.ThreadUtils.runOnUiThread;
import static io.agora.rtc.Constants.CONNECTION_CHANGED_BANNED_BY_SERVER;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import io.agora.chatroom.R;
import io.agora.chatroom.activity.ChatRoomActivity;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public final class RtcManager {
    public static List<IRtcEngineEventHandler> iRtcEngineEventHandlerList = new ArrayList<>();

    public interface RtcEventListener {
        void onJoinChannelSuccess(String channelId);

        void onUserOnlineStateChanged(int uid, boolean isOnline);

        void onUserMuteAudio(int uid, boolean muted);

        void onAudioMixingStateChanged(boolean isPlaying);

        void onAudioVolumeIndication(int uid, int volume);
    }

    private final String TAG = RtcManager.class.getSimpleName();

    private static RtcManager instance;

    private Context mContext;
    private RtcEventListener mListener;
    private RtcEngine mRtcEngine;

    public RtcEngine getmRtcEngine() {
        return mRtcEngine;
    }

    private int mUserId;

    private RtcManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static RtcManager instance(Context context) {
        if (instance == null) {
            synchronized (RtcManager.class) {
                if (instance == null)
                    instance = new RtcManager(context);
            }
        }
        return instance;
    }

    public void setListener(RtcEventListener listener) {
        mListener = listener;
    }

    public void init() {
        if (mRtcEngine == null) {
            try {
                mRtcEngine = RtcEngine.create(mContext, mContext.getString(R.string.app_id), mEventHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mRtcEngine != null) {
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY, Constants.AUDIO_SCENARIO_CHATROOM_ENTERTAINMENT);
            mRtcEngine.enableAudioVolumeIndication(500, 3, false);
        }
    }

    void joinChannel(String channelId, int userId) {
        Log.d(TAG, "joinChannel: 登录频道"+channelId);
        if (mRtcEngine != null){
            OkHttpInstance.getRTCToken(channelId, new OkHttpResponseCallBack() {
                @Override
                public void getResponseString(String responseString) {
                    mRtcEngine.joinChannel(responseString, channelId, null, userId);
                }
            });
        }
    }

    void setClientRole(int role) {
        if (mRtcEngine != null)
            mRtcEngine.setClientRole(role);
    }

    public void muteAllRemoteAudioStreams(boolean muted) {
        if (mRtcEngine != null)
            mRtcEngine.muteAllRemoteAudioStreams(muted);
    }

    void muteLocalAudioStream(boolean muted) {
        if (mRtcEngine != null)
            mRtcEngine.muteLocalAudioStream(muted);
        if (mListener != null)
            mListener.onUserMuteAudio(mUserId, muted);
    }

    public void startAudioMixing(String filePath) {
        if (mRtcEngine != null) {
            Log.d(TAG, "open: 调用混音2");
            mRtcEngine.startAudioMixing(filePath, false, false, 1);
            mRtcEngine.adjustAudioMixingVolume(15);
        }
    }

    public void stopAudioMixing() {
        if (mRtcEngine != null)
            mRtcEngine.stopAudioMixing();
    }

    public void setVoiceChanger(int type) {
        if (mRtcEngine != null)
            mRtcEngine.setParameters(String.format(Locale.getDefault(), "{\"che.audio.morph.voice_changer\": %d}", type));
    }

    public void setReverbPreset(int type) {
        if (mRtcEngine != null)
            mRtcEngine.setParameters(String.format(Locale.getDefault(), "{\"che.audio.morph.reverb_preset\": %d}", type));
    }

    void leaveChannel() {
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
            setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        }
    }


    OkHttpResponseCallBack okHttpResponseCallBack;
    public void setChatRoomActivity(OkHttpResponseCallBack okHttpResponseCallBack) {
        this.okHttpResponseCallBack = okHttpResponseCallBack;
    }
    private IRtcEngineEventHandler mEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onStreamMessage(int uid, int streamId, byte[] data) {
            for (IRtcEngineEventHandler handler : iRtcEngineEventHandlerList) {
                handler.onStreamMessage(uid,streamId,data);
            }
        }
        @Override
        public void onConnectionStateChanged(int state, int reason) {
            super.onConnectionStateChanged(state, reason);
            Log.d(TAG, "onConnectionStateChanged: 频道已被封禁"+state+"|"+reason);
            if (reason==CONNECTION_CHANGED_BANNED_BY_SERVER){
                Log.d(TAG, "onConnectionStateChanged: 频道已被封禁");
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mContext,"频道已被封禁",Toast.LENGTH_LONG).show();
                        try {
                            leaveChannel();
                            ChatRoomActivity.chatRoomActivity.finish();
                        }catch (Exception e){
                            Log.e(TAG, "run: 抛出异常");
                        }
                    }
                });
            }
            for (IRtcEngineEventHandler handler : iRtcEngineEventHandlerList) {
                handler.onConnectionStateChanged(state,reason);
            }
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.i(TAG, String.format("onJoinChannelSuccess %s %d", channel, uid));
            for (IRtcEngineEventHandler handler : iRtcEngineEventHandlerList) {
                handler.onJoinChannelSuccess(channel,uid,elapsed);
            }
            mUserId = uid;
            if (mListener != null)
                mListener.onJoinChannelSuccess(channel);
        }

        @Override
        public void onClientRoleChanged(int oldRole, int newRole) {
            super.onClientRoleChanged(oldRole, newRole);
            Log.i(TAG, String.format("onClientRoleChanged %d %d", oldRole, newRole));
            for (IRtcEngineEventHandler handler : iRtcEngineEventHandlerList) {
                handler.onClientRoleChanged(oldRole, newRole);
            }
            if (mListener != null) {
                if (newRole == Constants.CLIENT_ROLE_BROADCASTER)
                    mListener.onUserOnlineStateChanged(mUserId, true);
                else if (newRole == Constants.CLIENT_ROLE_AUDIENCE)
                    mListener.onUserOnlineStateChanged(mUserId, false);
            }
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            Log.i(TAG, String.format("onUserJoined %d", uid));
            for (IRtcEngineEventHandler handler : iRtcEngineEventHandlerList) {
                handler.onUserJoined(uid, elapsed);
            }
            if (mListener != null)
                mListener.onUserOnlineStateChanged(uid, true);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            Log.i(TAG, String.format("onUserOffline %d", uid));
            for (IRtcEngineEventHandler handler : iRtcEngineEventHandlerList) {
                handler.onUserOffline(uid, reason);
            }
            if (mListener != null)
                mListener.onUserOnlineStateChanged(uid, false);
        }

        @Override
        public void onUserMuteAudio(int uid, boolean muted) {
            super.onUserMuteAudio(uid, muted);
            Log.i(TAG, String.format("onUserMuteAudio %d %b", uid, muted));
            for (IRtcEngineEventHandler handler : iRtcEngineEventHandlerList) {
                handler.onUserMuteAudio(uid, muted);
            }
            if (mListener != null)
                mListener.onUserMuteAudio(uid, muted);
        }

        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            super.onAudioVolumeIndication(speakers, totalVolume);
            for (IRtcEngineEventHandler handler : iRtcEngineEventHandlerList) {
                handler.onAudioVolumeIndication(speakers, totalVolume);
            }
            for (AudioVolumeInfo info : speakers) {
                if (info.volume > 0) {
                    int uid = info.uid == 0 ? mUserId : info.uid;
                    if (mListener != null)
                        mListener.onAudioVolumeIndication(uid, info.volume);
                }
            }
        }

        @Override
        public void onAudioMixingStateChanged(int state, int errorCode) {
            super.onAudioMixingStateChanged(state, errorCode);
            for (IRtcEngineEventHandler handler : iRtcEngineEventHandlerList) {
                handler.onAudioMixingStateChanged(state, errorCode);
            }
            if (mListener != null)
                mListener.onAudioMixingStateChanged(state == Constants.MEDIA_ENGINE_AUDIO_EVENT_MIXING_PLAY);
        }
    };

}
