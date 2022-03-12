package io.agora.chatroom.manager;

import static io.agora.chatroom.ThreadUtils.runOnUiThread;
import static io.agora.chatroom.ktv.KtvFrameLayout.ktvView;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Map;

import io.agora.chatroom.Common;
import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import io.agora.chatroom.UserInfoList;
import io.agora.chatroom.activity.ChatRoomActivity;
import io.agora.chatroom.gift.GiftFrameLayout;
import io.agora.chatroom.ktv.MusicPlayer;
import io.agora.chatroom.model.AttributeKey;
import io.agora.chatroom.model.ChannelData;
import io.agora.chatroom.model.Constant;
import io.agora.chatroom.model.Member;
import io.agora.chatroom.model.Message;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmMessage;

public final class ChatRoomManager extends SeatManager implements MessageManager {

    private final String TAG = ChatRoomManager.class.getSimpleName();

    private static ChatRoomManager instance;

    public RtcManager mRtcManager;
    public RtmManager mRtmManager;
    private ChatRoomEventListener mListener;

    private ChannelData mChannelData = new ChannelData();

    @Override
    public ChannelData getChannelData() {
        return mChannelData;
    }

    @Override
    MessageManager getMessageManager() {
        return this;
    }

    @Override
    public RtcManager getRtcManager() {
        return mRtcManager;
    }

    @Override
    public RtmManager getRtmManager() {
        return mRtmManager;
    }

    @Override
    void onSeatUpdated(int position) {
        if (mListener != null) {
            mListener.onSeatUpdated(position);
        }
    }

    public ChatRoomManager(Context context) {
        mRtcManager = RtcManager.instance(context);
        mRtcManager.setListener(mRtcListener);
        mRtmManager = RtmManager.instance(context);
        mRtmManager.setListener(mRtmListener);
        setmContext(context);
    }

    public static ChatRoomManager instance(Context context) {
        if (instance == null) {
            synchronized (ChatRoomManager.class) {
                if (instance == null)
                    instance = new ChatRoomManager(context);
            }
        }
        return instance;
    }

    public void setListener(ChatRoomEventListener listener) {
        mListener = listener;
    }

