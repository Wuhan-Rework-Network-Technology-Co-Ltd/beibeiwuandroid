  package io.agora.chatroom.model;

import androidx.annotation.DrawableRes;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

import io.agora.chatroom.Common;

public class Channel implements Serializable {

    public Channel() {
    }

    @JSONField(name = "id")
    String id;
    @JSONField(name = "audioroomcover")
    String audioroomcover;
    @JSONField(name = "audioroomname")
    String audioroomname;
    @JSONField(name = "audioroombackground")
    String audioroombackground;
    @JSONField(name = "audioroomtype")
    String audioroomtype;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAudioroomcover() {
        return Common.getOssResourceUrl(audioroomcover);
    }

    public void setAudioroomcover(String audioroomcover) {
        this.audioroomcover = audioroomcover;
    }

    public String getAudioroomname() {
        return audioroomname;
    }

    public void setAudioroomname(String audioroomname) {
        this.audioroomname = audioroomname;
    }

    public String getAudioroombackground() {
        return Common.getOssResourceUrl(audioroombackground);
    }

    public void setAudioroombackground(String audioroombackground) {
        this.audioroombackground = audioroombackground;
    }

    public String getAudioroomtype() {
        return audioroomtype;
    }

    public void setAudioroomtype(String audioroomtype) {
        this.audioroomtype = audioroomtype;
    }

    private int drawableRes;
    private int backgroundRes;
    private String name;

    public Channel(@DrawableRes int drawableRes, @DrawableRes int backgroundRes, String name) {
        this.drawableRes = drawableRes;
        this.backgroundRes = backgroundRes;
        this.name = name;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public void setDrawableRes(@DrawableRes int drawableRes) {
        this.drawableRes = drawableRes;
    }

    public int getBackgroundRes() {
        return backgroundRes;
    }

    public void setBackgroundRes(@DrawableRes int backgroundRes) {
        this.backgroundRes = backgroundRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
