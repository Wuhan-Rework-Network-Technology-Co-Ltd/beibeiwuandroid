package xin.banghua.beiyuan;


import static xin.banghua.beiyuan.custom_ui.ad.SplashAdFrameLayout.isClickAd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.baidu.mobads.sdk.api.SplashAd;
import com.orhanobut.dialogplus.DialogPlus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.agora.chatroom.manager.ChatRoomManager;
import io.agora.chatroom.util.MD5Tool;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Adapter.FollowList;
import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.MainBranch.LocationService;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.Signin.SigninActivity;
import xin.banghua.beiyuan.custom_ui.ad.CommonCallBackInterfaceWithSlashAd;
import xin.banghua.beiyuan.custom_ui.ad.SplashAdFrameLayout;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;

public class LaunchActivity extends Activity {
    private static final String TAG = "SplashScreenSampleActivity";

    ImageView launchImage;

    Context mContext;


    SplashAdFrameLayout splashAdFrameLayout;

    @Override
    protected void onResume() {
        super.onResume();





        intent = new Intent(LaunchActivity.this, Main4Activity.class);

        Log.d(TAG, "onCreate: applet OSS测试"+ Common.getOssResourceUrl("https://appletattachment.oss-cn-beijing.aliyuncs.com/images/11/2018/10/ASPPPs46NZmnZpYUhp9Y4FES06evuu.jpg"));
        Log.d(TAG, "onCreate: moyuan OSS测试"+ Common.getOssResourceUrl("https://moyuanoss.oss-cn-shanghai.aliyuncs.com/images/11/2018/10/ASPPPs46NZmnZpYUhp9Y4FES06evuu.jpg"));


        Log.d(TAG, "onCreate: md5"+ MD5Tool.MD5("123"));



        ifSignin();


        if (!SharedHelper.getInstance(this).readPrivateAgreement()){
            Log.d(TAG, "onResume: 弹出隐私协议");
            private_policy_dialog();
        }else {
            //初始化第三方SDK
            App.initThirdSDK();

            OkHttpInstance.isShowAD(responseString -> {
                if (responseString.equals("no")){
                    intentMain(2);
                }else {
                    //开屏广告
                    splashAdFrameLayout = findViewById(R.id.splashAdFrameLayout);
                    splashAdFrameLayout.setParams(LaunchActivity.this,launchImage,intent);
                    splashAdFrameLayout.ttADShow(new CommonCallBackInterfaceWithSlashAd() {
                        @Override
                        public void callBack(SplashAd splashAd) {
                            intentMain(1);
                        }
                    },responseString);
                }
            });

            //已同意隐私协议
//            image_framelayout.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (isClickAd || isLoadedAd){//广告加载成功或点击了广告都不跳转
//                        return;
//                    }
//                    //跳转主页
//                    intentMain(2);
//                }
//            },3000);
        }
        if (isClickAd) {
            Log.d(TAG, "onStart: 跳转首页");
            // 从广告界面返回的
            intentMain(8);
        }
    }
    FrameLayout image_framelayout;
    LinearLayout ad_linearlayout;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        if (",165661,117886,86261,1596,169152,20978,".contains(",169152,")){
            Log.d(TAG, "onCreate: 测试包含169152");
        }

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        Common.screen_width = metric.widthPixels;     // 屏幕宽度（像素）
        Common.screen_height = metric.heightPixels;   // 屏幕高度（像素）
        Common.screen_density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        Common.screen_densityDpi = metric.densityDpi;

        io.agora.chatroom.Common.screen_width = metric.widthPixels;     // 屏幕宽度（像素）
        io.agora.chatroom.Common.screen_height = metric.heightPixels;   // 屏幕高度（像素）
        io.agora.chatroom.Common.screen_density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        io.agora.chatroom.Common.screen_densityDpi = metric.densityDpi;


