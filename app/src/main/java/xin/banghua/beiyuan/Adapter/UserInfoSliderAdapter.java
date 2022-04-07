package xin.banghua.beiyuan.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.orhanobut.dialogplus.DialogPlus;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.chatroom.gift.GiftList;
import io.agora.chatroom.gift.GiftSentAdapter;
import io.agora.chatroom.util.PortraitFrameView;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.Signin.SigninActivity;
import xin.banghua.beiyuan.utils.MapDistance;
import xin.banghua.beiyuan.utils.OkHttpInstance;

public class UserInfoSliderAdapter extends RecyclerView.Adapter implements  ViewPagerEx.OnPageChangeListener{
    private static final String TAG = "UserInfoSliderAdapter";
    private final static int TYPE_HEAD = 0;
    private final static int TYPE_CONTENT = 1;

    Integer current_timestamp = Math.round(new Date().getTime()/1000);
    //幻灯片
    SliderLayout mDemoSlider;
    JSONArray jsonArray;
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
    private ArrayList<String> online = new ArrayList<>();
    private Context mContext;


    //设置按钮监听
    UserInfoCallBack userInfoCallBack;
    public void setBtnListen(UserInfoCallBack userInfoCallBack){
        this.userInfoCallBack = userInfoCallBack;
    }

