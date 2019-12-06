package xin.banghua.beiyuan.Main5Branch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Adapter.FriendList;
import xin.banghua.beiyuan.BadgeBottomNav;
import xin.banghua.beiyuan.Main3Activity;
import xin.banghua.beiyuan.ParseJSON.ParseJSONArray;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.RongYunContactCard.MyContactCard;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.Signin.SigninActivity;

public class CommonSettingActivity extends AppCompatActivity {
    private static final String TAG = "CommonSettingActivity";
    Button clearCache,clearChat;

    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_setting);
        mContext = this;
        ImageView back_btn = findViewById(R.id.iv_back_left);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        initButton();
    }

    private void initButton() {
        clearCache = findViewById(R.id.clearCache);
        clearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "清除缓存", Toast.LENGTH_LONG).show();
            }
        });
        clearChat = findViewById(R.id.clearChat);
        clearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFriends("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=friends&m=socialchat");
                Toast.makeText(mContext, "清除聊天记录", Toast.LENGTH_LONG).show();
            }
        });
    }


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
                RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE,jsonObject.getString("id"),new io.rong.imlib.RongIMClient.ResultCallback(){

                    @Override
                    public void onSuccess(Object o) {

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
                RongIM.getInstance().deleteMessages(Conversation.ConversationType.PRIVATE, jsonObject.getString("id"), new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
            }
            MyContactCard myContactCard = new MyContactCard();
            myContactCard.setUserInfoList(userInfoList);
            myContactCard.initContactCard();
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
            }
        }
    };
}
