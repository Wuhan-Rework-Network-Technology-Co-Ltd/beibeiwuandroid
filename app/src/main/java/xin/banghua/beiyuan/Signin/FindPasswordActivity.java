package xin.banghua.beiyuan.Signin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.R;

import static io.rong.imkit.fragment.ConversationFragment.TAG;

public class FindPasswordActivity extends AppCompatActivity {

    private Context mContext;
    //账号密码输入框
    EditText userAccount;
    EditText userPassword;
    EditText verificationCode;
    //验证码按钮和提交按钮
    Button verificationCode_btn,submit_btn;

    String userAcountString,userPasswordString,verificationCodeString;

    String smscode;

    Integer countDown = 60;
    //属性
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        userAccount = findViewById(R.id.userAccount);
        userPassword = findViewById(R.id.userPassword);
        verificationCode = findViewById(R.id.verificationCode);
        mContext = this;
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
                case 1:
                    Toast.makeText(mContext, "密码修改成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(FindPasswordActivity.this, SigninActivity.class);
                    startActivity(intent);
                    break;
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
            findPassword("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=findpassword&m=socialchat",userAcountString,userPasswordString);
        }else {
            Toast.makeText(mContext, "验证码错误", Toast.LENGTH_LONG).show();
        }
    }

    //TODO okhttp发送验证码
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
                    Log.d(TAG, "run: sendCode发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //TODO okhttp获取用户信息
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

    //TODO okhttp修改密码
    public void findPassword(final String url,final String userPhone,final String userPassword){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("userPhone", userPhone)
                        .add("userPassword", userPassword)
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
                    Log.d(TAG, "run: findPassword发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