    List<UserInfoList> userInfoLists = new ArrayList<>();
    public UserInfoSliderAdapter(Context context,List<UserInfoList> userInfoLists) {
        mContext = context;
        this.userInfoLists = userInfoLists;
    }
    public void setUserInfoLists(List<UserInfoList> userInfoLists){
        this.userInfoLists = userInfoLists;
        notifyDataSetChanged();
    }
    public void swapData(List<UserInfoList> userInfoLists){
        int oldSize = this.userInfoLists.size();
        int newSize = userInfoLists.size();
        this.userInfoLists = userInfoLists;
        notifyItemRangeInserted(oldSize , newSize);
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

    public UserInfoSliderAdapter(Context mContext,JSONArray jsonArray,ArrayList<String> userID, ArrayList<String> userPortrait, ArrayList<String> userNickName,ArrayList<String> userAge,ArrayList<String> userGender,ArrayList<String> userProperty,ArrayList<String> userLocation,ArrayList<String> userRegion,ArrayList<String> userVIP,ArrayList<String> mUserSVIP,ArrayList<String> allowLocation) {
        this.jsonArray = jsonArray;

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

        Log.d(TAG, "UserInfoSliderAdapter: 初始化");
    }
    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: position"+position);
//        if ( position == 0){ // 头部
//            Log.d(TAG, "getItemViewType: 头");
//            return TYPE_HEAD;
//        }else{
//            Log.d(TAG, "getItemViewType: 身");
//            return TYPE_CONTENT;
//        }
        return TYPE_CONTENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == TYPE_HEAD){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_slider,viewGroup,false);
            UserInfoSliderAdapter.SliderHolder viewHolder = new UserInfoSliderAdapter.SliderHolder(view);
            return viewHolder;
        }else if(i == TYPE_CONTENT){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_fragment_adapter,viewGroup,false);
            UserInfoSliderAdapter.UserinfoHolder viewHolder = new UserInfoSliderAdapter.UserinfoHolder(view);
            return viewHolder;
        }
        return null;
    }


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof UserinfoHolder){
            ((UserinfoHolder) holder).portraitFrameView.setPortraitFrame(((UserinfoHolder) holder).portraitFrameView.getTag().toString());
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        Log.d(TAG, "onBindViewHolder: 进入");
        if (viewHolder instanceof SliderHolder){
//            HashMap<String,String> url_maps = new HashMap<String, String>();
//            if (jsonArray.length()>0){
//                for (int j=0;j<jsonArray.length();j++){
//                    try {
//                      final JSONObject jsonObject = jsonArray.getJSONObject(j);
//                        //url_maps.put(jsonObject.getString("slidename"), jsonObject.getString("slidepicture"));
//                        TextSliderView textSliderView = new TextSliderView(mContext);
//                        // initialize a SliderLayout
//                        textSliderView
//                                .description(jsonObject.getString("slidename"))
//                                .image(Common.getOssResourceUrl(jsonObject.getString("slidepicture")))
//                                .setScaleType(BaseSliderView.ScaleType.Fit)
//                                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
//                                    @Override
//                                    public void onSliderClick(BaseSliderView slider) {
//                                        try {
//                                            if (!jsonObject.getString("slideurl").isEmpty()){
//                                                Log.d(TAG, "onSliderClick: 链接是"+jsonObject.getString("slideurl"));
//                                                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
//                                                intent.putExtra("slidername",jsonObject.getString("slidename"));
//                                                intent.putExtra("sliderurl",jsonObject.getString("slideurl"));
//                                                mContext.startActivity(intent);
//                                            }
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
//
//                        //add your extra information
//                        textSliderView.bundle(new Bundle());
//                        textSliderView.getBundle()
//                                .putString("extra",jsonObject.getString("slidename"));
//
//                        mDemoSlider.addSlider(textSliderView);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//
//
//            mDemoSlider.setMinimumHeight(100);
//            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
//            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
//            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
//            mDemoSlider.setDuration(4000);
//            mDemoSlider.addOnPageChangeListener(this);
//
//            mDemoSlider.setVisibility(View.GONE);
        }else if (viewHolder instanceof UserinfoHolder){
            UserInfoList currentItem = userInfoLists.get(i);

            ((UserinfoHolder) viewHolder).lv_img.setImageResource(Common.getLevelFromUser(currentItem));

            ((UserinfoHolder) viewHolder).online_tv.setText(currentItem.getOnline());
            if (currentItem.getOnline().equals("在线")){
                Glide.with(mContext).load(R.mipmap.green_point).into( ((UserinfoHolder) viewHolder).online_img);
            }else {
                Glide.with(mContext).load(R.mipmap.gray_point).into( ((UserinfoHolder) viewHolder).online_img);
            }

            ((UserinfoHolder) viewHolder).portraitFrameView.setPortraitFrame(currentItem.getPortraitframe());
            ((UserinfoHolder) viewHolder).portraitFrameView.setTag(currentItem.getPortraitframe());
            Glide.with(mContext)
                    .asBitmap()
                    .load(Common.getOssResourceUrl(currentItem.getPortrait()))
                    .into(((UserinfoHolder) viewHolder).userPortrait);
            ((UserinfoHolder) viewHolder).userNickName.setText(currentItem.getNickname());
            ((UserinfoHolder) viewHolder).userAge.setText(" "+currentItem.getAge());
            if (currentItem.getGender().equals("男")){
                Resources resources = mContext.getResources();
                Drawable drawable = resources.getDrawable(R.mipmap.bboy,null);
                //((UserinfoHolder) viewHolder).userGender.setForeground(drawable);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ((UserinfoHolder) viewHolder).userAge.setCompoundDrawables(drawable, null, null, null);
                ((UserinfoHolder) viewHolder).userAge.setBackgroundResource(R.mipmap.h_sex_male);
            }else {
                Resources resources = mContext.getResources();
                Drawable drawable = resources.getDrawable(R.mipmap.ggirl,null);
                //((UserinfoHolder) viewHolder).userGender.setForeground(drawable);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ((UserinfoHolder) viewHolder).userAge.setCompoundDrawables(drawable, null, null, null);
                ((UserinfoHolder) viewHolder).userAge.setBackgroundResource(R.mipmap.h_sex);
            }
            ((UserinfoHolder) viewHolder).userProperty.setText(currentItem.getProperty());
            if (currentItem.getAllowlocation().equals("1")){
                ((UserinfoHolder) viewHolder).userLocation.setText("  "+ MapDistance.getDistance(Common.latitude,Common.longitude,currentItem.getLatitude(),currentItem.getLongitude())+"km");
            }else {
                ((UserinfoHolder) viewHolder).userLocation.setText("   ? km");
            }
            ((UserinfoHolder) viewHolder).userRegion.setText(currentItem.getRegion());



            //现在vip传过来的是时间
            //GOTO  会员标识
            //现在vip传过来的是时间
            ((UserinfoHolder) viewHolder).vip_gray.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(currentItem.getSvip())){
                if (TextUtils.isEmpty(currentItem.getSvip())){
                    Resources resources = mContext.getResources();
                    Drawable drawable = resources.getDrawable(R.mipmap.huizhang,null);
                    ((UserinfoHolder) viewHolder).userVIP.setForeground(drawable);


                    Glide.with(mContext)
                            .asBitmap()
                            .load(R.drawable.ic_vip_gray)
                            .into(((UserinfoHolder) viewHolder).vip_gray);
                }else {
                    int vip_time = Integer.parseInt(currentItem.getSvip()+"");
                    if (vip_time > current_timestamp) {
                        //vipicon分级
                        Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                        if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_diamond)
                                    .into(((UserinfoHolder) viewHolder).vip_gray);
                        } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_black)
                                    .into(((UserinfoHolder) viewHolder).vip_gray);
                        } else {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_white)
                                    .into(((UserinfoHolder) viewHolder).vip_gray);
                        }
                    } else {
                        Resources resources = mContext.getResources();
                        Drawable drawable = resources.getDrawable(R.mipmap.huizhang, null);
                        ((UserinfoHolder) viewHolder).userVIP.setForeground(drawable);

                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_vip_gray)
                                .into(((UserinfoHolder) viewHolder).vip_gray);
                    }
                }
            }else {
                int svip_time = Integer.parseInt(currentItem.getSvip()+"");
                if (svip_time > current_timestamp) {
                    //vipicon分级
                    Log.d("会员时长", ((svip_time - current_timestamp) + ""));
                    if ((svip_time - current_timestamp) < 3600 * 24 * 30) {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_svip_diamond)
                                .into(((UserinfoHolder) viewHolder).vip_gray);
                    } else if ((svip_time - current_timestamp) < 3600 * 24 * 180) {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_svip_black)
                                .into(((UserinfoHolder) viewHolder).vip_gray);
                    } else {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_svip_white)
                                .into(((UserinfoHolder) viewHolder).vip_gray);
                    }
                } else {
                    if (TextUtils.isEmpty(currentItem.getVip())){
                        Resources resources = mContext.getResources();
                        Drawable drawable = resources.getDrawable(R.mipmap.huizhang,null);
                        ((UserinfoHolder) viewHolder).userVIP.setForeground(drawable);

                        ((UserinfoHolder) viewHolder).vip_gray.setVisibility(View.VISIBLE);
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_vip_gray)
                                .into(((UserinfoHolder) viewHolder).vip_gray);
                    }else {
                        int vip_time = Integer.parseInt(currentItem.getVip()+"");
                        if (vip_time > current_timestamp) {
                            //vipicon分级
                            Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                            if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_vip_diamond)
                                        .into(((UserinfoHolder) viewHolder).vip_gray);
                            } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_vip_black)
                                        .into(((UserinfoHolder) viewHolder).vip_gray);
                            } else {
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_vip_white)
                                        .into(((UserinfoHolder) viewHolder).vip_gray);
                            }
                        } else {
                            Resources resources = mContext.getResources();
                            Drawable drawable = resources.getDrawable(R.mipmap.huizhang, null);
                            ((UserinfoHolder) viewHolder).userVIP.setForeground(drawable);

                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_gray)
                                    .into(((UserinfoHolder) viewHolder).vip_gray);
                        }
                    }
                }
            }



            ((UserinfoHolder) viewHolder).userinfoLayout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (Common.myID ==null){
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                                .setTitle("登录后可以发起聊天！")
                                .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intentSignin = new Intent(mContext, SigninActivity.class);
                                        mContext.startActivity(intentSignin);
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        builder.create().show();
                    }else {
                        Intent intent = new Intent(v.getContext(), PersonageActivity.class);
                        intent.putExtra("userID", currentItem.getId());
                        v.getContext().startActivity(intent);
                    }
                }
            });


            if(userInfoCallBack!=null){
                ((UserinfoHolder) viewHolder).custom_btn.setVisibility(View.VISIBLE);

                ((UserinfoHolder) viewHolder).userinfoLayout.setOnLongClickListener(new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View v) {
                        Log.d(TAG, "长按中");
                        final DialogPlus dialog = DialogPlus.newDialog(mContext)
                                .setAdapter(new BaseAdapter() {
                                    @Override
                                    public int getCount() {
                                        return 0;
                                    }

                                    @Override
                                    public Object getItem(int position) {
                                        return null;
                                    }

                                    @Override
                                    public long getItemId(int position) {
                                        return 0;
                                    }

                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        return null;
                                    }
                                })
                                .setFooter(R.layout.dialog_foot_confirm)
                                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                                .create();
                        dialog.show();
                        View view = dialog.getFooterView();
                        TextView prompt = view.findViewById(R.id.prompt_tv);
                        prompt.setText("确定要删除吗？");
                        Button dismissdialog_btn = view.findViewById(R.id.cancel_btn);
                        dismissdialog_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        Button confirm_btn = view.findViewById(R.id.confirm_btn);
                        confirm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                OkHttpInstance.deleteFriendNumber(currentItem.getId(),responseString -> {
                                    userInfoLists.remove(i);
                                    notifyDataSetChanged();
                                });
                                dialog.dismiss();
                            }
                        });
                        return true;
                    }
                });

                ((UserinfoHolder) viewHolder).custom_btn.setOnClickListener(v -> {
                    ((UserinfoHolder) viewHolder).custom_btn.setVisibility(View.GONE);
                    userInfoCallBack.getUserInfo(currentItem);
                    Toast.makeText(mContext,"已同意好友申请！",Toast.LENGTH_SHORT).show();
                });
                if (currentItem.getAgree().equals("1")){
                    ((UserinfoHolder) viewHolder).custom_btn.setText("已加");
                    ((UserinfoHolder) viewHolder).custom_btn.setClickable(false);
                    ((UserinfoHolder) viewHolder).custom_btn.setBackgroundResource(R.drawable.corner_gray_bg);
                }else {
                    ((UserinfoHolder) viewHolder).custom_btn.setText("同意");
                    ((UserinfoHolder) viewHolder).custom_btn.setClickable(true);
                    ((UserinfoHolder) viewHolder).custom_btn.setBackgroundResource(R.drawable.bd_bg_square_round_corner_blue);
                }
            }else {
                ((UserinfoHolder) viewHolder).custom_btn.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(currentItem.getGift_string())){
                if (currentItem.getGift_string().contains("gift")){
                    ((UserinfoHolder) viewHolder).sent_gift_recyclerview.setAdapter(new GiftSentAdapter(mContext, JSON.parseArray(currentItem.getGift_string(), GiftList.class)));
                    ((UserinfoHolder) viewHolder).sent_gift_recyclerview.setLayoutManager(new GridLayoutManager(mContext,4));
                    ((UserinfoHolder) viewHolder).gift_layout.setVisibility(View.VISIBLE);
                }else {
                    ((UserinfoHolder) viewHolder).gift_layout.setVisibility(View.GONE);
                }
            }else {
                ((UserinfoHolder) viewHolder).gift_layout.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(currentItem.getYourleavewords())){
                ((UserinfoHolder) viewHolder).remaindwords_layout.setVisibility(View.VISIBLE);
                ((UserinfoHolder) viewHolder).remaindwords_tv.setText("  "+currentItem.getYourleavewords());
            }else {
                ((UserinfoHolder) viewHolder).remaindwords_layout.setVisibility(View.GONE);
            }


            if (!currentItem.getRp_verify_time().equals("0")){
                ((UserinfoHolder) viewHolder).rp_verify_img.setVisibility(View.VISIBLE);
            }else {
                ((UserinfoHolder) viewHolder).rp_verify_img.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return userInfoLists.size();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private class SliderHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "SliderHolder";

        public SliderHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "SliderHolder: 进入");
            mDemoSlider = itemView.findViewById(R.id.recyclerview_slider);
        }
    }


    public class UserinfoHolder extends RecyclerView.ViewHolder{

        private static final String TAG = "UserinfoHolder";
        TextView userID;
        CircleImageView userPortrait;
        TextView userNickName;
        TextView userAge;
        TextView userGender;
        TextView userProperty;
        TextView userLocation;
        TextView userRegion;
        TextView userVIP;
        LinearLayout userinfoLayout;

        ImageView vip_gray;
        ImageView vip_diamond;
        ImageView vip_black;
        ImageView vip_white;

        TextView online_tv;
        ImageView online_img;

        Button custom_btn;
        RecyclerView sent_gift_recyclerview;
        LinearLayout gift_layout;

        LinearLayout remaindwords_layout;
        TextView remaindwords_tv;

        PortraitFrameView portraitFrameView;

        ImageView lv_img;

        ImageView rp_verify_img;
        public UserinfoHolder(@NonNull View itemView) {
            super(itemView);

            rp_verify_img = itemView.findViewById(R.id.rp_verify_img);

            lv_img = itemView.findViewById(R.id.lv_img);
            portraitFrameView = itemView.findViewById(R.id.portraitFrameView);

            remaindwords_layout = itemView.findViewById(R.id.remaindwords_layout);
            remaindwords_tv = itemView.findViewById(R.id.remaindwords_tv);

            userID = itemView.findViewById(R.id.userID);
            userPortrait = itemView.findViewById(R.id.authportrait);
            userNickName = itemView.findViewById(R.id.userNickName);
            userAge = itemView.findViewById(R.id.userAge);
            //userGender = itemView.findViewById(R.id.userGender);
            userProperty = itemView.findViewById(R.id.userProperty);
            userLocation = itemView.findViewById(R.id.userLocation);
            userRegion = itemView.findViewById(R.id.userRegion);
            userVIP = itemView.findViewById(R.id.userVIP);

            vip_gray = itemView.findViewById(R.id.vip_gray);
            vip_diamond = itemView.findViewById(R.id.vip_diamond);
            vip_black = itemView.findViewById(R.id.vip_black);
            vip_white = itemView.findViewById(R.id.vip_white);

            userinfoLayout = itemView.findViewById(R.id.userinfo_layout);

            online_tv = itemView.findViewById(R.id.online_tv);
            online_img = itemView.findViewById(R.id.online_img);
            Log.d(TAG, "UserinfoHolder: 进入");


            custom_btn = itemView.findViewById(R.id.custom_btn);
            sent_gift_recyclerview = itemView.findViewById(R.id.sent_gift_recyclerview);
            gift_layout = itemView.findViewById(R.id.gift_layout);
        }
    }
}
