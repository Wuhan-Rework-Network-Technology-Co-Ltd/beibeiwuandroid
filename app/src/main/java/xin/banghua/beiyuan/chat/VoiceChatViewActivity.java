package xin.banghua.beiyuan.chat;


import static xin.banghua.beiyuan.chat.VoiceFloatService.FROM_VOICE_SERVICE;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.PermissionChecker;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import io.agora.chatroom.UserInfoList;
import io.agora.chatroom.gift.GiftDialog;
import io.agora.chatroom.manager.RtmManager;
import io.agora.chatroom.model.Member;
import io.agora.chatroom.util.CheckPermission;
import io.agora.chatroom.util.ReportDialog;
import io.agora.chatroom.util.SoundUtil;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtm.LocalInvitation;
import io.agora.rtm.RemoteInvitation;
import io.agora.rtm.RtmCallEventListener;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import xin.banghua.beiyuan.Adapter.FollowList;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.chat.faceunity.RtcBasedActivity;
import xin.banghua.beiyuan.utils.ThreadUtils;


/**
 * 呼叫页面不再出现呼叫站内信提示
 */
public class VoiceChatViewActivity extends RtcBasedActivity {
    private static final String TAG = "VoiceChatViewActivity";

    private static final String LOG_TAG = VoiceChatViewActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    public static RtcEngine mRtcEngine; // Tutorial Step 1

    GiftDialog giftDialog;
    ImageView btn_gift,btn_follow,btn_report_violation;
    public static ConstraintLayout container;
    private void initGiftAndFollow(){
        container = findViewById(R.id.container);
        btn_gift = findViewById(R.id.btn_gift);
        btn_gift.setVisibility(View.GONE);
        btn_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GiftDialog.getInstance(VoiceChatViewActivity.this,false, container,null)
                        .initShow(new Member(userInfoList.getId(), userInfoList.getNickname(),
                                userInfoList.getPortrait(),userInfoList.getGender(),userInfoList.getProperty(),
                                userInfoList.getPortraitframe(),userInfoList.getVeilcel()));
            }
        });



        btn_follow = findViewById(R.id.btn_follow);
        btn_follow.setVisibility(View.GONE);
        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VoiceChatViewActivity.this,"关注成功",Toast.LENGTH_SHORT).show();
                btn_follow.setVisibility(View.GONE);
                xin.banghua.beiyuan.utils.OkHttpInstance.follow(userInfoList.getId(), responseString -> {
                    Common.followList.add(new FollowList(userInfoList.getId()));
                });
            }
        });

        btn_report_violation = findViewById(R.id.btn_report_violation);
        btn_report_violation.setVisibility(View.GONE);
        btn_report_violation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportDialog reportDialog = new ReportDialog(VoiceChatViewActivity.this);
                reportDialog.show(ReportDialog.REPORT_TYPE_2,userInfoList.getId());
            }
        });
    }

    private RtmManager mRtmManager;
    private RtmClientListener mClientListener;
    private RtmCallEventListener mRtmCallEventListener;
    public static String channel,targetId,targetPortrait;
    public static String myRole = "local";
    public static String from = "this";
    public static Long during_long = SystemClock.elapsedRealtime();

    CircleImageView authportrait;
    TextView prompt_tv;
    Chronometer chronometer;
    ImageView btn_mute,btn_speaker;

    @BindView(R.id.zoom_icon)
    ImageView zoom_icon;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                zoom();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    Boolean isPermission = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_chat_view);

        ButterKnife.bind(this);

        CheckPermission.verifyPermissionCameraAndAudioAndStorage(VoiceChatViewActivity.this);

        Toast.makeText(VoiceChatViewActivity.this,"开始匹配，请耐心等待！",Toast.LENGTH_SHORT).show();

        //关闭服务和dialog

