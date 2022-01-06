package xin.banghua.beiyuan.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.Signin.SigninActivity;
import xin.banghua.beiyuan.utils.Common;

public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder>{
    private static final String TAG = "UserInfoAdapter";

    //用户id,头像，昵称，年龄，性别，属性，地区
    private ArrayList<String> mUserID = new ArrayList<>();
    private ArrayList<String> mUserPortrait = new ArrayList<>();
    private ArrayList<String> mUserNickName = new ArrayList<>();
    private ArrayList<String> mUserAge = new ArrayList<>();
    private ArrayList<String> mUserGender = new ArrayList<>();
    private ArrayList<String> mUserProperty = new ArrayList<>();
    private ArrayList<String> mUserLocation = new ArrayList<>();
    private ArrayList<String> mUserRegion = new ArrayList<>();
    private ArrayList<String> mUserVIP = new ArrayList<>();
    private ArrayList<String> mUserSVIP = new ArrayList<>();
    private ArrayList<String> mAllowLocation = new ArrayList<>();
    private Context mContext;

    Integer current_timestamp = Math.round(new Date().getTime()/1000);

    public UserInfoAdapter(Context mContext,ArrayList<String> userID, ArrayList<String> userPortrait, ArrayList<String> userNickName,ArrayList<String> userAge,ArrayList<String> userGender,ArrayList<String> userProperty,ArrayList<String> userLocation,ArrayList<String> userRegion,ArrayList<String> userVIP,ArrayList<String> mUserSVIP,ArrayList<String> allowLocation) {
        this.mUserID = userID;
        this.mUserPortrait = userPortrait;
        this.mUserNickName = userNickName;
        this.mUserAge = userAge;
        this.mUserGender = userGender;
        this.mUserProperty = userProperty;
        this.mUserLocation = userLocation;
        this.mUserRegion = userRegion;
        this.mUserVIP = userVIP;
        this.mUserSVIP = mUserSVIP;
        this.mAllowLocation = allowLocation;
        this.mContext = mContext;
    }
    //替换数据，并更新
    public void swapData(ArrayList<String> mUserID,ArrayList<String> mUserPortrait,ArrayList<String> mUserNickName,ArrayList<String> mUserAge,ArrayList<String> mUserGender,ArrayList<String> mUserProperty,ArrayList<String> mUserLocation,ArrayList<String> mUserRegion,ArrayList<String> mUserVIP,ArrayList<String> mUserSVIP,ArrayList<String> mAllowLocation){
        this.mUserID = mUserID;
        this.mUserPortrait = mUserPortrait;
        this.mUserNickName = mUserNickName;
        this.mUserAge = mUserAge;
        this.mUserGender = mUserGender;
        this.mUserProperty = mUserProperty;
        this.mUserLocation = mUserLocation;
        this.mUserRegion = mUserRegion;
        this.mUserVIP = mUserVIP;
        this.mUserSVIP = mUserSVIP;
        this.mAllowLocation = mAllowLocation;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_userinfo,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: Called");

        viewHolder.userID.setText(mUserID.get(i));
        Glide.with(mContext)
                .asBitmap()
                .load(Common.getOssResourceUrl(mUserPortrait.get(i)))
                .into(viewHolder.userPortrait);
        viewHolder.userNickName.setText(mUserNickName.get(i));
        viewHolder.userAge.setText(mUserAge.get(i));
        viewHolder.userGender.setText(mUserGender.get(i));
        if (mUserGender.get(i).equals("男")){
            Resources resources = mContext.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.male,null);
            viewHolder.userGender.setForeground(drawable);
        }else {
            Resources resources = mContext.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.female,null);
            viewHolder.userGender.setForeground(drawable);
        }
        viewHolder.userProperty.setText(mUserProperty.get(i));
        if (mAllowLocation.get(i).equals("1")){
            viewHolder.userLocation.setText(mUserLocation.get(i)+"km");
        }else {
            viewHolder.userLocation.setText("? km");
        }
        viewHolder.userRegion.setText(mUserRegion.get(i));
        viewHolder.userVIP.setText(mUserVIP.get(i));
