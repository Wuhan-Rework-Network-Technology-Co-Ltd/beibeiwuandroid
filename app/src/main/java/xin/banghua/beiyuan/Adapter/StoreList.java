package xin.banghua.beiyuan.Adapter;

import com.alibaba.fastjson.annotation.JSONField;

import xin.banghua.beiyuan.Common;

public class StoreList {
    public StoreList() {
    }

    @JSONField(name = "auyhid")
    String auyhid;
    @JSONField(name = "goods_id")
    String goods_id;

    @JSONField(name = "id")
    String id;
    @JSONField(name = "type")
    String type;
    @JSONField(name = "name")
    String name;
    @JSONField(name = "image")
    String image;
    @JSONField(name = "svga")
    String svga;
    @JSONField(name = "description")
    String description;
    @JSONField(name = "currency")
    String currency;
    @JSONField(name = "price")
    String price;
    @JSONField(name = "tag")
    String tag;
    @JSONField(name = "time")
    String time;

    public String getAuyhid() {
        return auyhid;
    }

    public void setAuyhid(String auyhid) {
        this.auyhid = auyhid;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSvga() {
        return Common.getOssResourceUrl(svga);
    }

    public void setSvga(String svga) {
        this.svga = svga;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
