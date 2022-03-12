package xin.banghua.beiyuan.custom_ui.ad;

import static com.bytedance.sdk.openadsdk.TTAdConstant.DOWNLOAD_TYPE_NO_POPUP;
import static com.bytedance.sdk.openadsdk.TTAdConstant.SPLASH_BUTTON_TYPE_DOWNLOAD_BAR;
import static com.bytedance.sdk.openadsdk.TTAdConstant.SPLASH_BUTTON_TYPE_FULL_SCREEN;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.sdk.android.cloudcode.AdLoadListener;
import com.alibaba.sdk.android.cloudcode.SplashAdView;
import com.baidu.mobads.sdk.api.RequestParameters;
import com.baidu.mobads.sdk.api.SplashAd;
import com.baidu.mobads.sdk.api.SplashInteractionListener;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.ISplashClickEyeListener;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTSplashAd;

import java.lang.ref.SoftReference;

import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.utils.CommonCallBackInterface;
import xin.banghua.beiyuan.utils.LogUtil;


public class SplashAdFrameLayout extends FrameLayout {
    private static final String TAG = "SplashAdFrameLayout";
    public SplashAdFrameLayout(@NonNull Context context) {
        super(context);
    }

    public SplashAdFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SplashAdFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public SplashAdFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    Activity activity;
    ImageView launchImage;
    Intent intent;
    public void setParams(Activity activity, ImageView launchImage, Intent intent){
        this.activity = activity;
        this.launchImage = launchImage;
        this.intent = intent;
    }



    private Context mContext;
    private View mView;


