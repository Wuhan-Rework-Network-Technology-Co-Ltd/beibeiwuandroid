package xin.banghua.beiyuan.Adapter;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

import xin.banghua.beiyuan.Common;

public class ScriptList implements Serializable {

    public ScriptList() {
    }

    @JSONField(name="id")
    String id = "";
    @JSONField(name="type")
    String type = "";
    @JSONField(name="script_name")
    String script_name = "";
    @JSONField(name="script_description")
    String script_description = "";
    @JSONField(name="script_singer")
    String script_singer = "";
    @JSONField(name="script_user")
    String script_user = "";
    @JSONField(name="script_video")
    String script_video = "";
    @JSONField(name="script_cover")
    String script_cover = "";
    @JSONField(name="script_during")
    String script_during = "";
    @JSONField(name="script_times")
    String script_times = "";
    @JSONField(name="time")
    String time = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScript_name() {
        return script_name;
    }

    public void setScript_name(String script_name) {
        this.script_name = script_name;
    }

    public String getScript_description() {
        return script_description;
    }

    public void setScript_description(String script_description) {
        this.script_description = script_description;
    }

    public String getScript_singer() {
        return script_singer;
    }

    public void setScript_singer(String script_singer) {
        this.script_singer = script_singer;
    }

    public String getScript_user() {
        return script_user;
    }

    public void setScript_user(String script_user) {
        this.script_user = script_user;
    }

    public String getScript_video() {
        return script_video;
    }

    public void setScript_video(String script_video) {
        this.script_video = script_video;
    }

    public String getScript_cover() {
        return Common.getOssResourceUrl(script_cover);
    }

    public void setScript_cover(String script_cover) {
        this.script_cover = script_cover;
    }

    public String getScript_during() {
        return script_during;
    }

    public void setScript_during(String script_during) {
        this.script_during = script_during;
    }

    public String getScript_times() {
        return script_times;
    }

    public void setScript_times(String script_times) {
        this.script_times = script_times;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
