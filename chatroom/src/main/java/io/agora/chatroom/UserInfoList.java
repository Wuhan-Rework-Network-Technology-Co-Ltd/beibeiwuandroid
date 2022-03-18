package io.agora.chatroom;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class UserInfoList implements Serializable {
    public UserInfoList() {
    }

    //头像框和坐骑
    @JSONField(name="portraitframe")
    String portraitframe = "";//礼物清单
    @JSONField(name="veilcel")
    String veilcel = "";//申请好友留言

    //添加好友
    @JSONField(name="gift_string")
    String gift_string = "";//礼物清单
    @JSONField(name="yourleavewords")
    String yourleavewords = "";//申请好友留言


    @JSONField(name = "alilogonid")
    String alilogonid = "";
    @JSONField(name = "aliname")
    String aliname = "";

    @JSONField(name = "vitality")
    String vitality = "0";
    @JSONField(name = "post")
    String post = "0";
    @JSONField(name = "comment")
    String comment = "0";

    @JSONField(name = "follow")
    String follow = "0";
    @JSONField(name = "fans")
    String fans = "0";

    @JSONField(name = "all_money")
    String all_money = "0.00";
    @JSONField(name = "all_income")
    String all_income = "0.00";

    @JSONField(name = "money")
    String money = "0.00";
    @JSONField(name = "income")
    String income = "0.00";

    @JSONField(name = "id")
    String id = "";
    @JSONField(name = "openid")
    String openid = "";
    @JSONField(name = "qqid")
    String qqid = "";
    @JSONField(name = "phone")
    String phone = "";
    @JSONField(name = "email")
    String email = "";
    @JSONField(name = "password")
    String password = "";
    @JSONField(name = "portrait")
    String portrait = "";
    @JSONField(name = "nickname")
    String nickname = "";
    @JSONField(name = "age")
    String age = "";
    @JSONField(name = "gender")
    String gender = "";
    @JSONField(name = "property")
    String property = "";
    @JSONField(name = "region")
    String region = "";
    @JSONField(name = "authgroup")
    String authgroup = "";
    @JSONField(name = "authlocation")
    String authlocation = "";
    @JSONField(name = "authcircle")
    String authcircle = "";
    @JSONField(name = "authfriend")
    String authfriend = "";
    @JSONField(name = "vip")
    String vip = "";
    @JSONField(name = "svip")
    String svip = "";
    @JSONField(name = "score")
    String score = "";
    @JSONField(name = "time")
    String time = "";
    @JSONField(name = "forbid")
    String forbid = "";
    @JSONField(name = "forbidreason")
    String forbidreason = "";
    @JSONField(name = "latitude")
    String latitude = "";
    @JSONField(name = "longitude")
    String longitude = "";
    @JSONField(name = "locationtime")
    String locationtime = "";
    @JSONField(name = "signature")
    String signature = "";
    @JSONField(name = "referral")
    String referral = "";
    @JSONField(name = "allowgroup")
    String allowgroup = "";
    @JSONField(name = "allowlocation")
    String allowlocation = "";
    @JSONField(name = "allowstatus")
    String allowstatus = "";
    @JSONField(name = "allowfriend")
    String allowfriend = "";
    @JSONField(name = "allowsvip")
    String allowsvip = "";

    @JSONField(name = "uniquelogintoken")
    String uniquelogintoken = "";
    @JSONField(name = "porn")
    String porn = "";
    @JSONField(name = "pornnew")
    String pornnew = "";
    @JSONField(name = "myfriends")
    String myfriends = "";
    @JSONField(name = "myblacklist")
    String myblacklist = "";
    @JSONField(name = "phonebrand")
    String phonebrand = "";
    @JSONField(name = "pushregid")
    String pushregid = "";
    @JSONField(name = "frontorback")
    String frontorback = "";
    @JSONField(name = "disturb")
    String disturb = "";
    @JSONField(name = "friendsremark")
    String friendsremark = "";
    @JSONField(name = "friendstag")
    String friendstag = "";
    @JSONField(name = "svip_try")
    String svip_try = "";
    @JSONField(name = "clientip")
    String clientip = "";


    //语音房间相关
    @JSONField(name="audioroom")
    String audioroom = "";//是否在语音房间
    @JSONField(name="audiorecommend")
    String audiorecommend = "";//是否是推荐房
    @JSONField(name="audiopopulation")
    String audiopopulation = "";//语音房间人数
    @JSONField(name="audioroomname")
    String audioroomname = "";//房间名
    @JSONField(name="audioroomtype")
    String audioroomtype = "";//房间名
    @JSONField(name="audioroomcover")
    String audioroomcover = "";//房间封面
    @JSONField(name="audioroombackground")
    String audioroombackground = "";//房间背景图
    @JSONField(name="audioroompassword")
    String audioroompassword = "";//房间密码
    @JSONField(name="audioroommessage")
    String audioroommessage = "";//房间留言，用于交友广场
    @JSONField(name="audioannouncement")
    String audioannouncement = "";//语音房间公告

    @JSONField(name="audioroomtime")
    String audioroomtime = "";//语音房间发布时间
    @JSONField(name="audioroom_online")
    String audioroom_online = "";//语音房间发布时间

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

    public String getGift_string() {
        return gift_string;
    }

    public void setGift_string(String gift_string) {
        this.gift_string = gift_string;
    }

    public String getYourleavewords() {
        return yourleavewords;
    }

    public void setYourleavewords(String yourleavewords) {
        this.yourleavewords = yourleavewords;
    }

    public String getAlilogonid() {
        return alilogonid;
    }

    public void setAlilogonid(String alilogonid) {
        this.alilogonid = alilogonid;
    }

    public String getAliname() {
        return aliname;
    }

    public void setAliname(String aliname) {
        this.aliname = aliname;
    }

    public String getVitality() {
        return vitality;
    }

    public void setVitality(String vitality) {
        this.vitality = vitality;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public String getAll_money() {
        return all_money;
    }

    public void setAll_money(String all_money) {
        this.all_money = all_money;
    }

    public String getAll_income() {
        return all_income;
    }

    public void setAll_income(String all_income) {
        this.all_income = all_income;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getQqid() {
        return qqid;
    }

    public void setQqid(String qqid) {
        this.qqid = qqid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPortrait() {
        return Common.getOssResourceUrl(portrait);
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAuthgroup() {
        return authgroup;
    }

    public void setAuthgroup(String authgroup) {
        this.authgroup = authgroup;
    }

    public String getAuthlocation() {
        return authlocation;
    }

    public void setAuthlocation(String authlocation) {
        this.authlocation = authlocation;
    }

    public String getAuthcircle() {
        return authcircle;
    }

    public void setAuthcircle(String authcircle) {
        this.authcircle = authcircle;
    }

    public String getAuthfriend() {
        return authfriend;
    }

    public void setAuthfriend(String authfriend) {
        this.authfriend = authfriend;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getSvip() {
        return svip;
    }

    public void setSvip(String svip) {
        this.svip = svip;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getForbid() {
        return forbid;
    }

    public void setForbid(String forbid) {
        this.forbid = forbid;
    }

    public String getForbidreason() {
        return forbidreason;
    }

    public void setForbidreason(String forbidreason) {
        this.forbidreason = forbidreason;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocationtime() {
        return locationtime;
    }

    public void setLocationtime(String locationtime) {
        this.locationtime = locationtime;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getReferral() {
        return referral;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }

    public String getAllowgroup() {
        return allowgroup;
    }

    public void setAllowgroup(String allowgroup) {
        this.allowgroup = allowgroup;
    }

    public String getAllowlocation() {
        return allowlocation;
    }

    public void setAllowlocation(String allowlocation) {
        this.allowlocation = allowlocation;
    }

    public String getAllowstatus() {
        return allowstatus;
    }

    public void setAllowstatus(String allowstatus) {
        this.allowstatus = allowstatus;
    }

    public String getAllowfriend() {
        return allowfriend;
    }

    public void setAllowfriend(String allowfriend) {
        this.allowfriend = allowfriend;
    }

    public String getAllowsvip() {
        return allowsvip;
    }

    public void setAllowsvip(String allowsvip) {
        this.allowsvip = allowsvip;
    }

    public String getUniquelogintoken() {
        return uniquelogintoken;
    }

    public void setUniquelogintoken(String uniquelogintoken) {
        this.uniquelogintoken = uniquelogintoken;
    }

    public String getPorn() {
        return porn;
    }

    public void setPorn(String porn) {
        this.porn = porn;
    }

    public String getPornnew() {
        return pornnew;
    }

    public void setPornnew(String pornnew) {
        this.pornnew = pornnew;
    }

    public String getMyfriends() {
        return myfriends;
    }

    public void setMyfriends(String myfriends) {
        this.myfriends = myfriends;
    }

    public String getMyblacklist() {
        return myblacklist;
    }

    public void setMyblacklist(String myblacklist) {
        this.myblacklist = myblacklist;
    }

    public String getPhonebrand() {
        return phonebrand;
    }

    public void setPhonebrand(String phonebrand) {
        this.phonebrand = phonebrand;
    }

    public String getPushregid() {
        return pushregid;
    }

    public void setPushregid(String pushregid) {
        this.pushregid = pushregid;
    }

    public String getFrontorback() {
        return frontorback;
    }

    public void setFrontorback(String frontorback) {
        this.frontorback = frontorback;
    }

    public String getDisturb() {
        return disturb;
    }

    public void setDisturb(String disturb) {
        this.disturb = disturb;
    }

    public String getFriendsremark() {
        return friendsremark;
    }

    public void setFriendsremark(String friendsremark) {
        this.friendsremark = friendsremark;
    }

    public String getFriendstag() {
        return friendstag;
    }

    public void setFriendstag(String friendstag) {
        this.friendstag = friendstag;
    }

    public String getSvip_try() {
        return svip_try;
    }

    public void setSvip_try(String svip_try) {
        this.svip_try = svip_try;
    }

    public String getClientip() {
        return clientip;
    }

    public void setClientip(String clientip) {
        this.clientip = clientip;
    }

    public String getAudioroom() {
        return audioroom;
    }

    public void setAudioroom(String audioroom) {
        this.audioroom = audioroom;
    }

    public String getAudiorecommend() {
        return audiorecommend;
    }

    public void setAudiorecommend(String audiorecommend) {
        this.audiorecommend = audiorecommend;
    }

    public String getAudiopopulation() {
        return audiopopulation;
    }

    public void setAudiopopulation(String audiopopulation) {
        this.audiopopulation = audiopopulation;
    }

    public String getAudioroomname() {
        return audioroomname;
    }

    public void setAudioroomname(String audioroomname) {
        this.audioroomname = audioroomname;
    }

    public String getAudioroomtype() {
        return audioroomtype;
    }

    public void setAudioroomtype(String audioroomtype) {
        this.audioroomtype = audioroomtype;
    }

    public String getAudioroomcover() {
        return Common.getOssResourceUrl(audioroomcover);
    }

    public void setAudioroomcover(String audioroomcover) {
        this.audioroomcover = audioroomcover;
    }

    public String getAudioroombackground() {
        return Common.getOssResourceUrl(audioroombackground);
    }

    public void setAudioroombackground(String audioroombackground) {
        this.audioroombackground = audioroombackground;
    }

    public String getAudioroompassword() {
        return audioroompassword;
    }

    public void setAudioroompassword(String audioroompassword) {
        this.audioroompassword = audioroompassword;
    }

    public String getAudioroommessage() {
        return audioroommessage;
    }

    public void setAudioroommessage(String audioroommessage) {
        this.audioroommessage = audioroommessage;
    }

    public String getAudioannouncement() {
        return audioannouncement;
    }

    public void setAudioannouncement(String audioannouncement) {
        this.audioannouncement = audioannouncement;
    }

    public String getAudioroomtime() {
        return audioroomtime;
    }

    public void setAudioroomtime(String audioroomtime) {
        this.audioroomtime = audioroomtime;
    }

    public String getAudioroom_online() {
        return audioroom_online;
    }

    public void setAudioroom_online(String audioroom_online) {
        this.audioroom_online = audioroom_online;
    }
}
