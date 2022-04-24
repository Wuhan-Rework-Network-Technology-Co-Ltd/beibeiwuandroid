package xin.banghua.beiyuan.Adapter;

import static xin.banghua.beiyuan.utils.ThreadUtils.runOnUiThread;
import static xin.banghua.onekeylogin.Constant.THEME_KEY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.chatroom.activity.ChatRoomActivity;
import io.agora.chatroom.manager.RtmManager;
import io.agora.chatroom.util.PortraitFrameView;
import io.agora.chatroom.util.WealthAndGlamour;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmMessage;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.App;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.Main4Branch.DouyinActivity;
import xin.banghua.beiyuan.Main4Branch.PostListActivity;
import xin.banghua.beiyuan.Main4Branch.VideoPlayActivity;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SliderWebViewActivity;
import xin.banghua.beiyuan.chat.VideoChatViewActivity;
import xin.banghua.beiyuan.chat.VoiceChatViewActivity;
import xin.banghua.beiyuan.comment.CommentDialog;
import xin.banghua.beiyuan.custom_ui.CustomVideoView;
import xin.banghua.beiyuan.custom_ui.MatchDialog;
import xin.banghua.beiyuan.custom_ui.NiceImageView;
import xin.banghua.beiyuan.custom_ui.NiceZoomImageView;
import xin.banghua.beiyuan.script.ScriptActivity;
import xin.banghua.beiyuan.topic.TopicAddedItem;
import xin.banghua.beiyuan.topic.TopicList;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;
import xin.banghua.onekeylogin.login.OneKeyLoginActivity;

public class LuntanSliderAdapter extends RecyclerView.Adapter  implements  ViewPagerEx.OnPageChangeListener {
    private static final String TAG = "LuntanAdapter";

    LuntanSliderAdapter luntanSliderAdapter;
    Integer current_timestamp = Math.round(new Date().getTime()/1000);
    //幻灯片
    SliderLayout mDemoSlider;
    JSONArray jsonArray;
    ViewHolder viewHolder_btn;
    public List<LuntanList> luntanLists;
    private Context mContext;
    private final static int TYPE_HEAD = 0;
    private final static int TYPE_CONTENT = 1;


