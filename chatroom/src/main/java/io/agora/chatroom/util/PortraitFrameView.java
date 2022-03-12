package io.agora.chatroom.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

import io.agora.chatroom.R;
import io.agora.chatroom.UserInfoList;


public class PortraitFrameView extends FrameLayout {
    private static final String TAG = "PortraitFrameView";
    public PortraitFrameView(@NonNull Context context) {
        super(context);
    }

    public PortraitFrameView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PortraitFrameView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public PortraitFrameView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private Context mContext;
    private View mView;

    ImageView portrait_frame_image_view;


    String defaultPortraitFrame;
    public Boolean isDefault = false;
    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.portrait_frame_view, this, true);


        //
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PortraitFrameView);
        defaultPortraitFrame = attributes.getString(R.styleable.PortraitFrameView_defaultPortraitFrame);

        if (!TextUtils.isEmpty(defaultPortraitFrame)){
            isDefault = true;
            setPortraitFrame(defaultPortraitFrame);
        }
    }
    //播放器变量
    SVGAImageView animationView;
    SVGAParser svgaParser;

    public void setPortraitFrame(String svgaUrl){
        if (TextUtils.isEmpty(svgaUrl)){
            return;
        }
        portrait_frame_image_view = mView.findViewById(R.id.portrait_frame_image_view);
        animationView = mView.findViewById(R.id.portrait_frame_svga_view);

        //svga播放器播放
        SVGAParser.Companion.shareParser().init(mContext);
        portrait_frame_image_view.setVisibility(GONE);
        animationView.setVisibility(VISIBLE);
        animationView.setBackgroundResource(R.color.transparent);
        svgaParser = SVGAParser.Companion.shareParser();
        svgaParser.setFrameSize(500, 500);
        try {
            if (isDefault){
                svgaParser.decodeFromAssets(svgaUrl+".svga", new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                        animationView.setVideoItem(videoItem);
                        animationView.startAnimation();
                        Log.d(TAG, "onComplete: loadAnimation1");
                    }

                    @Override
                    public void onError() {

                    }
                });
            }else {
                svgaParser.decodeFromURL(new URL(svgaUrl), new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                        animationView.setVideoItem(videoItem);
                        animationView.startAnimation();
                        Log.d(TAG, "onComplete: loadAnimation1");
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public void setPortraitFrame(UserInfoList userInfoList){
        if (userInfoList==null){
            return;
        }
//        Integer current_timestamp = Math.round(new Date().getTime()/1000);
//        int svip_time = Integer.parseInt(userInfoList.getSvip()+"");
//        if (svip_time > current_timestamp) {
//            //svip
//        } else {
//            int vip_time = Integer.parseInt(userInfoList.getVip()+"");
//            if (vip_time > current_timestamp) {
//                //vip，是皇冠加身则返回
//                if (userInfoList.getPortraitframe().contains("images/34/2022/03/JRYN5e6Xx69o05Z0yn0pcVCVrRpYii.svga")){
//                    return;
//                }
//            } else {
//                //非会员
//                if (userInfoList.getPortraitframe().contains("images/34/2022/03/JRYN5e6Xx69o05Z0yn0pcVCVrRpYii.svga") || userInfoList.getPortraitframe().contains("images/34/2022/03/Srav5urq1aW7B0b1U6wmu6Vqwv01Jv.svga")){
//                    return;
//                }
//            }
//        }



        portrait_frame_image_view = mView.findViewById(R.id.portrait_frame_image_view);
        animationView = mView.findViewById(R.id.portrait_frame_svga_view);

        //svga播放器播放
        SVGAParser.Companion.shareParser().init(mContext);
        portrait_frame_image_view.setVisibility(GONE);
        animationView.setVisibility(VISIBLE);
        animationView.setBackgroundResource(R.color.transparent);
        svgaParser = SVGAParser.Companion.shareParser();
        svgaParser.setFrameSize(500, 500);
        try {
            svgaParser.decodeFromURL(new URL(userInfoList.getPortraitframe()), new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    animationView.setVideoItem(videoItem);
                    animationView.startAnimation();
                    Log.d(TAG, "onComplete: loadAnimation1");
                }

                @Override
                public void onError() {

                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


}
