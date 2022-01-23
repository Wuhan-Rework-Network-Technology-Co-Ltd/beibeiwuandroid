package xin.banghua.beiyuan;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.baidu.mobads.sdk.api.SplashAd;
import com.orhanobut.dialogplus.DialogPlus;

import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.custom_ui.ad.CommonCallBackInterfaceWithSlashAd;
import xin.banghua.beiyuan.custom_ui.ad.SplashAdFrameLayout;
import xin.banghua.beiyuan.utils.Common;
import xin.banghua.beiyuan.utils.MD5Tool;
import xin.banghua.beiyuan.utils.OkHttpInstance;

import static xin.banghua.beiyuan.custom_ui.ad.SplashAdFrameLayout.isClickAd;

public class LaunchActivity extends Activity {
    private static final String TAG = "SplashScreenSampleActivity";

    ImageView launchImage;

    Context mContext;


    SplashAdFrameLayout splashAdFrameLayout;

    @Override
    protected void onResume() {
        super.onResume();
        if (!SharedHelper.getInstance(this).readPrivateAgreement()){
            Log.d(TAG, "onResume: 弹出隐私协议");
            private_policy_dialog();
        }else {
            //初始化第三方SDK
            App.initThirdSDK();

            OkHttpInstance.isShowAD(responseString -> {
                if (responseString.equals("no")){
                    intentMain(2);
                }else {
                    //开屏广告
                    splashAdFrameLayout = findViewById(R.id.splashAdFrameLayout);
                    splashAdFrameLayout.setParams(LaunchActivity.this,launchImage,intent);
                    splashAdFrameLayout.ttADShow(new CommonCallBackInterfaceWithSlashAd() {
                        @Override
                        public void callBack(SplashAd splashAd) {
                            intentMain(1);
                        }
                    },responseString);
                }
            });

            //已同意隐私协议
//            image_framelayout.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (isClickAd || isLoadedAd){//广告加载成功或点击了广告都不跳转
//                        return;
//                    }
//                    //跳转主页
//                    intentMain(2);
//                }
//            },3000);
        }
        if (isClickAd) {
            Log.d(TAG, "onStart: 跳转首页");
            // 从广告界面返回的
            intentMain(8);
        }
    }
    FrameLayout image_framelayout;
    LinearLayout ad_linearlayout;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * 【注意】开屏点睛需要开屏和主页的窗口具有特性 {@linkplain Window.FEATURE_ACTIVITY_TRANSITIONS}
         * Tips: 一般具有Material Design风格的App主题，系统会默认开启该特性。
         *       若没有该特性，可以在{@link #setContentView(int)}之前
         *       调用 {@link #requestWindowFeature(int)} 即可开启特性，如下：
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        }
        /* 设置开屏全屏显示&透明状态栏 */
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);



        super.onCreate(savedInstanceState);

        // Handle the splash screen transition.
        //SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_launch);

        mContext = this;

        image_framelayout = findViewById(R.id.image_framelayout);
        launchImage = findViewById(R.id.launchImage);
        launchImage.setImageResource(R.drawable.launch);


        intent = new Intent(LaunchActivity.this, MainActivity.class);

        Log.d(TAG, "onCreate: applet OSS测试"+ Common.getOssResourceUrl("https://appletattachment.oss-cn-beijing.aliyuncs.com/images/11/2018/10/ASPPPs46NZmnZpYUhp9Y4FES06evuu.jpg"));
        Log.d(TAG, "onCreate: moyuan OSS测试"+ Common.getOssResourceUrl("https://moyuanoss.oss-cn-shanghai.aliyuncs.com/images/11/2018/10/ASPPPs46NZmnZpYUhp9Y4FES06evuu.jpg"));


        Log.d(TAG, "onCreate: md5"+ MD5Tool.MD5("123"));

    }



    //1.检测权限
    private void private_policy_dialog() {
        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return 0;
                    }

                    @Override
                    public Object getItem(int position) {
                        return null;
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        return null;
                    }
                })
                .setFooter(R.layout.dialog_private_agreement)
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
        View view = dialog.getFooterView();
        Button useragreement_btn = view.findViewById(R.id.useragreement_btn);
        useragreement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
                intent.putExtra("slidername","小贝乐园用户协议");
                intent.putExtra("sliderurl","https://www.banghua.xin/useragreement.html");
                mContext.startActivity(intent);
                dialog.dismiss();
            }
        });
        Button privacypolicy_btn = view.findViewById(R.id.privacypolicy_btn);
        privacypolicy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
                intent.putExtra("slidername","小贝乐园隐私政策");
                intent.putExtra("sliderurl","https://www.banghua.xin/privacypolicy.html");
                mContext.startActivity(intent);
                dialog.dismiss();
            }
        });
        Button dismissdialog_btn = view.findViewById(R.id.cancel_btn);
        dismissdialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
                dialog.dismiss();
            }
        });
        Button confirm_btn = view.findViewById(R.id.confirm_btn);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始化第三方SDK
                App.initThirdSDK();


                OkHttpInstance.isShowAD(responseString -> {
                    if (responseString.equals("no")){
                        intentMain(2);
                    }else {
                        //开屏广告
                        splashAdFrameLayout = findViewById(R.id.splashAdFrameLayout);
                        splashAdFrameLayout.setParams(LaunchActivity.this,launchImage,intent);
                        splashAdFrameLayout.ttADShow(new CommonCallBackInterfaceWithSlashAd() {
                            @Override
                            public void callBack(SplashAd splashAd) {
                                intentMain(1);
                            }
                        },responseString);
                    }
                });


                SharedHelper.getInstance(mContext).savePrivateAgreement(true);
//                //1.检测权限
//                int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
//                if (permission != PermissionChecker.PERMISSION_GRANTED) {
//                    //2.没有权限，弹出对话框申请
//                    ActivityCompat.requestPermissions(SplashScreenSampleActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1000);
//                }
                dialog.dismiss();
            }
        });
    }

    //授权回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:
                switch (permissions[0]){
                    case Manifest.permission.ACCESS_COARSE_LOCATION://权限1
                        if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                            //intentMain(4);
                            Log.d(TAG, "onRequestPermissionsResult: COARSE");
                        }else {
                            //Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                            //intentMain(5);
                        }
                        break;
                    case Manifest.permission.ACCESS_FINE_LOCATION://权限2
                        if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                            //intentMain(6);
                            Log.d(TAG, "onRequestPermissionsResult: fine");
                        } else {
                            //Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                            //intentMain(7);
                        }
                        break;
                    default:
                }
                break;
            default:
        }
    }

    public void intentMain(int i){
        Log.d(TAG, "onStart: 跳转首页intentMain"+i);
        Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        //实现定位授权，初始图片，
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(SplashScreenSampleActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }, 1000);
    }







    @Override
    protected void onStart() {
        super.onStart();

    }
}