    String slidesort = "首页";
    public LuntanSliderAdapter(List<LuntanList> luntanLists,Context mContext,String slidesort) {
        Log.d(TAG, "LuntanAdapter: start"+luntanLists.size());


        this.luntanLists = luntanLists;
        this.mContext = mContext;
        this.slidesort = slidesort;

        luntanSliderAdapter = this;

        Log.d(TAG, "4测试黑名单: "+luntanLists.size());

    }
    //替换数据，并更新
    public void setData(List<LuntanList> luntanLists){
        this.luntanLists = luntanLists;
        notifyDataSetChanged();
    }
    //替换数据，并更新
    public void swapData(List<LuntanList> luntanLists){
        int oldSize = this.luntanLists.size();
        int newSize = luntanLists.size();
        this.luntanLists = luntanLists;
        notifyItemRangeInserted(oldSize , newSize);
    }
    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: position"+position);
        if ( position == 0){ // 头部
            Log.d(TAG, "getItemViewType: 头");
            return TYPE_HEAD;
        }else{
            Log.d(TAG, "getItemViewType: 身");
            return TYPE_CONTENT;
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,final int i) {
        Log.d(TAG, "onCreateViewHolder: 进入");
        if (i == TYPE_HEAD){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.slider_bar,viewGroup,false);
            LuntanSliderAdapter.SliderHolder viewHolder = new LuntanSliderAdapter.SliderHolder(view);
            return viewHolder;
        }else if(i == TYPE_CONTENT){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.guangchang_fragment_adapter,viewGroup,false);
            LuntanSliderAdapter.ViewHolder viewHolder = new LuntanSliderAdapter.ViewHolder(view);
            return viewHolder;
        }
        return null;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);
        if (viewHolder instanceof LuntanSliderAdapter.ViewHolder){
            ((ViewHolder) viewHolder).portraitFrameView.setPortraitFrame(((ViewHolder) viewHolder).portraitFrameView.getTag().toString());
        }
    }
    HashMap<Integer,ViewHolder> viewHolders = new HashMap<Integer,ViewHolder>();
    public ViewHolder getViewHolder(int position){
        return viewHolders.get(position);
    }


    private void startVoiceMatch(){
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
            OkHttpInstance.xiaobeiMatch("语音", new OkHttpResponseCallBack() {
                @Override
                public void getResponseString(String responseString) {
                    if (responseString.equals("wait")){
                        VoiceChatViewActivity.targetId = null;
                        Intent intent = new Intent(mContext, VoiceChatViewActivity.class);
                        intent.putExtra("channel", "match"+Common.userInfoList.getId());
                        intent.putExtra("targetId", Common.userInfoList.getId());
                        intent.putExtra("targetPortrait", Common.userInfoList.getPortrait());
                        intent.putExtra("myRole", "remote");
                        mContext.startActivity(intent);
                    }else {
                        UserInfoList userInfoList = JSON.parseObject(responseString,UserInfoList.class);
                        RtmMessage message = RtmManager.instance(mContext).getRtmClient().createMessage();
                        message.setText("{\"type\":\"match_voice\"}");
                        RtmManager.instance(mContext).getRtmClient().sendMessageToPeer(userInfoList.getId(), message, RtmManager.instance(mContext).getSendMessageOptions(), new ResultCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: 声网消息发送成功");
                                UserInfoList userInfoList = JSON.parseObject(responseString,UserInfoList.class);
                                Intent intent = new Intent(mContext, VoiceChatViewActivity.class);
                                intent.putExtra("channel", "match"+userInfoList.getId());
                                intent.putExtra("targetId", Common.userInfoList.getId());
                                intent.putExtra("targetPortrait", Common.userInfoList.getPortrait());
                                intent.putExtra("myRole", "remote");
                                mContext.startActivity(intent);
                            }
                            @Override
                            public void onFailure(ErrorInfo errorInfo) {
                                Log.d(TAG, "onFailure: 声网消息发送失败"+errorInfo.toString());
                                startVoiceMatch();
                            }
                        });
                    }
                }
            });
        }
    }

    private void startVideoMatch(){
        if (Common.myID ==null){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
                    .setTitle("登录后可以发起匹配！")
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
            OkHttpInstance.xiaobeiMatch("视频", new OkHttpResponseCallBack() {
                @Override
                public void getResponseString(String responseString) {
                    if (responseString.equals("wait")){
                        VideoChatViewActivity.targetId = null;
                        Intent intent = new Intent(mContext, VideoChatViewActivity.class);
                        intent.putExtra("channel", "match"+Common.userInfoList.getId());
                        intent.putExtra("targetId", Common.userInfoList.getId());
                        intent.putExtra("targetPortrait", Common.userInfoList.getPortrait());
                        intent.putExtra("myRole", "remote");
                        mContext.startActivity(intent);
                    }else {
                        UserInfoList userInfoList = JSON.parseObject(responseString,UserInfoList.class);
                        RtmMessage message = RtmManager.instance(mContext).getRtmClient().createMessage();
                        message.setText("{\"type\":\"match_video\"}");
                        RtmManager.instance(mContext).getRtmClient().sendMessageToPeer(userInfoList.getId(), message, RtmManager.instance(mContext).getSendMessageOptions(), new ResultCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: 声网消息发送成功");
                                UserInfoList userInfoList = JSON.parseObject(responseString,UserInfoList.class);
                                Intent intent = new Intent(mContext, VideoChatViewActivity.class);
                                intent.putExtra("channel", "match"+userInfoList.getId());
                                intent.putExtra("targetId", Common.userInfoList.getId());
                                intent.putExtra("targetPortrait", Common.userInfoList.getPortrait());
                                intent.putExtra("myRole", "remote");
                                mContext.startActivity(intent);
                            }
                            @Override
                            public void onFailure(ErrorInfo errorInfo) {
                                Log.d(TAG, "onFailure: 声网消息发送失败"+errorInfo.toString());
                                startVideoMatch();
                            }
                        });
                    }
                }
            });
        }
    }
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder,final int i) {
        int currentPosition = i;
        if (viewHolder instanceof LuntanSliderAdapter.SliderHolder){
            if (slidesort.equals("精华")){
                ((SliderHolder) viewHolder).slider_layout.setVisibility(View.GONE);
                ((SliderHolder) viewHolder).match_layout.setVisibility(View.VISIBLE);

                Glide.with(App.getApplication()).load(R.mipmap.peiyin).into(((SliderHolder) viewHolder).script_img);
                ((SliderHolder) viewHolder).script_img.setOnClickListener(v -> {
                    if (Common.myID ==null){
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
                                .setTitle("登录后可以发起匹配！")
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
                        mContext.startActivity(new Intent(mContext, ScriptActivity.class));
                    }
                });

                Glide.with(App.getApplication()).load(R.mipmap.voice_match).into(((SliderHolder) viewHolder).voice_match_img);
                ((SliderHolder) viewHolder).voice_match_img.setOnClickListener(v -> {
                    if (Common.myID ==null){
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
                                .setTitle("登录后可以发起匹配！")
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
                        MatchDialog matchDialog = new MatchDialog(mContext);
                        matchDialog.initShow(new OkHttpResponseCallBack() {
                            @Override
                            public void getResponseString(String responseString) {
                                ((SliderHolder) viewHolder).precaution_fraud.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startVoiceMatch();
                                    }
                                },1000);
                            }
                        });
                    }
                });

                Glide.with(App.getApplication()).load(R.mipmap.video_match).into(((SliderHolder) viewHolder).video_match_img);
                ((SliderHolder) viewHolder).video_match_img.setOnClickListener(v -> {
                    if (Common.myID ==null){
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
                                .setTitle("登录后可以发起匹配！")
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
                        MatchDialog matchDialog = new MatchDialog(mContext);
                        matchDialog.initShow(new OkHttpResponseCallBack() {
                            @Override
                            public void getResponseString(String responseString) {
                                ((SliderHolder) viewHolder).precaution_fraud.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startVideoMatch();
                                    }
                                },1000);
                            }
                        });
                    }
                });

                Glide.with(App.getApplication()).load(R.mipmap.douyin).into(((SliderHolder) viewHolder).video_dynamic_img);
                ((SliderHolder) viewHolder).video_dynamic_img.setOnClickListener(v -> {
                    if (Common.myID ==null){
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
                                .setTitle("登录后可以发起匹配！")
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
                        mContext.startActivity(new Intent(mContext, DouyinActivity.class));
                    }
                });
            }else {
                ((SliderHolder) viewHolder).slider_layout.setVisibility(View.VISIBLE);
                ((SliderHolder) viewHolder).match_layout.setVisibility(View.GONE);
                OkHttpInstance.getSlide(slidesort, new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        try {
                            List<SlideList> slideLists = JSON.parseArray(responseString,SlideList.class);

                            Glide.with(App.getApplication()).load(slideLists.get(0).getSlidepicture()).into(((SliderHolder) viewHolder).leyuan_convention);
                            ((SliderHolder) viewHolder).leyuan_convention.setOnClickListener(v -> {
                                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
                                intent.putExtra("slidername",slideLists.get(0).getSlidename());
                                intent.putExtra("sliderurl",slideLists.get(0).getSlideurl());
                                mContext.startActivity(intent);
                            });

                            Glide.with(App.getApplication()).load(slideLists.get(1).getSlidepicture()).into(((SliderHolder) viewHolder).precaution_fraud);
                            ((SliderHolder) viewHolder).precaution_fraud.setOnClickListener(v -> {
                                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
                                intent.putExtra("slidername",slideLists.get(1).getSlidename());
                                intent.putExtra("sliderurl",slideLists.get(1).getSlideurl());
                                mContext.startActivity(intent);
                            });
                        }catch (Exception e){
                            Log.e(TAG, "getResponseString: 抛出异常");
                        }
                    }
                });
            }
