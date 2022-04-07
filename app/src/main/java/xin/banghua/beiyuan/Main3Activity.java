package xin.banghua.beiyuan;

import static xin.banghua.onekeylogin.Constant.THEME_KEY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Adapter.FriendList;
import xin.banghua.beiyuan.AlphabeticalOrder.PinyinUtils;
import xin.banghua.beiyuan.ParseJSON.ParseJSONArray;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.RongYunExtension.MyContactCard;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.Signin.SigninActivity;
import xin.banghua.onekeylogin.login.OneKeyLoginActivity;

public class Main3Activity extends AppCompatActivity {
    private static final String TAG = "Main3Activity";

    Uniquelogin uniquelogin;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mFragmentPagerAdapter;//将tab页面持久在内存中
    private Fragment mConversationList;
    private Fragment mConversationFragment = null;
    private List<Fragment> mFragment = new ArrayList<>();
    private List<FriendList> friendList = new ArrayList<>();
    Map<String,FriendList> friendListMap = new HashMap();
    private ImageView iv_back_left;

    private TextView mTextMessage;
    //未读信息监听相关
    private BottomNavigationView bottomNavigationView;
    private IUnReadMessageObserver iUnReadMessageObserver;
    private TextView unreadNumber;


    //通知未开启提示
    LinearLayout notification_hint_layout;
    TextView open_note_tv,close_note_hint_tv;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_shenbian:

                    Intent intent1 = new Intent(Main3Activity.this, MainActivity.class);
                    startActivity(intent1);
                    return true;
                case R.id.navigation_haoyou:

                    Intent intent2 = new Intent(Main3Activity.this, Main2Activity.class);
                    startActivity(intent2);
                    return true;
                case R.id.navigation_xiaoxi:

                    //Intent intent3 = new Intent(Main3Activity.this, Main3Activity.class);
                    //startActivity(intent3);
                    return true;
                case R.id.navigation_dongtai:

                    Intent intent4 = new Intent(Main3Activity.this, Main4Activity.class);
                    startActivity(intent4);
                    return true;
                case R.id.navigation_wode:

                    Intent intent5 = new Intent(Main3Activity.this, Main5Activity.class);
                    startActivity(intent5);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        ifSignin();
        mTextMessage = (TextView) findViewById(R.id.message);

        //getDataFriends(getString(R.string.friends_url));
        if (xin.banghua.beiyuan.Common.friendListMap==null) {
            getDataFriends(getString(R.string.friends_url));
        }else {
            for (Map.Entry<String, FriendList> m : xin.banghua.beiyuan.Common.friendListMap.entrySet()) {//通过entrySet
                friendList.add(m.getValue());
            }
        }

