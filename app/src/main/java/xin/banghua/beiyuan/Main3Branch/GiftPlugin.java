package xin.banghua.beiyuan.Main3Branch;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;

import io.agora.chatroom.gift.GiftDialog;
import io.agora.chatroom.model.Member;
import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.ConversationActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;

public class GiftPlugin implements IPluginModule {
    private static final String TAG = "GiftPlugin";
    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.rc_gift_plugin_icon);
    }

    @Override
    public String obtainTitle(Context context) {
        return "礼物";
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        Log.d(TAG, "onClick: 聊天对象"+rongExtension.getTargetId());
        OkHttpInstance.getUserAttributes(rongExtension.getTargetId(), new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                UserInfoList userInfoList = JSON.parseObject(responseString,UserInfoList.class);
                GiftDialog.getInstance(fragment.getActivity(),false, ConversationActivity.container,null)
                        .initShow(new Member(userInfoList.getId(), userInfoList.getNickname(),
                                userInfoList.getPortrait(),userInfoList.getGender(),userInfoList.getProperty(),
                                userInfoList.getPortraitframe(),userInfoList.getVeilcel()));
            }
        });
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}