package xin.banghua.beiyuan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.ha.adapter.AliHaAdapter;
import com.alibaba.ha.adapter.AliHaConfig;
import com.alibaba.ha.adapter.Plugin;
import com.baidu.mobads.sdk.api.MobadsPermissionSettings;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.faceunity.nama.FURenderer;
import com.faceunity.nama.post.DemoApplication;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadConnection;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.capture.video.camera.CameraVideoManager;
import io.agora.chatroom.ChatRoomApplication;
import io.agora.chatroom.manager.ChatRoomManager;
import io.agora.chatroom.manager.RtcManager;
import io.agora.chatroom.manager.RtmManager;
import io.agora.chatroom.model.Constant;
import io.agora.rtc.RtcEngine;
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
import xin.banghua.beiyuan.Signin.LoginSmsActivity;
import xin.banghua.beiyuan.Signin.OneKeyLogInActivityResult;
import xin.banghua.beiyuan.chat.MessageReceivedService;
import xin.banghua.beiyuan.chat.faceunity.PreprocessorFaceUnity;
import xin.banghua.beiyuan.chat.faceunity.RtcEngineEventHandler;
import xin.banghua.beiyuan.chat.faceunity.RtcEngineEventHandlerProxy;
import xin.banghua.beiyuan.match.MatchMessage;
import xin.banghua.beiyuan.match.MatchMessageProvider;
import xin.banghua.beiyuan.utils.NoEtagFileDownloaUrlConnection;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;
import xin.banghua.beiyuan.utils.ZipUtils;
import xin.banghua.onekeylogin.ModeSelectActivity;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.player.VideoViewConfig;
import xyz.doikki.videoplayer.player.VideoViewManager;

//import xin.banghua.beiyuan.ContactCardExtensionModule;
//import io.rong.contactcard.IContactCardInfoProvider;

/**
 * 作者：Administrator
 * 时间：2019/3/1
 * 功能：
 */
public class App extends Application implements Application.ActivityLifecycleCallbacks{
    private static final String TAG = "App";