//        CheckPermission.verifyPermissionCameraAndAudioAndStorageForCall(1201, VoiceChatViewActivity.this, new CommonCallBackInterfaceWithParam() {
//            @Override
//            public void callBack(String param) {
//                if (param.equals("granted")){
//                    initChat();
//                }
//            }
//        });
        initChat();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: 授权回调"+requestCode);
        switch (requestCode) {
            case 1201:
                Boolean haveDenied = false;
                for (int result : grantResults){
                    if (result== PermissionChecker.PERMISSION_DENIED){
                        haveDenied = true;
                    }
                }
                if (haveDenied){
                    Toast.makeText(VoiceChatViewActivity.this,"拒绝了功能所需的权限",Toast.LENGTH_LONG).show();
                }else {
                    initChat();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    public void initChat(){
        isPermission = true;

        if (Common.isServiceExisted(VoiceChatViewActivity.this,"xin.banghua.beiyuan.custom_ui.match_dialog.MatchViewService")) {
            Log.d("触发", "service已启动");
            //MyActivityManager.getInstance().getCurrentActivity().stopService(MatchViewDialog.matchViewIntent);
        }else {
            Log.d("触发", "service未启动");
        }


        Common.instanceVoiceFloatService(this);
        //参数
        channel = getIntent().getStringExtra("channel");
//        targetId = getIntent().getStringExtra("targetId");
//        targetPortrait = getIntent().getStringExtra("targetPortrait");
        if (getIntent().getStringExtra("myRole")!=null)
            myRole = getIntent().getStringExtra("myRole");
        if (getIntent().getStringExtra("from")!=null)
            from = getIntent().getStringExtra("from");

        //主叫呼叫后直接进入这个页面，在这个页面监听被叫是拒绝还是同意，被叫拒绝则挂断
        mRtmManager = RtmManager.instance(getApplicationContext());
        mClientListener = new GiftRtmClientListenerService();
        mRtmManager.rtmClientListenerList.add(mClientListener);
        mRtmCallEventListener = new MyRtmCallEventListener();
        mRtmManager.rtmCallEventListenerList.add(mRtmCallEventListener);


        authportrait = findViewById(R.id.authportrait);
        prompt_tv = findViewById(R.id.prompt_tv);
        chronometer = findViewById(R.id.chronometer);
        btn_mute = findViewById(R.id.btn_mute);
        btn_speaker = findViewById(R.id.btn_speaker);


        initAgoraEngineAndJoinChannel();
//        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
//            initAgoraEngineAndJoinChannel();
//        }

        prompt_tv.setVisibility(View.VISIBLE);
        chronometer.setVisibility(View.GONE);

        if (myRole.equals("remote")||from.equals(FROM_VOICE_SERVICE)){
            if (!from.equals(FROM_VOICE_SERVICE)){
                mRtcEngine.leaveChannel();
                during_long = SystemClock.elapsedRealtime();
            }else {

            }

            Log.d(TAG, "onCreate: 计时器开始被叫");
            prompt_tv.setVisibility(View.GONE);
            chronometer.setVisibility(View.VISIBLE);
            chronometer.setBase(during_long);
            chronometer.start();
            btn_mute.setVisibility(View.VISIBLE);
            btn_speaker.setVisibility(View.VISIBLE);
        }




        initGiftAndFollow();


        zoom_icon.setOnClickListener(view -> {
            zoom();
        });



        closeService();
    }


    @Override
    public void finish() {
        super.finish();

        Log.d(TAG, "finish: 语音通话结束");

    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // Tutorial Step 1
        prompt_tv.postDelayed(new Runnable() {
            @Override
            public void run() {
                joinChannel();           // Tutorial Step 2
            }
        },500);

    }

    public final void showLongToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isPermission){
            return;
        }

        closeService();


        if (!TextUtils.isEmpty(targetId)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: 1First remote video decoded, uid:"+targetId);
                    //加个缓冲
                    prompt_tv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(VoiceChatViewActivity.this,"匹配成功！",Toast.LENGTH_SHORT).show();
                            SoundUtil.getInstance(VoiceChatViewActivity.this).playSoundLoop(SoundUtil.SOUND_LOVE_BELL);
                            OkHttpInstance.getUserAttributes(targetId, new OkHttpResponseCallBack() {
                                @Override
                                public void getResponseString(String responseString) {
                                    userInfoList = JSON.parseObject(responseString, UserInfoList.class);
                                    targetId = userInfoList.getId();
                                    targetPortrait = userInfoList.getPortrait();
                                    Glide.with(VoiceChatViewActivity.this)
                                            .load(Common.getOssResourceUrl(userInfoList.getPortrait()))
                                            .into(authportrait);
                                    btn_gift.setVisibility(View.VISIBLE);
                                    btn_follow.setVisibility(View.VISIBLE);
                                    btn_report_violation.setVisibility(View.VISIBLE);
                                    if (Common.followList.contains(targetId)){
                                        btn_follow.setVisibility(View.GONE);
                                    }else {
                                        btn_follow.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            for (int i = 0; i < Common.followList.size(); i++){
                                if (Common.followList.get(i).getUserId().equals(targetId)){
                                    btn_follow.setVisibility(View.GONE);
                                }
                            }
                        }
                    },1000);
                }
            });
        }
    }

    /**
     * 关闭服务，在打开时或通话结束时
     */
    public void closeService(){
        if (Common.isServiceExisted(this,"xin.banghua.beiyuan.chat.VoiceFloatService")) {
            Log.d("触发", "service已启动");

            stopService(Common.getInstanceVoiceFloatService());
        }else {
            Log.d("触发", "service未启动");
        }
    }

    // Tutorial Step 7
    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorGray2), PorterDuff.Mode.MULTIPLY);
        }

        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    // Tutorial Step 5
    public void onSwitchSpeakerphoneClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorGray2), PorterDuff.Mode.MULTIPLY);
        }

        // Enables/Disables the audio playback route to the speakerphone.
        //
        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
        int result = mRtcEngine.setEnableSpeakerphone(view.isSelected());
        Log.d(TAG, "onSwitchSpeakerphoneClicked: 听筒切换"+result+"|"+view.isSelected());
    }

    // Tutorial Step 3

    /**
     * 挂断，取消呼叫，评星
     * @param view
     */
    public void onEndCallClicked(View view) {
        Log.d(TAG, "onEndCallClicked: 触发挂断1");
        if (RtmManager.instance(getApplicationContext()).getRtmClient().getRtmCallManager() != null) {
//            RtmManager.instance(getApplicationContext()).getRtmClient().getRtmCallManager().cancelLocalInvitation(MatchChatActivity.invitation, new ResultCallback<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Log.d(TAG, "onSuccess: 取消呼叫成功");
//
//                }
//
//                @Override
//                public void onFailure(ErrorInfo errorInfo) {
//                    Log.d(TAG, "onFailure: 取消呼叫失败"+errorInfo.toString());
//                }
//            });
        }

        OkHttpInstance.removeXiaobeiMatch("语音");

        leaveChannel();

        mRtmManager.rtmClientListenerList.remove(mClientListener);
        mRtmManager.rtmCallEventListenerList.remove(mRtmCallEventListener);

        finish();
    }

    /**
     * 关闭通话，用于会员判断
     */
    public void closeChat(){
        leaveChannel();

        mRtmManager.rtmClientListenerList.remove(mClientListener);
        mRtmManager.rtmCallEventListenerList.remove(mRtmCallEventListener);

        finish();
    }

    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = rtcEngine();

            mRtcEngine.disableVideo();
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void joinChannel() {
        Log.d(TAG, "joinChannel: 加入频道"+channel);
        OkHttpInstance.getRTCToken(channel,new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                String accessToken = responseString;
                Log.d(TAG, "getResponseString: 获取rtc token"+accessToken);
                if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "#YOUR ACCESS TOKEN#")) {
                    accessToken = null; // default, no token
                }

                mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);

                // Allows a user to join a channel.
                int result = mRtcEngine.joinChannel(accessToken, channel, "Extra Optional Data", Integer.parseInt(Common.myID)); // if you do not specify the uid, we will generate the uid for you
                Log.d(TAG, "joinChannel: 加入频道结果"+result);
            }
        });
    }

    // Tutorial Step 3
    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    // Tutorial Step 4
    private void onRemoteUserLeft(int uid, int reason) {
        //showLongToast(String.format(Locale.US, "user %d left %d", (uid & 0xFFFFFFFFL), reason));
        View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
        tipMsg.setVisibility(View.VISIBLE);

        leaveChannel();
    }

    // Tutorial Step 6
    private void onRemoteUserVoiceMuted(int uid, boolean muted) {
        //showLongToast(String.format(Locale.US, "user %d muted or unmuted %b", (uid & 0xFFFFFFFFL), muted));
    }


    public void zoom () {
        Log.d(TAG, "onEndCallClicked: 触发挂断2");

        Intent intent = Common.getInstanceVoiceFloatService();
        //hasBind = bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onEndCallClicked: 触发挂断3");
        if (requestCode == 33493 && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                } else {
                    //开启房间服务
                    Log.d(TAG, "服务33493触发");
                    //Intent intent = new Intent(this, ChatRoomService.class);
                    Intent intent = Common.getInstanceVoiceFloatService();
                    //hasBind = bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
                    startService(intent);

                    finish();
                }
            }
        }
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {

    }


    @Override
    public void onUserMuteAudio(final int uid, final boolean muted) { // Tutorial Step 6
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onRemoteUserVoiceMuted(uid, muted);
            }
        });
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onRemoteUserLeft(uid, reason);
                closeService();
                finish();
            }
        });
    }

    UserInfoList userInfoList;
    @Override
    public void onUserJoined(int uid, int elapsed) {
        Log.d(TAG, "onUserJoined: 语音匹配了"+Common.userInfoList.getId()+uid);
        if (!Common.userInfoList.getId().equals(uid+"")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: First remote video decoded, uid:"+uid);
                    //加个缓冲
                    prompt_tv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(VoiceChatViewActivity.this,"匹配成功！",Toast.LENGTH_SHORT).show();
                            SoundUtil.getInstance(VoiceChatViewActivity.this).playSoundLoop(SoundUtil.SOUND_LOVE_BELL);
                            OkHttpInstance.getUserAttributes(uid+"", new OkHttpResponseCallBack() {
                                @Override
                                public void getResponseString(String responseString) {
                                    userInfoList = JSON.parseObject(responseString, UserInfoList.class);
                                    Glide.with(VoiceChatViewActivity.this)
                                            .load(Common.getOssResourceUrl(userInfoList.getPortrait()))
                                            .into(authportrait);
                                    btn_gift.setVisibility(View.VISIBLE);
                                    btn_follow.setVisibility(View.VISIBLE);
                                    btn_report_violation.setVisibility(View.VISIBLE);
                                    if (Common.followList.contains(targetId)){
                                        btn_follow.setVisibility(View.GONE);
                                    }else {
                                        btn_follow.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            for (int i = 0; i < Common.followList.size(); i++){
                                if (Common.followList.get(i).getUserId().equals(uid)){
                                    btn_follow.setVisibility(View.GONE);
                                }
                            }
                        }
                    },1000);
                }
            });
        }

    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {

    }

    class GiftRtmClientListenerService implements RtmClientListener {

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

    class MyRtmCallEventListener implements RtmCallEventListener {

        @Override
        public void onLocalInvitationReceivedByPeer(LocalInvitation localInvitation) {
            Log.d(TAG, "onLocalInvitationReceivedByPeer: 返回给主叫：被叫已收到呼叫邀请。");
        }

        @Override
        public void onLocalInvitationAccepted(LocalInvitation localInvitation, String s) {
            Log.d(TAG, "onLocalInvitationAccepted: 返回给主叫：被叫已接受呼叫邀请。");
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onCreate: 计时器开始主叫");
                    prompt_tv.setVisibility(View.GONE);
                    chronometer.setVisibility(View.VISIBLE);
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    btn_mute.setVisibility(View.VISIBLE);
                    btn_speaker.setVisibility(View.VISIBLE);
                }
            },1000);
        }

        @Override
        public void onLocalInvitationRefused(LocalInvitation localInvitation, String s) {
            Log.d(TAG, "onLocalInvitationRefused: 返回给主叫：被叫已拒绝呼叫邀请。");
            Log.d(TAG, "onEndCallClicked: 触发挂断4");
            finish();
        }

        @Override
        public void onLocalInvitationCanceled(LocalInvitation localInvitation) {
            Log.d(TAG, "onLocalInvitationCanceled: 返回给主叫：呼叫邀请已被取消。");
            Log.d(TAG, "onEndCallClicked: 触发挂断5");
            finish();
        }

        @Override
        public void onLocalInvitationFailure(LocalInvitation localInvitation, int i) {
            Log.d(TAG, "onLocalInvitationFailure: 返回给主叫：呼叫邀请进程失败。");
            Log.d(TAG, "onEndCallClicked: 触发挂断6");
            finish();
        }

        @Override
        public void onRemoteInvitationReceived(RemoteInvitation remoteInvitation) {

        }

        @Override
        public void onRemoteInvitationAccepted(RemoteInvitation remoteInvitation) {

        }

        @Override
        public void onRemoteInvitationRefused(RemoteInvitation remoteInvitation) {

        }

        @Override
        public void onRemoteInvitationCanceled(RemoteInvitation remoteInvitation) {

        }

        @Override
        public void onRemoteInvitationFailure(RemoteInvitation remoteInvitation, int i) {

        }
    }
}
