package xin.banghua.beiyuan.PushPackage.Vivo;

import android.content.Context;
import android.util.Log;

import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.vivo.push.ups.CodeResult;
import com.vivo.push.ups.TokenResult;
import com.vivo.push.ups.UPSRegisterCallback;
import com.vivo.push.ups.UPSTurnCallback;
import com.vivo.push.ups.VUpsManager;

import xin.banghua.beiyuan.PushPackage.PushClass;

public class VivoGetToken {
    private static final String TAG = "VivoGetToken";

    public static void initVivoPush(Context mContext){
        // 在当前工程入口函数，建议在Application的onCreate函数中，添加以下代码
        PushClient.getInstance(mContext).initialize();
        // 打开push开关, 关闭为turnOffPush，详见api接入文档
        PushClient.getInstance(mContext).turnOnPush(new IPushActionListener() {
            @Override
            public void onStateChanged(int state) {
                // TODO: 开关状态处理， 0代表成功
                Log.d(TAG, "vivo推送: 开关状态处理"+state);
            }
        });
        VUpsManager.getInstance().turnOnPush(mContext,new UPSTurnCallback() {
            @Override
            public void onResult(CodeResult codeResult) {
                if(codeResult.getReturnCode()   == 0){
                    Log.d(TAG, "vivo推送:初始化成功");
                }else {
                    Log.d(TAG, "vivo推送:初始化失败");
                }
            }
        });
        VUpsManager.getInstance().registerToken(mContext,"20018", "42e08441-ce3f-408d-a0a5-0d5ffbd4d19e", "c4cedf50-2788-4bec-952b-d62a8a2af545", new UPSRegisterCallback()   {
            @Override
            public void onResult(TokenResult tokenResult) {
                if   (tokenResult.getReturnCode() == 0) {
                    PushClass.pushRegID = tokenResult.getToken();
                    Log.d(TAG, "vivo推送:注册成功 regID = " +   PushClass.pushRegID);
                } else {
                    Log.d(TAG, "vivo推送:注册失败");
                }
            }
        });
    }
}
