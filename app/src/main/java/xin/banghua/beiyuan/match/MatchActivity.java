package xin.banghua.beiyuan.match;

import static xin.banghua.onekeylogin.Constant.THEME_KEY;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.faceunity.nama.ui.CircleImageView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.manager.RtmManager;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmMessage;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.model.Conversation;
import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.App;
import xin.banghua.beiyuan.BadgeBottomNav;
import xin.banghua.beiyuan.CheckPermission;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.Main3Activity;
import xin.banghua.beiyuan.Main4Activity;
import xin.banghua.beiyuan.Main5Activity;
import xin.banghua.beiyuan.MainActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.chat.VideoChatViewActivity;
import xin.banghua.beiyuan.chat.VoiceChatViewActivity;
import xin.banghua.beiyuan.custom_ui.MatchDialog;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;
import xin.banghua.onekeylogin.login.OneKeyLoginActivity;

public class MatchActivity extends AppCompatActivity {
    private static final String TAG = "matchactivity";
    @BindView(R.id.match_layout)
    ConstraintLayout match_layout;
    @BindView(R.id.online_match)
    ImageView online_match;
    @BindView(R.id.voice_match)
    ImageView voice_match;
    @BindView(R.id.video_match)
    ImageView video_match;
    @BindView(R.id.online_match_times_tv)
    TextView online_match_times_tv;

    int[] location1 = new int[2];


    List<UserInfoList> userInfoLists = new ArrayList<>();

    int index1 = 1;
    int index2 = 2;
    int index3 = 3;
    int index4 = 4;
    int index5 = 5;
    int index6 = 6;
    int index7 = 7;

    AppCompatActivity mContext;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_shenbian:

                    Intent intent1 = new Intent(MatchActivity.this, MainActivity.class);
                    startActivity(intent1);
                    return true;
                case R.id.navigation_haoyou:
                    //Intent intent2 = new Intent(Main2Activity.this, Main2Activity.class);
                    //startActivity(intent2);
                    return true;
                case R.id.navigation_xiaoxi:
                    Intent intent3 = new Intent(MatchActivity.this, Main3Activity.class);
                    startActivity(intent3);
                    return true;
                case R.id.navigation_dongtai:
                    Intent intent4 = new Intent(MatchActivity.this, Main4Activity.class);
                    startActivity(intent4);
                    return true;
                case R.id.navigation_wode:
                    Intent intent5 = new Intent(MatchActivity.this, Main5Activity.class);
                    startActivity(intent5);
                    return true;
            }
            return false;
        }
    };

    //未读信息监听相关
    private BottomNavigationView bottomNavigationView;
    private IUnReadMessageObserver iUnReadMessageObserver;

    FrameLayout match_loading_layout;
    ImageView match_love;
    CircleImageView portrait_one;
    CircleImageView portrait_two;
    TextView online_match_num_tv;

    @Override
    protected void onResume() {
        super.onResume();
        if (Common.userInfoList!=null){
            int lv = Integer.parseInt(Common.userInfoList.getVitality()) + Integer.parseInt(Common.userInfoList.getPost()) + Integer.parseInt(Common.userInfoList.getComment());
            if (lv <= 100){
                OkHttpInstance.getOnlineMatchTimes(new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        Common.onlineMatchTimes = 10 - Integer.parseInt(responseString);
                        if (Common.onlineMatchTimes>0){
                            online_match.setClickable(true);
                            online_match_times_tv.setText("剩余次数"+Common.onlineMatchTimes+"\n2级后无限");
                        }
                    }
                });
            }else{
                online_match_times_tv.setVisibility(View.GONE);
                Common.onlineMatchTimes = 9999;
                online_match.setClickable(true);
            }
        }


        OkHttpInstance.getOnlineMatchNum(new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                try {
                    if (Integer.parseInt(responseString)==0){
                        online_match_num_tv.setText("当前6879人正在匹配");
                    }else {
                        online_match_num_tv.setText("当前"+responseString+"人正在匹配");
                    }
                }catch (Exception e){
                    Log.e(TAG, "getResponseString: 抛出异常");
                }
            }
        });


        OkHttpInstance.getOnlineMatchUser(id,gender,property,new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                if (!responseString.equals("false")) {
                    userInfoLists = JSON.parseArray(responseString, UserInfoList.class);
                    Collections.shuffle(userInfoLists);
                }
            }
        });
    }
    String id = "";
    String gender = "男";
    String property = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        App.getApplication().initVideoCaptureAsync();

        getWindow().setStatusBarColor(getResources().getColor(R.color.match_color_bg,null));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        ButterKnife.bind(this);
        mContext = this;

        online_match.setOnClickListener(v -> {
            if (Common.onlineMatchTimes>0){
                MatchDialog matchDialog = new MatchDialog(mContext);
                matchDialog.initShow(new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        voice_match.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startOnlineMatch();
                            }
                        },1000);
                    }
                });
            }else {
                Toast.makeText(mContext,"次数已用完，升到2级后可无限次数",Toast.LENGTH_SHORT).show();
            }
        });
        online_match.setClickable(false);

        online_match_times_tv.setText("剩余次数0\n2级后无限");

        online_match_num_tv = findViewById(R.id.online_match_num_tv);



        match_love = findViewById(R.id.match_love);
        Glide.with(this).load(R.drawable.match_loading).into(match_love);

        match_loading_layout = findViewById(R.id.match_loading_layout);
        match_loading_layout.setVisibility(View.GONE);
        portrait_one = findViewById(R.id.portrait_one);
        portrait_two = findViewById(R.id.portrait_two);
