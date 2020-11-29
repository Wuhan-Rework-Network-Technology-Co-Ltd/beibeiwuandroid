package xin.banghua.beiyuan.PushPackage.Oppo;

import android.util.Log;

import com.heytap.msp.push.callback.ICallBackResultService;

import xin.banghua.beiyuan.PushPackage.PushClass;

public class OppoPushCallback implements ICallBackResultService {
    private static final String TAG = "OppoPushCallback";
    @Override
    public void onRegister(int i, String s) {
        if (i==0) {
            PushClass.pushRegID = s;
            Log.d(TAG, "oppo推送:注册成功 regID = " + PushClass.pushRegID);
        }else {
            Log.d(TAG, "oppo推送:注册失败 i = " + i);
        }
    }

    @Override
    public void onUnRegister(int i) {

    }

    @Override
    public void onSetPushTime(int i, String s) {

    }

    @Override
    public void onGetPushStatus(int i, int i1) {

    }

    @Override
    public void onGetNotificationStatus(int i, int i1) {

    }
}
