package xin.banghua.beiyuan.Signin;

import static io.rong.imkit.fragment.ConversationFragment.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SliderWebViewActivity;

public class SignupActivity extends Activity {
    private Context mContext;
    //账号密码输入框
    EditText userAccount;
    EditText userPassword;
    EditText verificationCode;
    //验证码按钮和提交按钮
    Button verificationCode_btn,submit_btn,privacypolity_btn,useragreement_btn;

    String userAcountString,userPasswordString,verificationCodeString;

    String smscode = "0000";

    Integer countDown = 60;

    CheckBox privacypolicy_check;
    //属性
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        userAccount = findViewById(R.id.userAccount);
        userPassword = findViewById(R.id.userPassword);
        verificationCode = findViewById(R.id.verificationCode);
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

        verificationCode_btn = findViewById(R.id.verificationCode_btn);
        verificationCode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAcountString = userAccount.getText().toString();
                if (userAcountString.length()!=11){
                    Toast.makeText(mContext, "请输入手机号", Toast.LENGTH_LONG).show();
                    return;
                }
                sendCode("https://www.banghua.xin/sms.php",userAcountString);
                countDown();
            }
        });
        submit_btn = findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userAcountString = userAccount.getText().toString();
                userPasswordString = userPassword.getText().toString();
                verificationCodeString = verificationCode.getText().toString();
                if (userAcountString.equals("")){
                    Toast.makeText(mContext, "请输入账号密码", Toast.LENGTH_LONG).show();
                    return;
                }
                if (userPasswordString.equals("")){
                    Toast.makeText(mContext, "请输入账号密码", Toast.LENGTH_LONG).show();
                    return;
                }
                if (verificationCodeString.equals("")){
                    Toast.makeText(mContext, "请输入验证码", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!(privacypolicy_check.isChecked())){
                    Toast.makeText(mContext, "勾选小贝乐园用户协议", Toast.LENGTH_LONG).show();
                    return;
                }

                verificationCode(verificationCodeString);
            }
        });
    }




//处理返回的数据
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 2:
                    Toast.makeText(mContext, "验证码发送成功", Toast.LENGTH_LONG).show();
                    smscode = msg.obj.toString();
                    break;
                case 3:
                    if (msg.arg1==0){
                        verificationCode_btn.setText("获取验证码");
                        verificationCode_btn.setEnabled(true);
                        countDown = 60;
                    }else {
                        verificationCode_btn.setText(msg.arg1+"");
                        verificationCode_btn.setEnabled(false);
                    }
                    break;
            }
        }
    };
    //TODO okhttp验证信息
    public void verificationCode(final String verificationCodeString){
                if (smscode.equals(verificationCodeString)){
                    Intent intent = new Intent(SignupActivity.this, Userset.class);
                    intent.putExtra("logtype","1");
                    intent.putExtra("userAccount",userAcountString);
                    intent.putExtra("userPassword",userPasswordString);
                    startActivity(intent);
                }else {
                    Toast.makeText(mContext, "验证码错误", Toast.LENGTH_LONG).show();
                }
    }

    //TODO okhttp获取用户信息
    public void sendCode(final String url,final String phoneNumber){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("phoneNumber", phoneNumber)
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

    //TODO 倒计时
    public void countDown(){
        new Thread(new Runnable() {
            @Override
            public void run(){
                while (countDown != 0) {
                    countDown--;
                    try {
                        Thread.sleep(1000);
                        Message message=handler.obtainMessage();
                        message.arg1=countDown;
                        message.what=3;
                        handler.sendMessageDelayed(message,10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }
}
