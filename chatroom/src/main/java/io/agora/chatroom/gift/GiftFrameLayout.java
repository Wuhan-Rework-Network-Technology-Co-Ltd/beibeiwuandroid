package io.agora.chatroom.gift;

import static io.agora.chatroom.ThreadUtils.runOnUiThread;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGADynamicEntity;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.Common;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.chatroom.UserInfoList;


public class GiftFrameLayout extends FrameLayout {
    private static final String TAG = "FrameLayoutExample";
    public GiftFrameLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GiftFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GiftFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public GiftFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private Context mContext;
    private View mView;


    @BindView(R2.id.gift_player)
    SVGAImageView gift_player;

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.gift_animation_layout, this, true);

        ButterKnife.bind(this, mView);

        gift_player.setMinimumHeight(Common.screen_height);
        gift_player.setMinimumWidth(Common.screen_width);
//        gift_player.post(new Runnable() {
//            @Override
//            public void run() {
//                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) gift_player.getLayoutParams();
//                if (layoutParams != null) {
//                    //设置宽度
//                    layoutParams.width = Common.screen_width;
//                    //设置高度
//                    layoutParams.height = Common.screen_height;
//                }
//            }
//        });
    }


    Boolean isFinished = true;
    List<String> svgaList = new ArrayList<>();
    public void showSVGA(String svgaUrl){
        if (isFinished){
            isFinished = false;
            startSVGA(svgaUrl);
        }else {
            svgaList.add(svgaUrl);
        }
    }


    public void showSVGA(String svgaUrl,@Nullable UserInfoList userInfoList){
        if (isFinished){
            isFinished = false;
            startSVGA(svgaUrl,userInfoList);
        }else {
            svgaList.add(svgaUrl);
        }
    }

    public void startSVGA(String svgaUrl){
        runOnUiThread(()->{
            Log.d(TAG, "showSVGA: 播放动画"+svgaUrl);
            //svga播放器播放
            SVGAParser.Companion.shareParser().init(mContext);
            gift_player.setBackgroundResource(R.color.transparent);
            SVGAParser parser = SVGAParser.Companion.shareParser();
            gift_player.setCallback(new SVGACallback() {
                @Override
                public void onPause() {

                }

                @Override
                public void onFinished() {
                    Log.d(TAG, "onFinished: 动画结束");
                    if (svgaList.size()==0){
                        isFinished = true;
                    }else {
                        startSVGA(svgaList.get(0),null);
                        svgaList.remove(0);
                    }
                }

                @Override
                public void onRepeat() {

                }

                @Override
                public void onStep(int i, double v) {

                }
            });

            try {
                parser.setFrameSize(500, 500);
                parser.decodeFromURL(new URL(svgaUrl), new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                        Log.d(TAG, "onComplete: 动画解析成功");
                        gift_player.setVideoItem(svgaVideoEntity);
                        gift_player.startAnimation();

//                        SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
//                        TextPaint textPaint = new TextPaint();
//                        textPaint.setColor(Color.BLUE);
//                        textPaint.setTextSize(22);
//                        dynamicEntity.setDynamicText("<江东> 进场了", textPaint, "img_joinRoomTextKey");
//                        dynamicEntity.setDynamicImage(Common.makeRoundCorner(Common.returnBitMap(Common.myUserInfoList.getPortrait())),"img_joinRoomHasMountsAvatarKey");
//                        SVGADrawable drawable = new SVGADrawable(svgaVideoEntity, dynamicEntity);
//                        gift_player.setImageDrawable(drawable);
//                        gift_player.startAnimation();
                    }
                    @Override
                    public void onError() {
                        Log.d(TAG, "onComplete: 动画解析失败");
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
    }

    public void startSVGA(String svgaUrl,@Nullable UserInfoList userInfoList){
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
//                if (svgaUrl.contains("images/34/2022/03/lEzR9IU1jXp20e05Upc590Gdr9uC9B.svga")){
//                    return;
//                }
//            } else {
//                //非会员
//                if (svgaUrl.contains("images/34/2022/03/lEzR9IU1jXp20e05Upc590Gdr9uC9B.svga") || svgaUrl.contains("images/34/2022/03/Vg868uj7bHP8UGPPPUUGkZB5kGg85Y.svga")){
//                    return;
//                }
//            }
//        }



        runOnUiThread(()->{
            Log.d(TAG, "showSVGA: 播放动画"+svgaUrl);
            //svga播放器播放
            SVGAParser.Companion.shareParser().init(mContext);
            gift_player.setBackgroundResource(R.color.transparent);
            SVGAParser parser = SVGAParser.Companion.shareParser();
            gift_player.setCallback(new SVGACallback() {
                @Override
                public void onPause() {

                }

                @Override
                public void onFinished() {
                    Log.d(TAG, "onFinished: 动画结束");
                    if (svgaList.size()==0){
                        isFinished = true;
                    }else {
                        startSVGA(svgaList.get(0),null);
                        svgaList.remove(0);
                    }
                }

                @Override
                public void onRepeat() {

                }

                @Override
                public void onStep(int i, double v) {

                }
            });

            try {
                parser.setFrameSize(500, 500);
                parser.decodeFromURL(new URL(svgaUrl), new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                        Log.d(TAG, "onComplete: 动画解析成功");
                        SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
                        TextPaint textPaint = new TextPaint();
                        textPaint.setColor(Color.BLACK);
                        textPaint.setTextSize(24);
                        dynamicEntity.setDynamicText("尊贵的  '"+userInfoList.getNickname()+"' 加入了房间！！！", textPaint, "img_joinRoomTextKey");
                        dynamicEntity.setDynamicImage(Common.makeRoundCorner(Common.returnBitMap(userInfoList.getPortrait())),"img_joinRoomHasMountsAvatarKey");
                        SVGADrawable drawable = new SVGADrawable(svgaVideoEntity, dynamicEntity);
                        gift_player.setImageDrawable(drawable);
                        gift_player.startAnimation();
                    }
                    @Override
                    public void onError() {
                        Log.d(TAG, "onComplete: 动画解析失败");
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
    }
}
