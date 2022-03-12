package io.agora.chatroom.model;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import io.agora.chatroom.Common;

public class Member {

    private String userId;
    private String name;
    private int avatarIndex;
    private String portrait;
    private String gender;
    private String property;
    private String portraitframe;
    private String veilcel;

    private Boolean isAnchor = false;

    public Member(String userId) {
        this.userId = userId;
    }

    public Member(String userId, String name,int avatarIndex, String portrait) {
        this(userId);
        this.name = name;
        this.avatarIndex = avatarIndex;
        this.portrait = portrait;
    }

    public Member(String userId, String name, int avatarIndex) {
        this(userId);
        this.name = name;
        this.avatarIndex = avatarIndex;
    }

    public Member(String userId, String name, String portrait,String gender,String property) {
        this(userId);
        this.name = name;
        this.portrait = portrait;
        this.gender = gender;
        this.property = property;
    }

    public Member(String userId, String name, String portrait,String gender,String property,String portraitframe,String veilcel) {
        this(userId);
        this.name = name;
        this.portrait = portrait;
        this.gender = gender;
        this.property = property;
        this.portraitframe = portraitframe;
        this.veilcel = veilcel;
    }

    public String getPortraitframe() {
        return Common.getOssResourceUrl(portraitframe);
    }

    public void setPortraitframe(String portraitframe) {
        this.portraitframe = portraitframe;
    }

    public String getVeilcel() {
        return Common.getOssResourceUrl(veilcel);
    }

    public void setVeilcel(String veilcel) {
        this.veilcel = veilcel;
    }

    public Boolean getAnchor() {
        return isAnchor;
    }

    public void setAnchor(Boolean anchor) {
        isAnchor = anchor;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getPortrait() {
        return Common.getOssResourceUrl(portrait);
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvatarIndex() {
        return avatarIndex;
    }

    public void setAvatarIndex(int avatarIndex) {
        this.avatarIndex = avatarIndex;
    }

    public void update(Member member) {
        this.name = member.name;
        this.avatarIndex = member.avatarIndex;
        this.portrait = member.portrait;
        this.gender = member.gender;
        this.property = member.property;
        this.portraitframe = member.portraitframe;
        this.veilcel = member.veilcel;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Member)
            return TextUtils.equals(userId, ((Member) obj).userId);
        return super.equals(obj);
    }

    public String toJsonString() {
        return new Gson().toJson(this);
    }

    public static Member fromJsonString(String str) {
        Log.d("TAG", "fromJsonString: 2头像地址"+str);
        Member member = new Gson().fromJson(str, Member.class);
        Log.d("TAG", "fromJsonString: 2头像地址"+member.getPortrait());
        return new Gson().fromJson(str, Member.class);
    }

}