//            HashMap<String,String> url_maps = new HashMap<String, String>();
//            if (jsonArray.length()>0){
//                for (int j=0;j<jsonArray.length();j++){
//                    try {
//                       final JSONObject jsonObject = jsonArray.getJSONObject(j);
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
        }else if (viewHolder instanceof LuntanSliderAdapter.ViewHolder){
            final LuntanList currentItem = luntanLists.get(currentPosition-1);
            Log.d(TAG, "onBindViewHolder: 测试帖子"+currentItem.getAuthid()+currentItem.getAuthnickname());

            ((ViewHolder) viewHolder).wealth_view.initShow("wealth",currentItem.getAll_money());
            ((ViewHolder) viewHolder).glamour_view.initShow("glamour",currentItem.getAll_income());

            ((ViewHolder) viewHolder).lv_img.setImageResource(Common.getLevelFromPost(currentItem));

            viewHolders.put(i,((ViewHolder) viewHolder));
            String authattributes_string = currentItem.getAuthage()+"岁 | "+currentItem.getAuthgender()+" | "+currentItem.getAuthregion()+" | "+currentItem.getAuthproperty();
            //((ViewHolder) viewHolder).authattributes.setText(authattributes_string);

            //((ViewHolder) viewHolder).id.setText(currentItem.getId());
            //((ViewHolder) viewHolder).plateid.setText(currentItem.getPlateid());
            //((ViewHolder) viewHolder).platename.setText(currentItem.getPlatename());
            //((ViewHolder) viewHolder).authid.setText(currentItem.getAuthid());
            ((ViewHolder) viewHolder).authnickname.setText(currentItem.getAuthnickname());


            ((ViewHolder) viewHolder).online_tv.setText(currentItem.getOnline());
            if (currentItem.getOnline().equals("在线")){
                Glide.with(mContext).load(R.mipmap.green_point).into( ((ViewHolder) viewHolder).online_img);
            }else {
                Glide.with(mContext).load(R.mipmap.gray_point).into( ((ViewHolder) viewHolder).online_img);
            }

            ((ViewHolder) viewHolder).portraitFrameView.setPortraitFrame(currentItem.getPortraitframe());
            ((ViewHolder) viewHolder).portraitFrameView.setTag(currentItem.getPortraitframe());
            Glide.with(mContext)
                    .asBitmap()
                    .load(Common.getOssResourceUrl(currentItem.getAuthportrait()))
                    .into(((ViewHolder) viewHolder).authportrait);



            if (currentItem.platename.equals("招募令")){
                ((ViewHolder) viewHolder).goToRoomBtn.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).goToRoomBtn.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, ChatRoomActivity.class);
                    intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_ID, currentItem.getAuthid());
                    intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_TYPE, "处CP");
                    intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, io.agora.chatroom.R.mipmap.bg_channel_0);
                    //intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, Constant.sRoomBG);
                    mContext.startActivity(intent);
                });
            }else {
                ((ViewHolder) viewHolder).goToRoomBtn.setVisibility(View.GONE);
            }

            //GOTO  会员标识
            //现在vip传过来的是时间
            ((ViewHolder) viewHolder).vip_gray.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(currentItem.getAuthsvip())){
                if (TextUtils.isEmpty(currentItem.getAuthvip())){
                    ((ViewHolder) viewHolder).vip_gray.setVisibility(View.GONE);
                    Glide.with(mContext)
                            .asBitmap()
                            .load(R.drawable.ic_vip_gray)
                            .into(((ViewHolder) viewHolder).vip_gray);
                }else {
                    Log.d("会员时间",currentItem.getAuthvip()+"");
                    int vip_time = Integer.parseInt(currentItem.getAuthvip()+"");
                    if (vip_time > current_timestamp) {
                        ((ViewHolder) viewHolder).vip_gray.setVisibility(View.VISIBLE);
                        //vipicon分级
                        Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                        if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_diamond)
                                    .into(((ViewHolder) viewHolder).vip_gray);
                        } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_black)
                                    .into(((ViewHolder) viewHolder).vip_gray);
                        } else {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_white)
                                    .into(((ViewHolder) viewHolder).vip_gray);
                        }
                    } else {
                        ((ViewHolder) viewHolder).vip_gray.setVisibility(View.GONE);
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_vip_gray)
                                .into(((ViewHolder) viewHolder).vip_gray);
                    }
                }
            }else {
                Log.d("会员时间",currentItem.getAuthsvip()+"");
                int svip_time = Integer.parseInt(currentItem.getAuthsvip()+"");
                if (svip_time > current_timestamp) {
                    ((ViewHolder) viewHolder).vip_gray.setVisibility(View.VISIBLE);
                    //vipicon分级
                    Log.d("会员时长", ((svip_time - current_timestamp) + ""));
                    if ((svip_time - current_timestamp) < 3600 * 24 * 30) {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_svip_diamond)
                                .into(((ViewHolder) viewHolder).vip_gray);
                    } else if ((svip_time - current_timestamp) < 3600 * 24 * 180) {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_svip_black)
                                .into(((ViewHolder) viewHolder).vip_gray);
                    } else {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_svip_white)
                                .into(((ViewHolder) viewHolder).vip_gray);
                    }
                } else {
                    if (TextUtils.isEmpty(currentItem.getAuthvip())){
                        ((ViewHolder) viewHolder).vip_gray.setVisibility(View.GONE);
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_vip_gray)
                                .into(((ViewHolder) viewHolder).vip_gray);
                    }else {
                        Log.d("会员时间",currentItem.getAuthvip()+"");
                        int vip_time = Integer.parseInt(currentItem.getAuthvip()+"");
                        if (vip_time > current_timestamp) {
                            ((ViewHolder) viewHolder).vip_gray.setVisibility(View.VISIBLE);
                            //vipicon分级
                            Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                            if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_vip_diamond)
                                        .into(((ViewHolder) viewHolder).vip_gray);
                            } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_vip_black)
                                        .into(((ViewHolder) viewHolder).vip_gray);
                            } else {
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_vip_white)
                                        .into(((ViewHolder) viewHolder).vip_gray);
                            }
                        } else {
                            ((ViewHolder) viewHolder).vip_gray.setVisibility(View.GONE);
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_gray)
                                    .into(((ViewHolder) viewHolder).vip_gray);
                        }
                    }
                }
            }





            //((ViewHolder) viewHolder).posttip.setText(currentItem.getPosttip().isEmpty()?"":currentItem.getPosttip());
            ((ViewHolder) viewHolder).posttip.setText("加精");
            if (currentItem.getPosttip().equals("加精")){
//                Resources resources = mContext.getResources();
//                Drawable drawable = resources.getDrawable(R.drawable.ic_essence,null);
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                ((ViewHolder) viewHolder).posttip.setCompoundDrawables(drawable,null,null,null);
                ((ViewHolder) viewHolder).posttip.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).posttop.setVisibility(View.GONE);
            }else if(currentItem.getPosttip().equals("置顶")){
//                Resources resources = mContext.getResources();
//                Drawable drawable = resources.getDrawable(R.drawable.ic_ontop,null);
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                ((ViewHolder) viewHolder).posttip.setCompoundDrawables(drawable,null,null,null);
                ((ViewHolder) viewHolder).posttip.setVisibility(View.GONE);
                ((ViewHolder) viewHolder).posttop.setVisibility(View.VISIBLE);
            }else if (currentItem.getPosttip().equals("置顶,加精")){
//                Resources resources = mContext.getResources();
//                Drawable drawable1 = resources.getDrawable(R.drawable.ic_essence,null);
//                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
//                Drawable drawable2 = resources.getDrawable(R.drawable.ic_ontop,null);
//                drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
//                ((ViewHolder) viewHolder).posttip.setCompoundDrawables(drawable1,null,drawable2,null);
                ((ViewHolder) viewHolder).posttip.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).posttop.setVisibility(View.VISIBLE);
            }else {
                //必须加上，否则会错乱
//                ((ViewHolder) viewHolder).posttip.setCompoundDrawables(null,null,null,null);
                ((ViewHolder) viewHolder).posttip.setVisibility(View.GONE);
                ((ViewHolder) viewHolder).posttop.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(currentItem.getPosttitle())){
                ((ViewHolder) viewHolder).posttitle.setVisibility(View.GONE);
            }else {
                ((ViewHolder) viewHolder).posttitle.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).posttitle.setText(currentItem.getPosttitle());
            }

            if (currentItem.getPosttext().length()>50) {
                ((ViewHolder) viewHolder).posttext.setText(currentItem.getPosttext().substring(0, 50)+"......");
                ((ViewHolder) viewHolder).detail_content.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).detail_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ViewHolder) viewHolder).posttext.setText(currentItem.getPosttext());
                        ((ViewHolder) viewHolder).detail_content.setVisibility(View.GONE);
                    }
                });
            }else {
                ((ViewHolder) viewHolder).posttext.setText(currentItem.getPosttext());
                ((ViewHolder) viewHolder).detail_content.setVisibility(View.GONE);
            }