        /**
         * 【注意】开屏点睛需要开屏和主页的窗口具有特性 {@linkplain Window.FEATURE_ACTIVITY_TRANSITIONS}
         * Tips: 一般具有Material Design风格的App主题，系统会默认开启该特性。
         *       若没有该特性，可以在{@link #setContentView(int)}之前
         *       调用 {@link #requestWindowFeature(int)} 即可开启特性，如下：
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        }
        /* 设置开屏全屏显示&透明状态栏 */
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);


        // Handle the splash screen transition.
        //SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_launch);

        mContext = this;

        image_framelayout = findViewById(R.id.image_framelayout);
        launchImage = findViewById(R.id.launchImage);
        launchImage.setImageResource(R.drawable.launch);
    }

    Uniquelogin uniquelogin;
    public void ifSignin(){
        Map<String,String> userInfo;
        SharedHelper sh;
        sh = new SharedHelper(getApplicationContext());
        userInfo = sh.readUserInfo();
        //Toast.makeText(mContext, userInfo.toString(), Toast.LENGTH_SHORT).show();

        if(userInfo.get("userID")==""){
            Log.d(TAG, "ifSignin: 1启动是否登录");
            Common.myID = null;
//            Intent intentSignin = new Intent(MainActivity.this, SigninActivity.class);
//            startActivity(intentSignin);
        }else{
            Log.d(TAG, "ifSignin: 2启动是否登录"+userInfo.toString());
            //唯一登录验证
            Common.myID = userInfo.get("userID");
            io.agora.chatroom.model.Constant.sUserId = Integer.parseInt(Common.myID);
            Log.d(TAG, "ifSignin: 聊天室我的id"+io.agora.chatroom.model.Constant.sUserId);
            uniquelogin = new Uniquelogin(this,handler);
            uniquelogin.compareUniqueLoginToken("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=uniquelogin&m=socialchat");
            //登录后，更新定位信息，包括经纬度和更新时间
            //获取用户id和定位值
            SharedHelper shlocation = new SharedHelper(getApplicationContext());
            Map<String,String> locationInfo = shlocation.readLocation();
            //postLocationInfo(userInfo.get("userID"),locationInfo.get("latitude"),locationInfo.get("longitude"),"https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=updatelocation&m=socialchat");
            //开启定位服务
            Intent startIntent = new Intent(this, LocationService.class);
            startService(startIntent);

            //获取自己信息，储存在Common类中
            getDataMyInfo(getString(R.string.personage_url),userInfo.get("userID"));

            OkHttpInstance.getFollowList(userInfo.get("userID"), responseString -> {
                if (!responseString.equals("false")){
                    Common.followList = JSON.parseArray(responseString, FollowList.class);
                    Log.d(TAG, "onBindViewHolder: 关注的人"+Common.followList.size());
                }
            });
        }
    }

    /**
     * TODO okhttp获取自己信息，储存在Common类中
     * @param url
     */
    //
    public void getDataMyInfo(final String url,String userID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("userid", userID)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Message message = handler.obtainMessage();
                    message.obj = response.body().string();
                    message.what = 2;
                    Log.d(TAG, "run: getDataMyInfo发送的值" + message.obj.toString());
                    handler.sendMessageDelayed(message, 10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是用户数据，2是幻灯片
            switch (msg.what){
                case 10:
                    if (msg.obj.toString().equals("false")){
                        uniquelogin.uniqueNotification();
                        SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("userID", "");
                        editor.commit();
                        Intent intent = new Intent(LaunchActivity.this, SigninActivity.class);
                        intent.putExtra("uniquelogin","强制退出");
                        startActivity(intent);
                    }
                    break;
                case 1:
                    if (Double.parseDouble(msg.obj.toString())> BuildConfig.VERSION_CODE){
                        final DialogPlus dialog = DialogPlus.newDialog(mContext)
                                .setAdapter(new BaseAdapter() {
                                    @Override
                                    public int getCount() {
                                        return 0;
                                    }

                                    @Override
                                    public Object getItem(int position) {
                                        return null;
                                    }

                                    @Override
                                    public long getItemId(int position) {
                                        return 0;
                                    }

                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        return null;
                                    }
                                })
                                .setFooter(R.layout.dialog_foot_newversion)
                                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                                .create();
                        dialog.show();
                        View view = dialog.getFooterView();
                        TextView scoreresult = view.findViewById(R.id.scoreresult);
                        scoreresult.setText("请尽快更新版本"+msg.obj.toString()+",否则部分功能可能无法使用！！！");
                        Button vipconversion_btn = view.findViewById(R.id.newversion_btn);
                        vipconversion_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
//                                intent.putExtra("slidername","新版本");
//                                intent.putExtra("sliderurl","https://a.app.qq.com/o/simple.jsp?pkgname=xin.banghua.beiyuan");
//                                mContext.startActivity(intent);
                                //new DownloadUtils(MainActivity.this, "https://oss.banghua.xin/attachment/beibeiwu.apk", "beibeiwu.apk");
//                                Toast.makeText(MainActivity.this, "正在下载中......", Toast.LENGTH_LONG).show();
                                Intent intent =  new  Intent();
                                intent.setAction( "android.intent.action.VIEW" );
                                Uri content_url = Uri.parse( "https://beibeiwu.banghua.xin" );
                                intent.setData(content_url);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        Button dismissdialog_btn = view.findViewById(R.id.dismissdialog_btn);
                        dismissdialog_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                    break;
                case 2:
                    Common.myInfo = msg.obj.toString();
                    Log.d(TAG, "handleMessage: 自己信息"+Common.myInfo);
                    Common.userInfoList = JSON.parseObject(Common.myInfo, UserInfoList.class);
                    OkHttpInstance.postRongyunUserRegister(Common.userInfoList, new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {
                            Log.d(TAG, "getResponseString: 融云登录成功");
                        }
                    });
                    Common.removeVipPortraitFrameOrVehicle();
                    io.agora.chatroom.Common.myUserInfoList = JSON.parseObject(Common.myInfo, io.agora.chatroom.UserInfoList.class);
                    Log.d(TAG, "handleMessage: 1自己信息"+Common.userInfoList.getId());
                    //聊天室用户信息
                    try {
                        io.agora.chatroom.model.Constant.sUserId= Integer.parseInt(Common.userInfoList.getId());
                        io.agora.chatroom.model.Constant.sName = Common.userInfoList.getNickname();
                        io.agora.chatroom.model.Constant.sPortrait = Common.userInfoList.getPortrait();
                        io.agora.chatroom.model.Constant.sGender = Common.userInfoList.getGender();
                        io.agora.chatroom.model.Constant.sProperty = Common.userInfoList.getProperty();
                        io.agora.chatroom.model.Constant.sRoomName = Common.userInfoList.getAudioroomname();
                        io.agora.chatroom.model.Constant.sRoomCover = Common.userInfoList.getAudioroomcover();
                        io.agora.chatroom.model.Constant.sRoomBG = Common.userInfoList.getAudioroombackground();
                        io.agora.chatroom.model.Constant.vip = Common.userInfoList.getVip();
                        io.agora.chatroom.model.Constant.svip = Common.userInfoList.getSvip();
                    } catch (NumberFormatException e) {
                        Log.d(TAG, "handleMessage: 抛出自己信息异常");
                        e.printStackTrace();
                    }

                    try {
                        //缓存好友备注
                        JSONObject jsonObject = new ParseJSONObject(msg.obj.toString()).getParseJSON();
                        if (jsonObject.getInt("svip_try")==1){
                            Log.d(TAG, "svip已试用");
                            SharedHelper.getInstance(LaunchActivity.this).saveTryChat(1);
                        }
                        if (!jsonObject.getString("friendsremark").equals("null")&&!jsonObject.getString("friendsremark").equals("")){
                            Map<String,String> map = new HashMap();
                            String[] friendsRemarkArray = jsonObject.getString("friendsremark").split(";");//id1:remark1
                            for (int i=0;i<friendsRemarkArray.length;i++){
                                String[] friendRemark = friendsRemarkArray[i].split(":");//id1    remark1
                                map.put(friendRemark[0],friendRemark[1]);
                            }
                            Common.friendsRemarkMap = map;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ChatRoomManager.instance(getApplication()).joinChannel("0");
                    break;
            }
        }
    };

    //1.检测权限
    private void private_policy_dialog() {
        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return 0;
                    }

                    @Override
                    public Object getItem(int position) {
                        return null;
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        return null;
                    }
                })
                .setFooter(R.layout.dialog_private_agreement)
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
        View view = dialog.getFooterView();
        Button useragreement_btn = view.findViewById(R.id.useragreement_btn);
        useragreement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
                intent.putExtra("slidername","小贝乐园用户协议");
                intent.putExtra("sliderurl","https://console.banghua.xin/useragreement.html");
                mContext.startActivity(intent);
                dialog.dismiss();
            }
        });
        Button privacypolicy_btn = view.findViewById(R.id.privacypolicy_btn);
        privacypolicy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
                intent.putExtra("slidername","小贝乐园隐私政策");
                intent.putExtra("sliderurl","https://console.banghua.xin/privacypolicy.html");
                mContext.startActivity(intent);
                dialog.dismiss();
            }
        });
        Button dismissdialog_btn = view.findViewById(R.id.cancel_btn);
        dismissdialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
                dialog.dismiss();
            }
        });
        Button confirm_btn = view.findViewById(R.id.confirm_btn);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始化第三方SDK
                App.initThirdSDK();


                OkHttpInstance.isShowAD(responseString -> {
                    if (responseString.equals("no")){
                        intentMain(2);
                    }else {
                        //开屏广告
                        splashAdFrameLayout = findViewById(R.id.splashAdFrameLayout);
                        splashAdFrameLayout.setParams(LaunchActivity.this,launchImage,intent);
                        splashAdFrameLayout.ttADShow(new CommonCallBackInterfaceWithSlashAd() {
                            @Override
                            public void callBack(SplashAd splashAd) {
                                intentMain(1);
                            }
                        },responseString);
                    }
                });


                SharedHelper.getInstance(mContext).savePrivateAgreement(true);
