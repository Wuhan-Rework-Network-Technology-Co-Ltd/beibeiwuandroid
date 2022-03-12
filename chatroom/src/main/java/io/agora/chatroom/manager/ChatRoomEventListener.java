package io.agora.chatroom.manager;

import io.agora.chatroom.model.Message;

public interface ChatRoomEventListener {

    void onSeatUpdated(int position);

    void onUserGivingGift(String userId);

    void onMessageAdded(int position, Message message);

    void onMemberListUpdated(String userId);

    void onUserStatusChanged(String userId, Boolean muted);

    void onAudioMixingStateChanged(boolean isPlaying);

    void onAudioVolumeIndication(String userId, int volume);

}