//            if (currentItem.getPostpictureList().length!=1){
//                ((ViewHolder) viewHolder).postpicture.setVisibility(View.GONE);
//            }else if (currentItem.getPostpictureList().length==1){
//                Glide.with(mContext)
//                        .asBitmap()
//                        .load(Common.getOssResourceUrl(currentItem.getPostpictureList()[0]))
//                        .into(((ViewHolder) viewHolder).postpicture);
//                ((ViewHolder) viewHolder).postpicture.setVisibility(View.VISIBLE);
//                ((ViewHolder) viewHolder).postpicture.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(v.getContext(), ImagerPagerActivity.class);
//                        intent.putExtra("luntanlist", currentItem);
//                        intent.putExtra("image_index", 0);
//                        v.getContext().startActivity(intent);
//                    }
//                });
//            }
//            if (currentItem.getPostpictureList().length<2){
//                ((ViewHolder) viewHolder).postpicture1.setVisibility(View.GONE);
//            }else {
//                Glide.with(mContext)
//                        .asBitmap()
//                        .load(Common.getOssResourceUrl(currentItem.getPostpictureList()[0]))
//                        .into(((ViewHolder) viewHolder).postpicture1);
//                ((ViewHolder) viewHolder).postpicture1.setVisibility(View.VISIBLE);
//                ((ViewHolder) viewHolder).postpicture1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(v.getContext(), ImagerPagerActivity.class);
//                        intent.putExtra("luntanlist", currentItem);
//                        intent.putExtra("image_index", 0);
//                        v.getContext().startActivity(intent);
//                    }
//                });
//            }
//            if (currentItem.getPostpictureList().length<2){
//                ((ViewHolder) viewHolder).postpicture2.setVisibility(View.GONE);
//            }else {
//                Glide.with(mContext)
//                        .asBitmap()
//                        .load(Common.getOssResourceUrl(currentItem.getPostpictureList()[1]))
//                        .into(((ViewHolder) viewHolder).postpicture2);
//                ((ViewHolder) viewHolder).postpicture2.setVisibility(View.VISIBLE);
//                ((ViewHolder) viewHolder).postpicture2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(v.getContext(), ImagerPagerActivity.class);
//                        intent.putExtra("luntanlist", currentItem);
//                        intent.putExtra("image_index", 1);
//                        v.getContext().startActivity(intent);
//                    }
//                });
//            }

            if (currentItem.getPostpicture().contains("images")){
                String[] postPicture = currentItem.getPostpicture().split(",");
                PictureAdapter pictureAdapter = new PictureAdapter(mContext,currentItem);
                ((ViewHolder) viewHolder).recyclerView.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).recyclerView.setAdapter(pictureAdapter);
                if (postPicture.length==1){
                    ((ViewHolder) viewHolder).recyclerView.setLayoutManager(new GridLayoutManager(mContext,1));
                }else if(postPicture.length==2 || postPicture.length==4){
                    ((ViewHolder) viewHolder).recyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
                }else {
                    ((ViewHolder) viewHolder).recyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
                }

                pictureAdapter.setPictures(Arrays.asList(postPicture));


            }else {
                ((ViewHolder) viewHolder).recyclerView.setVisibility(View.GONE);
            }

