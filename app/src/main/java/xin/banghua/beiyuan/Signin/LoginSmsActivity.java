package xin.banghua.beiyuan.Signin;

import static xin.banghua.onekeylogin.Constant.THEME_KEY;
import static xin.banghua.onekeylogin.ModeSelectActivity.log_in_intent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.luozm.captcha.Captcha;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tuo.customview.VerificationCodeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SliderWebViewActivity;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;
import xin.banghua.onekeylogin.login.OneKeyLoginActivity;

public class LoginSmsActivity extends AppCompatActivity {



    Context mContext;


    @BindView(R.id.page_1)
    ConstraintLayout page_1;
    @BindView(R.id.captCha)
    Captcha captcha;
    @BindView(R.id.phone_et)
    EditText phone_et;
    @BindView(R.id.next_btn)
    Button next_btn;
    @BindView(R.id.privacypolity_check)
    CheckBox privacypolity_check;
    @BindView(R.id.useragreement_btn)
    Button useragreement_btn;
    @BindView(R.id.privacypolicy_btn)
    Button privacypolicy_btn;
    @BindView(R.id.wxLogin_btn)
    Button wxLogin_btn;
    @BindView(R.id.oneKeyLogin_btn)
    Button oneKeyLogin_btn;
    @BindView(R.id.password_btn)
    Button password_btn;


    @BindView(R.id.page_2)
    ConstraintLayout page_2;
    @BindView(R.id.phone_end_tv)
    TextView phone_end_tv;
    @BindView(R.id.codeView)
    VerificationCodeView codeView;
    @BindView(R.id.countDown_hint_tv)
    TextView countDown_hint_tv;
    @BindView(R.id.countDown_tv)
    TextView countDown_tv;


    @BindView(R.id.go_back_img)
    ImageView go_back_img;

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



    String userAccountString,userPasswordString,verificationCodeString;
    String smscode = "0000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sms);

        ButterKnife.bind(this);

        mContext= this;

        regToWx();

        privacypolicy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
                intent.putExtra("slidername","小贝乐园隐私政策");
                intent.putExtra("sliderurl","https://console.banghua.xin/privacypolicy.html");
                mContext.startActivity(intent);
            }
        });

        useragreement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
                intent.putExtra("slidername","小贝乐园用户协议");
                intent.putExtra("sliderurl","https://console.banghua.xin/useragreement.html");
                mContext.startActivity(intent);
            }
        });

        oneKeyLogin_btn.setOnClickListener(view -> startOneKeyLogIn());
        if (!getIntent().getBooleanExtra("from_one_key_login",false)){
            startOneKeyLogIn();
        }

        password_btn.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, SigninActivity.class);
            intent.putExtra("from_one_key_login",true);
            mContext.startActivity(intent);
        });


        wxLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test";
                api.sendReq(req);
            }
        });

        next_btn.setOnClickListener(view -> {
            userAccountString = phone_et.getText().toString();
            if (userAccountString.length()!=11){
                Toast.makeText(mContext, "请输入手机号", Toast.LENGTH_LONG).show();
                return;
            }
            if (!(privacypolity_check.isChecked())){
                Toast.makeText(mContext, "请勾选小贝乐园用户协议", Toast.LENGTH_LONG).show();
                return;
            }
            puzzle();
        });


        countDown_hint_tv.setClickable(false);
        countDown_hint_tv.setOnClickListener(view -> {
            OkHttpInstance.sendCode(userAccountString, new OkHttpResponseCallBack() {
                @Override
                public void getResponseString(String responseString) {
                    Toast.makeText(mContext, "验证码发送成功", Toast.LENGTH_LONG).show();
                    smscode = responseString;

                    countDown = 60;
                    countDown();
                }
            });
        });


        go_back_img.setOnClickListener(view -> {
            startOneKeyLogIn();
        });
    }

    /**
     * 直接开启一键登录
     */
    private void startOneKeyLogIn(){
        Intent intent = new Intent(mContext, OneKeyLoginActivity.class);
        intent.putExtra(THEME_KEY, 4);
        startActivity(intent);
    }



    public void puzzle(){
        captcha = (Captcha) findViewById(R.id.captCha);
        captcha.setVisibility(View.VISIBLE);
        captcha.setMaxFailedCount(5);
        captcha.setCaptchaListener(new Captcha.CaptchaListener() {
            @Override
            public String onAccess(long time) {
                Toast.makeText(LoginSmsActivity.this,"验证成功",Toast.LENGTH_SHORT).show();
                OkHttpInstance.sendCode(userAccountString, new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        Toast.makeText(mContext, "验证码发送成功", Toast.LENGTH_LONG).show();
                        smscode = responseString;

                        page_1.setVisibility(View.GONE);
                        captcha.setVisibility(View.GONE);
                        page_2.setVisibility(View.VISIBLE);
                        phone_end_tv.setText(userAccountString.substring(7));
                        countDown();

                        verifyCode();
                    }
                });
                return "验证通过,耗时"+time+"毫秒";
            }

            @Override
            public String onFailed(int failedCount) {
                Toast.makeText(LoginSmsActivity.this,"验证失败",Toast.LENGTH_SHORT).show();
                captcha.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        captcha.reset(false);
                    }
                },1000);
                return "验证失败,已失败"+failedCount+"次";
            }

            @Override
            public String onMaxFailed() {
                Toast.makeText(LoginSmsActivity.this,"验证超过次数，你的帐号被封锁",Toast.LENGTH_SHORT).show();
                return "验证失败,帐号已封锁";
            }
        });
    }


    public void verifyCode(){
        codeView.setPwdMode(false);
        codeView.setInputCompleteListener(new VerificationCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                if (codeView.getInputContent().length()==4){
                    if (codeView.getInputContent().equals(smscode)){
                        OkHttpInstance.smsLogin(userAccountString, new OkHttpResponseCallBack() {
                            @Override
                            public void getResponseString(String responseString) {
                                log_in_intent.putExtra("login_result",responseString);
                                startActivity(log_in_intent);
                            }
                        });
                    }else {
                        Toast.makeText(LoginSmsActivity.this,"验证码错误，请重新输入",Toast.LENGTH_SHORT).show();
                        codeView.clearInputContent();
                    }
                }
            }

            @Override
            public void deleteContent() {

            }
        });
    }

    Integer countDown = 60;
    //TODO okhttp获取用户信息
    public void countDown(){
        new Thread(new Runnable() {
            @Override
            public void run(){

                while (countDown != 0) {
                    countDown--;
                    try {
                        Thread.sleep(1000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                countDown_tv.setText("("+countDown+"s)");

                                if (countDown<1){
                                    countDown_hint_tv.setClickable(true);
                                    countDown_tv.setText("");
                                }else {
                                    countDown_hint_tv.setClickable(false);
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }
}