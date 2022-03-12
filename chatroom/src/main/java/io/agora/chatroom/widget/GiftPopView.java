package io.agora.chatroom.widget;

import static io.agora.chatroom.ThreadUtils.runOnUiThread;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.chatroom.gift.GiftMessage;

public class GiftPopView extends FrameLayout {

    Context mContext;

    @BindView(R2.id.iv_avatar)
    ImageView iv_avatar;
    @BindView(R2.id.tv_name)
    TextView tv_name;
    @BindView(R2.id.tv_tips)
    TextView tv_tips;
    @BindView(R2.id.iv_gift)
    ImageView iv_gift;

    public GiftPopView(Context context) {
        super(context);
        init(context);
    }

    public GiftPopView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GiftPopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_gift, this);
        ButterKnife.bind(this);
    }

    public void show(GiftMessage message) {
        runOnUiThread(()->{
            try {
                Glide.with(getContext()).load(message.getSenderPortrait()).into(iv_avatar);
                Glide.with(getContext()).load(message.getGift_image()).into(iv_gift);
            }catch (Exception e){

            }

            tv_name.setText(message.getSenderName());
            tv_tips.setText("  "+message.getReceiverName()+"");

            setVisibility(View.VISIBLE);
            animation();
            postDelayed(() -> setVisibility(View.GONE), 2500);
        });
    }

    private void animation() {
        AnimationSet animationSet = new AnimationSet(true);
        measure(0, 0);
        animationSet.addAnimation(new TranslateAnimation(0, 0, getMeasuredHeight(), 0));
        animationSet.addAnimation(new AlphaAnimation(0, 1));
        animationSet.setDuration(1000);
        startAnimation(animationSet);
    }



}
