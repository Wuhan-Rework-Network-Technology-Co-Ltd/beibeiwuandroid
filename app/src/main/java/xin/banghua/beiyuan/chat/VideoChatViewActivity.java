package xin.banghua.beiyuan.chat;


import static xin.banghua.beiyuan.chat.VideoFloatService.FROM_VIDEO_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.PermissionChecker;

import com.alibaba.fastjson.JSON;
import com.faceunity.nama.FURenderer;
import com.faceunity.nama.post.base.BaseFaceUnityActivity;
import com.faceunity.nama.ui.FaceUnityView;
import com.faceunity.nama.ui.enums.StickerEnum;
import com.faceunity.nama.utils.CameraUtils;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.capture.video.camera.CameraVideoManager;
import io.agora.capture.video.camera.Constant;
import io.agora.capture.video.camera.VideoCapture;
import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import io.agora.chatroom.UserInfoList;
import io.agora.chatroom.gift.GiftDialog;
import io.agora.chatroom.manager.RtmManager;
import io.agora.chatroom.model.Member;
import io.agora.chatroom.util.CheckPermission;
import io.agora.chatroom.util.ReportDialog;
import io.agora.chatroom.util.SoundUtil;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.agora.rtm.LocalInvitation;
import io.agora.rtm.RemoteInvitation;
import io.agora.rtm.RtmCallEventListener;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import xin.banghua.beiyuan.Adapter.FollowList;
import xin.banghua.beiyuan.App;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.chat.faceunity.PreprocessorFaceUnity;
import xin.banghua.beiyuan.chat.faceunity.RtcBasedActivity;
import xin.banghua.beiyuan.chat.faceunity.RtcEngineEventHandler;
import xin.banghua.beiyuan.chat.faceunity.RtcVideoConsumer;
import xin.banghua.beiyuan.utils.ThreadUtils;


/**
 * 呼叫页面不再出现呼叫站内信提示
 */
public class VideoChatViewActivity extends RtcBasedActivity implements RtcEngineEventHandler,  SensorEventListener{
    private static final String TAG = VideoChatViewActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID = 22;

    // Permission WRITE_EXTERNAL_STORAGE is not mandatory
    // for Agora RTC SDK, just in case if you wanna save
    // logs to external sdcard.
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public RtcEngine mRtcEngine; // Tutorial Step 1
    private boolean mCallEnd;
    private boolean mMuted;

    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    public static VideoCanvas mLocalVideo;
    public VideoCanvas mRemoteVideo;

    private ImageView mCallBtn;
    private ImageView mMuteBtn;
    private ImageView mSwitchCameraBtn;


    //美颜
    private static final int CAPTURE_WIDTH = 1280;
    private static final int CAPTURE_HEIGHT = 720;
    private static final int CAPTURE_FRAME_RATE = 24;
    @BindView(R.id.btn_beautiful)
    ImageView btn_beautiful;

    @BindView(R.id.fu_view)
    FaceUnityView fu_view;

    //游戏
    private ImageView btn_rock_paper_scissors;

    public static Boolean isLeft = false;

    Boolean isFirstLoad = true;

