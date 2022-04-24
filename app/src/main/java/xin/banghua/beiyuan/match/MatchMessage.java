package xin.banghua.beiyuan.match;


import android.annotation.SuppressLint;
import android.os.Parcel;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import xin.banghua.beiyuan.Adapter.UserInfoList;

@MessageTag(value = "RC:CustomMsg", flag = MessageTag.ISCOUNTED)
@SuppressLint("ParcelCreator")
public class MatchMessage extends MessageContent {
    private static final String TAG = "MatchMessage";
    // 快速构建消息对象方法
    public static MatchMessage obtain(UserInfoList userInfoList) {
        MatchMessage model = new MatchMessage(userInfoList);
        return model;
    }
    public MatchMessage() {

    }

    public MatchMessage(Parcel in) {
        userInfoListString = in.readString();
        setUserInfo(ParcelUtils.readFromParcel(in, UserInfo.class));
        setDestruct(ParcelUtils.readIntFromParcel(in) == 1);
        setDestructTime(ParcelUtils.readLongFromParcel(in));
    }
    public static final Creator<MatchMessage> CREATOR = new Creator<MatchMessage>() {
        @Override
        public MatchMessage createFromParcel(Parcel source) {
            return new MatchMessage(source);
        }

        @Override
        public MatchMessage[] newArray(int size) {
            return new MatchMessage[size];
        }
    };
    public MatchMessage(UserInfoList userInfoList) {
        userInfoListString = JSON.toJSONString(userInfoList);
    }
    String userInfoListString;
    /**
     * 将本地消息对象序列化为消息数据。
     *
     * @return 消息数据。
     */
    @Override
    public byte[] encode() {
        try {
            return userInfoListString.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            //Rog.e(TAG, "UnsupportedEncodingException ", e);
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userInfoListString);
    }
}