package xin.banghua.beiyuan.PushPackage;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.heytap.msp.push.HeytapPushManager;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.meizu.cloud.pushsdk.PushManager;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.xiaomi.mipush.sdk.MiPushClient;

import xin.banghua.beiyuan.PushPackage.Oppo.OppoPushCallback;

public class PushClass {
    private static final String TAG = "PushClass";

    public static Context mContext;

    public static String phoneBrand = "other";
    public static String pushRegID = "other";

    /**
     * Init push service.
     */
    public static void initPushService(Context context){
        mContext = context;

        if(RomUtil.isMiui()){//如果是小米设备，则初始化小米推送
            Log.d(TAG, "RomUtil: isMiui");
            phoneBrand = "小米";
            initXiaomiPush();
        }else if (RomUtil.isOppo()){
            Log.d(TAG, "RomUtil: isOppo");
            phoneBrand = "oppo";
            HeytapPushManager.init(mContext,false);
            if (HeytapPushManager.isSupportPush()){
                HeytapPushManager.register(mContext, "09cbed5f985842c1b962b9b509ee3ef1", "726b8a421d1b4e4e80e4813ed04d85a5", new OppoPushCallback());
            }
        }else if (RomUtil.isEmui()){
            Log.d(TAG, "RomUtil: isEmui");
            phoneBrand = "华为";
            initHuaweiPush();
        }else if (RomUtil.isVivo()){
            Log.d(TAG, "RomUtil: isVivo");
            phoneBrand = "vivo";
            initVivoPush();
        } else if (RomUtil.isFlyme()){
            Log.d(TAG, "RomUtil: isFlyme");
            phoneBrand = "魅族";
            PushManager.register(mContext, "124945", "399a4bb4701046ffbff85a5505251abb");
        }
    }


    public static void initHuaweiPush(){
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(mContext).getString("client/app_id");
                    String token = HmsInstanceId.getInstance(mContext).getToken(appId, "HCM");
                    Log.i(TAG, "get token:" + token);
                    if(!TextUtils.isEmpty(token)) {
                        PushClass.pushRegID = token;
                        Log.d(TAG, "initHuaweiPush华为推送:注册成功 regID =: "+PushClass.pushRegID);
                    }
                } catch (ApiException e) {
                    Log.e(TAG, "get token failed, " + e);
                }
            }
        }.start();
    }


    public static void initXiaomiPush(){
        MiPushClient.registerPush(mContext, "2882303761518213592", "5531821328592");
    }

    public static void initVivoPush(){
        //VivoGetToken.initVivoPush(mContext);
        // 在当前工程入口函数，建议在Application的onCreate函数中，添加以下代码
        PushClient.getInstance(mContext).initialize();
        // 打开push开关, 关闭为turnOffPush，详见api接入文档
        PushClient.getInstance(mContext).turnOnPush(new IPushActionListener() {
            @Override
            public void onStateChanged(int state) {
                // TODO: 开关状态处理， 0代表成功  有缓存时需要PushClient.getInstance(mContext).getRegId()获取缓存打的regid
                Log.d(TAG, "vivo推送: 开关状态处理"+state+PushClient.getInstance(mContext).getRegId());
                if (PushClient.getInstance(mContext).getRegId()!=null)
                   PushClass.pushRegID = PushClient.getInstance(mContext).getRegId();
            }
        });
//        VUpsManager.getInstance().turnOnPush(mContext,new UPSTurnCallback() {
//            @Override
//            public void onResult(CodeResult codeResult) {
//                if(codeResult.getReturnCode() == 0){
//                    Log.d(TAG, "vivo推送:初始化成功");
//                }else {
//                    Log.d(TAG, "vivo推送:初始化失败");
//                }
//            }
//        });
//        VUpsManager.getInstance().registerToken(mContext,"20018", "42e08441-ce3f-408d-a0a5-0d5ffbd4d19e", "c4cedf50-2788-4bec-952b-d62a8a2af545", new UPSRegisterCallback()   {
//            @Override
//            public void onResult(TokenResult tokenResult) {
//                if (tokenResult.getReturnCode() == 0) {
//                    pushRegID = tokenResult.getToken();
//                    Log.d(TAG, "vivo推送:注册成功 regID = " +   pushRegID);
//                } else {
//                    Log.d(TAG, "vivo推送:注册失败"+tokenResult.getReturnCode());
//                }
//            }
//        });
    }
}
