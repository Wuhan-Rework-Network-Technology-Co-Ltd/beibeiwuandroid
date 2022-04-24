package xin.banghua.beiyuan.chat;

import static xin.banghua.beiyuan.utils.ThreadUtils.runOnUiThread;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;
import java.util.Map;
import java.util.Timer;

import io.agora.chatroom.manager.RtmManager;
import io.agora.chatroom.util.SoundUtil;
import io.agora.rtm.LocalInvitation;
import io.agora.rtm.RemoteInvitation;
import io.agora.rtm.RtmCallEventListener;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import xin.banghua.beiyuan.Main3Activity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

/**
 * @author 几圈年轮
 * @date 2020/3/30.
 * description：前台服务
 */
public class MessageReceivedForegroundService extends Service {
    private static final String TAG = "MessageReceivedForegrou";


    /**
     * 交互和
     */
    public static int interaction_sum = 0;

    /**
     * 礼物和
     */
    public static int gift_sum = 0;

    /**
     * 消息和
     */
    public static int message_sum = 0;
    /**
     * 朋友和
     */
    public static int friend_sum = 0;
    /**
     * 动态和
     */
    public static int dynamic_sum = 0;

    /**
     * 标记服务是否启动
     */
    public static boolean serviceIsLive = false;

    /**
     * 唯一前台通知ID
     */
    private int NOTIFICATION_ID = 1;


    public static Notification notification;

    Context mContext;

    private RtmManager mRtmManager;
    private RtmClientListener mClientListener;
    private RtmCallEventListener mRtmCallEventListener;


    private RtmChannelListener rtmChannelListener;
    SharedHelper shuserinfo;

    Timer timer;

    int x = 0;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");

        mContext = this;

        shuserinfo = new SharedHelper(mContext);

        mRtmManager = RtmManager.instance(getApplicationContext());
        mClientListener = new MyRtmClientListenerService();
        mRtmManager.rtmClientListenerList.add(mClientListener);
        mRtmCallEventListener = new MyRtmCallEventListenerService();
        mRtmManager.rtmCallEventListenerList.add(mRtmCallEventListener);
        // 获取服务通知
        createForegroundNotificationCus();

        rtmChannelListener = new RtmChannelListener() {
            @Override
            public void onMemberCountUpdated(int i) {
                Log.d(TAG, "onMessageReceived: 技能频道新人加入"+i);
            }

            @Override
            public void onAttributesUpdated(List<RtmChannelAttribute> list) {

            }

            @Override
            public void onMessageReceived(RtmMessage rtmMessage, RtmChannelMember rtmChannelMember) {
                Log.d(TAG, "onMessageReceived: 收到技能频道消息"+rtmMessage.getText());

            }

            @Override
            public void onImageMessageReceived(RtmImageMessage rtmImageMessage, RtmChannelMember rtmChannelMember) {

            }

            @Override
            public void onFileMessageReceived(RtmFileMessage rtmFileMessage, RtmChannelMember rtmChannelMember) {

            }

            @Override
            public void onMemberJoined(RtmChannelMember rtmChannelMember) {

            }

            @Override
            public void onMemberLeft(RtmChannelMember rtmChannelMember) {

            }
        };

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        // 标记服务启动
        serviceIsLive = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        try{
            if (mRtmManager!=null)
                mRtmManager.rtmClientListenerList.remove(mClientListener);
            // 标记服务关闭
            serviceIsLive = false;
            // 移除通知
            stopForeground(true);
        }catch (NullPointerException nullPointerException){

        }

