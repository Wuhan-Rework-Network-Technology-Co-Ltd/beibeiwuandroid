package io.agora.chatroom.manager;

import io.agora.chatroom.model.Message;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmMessage;

public interface MessageManager {

    void sendOrder(String userId, String orderType, String content, ResultCallback<Void> callback);

    void sendMessage(String text);

    void processMessage(RtmMessage rtmMessage);

    void addMessage(Message message);

}