    UserInfoList userInfoList;
    private void setupRemoteVideo(int uid) {
        Log.d(TAG, "setupRemoteVideo: 1开始接受用户推流"+uid);
        OkHttpInstance.getUserAttributes(uid+"", new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                userInfoList = JSON.parseObject(responseString,UserInfoList.class);
                targetId = userInfoList.getId();
                targetPortrait = userInfoList.getPortrait();
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

        ViewGroup parent = mRemoteContainer;
        if (parent.indexOfChild(localVideo) > -1) {
            parent = mLocalContainer;
        }

        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        if (mRemoteVideo != null) {
            Log.d(TAG, "setupRemoteVideo: 不空");
            return;
        }else {
            Log.d(TAG, "setupRemoteVideo: 空");
        }

        /*
          Creates the video renderer view.
          CreateRendererView returns the SurfaceView type. The operation and layout of the view
          are managed by the app, and the Agora SDK renders the view provided by the app.
          The video display view must be created using this method instead of directly
          calling SurfaceView.
         */
        SurfaceView view = rtcEngine().CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(parent == mLocalContainer);
        parent.addView(view);
        mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid);
        // Initializes the video view of a remote user.

        if (isFirstLoad){
            Log.d(TAG, "mRtcEngine 空");
            initializeEngine();
            int result = mRtcEngine.setupRemoteVideo(mRemoteVideo);

            Log.d(TAG, "setupRemoteVideo: 远端视频加载"+result);
        }else {
            Log.d(TAG, "mRtcEngine 不空");
        }
    }

    private void onRemoteUserLeft(int uid) {
        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
            //离开频道
            removeFromParentTextureView(localVideo);
            localVideo = null;
            removeFromParent(mRemoteVideo);
            mRemoteVideo = null;
        }
    }



    ImageView btn_gift,btn_follow,btn_report_violation;

    /**
     * 初始化礼物和关注
     */
    public static ConstraintLayout container;
    private void initGiftAndFollow(){
        container = findViewById(R.id.container);
        btn_gift = findViewById(R.id.btn_gift);
        btn_gift.setVisibility(View.GONE);
        btn_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GiftDialog.getInstance(VideoChatViewActivity.this,false, container,null)
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
                Toast.makeText(VideoChatViewActivity.this,"关注成功",Toast.LENGTH_SHORT).show();
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
                ReportDialog reportDialog = new ReportDialog(VideoChatViewActivity.this);
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
    public static Long during_long;

    Chronometer chronometer;
    TextView prompt_tv;


    @BindView(R.id.zoom_icon)
    ImageView zoom_icon;

    @Override
    protected void onPause() {
        super.onPause();

    }

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
        setContentView(R.layout.activity_video_chat_view);

        ButterKnife.bind(this);

        CheckPermission.verifyPermissionCameraAndAudioAndStorage(VideoChatViewActivity.this);

        App.getApplication().initVideoCaptureAsync();


        Toast.makeText(VideoChatViewActivity.this,"开始匹配，请耐心等待！",Toast.LENGTH_SHORT).show();

//        CheckPermission.verifyPermissionCameraAndAudioAndStorageForCall(1202, VideoChatViewActivity.this, new CommonCallBackInterfaceWithParam() {
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
            case 1202:
                Boolean haveDenied = false;
                for (int result : grantResults){
                    if (result== PermissionChecker.PERMISSION_DENIED){
                        haveDenied = true;
                    }
                }
                if (haveDenied){
                    Toast.makeText(VideoChatViewActivity.this,"拒绝了功能所需的权限",Toast.LENGTH_LONG).show();
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

        if (Common.isServiceExisted(VideoChatViewActivity.this,"xin.banghua.beiyuan.custom_ui.match_dialog.MatchViewService")) {
            Log.d("触发", "service已启动");
            //VideoChatViewActivity.this.stopService(MatchViewDialog.matchViewIntent);
        }else {
            Log.d("触发", "service未启动");
        }


        during_long = SystemClock.elapsedRealtime();

        isLeft = false;

        FURenderer.setup(VideoChatViewActivity.this);
        mVideoManager = application().videoManager();


        Common.instanceVideoFloatService(this);

        //参数
        channel = getIntent().getStringExtra("channel");

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

        initUI();


        initEngineAndJoinChannel();


        if (myRole.equals("remote")||from.equals(FROM_VIDEO_SERVICE)){
            if (!from.equals(FROM_VIDEO_SERVICE)){
                if (mRtcEngine!=null)
                    mRtcEngine.leaveChannel();

                during_long = SystemClock.elapsedRealtime();
            }else {
                prompt_tv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //setupRemoteVideo(Integer.parseInt(targetId));
                    }
                },1000);
            }

            chronometer.setVisibility(View.VISIBLE);
            chronometer.setBase(during_long);
            chronometer.start();

            prompt_tv.setVisibility(View.GONE);
        }



        initGiftAndFollow();


        if (!SharedHelper.getInstance(this).readPrompt("sticker_description")) {
            SharedHelper.getInstance(this).savePrompt("sticker_description",true);
            stickerDescriptionShow();
        }




        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (Common.getChronometerSeconds(chronometer)==300) {

                }