//            if (currentItem.getPostpictureList().length<3){
//                ((ViewHolder) viewHolder).postpicture3.setVisibility(View.GONE);
//            }else {
//                Glide.with(mContext)
//                        .asBitmap()
//                        .load(Common.getOssResourceUrl(currentItem.getPostpictureList()[2]))
//                        .into(((ViewHolder) viewHolder).postpicture3);
//                ((ViewHolder) viewHolder).postpicture3.setVisibility(View.VISIBLE);
//                ((ViewHolder) viewHolder).postpicture3.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(v.getContext(), ImagerPagerActivity.class);
//                        intent.putExtra("luntanlist", currentItem);
//                        intent.putExtra("image_index", 2);
//                        v.getContext().startActivity(intent);
//                    }
//                });
//            }

            ((ViewHolder) viewHolder).like.setText(""+currentItem.getLike());
            ((ViewHolder) viewHolder).like_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ViewHolder) viewHolder).like.setTextColor(mContext.getResources().getColor(R.color.red));
                    Log.d(TAG, "onClick: clicked on: " + currentItem.getId());
                    //Toast.makeText(mContext, mUserID.get(i) + mUserNickName.get(i), Toast.LENGTH_LONG).show();
                    viewHolder_btn = (ViewHolder) viewHolder;
                    like("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=luntanlike&m=socialchat",currentItem.getId());
                }
            });