    public void joinChannel(String channelId) {
        Log.d(TAG, "onSuccess: rtm登录"+channelId);
        this.channelId = channelId;
        mRtmManager.login(Constant.sUserId, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Member member = new Member(Common.myUserInfoList.getId(), Common.myUserInfoList.getNickname(),
                        Common.myUserInfoList.getPortrait(),Common.myUserInfoList.getGender(),
                        Common.myUserInfoList.getProperty(),Common.myUserInfoList.getPortraitframe(),Common.myUserInfoList.getVeilcel());

                mRtmManager.setLocalUserAttributes(AttributeKey.KEY_USER_INFO, member.toJsonString());

                if (!channelId.equals("0")){
                    mRtcManager.joinChannel(channelId, Integer.parseInt(Common.myUserInfoList.getId()));
                }
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                if (errorInfo.getErrorCode()==8){
                    mRtmManager.logoutWithCallback(new ResultCallback() {
                        @Override
                        public void onSuccess(Object o) {
                            joinChannel(channelId);
                        }

                        @Override
                        public void onFailure(ErrorInfo errorInfo) {

                        }
                    });
                }
            }
        });
    }

    public void leaveChannel() {
        Log.d(TAG, "leaveChannel: 离开频道");
        mRtcManager.leaveChannel();
        //mRtmManager.leaveChannel();
        mChannelData.release();


        if (Common.myUserInfoList != null){
            if (channelId!=null){
                if (channelId.equals(Common.myUserInfoList.getId())){
                    OkHttpInstance.deleteRecruitment(new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {

                        }
                    });
                }
            }
        }
    }
    public String channelId = "0";
    private void checkAndBeAnchor() {
        String myUserId = String.valueOf(Constant.sUserId);

        if (mChannelData.isAnchorMyself()) {
            int index = mChannelData.indexOfSeatArray(myUserId);
            if (index == -1) {
                index = mChannelData.firstIndexOfEmptySeat();
            }
            toBroadcaster(myUserId, index);
        } else {
            //if (mChannelData.hasAnchor()) return;
            if (myUserId.equals(channelId)){
                mRtmManager.addOrUpdateChannelAttributes(AttributeKey.KEY_ANCHOR_ID, myUserId, new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //，然后放到麦位
                        toBroadcaster(myUserId, mChannelData.firstIndexOfEmptySeat());
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {

                    }
                });
            }
        }
    }

    public void givingGift() {
        Message message = new Message(Message.MESSAGE_TYPE_GIFT, null, Constant.sUserId);
        mRtmManager.sendMessage(message.toJsonString(), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (mListener != null)
                    mListener.onUserGivingGift(message.getSendId());
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    @Override
    public void sendOrder(String userId, String orderType, String content, ResultCallback<Void> callback) {
        if (!mChannelData.isAnchorMyself()) return;
        Message message = new Message(orderType, content, Constant.sUserId);
        mRtmManager.sendMessageToPeer(userId, message.toJsonString(), callback);
    }

    @Override
    public void sendMessage(String text) {
        Message message = new Message(text, Constant.sUserId);
        mRtmManager.sendMessage(message.toJsonString(), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                addMessage(message);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    @Override
    public void processMessage(RtmMessage rtmMessage) {
        Log.d(TAG, "processMessage: 收到频道消息"+rtmMessage.getText().toString());
        if (rtmMessage.getText().contains("veilcel")){
            //新人加入频道消息
            try {
                UserInfoList userInfoList = JSON.parseObject(rtmMessage.getText().toString(),UserInfoList.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "onMemberJoined: 用户加入了"+userInfoList.getId());
                        GiftFrameLayout giftFrameLayout = new GiftFrameLayout(mContext);
                        ChatRoomActivity.chatRoomActivity.container.addView(giftFrameLayout);
                        giftFrameLayout.showSVGA(userInfoList.getVeilcel(),userInfoList);
                    }
                });
            }catch (Exception e){

            }
            return;
        }
        if (!rtmMessage.getText().contains("messageType")){
            //普通单人消息不处理，例如{"type":"gift","gift_name":"城堡","gift_amount":"1","timestamp":"1624694236"}
            //只有聊天室消息类型才处理，例如{"content":"城堡,1,2446,0","messageType":2,"sendId":"2288"}
            return;
        }
        Message message = Message.fromJsonString(rtmMessage.getText());
        switch (message.getMessageType()) {
            case Message.MESSAGE_TYPE_TEXT:
            case Message.MESSAGE_TYPE_IMAGE:
                addMessage(message);
                break;
            case Message.MESSAGE_TYPE_GIFT:
                if (mListener != null)
                    mListener.onUserGivingGift(message.getSendId());
                break;
            case Message.MESSAGE_TYPE_ORDER:
                String myUserId = String.valueOf(Constant.sUserId);
                switch (message.getOrderType()) {
                    case Message.ORDER_TYPE_AUDIENCE:
                        toAudience(myUserId, null);
                        break;
                    case Message.ORDER_TYPE_BROADCASTER:
                        toBroadcaster(myUserId, Integer.valueOf(message.getContent()));
                        break;
                    case Message.ORDER_TYPE_MUTE:
                        muteMic(myUserId, Boolean.valueOf(message.getContent()));
                        break;
                }
                break;
        }
    }

    @Override
    public void addMessage(Message message) {
        int position = mChannelData.addMessage(message);
        if (mListener != null)
            mListener.onMessageAdded(position,message);
    }

    private RtcManager.RtcEventListener mRtcListener = new RtcManager.RtcEventListener() {
        @Override
        public void onJoinChannelSuccess(String channelId) {
            mRtmManager.joinChannel(channelId, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    //入场动画
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendMessage("加入了房间！");
                        }
                    },1000);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "onMemberJoined: 用户加入了"+Common.myUserInfoList.getId());
                            GiftFrameLayout giftFrameLayout = new GiftFrameLayout(mContext);
                            ChatRoomActivity.chatRoomActivity.container.addView(giftFrameLayout);
                            giftFrameLayout.showSVGA(Common.myUserInfoList.getVeilcel(),Common.myUserInfoList);
                        }
                    });
                    mRtmManager.sendMessage(JSON.toJSON(Common.myUserInfoList).toString(), new ResultCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: 11进入频道成功");

                        }

                        @Override
                        public void onFailure(ErrorInfo errorInfo) {
                            Log.d(TAG, "onSuccess: 进入频道失败");
                        }
                    });
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {

                }
            });
        }

        @Override
        public void onUserOnlineStateChanged(int uid, boolean isOnline) {
            if (isOnline) {
                mChannelData.addOrUpdateUserStatus(uid, false);

                if (mListener != null)
                    mListener.onUserStatusChanged(String.valueOf(uid), false);
            } else {
                mChannelData.removeUserStatus(uid);

                if (mListener != null)
                    mListener.onUserStatusChanged(String.valueOf(uid), null);
            }
        }

        @Override
        public void onUserMuteAudio(int uid, boolean muted) {
            mChannelData.addOrUpdateUserStatus(uid, muted);

            if (mListener != null)
                mListener.onUserStatusChanged(String.valueOf(uid), muted);
        }

        @Override
        public void onAudioMixingStateChanged(boolean isPlaying) {
            if (mListener != null)
                mListener.onAudioMixingStateChanged(isPlaying);
        }

        @Override
        public void onAudioVolumeIndication(int uid, int volume) {
            if (mListener != null)
                mListener.onAudioVolumeIndication(String.valueOf(uid), volume);
        }
    };

    private RtmManager.RtmEventListener mRtmListener = new RtmManager.RtmEventListener() {
        @Override
        public void onChannelAttributesLoaded() {
            checkAndBeAnchor();
        }

        @Override
        public void onChannelAttributesUpdated(Map<String, String> attributes) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                String key = entry.getKey();
                switch (key) {
                    case AttributeKey.KEY_ANCHOR_ID:
                        String userId = entry.getValue();
                        if (mChannelData.setAnchorId(userId))
                            Log.i(TAG, String.format("onChannelAttributesUpdated %s %s", key, userId));
                        break;
                    default:
                        int index = AttributeKey.indexOfSeatKey(key);
                        if (index >= 0) {
                            String value = entry.getValue();
                            if (updateSeatArray(index, value)) {
                                Log.i(TAG, String.format("onChannelAttributesUpdated %s %s", key, value));

                                if (mListener != null)
                                    mListener.onSeatUpdated(index);
                            }
                        }
                        break;
                }
            }
        }

        @Override
        public void onInitMembers(List<RtmChannelMember> members) {
            for (RtmChannelMember member : members) {
                mChannelData.addOrUpdateMember(new Member(member.getUserId()));
            }

            if (mListener != null) {
                mListener.onMemberListUpdated(null);
            }
        }

        @Override
        public void onMemberJoined(String userId, Map<String, String> attributes) {
//            OkHttpInstance.getUserAttributes(userId, new OkHttpResponseCallBack() {
//                @Override
//                public void getResponseString(String responseString) {
//                    if (!responseString.equals("false")){
//                        UserInfoList userInfoList = JSON.parseObject(responseString, UserInfoList.class);
//                        Member member = new Member(userId, userInfoList.getNickname(), userInfoList.getPortrait(),userInfoList.getGender(),userInfoList.getProperty());
//
//                        mChannelData.addOrUpdateMember(member);
//
//                        if (mListener != null)
//                            mListener.onMemberListUpdated(userId);
//                    }
//                }
//            });

            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                if (AttributeKey.KEY_USER_INFO.equals(entry.getKey())) {
                    Member member = Member.fromJsonString(entry.getValue());
                    mChannelData.addOrUpdateMember(member);
                    if (mListener != null)
                        mListener.onMemberListUpdated(userId);
                    break;
                }
            }
        }

        @Override
        public void onMemberLeft(String userId) {
            mChannelData.removeMember(userId);

            Log.d(TAG, "onMemberJoined: 用户离开");

            if (MusicPlayer.mMusicModel!=null && ktvView!=null){
                if (MusicPlayer.mMusicModel.getUserId().equals(userId)){
                    //唱歌的人离开频道，播放新歌
                    Log.d(TAG, "onMessageAdded: 播放新歌8");
                    ktvView.playNewSong();
                }
            }


            if (mListener != null)
                mListener.onMemberListUpdated(userId);
        }

        @Override
        public void onMessageReceived(RtmMessage message) {
            processMessage(message);
        }
    };

}
