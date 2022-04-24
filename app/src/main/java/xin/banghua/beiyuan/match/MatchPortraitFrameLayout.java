package xin.banghua.beiyuan.match;

import static xin.banghua.onekeylogin.Constant.THEME_KEY;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.faceunity.nama.ui.CircleImageView;

import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.onekeylogin.login.OneKeyLoginActivity;


public class MatchPortraitFrameLayout extends FrameLayout {
    private static final String TAG = "FrameLayoutExample";
    public MatchPortraitFrameLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MatchPortraitFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MatchPortraitFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MatchPortraitFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private Context mContext;
    private View mView;

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.match_portrait_framelayout, this, true);
    }



    public void notClickableShow(){
        container_portrait.setOnClickListener(v -> {
            Toast.makeText(mContext,"次数已用完，升到2级后可无限次数",Toast.LENGTH_SHORT).show();
        });
    }

    ConstraintLayout container_portrait;
    CircleImageView circleImageView;
    TextView textView;
    public void initShow(UserInfoList userInfoList){
        try {
            container_portrait = mView.findViewById(R.id.container_portrait);
            circleImageView = mView.findViewById(R.id.circleImageView);
            textView = mView.findViewById(R.id.textView);
            //自定义部分
            Glide.with(mContext).load(userInfoList.getPortrait()).into(circleImageView);
            textView.setText(userInfoList.getNickname());

            container_portrait.setOnClickListener(v -> {
                if (Common.myID ==null){
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
                            .setTitle("登录后可以发起聊天！")
                            .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(mContext, OneKeyLoginActivity.class);
                                    intent.putExtra(THEME_KEY, 4);
                                    mContext.startActivity(intent);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.create().show();
                }else {
                    Intent intent = new Intent(mContext, PersonageActivity.class);
                    intent.putExtra("userID",userInfoList.getId());
                    intent.putExtra("from","match");
                    mContext.startActivity(intent);
                }
            });
        }catch (Exception e){
            Log.e(TAG, "initShow: 抛出异常");
        }
    }
}
