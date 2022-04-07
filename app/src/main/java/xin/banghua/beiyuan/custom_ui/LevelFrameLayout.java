package xin.banghua.beiyuan.custom_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.R;


public class LevelFrameLayout extends FrameLayout {
    private static final String TAG = "FrameLayoutExample";
    public LevelFrameLayout(@NonNull Context context) {
        super(context);
    }

    public LevelFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LevelFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public LevelFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private Context mContext;
    private View mView;

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.level_frame, this, true);

        lv_img = mView.findViewById(R.id.lv_img);
    }


    ImageView lv_img;
    public void initShow(UserInfoList userInfoList){
        //自定义部分
        int lv = Integer.parseInt(userInfoList.getVitality()) + Integer.parseInt(userInfoList.getPost()) + Integer.parseInt(userInfoList.getComment());
        if (lv <= 100){
            lv_img.setImageResource(R.mipmap.lv1);
        }else if (lv <= 200){
            lv_img.setImageResource(R.mipmap.lv2);
        }else if (lv <= 400){
            lv_img.setImageResource(R.mipmap.lv3);
        }else if (lv <= 800){
            lv_img.setImageResource(R.mipmap.lv4);
        }else if (lv <= 1600){
            lv_img.setImageResource(R.mipmap.lv5);
        }else if (lv <= 3500){
            lv_img.setImageResource(R.mipmap.lv6);
        }else if (lv <= 5500){
            lv_img.setImageResource(R.mipmap.lv7);
        }else if (lv <= 7500){
            lv_img.setImageResource(R.mipmap.lv8);
        }else if (lv <= 10000){
            lv_img.setImageResource(R.mipmap.lv9);
        }else if (lv <= 15000){
            lv_img.setImageResource(R.mipmap.lv10);
        }else if (lv <= 20000){
            lv_img.setImageResource(R.mipmap.lv11);
        }else if (lv <= 25000){
            lv_img.setImageResource(R.mipmap.lv12);
        }else if (lv <= 30000){
            lv_img.setImageResource(R.mipmap.lv13);
        }else if (lv <= 40000){
            lv_img.setImageResource(R.mipmap.lv14);
        }else if (lv <= 50000){
            lv_img.setImageResource(R.mipmap.lv15);
        }else if (lv <= 60000){
            lv_img.setImageResource(R.mipmap.lv16);
        }else if (lv <= 70000){
            lv_img.setImageResource(R.mipmap.lv17);
        }else if (lv <= 80000){
            lv_img.setImageResource(R.mipmap.lv18);
        }else if (lv <= 90000){
            lv_img.setImageResource(R.mipmap.lv19);
        }else{
            lv_img.setImageResource(R.mipmap.lv20);
        }
    }
}
