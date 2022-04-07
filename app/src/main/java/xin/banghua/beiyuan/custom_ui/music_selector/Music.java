package xin.banghua.beiyuan.custom_ui.music_selector;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class Music implements Serializable {
	@JSONField(name="music_name")
	private String name;   //歌曲名称
	@JSONField(name="music_singer")
	private String singer; //歌手
	@JSONField(name="music_duration")
	private long duration; //歌曲时长
	@JSONField(name="music_path")
	private String path;   //歌曲路径
	@JSONField(name="music_tag")
	private String tag;   //歌曲标签
	@JSONField(name="music_image")
	private String image;   //歌曲标签
	@JSONField(name="used_times")
	private String used_times;   //歌曲使用次数
	@JSONField(name="first_publish")
	private String first_publish;   //首发作品id

	private String localOrOnline = "online";   //类型，online是在线，local是本地，
	
	public Music() {
		super();
	}
	public Music(String name, String singer, long duration, String path, String localOrOnline) {
		super();
		this.name = name;
		this.singer = singer;
		this.duration = duration;
		this.path = path;
		this.localOrOnline = localOrOnline;
	}

	public String getUsed_times() {
		return used_times;
	}

	public void setUsed_times(String used_times) {
		this.used_times = used_times;
	}

	public String getFirst_publish() {
		return first_publish;
	}

	public void setFirst_publish(String first_publish) {
		this.first_publish = first_publish;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getLocalOrOnline() {
		return localOrOnline;
	}

	public void setLocalOrOnline(String localOrOnline) {
		this.localOrOnline = localOrOnline;
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
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