//            ((ViewHolder) viewHolder).favorite.setText(currentItem.getFavorite());
            ((ViewHolder) viewHolder).time.setText(currentItem.getTime());


            ((ViewHolder) viewHolder).authnickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToPersonage(currentItem.getAuthid());
                }
            });
            ((ViewHolder) viewHolder).authportrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToPersonage(currentItem.getAuthid());
                }
            });

//            ((MainViewHolder) viewHolder).posttitle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    intentPostlist(v,currentItem);
//                }
//            });
//            ((MainViewHolder) viewHolder).posttext.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    intentPostlist(v,currentItem);
//                }
//            });
//            ((MainViewHolder) viewHolder).postpicture.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    intentPostlist(v,currentItem);
//                }
//            });
//
//            ((MainViewHolder) viewHolder).luntanLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    intentPostlist(v,currentItem);
//                }
//            });

            ((ViewHolder) viewHolder).menu_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InformBlacklistAdapter adapter = new InformBlacklistAdapter(mContext, "post", currentItem.getId(), currentItem.getAuthid(), new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {
                            runOnUiThread(()->{
                                luntanLists.remove(currentPosition-1);
                                luntanSliderAdapter.notifyDataSetChanged();
                            });
                        }
                    });
                    final DialogPlus dialog = DialogPlus.newDialog(mContext)
                            .setAdapter(adapter)
                            .setFooter(R.layout.inform_blacklist_foot)
                            .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                            .create();
                    dialog.show();
                    View view = dialog.getFooterView();
                    Button cancel = view.findViewById(R.id.inform_blacklist_cancel_btn);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });


            CommentDialog commentDialog = new CommentDialog(mContext);
            ((ViewHolder) viewHolder).comment_tv.setText(""+currentItem.getComment_sum());
            ((ViewHolder) viewHolder).comment_layout.setOnClickListener(view -> {
                if (Common.myID==null){
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
                            .setTitle("登录以查看更多内容！")
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
                    ((ViewHolder) viewHolder).comment_tv.setTextColor(mContext.getResources().getColor(R.color.red));
                    commentDialog.loadComment(currentItem);
                    commentDialog.startLoadComment();
                }
            });

            ((ViewHolder) viewHolder).userAge.setText(" "+currentItem.getAuthage());
            ((ViewHolder) viewHolder).userRegion.setText(currentItem.getAuthregion());
            ((ViewHolder) viewHolder).userProperty.setText(currentItem.getAuthproperty());
            if (currentItem.getAuthgender().equals("男")){
                Resources resources = mContext.getResources();
                Drawable drawable = resources.getDrawable(R.mipmap.bboy,null);
                //((UserinfoHolder) viewHolder).userGender.setForeground(drawable);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ((ViewHolder) viewHolder).userAge.setCompoundDrawables(drawable, null, null, null);
                ((ViewHolder) viewHolder).userAge.setBackgroundResource(R.mipmap.h_sex_male);
            }else {
                Resources resources = mContext.getResources();
                Drawable drawable = resources.getDrawable(R.mipmap.ggirl,null);
                //((UserinfoHolder) viewHolder).userGender.setForeground(drawable);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ((ViewHolder) viewHolder).userAge.setCompoundDrawables(drawable, null, null, null);
                ((ViewHolder) viewHolder).userAge.setBackgroundResource(R.mipmap.h_sex);
            }

            Log.d(TAG, "onBindViewHolder: 帖子视频"+currentItem.getPostvideo());
            if (!currentItem.getPostvideo().equals("https://oss.banghua.xin/0")){
                if (Integer.parseInt(currentItem.getHeight())>=Integer.parseInt(currentItem.getWidth())){
                    ((ViewHolder) viewHolder).video_layout.getLayoutParams().height = 700;
                    int width = Math.round(700*((Float.parseFloat(currentItem.getWidth())/Float.parseFloat(currentItem.getHeight()))));
                    ((ViewHolder) viewHolder).video_layout.getLayoutParams().width = width;

                    ((ViewHolder) viewHolder).cover_view.getLayoutParams().height = 700;
                    ((ViewHolder) viewHolder).cover_view.getLayoutParams().width = width;
                }else {
                    ((ViewHolder) viewHolder).video_layout.getLayoutParams().width = 700;
                    int height = Math.round(700*((Float.parseFloat(currentItem.getHeight())/Float.parseFloat(currentItem.getWidth()))));
                    ((ViewHolder) viewHolder).video_layout.getLayoutParams().height = height;

                    ((ViewHolder) viewHolder).cover_view.getLayoutParams().width = 700;
                    ((ViewHolder) viewHolder).cover_view.getLayoutParams().height = height;
                }
                Glide.with(mContext).load(currentItem.getCover()).into(((ViewHolder) viewHolder).cover_view);

                ((ViewHolder) viewHolder).video_layout.setOnClickListener(v -> {
                    Log.d(TAG, "222luntanList.getPlay_once(): "+currentItem.getPlay_once());
                    Intent intent = new Intent(mContext, VideoPlayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("luntanList", (Serializable) currentItem);
                    mContext.startActivity(intent);
                });
                ((ViewHolder) viewHolder).video_layout.setVisibility(View.VISIBLE);
                if (currentItem.getStartVideo()){
                    ((ViewHolder) viewHolder).video_layout.addView(CustomVideoView.getInstance(mContext,currentItem),2);
                }
            }else {
                ((ViewHolder) viewHolder).video_layout.setVisibility(View.GONE);
            }


            ((ViewHolder) viewHolder).follow_img.setVisibility(View.VISIBLE);
            Log.d(TAG, "onBindViewHolder: 关注的人"+Common.followList.size());
            for (int j = 0;j < Common.followList.size();j++){
                Log.d(TAG, "onBindViewHolder: 关注的人"+Common.followList.get(j));
                if (Common.followList.get(j).getUserId().equals(currentItem.getAuthid())){
                    ((ViewHolder) viewHolder).follow_img.setVisibility(View.GONE);
                }
            }

            ((ViewHolder) viewHolder).follow_img.setOnClickListener(v -> {
                ((ViewHolder) viewHolder).follow_img.setVisibility(View.GONE);
                Toast.makeText(mContext,"关注成功",Toast.LENGTH_SHORT).show();
                OkHttpInstance.follow(currentItem.getAuthid(), responseString -> {
                    Common.followList.add(new FollowList(currentItem.getAuthid()));
                    notifyDataSetChanged();
                });
            });

            if (!TextUtils.isEmpty(currentItem.getTopic())){
                if (currentItem.getTopic()!="null"){
                    List<TopicList> topicLists = JSON.parseArray(currentItem.getTopic(),TopicList.class);
                    ((ViewHolder) viewHolder).topic_layout.removeAllViews();
                    for (int j = 0;j<topicLists.size();j++){
                        TopicAddedItem topicAddedItem = new TopicAddedItem(mContext);
                        topicAddedItem.initShow(topicLists.get(j));
                        ((ViewHolder) viewHolder).topic_layout.addView(topicAddedItem);
                    }
                }
            }

            if (!currentItem.getRp_verify_time().equals("0")){
                ((ViewHolder) viewHolder).rp_verify_img.setVisibility(View.VISIBLE);
            }else {
                ((ViewHolder) viewHolder).rp_verify_img.setVisibility(View.GONE);
            }
        }
    }

    private void goToPersonage(String userId){
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
            intent.putExtra("userID",userId);
            mContext.startActivity(intent);
        }
    }


    public void intentPostlist(View v,LuntanList currentItem){
        Intent intent = new Intent(v.getContext(), PostListActivity.class);
        intent.putExtra("postid",currentItem.getId());
        intent.putExtra("plateid",currentItem.getPlateid());
        intent.putExtra("platename",currentItem.getPlatename());
        intent.putExtra("authid",currentItem.getAuthid());
        intent.putExtra("authnickname",currentItem.getAuthnickname());
        intent.putExtra("authportrait",currentItem.getAuthportrait());
        intent.putExtra("posttip",currentItem.getPosttip());
        intent.putExtra("posttitle",currentItem.getPosttitle());
        intent.putExtra("posttext",currentItem.getPosttext());
        intent.putExtra("postpicture",currentItem.getPostpictureList());
        intent.putExtra("like",currentItem.getLike());
        intent.putExtra("favorite",currentItem.getFavorite());
        intent.putExtra("time",currentItem.getTime());
        v.getContext().startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return luntanLists.size()+1;
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

        LinearLayout slider_layout;
        ImageView leyuan_convention;
        ImageView precaution_fraud;

        LinearLayout match_layout;
        ImageView script_img;
        ImageView voice_match_img;
        ImageView video_match_img;
        ImageView video_dynamic_img;
        public SliderHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "SliderHolder: 进入");
            mDemoSlider = itemView.findViewById(R.id.recyclerview_slider);


            slider_layout = itemView.findViewById(R.id.slider_layout);
            leyuan_convention = itemView.findViewById(R.id.leyuan_convention);
            precaution_fraud = itemView.findViewById(R.id.precaution_fraud);

            match_layout = itemView.findViewById(R.id.match_layout);
            voice_match_img = itemView.findViewById(R.id.voice_match_img);
            video_match_img = itemView.findViewById(R.id.video_match_image);
            video_dynamic_img = itemView.findViewById(R.id.video_dynamic_image);

            script_img = itemView.findViewById(R.id.script_image);
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView plateid;
        TextView platename;
        TextView authid;
        TextView authnickname;
        CircleImageView authportrait;
        TextView posttip;
        LinearLayout posttop;
        TextView posttitle;
        TextView posttext;
        NiceZoomImageView postpicture;
        NiceZoomImageView postpicture1;
        NiceZoomImageView postpicture2;
        NiceZoomImageView postpicture3;
        TextView like;
        ImageView like_img;
        TextView comment_tv;
        ImageView comment_tv_img;
        TextView favorite;
        TextView time;
        Button detail_content;

        TextView authattributes;
        Button menu_btn;

        RelativeLayout luntanLayout;

        ImageView vip_gray;
        ImageView vip_diamond;
        ImageView vip_black;
        ImageView vip_white;


        TextView userAge;
        TextView userProperty;
        TextView userRegion;

        Button goToRoomBtn;

        LinearLayout comment_layout,like_layout;

        public CustomVideoView videoView;

        public FrameLayout video_layout;


        NiceImageView cover_view;
        ImageView film_cover_img;
        ImageView cover_img;

        TextView online_tv;
        ImageView online_img;
        ImageView follow_img;

        PortraitFrameView portraitFrameView;

        RecyclerView recyclerView;

        ImageView lv_img;

        LinearLayout topic_layout;

        ImageView rp_verify_img;

        WealthAndGlamour wealth_view;
        WealthAndGlamour glamour_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            wealth_view = itemView.findViewById(R.id.wealth_view);
            glamour_view = itemView.findViewById(R.id.glamour_view);

            rp_verify_img = itemView.findViewById(R.id.rp_verify_img);

            topic_layout = itemView.findViewById(R.id.topic_layout);

            lv_img = itemView.findViewById(R.id.lv_img);
            recyclerView = itemView.findViewById(R.id.recyclerview);
            portraitFrameView = itemView.findViewById(R.id.portraitFrameView);
            //id = itemView.findViewById(R.id.id);
            //plateid = itemView.findViewById(R.id.plateid);
            //platename = itemView.findViewById(R.id.platename);
            //authid = itemView.findViewById(R.id.authid);
            comment_layout = itemView.findViewById(R.id.comment_layout);
            like_layout = itemView.findViewById(R.id.like_layout);
            authnickname = itemView.findViewById(R.id.authnickname);
            authportrait = itemView.findViewById(R.id.authportrait);
            posttip = itemView.findViewById(R.id.posttip);
            posttop = itemView.findViewById(R.id.posttop);
            posttitle = itemView.findViewById(R.id.posttitle);
            posttext = itemView.findViewById(R.id.posttext);
            postpicture = itemView.findViewById(R.id.postpicture);
            postpicture1 = itemView.findViewById(R.id.postpicture1);
            postpicture2 = itemView.findViewById(R.id.postpicture2);
            postpicture3 = itemView.findViewById(R.id.postpicture3);
            like = itemView.findViewById(R.id.like);
            like_img = itemView.findViewById(R.id.like_img);
            comment_tv = itemView.findViewById(R.id.comment_tv);
            comment_tv_img = itemView.findViewById(R.id.comment_tv_img);
            like = itemView.findViewById(R.id.like);
            //favorite = itemView.findViewById(R.id.favorite);
            time = itemView.findViewById(R.id.time);

            luntanLayout = itemView.findViewById(R.id.luntanLayout);

            //authattributes = itemView.findViewById(R.id.authattributes);
            menu_btn = itemView.findViewById(R.id.menu_btn);

            detail_content = itemView.findViewById(R.id.detail_content);


            vip_gray = itemView.findViewById(R.id.vip_gray);
