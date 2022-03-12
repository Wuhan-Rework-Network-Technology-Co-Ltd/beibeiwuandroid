package io.agora.chatroom.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import io.agora.chatroom.R;
import io.agora.chatroom.util.AlertUtil;
import io.agora.rtm.ChannelAttributeOptions;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.LocalInvitation;
import io.agora.rtm.RemoteInvitation;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmAttribute;
import io.agora.rtm.RtmCallEventListener;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.SendMessageOptions;

public final class RtmManager {

    public interface RtmEventListener {
        void onChannelAttributesLoaded();

        void onChannelAttributesUpdated(Map<String, String> attributes);

        void onInitMembers(List<RtmChannelMember> members);

        void onMemberJoined(String userId, Map<String, String> attributes);

        void onMemberLeft(String userId);

        void onMessageReceived(RtmMessage message);
    }

    private final String TAG = RtmManager.class.getSimpleName();

    private static RtmManager instance;

    private Context mContext;
    private RtmEventListener mListener;
    private RtmClient mRtmClient;
    private RtmChannel mRtmChannel;

    public RtmChannel getmRtmChannel() {
        return mRtmChannel;
    }

    private boolean mIsLogin;


    private SendMessageOptions mSendMsgOptions;

    public static List<RtmClientListener> rtmClientListenerList = new ArrayList<>();
    public static List<RtmChannelListener> rtmChannelListenerList = new ArrayList<>();
    public static List<RtmCallEventListener> rtmCallEventListenerList = new ArrayList<>();

    private RtmManager(Context context) {
        mContext = context.getApplicationContext();

    }

    public static RtmManager instance(Context context) {
        if (instance == null) {
            synchronized (RtmManager.class) {
                if (instance == null)
                    instance = new RtmManager(context);
            }
        }
        return instance;
    }

    public void logout(){
        if (mRtmClient!=null)
            mRtmClient.logout(null);
        mIsLogin = false;
    }

    public void logoutWithCallback(@Nullable ResultCallback resultCallback){
        if (mRtmClient!=null)
            mRtmClient.logout(resultCallback);
        mIsLogin = false;
    }

    public RtmClient getRtmClient(){
        return mRtmClient;
    }
    public SendMessageOptions getSendMessageOptions(){
        mSendMsgOptions = new SendMessageOptions();
        mSendMsgOptions.enableHistoricalMessaging = true;
        mSendMsgOptions.enableOfflineMessaging = true;
        return mSendMsgOptions;
    }

    public void setListener(RtmEventListener listener) {
        mListener = listener;
    }

