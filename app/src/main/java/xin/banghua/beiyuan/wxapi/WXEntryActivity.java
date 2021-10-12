package xin.banghua.beiyuan.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Main3Branch.RongyunConnect;
import xin.banghua.beiyuan.MainActivity;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.PushPackage.PushClass;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.Signin.SigninActivity;
import xin.banghua.beiyuan.Signin.Userset;
import xin.banghua.beiyuan.Uniquelogin;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXEntryActivity";
    private Context mContext;
    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    private static final String APP_ID = "wxef862b4ad2079599";

    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;

    String nickname,portrait,openid,gender,region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        Log.d(TAG, "onCreate: ");

        api = WXAPIFactory.createWXAPI(this, APP_ID, false);
        try {
            Intent intent = getIntent();
            api.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.d(TAG, "onReq: ");

    }

    @Override
    public void onResp(BaseResp baseResp) {
        Toast.makeText(this, "登陆中......", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onResp: ");
        if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            SendAuth.Resp authResp = (SendAuth.Resp)baseResp;
            final String code = authResp.code;
            Log.d(TAG, "onResp: "+code);
            getAccessToken(String.format("https://api.weixin.qq.com/sns/oauth2/access_token?" +
                            "appid=%s&secret=%s&code=%s&grant_type=authorization_code", "wxef862b4ad2079599",
                    "efef7c0891675d0b1979d5dee348a88d", code));
        }
        if(baseResp.getType()==ConstantsAPI.COMMAND_PAY_BY_WX){
            Log.d(TAG,"onPayFinish,errCode="+baseResp.errCode);
            Log.d(TAG, "onResp: "+baseResp.toString());
        }
        finish();
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
                        Log.d(TAG, "handleMessage: 幻灯片接收的值"+msg.obj.toString());
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        Log.d(TAG, "handleMessage: access_token"+jsonObject.getString("access_token"));
                        Log.d(TAG, "handleMessage:expires_in "+jsonObject.getString("expires_in"));
                        Log.d(TAG, "handleMessage: refresh_token"+jsonObject.getString("refresh_token"));
                        Log.d(TAG, "handleMessage: openid"+jsonObject.getString("openid"));
                        Log.d(TAG, "handleMessage: scope"+jsonObject.getString("scope"));
                        Log.d(TAG, "handleMessage: unionid"+jsonObject.getString("unionid"));
                        getDataUserinfo(String.format("https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s",jsonObject.getString("access_token"),jsonObject.getString("openid")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        Log.d(TAG, "handleMessage: 微信用户信息"+msg.obj.toString());
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        Log.d(TAG, "handleMessage: nickname："+jsonObject.getString("nickname"));
                        Log.d(TAG, "handleMessage: headimgurl： "+jsonObject.getString("headimgurl"));
                        Log.d(TAG, "handleMessage: openid："+jsonObject.getString("openid"));
                        //微信已获取到用户信息，现在需要保存到数据库
                        portrait = jsonObject.getString("headimgurl");
                        //
                        if (!portrait.startsWith("https")){
                            StringBuilder strBuilder = new StringBuilder(portrait);
                            strBuilder.insert(4, 's');
                            portrait=strBuilder.toString();
                        }

                        nickname = jsonObject.getString("nickname");
                        openid = jsonObject.getString("openid");

                        saveUserinfo("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=weinxinregister&m=socialchat",openid,nickname,portrait);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    //不跳转个人设置，直接保存用户数据，然后跳转MainActivity   2019/8/24 需要调整个人设置,保存后跳转个人设置
                    mContext = getApplicationContext();
                    JSONObject jsonObject = null;//自定义的
                    try {
                        jsonObject = new ParseJSONObject(msg.obj.toString()).getParseJSON();

                        if (jsonObject.getString("type").equals("3")){
                            //被禁用，跳转登录页
                            Log.d(TAG, "handleMessage: forbidtime"+jsonObject.get("forbidtime"));
                            Intent intent1 = new Intent(WXEntryActivity.this, SigninActivity.class);
                            intent1.putExtra("forbidtime",jsonObject.get("forbidtime")+"");
                            intent1.putExtra("forbidreason",jsonObject.get("forbidreason")+"");
                            startActivity(intent1);
                        }
                        //不管登陆过没有，都保存本地数据
                        SharedHelper sh = new SharedHelper(mContext);
                        sh.saveUserInfo(jsonObject.getString("id"),jsonObject.getString("nickname"),jsonObject.getString("portrait"),"20","男","z","中国");
                        if (jsonObject.getString("type").equals("1")){
                            //更新redis缓存
                            updateRedisCache(jsonObject.getString("id"));
                            //注册融云
                            //跳转首页
                            postRongyunUserRegisterExist("https://console.banghua.xin/otherinterface/rongyun/RongCloudNew/example/User/userregister.php",jsonObject.getString("id"),jsonObject.getString("nickname"),jsonObject.getString("portrait"));
                        }else if (jsonObject.getString("type").equals("2")){
                            //更新redis缓存
                            updateRedisCache(jsonObject.getString("id"));
                            //不存在，注册融云，然后跳转设置页
                            postRongyunUserRegister("https://console.banghua.xin/otherinterface/rongyun/RongCloudNew/example/User/userregister.php",jsonObject.getString("id"),jsonObject.getString("nickname"),jsonObject.getString("portrait"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    Log.d(TAG, "handleMessage: 4");
                    try {
                        JSONObject object1 = new JSONObject(msg.obj.toString());
                        Log.d("融云token获取",object1.getString("code"));
                        if (object1.getString("code").equals("200")){

                            //保存融云token
                            Log.d("融云token",object1.getString("token"));
                            mContext = getApplicationContext();
                            SharedHelper sh1 = new SharedHelper(mContext);
                            sh1.saveRongtoken(object1.getString("token"));
                            //链接融云
                            RongyunConnect rongyunConnect = new RongyunConnect();
                            rongyunConnect.connect(object1.getString("token"));
                            //跳转首页
                            //Log.d("跳转首页",object1.getString("userNickName"));
                            //Intent intent = new Intent(WXEntryActivity.this, MainActivity.class);
                            //startActivity(intent);
                            //跳转设置页
                            Intent intent1 = new Intent(WXEntryActivity.this, Userset.class);
                            intent1.putExtra("logtype","2");
                            intent1.putExtra("openid",openid);
                            startActivity(intent1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    Log.d(TAG, "handleMessage: 5");
                    try {
                        JSONObject object2 = new JSONObject(msg.obj.toString());
                        Log.d("融云token获取",object2.getString("code"));
                        if (object2.getString("code").equals("200")){

                            //保存融云token
                            Log.d("融云token",object2.getString("token"));
                            mContext = getApplicationContext();
                            SharedHelper sh1 = new SharedHelper(mContext);
                            sh1.saveRongtoken(object2.getString("token"));
                            //链接融云
                            RongyunConnect rongyunConnect = new RongyunConnect();
                            rongyunConnect.connect(object2.getString("token"));
                            //跳转首页
                            Intent intent = new Intent(WXEntryActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    //TODO okhttp获取用户信息
    public void getAccessToken(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("type", "getUserInfo")
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
    public void getDataUserinfo(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("type", "getUserInfo")
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=2;
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //TODO okhttp保存用户数据到数据库
    public void saveUserinfo(final String url, final String openid,final String nickname,final String portrait){
        new Thread(new Runnable() {
            @Override
            public void run(){
                mContext = getApplicationContext();
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("openid", openid)
                        .add("nickname", nickname)
                        .add("portrait", portrait)
                        .add("uniquelogintoken",new Uniquelogin(mContext,handler).saveToken())
                        .add("phonebrand", PushClass.phoneBrand)
                        .add("pushregid",PushClass.pushRegID)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=3;
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
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
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("phonebrand", PushClass.phoneBrand)
                        .add("pushregid",PushClass.pushRegID)
                        .add("frontorback","1")
                        .build();
                Request request = new Request.Builder()
                        .url("https://redis.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=xiaobeisignin&m=rediscache")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //TODO 登录 form形式的post
    public void postRongyunUserRegister(final String url, final String userID, final String userNickName,final String userPortrait){
        new Thread(new Runnable() {
            @Override
            public void run(){
                Log.d("融云注册信息","进入融云注册");
                OkHttpClient client = new OkHttpClient();
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
                    message.what=4;
                    JSONObject jsonObject = new ParseJSONObject(message.obj.toString()).getParseJSON();
                    Log.d("融云注册信息token",jsonObject.getString("token"));
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //TODO 登录 form形式的post
    public void postRongyunUserRegisterExist(final String url, final String userID, final String userNickName,final String userPortrait){
        new Thread(new Runnable() {
            @Override
            public void run(){
                Log.d("融云注册信息","进入融云注册");
                OkHttpClient client = new OkHttpClient();
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
                    message.what=5;
                    JSONObject jsonObject = new ParseJSONObject(message.obj.toString()).getParseJSON();
                    Log.d("融云注册信息token",jsonObject.getString("token"));
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