        //底部导航初始化和配置监听，默认选项
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.navigation_xiaoxi);
        //好友申请数
        BadgeBottomNav badgeBottomNav = new BadgeBottomNav(this,handler);
        badgeBottomNav.getDataFriendsapply("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=friendsapplynumber&m=socialchat");
        //未读信息监听
        iUnReadMessageObserver = new IUnReadMessageObserver() {
            @Override
            public void onCountChanged(int i) {
                BadgeBottomNav.unreadMessageBadge(bottomNavigationView,i,getApplicationContext());
                //initUnreadBadge(bottomNavigationView,i);
            }
        };
        RongIM.getInstance().addUnReadMessageCountChangedObserver(iUnReadMessageObserver, Conversation.ConversationType.PRIVATE);

        initView();

        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {

            @Override
            public UserInfo getUserInfo(String userId) {
//                //获取用户信息
//                for (FriendList i:friendList){
//                    if (i.getmUserID().equals(userId)){
//                        Log.d(TAG, "getUserInfo: 进入"+userId);
//                        return new UserInfo(i.getmUserID(),i.getmUserNickName(), Uri.parse(i.getmUserPortrait()));
//                    }
//                }
//                Log.d(TAG, "getUserInfo: 没进入"+userId);
                //获取用户信息
                Log.d("TAG","setUserInfoProvider"+userId);
                new Thread(new Runnable() {
                    @Override
                    public void run(){
                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add("userid", userId)
                                .build();
                        Request request = new Request.Builder()
                                .url(getString(R.string.personage_url))
                                .post(formBody)
                                .build();

                        try (Response response = client.newCall(request).execute()) {
                            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                            try {
                                JSONObject jsonObject = new ParseJSONObject(response.body().string()).getParseJSON();

                                //替换备注
                                String nickname = jsonObject.getString("nickname");
                                if (xin.banghua.beiyuan.Common.friendsRemarkMap!=null){
                                    for (Map.Entry<String, String> m : xin.banghua.beiyuan.Common.friendsRemarkMap.entrySet()) {//通过entrySet
                                        System.out.println("key:" + m.getKey() + " value:" + m.getValue());
                                        if (m.getKey().equals(userId)){
                                            nickname = m.getValue();
                                        }
                                    }
                                }

                                UserInfo userInfo = new UserInfo(userId, nickname, Uri.parse(Common.getOssResourceUrl(jsonObject.getString("portrait"))));
                                RongIM.getInstance().refreshUserInfoCache(userInfo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                return null;
            }

        }, true);




        initNotification();



        //判断是否是通知的跳转
        Intent intent = getIntent();
        Log.d(TAG, "onCreate: userid和usernamegetDataString"+intent.getDataString());
        String userid = intent.getStringExtra("userid");
        String username = intent.getStringExtra("username");

        Log.d(TAG, "onCreate:userid和username "+userid+username);

        if (RongIM.getInstance() != null && userid!=null) {
            RongIM.getInstance().startPrivateChat(this, userid, username);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            Log.d(TAG, "返回刷新");
        }
    }

    public void ifSignin(){
        Map<String,String> userInfo;
        SharedHelper sh;
        sh = new SharedHelper(getApplicationContext());
        userInfo = sh.readUserInfo();
        //Toast.makeText(mContext, userInfo.toString(), Toast.LENGTH_SHORT).show();
        if(userInfo.get("userID").equals("")){
            Intent intent = new Intent(Main3Activity.this, OneKeyLoginActivity.class);
            intent.putExtra(THEME_KEY, 4);
            startActivity(intent);
        }else{
            //唯一登录验证
            uniquelogin = new Uniquelogin(this,handler);
            uniquelogin.compareUniqueLoginToken("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=uniquelogin&m=socialchat");
        }
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
    private void initView() {
        mFragment.clear();

        iv_back_left = (ImageView)findViewById(R.id.iv_back_left);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mConversationList = initConversationList();//获取融云会话列表的对象
        iv_back_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {
            mFragment.add(mConversationList);//加入会话列表
            mFragment.add(FriendFragment.getInstance());
        }catch (Exception e){

        }

        //配置ViewPager的适配器
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        mViewPager.setAdapter(mFragmentPagerAdapter);



        //通知开启提示
        notification_hint_layout = findViewById(R.id.notification_hint_layout);
        open_note_tv = findViewById(R.id.open_note_tv);
        close_note_hint_tv = findViewById(R.id.close_note_hint_tv);


    }

    private Fragment initConversationList() {
        /**
         * appendQueryParameter对具体的会话列表做展示
         */
        if (mConversationFragment == null) {
            Log.d(TAG, "initConversationList: 不存在");
            ConversationListFragment listFragment = new ConversationListFragment();
            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationList")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")//设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")
                    // .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                    //.appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//公共服务号
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置私聊会是否聚合显示
                    .build();
            listFragment.setUri(uri);
            return listFragment;
        } else {
            Log.d(TAG, "initConversationList: 已存在");
            return mConversationFragment;
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是用户数据，2是幻灯片
            switch (msg.what){
                case 1:
                    try {
                        String resultJson1 = msg.obj.toString();
                        Log.d(TAG, "handleMessage: 用户数据接收的值"+msg.obj.toString());

                        JSONArray jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
                        initFriends(getWindow().getDecorView(),jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 11:
                    String resultJson1 = msg.obj.toString();
                    Log.d(TAG, "handleMessage: 用户数据接收的值"+msg.obj.toString());
                    SharedHelper shvalue = new SharedHelper(getApplicationContext());
                    String newFriendApplyNumber = shvalue.readNewFriendApplyNumber();
                    if (newFriendApplyNumber.equals(msg.obj.toString())){
                        //BadgeBottomNav.newFriendApplicationBadge(bottomNavigationView,msg.obj.toString(),getApplicationContext());
                    }else {
                        BadgeBottomNav.newFriendApplicationBadge(bottomNavigationView,msg.obj.toString(),getApplicationContext());
                    }
                    break;
                case 10:
                    if (msg.obj.toString().equals("false")){
                        uniquelogin.uniqueNotification();
                        SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("userID", "");
                        editor.commit();
                        Intent intent = new Intent(Main3Activity.this, SigninActivity.class);
                        intent.putExtra("uniquelogin","强制退出");
                        startActivity(intent);
                    }
                    break;
            }
        }
    };

    //TODO okhttp获取好友信息
    public void getDataFriends(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shvalue = new SharedHelper(getApplicationContext());
                String userID = shvalue.readUserInfo().get("userID");
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", userID)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=1;
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //TODO 初始化用户列表
    private void initFriends(View view, JSONArray jsonArray) throws JSONException {
        Log.d(TAG, "initFriends: ");
        List<UserInfo> userInfoList = new ArrayList<>();
        if (jsonArray.length()>0){
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);


                String nickname = jsonObject.getString("nickname");
                //替换备注
                if (xin.banghua.beiyuan.Common.friendsRemarkMap!=null){
                    for (Map.Entry<String, String> m : xin.banghua.beiyuan.Common.friendsRemarkMap.entrySet()) {//通过entrySet
                        System.out.println("key:" + m.getKey() + " value:" + m.getValue());
                        if (m.getKey().equals(jsonObject.getString("id"))){
                            nickname = m.getValue();
                        }
                    }
                }

                //好友信息
                FriendList friends = new FriendList(jsonObject.getString("id"), jsonObject.getString("portrait"), nickname, jsonObject.getString("age"), jsonObject.getString("gender"), jsonObject.getString("region"), jsonObject.getString("property"), jsonObject.getString("vip"),jsonObject.getString("svip"),jsonObject.getString("portraitframe"));
                friendList.add(filledData(friends));
                friendListMap.put(jsonObject.getString("id"),filledData(friends));

                UserInfo userInfo = new UserInfo(jsonObject.getString("id"), nickname, Uri.parse(Common.getOssResourceUrl(jsonObject.getString("portrait"))));
                RongIM.getInstance().refreshUserInfoCache(userInfo);
                userInfoList.add(userInfo);
            }
            MyContactCard myContactCard = new MyContactCard();
            myContactCard.setUserInfoList(userInfoList);
            myContactCard.initContactCard();
        }
        xin.banghua.beiyuan.Common.friendList = friendList;
        xin.banghua.beiyuan.Common.friendListMap = friendListMap;
    }

    /**
     * 将数据中的用户名拼音首字母加入数据数组
     *
     * @param data
     * @return
     */
    public static FriendList filledData(FriendList data) {
        //汉字转换成拼音   表情会报StringIndexOutOfBoundsException
        String pinyin = PinyinUtils.getPingYin(data.getmUserNickName());
        String sortString = "#";
        try {
            sortString = pinyin.substring(0, 1).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            data.setLetters(sortString.toUpperCase());
        } else {
            data.setLetters("#");
        }
        return data;
    }
    public void initNotification() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
        boolean isOpened = manager.areNotificationsEnabled();
        if (!isOpened){
            notification_hint_layout.setVisibility(View.VISIBLE);
            open_note_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("android.provider.extra.APP_PACKAGE", Main3Activity.this.getPackageName());
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //5.0
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("app_package", Main3Activity.this.getPackageName());
                        intent.putExtra("app_uid", Main3Activity.this.getApplicationInfo().uid);
                        startActivity(intent);
                    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {  //4.4
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + Main3Activity.this.getPackageName()));
                    } else if (Build.VERSION.SDK_INT >= 15) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", Main3Activity.this.getPackageName(), null));
                    }
                    startActivity(intent);
                }
            });
            close_note_hint_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notification_hint_layout.setVisibility(View.GONE);
                }
            });

