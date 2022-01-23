package xin.banghua.beiyuan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.orhanobut.dialogplus.DialogPlus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.chatroom.activity.ChannelGridActivity;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.model.Conversation;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.MainBranch.LocationService;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.Signin.SigninActivity;
import xin.banghua.beiyuan.utils.Common;

//import android.support.design.widget.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Uniquelogin uniquelogin;

    //super果 18673668974
    private static final String token1 = "FnqsejuE+d830KfLY+PQ/r2N7KVbcvzutnIpZ+Sh9wDFjYfKqXxBctd4ec0OeFSRUAuX4eO3BltHQ60BVeZ2HA==";
    //贝吉塔 18673668975
    private String token2 = "";
    //希特 18673668976
    private static final String token3 = "PxvEuLbavYH5sNKqsxX7lb2N7KVbcvzutnIpZ+Sh9wDFjYfKqXxBcvx+1j1Oq44Au22Gnok3QTNHQ60BVeZ2HA==";


    private Context mContext = this;


    private TextView mTextMessage;

    //未读信息监听相关
    private BottomNavigationView bottomNavigationView;
    private IUnReadMessageObserver iUnReadMessageObserver;
    private TextView unreadNumber;

    //初始化底部导航按钮监听
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_shenbian:

                    //Intent intent1 = new Intent(MainActivity.this, MainActivity.class);
                    //startActivity(intent1);
                    return true;
                case R.id.navigation_haoyou:
                    if (Common.myID ==null){
                        Intent intentSignin = new Intent(mContext, SigninActivity.class);
                        mContext.startActivity(intentSignin);
                    }else {
                        Intent intent2 = new Intent(MainActivity.this, Main2Activity.class);
                        startActivity(intent2);
                    }
                    return true;
                case R.id.navigation_xiaoxi:

                    //Intent intent3 = new Intent(MainActivity.this, Main3Activity.class);
                    //startActivity(intent3);
                    //启动会话列表
                    if (Common.myID ==null){
                        Intent intentSignin = new Intent(mContext, SigninActivity.class);
                        mContext.startActivity(intentSignin);
                    }else {
                        startActivity(new Intent(MainActivity.this, Main3Activity.class));
                    }
                    return true;
                case R.id.navigation_dongtai:
                    Intent intent4 = new Intent(MainActivity.this, Main4Activity.class);
                    startActivity(intent4);
                    return true;
                case R.id.navigation_wode:
                    if (Common.myID ==null){
                        Intent intentSignin = new Intent(mContext, SigninActivity.class);
                        mContext.startActivity(intentSignin);
                    }else {
                        Intent intent5 = new Intent(MainActivity.this, Main5Activity.class);
                        startActivity(intent5);
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * 【注意】开屏点睛需要开屏和主页的窗口具有特性 {@linkplain Window.FEATURE_ACTIVITY_TRANSITIONS}
         * Tips: 一般具有Material Design风格的App主题，系统会默认开启该特性。
         *       若没有该特性，可以在{@link #setContentView(int)}之前
         *       调用 {@link #requestWindowFeature(int)} 即可开启特性，如下：
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //判断是否登录
        ifSignin();

        //判断版本号是否需要更新
        updateVersion();

        SharedPreferences sp = getApplication().getSharedPreferences("firstStarted", Context.MODE_PRIVATE);
        if (sp.getString("firstStarted", "null").equals("null")) {

            CheckPermission.verifyPermissionLocation(this);

            SharedPreferences.Editor editor = sp.edit();
            editor.putString("firstStarted", "firstStarted");
            editor.commit();
        }

        //定位问题
        //localization();

        //底部导航初始化和配置监听，默认选项
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.navigation_shenbian);
        //好友申请数
        BadgeBottomNav badgeBottomNav = new BadgeBottomNav(this,handler);
        badgeBottomNav.getDataFriendsapply(getString(R.string.friendsapplynumber_url));
        //未读信息监听
        iUnReadMessageObserver = new IUnReadMessageObserver() {
            @Override
            public void onCountChanged(int i) {
                BadgeBottomNav.unreadMessageBadge(bottomNavigationView,i,getApplicationContext());
                //initUnreadBadge(bottomNavigationView,i);
            }
        };
        RongIM.getInstance().addUnReadMessageCountChangedObserver(iUnReadMessageObserver,Conversation.ConversationType.PRIVATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RongIM.getInstance().removeUnReadMessageCountChangedObserver(iUnReadMessageObserver);
    }

    public void initUnreadBadge(BottomNavigationView navigation, Integer integer){
        //底部导航栏角标
        //获取整个的NavigationView
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        //这里就是获取所添加的每一个Tab(或者叫menu)，
        View tab = menuView.getChildAt(1);
        BottomNavigationItemView itemView = (BottomNavigationItemView) tab;
        //加载我们的角标View，新创建的一个布局
        View badge = LayoutInflater.from(this).inflate(R.layout.badge_unreadmessage, menuView, false);
        //添加到Tab上
        itemView.addView(badge);
        unreadNumber = badge.findViewById(R.id.badge_text);
        unreadNumber.setText("");
        unreadNumber.setText(String.valueOf(integer));
        //无消息时可以将它隐藏即可
        if (integer>0){
            unreadNumber.setVisibility(View.VISIBLE);
        }else {
            unreadNumber.setVisibility(View.GONE);
        }
    }
    public void ifSignin(){
        Map<String,String> userInfo;
        SharedHelper sh;
        sh = new SharedHelper(getApplicationContext());
        userInfo = sh.readUserInfo();
        //Toast.makeText(mContext, userInfo.toString(), Toast.LENGTH_SHORT).show();
        if(userInfo.get("userID")==""){

            Common.myID = null;
//            Intent intentSignin = new Intent(MainActivity.this, SigninActivity.class);
//            startActivity(intentSignin);
        }else{
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
        }
    }

    public void updateVersion(){
        //判断是不是关闭后的第一次启动
        SharedHelper shvalue = new SharedHelper(getApplicationContext());
        if (shvalue.readOnestart() == 1){
            shvalue.saveOnestart(2);
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .add("system", "android")
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getversion&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);

                        Message message = handler.obtainMessage();
                        message.obj = response.body().string();
                        message.what = 1;
                        Log.d(TAG, "run: Userinfo发送的值" + message.obj.toString());
                        handler.sendMessageDelayed(message, 10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    //定位
    private void localization() {
        Location location = getLastKnownLocation();

        if (location!=null){
            Log.d(TAG, "localization: latitude:"+location.getLatitude());
            Log.d(TAG, "localization: longitude:"+location.getLongitude());
            //保存定位值
            SharedHelper shlocation = new SharedHelper(getApplicationContext());
            //Float latitude = (float)(Math.round(location.getLatitude()*10))/10;
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            shlocation.saveLocation(latitude+"",longitude+"");
        }
    }

    private Location getLastKnownLocation() {
        Location l=null;
        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                l = mLocationManager.getLastKnownLocation(provider);
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    //更新定位信息
    public void postLocationInfo(final String userID,final String latitude, final String longitude,final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("userID", userID)
                        .add("latitude", latitude)
                        .add("longitude",longitude)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    //Log.d("form形式的post",response.body().string());
                    //不需要返回值
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是用户数据，2是幻灯片
            switch (msg.what){
                case 11:
                    //Log.d(TAG, "handleMessage: 用户数据接收的值"+msg.obj.toString());
                    SharedHelper shvalue = new SharedHelper(getApplicationContext());
                    String newFriendApplyNumber = shvalue.readNewFriendApplyNumber();
                    Log.d(TAG, "对比值"+newFriendApplyNumber+":"+msg.obj.toString());
                    if (newFriendApplyNumber.equals(msg.obj.toString())){
                        //BadgeBottomNav.newFriendApplicationBadge(bottomNavigationView,msg.obj.toString(),getApplicationContext());
                    }else {
                        BadgeBottomNav.newFriendApplicationBadge(bottomNavigationView,msg.obj.toString(),getApplicationContext());
                    }
                    break;
                case 10:
                    if (msg.obj.toString().equals("false")){
                        //通知
                        uniquelogin.uniqueNotification();
                        SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("userID", "");
                        editor.commit();
                        Intent intent = new Intent(MainActivity.this, SigninActivity.class);
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
                    xin.banghua.beiyuan.Common.myInfo = msg.obj.toString();
                    Log.d(TAG, "handleMessage: 自己信息"+xin.banghua.beiyuan.Common.myInfo);
                    Common.userInfoList = JSON.parseObject(xin.banghua.beiyuan.Common.myInfo, UserInfoList.class);
                    Log.d(TAG, "handleMessage: 1自己信息"+Common.userInfoList.getId());
                    //聊天室用户信息
                    try {
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
                            SharedHelper.getInstance(MainActivity.this).saveTryChat(1);
                        }
                        if (!jsonObject.getString("friendsremark").equals("null")&&!jsonObject.getString("friendsremark").equals("")){
                            Map<String,String> map = new HashMap();
                            String[] friendsRemarkArray = jsonObject.getString("friendsremark").split(";");//id1:remark1
                            for (int i=0;i<friendsRemarkArray.length;i++){
                                String[] friendRemark = friendsRemarkArray[i].split(":");//id1    remark1
                                map.put(friendRemark[0],friendRemark[1]);
                            }
                            xin.banghua.beiyuan.Common.friendsRemarkMap = map;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };



    public void testChatRoom(View view){
        if (Common.myID ==null){
            Intent intentSignin = new Intent(mContext, SigninActivity.class);
            mContext.startActivity(intentSignin);
        }else {
            Intent intent = new Intent(MainActivity.this, ChannelGridActivity.class);
            startActivity(intent);
        }
    }
}
