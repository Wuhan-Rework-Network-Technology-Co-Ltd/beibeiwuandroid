package io.agora.chatroom;

import android.util.Log;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;


public class FilmList implements Serializable {
    private static final String TAG = "FilmList";
    public FilmList() {
    }

    @JSONField(name = "id")
    String id = "";
    @JSONField(name = "uploader")
    String uploader = "";
    @JSONField(name = "type")
    String type = "";
    @JSONField(name = "name")
    String name = "";
    @JSONField(name = "director")
    String director = "";
    @JSONField(name = "classify")
    String classify = "";
    @JSONField(name = "cover")
    String cover = "";
    @JSONField(name = "url")
    String url = "";
    @JSONField(name = "forbid")
    String forbid = "";
    @JSONField(name = "time")
    String time = "";

    @JSONField(name = "nickname")
    String nickname = "";
    @JSONField(name = "portrait")
    String portrait = "";
    @JSONField(name = "online")
    String online = "";


    Long filmCurrentPosition = 0l;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPortrait() {
        return Common.getOssResourceUrl(portrait);
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public Long getFilmCurrentPosition() {
        return filmCurrentPosition;
    }

    public void setFilmCurrentPosition(Long filmCurrentPosition) {
        Log.d(TAG, "setFilmCurrentPosition: 当前电影位置"+filmCurrentPosition);
        this.filmCurrentPosition = filmCurrentPosition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getCover() {
        return Common.getOssResourceUrl(cover);
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUrl() {
        return Common.getOssResourceUrl(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getForbid() {
        return forbid;
    }

    public void setForbid(String forbid) {
        this.forbid = forbid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



}