//        RongExtensionManager.getInstance().addExtensionModule(new ContactCardExtensionModule());
//        RongIM.registerMessageType(MatchMessage.class); //注册名片消息
//        RongIM.getInstance().registerMessageTemplate(new MatchMessageProvider());

        //底部导航初始化和配置监听，默认选项
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.navigation_haoyou);
        //未读信息监听
        iUnReadMessageObserver = new IUnReadMessageObserver() {
            @Override
            public void onCountChanged(int i) {
                BadgeBottomNav.unreadMessageBadge(bottomNavigationView, i, getApplicationContext());
                //initUnreadBadge(bottomNavigationView,i);
            }
        };
        RongIM.getInstance().addUnReadMessageCountChangedObserver(iUnReadMessageObserver, Conversation.ConversationType.PRIVATE);


        voice_match.setOnClickListener(v -> {
            CheckPermission.verifyPermissionAudioAndStorage(mContext);
            if (Common.userInfoList ==null){
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
                        .setTitle("登录后可以发起匹配！")
                        .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(mContext, OneKeyLoginActivity.class);
                                intent.putExtra(THEME_KEY, 4);
                                mContext.startActivity(intent);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();
            }else {
                MatchDialog matchDialog = new MatchDialog(mContext);
                matchDialog.initShow(new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        voice_match.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startVoiceMatch();
                            }
                        },1000);
                    }
                });
            }
        });
        video_match.setOnClickListener(v -> {
            CheckPermission.verifyPermissionCameraAndAudioAndStorage(mContext);
            if (Common.userInfoList ==null){
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
                        .setTitle("登录后可以发起匹配！")
                        .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(mContext, OneKeyLoginActivity.class);
                                intent.putExtra(THEME_KEY, 4);
                                mContext.startActivity(intent);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();
            }else {
                MatchDialog matchDialog = new MatchDialog(mContext);
                matchDialog.initShow(new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        voice_match.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startVideoMatch();
                            }
                        },1000);
                    }
                });
            }
        });



        if (Common.userInfoList != null){
            id = Common.userInfoList.getId();
            gender = Common.userInfoList.getGender();
            property = Common.userInfoList.getProperty();
        }
        OkHttpInstance.getOnlineMatchUser(id,gender,property,new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                if(!responseString.equals("false")){
                    userInfoLists = JSON.parseArray(responseString,UserInfoList.class);
                    Collections.shuffle(userInfoLists);
                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (index1<userInfoLists.size()){
                                        portraitAnimation(userInfoLists.get(index1 - 1));
                                        index1 = index1 + 7;
                                    }else {
                                        index1 = 1;
                                    }
                                }
                            });
                        }
                    }, 0, 5000);


                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (index2<userInfoLists.size()){
                                        portraitAnimation(userInfoLists.get(index2 - 1));
                                        index2 = index2 + 7;
                                    }else {
                                        index2 = 2;
                                    }
                                }
                            });
                        }
                    }, 1000, 5000);

                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (index3<userInfoLists.size()){
                                        portraitAnimation(userInfoLists.get(index3 - 1));
                                        index3 = index3 + 7;
                                    }else {
                                        index3 = 3;
                                    }
                                }
                            });
                        }
                    }, 2000, 5000);

                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (index4<userInfoLists.size()){
                                        portraitAnimation(userInfoLists.get(index4 - 1));
                                        index4 = index4 + 7;
                                    }else {
                                        index4 = 4;
                                    }
                                }
                            });
                        }
                    }, 3000, 5000);

                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (index5<userInfoLists.size()){
                                        portraitAnimation(userInfoLists.get(index5 - 1));
                                        index5 = index5 + 7;
                                    }else {
                                        index5 = 5;
                                    }
                                }
                            });
                        }
                    }, 5000, 5000);


                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (index6<userInfoLists.size()){
                                        portraitAnimation(userInfoLists.get(index6 - 1));
                                        index6 = index6 + 7;
                                    }else {
                                        index6 = 6;
                                    }
                                }
                            });
                        }
                    }, 6000, 5000);


                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (index7<userInfoLists.size()){
                                        portraitAnimation(userInfoLists.get(index7 - 1));
                                        index7 = index7 + 7;
                                    }else {
                                        index7 = 7;
                                    }
                                }
                            });
                        }
                    }, 7000, 5000);
                }
            }
        });
    }



    public void portraitAnimation(UserInfoList userInfoList){
        //礼物动画加数据库修改
        MatchPortraitFrameLayout imageView = new MatchPortraitFrameLayout(MatchActivity.this);
        imageView.initShow(userInfoList);
        if (Common.onlineMatchTimes<1){
            imageView.notClickableShow();
        }
        match_layout.addView(imageView); //动态添加图片
        match_layout.getLocationInWindow(location1);
        PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("translationX", new Random().nextInt(1000)%(1000-100+1) + 100, new Random().nextInt(1000)%(1000-100+1) + 100);
        PropertyValuesHolder holder2 = PropertyValuesHolder.ofFloat("translationY", new Random().nextInt(1000)%(1000-100+1) + 100, new Random().nextInt(1000)%(1000-100+1) + 100);
        PropertyValuesHolder holder3 = PropertyValuesHolder.ofFloat("scaleX", 0.1f, 1.0f);
        PropertyValuesHolder holder4 = PropertyValuesHolder.ofFloat("scaleY", 0.1f, 1.0f);
        ObjectAnimator animator1 = ObjectAnimator.ofPropertyValuesHolder(imageView, holder1, holder2, holder3, holder4);

        PropertyValuesHolder holder5 = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);
        ObjectAnimator animator2 = ObjectAnimator.ofPropertyValuesHolder(imageView, holder5);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animator1,animator2);
        set.setDuration(2500);
        set.start();
        match_layout.postDelayed(() -> {
            match_layout.removeView(imageView);
        }, 5000);
    }

    private void startOnlineMatch(){
        if (Common.userInfoList ==null){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
                    .setTitle("登录后可以发起匹配！")
                    .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(mContext, OneKeyLoginActivity.class);
                            intent.putExtra(THEME_KEY, 4);
                            mContext.startActivity(intent);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            builder.create().show();
        }else{
            if (RongIM.getInstance() != null) {
                Common.chatFrom = "match";

                match_loading_layout.setVisibility(View.VISIBLE);
                OkHttpInstance.getOnlineMatchUserOne(new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        UserInfoList userInfoList = JSON.parseObject(responseString,UserInfoList.class);

                        portrait_one.setVisibility(View.VISIBLE);
                        portrait_two.setVisibility(View.VISIBLE);
                        Glide.with(MatchActivity.this).load(userInfoList.getPortrait()).into(portrait_one);
                        Glide.with(MatchActivity.this).load(Common.userInfoList.getPortrait()).into(portrait_two);

                        match_love.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                match_loading_layout.setVisibility(View.GONE);
                                portrait_one.setVisibility(View.GONE);
                                portrait_two.setVisibility(View.GONE);
                                Common.onlineMatchTimes -= 1;
                                online_match_times_tv.setText("剩余次数"+Common.onlineMatchTimes+"\n2级后无限");
                                RongIM.getInstance().startPrivateChat(mContext, userInfoList.getId(), userInfoList.getNickname());
                            }
                        },1500);
                    }
                });
            }
        }
    }

    private void startVoiceMatch(){
        if (Common.userInfoList == null){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
                    .setTitle("登录后可以发起匹配！")
                    .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(mContext, OneKeyLoginActivity.class);
                            intent.putExtra(THEME_KEY, 4);
                            mContext.startActivity(intent);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            builder.create().show();
        }else {
            OkHttpInstance.xiaobeiMatch("语音", new OkHttpResponseCallBack() {
                @Override
                public void getResponseString(String responseString) {
                    if (responseString.equals("wait")){
                        VoiceChatViewActivity.targetId = null;
                        Intent intent = new Intent(mContext, VoiceChatViewActivity.class);
                        intent.putExtra("channel", "match"+Common.userInfoList.getId());
                        intent.putExtra("targetId", Common.userInfoList.getId());
                        intent.putExtra("targetPortrait", Common.userInfoList.getPortrait());
                        intent.putExtra("myRole", "remote");
                        mContext.startActivity(intent);
                    }else {
                        UserInfoList userInfoList = JSON.parseObject(responseString,UserInfoList.class);
                        RtmMessage message = RtmManager.instance(mContext).getRtmClient().createMessage();
                        message.setText("{\"type\":\"match_voice\"}");
                        RtmManager.instance(mContext).getRtmClient().sendMessageToPeer(userInfoList.getId(), message, RtmManager.instance(mContext).getSendMessageOptions(), new ResultCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: 声网消息发送成功");
                                UserInfoList userInfoList = JSON.parseObject(responseString,UserInfoList.class);
                                Intent intent = new Intent(mContext, VoiceChatViewActivity.class);
                                intent.putExtra("channel", "match"+userInfoList.getId());
                                intent.putExtra("targetId", Common.userInfoList.getId());
                                intent.putExtra("targetPortrait", Common.userInfoList.getPortrait());
                                intent.putExtra("myRole", "remote");
                                mContext.startActivity(intent);
                            }
                            @Override
                            public void onFailure(ErrorInfo errorInfo) {
                                Log.d(TAG, "onFailure: 声网消息发送失败"+errorInfo.toString());
                                startVoiceMatch();
                            }
                        });
                    }
                }
            });
        }
    }

    private void startVideoMatch(){
        if (Common.userInfoList ==null){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
                    .setTitle("登录后可以发起匹配！")
                    .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(mContext, OneKeyLoginActivity.class);
                            intent.putExtra(THEME_KEY, 4);
                            mContext.startActivity(intent);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            builder.create().show();
        }else {
            OkHttpInstance.xiaobeiMatch("视频", new OkHttpResponseCallBack() {
                @Override
                public void getResponseString(String responseString) {
                    if (responseString.equals("wait")){
                        VideoChatViewActivity.targetId = null;
                        Intent intent = new Intent(mContext, VideoChatViewActivity.class);
                        intent.putExtra("channel", "match"+Common.userInfoList.getId());
                        intent.putExtra("targetId", Common.userInfoList.getId());
                        intent.putExtra("targetPortrait", Common.userInfoList.getPortrait());
                        intent.putExtra("myRole", "remote");
                        mContext.startActivity(intent);
                    }else {
                        UserInfoList userInfoList = JSON.parseObject(responseString,UserInfoList.class);
                        RtmMessage message = RtmManager.instance(mContext).getRtmClient().createMessage();
                        message.setText("{\"type\":\"match_video\"}");
                        RtmManager.instance(mContext).getRtmClient().sendMessageToPeer(userInfoList.getId(), message, RtmManager.instance(mContext).getSendMessageOptions(), new ResultCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: 声网消息发送成功");
                                UserInfoList userInfoList = JSON.parseObject(responseString,UserInfoList.class);
                                Intent intent = new Intent(mContext, VideoChatViewActivity.class);
                                intent.putExtra("channel", "match"+userInfoList.getId());
                                intent.putExtra("targetId", Common.userInfoList.getId());
                                intent.putExtra("targetPortrait", Common.userInfoList.getPortrait());
                                intent.putExtra("myRole", "remote");
                                mContext.startActivity(intent);
                            }
                            @Override
                            public void onFailure(ErrorInfo errorInfo) {
                                Log.d(TAG, "onFailure: 声网消息发送失败"+errorInfo.toString());
                                startVideoMatch();
                            }
                        });
                    }
                }
            });
        }
    }
}