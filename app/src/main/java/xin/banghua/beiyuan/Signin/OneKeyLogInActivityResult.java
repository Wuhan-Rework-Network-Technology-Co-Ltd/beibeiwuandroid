package xin.banghua.beiyuan.Signin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.LaunchActivity;
import xin.banghua.beiyuan.PushPackage.PushClass;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;


public class OneKeyLogInActivityResult extends AppCompatActivity {
    private static final String TAG = "OneKeyLogInActivityResult";

    Context mContext;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_key_log_in);
        mContext = this;

        String login_result = getIntent().getStringExtra("login_result");

        Log.d(TAG, "onCreate: 一键登录结果"+login_result);

        SharedHelper sh = new SharedHelper(mContext);

        JSONObject object = null;//原生的
        try {
            object = new JSONObject(login_result);

            String error = object.getString("error");
            Toast.makeText(mContext,object.getString("info"),Toast.LENGTH_LONG).show();
            switch (error){
                case "-1"://注册
                    Log.d(TAG, "run: 一键登录成功，跳转设置"+object.getString("userAccount"));
                    intent = new Intent(mContext, Userset.class);
                    intent.putExtra("logtype","1");
                    intent.putExtra("userAccount",object.getString("userAccount"));
                    intent.putExtra("userPassword","");
                    startActivity(intent);
                    break;
                case "1"://封禁
                    intent = new Intent(mContext,SigninActivity.class);
                    startActivity(intent);
                    break;
                case "0"://登陆成功
                    Log.d(TAG, "run: 一键登录成功，跳转首页"+object.getString("userID"));
                    sh.saveUserInfoID(object.getString("userID"));
                    intent = new Intent(mContext, LaunchActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //TODO 登录 form形式的post
    public void updateRedisCache(String myid){
         new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("phonebrand", PushClass.phoneBrand)
                        .add("pushregid",PushClass.pushRegID)
                        .add("frontorback","1")
                        .build();
                Request request = new Request.Builder()
                        .url("https://redis.banghua.xin/app/index.php?i=888&c=entry&a=webapp&do=moyuansignin&m=rediscache")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}