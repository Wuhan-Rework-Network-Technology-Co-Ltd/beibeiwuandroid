package xin.banghua.beiyuan.Adapter;

import com.alibaba.fastjson.annotation.JSONField;

public class FollowList {
    public FollowList() {
    }

    @JSONField(name = "userId")
    String userId;


    public FollowList(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