//                if (Common.getChronometerSeconds(chronometer)==180) {
//                    fu_view.getmStickerModule().selectSticker(StickerEnum.getStickers().get(0));
//                }
            }
        });



        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        mVideoManager.startCapture();
        mFURenderer.queueEvent(new Runnable() {
            @Override
            public void run() {
                mFURenderer.onSurfaceCreated();
            }
        });


        closeService();

    }

    private void initUI() {
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);

        mCallBtn = findViewById(R.id.btn_call);
        mMuteBtn = findViewById(R.id.btn_mute);
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera);

        chronometer = findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();



        prompt_tv = findViewById(R.id.prompt_tv);



        zoom_icon.setOnClickListener(view -> {
            try {
                zoom();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //美颜
        btn_beautiful.setVisibility(View.VISIBLE);
        btn_beautiful.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fu_view.getVisibility() == View.GONE) {
                    fu_view.setVisibility(View.VISIBLE);
                } else {
                    fu_view.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        prompt_tv.postDelayed(new Runnable() {
            @Override
            public void run() {
                joinChannel();         // Tutorial Step 2
            }
        },500);
    }

    private void initializeEngine() {
        try {
            mRtcEngine = rtcEngine();

            mRtcEngine.enableVideo();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.


//        beautiful_framelayout.initShow(mRtcEngine);
//        int resultInt = mRtcEngine.setBeautyEffectOptions(true, new BeautyOptions(LIGHTENING_CONTRAST_NORMAL, 0.3F, 0.3F, 0.3F));
//        Log.d(TAG, "getResponseString: 美颜返回值"+resultInt);

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
        mRtcEngine.setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
    }

    private void setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
//        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
//        view.setZOrderMediaOverlay(true);
//        mLocalContainer.addView(view);
//        // Initializes the local video view.
//        // RENDER_MODE_HIDDEN: Uniformly scale the video until it fills the visible boundaries. One dimension of the video may have clipped contents.
//        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
//        mRtcEngine.setupLocalVideo(mLocalVideo);


        initVideoModule();
        mRtcEngine.setVideoSource(new RtcVideoConsumer());


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    //美颜部分
    private SensorManager mSensorManager;
    private int mCameraFace = FURenderer.CAMERA_FACING_FRONT;

    private CameraVideoManager mVideoManager;
    private FURenderer mFURenderer;

    TextureView localVideo;
    private void initVideoModule() {
        mVideoManager.setCameraStateListener(new VideoCapture.VideoCaptureStateListener() {
            @Override
            public void onFirstCapturedFrame(int width, int height) {
                Log.i(TAG, "onFirstCapturedFrame: " + width + "x" + height);
            }

            @Override
            public void onCameraCaptureError(int error, String msg) {
                Log.i(TAG, "onCameraCaptureError: error:" + error + " " + msg);
                if (mVideoManager != null) {
                    // When there is a camera error, the capture should
                    // be stopped to reset the internal states.
                    mVideoManager.stopCapture();
                }
            }

            @Override
            public void onCameraClosed() {

            }
        });

        mFURenderer = ((PreprocessorFaceUnity) mVideoManager.getPreprocessor()).getFURenderer();
        fu_view.setModuleManager(mFURenderer);
        fu_view.getmStickerModule().setContext(this);


        //fu_view.onlyShowSticker(Common.myUserInfoList.getRole());


        fu_view.postDelayed(new Runnable() {
            @Override
            public void run() {
                fu_view.onlyShowSticker(2);
                if (Common.userInfoList.getGender().equals("男")){
                    fu_view.getmStickerModule().selectSticker(StickerEnum.getStickers().get(3));
                }else {
                    fu_view.getmStickerModule().selectSticker(StickerEnum.getStickers().get(4));
                }
            }
        },2000);


        mFURenderer.setOnTrackStatusChangedListener(new FURenderer.OnTrackStatusChangedListener() {
            @Override
            public void onTrackStatusChanged(int type, int status) {
                Log.d(TAG, "onTrackStatusChanged: "+(type == FURenderer.TRACK_TYPE_FACE ? R.string.toast_not_detect_face : R.string.toast_not_detect_face_or_body));
            }
        });

        mVideoManager.setPictureSize(CAPTURE_WIDTH, CAPTURE_HEIGHT);
        mVideoManager.setFrameRate(CAPTURE_FRAME_RATE);
        mVideoManager.setFacing(Constant.CAMERA_FACING_FRONT);
        mVideoManager.setLocalPreviewMirror(Constant.MIRROR_MODE_AUTO);

        localVideo = new TextureView(VideoChatViewActivity.this);
        mLocalContainer.addView(localVideo);
        mVideoManager.setLocalPreview(localVideo);

        // create screenshot to compare effect before and after using API
        /**
         * {@link io.agora.framework.PreprocessorFaceUnity#onPreProcessFrame}
         */
        findViewById(R.id.btn_switch_camera).setOnLongClickListener(view -> {
            PreprocessorFaceUnity.needCapture = true;
            return true;
        });
    }


    private void joinChannel() {
        Log.d(TAG, "joinChannel:我加入频道");
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.

        OkHttpInstance.getRTCToken(channel,new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                String accessToken = responseString;
                if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "#YOUR ACCESS TOKEN#")) {
                    accessToken = null; // default, no token
                }
                // Sets the channel profile of the Agora RtcEngine.
                // CHANNEL_PROFILE_COMMUNICATION(0): (Default) The Communication profile. Use this profile in one-on-one calls or group calls, where all users can talk freely.
                // CHANNEL_PROFILE_LIVE_BROADCASTING(1): The Live-Broadcast profile. Users in a live-broadcast channel have a role as either broadcaster or audience. A broadcaster can both send and receive streams; an audience can only receive streams.
//                mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);

                // Allows a user to join a channel.
                mRtcEngine.joinChannel(accessToken, channel, "Extra Optional Data", Integer.parseInt(Common.myID)); // if you do not specify the uid, we will generate the uid for you
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    public void finish() {
        super.finish();

        BaseFaceUnityActivity.isLeaveVideoChat = true;

        if (mVideoManager!=null) {
            mVideoManager.stopCapture();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mVideoManager!=null)
            mVideoManager.stopCapture();

        localVideo = null;
        mVideoManager = null;

    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {

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
                    Toast.makeText(VideoChatViewActivity.this,"匹配成功！",Toast.LENGTH_SHORT).show();
                    SoundUtil.getInstance(VideoChatViewActivity.this).playSoundLoop(SoundUtil.SOUND_LOVE_BELL);
                    Log.d(TAG, "run: 2First remote video decoded, uid:"+targetId);
                    //加个缓冲
                    prompt_tv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setupRemoteVideo(Integer.parseInt(targetId));    // Tutorial Step 2
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
        if (Common.isServiceExisted(this,"xin.banghua.beiyuan.chat.VideoFloatService")) {
            Log.d("触发", "service已启动");

            stopService(Common.getInstanceVideoFloatService());
        }else {
            Log.d("触发", "service未启动");
        }
    }

    private void leaveChannel() {
        if (mRtcEngine!=null)
           mRtcEngine.leaveChannel();

        //直到离开频道才停止采集
        CountDownLatch countDownLatch = new CountDownLatch(1);
        mFURenderer.queueEvent(new Runnable() {
            @Override
            public void run() {
                mFURenderer.onSurfaceDestroyed();
                countDownLatch.countDown();
            }
        });
//        try {
//            countDownLatch.await();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        mVideoManager.stopCapture();
        mVideoManager.setCameraStateListener(null);
        mFURenderer.setOnTrackStatusChangedListener(null);
        mSensorManager.unregisterListener(this);
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
        mMuteBtn.setImageResource(res);
    }

    public void onSwitchCameraClicked(View view) {
        // Switches between front and rear cameras.
        //mRtcEngine.switchCamera();

        mVideoManager.switchCamera();
        mCameraFace = FURenderer.CAMERA_FACING_FRONT - mCameraFace;
        mFURenderer.onCameraChanged(mCameraFace, CameraUtils.getCameraOrientation(mCameraFace));
    }

    public void onCallClicked(View view) {
        if (mCallEnd) {
            Log.d(TAG, "onCallClicked: 开始呼叫");
            startCall();
            mCallEnd = false;
            mCallBtn.setImageResource(R.drawable.btn_endcall);
        } else {
            OkHttpInstance.removeXiaobeiMatch("视频");
            endCall();
            mCallEnd = true;
            mCallBtn.setImageResource(R.drawable.btn_startcall);
            Log.d(TAG, "onCallClicked: 1取消呼叫");
            leaveChannel();
            Log.d(TAG, "onCallClicked: 2取消呼叫");
            mRtmManager.rtmClientListenerList.remove(mClientListener);
            mRtmManager.rtmCallEventListenerList.remove(mRtmCallEventListener);

            //评星
            Log.d(TAG, "onCallClicked: 停止呼叫"+Common.getChronometerSeconds(chronometer));

            finish();
        }

        showButtons(!mCallEnd);


    }

    /**
     * 关闭通话，用于会员判断
     */
    public void closeChat(){
        leaveChannel();
        //RtcEngine.destroy();
        //mRtcEngine = null;

        mRtmManager.rtmClientListenerList.remove(mClientListener);
        mRtmManager.rtmCallEventListenerList.remove(mRtmCallEventListener);

        finish();
    }

    private void startCall() {
        setupLocalVideo();
        Log.d(TAG, "startCall: 开始呼叫");
        joinChannel();
    }

    /**
     * 取消呼叫，评星，离开频道
     */
    private void endCall() {
        //取消呼叫
        if (RtmManager.instance(getApplicationContext()).getRtmClient().getRtmCallManager() != null) {
//            RtmManager.instance(getApplicationContext()).getRtmClient().getRtmCallManager().cancelLocalInvitation(ChatFragment.invitation, new ResultCallback<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Log.d(TAG, "onSuccess: 取消呼叫成功");
//                    ChatFragment.invitation = null;
//                    MatchChatActivity.invitation = null;
//                }
//
//                @Override
//                public void onFailure(ErrorInfo errorInfo) {
//
//                }
//            });
        }

        //离开频道
        removeFromParentTextureView(localVideo);
        localVideo = null;
        removeFromParent(mRemoteVideo);
        mRemoteVideo = null;

    }

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mMuteBtn.setVisibility(visibility);
        mSwitchCameraBtn.setVisibility(visibility);
    }

    private ViewGroup removeFromParent(VideoCanvas canvas) {
        if (canvas != null) {
            ViewParent parent = canvas.view.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(canvas.view);
                return group;
            }
        }
        return null;
    }


    private ViewGroup removeFromParentTextureView(TextureView textureView) {
        if (textureView != null) {
            ViewParent parent = textureView.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(textureView);
                return group;
            }
        }
        return null;
    }

    private void switchView(VideoCanvas canvas) {
        ViewGroup parent = removeFromParent(canvas);
        if (parent == mLocalContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(false);
            }
            mRemoteContainer.addView(canvas.view);
        } else if (parent == mRemoteContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(true);
            }

            mLocalContainer.addView(canvas.view);

        }
    }

    private void switchViewTextureView(TextureView textureView) {
        ViewGroup parent = removeFromParentTextureView(textureView);
        localVideo = new TextureView(VideoChatViewActivity.this);
        if (parent == mLocalContainer) {
            mRemoteContainer.addView(localVideo);
        } else if (parent == mRemoteContainer) {
            mLocalContainer.addView(localVideo);
        }
        mVideoManager.setLocalPreview(localVideo);
    }




    public void onLocalContainerClick(View view) {
        switchViewTextureView(localVideo);
        switchView(mRemoteVideo);
    }

    public void zoom () {
        Intent intent = Common.getInstanceVideoFloatService();
        //hasBind = bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 33493 && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                } else {
                    //开启房间服务
                    Log.d(TAG, "服务33493触发");
                    //Intent intent = new Intent(this, ChatRoomService.class);
                    Intent intent = Common.getInstanceVideoFloatService();
                    //hasBind = bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
                    startService(intent);

                    finish();
                }
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (Math.abs(x) > 3 || Math.abs(y) > 3) {
                if (Math.abs(x) > Math.abs(y)) {
                    mFURenderer.onDeviceOrientationChanged(x > 0 ? 0 : 180);
                } else {
                    mFURenderer.onDeviceOrientationChanged(y > 0 ? 90 : 270);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {

    }

    @Override
    public void onUserJoined(final int uid, int elapsed) {
        Log.d(TAG, "run: 有人进入频道"+uid);
        if (!Common.userInfoList.getId().equals(uid+"")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VideoChatViewActivity.this,"匹配成功！",Toast.LENGTH_SHORT).show();
                    SoundUtil.getInstance(VideoChatViewActivity.this).playSoundLoop(SoundUtil.SOUND_LOVE_BELL);
                    Log.d(TAG, "run: First remote video decoded, uid:"+uid);
                    //加个缓冲
                    prompt_tv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setupRemoteVideo(uid);    // Tutorial Step 2
                        }
                    },1000);
                }
            });
        }
    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        if (isLeft)
            return;
        Log.d(TAG, "onUserOffline: 1用户离开频道"+uid);
        isLeft = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: 视频通话时长"+Common.getChronometerSeconds(chronometer));
                //评星
                onRemoteUserLeft(uid);
                closeService();
                leaveChannel();

                finish();
            }
        });
    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private void stickerDescriptionShow(){
        new MaterialTapTargetPrompt.Builder(this)
                .setTarget(btn_beautiful)
                .setPrimaryText("关于面具")
                .setSecondaryText("通话3分钟后，双方面具自动摘除，也可以点击此处，主动摘除自己的面具！")
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED
                                || state == MaterialTapTargetPrompt.STATE_DISMISSING)
                        {
                            // User has pressed the prompt target

                        }
                    }
                })
                .show();
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
                    chronometer.setVisibility(View.VISIBLE);
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();

                    prompt_tv.setVisibility(View.GONE);
                }
            },500);
        }

        @Override
        public void onLocalInvitationRefused(LocalInvitation localInvitation, String s) {
            Log.d(TAG, "onLocalInvitationRefused: 返回给主叫：被叫已拒绝呼叫邀请。");
            finish();
        }

        @Override
        public void onLocalInvitationCanceled(LocalInvitation localInvitation) {
            Log.d(TAG, "onLocalInvitationCanceled: 返回给主叫：呼叫邀请已被取消。");
            finish();
        }

        @Override
        public void onLocalInvitationFailure(LocalInvitation localInvitation, int i) {
            Log.d(TAG, "onLocalInvitationFailure: 返回给主叫：呼叫邀请进程失败。");
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
