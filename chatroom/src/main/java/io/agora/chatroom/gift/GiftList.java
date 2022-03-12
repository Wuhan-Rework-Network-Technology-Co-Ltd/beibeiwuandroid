package io.agora.chatroom.gift;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

import io.agora.chatroom.Common;

public class GiftList implements Serializable {
    public GiftList() {

    }

    @JSONField(name = "id")
    String id = "";
    @JSONField(name = "gift_image")
    String gift_image = "";
    @JSONField(name = "gift_svga")
    String gift_svga = "";
    @JSONField(name = "gift_name")
    public String gift_name = "";
    @JSONField(name = "gift_price")
    String gift_price = "";
    @JSONField(name = "gift_tag")
    String gift_tag = "";
    @JSONField(name = "gift_description")
    String gift_description = "";
    @JSONField(name = "time")
    String time = "";


    @JSONField(name = "num")
    int num = 1;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Boolean isSelected = false;

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGift_image() {
        return Common.getOssResourceUrl(gift_image);
    }

    public void setGift_image(String gift_image) {
        this.gift_image = gift_image;
    }

    public String getGift_svga() {
        return Common.getOssResourceUrl(gift_svga);
    }

    public void setGift_svga(String gift_svga) {
        this.gift_svga = gift_svga;
    }

    public String getGift_name() {
        return gift_name;
    }

    public void setGift_name(String gift_name) {
        this.gift_name = gift_name;
    }

    public String getGift_price() {
        return gift_price;
    }

    public void setGift_price(String gift_price) {
        this.gift_price = gift_price;
    }

    public String getGift_tag() {
        return gift_tag;
    }

    public void setGift_tag(String gift_tag) {
        this.gift_tag = gift_tag;
    }

    public String getGift_description() {
        return gift_description;
    }

    public void setGift_description(String gift_description) {
        this.gift_description = gift_description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
