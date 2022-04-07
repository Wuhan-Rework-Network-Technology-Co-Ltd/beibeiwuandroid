package xin.banghua.beiyuan.Adapter;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

import io.agora.chatroom.Common;

public class LuntanList  implements Serializable {
    public LuntanList() {
    }

    @JSONField(name="rp_verify_time")
    String rp_verify_time = "";//真人认证成功时间，存在则说明完成了真人认证

    @JSONField(name = "topic")
    String topic = "";

    @JSONField(name = "vitality")
    String vitality = "0";
    @JSONField(name = "post")
    String post = "0";
    @JSONField(name = "comment")
    String comment = "0";

    @JSONField(name = "myfriends")
    String myfriends = "";
    @JSONField(name = "myblacklist")
    String myblacklist = "";

    @JSONField(name = "play_once")
    String play_once;
    @JSONField(name = "more_five")
    String more_five;
    @JSONField(name = "play_completed")
    String play_completed;
    @JSONField(name = "play_time")
    String play_time;

    @JSONField(name = "id")
    String id;
    @JSONField(name = "plateid")
    String plateid;
    @JSONField(name = "platename")
    String platename;
    @JSONField(name = "authid")
    String authid;
    @JSONField(name = "authnickname")
    String authnickname;
    @JSONField(name = "authportrait")
    String authportrait;
    @JSONField(name = "portraitframe")
    String portraitframe;
    @JSONField(name = "posttip")
    String posttip;
    @JSONField(name = "posttitle")
    String posttitle;
    @JSONField(name = "posttext")
    String posttext;
    @JSONField(name = "postpicture")
    String postpicture;

    String[] postpictureList;



    @JSONField(name = "like")
    String like;
    @JSONField(name = "favorite")
    String favorite;
    @JSONField(name = "time")
    String time;
    @JSONField(name = "age")
    String authage;
    @JSONField(name = "gender")
    String authgender;
    @JSONField(name = "region")
    String authregion;
    @JSONField(name = "property")
    String authproperty;
    @JSONField(name = "vip")
    String authvip;
    @JSONField(name = "svip")
    String authsvip;
    @JSONField(name = "comment_sum")
    String comment_sum;
    @JSONField(name = "comment_forbid")
    String comment_forbid;



    @JSONField(name = "postvideo")
    String postvideo;
    @JSONField(name = "width")
    String width;
    @JSONField(name = "height")
    String height;
    @JSONField(name = "cover")
    String cover;

    Boolean isStartVideo = false;

    @JSONField(name = "online")
    String online = "0";


    public LuntanList(String authage, String authgender, String authregion, String authproperty, String id, String plateid,
                      String platename, String authid, String authnickname, String authportrait, String posttip,
                      String posttitle, String posttext, String[] postpictureList, String like, String favorite, String time,
                      String authvip,String authsvip,String comment_sum) {
        this.authage = authage;
        this.authgender = authgender;
        this.authregion = authregion;
        this.authproperty = authproperty;
        this.id = id;
        this.plateid = plateid;
        this.platename = platename;
        this.authid = authid;
        this.authnickname = authnickname;
        this.authportrait = authportrait;
        this.posttip = posttip;
        this.posttitle = posttitle;
        this.posttext = posttext;
        this.postpictureList = postpictureList;
        this.like = like;
        this.favorite = favorite;
        this.time = time;

        this.authvip = authvip;
        this.authsvip = authsvip;

        this.comment_sum = comment_sum;
    }

    public String getRp_verify_time() {
        return rp_verify_time;
    }

    public void setRp_verify_time(String rp_verify_time) {
        this.rp_verify_time = rp_verify_time;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    public String getPlay_time() {
        return play_time;
    }

    public void setPlay_time(String play_time) {
        this.play_time = play_time;
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

    public String getPlay_once() {
        return play_once;
    }

    public void setPlay_once(String play_once) {
        this.play_once = play_once;
    }

    public String getMore_five() {
        return more_five;
    }

    public void setMore_five(String more_five) {
        this.more_five = more_five;
    }

    public String getPlay_completed() {
        return play_completed;
    }

    public void setPlay_completed(String play_completed) {
        this.play_completed = play_completed;
    }

    public String getPortraitframe() {
        return Common.getOssResourceUrl(portraitframe);
    }

    public void setPortraitframe(String portraitframe) {
        this.portraitframe = portraitframe;
    }

    public String getOnline() {
        return xin.banghua.beiyuan.Common.getOnlineState(Long.parseLong(online));
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getCover() {
        return Common.getOssResourceUrl(cover);
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Boolean getStartVideo() {
        return isStartVideo;
    }

    public void setStartVideo(Boolean startVideo) {
        isStartVideo = startVideo;
    }

    public String getPostvideo() {
        return Common.getOssResourceUrl(postvideo);
    }

    public void setPostvideo(String postvideo) {
        this.postvideo = postvideo;
    }

    public String getPostpicture() {
        return postpicture;
    }

    public void setPostpicture(String postpicture) {
        this.postpicture = postpicture;
    }

    public String getComment_forbid() {
        return comment_forbid;
    }

    public void setComment_forbid(String comment_forbid) {
        this.comment_forbid = comment_forbid;
    }

    public String getComment_sum() {
        return comment_sum;
    }

    public void setComment_sum(String comment_sum) {
        this.comment_sum = comment_sum;
    }

    public String getAuthsvip() {
        return authsvip;
    }

    public void setAuthsvip(String authsvip) {
        this.authsvip = authsvip;
    }

    public String getAuthage() {
        return authage;
    }

    public void setAuthage(String authage) {
        this.authage = authage;
    }

    public String getAuthgender() {
        return authgender;
    }

    public void setAuthgender(String authgender) {
        this.authgender = authgender;
    }

    public String getAuthregion() {
        return authregion;
    }

    public void setAuthregion(String authregion) {
        this.authregion = authregion;
    }

    public String getAuthproperty() {
        return authproperty;
    }

    public void setAuthproperty(String authproperty) {
        this.authproperty = authproperty;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPlateid(String plateid) {
        this.plateid = plateid;
    }

    public void setPlatename(String platename) {
        this.platename = platename;
    }

    public void setAuthid(String authid) {
        this.authid = authid;
    }

    public void setAuthnickname(String authnickname) {
        this.authnickname = authnickname;
    }

    public void setAuthportrait(String authportrait) {
        this.authportrait = authportrait;
    }

    public void setPosttip(String posttip) {
        this.posttip = posttip;
    }

    public void setPosttitle(String posttitle) {
        this.posttitle = posttitle;
    }

    public void setPosttext(String posttext) {
        this.posttext = posttext;
    }

    public void setPostpictureList(String[] postpictureList) {
        this.postpictureList = postpictureList;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getPlateid() {
        return plateid;
    }

    public String getPlatename() {
        return platename;
    }

    public String getAuthid() {
        return authid;
    }

    public String getAuthnickname() {
        return authnickname;
    }

    public String getAuthportrait() {
        return authportrait;
    }

    public String getPosttip() {
        return posttip;
    }

    public String getPosttitle() {
        return posttitle;
    }

    public String getPosttext() {
        return posttext;
    }

    public String[] getPostpictureList() {
        return postpictureList;
    }

    public String getLike() {
        return like;
    }

    public String getFavorite() {
        return favorite;
    }

    public String getTime() {
        if (time.contains("前")){
            return time;
        }else {
            return Common.getShortTime(Long.parseLong(time));
        }
    }

    public String getAuthvip() {
        return authvip;
    }

    public void setAuthvip(String authvip) {
        this.authvip = authvip;
    }
}
