package io.agora.chatroom.gift;


import static io.agora.chatroom.ThreadUtils.runOnUiThread;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.chatroom.Common;
import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.chatroom.UserInfoList;
import io.agora.chatroom.manager.ChatRoomManager;
import io.agora.chatroom.manager.RtmManager;
import io.agora.chatroom.model.ChannelData;
import io.agora.chatroom.model.Constant;
import io.agora.chatroom.model.Member;
import io.agora.chatroom.widget.GiftPopView;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;

public class GiftDialog {
    public static GiftDialog uniqueInstance;
    //Singleton类只有一个构造方法并且是被private修饰的，所以用户无法通过new方法创建该对象实例
    public static GiftDialog getInstance(Activity mContext, Boolean isChatRoom,ConstraintLayout view,@Nullable GiftCallBack giftCallBack){
        uniqueInstance = new GiftDialog(mContext,isChatRoom);
        uniqueInstance.mContext = mContext;
        uniqueInstance.container = view;
        uniqueInstance.giftCallBack = giftCallBack;
        return uniqueInstance;
    }

    public static ConstraintLayout container;
    public static GiftCallBack giftCallBack;

    private static final String TAG = "DialogExample";

    /**  */
    public static final int DIALOG_EXAMPLE = 1;

    public static Activity mContext;
    private BottomSheetDialog bottomSheetDialog;

    @BindView(R2.id.iv_dialog_close)
    Button iv_dialog_close;


    //是否是聊天室，聊天室发送频道消息，不是聊天室发送个人消息
    Boolean isChatRoom = true;

