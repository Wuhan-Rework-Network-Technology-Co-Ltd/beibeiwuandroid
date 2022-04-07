package xin.banghua.beiyuan.chat;

import static xin.banghua.beiyuan.utils.ThreadUtils.runOnUiThread;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import io.agora.chatroom.UserInfoList;
import io.agora.chatroom.manager.ChatRoomManager;
import io.agora.chatroom.util.SoundUtil;
import xin.banghua.beiyuan.App;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.chat.faceunity.RtcEngineEventHandler;


public class VideoFloatService extends Service implements RtcEngineEventHandler {
    public static final String FROM_VIDEO_SERVICE = "VideoFloatService";

    private static final String TAG = "VideoFloatService";
    private ChatRoomManager mManager;

    private WindowManager winManager;
    private WindowManager.LayoutParams wmParams;
    private LayoutInflater inflater;
    //浮动布局
    private View mFloatingLayout;
    private LinearLayout linearLayout;
    private long rangeTime;
    private TextView channel_name_tv;

    private CircleImageView my_imgview;
    Button close_btn;


    @Override
    public void onCreate() {
        Log.d("服务","onCreate");
        mManager = ChatRoomManager.instance(this);
        App.getApplication().addRtcHandler(this);

        initWindow();
        //悬浮框点击事件的处理
        initFloating();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("服务","onStartCommand");


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("服务","onDestroy");
        super.onDestroy();
        App.getApplication().removeRtcHandler(this);
        try {
            winManager.removeView(mFloatingLayout);
        }catch (RuntimeException e){

        }

        //mFloatingLayout.setVisibility(View.GONE);

//        CommonKtv.audioroom = 0;
//        Log.d(TAG, "run:CommonKtv.audioroom:"+CommonKtv.audioroom);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //Service断开连接时回调
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("TAG", "onUnbind方法被调用!");
        return true;
    }


    @Override
    public void onRebind(Intent intent) {
        Log.i("TAG", "onRebind方法被调用!");
        super.onRebind(intent);
    }

    /**
     * 初始化窗口
     */
    private void initWindow() {

        winManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);

        //设置好悬浮窗的参数
        wmParams = getParams();
        // 悬浮窗默认显示以左上角为起始坐标
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        //悬浮窗的开始位置，因为设置的是从左上角开始，所以屏幕左上角是x=0;y=0
        wmParams.x = winManager.getDefaultDisplay().getWidth();
        wmParams.y = winManager.getDefaultDisplay().getHeight() - getResources().getDimensionPixelSize(R.dimen.dimen_450);
        //得到容器，通过这个inflater来获得悬浮窗控件
        inflater = LayoutInflater.from(getApplicationContext());
        // 获取浮动窗口视图所在布局
        mFloatingLayout = inflater.inflate(R.layout.service_float_video, null);
//        mFloatingLayout.setBackgroundResource(R.drawable.shape_gradiant_black);
        // 添加悬浮窗的视图
        winManager.addView(mFloatingLayout, wmParams);
        //转动
//        my_imgview = mFloatingLayout.findViewById(R.id.my_imgview);
//        Animation animation= AnimationUtils.loadAnimation(this,R.anim.rotate_repeat);
//        LinearInterpolator lin=new LinearInterpolator();
//        animation.setInterpolator(lin);
//        my_imgview.startAnimation(animation);
    }
    private WindowManager.LayoutParams getParams() {
        wmParams = new WindowManager.LayoutParams();
        //悬浮窗口的黑色背景透明化
        wmParams.format = PixelFormat.TRANSLUCENT;
        //设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //设置可以显示在状态栏上
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        return wmParams;
    }

    /**
     * 悬浮窗点击事件
     */
    private void initFloating() {
        linearLayout = mFloatingLayout.findViewById(R.id.line1);

        my_imgview = mFloatingLayout.findViewById(R.id.my_imgview);
        my_imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取ActivityManager
                ActivityManager mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                //获得当前运行的task
                List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
                for (ActivityManager.RunningTaskInfo rti : taskList) {
                    //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
                    if (rti.topActivity.getPackageName().equals(getPackageName())) {
                        mAm.moveTaskToFront(rti.id, 0);
                    }
                }



                Intent intent = new Intent(VideoFloatService.this, VideoChatViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("channel", VideoChatViewActivity.channel);
                intent.putExtra("targetId", VideoChatViewActivity.targetId);
                intent.putExtra("targetPortrait", VideoChatViewActivity.targetPortrait);
                intent.putExtra("myRole", VideoChatViewActivity.myRole);
                intent.putExtra("from", FROM_VIDEO_SERVICE);
                intent.putExtra("userJoined",userJoined);
                startActivity(intent);
            }
        });

        //悬浮框触摸事件，设置悬浮框可拖动
        linearLayout.setOnTouchListener(new FloatingListener());
    }


    //开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
    private int mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;
    //开始时的坐标和结束时的坐标（相对于自身控件的坐标）
    private int mStartX, mStartY, mStopX, mStopY;
    //判断悬浮窗口是否移动，这里做个标记，防止移动后松手触发了点击事件
    private boolean isMove;

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {

    }

    @Override
    public void onUserOffline(int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mManager.leaveChannel();
                stopService(Common.getInstanceVideoFloatService());
                //获取ActivityManager
                ActivityManager mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                //获得当前运行的task
                List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
                for (ActivityManager.RunningTaskInfo rti : taskList) {
                    //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
                    if (rti.topActivity.getPackageName().equals(getPackageName())) {
                        mAm.moveTaskToFront(rti.id, 0);
                    }
                }
            }
        });
    }

    int userJoined = 0;
    @Override
    public void onUserJoined(int uid, int elapsed) {
        if (!Common.userInfoList.getId().equals(uid+"")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    userJoined = uid;
                    Toast.makeText(VideoFloatService.this,"匹配成功！",Toast.LENGTH_SHORT).show();
                    SoundUtil.getInstance(VideoFloatService.this).playSoundLoop(SoundUtil.SOUND_LOVE_BELL);
                    OkHttpInstance.getUserAttributes(uid+"", new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {
                            UserInfoList userInfoList = JSON.parseObject(responseString, UserInfoList.class);
                            VideoChatViewActivity.targetId = userInfoList.getId();
                            VideoChatViewActivity.targetPortrait = userInfoList.getPortrait();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {

    }

    private class FloatingListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d(TAG, "onTouch: 触发拖动");
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    mTouchStartX = (int) event.getRawX();
                    mTouchStartY = (int) event.getRawY();
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mTouchCurrentX = (int) event.getRawX();
                    mTouchCurrentY = (int) event.getRawY();
                    wmParams.x += mTouchCurrentX - mTouchStartX;
                    wmParams.y += mTouchCurrentY - mTouchStartY;
                    winManager.updateViewLayout(mFloatingLayout, wmParams);
                    mTouchStartX = mTouchCurrentX;
                    mTouchStartY = mTouchCurrentY;
                    break;
                case MotionEvent.ACTION_UP:
                    mStopX = (int) event.getX();
                    mStopY = (int) event.getY();
                    if (Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1) {
                        isMove = true;
                    }
                    break;
                default:
                    break;
            }

            //如果是移动事件不触发OnClick事件，防止移动的时候一放手形成点击事件
            return true;
        }
    }
}