//        if (mUserVIP.get(i).equals("普通")){
//            Resources resources = mContext.getResources();
//            Drawable drawable = resources.getDrawable(R.drawable.nonmember,null);
//            viewHolder.userVIP.setForeground(drawable);
//        }else {
//            Resources resources = mContext.getResources();
//            Drawable drawable = resources.getDrawable(R.drawable.vip_image,null);
//            viewHolder.userVIP.setForeground(drawable);
//        }

        //现在vip传过来的是时间
        //GOTO  会员标识
        //现在vip传过来的是时间
        viewHolder.vip_gray.setVisibility(View.VISIBLE);
        if (mUserSVIP.get(i).isEmpty()||mUserSVIP.get(i)=="null"){
            if (mUserVIP.get(i).isEmpty()||mUserVIP.get(i)=="null"){
                Resources resources = mContext.getResources();
                Drawable drawable = resources.getDrawable(R.drawable.nonmember,null);
                viewHolder.userVIP.setForeground(drawable);


                Glide.with(mContext)
                        .asBitmap()
                        .load(R.drawable.ic_vip_gray)
                        .into(viewHolder.vip_gray);
            }else {
                int vip_time = Integer.parseInt(mUserVIP.get(i)+"");
                if (vip_time > current_timestamp) {
                    //vipicon分级
                    Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                    if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_vip_diamond)
                                .into(viewHolder.vip_gray);
                    } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_vip_black)
                                .into(viewHolder.vip_gray);
                    } else {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_vip_white)
                                .into(viewHolder.vip_gray);
                    }
                } else {
                    Resources resources = mContext.getResources();
                    Drawable drawable = resources.getDrawable(R.drawable.nonmember, null);
                    viewHolder.userVIP.setForeground(drawable);

                    Glide.with(mContext)
                            .asBitmap()
                            .load(R.drawable.ic_vip_gray)
                            .into(viewHolder.vip_gray);
                }
            }
        }else {
            int svip_time = Integer.parseInt(mUserSVIP.get(i)+"");
            if (svip_time > current_timestamp) {
                //vipicon分级
                Log.d("会员时长", ((svip_time - current_timestamp) + ""));
                if ((svip_time - current_timestamp) < 3600 * 24 * 30) {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(R.drawable.ic_svip_diamond)
                            .into(viewHolder.vip_gray);
                } else if ((svip_time - current_timestamp) < 3600 * 24 * 180) {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(R.drawable.ic_svip_black)
                            .into(viewHolder.vip_gray);
                } else {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(R.drawable.ic_svip_white)
                            .into(viewHolder.vip_gray);
                }
            } else {
                if (mUserVIP.get(i).isEmpty()||mUserVIP.get(i)=="null"){
                    Resources resources = mContext.getResources();
                    Drawable drawable = resources.getDrawable(R.drawable.nonmember,null);
                    viewHolder.userVIP.setForeground(drawable);

                    viewHolder.vip_gray.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .asBitmap()
                            .load(R.drawable.ic_vip_gray)
                            .into(viewHolder.vip_gray);
                }else {
                    int vip_time = Integer.parseInt(mUserVIP.get(i)+"");
                    if (vip_time > current_timestamp) {
                        //vipicon分级
                        Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                        if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_diamond)
                                    .into(viewHolder.vip_gray);
                        } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_black)
                                    .into(viewHolder.vip_gray);
                        } else {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_white)
                                    .into(viewHolder.vip_gray);
                        }
                    } else {
                        Resources resources = mContext.getResources();
                        Drawable drawable = resources.getDrawable(R.drawable.nonmember, null);
                        viewHolder.userVIP.setForeground(drawable);

                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_vip_gray)
                                .into(viewHolder.vip_gray);
                    }
                }
            }
        }

        viewHolder.userinfoLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (Common.myID ==null){
                    Intent intentSignin = new Intent(mContext, SigninActivity.class);
                    mContext.startActivity(intentSignin);
                }else {
                    Log.d(TAG, "onClick: clicked on: " + mUserID.get(i));
                    //Toast.makeText(mContext,mUserID.get(i)+mUserNickName.get(i),Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(v.getContext(), PersonageActivity.class);
                    intent.putExtra("userID", mUserID.get(i));
                    //保存选中的用户id
                    //SharedHelper shvalue = new SharedHelper(mContext);
                    //shvalue.saveValue(mUserID.get(i));
                    Log.d(TAG, "onClick: 保存选中的用户id" + mUserID.get(i));
                    v.getContext().startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUserID.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView userID;
        CircleImageView userPortrait;
        TextView userNickName;
        TextView userAge;
        TextView userGender;
        TextView userProperty;
        TextView userLocation;
        TextView userRegion;
        TextView userVIP;
        RelativeLayout userinfoLayout;

        ImageView vip_gray;
        ImageView vip_diamond;
        ImageView vip_black;
        ImageView vip_white;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userID = itemView.findViewById(R.id.userID);
            userPortrait = itemView.findViewById(R.id.authportrait);
            userNickName = itemView.findViewById(R.id.userNickName);
            userAge = itemView.findViewById(R.id.userAge);
            userGender = itemView.findViewById(R.id.userGender);
            userProperty = itemView.findViewById(R.id.userProperty);
            userLocation = itemView.findViewById(R.id.userLocation);
            userRegion = itemView.findViewById(R.id.userRegion);
            userVIP = itemView.findViewById(R.id.userVIP);

            vip_gray = itemView.findViewById(R.id.vip_gray);
            vip_diamond = itemView.findViewById(R.id.vip_diamond);
            vip_black = itemView.findViewById(R.id.vip_black);
            vip_white = itemView.findViewById(R.id.vip_white);

            userinfoLayout = itemView.findViewById(R.id.userinfo_layout);
        }
    }
}
