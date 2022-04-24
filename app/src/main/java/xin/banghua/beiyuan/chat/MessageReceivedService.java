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


    private static final String TAG = "MessageReceivedService";


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