    public GiftDialog(Activity mContext,Boolean isChatRoom) {
        this.mContext = mContext;
        this.isChatRoom = isChatRoom;
        initSheetDialog();

        if (!RtmManager.rtmClientListenerList.contains(mClientListener)){
            RtmManager.rtmClientListenerList.add(mClientListener);
            Log.d(TAG, "GiftDialog: 个人监听"+RtmManager.rtmClientListenerList.size());
        }
        if (!RtmManager.rtmChannelListenerList.contains(mChannelListener)){
            RtmManager.rtmChannelListenerList.add(mChannelListener);
            Log.d(TAG, "GiftDialog: 频道监听"+RtmManager.rtmChannelListenerList.size());
        }
    }
    View view;
    private float slideOffset = 0;
    private void initSheetDialog() {
        if (bottomSheetDialog != null) {
            return;
        }

        //view   需要设置中固定高度android:layout_height="844dp"
        view = View.inflate(mContext, R.layout.gift_dialog, null);

        ButterKnife.bind(this, view);


        OkHttpInstance.getUserAttributes(Common.myUserInfoList.getId(), new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                Common.myUserInfoList = JSON.parseObject(responseString, UserInfoList.class);
                remainder_money_tv.setText("余额："+Common.myUserInfoList.getMoney());
                remainder_money.setText("余额："+Common.myUserInfoList.getMoney());
            }
        });

        //dialog
        bottomSheetDialog = new BottomSheetDialog(mContext, R.style.dialog);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setOnDismissListener(dialog -> {
            //消失监听
        });
        BottomSheetBehavior mDialogBehavior = BottomSheetBehavior.from((View) view.getParent());
        mDialogBehavior.setPeekHeight(getWindowHeight());
        //dialog滑动监听
        mDialogBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mDialogBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    Log.d(TAG, "onStateChanged: slideOffset"+slideOffset);
                    if (slideOffset <= -0.12 && slideOffset != -1) {
                        //当向下滑动时 值为负数
                        bottomSheetDialog.dismiss();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //show的时候会先调用一次onSlide，导致slideOffset为-1，所以判断是否下滑隐藏时，加个判断slideOffset != -1
                GiftDialog.this.slideOffset = slideOffset;//记录滑动值
            }
        });


        iv_dialog_close.setOnClickListener(v -> bottomSheetDialog.dismiss());




        //svga播放器播放
        SVGAParser.Companion.shareParser().init(mContext);
        gift_player.setBackgroundResource(R.color.transparent);
        gift_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gift_player.stepToFrame(currentFrame++, true);
            }
        });
        gift_player.setCallback(new SVGACallback() {
            @Override
            public void onPause() {
                Log.d(TAG, "onPause: 动画暂停");
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "onFinished: 动画结束");
                bottomSheetDialog.dismiss();
            }

            @Override
            public void onRepeat() {
                //Log.d(TAG, "onRepeat: 动画重复");
            }

            @Override
            public void onStep(int i, double v) {
                Log.d(TAG, "onStep: 动画步骤"+i+"|"+v);
            }
        });
        parser = SVGAParser.Companion.shareParser();
    }

    private int getWindowHeight() {
        Resources res = mContext.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }


    @BindView(R2.id.gift_recyclerview)
    RecyclerView gift_recyclerview;

    List<GiftList> giftLists = new ArrayList<>();

    public void initShow(@Nullable Member member){

        if (member!=null){
            receiverMember = member;
        }else {
            receiverMember = new Member(Common.myUserInfoList.getId(), Common.myUserInfoList.getNickname(),
                    Common.myUserInfoList.getPortrait(),Common.myUserInfoList.getGender(),
                    Common.myUserInfoList.getProperty(),Common.myUserInfoList.getPortraitframe(),
                    Common.myUserInfoList.getVeilcel());
        }


        mChannelData = ChatRoomManager.instance(mContext).getChannelData();

        showSelectedBroadcaster();

        pay_layout.setVisibility(View.GONE);
        pay_method_layout.setVisibility(View.GONE);
        giftPopView.setVisibility(View.GONE);
        gift_recyclerview.setVisibility(View.VISIBLE);
        remainder_money_layout.setVisibility(View.VISIBLE);
        //自定义部分
        //adpter绑定数据
        GiftAdapter giftAdapter = new GiftAdapter(giftLists);
        gift_recyclerview.setAdapter(giftAdapter);
        gift_recyclerview.setLayoutManager(new GridLayoutManager(mContext,4));



        //获取礼物列表
        OkHttpInstance.getGiftList(responseString -> {
            giftLists = JSON.parseArray(responseString,GiftList.class);
            giftAdapter.setGiftLists(giftLists);
        });


        recharge_btn.setOnClickListener(v -> {
            switchStatus("选充值金额");
            initPay();
        });

        remainder_money_tv.setText("余额："+Common.myUserInfoList.getMoney());
        remainder_money.setText("余额："+Common.myUserInfoList.getMoney());

        slideOffset = 0;
        bottomSheetDialog.show();
    }



    @BindView(R2.id.gift_player)
    SVGAImageView gift_player;
    SVGAParser parser;
    int currentFrame = 0;

    Boolean isFinished = true;
    List<String> svgaList = new ArrayList<>();


    public void hideGift(){
        uniqueInstance.gift_recyclerview.setVisibility(View.GONE);
        uniqueInstance.select_receiver_layout.setVisibility(View.GONE);
        uniqueInstance.remainder_money_layout.setVisibility(View.GONE);
    }

    public void showSVGA(String svgaUrl){
        if (isFinished){
            isFinished = false;
            startSVGA(svgaUrl);
        }else {
            svgaList.add(svgaUrl);
        }
    }

    public void startSVGA(String svgaUrl){
        runOnUiThread(()->{
            Log.d(TAG, "showSVGA: 播放动画"+svgaUrl);
            uniqueInstance.gift_player.setCallback(new SVGACallback() {
                @Override
                public void onPause() {

                }

                @Override
                public void onFinished() {
                    Log.d(TAG, "onFinished: 动画结束");
                    if (svgaList.size()==0){
                        isFinished = true;
                        bottomSheetDialog.dismiss();
                    }else {
                        startSVGA(svgaList.get(0));
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

            uniqueInstance.slideOffset = 0;
            uniqueInstance.bottomSheetDialog.show();
            try {
                uniqueInstance.parser.setFrameSize(500, 500);
                uniqueInstance.parser.decodeFromURL(new URL(svgaUrl), new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                        Log.d(TAG, "onComplete: 动画解析成功");
                        uniqueInstance.gift_player.setVideoItem(svgaVideoEntity);
                        uniqueInstance.gift_player.startAnimation();
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


    //收礼用户id
    Member receiverMember;
    @BindView(R2.id.select_receiver_layout)
    LinearLayout select_receiver_layout;
    @BindView(R2.id.remainder_money_layout)
    LinearLayout remainder_money_layout;
    @BindView(R2.id.remainder_money_tv)
    public
    TextView remainder_money_tv;
    @BindView(R2.id.recharge_btn)
    Button recharge_btn;
    @BindView(R2.id.receiver_img)
    CircleImageView receiver_img;
    @BindView(R2.id.receiver_name)
    TextView receiver_name;
    public void showSelectedBroadcaster(){
        if (Common.myUserInfoList==null){
            return;
        }

        memberList.clear();
        for (int i = 0;i<mChannelData.getSeatArray().length;i++) {
            if (mChannelData.getSeatArray()[i]!=null){
                if (mChannelData.isUserOnline(mChannelData.getSeatArray()[i].getUserId())) {
                    memberList.add(mChannelData.getMember(mChannelData.getSeatArray()[i].getUserId()));
                }
            }
        }
        if (isChatRoom){
            if (memberList.size()>0){
                select_receiver_layout.setVisibility(View.VISIBLE);
                select_receiver_layout.setOnClickListener(v -> {
                    showBroadcaster();
                });
                receiverMember = memberList.get(0);
                Glide.with(mContext).load(memberList.get(0).getPortrait()).into(receiver_img);
                receiver_name.setText(memberList.get(0).getName());
            }else {
                select_receiver_layout.setVisibility(View.GONE);
            }
        }else {
            select_receiver_layout.setVisibility(View.GONE);
        }
    }

    @BindView(R2.id.broadcaster_card)
    CardView broadcaster_card;
    @BindView(R2.id.broadcaster_recyclerview)
    RecyclerView broadcaster_recyclerview;
    BroadcasterAdapter broadcasterAdapter;

    private ChannelData mChannelData;

    List<Member> memberList = new ArrayList<>();
    public void showBroadcaster(){
        memberList.clear();
        for (int i = 0;i<mChannelData.getSeatArray().length;i++) {
            if (mChannelData.getSeatArray()[i]!=null){
                if (mChannelData.isUserOnline(mChannelData.getSeatArray()[i].getUserId())) {
                    Log.d(TAG, "showBroadcaster: 主播" + mChannelData.getSeatArray()[i].getUserId());
                    memberList.add(mChannelData.getMember(mChannelData.getSeatArray()[i].getUserId()));
                }
            }
        }
        if (broadcasterAdapter==null){
            broadcasterAdapter = new BroadcasterAdapter(memberList);
            broadcaster_recyclerview.setAdapter(broadcasterAdapter);
            broadcaster_card.setVisibility(View.VISIBLE);
            broadcaster_recyclerview.setLayoutManager(new GridLayoutManager(mContext,4));
        }else {
            broadcaster_card.setVisibility(View.VISIBLE);
            broadcasterAdapter.setMemberList(memberList);
            broadcasterAdapter.notifyDataSetChanged();
        }
    }


    @BindView(R2.id.pay_layout)
    ConstraintLayout pay_layout;
    @BindView(R2.id.remainder_money)
    TextView remainder_money;
    @BindView(R2.id.pay_item_1)
    LinearLayout pay_item_1;
    @BindView(R2.id.pay_item_2)
    LinearLayout pay_item_2;
    @BindView(R2.id.pay_item_3)
    LinearLayout pay_item_3;
    @BindView(R2.id.pay_item_4)
    LinearLayout pay_item_4;
    @BindView(R2.id.pay_item_5)
    LinearLayout pay_item_5;
    @BindView(R2.id.pay_item_6)
    LinearLayout pay_item_6;
    @BindView(R2.id.pay_confirm_btn)
    Button pay_confirm_btn;

    public static int selected_pay = 1;

    @BindView(R2.id.pay_method_layout)
    ConstraintLayout pay_method_layout;
    @BindView(R2.id.pay_radio)
    RadioGroup pay_radio;
    @BindView(R2.id.alipay_radio)
    RadioButton alipay_radio;
    @BindView(R2.id.wechatpay_radio)
    RadioButton wechatpay_radio;
    @BindView(R2.id.submit_order_btn)
    Button submit_order_btn;
    int pay_method = 0;//0是微信支付，1是支付宝支付

    public void initPay(){
        switchStatus("选充值金额");
        selected_pay = 1;
        paySelectedBG(selected_pay);
        //金额选择
        pay_item_1.setOnClickListener(v -> {
            Log.d(TAG, "initPay: 点击了");
            selected_pay = 1;
            paySelectedBG(selected_pay);
        });
        pay_item_2.setOnClickListener(v -> {
            selected_pay = 6;
            paySelectedBG(selected_pay);
        });
        pay_item_3.setOnClickListener(v -> {
            selected_pay = 30;
            paySelectedBG(selected_pay);
        });
        pay_item_4.setOnClickListener(v -> {
            selected_pay = 98;
            paySelectedBG(selected_pay);
        });
        pay_item_5.setOnClickListener(v -> {
            selected_pay = 298;
            paySelectedBG(selected_pay);
        });
        pay_item_6.setOnClickListener(v -> {
            selected_pay = 518;
            paySelectedBG(selected_pay);
        });
        pay_confirm_btn.setOnClickListener(v -> {
            switchStatus("选支付方式");
        });

        //支付方式
        pay_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "onCheckedChanged: 改变支付方式"+checkedId+"|"+alipay_radio.getId());
                if (alipay_radio.getId() == checkedId){
                    pay_method = 1;
                }else {
                    pay_method = 0;
                }
            }
        });
        submit_order_btn.setOnClickListener(v -> {
            if (pay_method==0){
                Log.d(TAG, "initPay: 微信支付");
                OkHttpInstance.buyCoinWechat(selected_pay+"", responseString -> {
                    JSONObject jsonObject = null;//原生的
                    try {
                        jsonObject = new JSONObject(responseString);
                        //返回了统一下单的参数和二次签名
                        PayReq request = new PayReq();
                        request.appId = jsonObject.getString("appid");
                        request.partnerId = jsonObject.getString("mch_id");
                        request.prepayId= jsonObject.getString("prepay_id");
                        request.packageValue = "Sign=WXPay";
                        request.nonceStr= jsonObject.getString("nonce_str");
                        request.timeStamp= jsonObject.getString("timeStamp");
                        request.sign= jsonObject.getString("sign");

                        api = WXAPIFactory.createWXAPI(mContext, APP_ID, true);
                        api.registerApp(APP_ID); // 将应用的appId注册到微信
                        api.sendReq(request);//用api发起支付调用
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }else {
                Log.d(TAG, "initPay: 支付宝支付");
                OkHttpInstance.buyCoinAlipay(selected_pay+"",responseString -> {
                    orderInfo = responseString;
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                });
            }

            switchStatus("选礼物");
        });
    }

    //支付宝接口调用
    Runnable payRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: 进入支付");
            //EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX); //关闭沙箱测试
            PayTask alipay = new PayTask(mContext);
            Map<String,String> result = alipay.payV2(orderInfo,true);
            Log.d(TAG, "run: 支付宝"+result.get("resultStatus"));
            if (result.get("resultStatus").equals("9000")) {
                runOnUiThread(()->Toast.makeText(mContext, "支付成功", Toast.LENGTH_LONG).show());
            }
        }
    };

    public void paySelectedBG(int x){
        pay_item_1.setBackgroundResource(R.drawable.gift_broadcaster_unselected_bg);
        pay_item_2.setBackgroundResource(R.drawable.gift_broadcaster_unselected_bg);
        pay_item_3.setBackgroundResource(R.drawable.gift_broadcaster_unselected_bg);
        pay_item_4.setBackgroundResource(R.drawable.gift_broadcaster_unselected_bg);
        pay_item_5.setBackgroundResource(R.drawable.gift_broadcaster_unselected_bg);
        pay_item_6.setBackgroundResource(R.drawable.gift_broadcaster_unselected_bg);

        switch (x){
            case 1:
                pay_item_1.setBackgroundResource(R.drawable.gift_broadcaster_selected_bg);
                break;
            case 6:
                pay_item_2.setBackgroundResource(R.drawable.gift_broadcaster_selected_bg);
                break;
            case 30:
                pay_item_3.setBackgroundResource(R.drawable.gift_broadcaster_selected_bg);
                break;
            case 98:
                pay_item_4.setBackgroundResource(R.drawable.gift_broadcaster_selected_bg);
                break;
            case 298:
                pay_item_5.setBackgroundResource(R.drawable.gift_broadcaster_selected_bg);
                break;
            case 518:
                pay_item_6.setBackgroundResource(R.drawable.gift_broadcaster_selected_bg);
                break;
        }
    }


    public void switchStatus(String status){
        switch (status){
            case "选礼物":
                select_receiver_layout.setVisibility(View.VISIBLE);
                gift_recyclerview.setVisibility(View.VISIBLE);
                remainder_money_layout.setVisibility(View.VISIBLE);
                pay_layout.setVisibility(View.GONE);
                pay_method_layout.setVisibility(View.GONE);
                break;
            case "选充值金额":
                select_receiver_layout.setVisibility(View.GONE);
                gift_recyclerview.setVisibility(View.GONE);
                remainder_money_layout.setVisibility(View.GONE);
                pay_layout.setVisibility(View.VISIBLE);
                pay_method_layout.setVisibility(View.GONE);
                break;
            case "选支付方式":
                select_receiver_layout.setVisibility(View.GONE);
                gift_recyclerview.setVisibility(View.GONE);
                remainder_money_layout.setVisibility(View.GONE);
                pay_layout.setVisibility(View.GONE);
                pay_method_layout.setVisibility(View.VISIBLE);
                break;
        }
    }


    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    private static final String APP_ID = "wxef862b4ad2079599";
    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;

    private String orderInfo;

    public static GiftFrameLayout gift_layout;

    public class GiftAdapter extends RecyclerView.Adapter<ViewHolder>{
        List<GiftList> giftLists = new ArrayList<>();

        public GiftAdapter(List<GiftList> giftLists) {
            this.giftLists = giftLists;
        }

        @Override
        public int getItemViewType(int position) {
            if (giftLists.get(position).isSelected){
                return 0;
            }else{
                return 1;
            }
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift_selected,parent,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift_unselected,parent,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }
        }

        public void setGiftLists(List<GiftList> giftLists){
            this.giftLists = giftLists;
            notifyDataSetChanged();
        }
        public void swapData(List<GiftList> userInfoLists){
            int oldSize = this.giftLists.size();
            int newSize = userInfoLists.size();
            this.giftLists = userInfoLists;
            notifyItemRangeInserted(oldSize , newSize);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: 刷新");

            int currentPosition = position;
            GiftList currentItem = this.giftLists.get(position);
            if (currentItem.getSelected()){
                holder.gift_name.setText(currentItem.getGift_price()+"乐园币");
                holder.gift_price.setText("赠送");
            }else {
                holder.gift_name.setText(currentItem.getGift_name());
                holder.gift_price.setText(currentItem.getGift_price()+"乐园币");
            }
            Glide.with(mContext).load(currentItem.getGift_image()).into(holder.gift_iv);


            holder.gift_layout.setOnClickListener(v -> {
                if (giftLists.get(currentPosition).getSelected()){
                    //支付
                    Log.d(TAG, "onSimpleItemClick: 支付礼物");

                    OkHttpInstance.sendGift(currentItem.getId(), receiverMember.getUserId(), responseString -> {
                        if (!responseString.equals("false")){
                            GiftMessage giftMessage = new GiftMessage();
                            giftMessage.setGift_image(currentItem.getGift_image());
                            giftMessage.setGift_svga(currentItem.getGift_svga());
                            giftMessage.setSenderName(Constant.sName);
                            giftMessage.setSenderPortrait(Constant.sPortrait);
                            giftMessage.setReceiverName(receiverMember.getName());


                            if (giftCallBack!=null)
                                giftCallBack.getGiftList(currentItem);

                            Common.myUserInfoList.setMoney(String.valueOf(new BigDecimal(Common.myUserInfoList.getMoney()).subtract(new BigDecimal(currentItem.getGift_price()))));
                            remainder_money_tv.setText("余额："+Common.myUserInfoList.getMoney());
                            remainder_money.setText("余额："+Common.myUserInfoList.getMoney());


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    GiftFrameLayout giftFrameLayout = new GiftFrameLayout(mContext);
                                    container.addView(giftFrameLayout);
                                    giftFrameLayout.showSVGA(currentItem.getGift_svga());
                                    GiftPopView giftPopView = new GiftPopView(mContext);
                                    container.addView(giftPopView);
                                    giftPopView.show(giftMessage);
                                }
                            });

                            //GiftDialog.gift_layout.showSVGA(currentItem.getGift_svga());
                            //uniqueInstance.giftPopView.show(giftMessage);
                            //发消息
                            String giftMessageJson = JSON.toJSONString(giftMessage);
                            if (isChatRoom){
                                ChatRoomManager.instance(mContext).mRtmManager.sendMessage(giftMessageJson, new ResultCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: 发送频道礼物成功");
                                        ChatRoomManager.instance(mContext).sendMessage("赠送礼物 "+currentItem.gift_name+" 给 "+receiverMember.getName());
                                    }

                                    @Override
                                    public void onFailure(ErrorInfo errorInfo) {
                                        Log.d(TAG, "onSuccess: 发送频道礼物失败");
                                    }
                                });
                            }else {
                                RtmMessage message = RtmManager.instance(mContext).getRtmClient().createMessage();
                                message.setText(giftMessageJson);
                                ChatRoomManager.instance(mContext).mRtmManager.sendMessageToPeer(receiverMember.getUserId(), giftMessageJson, new ResultCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }

                                    @Override
                                    public void onFailure(ErrorInfo errorInfo) {

                                    }
                                });
                            }
                        }else {
                            Toast.makeText(mContext,"余额不足",Toast.LENGTH_SHORT).show();
                            initPay();
                        }
                    });
                }else {
                    Log.d(TAG, "onSimpleItemClick: 选择礼物");
                    for (GiftList giftList:giftLists) {
                        giftList.setSelected(false);
                    }
                    giftLists.get(currentPosition).setSelected(true);
                    setGiftLists(giftLists);
                }
            });
        }

        @Override
        public int getItemCount() {
            return giftLists.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout gift_layout;
        ImageView gift_iv;
        TextView gift_name;
        TextView gift_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gift_layout = itemView.findViewById(R.id.gift_layout);
            gift_iv = itemView.findViewById(R.id.gift_iv);
            gift_name = itemView.findViewById(R.id.gift_name);
            gift_price = itemView.findViewById(R.id.gift_price);
        }
    }




    public class BroadcasterAdapter extends RecyclerView.Adapter<BroadcasterViewHolder>{
        List<Member> memberList;
        public BroadcasterAdapter(List<Member> memberList) {
            this.memberList = memberList;
        }


        public void setMemberList(List<Member> memberList){
            this.memberList = memberList;
            notifyDataSetChanged();
        }
        @NonNull
        @Override
        public BroadcasterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatroom_broadcaster,parent,false);
            BroadcasterViewHolder viewHolder = new BroadcasterViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull BroadcasterViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: 刷新");
            Member currentItem = memberList.get(position);


            Glide.with(mContext).load(currentItem.getPortrait()).into(holder.iv_avatar);
            holder.name.setText(currentItem.getName());

            if (receiverMember.getUserId().equals(currentItem.getUserId())){
                holder.broadcaster_layout.setBackgroundResource(R.drawable.gift_broadcaster_selected_bg);
            }else {
                holder.broadcaster_layout.setBackgroundResource(R.drawable.gift_broadcaster_unselected_bg);
            }
            holder.broadcaster_layout.setOnClickListener(v -> {
                Log.d(TAG, "onBindViewHolder: 选择主播");
                broadcaster_card.setVisibility(View.GONE);

                receiverMember = currentItem;
                Glide.with(mContext).load(currentItem.getPortrait()).into(receiver_img);
                receiver_name.setText(currentItem.getName());
            });
        }

        @Override
        public int getItemCount() {
            return memberList.size();
        }
    }

    public class BroadcasterViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout broadcaster_layout;
        CircleImageView iv_avatar;
        TextView name;

        public BroadcasterViewHolder(@NonNull View itemView) {
            super(itemView);

            broadcaster_layout = itemView.findViewById(R.id.broadcaster_layout);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            name = itemView.findViewById(R.id.name);
        }
    }


    @BindView(R2.id.giftPopView)
    GiftPopView giftPopView;


    public static RtmClientListener mClientListener = new RtmClientListener() {
        @Override
        public void onConnectionStateChanged(int i, int i1) {

        }

        @Override
        public void onMessageReceived(RtmMessage rtmMessage, String s) {
            Log.d(TAG, "onMessageReceived: 收到个人礼物消息"+rtmMessage.getText());
            if (rtmMessage.getText().contains("giftMessage")){
                GiftMessage giftMessage = JSON.parseObject(rtmMessage.getText(),GiftMessage.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GiftFrameLayout giftFrameLayout = new GiftFrameLayout(mContext);
                        container.addView(giftFrameLayout);
                        giftFrameLayout.showSVGA(giftMessage.getGift_svga());

                        GiftPopView giftPopView = new GiftPopView(mContext);
                        container.addView(giftPopView);
                        giftPopView.show(giftMessage);

                    }
                });

                //GiftDialog.gift_layout.showSVGA(giftMessage.getGift_svga());
                //uniqueInstance.giftPopView.show(giftMessage);
            }
        }

        @Override
        public void onImageMessageReceivedFromPeer(RtmImageMessage rtmImageMessage, String s) {

        }

        @Override
        public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {

        }

        @Override
        public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

        }

        @Override
        public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

        }

        @Override
        public void onTokenExpired() {

        }

        @Override
        public void onPeersOnlineStatusChanged(Map<String, Integer> map) {

        }
    };

    public static RtmChannelListener mChannelListener = new RtmChannelListener() {
        @Override
        public void onMemberCountUpdated(int i) {

        }

        @Override
        public void onAttributesUpdated(List<RtmChannelAttribute> list) {

        }

        @Override
        public void onMessageReceived(RtmMessage rtmMessage, RtmChannelMember rtmChannelMember) {
            Log.d(TAG, "onMessageReceived: 收到频道礼物消息"+rtmMessage.getText());
            if (rtmMessage.getText().contains("giftMessage")){
                GiftMessage giftMessage = JSON.parseObject(rtmMessage.getText(),GiftMessage.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GiftFrameLayout giftFrameLayout = new GiftFrameLayout(mContext);
                        container.addView(giftFrameLayout);
                        giftFrameLayout.showSVGA(giftMessage.getGift_svga());

                        GiftPopView giftPopView = new GiftPopView(mContext);
                        container.addView(giftPopView);
                        giftPopView.show(giftMessage);

                    }
                });

                //GiftDialog.gift_layout.showSVGA(giftMessage.getGift_svga());
                //uniqueInstance.giftPopView.show(giftMessage);
            }
        }

        @Override
        public void onImageMessageReceived(RtmImageMessage rtmImageMessage, RtmChannelMember rtmChannelMember) {

        }

        @Override
        public void onFileMessageReceived(RtmFileMessage rtmFileMessage, RtmChannelMember rtmChannelMember) {

        }

        @Override
        public void onMemberJoined(RtmChannelMember rtmChannelMember) {

        }

        @Override
        public void onMemberLeft(RtmChannelMember rtmChannelMember) {

        }
    };
}
