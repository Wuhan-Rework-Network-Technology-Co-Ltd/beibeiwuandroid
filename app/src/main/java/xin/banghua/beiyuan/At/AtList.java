package xin.banghua.beiyuan.At;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class AtList implements Serializable {
    public AtList() {
    }

    @JSONField(name = "id")
    String mUserID;
    @JSONField(name = "portrait")
    String mUserPortrait;
    @JSONField(name = "portraitframe")
    String mUserPortraitFrame;
    @JSONField(name = "nickname")
    String mUserNickName;
    @JSONField(name = "vip")
    String mVip;

    private String letters;//显示拼音的首字母

    public AtList(String mUserID, String mUserPortrait, String mUserPortraitFrame, String mUserNickName, String mVip) {
        this.mUserID = mUserID;
        this.mUserPortrait = mUserPortrait;
        this.mUserPortraitFrame = mUserPortraitFrame;
        this.mUserNickName = mUserNickName;
        this.mVip = mVip;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public String getmUserPortraitFrame() {
        return mUserPortraitFrame;
    }

    public void setmUserPortraitFrame(String mUserPortraitFrame) {
        this.mUserPortraitFrame = mUserPortraitFrame;
    }

    public String getmVip() {
        return mVip;
    }

    public void setmVip(String mVip) {
        this.mVip = mVip;
    }

    public String getmUserID() {
        return mUserID;
    }

    public void setmUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    public String getmUserPortrait() {
        return mUserPortrait;
    }

    public void setmUserPortrait(String mUserPortrait) {
        this.mUserPortrait = mUserPortrait;
    }

    public String getmUserNickName() {
        return mUserNickName;
    }

    public void setmUserNickName(String mUserNickName) {
        this.mUserNickName = mUserNickName;
    }
}