    public void init() {
        if (mRtmClient == null) {
            try {
                mRtmClient = RtmClient.createInstance(mContext, mContext.getString(R.string.app_id), mClientListener);
                mRtmClient.getRtmCallManager().setEventListener(rtmCallEventListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    String rtmToken = "";
    void login(int userId, ResultCallback<Void> callback) {
        if (mRtmClient != null) {
            if (mIsLogin) {
                if (callback != null)
                    callback.onSuccess(null);
                return;
            }

            OkHttpInstance.getRTMToken(new OkHttpResponseCallBack() {
                @Override
                public void getResponseString(String responseString) {
                    rtmToken = responseString;
                    mRtmClient.login(responseString, String.valueOf(userId), new ResultCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "rtm login success");
                            mIsLogin = true;

                            if (callback != null)
                                callback.onSuccess(aVoid);
                        }

                        @Override
                        public void onFailure(ErrorInfo errorInfo) {
                            Log.e(TAG, String.format("rtm join %s", errorInfo.getErrorDescription()));
                            mIsLogin = false;

                            if (callback != null)
                                callback.onFailure(errorInfo);
                        }
                    });
                }
            });
        }
    }

    void joinChannel(String channelId, ResultCallback<Void> callback) {
        if (mRtmClient != null) {
            leaveChannel();

            Log.w(TAG, String.format("joinChannel %s", channelId));

            try {
                RtmChannel rtmChannel = mRtmClient.createChannel(channelId, mChannelListener);
                if (rtmChannel == null) return;
                rtmChannel.join(new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "rtm join success");
                        mRtmChannel = rtmChannel;

                        getChannelAttributes(channelId);
                        getMembers();

                        if (callback != null)
                            callback.onSuccess(aVoid);
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {
                        Log.e(TAG, String.format("rtm join %s", errorInfo.getErrorDescription()));
                        AlertUtil.showToast("RTM login failed, see the log to get more info");

                        mRtmChannel = rtmChannel;

                        if (callback != null)
                            callback.onFailure(errorInfo);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void getChannelAttributes(String channelId) {
        if (mRtmClient != null) {
            mRtmClient.getChannelAttributes(channelId, new ResultCallback<List<RtmChannelAttribute>>() {
                @Override
                public void onSuccess(List<RtmChannelAttribute> attributeList) {
                    processChannelAttributes(attributeList);

                    if (mListener != null)
                        mListener.onChannelAttributesLoaded();
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    Log.e(TAG, String.format("getChannelAttributes %s", errorInfo.getErrorDescription()));
                }
            });
        }
    }

    private void processChannelAttributes(List<RtmChannelAttribute> attributeList) {
        if (attributeList != null) {
            Map<String, String> attributes = new HashMap<>();
            for (RtmChannelAttribute attribute : attributeList) {
                attributes.put(attribute.getKey(), attribute.getValue());
            }

            if (mListener != null)
                mListener.onChannelAttributesUpdated(attributes);
        }
    }

    private void getMembers() {
        if (mRtmChannel != null) {
            mRtmChannel.getMembers(new ResultCallback<List<RtmChannelMember>>() {
                @Override
                public void onSuccess(List<RtmChannelMember> rtmChannelMembers) {
                    if (mListener != null)
                        mListener.onInitMembers(rtmChannelMembers);

                    for (RtmChannelMember member : rtmChannelMembers) {
                        getUserAttributes(member.getUserId());
                    }
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {

                }
            });
        }
    }

    private void getUserAttributes(String userId) {
        if (mRtmClient != null) {
            mRtmClient.getUserAttributes(userId, new ResultCallback<List<RtmAttribute>>() {
                @Override
                public void onSuccess(List<RtmAttribute> rtmAttributes) {
                    Log.d(TAG, String.format("getUserAttributes %s", rtmAttributes.toString()));

                    processUserAttributes(userId, rtmAttributes);
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    Log.e(TAG, String.format("getUserAttributes %s", errorInfo.getErrorDescription()));
                }
            });
        }
    }

    private void processUserAttributes(String userId, List<RtmAttribute> attributeList) {
        if (attributeList != null) {
            Map<String, String> attributes = new HashMap<>();
            for (RtmAttribute attribute : attributeList) {
                attributes.put(attribute.getKey(), attribute.getValue());
            }

            if (mListener != null)
                mListener.onMemberJoined(userId, attributes);
        }
    }

    void setLocalUserAttributes(String key, String value) {
        if (mRtmClient != null) {
            RtmAttribute attribute = new RtmAttribute(key, value);
            Log.d(TAG, "setLocalUserAttributes: 更新用户属性");
            mRtmClient.addOrUpdateLocalUserAttributes(Collections.singletonList(attribute), new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "setLocalUserAttributes: 更新用户属性成功");
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    Log.d(TAG, "setLocalUserAttributes: 更新用户属性失败"+errorInfo);
                }
            });
        }
    }

    void addOrUpdateChannelAttributes(String key, String value, ResultCallback<Void> callback) {
        if (mRtmClient != null) {
            if (mRtmChannel == null) {
                AlertUtil.showToast("RTM not login, see the log to get more info");
                return;
            }

            RtmChannelAttribute attribute = new RtmChannelAttribute(key, value);
            mRtmClient.addOrUpdateChannelAttributes(mRtmChannel.getId(), Collections.singletonList(attribute), options(), new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, String.format("addOrUpdateChannelAttributes %s %s", key, value));

                    if (callback != null)
                        callback.onSuccess(aVoid);
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    Log.e(TAG, String.format("addOrUpdateChannelAttributes %s %s %s", key, value, errorInfo.getErrorDescription()));

                    if (callback != null)
                        callback.onFailure(errorInfo);
                }
            });
        }
    }

