package io.agora.chatroom;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class BarrageList implements Serializable {
    public BarrageList() {
    }

    @JSONField(name = "id")
    String id = "";
    @JSONField(name = "authid")
    String authid = "";
    @JSONField(name = "filmid")
    String filmid = "";
    @JSONField(name = "barrage_text")
    String barrage_text = "";
    @JSONField(name = "progress")
    String progress = "";
    @JSONField(name = "vip")
    String vip = "";
    @JSONField(name = "time")
    String time = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthid() {
        return authid;
    }

    public void setAuthid(String authid) {
        this.authid = authid;
    }

    public String getFilmid() {
        return filmid;
    }

    public void setFilmid(String filmid) {
        this.filmid = filmid;
    }

    public String getBarrage_text() {
        return barrage_text;
    }

    public void setBarrage_text(String barrage_text) {
        this.barrage_text = barrage_text;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
