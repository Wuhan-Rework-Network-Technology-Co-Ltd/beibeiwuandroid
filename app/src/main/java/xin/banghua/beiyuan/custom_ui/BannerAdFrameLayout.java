package xin.banghua.beiyuan.custom_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.sdk.android.cloudcode.AdLoadListener;
import com.alibaba.sdk.android.cloudcode.BannerAdView;

import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.util.LogUtil;


public class BannerAdFrameLayout extends FrameLayout {
    private static final String TAG = "FrameLayoutExample";
    public BannerAdFrameLayout(@NonNull Context context) {
        super(context);
    }

    public BannerAdFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BannerAdFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public BannerAdFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private Context mContext;
    private View mView;

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.framelayout_banner_ad, this, true);


        // 获取banner广告View
        final BannerAdView bannerAdView = mView.findViewById(R.id.bannerAdView);

        // 设置加载监听
        bannerAdView.setAdLoadListener(new AdLoadListener() {
            @Override
            public void loadSuccess() {
                LogUtil.checkPoint("广告加载成功");
                Log.d(TAG, "adShow: 广告加载成功");
            }

            @Override
            public void loadFail(String s, String s1) {
                LogUtil.checkFail(s + " " + s1);
                Log.d(TAG, "adShow: 广告加载失败" + s1);
            }
        });

        // 设置交互监听
        bannerAdView.setAdInteractListener(new BannerAdView.AdInteractListener() {
            @Override
            public void onShowed() {
                LogUtil.checkPoint("广告曝光");
            }

            @Override
            public void onClicked() {
                LogUtil.checkPoint("广告点击");
            }

            @Override
            public void close() {
                LogUtil.checkPoint("广告关闭");
                // 用户点击关闭后，需要隐藏广告
                mView.setVisibility(View.GONE);
            }

            @Override
            public void onAction(int i, int i1) {
                LogUtil.checkPoint("广告效果 " + i + " " + i1);
            }
        });

        // 请求加载banner广告
        bannerAdView.loadAd();
    }



    public void initShow(){
        //自定义部分
    }
}