    private ChannelAttributeOptions options() {
        return new ChannelAttributeOptions(true);
    }

    void deleteChannelAttributesByKey(String key) {
        if (mRtmClient != null) {
            if (mRtmChannel == null) {
                AlertUtil.showToast("RTM not login, see the log to get more info");
                return;
            }

            mRtmClient.deleteChannelAttributesByKeys(mRtmChannel.getId(), Collections.singletonList(key), options(), null);
        }
    }

    public void sendMessage(String content, ResultCallback<Void> callback) {
        if (mRtmClient != null) {
            RtmMessage message = mRtmClient.createMessage(content);
            if (mRtmChannel != null) {
                mRtmChannel.sendMessage(message, new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, String.format("sendMessage %s", content));

                        if (callback != null)
                            callback.onSuccess(aVoid);
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {
                        Log.e(TAG, String.format("sendMessage %s", errorInfo.getErrorDescription()));

                        if (callback != null)
                            callback.onFailure(errorInfo);
                    }
                });
            }
        }
    }

    public void sendMessageToPeer(String userId, String content, ResultCallback<Void> callback) {
        if (TextUtils.isEmpty(userId)) return;

        if (mRtmClient != null) {
            RtmMessage message = mRtmClient.createMessage(content);
            mRtmClient.sendMessageToPeer(userId, message, null, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, String.format("sendMessageToPeer %s %s", userId, content));

                    if (callback != null)
                        callback.onSuccess(aVoid);
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    Log.e(TAG, String.format("sendMessageToPeer %s", errorInfo.getErrorDescription()));

                    if (callback != null)
                        callback.onFailure(errorInfo);
                }
            });
        }
    }

    void leaveChannel() {
        if (mRtmChannel != null) {
            Log.w(TAG, String.format("leaveChannel %s", mRtmChannel.getId()));

            mRtmChannel.leave(null);
            mRtmChannel.release();
            mRtmChannel = null;
        }
    }



    private RtmClientListener mClientListener = new RtmClientListener() {
        @Override
        public void onConnectionStateChanged(int i, int i1) {
            for (RtmClientListener listener : rtmClientListenerList) {
                listener.onConnectionStateChanged(i, i1);
            }
        }

        @Override
        public void onMessageReceived(RtmMessage rtmMessage, String s) {
            Log.i(TAG, String.format("onPeerMessageReceived %s %s", rtmMessage.getText(), s));
            Log.d(TAG, "onMessageReceived: 收到个人消息"+rtmMessage.getText().toString());
            if (mListener != null)
                mListener.onMessageReceived(rtmMessage);


            if (rtmClientListenerList.isEmpty()) {
                // If currently there is no callback to handle this
                // message, this message is unread yet. Here we also
                // take it as an offline message.
                //mMessagePool.insertOfflineMessage(rtmMessage, s);
            } else {
                for (RtmClientListener listener : rtmClientListenerList) {
                    listener.onMessageReceived(rtmMessage, s);
                }
            }
        }

        @Override
        public void onImageMessageReceivedFromPeer(RtmImageMessage rtmImageMessage, String s) {
            if (rtmClientListenerList.isEmpty()) {
                // If currently there is no callback to handle this
                // message, this message is unread yet. Here we also
                // take it as an offline message.
                //mMessagePool.insertOfflineMessage(rtmImageMessage, peerId);
            } else {
                for (RtmClientListener listener : rtmClientListenerList) {
                    listener.onImageMessageReceivedFromPeer(rtmImageMessage, s);
                }
            }
        }

        @Override
        public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {
            if (rtmClientListenerList.isEmpty()) {
                // If currently there is no callback to handle this
                // message, this message is unread yet. Here we also
                // take it as an offline message.
                //mMessagePool.insertOfflineMessage(rtmFileMessage, s);
            } else {
                for (RtmClientListener listener : rtmClientListenerList) {
                    listener.onFileMessageReceivedFromPeer(rtmFileMessage, s);
                }
            }
        }

        @Override
        public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {
            for (RtmClientListener listener : rtmClientListenerList) {
                listener.onMediaUploadingProgress(rtmMediaOperationProgress, l);
            }
        }

        @Override
        public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {
            for (RtmClientListener listener : rtmClientListenerList) {
                listener.onMediaDownloadingProgress(rtmMediaOperationProgress, l);
            }
        }

        @Override
        public void onTokenExpired() {
            for (RtmClientListener listener : rtmClientListenerList) {
                listener.onTokenExpired();
            }
            mRtmClient.renewToken(rtmToken, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {

                }
            });
        }

        @Override
        public void onPeersOnlineStatusChanged(Map<String, Integer> map) {
            for (RtmClientListener listener : rtmClientListenerList) {
                listener.onPeersOnlineStatusChanged(map);
            }
        }
    };

    private RtmChannelListener mChannelListener = new RtmChannelListener() {
        @Override
        public void onMemberCountUpdated(int i) {
            for (RtmChannelListener listener : rtmChannelListenerList) {
                listener.onMemberCountUpdated(i);
            }
        }

        @Override
        public void onAttributesUpdated(List<RtmChannelAttribute> list) {
            Log.i(TAG, "onAttributesUpdated");
            for (RtmChannelListener listener : rtmChannelListenerList) {
                listener.onAttributesUpdated(list);
            }
            processChannelAttributes(list);
        }

        @Override
        public void onMessageReceived(RtmMessage rtmMessage, RtmChannelMember rtmChannelMember) {
            Log.d(TAG, "onMessageReceived: 收到频道消息"+rtmMessage.getText().toString());
            for (RtmChannelListener listener : rtmChannelListenerList) {
                listener.onMessageReceived(rtmMessage,rtmChannelMember);
            }
            if (mListener != null)
                mListener.onMessageReceived(rtmMessage);
        }

        @Override
        public void onImageMessageReceived(RtmImageMessage rtmImageMessage, RtmChannelMember rtmChannelMember) {
            for (RtmChannelListener listener : rtmChannelListenerList) {
                listener.onImageMessageReceived(rtmImageMessage,rtmChannelMember);
            }
        }

        @Override
        public void onFileMessageReceived(RtmFileMessage rtmFileMessage, RtmChannelMember rtmChannelMember) {
            for (RtmChannelListener listener : rtmChannelListenerList) {
                listener.onFileMessageReceived(rtmFileMessage,rtmChannelMember);
            }
        }

        @Override
        public void onMemberJoined(RtmChannelMember rtmChannelMember) {
            for (RtmChannelListener listener : rtmChannelListenerList) {
                listener.onMemberJoined(rtmChannelMember);
            }
            String userId = rtmChannelMember.getUserId();
            Log.i(TAG, String.format("onMemberJoined %s", userId));

            getUserAttributes(userId);
        }

        @Override
        public void onMemberLeft(RtmChannelMember rtmChannelMember) {
            for (RtmChannelListener listener : rtmChannelListenerList) {
                listener.onMemberLeft(rtmChannelMember);
            }
            String userId = rtmChannelMember.getUserId();
            Log.i(TAG, String.format("onMemberLeft %s", userId));

            if (mListener != null)
                mListener.onMemberLeft(userId);
        }
    };


    private RtmCallEventListener rtmCallEventListener = new RtmCallEventListener() {
        @Override
        public void onLocalInvitationReceivedByPeer(LocalInvitation localInvitation) {
            Log.d(TAG, "onLocalInvitationReceivedByPeer: 返回给主叫：被叫已收到呼叫邀请。");
            for (RtmCallEventListener listener : rtmCallEventListenerList) {
                listener.onLocalInvitationReceivedByPeer(localInvitation);
            }
        }

        @Override
        public void onLocalInvitationAccepted(LocalInvitation localInvitation, String s) {
            Log.d(TAG, "onLocalInvitationAccepted: 返回给主叫：被叫已接受呼叫邀请。");
            for (RtmCallEventListener listener : rtmCallEventListenerList) {
                listener.onLocalInvitationAccepted(localInvitation,s);
            }
        }

        @Override
        public void onLocalInvitationRefused(LocalInvitation localInvitation, String s) {
            Log.d(TAG, "onLocalInvitationRefused: 返回给主叫：被叫已拒绝呼叫邀请。");
            for (RtmCallEventListener listener : rtmCallEventListenerList) {
                listener.onLocalInvitationRefused(localInvitation,s);
            }
        }

        @Override
        public void onLocalInvitationCanceled(LocalInvitation localInvitation) {
            Log.d(TAG, "onLocalInvitationCanceled: 返回给主叫：呼叫邀请已被取消。");
            for (RtmCallEventListener listener : rtmCallEventListenerList) {
                listener.onLocalInvitationCanceled(localInvitation);
            }
        }

        @Override
        public void onLocalInvitationFailure(LocalInvitation localInvitation, int i) {
            Log.d(TAG, "onLocalInvitationFailure: 返回给主叫：呼叫邀请进程失败。");
            for (RtmCallEventListener listener : rtmCallEventListenerList) {
                listener.onLocalInvitationFailure(localInvitation,i);
            }
        }

        @Override
        public void onRemoteInvitationReceived(RemoteInvitation remoteInvitation) {
            Log.d(TAG, "onRemoteInvitationReceived: 返回给被叫：收到一个呼叫邀请。");
            for (RtmCallEventListener listener : rtmCallEventListenerList) {
                listener.onRemoteInvitationReceived(remoteInvitation);
            }
        }

        @Override
        public void onRemoteInvitationAccepted(RemoteInvitation remoteInvitation) {
            Log.d(TAG, "onRemoteInvitationAccepted: 返回给被叫：接受呼叫邀请成功。");
            for (RtmCallEventListener listener : rtmCallEventListenerList) {
                listener.onRemoteInvitationAccepted(remoteInvitation);
            }
        }

        @Override
        public void onRemoteInvitationRefused(RemoteInvitation remoteInvitation) {
            Log.d(TAG, "onRemoteInvitationRefused: 返回给被叫：拒绝呼叫邀请成功。");
            for (RtmCallEventListener listener : rtmCallEventListenerList) {
                listener.onRemoteInvitationRefused(remoteInvitation);
            }
        }

        @Override
        public void onRemoteInvitationCanceled(RemoteInvitation remoteInvitation) {
            Log.d(TAG, "onRemoteInvitationCanceled: 返回给被叫：主叫已取消呼叫邀请。");
            for (RtmCallEventListener listener : rtmCallEventListenerList) {
                listener.onRemoteInvitationCanceled(remoteInvitation);
            }
        }

        @Override
        public void onRemoteInvitationFailure(RemoteInvitation remoteInvitation, int i) {
            Log.d(TAG, "onRemoteInvitationFailure: 返回给被叫：来自主叫的呼叫邀请进程失败。");
            for (RtmCallEventListener listener : rtmCallEventListenerList) {
                listener.onRemoteInvitationFailure(remoteInvitation,i);
            }
        }
    };
}
