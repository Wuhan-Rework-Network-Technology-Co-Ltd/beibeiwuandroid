package xin.banghua.beiyuan.match;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.faceunity.nama.ui.CircleImageView;

import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.App;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;

public class MatchCard extends FrameLayout {
    private static final String TAG = "FrameLayoutExample";
    public MatchCard(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MatchCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MatchCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MatchCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private Context mContext;
    private View mView;

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.match_message_provider, this, true);

        portrait_one = mView.findViewById(R.id.portrait_one);
        portrait_two = mView.findViewById(R.id.portrait_two);
        matched_degree = mView.findViewById(R.id.matched_degree);
        property_tv = mView.findViewById(R.id.property_tv);
        age_tv = mView.findViewById(R.id.age_tv);
        close_btn = mView.findViewById(R.id.close_btn);
    }

    CircleImageView portrait_one;
    CircleImageView portrait_two;
    TextView matched_degree;
    TextView property_tv;
    TextView age_tv;
    Button close_btn;
    public void show(String uid){
        close_btn.setOnClickListener(v -> {
            MatchCard.this.setVisibility(GONE);
        });

        if (Common.userInfoList==null){
            return;
        }
        OkHttpInstance.getUserAttributes(uid, new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                UserInfoList userInfoList = JSON.parseObject(responseString,UserInfoList.class);
                if (Common.isFriend(userInfoList)){
                    MatchCard.this.setVisibility(GONE);
                    return;
                }
                Glide.with(App.getApplication()).load(userInfoList.getPortrait()).into(portrait_one);
                Glide.with(App.getApplication()).load(Common.userInfoList.getPortrait()).into(portrait_two);
                age_tv.setText("ta是："+userInfoList.getGender()+userInfoList.getProperty());
                property_tv.setText("ta的年龄："+userInfoList.getAge());

                int degree = 85;
                if (userInfoList.getRegion().equals(Common.userInfoList.getRegion())){
                    degree = degree + 6;
                }
                if (Integer.parseInt(userInfoList.getAge()) + 3 < Integer.parseInt(Common.userInfoList.getAge()) && Integer.parseInt(userInfoList.getAge()) - 3 > Integer.parseInt(Common.userInfoList.getAge())){
                    degree = degree + 3;
                }
                if (!TextUtils.isEmpty(userInfoList.getRp_verify_time())){
                    if (!userInfoList.getRp_verify_time().equals("0")){
                        degree = degree + 3;
                    }
                }
                if (Integer.parseInt(userInfoList.getVitality()) > 300){
                    degree = degree + 3;
                }
                matched_degree.setText(degree+"%匹配度");
            }
        });
    }
}
