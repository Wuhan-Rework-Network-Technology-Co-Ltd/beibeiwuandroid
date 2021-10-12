package xin.banghua.beiyuan.custom_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.sdk.android.cloudcode.AdLoadListener;
import com.alibaba.sdk.android.cloudcode.SplashAdView;
import com.baidu.mobads.sdk.api.RequestParameters;
import com.baidu.mobads.sdk.api.SplashAd;
import com.baidu.mobads.sdk.api.SplashInteractionListener;

import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.util.CommonCallBackInterface;
import xin.banghua.beiyuan.util.CommonCallBackInterfaceWithSlashAd;
import xin.banghua.beiyuan.util.LogUtil;


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

    ImageView launchImage;

    /**
     * 设置启用图片，当广告加载成功时隐藏图片
     * @param imageView
     */
    public void setLaunchImage(ImageView imageView){
        launchImage = imageView;
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
        parameters.addExtra(SplashAd.KEY_LIMIT_REGION_CLICK, "false");
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
