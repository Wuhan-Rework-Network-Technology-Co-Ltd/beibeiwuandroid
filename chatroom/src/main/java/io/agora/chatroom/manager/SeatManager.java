package io.agora.chatroom.manager;

import static io.agora.chatroom.ThreadUtils.runOnUiThread;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.google.gson.Gson;

import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import io.agora.chatroom.activity.ChatRoomActivity;
import io.agora.chatroom.ktv.MusicPlayer;
import io.agora.chatroom.model.AttributeKey;
import io.agora.chatroom.model.ChannelData;
import io.agora.chatroom.model.Constant;
import io.agora.chatroom.model.Message;
import io.agora.chatroom.model.Seat;
import io.agora.rtc.Constants;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;

public abstract class SeatManager {
    Context mContext;

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    private final String TAG = SeatManager.class.getSimpleName();

    abstract ChannelData getChannelData();

    abstract MessageManager getMessageManager();

    abstract RtcManager getRtcManager();

    abstract RtmManager getRtmManager();

    public static AppCompatCheckBox cb_mixing;
    public final void toBroadcaster(String userId, int position) {
        Log.d(TAG, String.format("toBroadcaster %s %d", userId, position));
        runOnUiThread(()->cb_mixing.setVisibility(View.VISIBLE));
        ChannelData channelData = getChannelData();
        if (Constant.isMyself(userId)) {
            int index = channelData.indexOfSeatArray(userId);
            if (index >= 0) {
                if (position == index) {
                    getRtcManager().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
                } else {
                    changeSeat(userId, index, position, new ResultCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            getRtcManager().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
                        }

                        @Override
                        public void onFailure(ErrorInfo errorInfo) {

                        }
                    });
                }
            } else {
                occupySeat(userId, position, new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getRtcManager().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {

                    }
                });
            }
        } else {
            getMessageManager().sendOrder(userId, Message.ORDER_TYPE_BROADCASTER, String.valueOf(position), null);
        }
    }

    public final void toAudience(String userId, ResultCallback<Void> callback) {
        Log.d(TAG, String.format("toAudience %s", userId));
        //如果下座位，删除我的全部歌曲，当前播放的我的歌曲切歌
        runOnUiThread(()->cb_mixing.setVisibility(View.INVISIBLE));
        if (ChatRoomActivity.isOrderedSong){
            ChatRoomActivity.isOrderedSong = false;
            if (userId.equals(Constant.sUserId+"")){//自己下座位，删除自己的歌，如果正在播放自己的歌，则播放新歌
                OkHttpInstance.deleteSongUser(Constant.sUserId+"", new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        if (MusicPlayer.mMusicModel!=null){
                            if (MusicPlayer.mMusicModel.getUserId().equals(Constant.sUserId+"")){
                                ChatRoomManager.instance(mContext).sendMessage("歌手离开了座位，开始下一首歌曲");
                            }
                        }
                    }
                });
            }
        }

        ChannelData channelData = getChannelData();
        if (Constant.isMyself(userId)) {
            resetSeat(channelData.indexOfSeatArray(userId), new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    getRtcManager().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);

                    if (callback != null)
                        callback.onSuccess(aVoid);
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    if (callback != null) {
                        callback.onFailure(errorInfo);
                    }
                }
            });
        } else {
            getMessageManager().sendOrder(userId, Message.ORDER_TYPE_AUDIENCE, null, callback);
        }
    }

    private void occupySeat(String userId, int position, ResultCallback<Void> callback) {
        modifySeat(position, new Seat(userId), callback);
    }

    private void resetSeat(int position, ResultCallback<Void> callback) {
        modifySeat(position, null, callback);
    }

    private void changeSeat(String userId, int oldPosition, int newPosition, ResultCallback<Void> callback) {
        Log.d(TAG, "changeSeat: 改变座位");
        resetSeat(oldPosition, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (getChannelData().updateSeat(oldPosition, null)) {
                    // don't wait onChannelAttributesUpdated, refresh now
                    onSeatUpdated(oldPosition);
                }
                occupySeat(userId, newPosition, callback);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    public void muteMic(String userId, boolean muted) {
        if (Constant.isMyself(userId)) {
            if (!getChannelData().isUserOnline(userId)) return;
            getRtcManager().muteLocalAudioStream(muted);
        } else {
            if (!getChannelData().isAnchorMyself()) return;
            getMessageManager().sendOrder(userId, Message.ORDER_TYPE_MUTE, String.valueOf(muted), null);
        }
    }

    public final void closeSeat(int position) {
        ChannelData channelData = getChannelData();

        if (!channelData.isAnchorMyself()) return;

        Seat seat = channelData.getSeatArray()[position];
        if (seat != null) {
            String userId = seat.getUserId();
            if (channelData.isUserOnline(userId)) {
                toAudience(userId, new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        modifySeat(position, new Seat(true), null);
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {

                    }
                });
                return;
            }
        }

        modifySeat(position, new Seat(true), null);
    }

    public final void openSeat(int position) {
        if (!getChannelData().isAnchorMyself()) return;
        resetSeat(position, null);
    }

    private void modifySeat(int position, Seat seat, ResultCallback<Void> callback) {
        Log.d(TAG, "modifySeat: 修改座位");
        if (position >= 0 && position < AttributeKey.KEY_SEAT_ARRAY.length)
            getRtmManager().addOrUpdateChannelAttributes(
                    AttributeKey.KEY_SEAT_ARRAY[position],
                    new Gson().toJson(seat),
                    callback
            );
    }

    abstract void onSeatUpdated(int position);

    final boolean updateSeatArray(int position, String value) {
        Log.d(TAG, "modifySeat: 更新座位");
        Seat seat = new Gson().fromJson(value, Seat.class);
        return getChannelData().updateSeat(position, seat);
    }

}