        super.onDestroy();
    }



    RemoteViews contentViews;
    NotificationCompat.Builder mBuilder;
    PendingIntent pendingIntent;
    Intent intent;

    public static NotificationManager notificationManager;
    /**
     * 创建服务通知
     */
    private void createForegroundNotificationCus() {

        Log.d(TAG, "createForegroundNotificationCus: 新建前台通知");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 唯一的通知通道的id.
        String notificationChannelId = "notification_channel_id_01";

        // Android8.0以上的系统，新建消息通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //用户可见的通道名称
            String channelName = "常驻通知";
            //通道的重要程度
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, channelName, importance);
            notificationChannel.setDescription("全部消息通知");
            //LED灯
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            //震动
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        //自定义显示布局
        try {
            contentViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //通过控件的Id设置属性
        contentViews.setImageViewResource(R.id.imageNo, R.drawable.logo);
        contentViews.setTextViewText(R.id.interact_tv, "0");
        contentViews.setTextViewText(R.id.gift_tv, "0");
        contentViews.setTextViewText(R.id.chat_tv, "0");
        contentViews.setTextViewText(R.id.friend_tv, "0");
        contentViews.setTextViewText(R.id.dynamic_tv, "0");
        //点击通知栏跳转的activity
        intent = new Intent(this, Main3Activity.class);
        pendingIntent = PendingIntent.getActivity(this, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this, notificationChannelId);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle("My notification");
        mBuilder.setTicker("new message");
        //自动管理通知栏消息
        mBuilder.setAutoCancel(false);
        mBuilder.setContentIntent(pendingIntent);
        ///自定义布局
        mBuilder.setContent(contentViews);
        mBuilder.setTimeoutAfter(60000*60*24);
        //使用默认提示音
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
//        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(1, mBuilder.build());

        //创建通知并返回
        //将服务置于启动状态 ,NOTIFICATION_ID指的是创建的通知的ID
        notification = mBuilder.build();
        NOTIFICATION_ID += 1;
        startForeground(NOTIFICATION_ID, notification);

        RongIMClient.getInstance().setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageWrapperListener() {
            @Override
            public boolean onReceived(Message message, int i, boolean b, boolean b1) {
                if (message.getObjectName().equals("RC:TxtMsg")){
                    String[] textSplit = message.getContent().toString().split("'");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (textSplit[1].contains("回复了你的评论：")||textSplit[1].contains("评论了你的帖子：")){
                                Log.d(TAG, "run: 收到评论提醒");
                                //互动
                                interaction_sum += 1;
                                contentViews.setTextViewText(R.id.interact_tv, interaction_sum+"");
                            }else if (textSplit[1].contains("此为收到礼物后的消息提醒，无需回复")){
                                //礼物
                                gift_sum += 1;
                                contentViews.setTextViewText(R.id.gift_tv, gift_sum+"");
                            }else if (textSplit[1].contains("有人申请您为好友了")){
                                //好友
                                friend_sum += 1;
                                contentViews.setTextViewText(R.id.friend_tv, friend_sum+"");
                            }else if (textSplit[1].contains("您关注的用户")){
                                //动态
                                dynamic_sum += 1;
                                contentViews.setTextViewText(R.id.dynamic_tv, dynamic_sum+"");
                            }else{
                                //私信
                                message_sum += 1;
                                contentViews.setTextViewText(R.id.chat_tv, message_sum+"");
                            }

                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                                notificationManager.notify(NOTIFICATION_ID, notification);
                            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                            }
                            SoundUtil.getInstance(getApplicationContext()).playSoundLoop(SoundUtil.SOUND_ALL);
                        }
                    });

                }
                return false;
            }

            /**
             * 接收实时或者离线消息。
             * 注意:
             * 1. 针对接收离线消息时，服务端会将 200 条消息打成一个包发到客户端，客户端对这包数据进行解析。
             * 2. hasPackage 标识是否还有剩余的消息包，left 标识这包消息解析完逐条抛送给 App 层后，剩余多少条。
             * 如何判断离线消息收完：
             * 1. hasPackage 和 left 都为 0；
             * 2. hasPackage 为 0 标识当前正在接收最后一包（200条）消息，left 为 0 标识最后一包的最后一条消息也已接收完毕。
             *
             * @param message    接收到的消息对象
             * @param left       每个数据包数据逐条上抛后，还剩余的条数
             * @param hasPackage 是否在服务端还存在未下发的消息包
             * @param offline    消息是否离线消息
             * @return 是否处理消息。 如果 App 处理了此消息，返回 true; 否则返回 false 由 SDK 处理。
             */
        });
    }

    class MyRtmClientListenerService implements RtmClientListener {

        @Override
        public void onConnectionStateChanged(int i, int i1) {

        }


        @Override
        public void onMessageReceived(RtmMessage rtmMessage, String s) {

        }

        @Override
        public void onImageMessageReceivedFromPeer(RtmImageMessage rtmImageMessage, String s) {

        }

        @Override
        public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {

        }

        @Override
        public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

        }

        @Override
        public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

        }

        @Override
        public void onTokenExpired() {

        }

        @Override
        public void onPeersOnlineStatusChanged(Map<String, Integer> map) {

        }
    }


     /**
     * 将本应用置顶到最前端
     * 当本应用位于后台时，则将它切换到最前端
     *
     * @param context
     */
    public void setTopApp(Context context) {
        Log.d(TAG, "setTopApp: 跳转前台");
        //获取ActivityManager
        ActivityManager mAm = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获得当前运行的task
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList) {
            //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            if (rti.topActivity.getPackageName().equals(context.getPackageName())) {
                Log.i(TAG, "#######################front###########################");
                mAm.moveTaskToFront(rti.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
        //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
        Intent resultIntent = new Intent(context, Main3Activity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(resultIntent);
        Log.i(TAG, "#######################end###########################");
    }
    //这里只处理被叫的呼叫接收
    /**
     * 呼叫的dialog需要消失，所以放外部
     */
    class MyRtmCallEventListenerService implements RtmCallEventListener {

        @Override
        public void onLocalInvitationReceivedByPeer(LocalInvitation localInvitation) {
            Log.d(TAG, "onLocalInvitationReceivedByPeer: 返回给主叫：被叫已收到呼叫邀请。");
        }

        @Override
        public void onLocalInvitationAccepted(LocalInvitation localInvitation, String s) {
            Log.d(TAG, "onLocalInvitationAccepted: 返回给主叫：被叫已接受呼叫邀请。");
        }

        @Override
        public void onLocalInvitationRefused(LocalInvitation localInvitation, String s) {
            Log.d(TAG, "onLocalInvitationRefused: 返回给主叫：被叫已拒绝呼叫邀请。");

        }

        @Override
        public void onLocalInvitationCanceled(LocalInvitation localInvitation) {
            Log.d(TAG, "onLocalInvitationCanceled: 返回给主叫：呼叫邀请已被取消。");
        }

        @Override
        public void onLocalInvitationFailure(LocalInvitation localInvitation, int i) {
            Log.d(TAG, "onLocalInvitationFailure: 返回给主叫：呼叫邀请进程失败。");

        }

        @Override
        public void onRemoteInvitationReceived(RemoteInvitation remoteInvitation) {
            Log.d(TAG, "onRemoteInvitationReceived: 返回给被叫：收到一个呼叫邀请。");

            setTopApp(getApplicationContext());

        }

        @Override
        public void onRemoteInvitationAccepted(RemoteInvitation remoteInvitation) {
            Log.d(TAG, "onRemoteInvitationAccepted: 返回给被叫：接受呼叫邀请成功。");
        }

        @Override
        public void onRemoteInvitationRefused(RemoteInvitation remoteInvitation) {
            Log.d(TAG, "onRemoteInvitationRefused: 返回给被叫：拒绝呼叫邀请成功。");
        }

        @Override
        public void onRemoteInvitationCanceled(RemoteInvitation remoteInvitation) {
            Log.d(TAG, "onRemoteInvitationCanceled: 返回给被叫：主叫已取消呼叫邀请。");

        }

        @Override
        public void onRemoteInvitationFailure(RemoteInvitation remoteInvitation, int i) {
            Log.d(TAG, "onRemoteInvitationFailure: 返回给被叫：来自主叫的呼叫邀请进程失败。");

        }
    }
}