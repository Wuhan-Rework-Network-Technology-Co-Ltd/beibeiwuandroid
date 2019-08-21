package xin.banghua.beiyuan;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.push.RongPushClient;
import io.rong.push.pushconfig.PushConfig;
import xin.banghua.beiyuan.Main3Branch.RongyunConnect;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

/**
 * 作者：Administrator
 * 时间：2019/3/1
 * 功能：
 */
public class App extends Application implements Application.ActivityLifecycleCallbacks{
    private static final String TAG = "App";
    /**
     * APP key:mgb7ka1nmddvg
     * App Secret: XMifrzdXUJz
     * <p>
     * <p>
     * token采用融云集成调试获取
     * token1、
     * Request： {userId=523846&name=super果&portraitUri=http://img.my.csdn.net/uploads/201309/01/1378037193_1286.jpg}
     * Response：{"code":200,"userId":"523846","token":"JeXL+71vahbPjzSTYBlf3Okw/3FJenp53iTgy0iFgV+zWO2xI0jlx8+r479bFjga59uiwpcN87KhrP49wK/ZpQ=="}
     * <p>
     * token2、
     * Request： {userId=623846&name=贝吉塔&portraitUri=http://img.my.csdn.net/uploads/201309/01/1378037193_1286.jpg}
     * Response：{"code":200,"userId":"623846","token":"EacIz4+D+DRuqsNNIIPgxekw/3FJenp53iTgy0iFgV+zWO2xI0jlx4uF9kwNt6u3GIRIZqWHKKehrP49wK/ZpQ=="}
     * <p>
     * token3、
     * Request： {userId=723846&name=希特&portraitUri=http://img.my.csdn.net/uploads/201309/01/1378037193_1286.jpg}
     * Response：{"code":200,"userId":"723846","token":"pElfOoRbRK1Uka6WGUsmk+kw/3FJenp53iTgy0iFgV+zWO2xI0jlx9QozjjqpuGuVhvs9Cfvmj+hrP49wK/ZpQ=="}
     */
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        //融云
        PushConfig config = new PushConfig.Builder()
                .enableHWPush(true)
                .enableMiPush("2882303761518124084", "5641812458084")
                .enableHWPush(true)
                .build();
        RongPushClient.setPushConfig(config);
        RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                return false;
            }
        });
        RongIM.init(this);
        //验证连接成功
//          connect("JeXL+71vahbPjzSTYBlf3Okw/3FJenp53iTgy0iFgV+zWO2xI0jlx8+r479bFjga59uiwpcN87KhrP49wK/ZpQ==");
        //为了测试 这是贝吉塔的token 贝吉塔跟super果实现单聊
//        SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
//        String token = shuserinfo.readRongtoken().get("Rongtoken");
//        connect(token);
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    public void connect(String token) {
        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {

            Log.d(TAG, "connect: 进入app的融云链接");
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查
                 * 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 * 2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.d("RONGCLOUD", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("RONGCLOUD", "--onSuccess" + userid);

                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d("RONGCLOUD", "--error" + errorCode.getMessage());
                }
            });
        }
    }


    /**
     * 保证融云客户端只在RongIMClient 的进程和 Push 进程执行了 init
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated:");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(TAG, "onActivityStarted: ");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, "onActivityResumed: ");
        //获取融云token
        SharedHelper sh = new SharedHelper(getApplicationContext());
        String token  = sh.readRongtoken().get("Rongtoken");
        RongyunConnect rongyunConnect = new RongyunConnect();
        rongyunConnect.connect(token);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, "onActivityPaused: ");
        RongIM.getInstance().disconnect();//断开融云
    }

    @Override
    public void onActivityStopped(Activity activity) {
            Log.d(TAG, "onActivityStopped:");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(TAG, "onActivitySaveInstanceState: ");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "onActivityDestroyed: ");
    }
}