//            vip_diamond = itemView.findViewById(R.id.vip_diamond);
//            vip_black = itemView.findViewById(R.id.vip_black);
//            vip_white = itemView.findViewById(R.id.vip_white);


            userAge = itemView.findViewById(R.id.userAge);
            userProperty = itemView.findViewById(R.id.userProperty);
            userRegion = itemView.findViewById(R.id.userRegion);

            goToRoomBtn = itemView.findViewById(R.id.goToRoomBtn);


            videoView = itemView.findViewById(R.id.player);

            video_layout = itemView.findViewById(R.id.video_layout);
            cover_view = itemView.findViewById(R.id.cover_view);
            film_cover_img = itemView.findViewById(R.id.film_cover_img);
            cover_img = itemView.findViewById(R.id.cover_img);

            online_tv = itemView.findViewById(R.id.online_tv);
            online_img = itemView.findViewById(R.id.online_img);
            follow_img = itemView.findViewById(R.id.follow_img);
        }
    }


    //网络数据部分
//处理返回的数据
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是用户数据，2是幻灯片
            switch (msg.what){
                case 1:
                    viewHolder_btn.like.setText(""+msg.obj.toString());
                    break;
            }
        }
    };
    //TODO okhttp点赞
    public void like(final String url,final String postid){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("postid", postid)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=1;
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
