package xin.banghua.beiyuan.Adapter;

import xin.banghua.beiyuan.Common;

public class FriendList {
    String mUserID;
    String mUserPortrait;
    String mUserNickName;
    String mUserAge;
    String mUserGender;
    String mUserRegion;
    String mUserProperty;
    String mVip;
    String mSVip;
    String portraitframe;
    private String letters;//显示拼音的首字母
    public FriendList(String userID, String userPotrait, String userNickName,String userAge,String userGender,String userRegion,String userProperty,String userVip,String userSVip){
        this.mUserID = userID;
        this.mUserPortrait = userPotrait;
        this.mUserNickName = userNickName;
        this.mUserAge = userAge;
        this.mUserGender = userGender;
        this.mUserRegion = userRegion;
        this.mUserProperty = userProperty;
        this.mVip = userVip;
        this.mSVip = userSVip;
    }

    public FriendList(String userID, String userPotrait, String userNickName,String userAge,String userGender,String userRegion,String userProperty,String userVip,String userSVip,String portraitframe){
        this.mUserID = userID;
        this.mUserPortrait = userPotrait;
        this.mUserNickName = userNickName;
        this.mUserAge = userAge;
        this.mUserGender = userGender;
        this.mUserRegion = userRegion;
        this.mUserProperty = userProperty;
        this.mVip = userVip;
        this.mSVip = userSVip;
        this.portraitframe = portraitframe;
    }

    public String getPortraitframe() {
        return Common.getOssResourceUrl(portraitframe);
    }

    public void setPortraitframe(String portraitframe) {
        this.portraitframe = portraitframe;
    }

    public String getmSVip() {
        return mSVip;
    }

    public void setmSVip(String mSVip) {
        this.mSVip = mSVip;
    }

    public String getmVip() {
        return mVip;
    }

    public void setmVip(String mVip) {
        this.mVip = mVip;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public String getmUserAge() {
        return mUserAge;
    }

    public void setmUserAge(String mUserAge) {
        this.mUserAge = mUserAge;
    }

    public String getmUserGender() {
        return mUserGender;
    }

    public void setmUserGender(String mUserGender) {
        this.mUserGender = mUserGender;
    }

    public String getmUserRegion() {
        return mUserRegion;
    }

    public void setmUserRegion(String mUserRegion) {
        this.mUserRegion = mUserRegion;
    }

    public String getmUserProperty() {
        return mUserProperty;
    }

    public void setmUserProperty(String mUserProperty) {
        this.mUserProperty = mUserProperty;
    }

    public String getmUserID() {
        return mUserID;
    }

    public String getmUserNickName() {
        return mUserNickName;
    }

    public String getmUserPortrait() {
        return mUserPortrait;
    }

    public void setmUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    public void setmUserPortrait(String mUserPortrait) {
        this.mUserPortrait = mUserPortrait;
    }

    public void setmUserNickName(String mUserNickName) {
        this.mUserNickName = mUserNickName;
    }
}
