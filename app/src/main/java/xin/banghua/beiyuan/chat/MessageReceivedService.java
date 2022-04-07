package xin.banghua.beiyuan.chat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Map;
import java.util.Timer;

import io.agora.chatroom.manager.RtmManager;
import io.agora.rtm.LocalInvitation;
import io.agora.rtm.RemoteInvitation;
import io.agora.rtm.RtmCallEventListener;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;


public class MessageReceivedService extends Service{

    public static String mPeerId = "";//当前聊天对象id
    public static boolean ifMessageSoundOn = true;//是否打开消息提示音，默认打开。会话界面需要判断，是否接收消息是当前聊天对象，是则关闭，不是则打开，如果是且黑屏也打开。
    public static boolean ifScreenOn = true;//当前是否锁屏

    private static final String TAG = "MessageReceivedService";


    //角标
    public static final String BADGE_INTERACT_ALL = "badge_message_all";
    public static final String BADGE_INTERACT_LIKE = "badge_message_like";
    public static final String BADGE_INTERACT_AT = "badge_message_at";
    public static final String BADGE_INTERACT_COMMENT = "badge_message_comment";
    public static final String BADGE_INTERACT_GIFT = "badge_message_gift";
    public static final String BADGE_INTERACT_REWARD = "badge_message_reward";
    public static final String BADGE_INTERACT_XUNYUAN_LIKE = "badge_message_xunyuan_like";

    //消息类型
    public static final String MESSAGE_TYPE_TEXT = "text";//文本消息
    public static final String MESSAGE_TYPE_POST = "post";//文本消息,post的cover是网络图片地址，不需要保存
    public static final String MESSAGE_TYPE_IMAGE = "image";//图片消息,发送方的image保存的是本地地址，接收方的image保存的是缩略图，需要保存mediaId,用于接收方点击时下载原图
    public static final String MESSAGE_TYPE_LOCATION = "location";//图片消息,cover是截图，需要像图片一样保存
    public static final String MESSAGE_TYPE_AUDIO = "audio";//文件消息
    public static final String MESSAGE_TYPE_VIDEO = "video";//文件消息,封面就用视频第一帧
    public static final String MESSAGE_TYPE_FILE = "file";//文件消息,暂时不做
    public static final String MESSAGE_TYPE_TIME = "time";//时间消息，不用于发送，只用于本地数据库储存
    public static final String MESSAGE_TYPE_PROMPT = "prompt";//提示消息，不用于发送，只用于本地数据库储存
    public static final String MESSAGE_TYPE_CALL_VOICE = "call_voice";//语音呼叫
    public static final String MESSAGE_TYPE_CALL_VIDEO = "call_video";//视频呼叫
    public static final String MESSAGE_TYPE_CALL_GAME = "call_game";//游戏呼叫
    public static final String MESSAGE_TYPE_CALL_FIVE_CHESS = "call_five_chess";//游戏呼叫
    public static final String MESSAGE_TYPE_CALL_DRAW_GUESS = "call_draw_guess";//游戏呼叫
    public static final String MESSAGE_TYPE_CALL_KTV = "call_ktv";//ktv呼叫
    public static final String MESSAGE_TYPE_MATCH_TEXT = "match_text";//匹配文字
    public static final String MESSAGE_TYPE_MATCH_VOICE = "match_voice";//匹配语音呼叫
    public static final String MESSAGE_TYPE_MATCH_VIDEO = "match_video";//匹配视频呼叫
    public static final String MESSAGE_TYPE_MATCH_KTV = "match_ktv";//匹配ktv
    public static final String MESSAGE_TYPE_MATCH_GAME = "match_game";//匹配游戏
    public static final String MESSAGE_TYPE_MATCH_FIVE_CHESS = "match_five_chess";//匹配游戏
    public static final String MESSAGE_TYPE_MATCH_DRAW_GUESS = "match_draw_guess";//匹配游戏

    public MessageReceivedService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private RtmManager mRtmManager;
    private RtmClientListener mClientListener;
    private RtmCallEventListener mRtmCallEventListener;

    Context mContext;

    Timer timer = new Timer();

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getBaseContext();
        Log.d(TAG, "onCreate: 启动接收消息服务");

        mRtmManager = RtmManager.instance(getApplicationContext());
        mClientListener = new MyRtmClientListenerService();
        mRtmManager.rtmClientListenerList.add(mClientListener);
        mRtmCallEventListener = new MyRtmCallEventListenerService();
        mRtmManager.rtmCallEventListenerList.add(mRtmCallEventListener);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    class MyRtmClientListenerService implements RtmClientListener {
        private static final String TAG = "MyRtmService1";
        @Override
        public void onConnectionStateChanged(int i, int i1) {
            Log.d(TAG, "onMessageReceived: 消息连接状态");
        }


        @Override
        public void onMessageReceived(RtmMessage rtmMessage, String s) {
            if (!rtmMessage.getText().contains("type")){
                Log.d(TAG, "onMessageReceived: 收到空消息"+s);
                return;
            }
//            String messageJson = rtmMessage.getText();
//            Log.d(TAG, "onMessageReceived: 收到空消息"+messageJson);
//            if (messageJson.contains("match_voice")){
//                Intent intent = new Intent(mContext, VoiceChatViewActivity.class);
//                intent.putExtra("channel", "match"+Common.userInfoList.getId());
//                intent.putExtra("targetId", Common.userInfoList.getId());
//                intent.putExtra("targetPortrait", Common.userInfoList.getPortrait());
//                intent.putExtra("myRole", "remote");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//                mContext.startActivity(intent);
//            }
//            if (messageJson.contains("match_video")){
//                Intent intent = new Intent(mContext, VideoChatViewActivity.class);
//                intent.putExtra("channel", "match"+Common.userInfoList.getId());
//                intent.putExtra("targetId", Common.userInfoList.getId());
//                intent.putExtra("targetPortrait", Common.userInfoList.getPortrait());
//                intent.putExtra("myRole", "remote");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//                mContext.startActivity(intent);
//            }
        }


        @Override
        public void onImageMessageReceivedFromPeer(RtmImageMessage rtmImageMessage, String s) {
            Log.d(TAG, "onMessageReceived: 接收图片消息");

        }


        @Override
        public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {
            Log.d(TAG, "onMessageReceived: 接收文件消息");
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

        }

        @Override
        public void onLocalInvitationCanceled(LocalInvitation localInvitation) {
            Log.d(TAG, "onLocalInvitationCanceled: 返回给主叫：呼叫邀请已被取消。");

        }

        @Override
        public void onLocalInvitationFailure(LocalInvitation localInvitation, int i) {

        }

        @Override
        public void onRemoteInvitationReceived(RemoteInvitation remoteInvitation) {

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

        }

        @Override
        public void onRemoteInvitationFailure(RemoteInvitation remoteInvitation, int i) {

        }
    }
}