package xin.banghua.beiyuan.PushPackage.Meizu;

import android.content.Context;
import android.util.Log;

import com.meizu.cloud.pushsdk.MzPushMessageReceiver;
import com.meizu.cloud.pushsdk.platform.message.PushSwitchStatus;
import com.meizu.cloud.pushsdk.platform.message.RegisterStatus;
import com.meizu.cloud.pushsdk.platform.message.SubAliasStatus;
import com.meizu.cloud.pushsdk.platform.message.SubTagsStatus;
import com.meizu.cloud.pushsdk.platform.message.UnRegisterStatus;

import xin.banghua.beiyuan.PushPackage.PushClass;

public class MeizuMessageReceiver extends MzPushMessageReceiver {
    private static final String TAG = "MeizuMessageReceiver";
    @Override
    public void onRegisterStatus(Context context, RegisterStatus registerStatus) {
        PushClass.pushRegID = registerStatus.getPushId();
        Log.d(TAG, "onNewToken魅族推送:注册成功 regID =: "+PushClass.pushRegID);
    }

    @Override
    public void onUnRegisterStatus(Context context, UnRegisterStatus unRegisterStatus) {

    }

    @Override
    public void onPushStatus(Context context, PushSwitchStatus pushSwitchStatus) {

    }

    @Override
    public void onSubTagsStatus(Context context, SubTagsStatus subTagsStatus) {

    }

    @Override
    public void onSubAliasStatus(Context context, SubAliasStatus subAliasStatus) {

    }
}
