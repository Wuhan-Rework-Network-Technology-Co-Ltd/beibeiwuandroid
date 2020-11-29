package xin.banghua.beiyuan;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import io.rong.contactcard.ContactCardExtensionModule;
import io.rong.contactcard.IContactCardInfoProvider;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.push.RongPushClient;
import io.rong.push.pushconfig.PushConfig;
import io.rong.sight.SightExtensionModule;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Main3Branch.RongyunConnect;
import xin.banghua.beiyuan.PushPackage.PushClass;
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

        //每次新启动，设值为1
        SharedHelper shvalue = new SharedHelper(getApplicationContext());
        shvalue.saveOnestart(1);

        PushClass.initPushService(getApplicationContext());


        Log.d(TAG, "onCreate: onActivityonCreate:");
        registerActivityLifecycleCallbacks(this);
        //融云
        PushConfig config = new PushConfig.Builder()
                //.enableHWPush(true)
                //.enableVivoPush(true)
                //.enableMiPush("2882303761518213592", "5531821328592")
                //.enableMeiZuPush("124945","399a4bb4701046ffbff85a5505251abb")
                //.enableOppoPush("09cbed5f985842c1b962b9b509ee3ef1","726b8a421d1b4e4e80e4813ed04d85a5")
                .build();
        RongPushClient.setPushConfig(config);
        RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                return false;
            }
        });
        RongIM.init(this);
        RongExtensionManager.getInstance().registerExtensionModule(new SightExtensionModule());
        RongIM.getInstance().setConversationClickListener(new MyConversationClickListener());
        //initContactCard();
        //验证连接成功
//          connect("JeXL+71vahbPjzSTYBlf3Okw/3FJenp53iTgy0iFgV+zWO2xI0jlx8+r479bFjga59uiwpcN87KhrP49wK/ZpQ==");
        //为了测试 这是贝吉塔的token 贝吉塔跟super果实现单聊
//        SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
//        String token = shuserinfo.readRongtoken().get("Rongtoken");
//        connect(token);
        closeAndroidPDialog();






        frontOrBack();
    }
    /**
     * 判断在前台还是后台
     */
    public int count = 0;
    private void frontOrBack() {
        //前后台切换判断
        registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }
            @Override
            public void onActivityStarted(Activity activity) {
                if (count == 0) {
                    Log.v("vergo", "**********切到前台**********");
                    setFrontOrBack("1");
                }
                count++;
            }
            @Override
            public void onActivityResumed(Activity activity) {
            }
            @Override
            public void onActivityPaused(Activity activity) {
            }
            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if (count == 0) {
                    Log.v("vergo", "**********切到后台**********");
                    setFrontOrBack("0");
                }
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }
            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    private void setFrontOrBack(String frontOrBack){
        Map<String, String> userInfo;
        SharedHelper sh;
        sh = new SharedHelper(getApplicationContext());
        userInfo = sh.readUserInfo();
        //Toast.makeText(mContext, userInfo.toString(), Toast.LENGTH_SHORT).show();
        if (!userInfo.get("userID").equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .add("myid", userInfo.get("userID"))
                            .add("frontorback", frontOrBack)
                            .add("phonebrand", PushClass.phoneBrand)
                            .add("pushregid",PushClass.pushRegID)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://weiqing.oushelun.cn/app/index.php?i=99999&c=entry&a=webapp&do=xiaobeifrontorback&m=rediscache")
                            .post(formBody)
                            .build();
                    try (Response response = client.newCall(request).execute()) {

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    private void closeAndroidPDialog(){
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initContactCard(){
        ContactCardExtensionModule contactCardExtensionModule = new ContactCardExtensionModule(new IContactCardInfoProvider() {
            /**
             * 获取所有通讯录用户
             *
             * @param contactInfoCallback
             */
            @Override
            public void getContactAllInfoProvider(IContactCardInfoCallback contactInfoCallback) {
                Log.d(TAG, "getContactAllInfoProvider: 呵呵");
                //imInfoProvider.getAllContactUserInfo(contactInfoCallback);
            }

            /**
             * 获取单一用户
             *
             * @param userId
             * @param name
             * @param portrait
             * @param contactInfoCallback
             */
            @Override
            public void getContactAppointedInfoProvider(String userId, String name, String portrait, IContactCardInfoCallback contactInfoCallback) {
                Log.d(TAG, "getContactAppointedInfoProvider: 哈哈");
                //imInfoProvider.getContactUserInfo(userId, contactInfoCallback);
            }
        }, (view, content) -> {
            Context activityContext = view.getContext();
            // 点击名片进入到个人详细界面
            Intent intent = new Intent(activityContext, Main3Activity.class);
            intent.putExtra("sadasdasada", content.getId());
            activityContext.startActivity(intent);
        });
        //ContactCardContext.getInstance().setContactCardSelectListProvider(new MyIContactCardSelectListProvider());

        RongExtensionManager.getInstance().registerExtensionModule(contactCardExtensionModule);

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
        //RongIM.getInstance().disconnect();//断开融云
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