//        SharedPreferences sp = getApplication().getSharedPreferences("firstStarted", Context.MODE_PRIVATE);
//        if (sp.getString("firstStarted", "null").equals("null")) {




//                new GlobalDialogSingle(this, "", "当前未获取通知权限", "去开启", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        //startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 33494);
//                        Intent intent = new Intent();
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
//                            intent.putExtra("android.provider.extra.APP_PACKAGE", Main3Activity.this.getPackageName());
//                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //5.0
//                            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
//                            intent.putExtra("app_package", Main3Activity.this.getPackageName());
//                            intent.putExtra("app_uid", Main3Activity.this.getApplicationInfo().uid);
//                            startActivity(intent);
//                        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {  //4.4
//                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            intent.addCategory(Intent.CATEGORY_DEFAULT);
//                            intent.setData(Uri.parse("package:" + Main3Activity.this.getPackageName()));
//                        } else if (Build.VERSION.SDK_INT >= 15) {
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                            intent.setData(Uri.fromParts("package", Main3Activity.this.getPackageName(), null));
//                        }
//                        startActivity(intent);
//                    }
//                }).show();


//            SharedPreferences.Editor editor = sp.edit();
//            editor.putString("firstStarted", "firstStarted");
//            editor.commit();
//            }
            //未打开通知
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("通知权限")
                    .setMessage("为了您能够及时的收到好友消息和系统通知，请在“通知”中打开权限！")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("android.provider.extra.APP_PACKAGE", Main3Activity.this.getPackageName());
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //5.0
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("app_package", Main3Activity.this.getPackageName());
                                intent.putExtra("app_uid", Main3Activity.this.getApplicationInfo().uid);
                                startActivity(intent);
                            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {  //4.4
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + Main3Activity.this.getPackageName()));
                            } else if (Build.VERSION.SDK_INT >= 15) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                intent.setData(Uri.fromParts("package", Main3Activity.this.getPackageName(), null));
                            }
                            startActivity(intent);
                        }
                    })
                    .create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }
    }

}
