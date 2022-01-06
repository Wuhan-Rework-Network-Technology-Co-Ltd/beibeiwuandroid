package xin.banghua.beiyuan.comment;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

import xin.banghua.beiyuan.utils.Common;

public class CommentList implements Serializable{
    public CommentList() {
    }

    @JSONField(name = "id")
    String id = "";
    @JSONField(name = "postid")
    String postid = "";
    @JSONField(name = "postid_user")
    String postid_user = "";
    @JSONField(name = "authid")
    String authid = "";

    @JSONField(name = "portrait")
    String portrait = "";
    @JSONField(name = "nickname")
    String nickname = "";

    @JSONField(name = "mainID")
    String mainID = "";
    @JSONField(name = "mainID_user")
    String mainID_user = "";

    @JSONField(name = "subID")
    String subID = "";
    @JSONField(name = "subID_comment")
    String subID_comment = "";
    @JSONField(name = "sub_nickname")
    String sub_nickname = "";

    @JSONField(name = "comment_text")
    String comment_text = "";
    @JSONField(name = "subcomment_num")
    String subcomment_num = "";
    @JSONField(name = "like")
    String like = "";

    String ifauthlike = "";
    @JSONField(name = "ifauthreply")
    String ifauthreply = "";
    @JSONField(name = "iftop")
    String iftop = "";
    @JSONField(name = "at")
    String at = "";

    @JSONField(name = "time")
    String time = "";

    @JSONField(name = "forbid")
    String forbid = "";

    @JSONField(name = "posttitle")
    String posttitle;

    public String getPostid_user() {
        return postid_user;
    }

    public void setPostid_user(String postid_user) {
        this.postid_user = postid_user;
    }

    public String getMainID_user() {
        return mainID_user;
    }

    public void setMainID_user(String mainID_user) {
        this.mainID_user = mainID_user;
    }

    public String getSubID_comment() {
        return subID_comment;
    }

    public void setSubID_comment(String subID_comment) {
        this.subID_comment = subID_comment;
    }

    public String getPosttitle() {
        return posttitle;
    }

    public void setPosttitle(String posttitle) {
        this.posttitle = posttitle;
    }

    public String getForbid() {
        return forbid;
    }

    public void setForbid(String forbid) {
        this.forbid = forbid;
    }

    public String getSub_nickname() {
        return sub_nickname;
    }

    public void setSub_nickname(String sub_nickname) {
        this.sub_nickname = sub_nickname;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getAuthid() {
        return authid;
    }

    public void setAuthid(String authid) {
        this.authid = authid;
    }



    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getSubcomment_num() {
        return subcomment_num;
    }

    public void setSubcomment_num(String subcomment_num) {
        this.subcomment_num = subcomment_num;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }



    public String getIfauthlike() {
        return ifauthlike;
    }

    public void setIfauthlike(String ifauthlike) {
        this.ifauthlike = ifauthlike;
    }

    public String getIfauthreply() {
        return ifauthreply;
    }

    public void setIfauthreply(String ifauthreply) {
        this.ifauthreply = ifauthreply;
    }

    public String getIftop() {
        return iftop;
    }

    public void setIftop(String iftop) {
        this.iftop = iftop;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }



    public String getTime() {
        return Common.getShortTime(Long.parseLong(time));
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMainID() {
        return mainID;
    }

    public void setMainID(String mainID) {
        this.mainID = mainID;
    }

    public String getSubID() {
        return subID;
    }

    public void setSubID(String subID) {
        this.subID = subID;
    }
}