//                //1.检测权限
//                int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
//                if (permission != PermissionChecker.PERMISSION_GRANTED) {
//                    //2.没有权限，弹出对话框申请
//                    ActivityCompat.requestPermissions(SplashScreenSampleActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1000);
//                }
                dialog.dismiss();
            }
        });
    }

    //授权回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:
                switch (permissions[0]){
                    case Manifest.permission.ACCESS_COARSE_LOCATION://权限1
                        if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                            //intentMain(4);
                            Log.d(TAG, "onRequestPermissionsResult: COARSE");
                        }else {
                            //Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                            //intentMain(5);
                        }
                        break;
                    case Manifest.permission.ACCESS_FINE_LOCATION://权限2
                        if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                            //intentMain(6);
                            Log.d(TAG, "onRequestPermissionsResult: fine");
                        } else {
                            //Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                            //intentMain(7);
                        }
                        break;
                    default:
                }
                break;
            default:
        }
    }

    public void intentMain(int i){
        Log.d(TAG, "onStart: 跳转首页intentMain"+i);
        Intent intent = new Intent(LaunchActivity.this, Main4Activity.class);
        startActivity(intent);
        finish();
        //实现定位授权，初始图片，
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(SplashScreenSampleActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }, 1000);
    }







    @Override
    protected void onStart() {
        super.onStart();

    }
}
