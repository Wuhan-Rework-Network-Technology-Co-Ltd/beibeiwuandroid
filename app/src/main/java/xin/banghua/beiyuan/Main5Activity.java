package xin.banghua.beiyuan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import io.rong.imkit.RongBaseNoActionbarActivity;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.Signin.SigninActivity;

public class Main5Activity extends AppCompatActivity {
    Uniquelogin uniquelogin;
    private TextView mTextMessage;
    //未读信息监听相关
    private BottomNavigationView bottomNavigationView;
    private IUnReadMessageObserver iUnReadMessageObserver;
    private TextView unreadNumber;
    private static final String TAG = "Main5Activity";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_shenbian:

                    Intent intent1 = new Intent(Main5Activity.this, MainActivity.class);
                    startActivity(intent1);
                    return true;
                case R.id.navigation_haoyou:

                    Intent intent2 = new Intent(Main5Activity.this, Main2Activity.class);
                    startActivity(intent2);
                    return true;
                case R.id.navigation_xiaoxi:

                    Intent intent3 = new Intent(Main5Activity.this, Main3Activity.class);
                    startActivity(intent3);
                    return true;
                case R.id.navigation_dongtai:

                    Intent intent4 = new Intent(Main5Activity.this, Main4Activity.class);
                    startActivity(intent4);
                    return true;
                case R.id.navigation_wode:

                    //Intent intent5 = new Intent(Main5Activity.this, Main5Activity.class);
                    //startActivity(intent5);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        ifSignin();
        mTextMessage = (TextView) findViewById(R.id.message);

        //底部导航初始化和配置监听，默认选项
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.navigation_wode);
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
    }
    public void ifSignin(){
        Map<String,String> userInfo;
        SharedHelper sh;
        sh = new SharedHelper(getApplicationContext());
        userInfo = sh.readUserInfo();
        //Toast.makeText(mContext, userInfo.toString(), Toast.LENGTH_SHORT).show();
        if(userInfo.get("userID")==""){
            Intent intentSignin = new Intent(Main5Activity.this, SigninActivity.class);
            startActivity(intentSignin);
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

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是用户数据，2是幻灯片
            switch (msg.what){
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
                        Intent intent = new Intent(Main5Activity.this, SigninActivity.class);
                        intent.putExtra("uniquelogin","强制退出");
                        startActivity(intent);
                    }
                    break;
            }
        }
    };


}
