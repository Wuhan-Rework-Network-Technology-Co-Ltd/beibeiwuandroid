package xin.banghua.beiyuan.Signin;


import static io.rong.imkit.fragment.ConversationFragment.TAG;
import static xin.banghua.onekeylogin.Constant.THEME_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.LaunchActivity;
import xin.banghua.beiyuan.Main3Branch.RongyunConnect;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.PushPackage.PushClass;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.SliderWebViewActivity;
import xin.banghua.beiyuan.Uniquelogin;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.onekeylogin.login.OneKeyLoginActivity;

public class SigninActivity extends Activity {
    private Context mContext;
    Response response = null;

    //登录账号密码
    EditText userAccount;
    EditText userPassword;

    //三个按钮
    private Button signIn,signUp,findPassword,wxLogin_btn,privacypolity_btn,useragreement_btn,oneKeyLogin_btn;
    CheckBox privacypolicy_check;
    //okhttp

    //微信
    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    private static final String APP_ID = "wxef862b4ad2079599";
    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;
    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);

        // 将应用的appId注册到微信
        api.registerApp(APP_ID);

        //建议动态监听微信启动广播进行注册到微信
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // 将该app注册到微信
                api.registerApp(APP_ID);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        regToWx();
        mContext = this;

        useragreement_btn = findViewById(R.id.useragreement_btn);
        useragreement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
                intent.putExtra("slidername","小贝乐园用户协议");
                intent.putExtra("sliderurl","https://console.banghua.xin/useragreement.html");
                mContext.startActivity(intent);
            }
        });
        privacypolity_btn = findViewById(R.id.privacypolicy_btn);
        privacypolity_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
                intent.putExtra("slidername","小贝乐园隐私政策");
                intent.putExtra("sliderurl","https://console.banghua.xin/privacypolicy.html");
                mContext.startActivity(intent);
            }
        });
        privacypolicy_check = findViewById(R.id.privacypolity_check);

        Intent intent = getIntent();
        String uniquelogin = intent.getStringExtra("uniquelogin");
        String forbidtime = intent.getStringExtra("forbidtime");
        String forbidreason = intent.getStringExtra("forbidreason");
        if (uniquelogin!=null){
            Toast.makeText(this, "您的账号在其他设备登录，强制退出", Toast.LENGTH_LONG).show();
        }
        if (!TextUtils.isEmpty(forbidtime)){
            Log.d(TAG, "onCreate: forbidtime"+forbidtime);
            Toast toast = Toast.makeText(getApplicationContext(), "已被封禁"+forbidtime+"天。"+forbidreason, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        signIn = (Button) findViewById(R.id.signin_btn);
        signUp = (Button) findViewById(R.id.signup_btn);
        wxLogin_btn = findViewById(R.id.wxLogin_btn);
        findPassword = findViewById(R.id.findPassword_btn);
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this,FindPasswordActivity.class);
                startActivity(intent);
            }
        });
        findPassword = (Button) findViewById(R.id.findPassword_btn);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userAccount = (EditText) findViewById(R.id.userAccount);
                userPassword = (EditText) findViewById(R.id.userPassword);
                if(userAccount.getText().toString().equals("")||userPassword.getText().toString().equals("")){
                    Toast.makeText(mContext, "请输入账号密码", Toast.LENGTH_LONG).show();
                }else{
                    if (!(privacypolicy_check.isChecked())){
                        Toast.makeText(mContext, "勾选小贝乐园用户协议", Toast.LENGTH_LONG).show();
                        return;
                    }
                    signIn.setClickable(false);
                    Toast.makeText(mContext, "登陆中", Toast.LENGTH_LONG).show();
                    postSignIn("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=signin&m=socialchat",userAccount.getText().toString(),userPassword.getText().toString());
                }

            }
        });


        wxLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(privacypolicy_check.isChecked())){
                    Toast.makeText(mContext, "勾选小贝乐园用户协议", Toast.LENGTH_LONG).show();
                    return;
                }

                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test";
                api.sendReq(req);
            }
        });


        oneKeyLogin_btn = findViewById(R.id.oneKeyLogin_btn);
        oneKeyLogin_btn.setOnClickListener(view -> {
            Intent intent1 = new Intent(mContext, OneKeyLoginActivity.class);
            intent1.putExtra(THEME_KEY, 4);
            startActivity(intent1);
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    signIn.setClickable(true);
                    String resultJson = msg.obj.toString();
                    try {
                        JSONObject object = new JSONObject(resultJson);
                        Log.d("进入handler",object.getString("error"));
                        if (object.getString("error").equals("0")){
                            //通知
                            Log.d("发送通知",object.getString("info"));
                            Toast.makeText(mContext,object.getString("info"),Toast.LENGTH_LONG).show();
                            //保存用户数据
                            Log.d("保存用户数据",object.getString("userID"));
                            mContext = getApplicationContext();
                            SharedHelper sh = new SharedHelper(mContext);
                            sh.saveUserInfo(object.getString("userID"),object.getString("userNickName"),object.getString("userPortrait"),object.getString("userAge"),object.getString("userGender"),object.getString("userProperty"),object.getString("userRegion"));
                            //更新redis缓存
                            updateRedisCache(object.getString("userID"));
                            //判断token是否存在
                            postRongyunUserRegister("https://console.banghua.xin/otherinterface/rongyun/RongCloudNew/example/User/userregister.php",object.getString("userID"),object.getString("userNickName"),object.getString("userPortrait"));

                        }else {
                            signIn.setClickable(true);
                            Log.d(TAG, "handleMessage: test");
                            Toast.makeText(SigninActivity.this,object.getString("info"),Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    String resultJson1 = msg.obj.toString();
                    try {
                        JSONObject object1 = new JSONObject(resultJson1);
                        Log.d("融云token获取",object1.getString("code"));
                        if (object1.getString("code").equals("200")){

                            //保存融云token
                            Log.d("融云token",object1.getString("token"));
                            mContext = getApplicationContext();
                            SharedHelper sh = new SharedHelper(mContext);
                            sh.saveRongtoken(object1.getString("token"));

                            //链接融云
                            RongyunConnect rongyunConnect = new RongyunConnect();
                            rongyunConnect.connect(object1.getString("token"));
                            //跳转首页
                            //Log.d("跳转首页",object1.getString("userNickName"));
                            Intent intent = new Intent(SigninActivity.this, LaunchActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }

        }
    };

    //TODO 登录 form形式的post
    public void postSignIn(final String url, final String userAccount, final String userPassword){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("userAccount", userAccount)
                        .add("userPassword",userPassword)
                        .add("uniquelogintoken",new Uniquelogin(mContext,handler).saveToken())
                        .add("phonebrand", PushClass.phoneBrand)
                        .add("pushregid",PushClass.pushRegID)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    Log.d(TAG, "run: 看看"+response.toString());
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    //Log.d("form形式的post",response.body().string());
                    //格式：{"error":"0","info":"登陆成功"}
                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=1;
                    JSONObject jsonObject = new ParseJSONObject(message.obj.toString()).getParseJSON();
                    Log.d("登录信息",jsonObject.getString("info"));
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //TODO 登录 form形式的post
    public void updateRedisCache(String myid){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("phonebrand", PushClass.phoneBrand)
                        .add("pushregid",PushClass.pushRegID)
                        .add("frontorback","1")
                        .build();
                Request request = new Request.Builder()
                        .url("https://redis.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=xiaobeisignin&m=rediscache")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //TODO 登录 注册融云
    public void postRongyunUserRegister(final String url, final String userID, final String userNickName,final String userPortrait){
        new Thread(new Runnable() {
            @Override
            public void run(){
                Log.d("融云注册信息","进入融云注册");
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("userID", userID)
                        .add("userNickName",userNickName)
                        .add("userPortrait",userPortrait)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    //Log.d(TAG, "run: 融云注册信息返回"+response.toString());
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    //Log.d("融云注册信息返回",response.body().string());
                    //格式：{"error":"0","info":"登陆成功"}
                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    Log.d("融云注册信息返回",message.obj.toString());
                    message.what=2;
                    JSONObject jsonObject = new ParseJSONObject(message.obj.toString()).getParseJSON();
                    Log.d("融云注册信息token",jsonObject.getString("token"));
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //禁用返回键  *取消禁用
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return super.onKeyUp(keyCode,event);// return false 或者return true 都不会走onBackPressed了
        return false;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return super.onKeyDown(keyCode, event);// 不拦截，如果这里拦截了，也不会走到onBackPressed方法了
        return false;
    }


    public void onBack(View view){
        onBackPressed();
    }
}
