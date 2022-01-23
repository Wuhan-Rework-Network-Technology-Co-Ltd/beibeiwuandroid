package xin.banghua.beiyuan;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.ha.adapter.AliHaAdapter;
import com.alibaba.ha.adapter.AliHaConfig;
import com.alibaba.ha.adapter.Plugin;
import com.alibaba.sdk.android.cloudcode.CloudCodeInitializer;
import com.alibaba.sdk.android.cloudcode.CloudCodeLog;
import com.alibaba.sdk.android.logger.LogLevel;
import com.baidu.mobads.sdk.api.BDAdConfig;
import com.baidu.mobads.sdk.api.BDDialogParams;
import com.baidu.mobads.sdk.api.MobadsPermissionSettings;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdSdk;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import io.agora.chatroom.ChatRoomApplication;
import io.agora.chatroom.manager.RtcManager;
import io.agora.chatroom.manager.RtmManager;
import io.agora.chatroom.model.Constant;
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
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.PushPackage.PushClass;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

//import xin.banghua.beiyuan.ContactCardExtensionModule;
//import io.rong.contactcard.IContactCardInfoProvider;

/**
 * 作者：Administrator
 * 时间：2019/3/1
 * 功能：
 */
public class App extends Application implements Application.ActivityLifecycleCallbacks{
    private static final String TAG = "App";

    public static Application mApplication;
    public static Application getApplication() {
        return mApplication;
    }
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

        mApplication = this;

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
        RongIM.init(this,"m7ua80gbmo0km");
        RongExtensionManager.getInstance().registerExtensionModule(new SightExtensionModule());
        RongIM.getInstance().setConversationClickListener(new MyConversationClickListener());
//        initContactCard();
        //验证连接成功
//          connect("JeXL+71vahbPjzSTYBlf3Okw/3FJenp53iTgy0iFgV+zWO2xI0jlx8+r479bFjga59uiwpcN87KhrP49wK/ZpQ==");
        //为了测试 这是贝吉塔的token 贝吉塔跟super果实现单聊
//        SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
//        String token = shuserinfo.readRongtoken().get("Rongtoken");
//        connect(token);

//        closeAndroidPDialog();


        //获取融云token
        SharedHelper sh = new SharedHelper(getApplicationContext());
        String token  = sh.readRongtoken().get("Rongtoken");
        RongyunConnect rongyunConnect = new RongyunConnect();
        rongyunConnect.connect(token);




        frontOrBack();



    }



    public static void initThirdSDK(){
        Log.d(TAG, "initThirdSDK: 启动第三方sdk");
        initHa();
        adInit();


        //聊天室
        ChatRoomApplication.instance = getApplication();
        RtcManager.instance(getApplication()).init();
        RtmManager.instance(getApplication()).init();
        Constant.goToPersonalPage =  new Intent(getApplication(), PersonageActivity.class);
    }

    /**
     * 云码广告初始化
     */
    public static void adInit(){

        //云码
        // 用户签署隐私协议之后，调用云码sdk初始化
        CloudCodeInitializer.init(getApplication());
        // 如果没有在manifest中配置渠道ID和媒体ID，需要在此处传入
//        CloudCodeInitializer.init(this, "598394599954655233", "598800657214651394");
        // 设置日志输出级别为debug
        CloudCodeLog.setLevel(LogLevel.DEBUG);


        //百度广告
        BDAdConfig bdAdConfig = new BDAdConfig.Builder()
                // 1、设置app名称，可选
                .setAppName("小贝乐园")
                // 2、应用在mssp平台申请到的appsid，和包名一一对应，此处设置等同于在AndroidManifest.xml里面设置
                .setAppsid("f678b1ce")
                // 3、设置下载弹窗的类型和按钮动效样式，可选
                .setDialogParams(new BDDialogParams.Builder()
                        .setDlDialogType(BDDialogParams.TYPE_BOTTOM_POPUP)
                        .setDlDialogAnimStyle(BDDialogParams.ANIM_STYLE_NONE)
                        .build())
                .build(getApplication());
        bdAdConfig.init();

        // 设置SDK可以使用的权限，包含：设备信息、定位、存储、APP LIST
        // 注意：建议授权SDK读取设备信息，SDK会在应用获得系统权限后自行获取IMEI等设备信息
        // 授权SDK获取设备信息会有助于提升ECPM
        MobadsPermissionSettings.setPermissionReadDeviceID(true);
        MobadsPermissionSettings.setPermissionLocation(true);
        MobadsPermissionSettings.setPermissionStorage(true);
        MobadsPermissionSettings.setPermissionAppList(true);


        //穿山甲广告
        TTAdSdk.init(getApplication(),
                new TTAdConfig.Builder()
                        .appId("5224717")
                        .useTextureView(true) //默认使用SurfaceView播放视频广告,当有SurfaceView冲突的场景，可以使用TextureView
                        .appName("小贝乐园")
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)//落地页主题
                        .allowShowNotify(true) //是否允许sdk展示通知栏提示,若设置为false则会导致通知栏不显示下载进度
                        .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                        .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI) //允许直接下载的网络状态集合,没有设置的网络下点击下载apk会有二次确认弹窗，弹窗中会披露应用信息
                        .supportMultiProcess(false) //是否支持多进程，true支持
                        .asyncInit(true) //是否异步初始化sdk,设置为true可以减少SDK初始化耗时。3450版本开始废弃~~
                        //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okhttp3版本的样例，其余请自行开发或者咨询工作人员。
                        .build(), new TTAdSdk.InitCallback() {
                    @Override
                    public void success() {
                        Log.d(TAG, "success: 穿山甲初始化成功");
                    }

                    @Override
                    public void fail(int i, String s) {
                        Log.d(TAG, "success: 穿山甲初始化失败"+s);
                    }
                });
    }

    /**
     * 阿里云性能分析
     */
    public static void initHa() {
        AliHaConfig config = new AliHaConfig();
        config.appKey = "333510424"; //配置项
        config.appVersion = BuildConfig.VERSION_NAME; //配置项
        config.appSecret = "ac4d390a63d645b79340e3b89efd8d3a"; //配置项
        config.channel = "mqc_test"; //配置项
        config.userNick = null; //配置项
        config.application = getApplication(); //配置项
        config.context = getApplication(); //配置项
        config.isAliyunos = false; //配置项
        config.rsaPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCx8PPK8UA6sxYfnfgIJjixl55IHxlAGkHKUxhsbd/VBmjG86vDVO9QLH8VRQgIPp/e/rjByFjwf60SALTozd3BS/xT3XLG1Oo8TuCsSrb9HiysfERGjmah54pDXl8O6oRiNhHYsh1qjR9JFaiRWGnW8NnLzHtIUGXahjf/JtmwyQIDAQAB"; //配置项
        AliHaAdapter.getInstance().addPlugin(Plugin.crashreporter);//崩溃
//        AliHaAdapter.getInstance().addPlugin(Plugin.apm);//性能
        AliHaAdapter.getInstance().openDebug(true);
        AliHaAdapter.getInstance().start(config);

        Log.d(TAG, "initHa: 启动阿里云性能分析");
//        TLogService.updateLogLevel(TLogLevel.XXXXXX);//配置项：控制台可拉取的日志级别
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
                            .url("https://redis.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=xiaobeifrontorback&m=rediscache")
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
            public void getContactAllInfoProvider(IContactCardInfoProvider.IContactCardInfoCallback contactInfoCallback) {
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
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("RONGCLOUD", "--onSuccess" + userid);

                }

                @Override
                public void onError(RongIMClient.ConnectionErrorCode connectionErrorCode) {

                }

                @Override
                public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {

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
