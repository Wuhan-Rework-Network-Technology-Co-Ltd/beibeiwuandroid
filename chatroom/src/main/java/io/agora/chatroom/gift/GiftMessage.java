package io.agora.chatroom.gift;

import com.alibaba.fastjson.annotation.JSONField;

public class GiftMessage{
    public GiftMessage() {
    }

    @JSONField(name = "type")
    String type = "giftMessage";
    @JSONField(name = "gift_image")
    String gift_image = "";
    @JSONField(name = "gift_svga")
    String gift_svga = "";
    @JSONField(name = "senderName")
    String senderName = "";
    @JSONField(name = "senderPortrait")
    String senderPortrait = "";
    @JSONField(name = "receiverName")
    String receiverName = "";


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGift_image() {
        return gift_image;
    }

    public void setGift_image(String gift_image) {
        this.gift_image = gift_image;
    }

    public String getGift_svga() {
        return gift_svga;
    }

    public void setGift_svga(String gift_svga) {
        this.gift_svga = gift_svga;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPortrait() {
        return senderPortrait;
    }

    public void setSenderPortrait(String senderPortrait) {
        this.senderPortrait = senderPortrait;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}
