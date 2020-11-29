package xin.banghua.beiyuan.PushPackage.Huawei;

import android.util.Log;

import com.huawei.hms.push.HmsMessageService;

import xin.banghua.beiyuan.PushPackage.PushClass;

public class HuaweiMessageReceiver  extends HmsMessageService {
    private static final String TAG = "HuaweiMessageReceiver";
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        PushClass.pushRegID = s;
        Log.d(TAG, "onNewToken华为推送:注册成功 regID =: "+PushClass.pushRegID);
    }
}