    SplashAdView splashAdView;

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.framelayout_splash_ad, this, true);
    }

    public static boolean isClickAd = false;
    public static boolean isLoadedAd = false;





    private TTAdNative mTTAdNative;
    private FrameLayout mSplashContainer;
    private TTSplashAd mSplashAd;
    private SplashClickEyeManager mSplashClickEyeManager;
    private SplashClickEyeListener mSplashClickEyeListener;
    private boolean mIsSplashClickEye = false;//是否是开屏点睛
    /**
     * 穿山甲
     */
    public void ttADShow(CommonCallBackInterfaceWithSlashAd commonCallBackInterfaceWithSlashAd,String responseString){
        mSplashContainer = (FrameLayout) findViewById(R.id.splash_container);
        //step2:创建TTAdNative对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(mContext);
        //getExtraInfo();
        //在合适的时机申请权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题
        //在开屏时候申请不太合适，因为该页面倒计时结束或者请求超时会跳转，在该页面申请权限，体验不好
        // TTAdManagerHolder.getInstance(this).requestPermissionIfNecessary(this);
        //加载开屏广告
        SplashClickEyeManager.getInstance().setSupportSplashClickEye(true);
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = null;


        if (responseString.equals("normal")){
            adSlot = new AdSlot.Builder()
                    .setCodeId("887588176")
                    .setImageAcceptedSize(1080, 1920)
                    .setSplashButtonType(SPLASH_BUTTON_TYPE_DOWNLOAD_BAR)
                    .setDownloadType(DOWNLOAD_TYPE_NO_POPUP)
                    .build();
        }else {
            Log.d(TAG, "ttADShow: 全屏广告");
            adSlot = new AdSlot.Builder()
                    .setCodeId("887588176")
                    .setImageAcceptedSize(1080, 1920)
                    .setSplashButtonType(SPLASH_BUTTON_TYPE_FULL_SCREEN)
                    .setDownloadType(DOWNLOAD_TYPE_NO_POPUP)
                    .build();
        }


        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
                Log.d(TAG, String.valueOf(message));
                showToast(message);

                mSplashContainer.setVisibility(GONE);

                baiduADShow((splashAd) -> {
                    splashAd.finishAndJump(intent, new SplashAd.OnFinishListener() {
                        @Override
                        public void onFinishActivity() {
                            Log.i(TAG, "onFinishActivity");
                            activity.finish();
                        }
                    });
                    splashAd.destroy();
                });
            }

            @Override
            @MainThread
            public void onTimeout() {
                showToast("开屏广告加载超时");

                mSplashContainer.setVisibility(GONE);

                baiduADShow((splashAd) -> {
                    splashAd.finishAndJump(intent, new SplashAd.OnFinishListener() {
                        @Override
                        public void onFinishActivity() {
                            Log.i(TAG, "onFinishActivity");
                            activity.finish();
                        }
                    });
                    splashAd.destroy();
                });
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                launchImage.setVisibility(GONE);
                Log.d(TAG, "开屏广告请求成功");
                if (ad == null) {
                    return;
                }
                mSplashAd = ad;

                //获取SplashView
                View view = ad.getSplashView();
                //初始化开屏点睛相关数据
                initSplashClickEyeData(mSplashAd, view);
                if (view != null && mSplashContainer != null && !activity.isFinishing()) {
                    mSplashContainer.setVisibility(View.VISIBLE);
                    mSplashContainer.removeAllViews();
                    //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
                    mSplashContainer.addView(view);
                    /**
                     * 设置是否开启开屏广告倒计时功能以及不显示跳过按钮,如果设置为true，您需要自定义倒计时逻辑，
                     * 参考样例请看：
                     * @see SplashActivity#useCustomCountdownButton
                     */
                    //useCustomCountdownButton(false,ad);

                } else {
                    commonCallBackInterfaceWithSlashAd.callBack(splashAd);
                }


                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        Log.d(TAG, "onAdClicked");
                        showToast("开屏广告点击");

                        isClickAd = true;
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Log.d(TAG, "onAdShow");
                        showToast("开屏广告展示");
                    }

                    @Override
                    public void onAdSkip() {
                        Log.d(TAG, "onAdSkip");
                        showToast("开屏广告跳过");
                        commonCallBackInterfaceWithSlashAd.callBack(splashAd);

                    }

                    @Override
                    public void onAdTimeOver() {
                        Log.d(TAG, "onAdTimeOver");
                        showToast("开屏广告倒计时结束");
                        commonCallBackInterfaceWithSlashAd.callBack(splashAd);
                    }
                });
                if (ad.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ad.setDownloadListener(new TTAppDownloadListener() {
                        boolean hasShow = false;

                        @Override
                        public void onIdle() {
                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            if (!hasShow) {
                                showToast("下载中...");
                                hasShow = true;
                            }
                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                            showToast("下载暂停...");

                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                            showToast("下载失败...");

                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                            showToast("下载完成...");

                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {
                            showToast("安装完成...");

                        }
                    });
                }
            }
        }, 3000);

    }
    private void initSplashClickEyeData(TTSplashAd splashAd, View splashView) {
        if (splashAd == null || splashView == null) {
            return;
        }
        mSplashClickEyeListener = new SplashClickEyeListener(activity, splashAd, mSplashContainer, mIsSplashClickEye);

        splashAd.setSplashClickEyeListener(mSplashClickEyeListener);
        mSplashClickEyeManager = SplashClickEyeManager.getInstance();
        mSplashClickEyeManager.setSplashInfo(splashAd, splashView, activity.getWindow().getDecorView());
    }
    private void showToast(String msg) {
        //Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }

    public static class SplashClickEyeListener implements ISplashClickEyeListener {
        private SoftReference<Activity> mActivity;
        private TTSplashAd mSplashAd;
        private View mSplashContainer;
        private boolean mIsFromSplashClickEye = false;

        public SplashClickEyeListener(Activity activity, TTSplashAd splashAd, View splashContainer, boolean isFromSplashClickEye) {
            mActivity = new SoftReference<>(activity);
            mSplashAd = splashAd;
            mSplashContainer = splashContainer;
            mIsFromSplashClickEye = isFromSplashClickEye;
        }

        @Override
        public void onSplashClickEyeAnimationStart() {
            //开始执行开屏点睛动画
            if (mIsFromSplashClickEye) {
                startSplashAnimationStart();
            }
        }

        @Override
        public void onSplashClickEyeAnimationFinish() {
            //sdk关闭了了点睛悬浮窗
            SplashClickEyeManager splashClickEyeManager = SplashClickEyeManager.getInstance();
            boolean isSupport = splashClickEyeManager.isSupportSplashClickEye();
            if (mIsFromSplashClickEye && isSupport) {
                finishActivity();
            }
            splashClickEyeManager.clearSplashStaticData();
        }

        @Override
        public boolean isSupportSplashClickEye(boolean isSupport) {
            SplashClickEyeManager splashClickEyeManager = SplashClickEyeManager.getInstance();
            splashClickEyeManager.setSupportSplashClickEye(isSupport);
            return false;
        }

        private void finishActivity() {
            if (mActivity.get() == null) {
                return;
            }
            mActivity.get().finish();
        }

        private void startSplashAnimationStart() {
            if (mActivity.get() == null || mSplashAd == null || mSplashContainer == null) {
                return;
            }
            SplashClickEyeManager splashClickEyeManager = SplashClickEyeManager.getInstance();
            ViewGroup content = mActivity.get().findViewById(android.R.id.content);
            splashClickEyeManager.startSplashClickEyeAnimation(mSplashContainer, content, content, new SplashClickEyeManager.AnimationCallBack() {
                @Override
                public void animationStart(int animationTime) {
                }

                @Override
                public void animationEnd() {
                    if (mSplashAd != null) {
                        mSplashAd.splashClickEyeAnimationFinish();
                    }
                }
            });
        }
    }



    private SplashAd splashAd;
    /**
     * 百度的广告
     */
    public void baiduADShow(CommonCallBackInterfaceWithSlashAd commonCallBackInterfaceWithSlashAd){
        isClickAd = false;
        // 默认请求https广告，若需要请求http广告，请设置AdSettings.setSupportHttps为false
        //         AdSettings.setSupportHttps(false);
        final RelativeLayout adsParent = (RelativeLayout) mView.findViewById(R.id.adsRl);

        SplashInteractionListener listener = new SplashInteractionListener() {
            @Override
            public void onLpClosed() {
                Log.i(TAG, "lp页面关闭");
                //commonCallBackInterfaceWithSlashAd.callBack(splashAd);
            }

            @Override
            public void onAdDismissed() {
                Log.i(TAG, "onAdDismissed");
                if (isClickAd) {
                    Log.d(TAG, "onStart: 跳转首页");
                    // 从广告界面返回的
                    return;
                }
                commonCallBackInterfaceWithSlashAd.callBack(splashAd);
            }

            @Override
            public void onADLoaded() {
                Log.i(TAG, "onADLoaded");

            }

            @Override
            public void onAdFailed(String arg0) {
                Log.i(TAG, "" + arg0);
                commonCallBackInterfaceWithSlashAd.callBack(splashAd);
            }

            @Override
            public void onAdPresent() {
                Log.i(TAG, "onAdPresent");

                launchImage.setVisibility(GONE);
                isLoadedAd = true;
            }

            @Override
            public void onAdClick() {
                Log.i(TAG, "onAdClick");
                // 设置开屏可接受点击时，该回调可用
                isClickAd = true;
            }

            @Override
            public void onAdCacheSuccess() {
                Log.i(TAG, "onAdCacheSuccess");
            }

            @Override
            public void onAdCacheFailed() {
                Log.i(TAG, "onAdCacheFailed");
            }
        };

        String adPlaceId = "7719810"; // 重要：请填上您的广告位ID，代码位错误会导致无法请求到广告


//        splashAd = new SplashAd(this, adPlaceId, listener);
//        splashAd.loadAndShow(adsParent);

        // 如果开屏需要load广告和show广告分开，请参考类RSplashManagerActivity的写法
        // 如果需要修改开屏超时时间、隐藏工信部下载整改展示，请设置下面代码;
        final RequestParameters.Builder parameters = new RequestParameters.Builder();
        // sdk内部默认超时时间为4200，单位：毫秒
        parameters.addExtra(SplashAd.KEY_TIMEOUT, "4200");
        // sdk内部默认值为true
        parameters.addExtra(SplashAd.KEY_DISPLAY_DOWNLOADINFO, "true");
        // 是否限制点击区域，默认不限制
        parameters.addExtra(SplashAd.KEY_LIMIT_REGION_CLICK, "true");
        // 是否展示点击引导按钮，默认不展示，若设置可限制点击区域，则此选项默认打开
        parameters.addExtra(SplashAd.KEY_DISPLAY_CLICK_REGION, "true");
        // 用户点击开屏下载类广告时，是否弹出Dialog
        // 此选项设置为true的情况下，会覆盖掉 {SplashAd.KEY_DISPLAY_DOWNLOADINFO} 的设置
        parameters.addExtra(SplashAd.KEY_POPDIALOG_DOWNLOAD, "true");
        splashAd = new SplashAd(mContext, adPlaceId, parameters.build(), listener);
        splashAd.loadAndShow(adsParent);
    }



    /**
     * 阿里的云码广告
     */
    public void aliADShow(CommonCallBackInterface commonCallBackInterface){
        // 获取banner广告View
        final SplashAdView splashAdView = mView.findViewById(R.id.splashAdView);
        // 这里配置广告位ID
        //splashAdView.setAdSlot(new AdSlot.Builder().slotId("649808463224683520").slotType(AdSlotType.SPLASH).build());
        // 设置广告加载监听
        splashAdView.setAdLoadListener(new AdLoadListener() {
            @Override
            public void loadSuccess() {
                LogUtil.checkPoint("广告加载成功");
                Log.d(TAG, "adShow: 广告加载成功");
                isLoadedAd = true;


            }

            @Override
            public void loadFail(String s, String s1) {
                LogUtil.checkFail(s + "广告加载失败 " + s1);
                Log.d(TAG, "adShow: 广告加载失败" + s + "|" + s1);
            }
        });

        // 设置广告交互监听
        splashAdView.setAdInteractListener(new SplashAdView.SplashAdInteractListener() {
            @Override
            public void onTimeOver() {
                LogUtil.checkPoint("倒计时结束");

                callOnClick();
            }

            @Override
            public void jump() {
                LogUtil.checkPoint("用户点击跳过");

                callOnClick();
            }

            @Override
            public void onShowed() {
                LogUtil.checkPoint("广告曝光");
            }

            @Override
            public void onClicked() {
                LogUtil.checkPoint("广告点击");
                isClickAd = true;
            }

            @Override
            public void onAction(int i, int i1) {
                LogUtil.checkPoint("广告效果 " + i + " " + i1);
            }
        });

        // 请求加载广告
        Log.d(TAG, "adShow: 加载广告");
        splashAdView.loadAd();
    }
}
