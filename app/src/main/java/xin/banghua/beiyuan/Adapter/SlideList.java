package xin.banghua.beiyuan.Adapter;

import com.alibaba.fastjson.annotation.JSONField;

import xin.banghua.beiyuan.Common;

public class SlideList {
    public SlideList() {
    }

    @JSONField(name = "id")
    String id;
    @JSONField(name = "slidesort")
    String slidesort;
    @JSONField(name = "slidename")
    String slidename;
    @JSONField(name = "slidepicture")
    String slidepicture;
    @JSONField(name = "slideurl")
    String slideurl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlidesort() {
        return slidesort;
    }

    public void setSlidesort(String slidesort) {
        this.slidesort = slidesort;
    }

    public String getSlidename() {
        return slidename;
    }

    public void setSlidename(String slidename) {
        this.slidename = slidename;
    }

    public String getSlidepicture() {
        return Common.getOssResourceUrl(slidepicture);
    }

    public void setSlidepicture(String slidepicture) {
        this.slidepicture = slidepicture;
    }

    public String getSlideurl() {
        return slideurl;
    }

    public void setSlideurl(String slideurl) {
        this.slideurl = slideurl;
    }
}
