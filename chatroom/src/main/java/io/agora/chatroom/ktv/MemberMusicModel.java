package io.agora.chatroom.ktv;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.File;
import java.io.Serializable;

import io.agora.chatroom.Common;

public class MemberMusicModel implements Serializable {
    public MemberMusicModel() {
    }

    @JSONField(name = "id")
    String id = "";
    @JSONField(name = "name")
    String name = "";
    @JSONField(name = "singer")
    String singer = "";
    @JSONField(name = "musicId")
    String musicId = "";
    @JSONField(name = "poster")
    String poster = "";
    @JSONField(name = "song")
    String song = "";
    @JSONField(name = "lrc")
    String lrc = "";
    @JSONField(name = "objectId")
    String objectId = "";

    @JSONField(name = "userId")
    String userId = "";
    @JSONField(name = "roomId")
    String roomId = "";

    @JSONField(name = "songId")
    String songId = "";

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public enum Type implements Serializable {
        Default, MiGu;
    }
    private File fileMusic;
    private File fileLrc;

    private Type type = Type.MiGu;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public File getFileMusic() {
        return fileMusic;
    }

    public void setFileMusic(File fileMusic) {
        this.fileMusic = fileMusic;
    }

    public File getFileLrc() {
        return fileLrc;
    }

    public void setFileLrc(File fileLrc) {
        this.fileLrc = fileLrc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSong() {
        return Common.getOssResourceUrl(song);
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getLrc() {
        return Common.getOssResourceUrl(lrc);
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