    public static App mApplication;
    public static App getApplication() {
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


        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");
        //消息接收服务
        ChatRoomManager.startIntent = new Intent(getApplicationContext(), MessageReceivedService.class);

        mApplication = this;

        //每次新启动，设值为1
        SharedHelper shvalue = new SharedHelper(getApplicationContext());
        shvalue.saveOnestart(1);

        PushClass.initPushService(getApplicationContext());

        //美颜
        DemoApplication.registerFURender(this);

        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15_000) // set connection timeout.
                        .readTimeout(15_000) // set read timeout.
                ) {
                    @Override
                    public FileDownloadConnection create(String originUrl) throws IOException {
                        return new NoEtagFileDownloaUrlConnection(originUrl);
                    }
                })
                .commit();

        //下载美颜素材
        File file_fu = new File("data/data/xin.banghua.beiyuan/files/faceunity");
        if (!file_fu.exists()){
            //Common.downloadFU();
        }

        try {
            ZipUtils.UnZipFolder(Common.getAssetsCacheFile(this,"faceunity.zip"),"data/data/" + "xin.banghua.beiyuan" + "/files/faceunity");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //一键登录
        ModeSelectActivity.sign_in_intent = new Intent(mApplication, LoginSmsActivity.class);
        ModeSelectActivity.log_in_intent = new Intent(mApplication, OneKeyLogInActivityResult.class);


        //声网,用于文字登录和语音房间
        RtmManager.instance(getApplication()).init();
        RtcManager.instance(getApplication()).init();

        //声网RTC，用于语音通话和视频通话
        initRtcEngine();


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

        //RongExtensionManager.getInstance().addExtensionModule(new ContactCardExtensionModule());
        RongIM.registerMessageType(MatchMessage.class); //注册名片消息
        RongIM.getInstance().registerMessageTemplate(new MatchMessageProvider());

        List<String> phrasesList = new ArrayList<>();
        phrasesList.add("hi，可以交个朋友吗？");
        phrasesList.add("哈喽，在忙什么呢？");
        phrasesList.add("你好，在干嘛呢，一起聊天呀！");
        phrasesList.add("你喜欢可爱的小动物吗？");
        phrasesList.add("你觉得异性之间会有纯友谊吗？");
        RongExtensionManager.getInstance().addPhraseList(phrasesList);
//        initContactCard();
        //验证连接成功
//          connect("JeXL+71vahbPjzSTYBlf3Okw/3FJenp53iTgy0iFgV+zWO2xI0jlx8+r479bFjga59uiwpcN87KhrP49wK/ZpQ==");
        //为了测试 这是贝吉塔的token 贝吉塔跟super果实现单聊
//        SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
//        String token = shuserinfo.readRongtoken().get("Rongtoken");
//        connect(token);

//        closeAndroidPDialog();

//        RongIMClient.getInstance().setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageWrapperListener() {
//            /**
//             * 接收实时或者离线消息。
//             * 注意:
//             * 1. 针对接收离线消息时，服务端会将 200 条消息打成一个包发到客户端，客户端对这包数据进行解析。
//             * 2. hasPackage 标识是否还有剩余的消息包，left 标识这包消息解析完逐条抛送给 App 层后，剩余多少条。
//             * 如何判断离线消息收完：
//             * 1. hasPackage 和 left 都为 0；
//             * 2. hasPackage 为 0 标识当前正在接收最后一包（200条）消息，left 为 0 标识最后一包的最后一条消息也已接收完毕。
//             *
//             * @param message    接收到的消息对象
//             * @param left       每个数据包数据逐条上抛后，还剩余的条数
//             * @param hasPackage 是否在服务端还存在未下发的消息包
//             * @param offline    消息是否离线消息
//             * @return 是否处理消息。 如果 App 处理了此消息，返回 true; 否则返回 false 由 SDK 处理。
//             */
//            @Override
//            public boolean onReceived(final Message message, final int left, boolean hasPackage, boolean offline) {
//                Log.d(TAG, "onMessageClick: 1被封禁");
//                if (message.getObjectName().equals("RC:TxtMsg")){
//                    String[] textSplit = message.getContent().toString().split("'");
//                    if (textSplit[1].contains("你因为违规，已被封禁。") && message.getSenderUserId().equals("1")){
//                        SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sp.edit();
//                        editor.putString("userID", "");
//                        editor.commit();
//                        Intent intent = new Intent(App.getApplication(), OneKeyLoginActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//                        startActivity(intent);
//                        System.exit(0);
//                    }
//                }
//                return false;
//            }
//        });


        //获取融云token
        SharedHelper sh = new SharedHelper(getApplicationContext());
        String token  = sh.readRongtoken().get("Rongtoken");
        RongyunConnect rongyunConnect = new RongyunConnect();
        rongyunConnect.connect(token);





        RongIM.getInstance().enableNewComingMessageIcon(true);//显示新消息提醒
        RongIM.getInstance().enableUnreadMessageIcon(true);//显示未读消息数目


        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                //使用使用IjkPlayer解码
                //.setPlayerFactory(IjkPlayerFactory.create())
                //使用ExoPlayer解码
                .setPlayerFactory(ExoMediaPlayerFactory.create())
                //使用MediaPlayer解码
                //.setPlayerFactory(AndroidMediaPlayerFactory.create())
                .build());


        new Timer().schedule(new TimerTask() {
            public void run() {
                if (Common.userInfoList != null){
                    OkHttpInstance.updateOnline(new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {
                            Log.d(TAG, "getResponseString: 更新在线状态");
                        }
                    });
                }
            }
        }, 10000, 300000);




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



        //礼物缓存
        try {
            File cacheDir = new File(getApplication().getExternalCacheDir(), "http");
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 128);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    /**
     * 云码广告初始化
     */
    public static void adInit(){

        //云码
        // 用户签署隐私协议之后，调用云码sdk初始化
//        CloudCodeInitializer.init(getApplication());
//        // 如果没有在manifest中配置渠道ID和媒体ID，需要在此处传入
////        CloudCodeInitializer.init(this, "598394599954655233", "598800657214651394");
//        // 设置日志输出级别为debug
//        CloudCodeLog.setLevel(LogLevel.DEBUG);


        //百度广告
//        BDAdConfig bdAdConfig = new BDAdConfig.Builder()
//                // 1、设置app名称，可选
//                .setAppName("小贝乐园")
//                // 2、应用在mssp平台申请到的appsid，和包名一一对应，此处设置等同于在AndroidManifest.xml里面设置
//                .setAppsid("f678b1ce")
//                // 3、设置下载弹窗的类型和按钮动效样式，可选
//                .setDialogParams(new BDDialogParams.Builder()
//                        .setDlDialogType(BDDialogParams.TYPE_BOTTOM_POPUP)
//                        .setDlDialogAnimStyle(BDDialogParams.ANIM_STYLE_NONE)
//                        .build())
//                .build(getApplication());
//        bdAdConfig.init();

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
                activity.getWindow().setStatusBarColor(getResources().getColor(R.color.text_color_4,null));
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            @Override
            public void onActivityStarted(Activity activity) {
                if (count == 0) {
                    Log.v("vergo", "**********切到前台**********");
                    setFrontOrBack("1");

                    if (Common.userInfoList == null && !(new SharedHelper(getApplicationContext()).readUserInfo().get("userID").equals(""))){
                        Intent intent = new Intent(App.getApplication(),LaunchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity(intent);
                    }else {
                        ChatRoomManager.instance(getApplication()).joinChannel("0");
                    }
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
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("myid", userInfo.get("userID"))
                            .add("frontorback", frontOrBack)
                            .add("phonebrand", PushClass.phoneBrand)
                            .add("pushregid",PushClass.pushRegID)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://redis.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=xiaobeifrontorback&m=rediscache")
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
            @SuppressLint("SoonBlockedPrivateApi") Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
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







    private CameraVideoManager mVideoManager;
    private RtcEngine mRtcEngine;
    private RtcEngineEventHandlerProxy mRtcEventHandler;

    private void initRtcEngine() {
        String appId = "957ac9a2ac63427686d2144e75caa972";
        if (TextUtils.isEmpty(appId)) {
            throw new RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/");
        }

        mRtcEventHandler = new RtcEngineEventHandlerProxy();
        try {
            mRtcEngine = RtcEngine.create(this, appId, mRtcEventHandler);
            mRtcEngine.enableVideo();
            mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        } catch (Exception e) {
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    public void initVideoCaptureAsync() {
        Context application = getApplicationContext();
        FURenderer.setup(application);
        mVideoManager = new CameraVideoManager(application,
                new PreprocessorFaceUnity(application));
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public void addRtcHandler(RtcEngineEventHandler handler) {
        mRtcEventHandler.addEventHandler(handler);
    }

    public void removeRtcHandler(RtcEngineEventHandler handler) {
        mRtcEventHandler.removeEventHandler(handler);
    }

    public CameraVideoManager videoManager() {
        return mVideoManager;
    }
}
