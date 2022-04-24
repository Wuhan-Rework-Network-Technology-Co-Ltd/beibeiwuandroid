package xin.banghua.beiyuan.Main5Branch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.CheckPermission;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.chat.MessageReceivedForegroundService;
import xin.banghua.beiyuan.utils.OkHttpInstance;

public class PrivateSettingActivity extends AppCompatActivity {

    private static final String TAG = "PrivateSettingActivity";

    Switch switch1,switch2,switch3,switch4,switch5,switch6,switch7;

    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_setting);
        mContext = this;
        ImageView back_btn = findViewById(R.id.iv_back_left);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        switch4 = findViewById(R.id.switch4);
        switch5 = findViewById(R.id.switch5);
        switch6 = findViewById(R.id.switch6);
        switch7 = findViewById(R.id.switch7);

        getPrivateSetting("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getprivatesetting&m=socialchat");
    }

    private void initSwitch() {
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(mContext, "开启", Toast.LENGTH_LONG).show();
                    setPrivateSetting("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=setprivatesetting&m=socialchat","group");
                }else {
                    Toast.makeText(mContext, "关闭", Toast.LENGTH_LONG).show();
                    setPrivateSetting("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=setprivatesetting&m=socialchat","group");
                }
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(mContext, "开启", Toast.LENGTH_LONG).show();
                    setPrivateSetting("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=setprivatesetting&m=socialchat","location");
                }else {
                    Toast.makeText(mContext, "关闭", Toast.LENGTH_LONG).show();
                    setPrivateSetting("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=setprivatesetting&m=socialchat","location");
                }
            }
        });

        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(mContext, "开启", Toast.LENGTH_LONG).show();
                    setPrivateSetting("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=setprivatesetting&m=socialchat","status");
                }else {
                    Toast.makeText(mContext, "关闭", Toast.LENGTH_LONG).show();
                    setPrivateSetting("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=setprivatesetting&m=socialchat","status");
                }
            }
        });

        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(mContext, "开启", Toast.LENGTH_LONG).show();
                    setPrivateSetting("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=setprivatesetting&m=socialchat","friend");
                }else {
                    Toast.makeText(mContext, "关闭", Toast.LENGTH_LONG).show();
                    setPrivateSetting("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=setprivatesetting&m=socialchat","friend");
                }
            }
        });

        switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(mContext, "开启", Toast.LENGTH_LONG).show();
                    setPrivateSetting("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=setprivatesetting&m=socialchat","svip");
                }else {
                    Toast.makeText(mContext, "关闭", Toast.LENGTH_LONG).show();
                    setPrivateSetting("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=setprivatesetting&m=socialchat","svip");
                }
            }
        });

        Boolean isAutoPlay = SharedHelper.getInstance(mContext).readAutoPlay();
        switch6.setChecked(isAutoPlay);
        switch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(mContext, "开启", Toast.LENGTH_LONG).show();
                    SharedHelper.getInstance(mContext).saveAutoPlay(true);
                }else {
                    Toast.makeText(mContext, "关闭", Toast.LENGTH_LONG).show();
                    SharedHelper.getInstance(mContext).saveAutoPlay(false);
                }
            }
        });



        Intent mForegroundService = new Intent(this, MessageReceivedForegroundService.class);
        if (SharedHelper.getInstance(mContext).readForegroundNotificationSetting()){
            switch7.setChecked(true);
        }else {
            switch7.setChecked(false);
        }
        switch7.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                // 启动服务
                //权限检测
                if (!CheckPermission.verifyPushPermission(PrivateSettingActivity.this)){
                    compoundButton.setChecked(false);
                }else {
                    if (!MessageReceivedForegroundService.serviceIsLive) {
                        Log.d(TAG, "onCreate: 开始前端通知");
                        // Android 8.0使用startForegroundService在前台启动新服务
                        mForegroundService.putExtra("Foreground", "This is a foreground service.");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(mForegroundService);
                        } else {
                            startService(mForegroundService);
                        }
                    } else {
                        Toast.makeText(this, "前台服务正在运行中...", Toast.LENGTH_SHORT).show();
                    }
                    SharedHelper.getInstance(mContext).saveForegroundNotificationSetting(true);
                }
            }else {
                if (mForegroundService!=null){
                    stopService(mForegroundService);
                }

                SharedHelper.getInstance(mContext).saveForegroundNotificationSetting(false);
            }
        });
    }



    //网络数据部分
//处理返回的数据
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

                        JSONObject jsonObject = new ParseJSONObject(msg.obj.toString()).getParseJSON();
                        if (jsonObject.getString("allowgroup").equals("1")){
                            switch1.setChecked(true);
                        }else {
                            switch1.setChecked(false);
                        }
                        if (jsonObject.getString("allowlocation").equals("1")){
                            switch2.setChecked(true);
                        }else {
                            switch2.setChecked(false);
                        }
                        if (jsonObject.getString("allowstatus").equals("1")){
                            switch3.setChecked(true);
                        }else {
                            switch3.setChecked(false);
                        }
                        if (jsonObject.getString("allowfriend").equals("1")){
                            switch4.setChecked(true);
                        }else {
                            switch4.setChecked(false);
                        }
                        if (jsonObject.getString("allowsvip").equals("1")){
                            switch5.setChecked(true);
                        }else {
                            switch5.setChecked(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    initSwitch();
                    break;
                case 2:

                    break;
            }
        }
    };
    //TODO okhttp获取用户信息
    public void getPrivateSetting(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(mContext);
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("userid", myid)
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

    //TODO okhttp获取用户信息
    public void setPrivateSetting(final String url, final String type){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(mContext);
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("userid", myid)
                        .add("type",type)
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
}
