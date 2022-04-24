package xin.banghua.beiyuan.Personage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.orhanobut.dialogplus.DialogPlus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.agora.chatroom.activity.ChatRoomActivity;
import io.agora.chatroom.gift.GiftCallBack;
import io.agora.chatroom.gift.GiftDialog;
import io.agora.chatroom.gift.GiftList;
import io.agora.chatroom.gift.GiftSentAdapter;
import io.agora.chatroom.model.Member;
import io.agora.chatroom.util.PortraitFrameView;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Adapter.FollowList;
import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.App;
import xin.banghua.beiyuan.CircleImageViewExtension;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.Main5Branch.BuysvipActivity;
import xin.banghua.beiyuan.Main5Branch.BuyvipActivity;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.utils.OkHttpInstance;

public class NewPersonageActivity extends AppCompatActivity {

    private static final String TAG = "PersonageActivity";

    UserInfoList userInfoList;

    public String mUserID;
    private View mView;

    private String mUserNickName;
    private String mUserPortrait;


    private TextView mUserNickName_tv;
    private CircleImageViewExtension mUserPortrait_iv;
    private TextView mUserAge_tv;
    private TextView mUserRegion_tv;
    private TextView mUserGender_tv;
    private TextView mUserProperty_tv;
    private TextView mUserSignature_tv;
    private EditText mLeaveWords_et;

    private Button make_friend;
    private Button make_friend_gift;
    private Button make_follow;
    private Button move_friendapply;
    private LinearLayout user_tiezi;

    private LinearLayout balcklist_btn;
    private TextView add_blacklist_tv;
    private Button deletefriend_btn;
    private Button startconversation_btn;
    private Button svip_chat_btn;
    private LinearLayout join_room_btn;

    private Activity mContext;

    ImageView vip_gray;
    ImageView vip_diamond;
    ImageView vip_black;
    ImageView vip_white;
    Integer current_timestamp = Math.round(new Date().getTime()/1000);

    TextView online_tv;
    ImageView online_img;

    TextView follow_tv,fans_tv;

    TextView id_tv;

    TextView chat_description;

    ConstraintLayout container;
    RecyclerView sent_gift_recyclerview;


    Button make_friend_btn,make_friend_cancel;
    LinearLayout all_btn_layout,make_friend_layout;
    List<GiftList> giftLists = new ArrayList<>();
    GiftSentAdapter giftSentAdapter;

    PortraitFrameView portraitFrameView;


    ImageView lv_img;

    ImageView rp_verify_img;


    String from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleperson);

        mContext = this;

        from = getIntent().getStringExtra("from");


        chat_description = findViewById(R.id.chat_description);
        if (!TextUtils.isEmpty(from)){
            if (from.equals("match")){
                chat_description.setText("*可以直接与此用户聊天");
            }
        }

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("个人信息");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.d(TAG, "onCreate: "+getIntent().getIntExtra("userid",1));


        onViewCreated();

        giftSentAdapter = new GiftSentAdapter(mContext,giftLists);
        sent_gift_recyclerview.setLayoutManager(new GridLayoutManager(mContext,4));
        sent_gift_recyclerview.setAdapter(giftSentAdapter);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }






    public void onViewCreated() {

        rp_verify_img = findViewById(R.id.rp_verify_img);


        portraitFrameView = findViewById(R.id.portraitFrameView);
        container = findViewById(R.id.container);
        sent_gift_recyclerview = findViewById(R.id.sent_gift_recyclerview);

        make_friend_btn = findViewById(R.id.make_friend_btn);
        make_friend_cancel = findViewById(R.id.make_friend_cancel);
        all_btn_layout = findViewById(R.id.all_btn_layout);
        make_friend_layout = findViewById(R.id.make_friend_layout);
        make_friend_btn.setOnClickListener(v -> {
            all_btn_layout.setVisibility(View.GONE);
            make_friend_layout.setVisibility(View.VISIBLE);
        });
        make_friend_cancel.setOnClickListener(v -> {
            all_btn_layout.setVisibility(View.VISIBLE);
            make_friend_layout.setVisibility(View.GONE);
        });

        Log.d(TAG, "onViewCreated: 进入person");
        follow_tv = findViewById(R.id.follow_tv);
        fans_tv = findViewById(R.id.fans_tv);
        id_tv = findViewById(R.id.id_tv);
        //取出选中的用户id
        //SharedHelper shvalue = new SharedHelper(App.getApplication().getApplicationContext());
        //mUserID = shvalue.readValue().get("value");
        String result = "1";
        if (this!=null){
            result = this.getIntent().getStringExtra("userID");
        }
        mUserID = result;

        vip_gray = findViewById(R.id.vip_gray);
        vip_diamond = findViewById(R.id.vip_diamond);
        vip_black = findViewById(R.id.vip_black);
        vip_white = findViewById(R.id.vip_white);

        online_tv =  findViewById(R.id.online_tv);

        online_img =  findViewById(R.id.online_img);

        mUserNickName_tv=findViewById(R.id.user_nickname);
        mUserPortrait_iv=findViewById(R.id.user_portrait);
        mUserAge_tv=findViewById(R.id.user_age);
        mUserRegion_tv=findViewById(R.id.user_region);
        //mUserGender_tv=findViewById(R.id.user_gender);
        mUserProperty_tv=findViewById(R.id.user_property);
        mUserSignature_tv=findViewById(R.id.user_signature);
        mLeaveWords_et=findViewById(R.id.leave_words);
        user_tiezi=findViewById(R.id.user_tiezi);
        user_tiezi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(App.getApplication(), Someonesluntan1Activity.class);
                intent.putExtra("authid",mUserID);
                startActivity(intent);
            }
        });
        startconversation_btn = findViewById(R.id.start_conversation);
        startconversation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动会话界面
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat(mContext, mUserID, mUserNickName_tv.getText().toString());
                }
            }
        });


        make_follow = findViewById(R.id.make_follow);
        for (int i = 0; i < Common.followList.size(); i++){
            if (Common.followList.get(i).getUserId().equals(mUserID)){
                make_follow.setText("取消关注");
                make_follow.setBackgroundResource(R.drawable.tjhy_10);
                make_follow.setTextColor(getResources().getColor(R.color.text_color_8_deep));
            }
        }

        make_follow.setOnClickListener(v -> {
            Log.d(TAG, "关注onViewCreated: "+make_follow.getText().toString());
            if (make_follow.getText().toString().equals("关注（互关自动成为好友）")){
                make_follow.setText("取消关注");
                make_follow.setBackgroundResource(R.drawable.tjhy_10);
                make_follow.setTextColor(getResources().getColor(R.color.text_color_8_deep));
                OkHttpInstance.follow(mUserID, responseString -> {
                    Common.followList.add(new FollowList(mUserID));
                });
            }else {
                make_follow.setText("关注（互关自动成为好友）");
                make_follow.setBackgroundResource(R.drawable.red_10);
                make_follow.setTextColor(getResources().getColor(R.color.text_color_4));
                OkHttpInstance.unfollow(mUserID,responseString -> {

                });
            }
        });
        make_friend_gift = findViewById(R.id.make_friend_gift);

        make_friend = findViewById(R.id.make_friend);
        make_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                TextView prompt = findViewById(R.id.prompt_tv);
                prompt.setText("确定要申请好友吗？");
                Button dismissdialog_btn = findViewById(R.id.cancel_btn);
                dismissdialog_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button confirm_btn = findViewById(R.id.confirm_btn);
                confirm_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        move_friendapply.setVisibility(View.VISIBLE);
                        make_friend.setEnabled(false);
                        make_friend.setText("申请好友中，等待对方同意");
                        getFriendNumber("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=friendsnumber&m=socialchat");
                        dialog.dismiss();
                    }
                });
            }
        });
        move_friendapply = findViewById(R.id.move_friendapply);
        move_friendapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (Common.vipTime == null && !Common.ifBuySVip) {
//                    Common.ifBuySVip = false;
//                    getVipinfo("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=viptimeinsousuo&m=socialchat",1);
//                }else {
//                    moveFriendApply();
//                }

                Common.ifBuySVip = false;
                getVipinfo("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=viptimeinsousuo&m=socialchat",1);
            }
        });

        balcklist_btn = findViewById(R.id.add_blacklist);
        add_blacklist_tv = findViewById(R.id.add_blacklist_tv);
        balcklist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add_blacklist_tv.getText().toString().equals("加入黑名单")) {
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
                    TextView prompt = findViewById(R.id.prompt_tv);
                    prompt.setText("确定要加入黑名单吗？");
                    Button dismissdialog_btn = findViewById(R.id.cancel_btn);
                    dismissdialog_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Button confirm_btn = findViewById(R.id.confirm_btn);
                    confirm_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //拉黑了好友，则刷新
                            try {
                                xin.banghua.beiyuan.Common.newFriendOrDeleteFriend = true;
                                xin.banghua.beiyuan.Common.friendListMap.remove(mUserID);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            add_blacklist_tv.setText("移除黑名单");
                            Toast.makeText(mContext,"已加入黑名单",Toast.LENGTH_LONG).show();


                            addBlacklist("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=addblacklist&m=socialchat");
                            dialog.dismiss();
                        }
                    });
                }else {
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
                    TextView prompt = findViewById(R.id.prompt_tv);
                    prompt.setText("确定要移除黑名单吗？");
                    Button dismissdialog_btn = findViewById(R.id.cancel_btn);
                    dismissdialog_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Button confirm_btn = findViewById(R.id.confirm_btn);
                    confirm_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            add_blacklist_tv.setText("加入黑名单");
                            Toast.makeText(mContext,"已移除黑名单",Toast.LENGTH_LONG).show();
                            deleteBlackList("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=deleteblacklist&m=socialchat");
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        deletefriend_btn = findViewById(R.id.delete_friend);
        deletefriend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                TextView prompt = findViewById(R.id.prompt_tv);
                prompt.setText("确定要删除好友吗？");
                Button dismissdialog_btn = findViewById(R.id.cancel_btn);
                dismissdialog_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button confirm_btn = findViewById(R.id.confirm_btn);
                confirm_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除了好友，则刷新
                        xin.banghua.beiyuan.Common.newFriendOrDeleteFriend = true;
                        xin.banghua.beiyuan.Common.friendListMap.remove(mUserID);
                        if (xin.banghua.beiyuan.Common.friendsRemarkMap!=null){
                            xin.banghua.beiyuan.Common.friendsRemarkMap.remove(mUserID);
                        }


                        RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE,mUserID,new io.rong.imlib.RongIMClient.ResultCallback(){

                            @Override
                            public void onSuccess(Object o) {

                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });

                        Toast.makeText(mContext,"已删除好友",Toast.LENGTH_LONG).show();
                        deletefriend_btn.setVisibility(View.INVISIBLE);
                        make_friend.setVisibility(View.VISIBLE);
                        startconversation_btn.setVisibility(View.GONE);

                        deleteFriend(getString(R.string.deletefriendnew_url));
                        dialog.dismiss();
                    }
                });

            }
        });

        svip_chat_btn = findViewById(R.id.svip_chat_btn);
        svip_chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (Common.vipTime == null && !Common.ifBuySVip) {
//                    Common.ifBuySVip = false;
//                    getVipinfo("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=viptimeinsousuo&m=socialchat",2);
//                }else {
//                    svipChat();
//                }

                if (TextUtils.isEmpty(from)){
                    Common.ifBuySVip = false;
                    getVipinfo("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=viptimeinsousuo&m=socialchat",2);
                }else {
                    if (RongIM.getInstance() != null) {
                        RongIM.getInstance().startPrivateChat(mContext, mUserID, mUserNickName_tv.getText().toString());
                    }
                }


            }
        });

        join_room_btn = findViewById(R.id.join_room_btn);
        join_room_btn.setOnClickListener(view1 -> {
            if (userInfoList!=null){
                Log.d(TAG, "onViewCreated: 进入房间");
                Intent intent = new Intent(mContext, ChatRoomActivity.class);
                intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_ID, mUserID);
                intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_TYPE, userInfoList.getAudioroomtype());
                intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, io.agora.chatroom.R.mipmap.bg_channel_0);
                //intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, Constant.sRoomBG);
                startActivity(intent);
            }
        });


        getDataPersonage(getString(R.string.personage_url));

        addSawMe("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=addsawme&m=socialchat");

        ifFriend(getString(R.string.iffriendnew_url));
    }
    //TODO okhttp获取用户信息
    public void getVipinfo(final String url,int moveOrChat){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(mContext);
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("id", myid)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    if (moveOrChat == 1) {
                        message.what=8;
                    }else {
                        message.what=10;
                    }
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //TODO okhttp删除黑名单
    public void deleteBlackList(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(mContext);
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("yourid", mUserID)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=7;
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //TODO okhttp删除好友
    public void deleteFriend(final String url){
        //Toast.makeText(mContext, "已加入黑名单", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(App.getApplication());
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("yourid", mUserID)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=6;
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void initPersonage(View view, JSONObject jsonObject) throws JSONException {
        make_friend_gift.setOnClickListener(v -> {
            make_friend_gift.setText("不要忘了点击开始申请哦！");
            try {
                GiftDialog.getInstance(NewPersonageActivity.this, false, container, new GiftCallBack() {
                    @Override
                    public void getGiftList(GiftList giftList) {
                        Boolean isExist = false;
                        for (int i = 0;i<giftLists.size();i++){
                            if (giftLists.get(i).getId().equals(giftList.getId())){
                                giftLists.get(i).setNum((giftLists.get(i).getNum()+1));
                                isExist = true;
                            }
                        }
                        if (!isExist){
                            giftLists.add(giftList);
                        }
                        giftSentAdapter.notifyDataSetChanged();
                    }
                }).initShow(new Member(jsonObject.getString("id"), jsonObject.getString("nickname"),
                        jsonObject.getString("portrait"), jsonObject.getString("gender"),
                        jsonObject.getString("property"),jsonObject.getString("portraitframe"),jsonObject.getString("veilcel")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        portraitFrameView.setPortraitFrame(Common.getOssResourceUrl(jsonObject.getString("portraitframe")));
        id_tv.setText("乐园id："+jsonObject.getString("id"));
        follow_tv.setText("关注："+jsonObject.getString("follow"));
        follow_tv.setOnClickListener(v -> {
            Intent intent = new Intent(this,FollowAndFansActivity.class);
            intent.putExtra("userId",mUserID);
            intent.putExtra("type",0);
            this.startActivity(intent);
        });
        fans_tv.setText("粉丝："+jsonObject.getString("fans"));
        fans_tv.setOnClickListener(v -> {
            Intent intent = new Intent(this,FollowAndFansActivity.class);
            intent.putExtra("userId",mUserID);
            intent.putExtra("type",1);
            this.startActivity(intent);
        });

        mUserNickName=jsonObject.getString("nickname");
        mUserPortrait=jsonObject.getString("portrait");

        online_tv.setText(userInfoList.getOnline());
        if (userInfoList.getOnline().equals("在线")){
            Glide.with(App.getApplication()).load(R.mipmap.green_point).into(online_img);
        }else {
            Glide.with(App.getApplication()).load(R.mipmap.gray_point).into(online_img);
        }


        mUserNickName_tv.setText(jsonObject.getString("nickname"));
        //if (jsonObject.getString("vip").equals("VIP"))  mUserPortrait_iv.isVIP(true,getResources(),false);
        Glide.with(App.getApplication())
                .asBitmap()
                .load(Common.getOssResourceUrl(jsonObject.getString("portrait")))
                .into(mUserPortrait_iv);

        vip_gray.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(jsonObject.getString("svip"))) {
            int svip_time = Integer.parseInt(jsonObject.getString("svip") + "");
            if (svip_time > current_timestamp) {
                //vipicon分级
                Log.d("会员时长", ((svip_time - current_timestamp) + ""));
                if ((svip_time - current_timestamp) < 3600 * 24 * 30) {
                    Glide.with(App.getApplication())
                            .asBitmap()
                            .load(R.drawable.ic_svip_diamond)
                            .into(vip_gray);
                } else if ((svip_time - current_timestamp) < 3600 * 24 * 180) {
                    Glide.with(App.getApplication())
                            .asBitmap()
                            .load(R.drawable.ic_svip_black)
                            .into(vip_gray);
                } else {
                    Glide.with(App.getApplication())
                            .asBitmap()
                            .load(R.drawable.ic_svip_white)
                            .into(vip_gray);
                }
            } else {
                if (!TextUtils.isEmpty(jsonObject.getString("vip"))) {
                    int vip_time = Integer.parseInt(jsonObject.getString("vip") + "");
                    if (vip_time > current_timestamp) {
                        //vipicon分级
                        Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                        if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                            Glide.with(App.getApplication())
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_diamond)
                                    .into(vip_gray);
                        } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                            Glide.with(App.getApplication())
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_black)
                                    .into(vip_gray);
                        } else {
                            Glide.with(App.getApplication())
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_white)
                                    .into(vip_gray);
                        }
                    } else {
                        Glide.with(App.getApplication())
                                .asBitmap()
                                .load(R.drawable.ic_vip_gray)
                                .into(vip_gray);
                    }
                }else {
                    Glide.with(App.getApplication())
                            .asBitmap()
                            .load(R.drawable.ic_vip_gray)
                            .into(vip_gray);
                }
            }
        }else if (jsonObject.getString("vip")!="null") {
            int vip_time = Integer.parseInt(jsonObject.getString("vip") + "");
            if (vip_time > current_timestamp) {
                //vipicon分级
                Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                    Glide.with(App.getApplication())
                            .asBitmap()
                            .load(R.drawable.ic_vip_diamond)
                            .into(vip_gray);
                } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                    Glide.with(App.getApplication())
                            .asBitmap()
                            .load(R.drawable.ic_vip_black)
                            .into(vip_gray);
                } else {
                    Glide.with(App.getApplication())
                            .asBitmap()
                            .load(R.drawable.ic_vip_white)
                            .into(vip_gray);
                }
            } else {
                Glide.with(App.getApplication())
                        .asBitmap()
                        .load(R.drawable.ic_vip_gray)
                        .into(vip_gray);
            }
        }else {
            Glide.with(App.getApplication())
                    .asBitmap()
                    .load(R.drawable.ic_vip_gray)
                    .into(vip_gray);
        }



        mUserAge_tv.setText(" "+jsonObject.getString("age"));
        mUserRegion_tv.setText(jsonObject.getString("region"));
        mUserProperty_tv.setText(jsonObject.getString("property"));
        //mUserGender_tv.setText(jsonObject.getString("gender"));
        if (jsonObject.getString("gender").equals("男")){
            Resources resources = mContext.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.male,null);
            //mUserGender_tv.setForeground(drawable);
        }else {
            Resources resources = mContext.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.female,null);
            //mUserGender_tv.setForeground(drawable);
        }
        if (jsonObject.getString("gender").equals("男")){
            Resources resources = mContext.getResources();
            Drawable drawable = resources.getDrawable(R.mipmap.bboy,null);
            //((UserinfoHolder) viewHolder).userGender.setForeground(drawable);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mUserAge_tv.setCompoundDrawables(drawable, null, null, null);
            mUserAge_tv.setBackgroundResource(R.drawable.yuan_sex_nan);
        }else {
            Resources resources = mContext.getResources();
            Drawable drawable = resources.getDrawable(R.mipmap.ggirl,null);
            //((UserinfoHolder) viewHolder).userGender.setForeground(drawable);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mUserAge_tv.setCompoundDrawables(drawable, null, null, null);
            mUserAge_tv.setBackgroundResource(R.drawable.yuan_sex_nv);
        }


        mUserSignature_tv.setText(jsonObject.getString("signature"));
        if (jsonObject.getString("allowfriend").equals("0")){
            make_friend.setEnabled(false);
            make_friend.setText("拒绝添加好友");
        }

        if (jsonObject.getString("allowsvip").equals("0")){
            svip_chat_btn.setEnabled(false);
            svip_chat_btn.setText("拒绝直接聊天");
        }
    }
    //网络数据部分
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是用户数据，2是幻灯片
            try {
                switch (msg.what){
                    case 1:
                        try {
                            String resultJson1 = msg.obj.toString();
                            Log.d(TAG, "handleMessage: 用户数据接收的值"+msg.obj.toString());

                            userInfoList = JSON.parseObject(resultJson1,UserInfoList.class);
                            //实名认证
                            if (userInfoList.getRp_verify_time().equals("0")){
                                rp_verify_img.setVisibility(View.GONE);
                            }else {
                                rp_verify_img.setVisibility(View.VISIBLE);
                            }
                            if (Common.isBlackList(userInfoList.getMyblacklist())){
                                make_friend_btn.setText("已被对方加入黑名单");
                                make_friend_btn.setClickable(false);
                                user_tiezi.setClickable(false);
                                join_room_btn.setClickable(false);
                                svip_chat_btn.setClickable(false);
                                make_follow.setClickable(false);
                            }
                            lv_img = findViewById(R.id.lv_img);
                            lv_img.setImageResource(Common.getLevelFromUser(userInfoList));
                            JSONObject jsonObject = new ParseJSONObject(msg.obj.toString()).getParseJSON();
                            try {
                                initPersonage(mView,jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        make_friend.setEnabled(false);
                        make_friend.setText(msg.obj.toString());
                        if (!msg.obj.toString().equals("对方已将您加入黑名单")){
                            move_friendapply.setVisibility(View.VISIBLE);
                            svip_chat_btn.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(mContext,msg.obj.toString(),Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(mContext, MainActivity.class);
//                    startActivity(intent);
                        break;
                    case 3:
                        add_blacklist_tv.setText("移除黑名单");
                        if (TextUtils.isEmpty(Common.userInfoList.getMyblacklist())){
                            Common.userInfoList.setMyblacklist(userInfoList.getId());
                        }else {
                            Common.userInfoList.setMyblacklist(Common.userInfoList.getMyblacklist()+","+userInfoList.getId());
                        }
                        Toast.makeText(mContext,"已加入黑名单",Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        Log.d(TAG, "handleMessage: 进入好友判断");
                        if (msg.obj.toString().equals("好友人数未超过限制")){
                            Log.d(TAG, "handleMessage: 跳转添加好友");
                            makeFriend("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=addfriend&m=socialchat");
                        }else {
                            Log.d(TAG, "handleMessage: 会员数量满");
                            make_friend.setText("今日免费5次已用完，请办理会员或明日再试");
                            //Toast.makeText(mContext,msg.obj.toString(),Toast.LENGTH_LONG).show();
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
                                    .setFooter(R.layout.dialog_foot_needvip)
                                    .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                                    .create();
                            dialog.show();
                            View view = dialog.getFooterView();
                            TextView buyvip_tv = findViewById(R.id.buyvip_tv);
                            buyvip_tv.setText("非会员每日可发起5次添加好友请求，请明日再试，也可开通会员，享受无限制添加好友和7大特权");
                            Button buvip = findViewById(R.id.buyvip_btn);
                            buvip.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, BuyvipActivity.class);
                                    startActivity(intent);
                                }
                            });
                            Button cancel = findViewById(R.id.goback_btn);
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        }
                        break;
                    case 5:
                        JSONObject jsonObject = null;//原生的
                        try {
                            jsonObject = new JSONObject(msg.obj.toString());
                            String blacklistinfo = jsonObject.getString("blacklistinfo");
                            String friendinfo = jsonObject.getString("friendinfo");
                            if (blacklistinfo.equals("已加入黑名单")){
                                add_blacklist_tv.setText("移除黑名单");
                                Toast.makeText(mContext,blacklistinfo,Toast.LENGTH_LONG).show();
                            }
                            if (friendinfo.equals("已经是好友")){
                                make_friend.setVisibility(View.GONE);
                                deletefriend_btn.setVisibility(View.VISIBLE);
                                startconversation_btn.setVisibility(View.VISIBLE);
                                Toast.makeText(mContext,friendinfo,Toast.LENGTH_LONG).show();
                            }else if (friendinfo.equals("已申请，等待对方同意")){
                                move_friendapply.setVisibility(View.VISIBLE);
                                make_friend.setEnabled(false);
                                make_friend.setText(friendinfo);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 6:
                        Toast.makeText(mContext,"已删除好友",Toast.LENGTH_LONG).show();
                        deletefriend_btn.setVisibility(View.INVISIBLE);
                        make_friend.setVisibility(View.VISIBLE);
                        startconversation_btn.setVisibility(View.GONE);
                        break;
                    case 7:
                        add_blacklist_tv.setText("加入黑名单");
                        Toast.makeText(mContext,"已移除黑名单",Toast.LENGTH_LONG).show();
                        break;
                    case 8:
                        xin.banghua.beiyuan.Common.vipTime = msg.obj.toString();
                        moveFriendApply();
                        break;
                    case 9:
                        Toast.makeText(mContext,"撤销成功",Toast.LENGTH_LONG).show();
                        make_friend.setText("申请好友");
                        make_friend.setEnabled(true);
                        move_friendapply.setVisibility(View.GONE);
                        break;
                    case 10:
                        xin.banghua.beiyuan.Common.vipTime = msg.obj.toString();
                        svipChat();
                        break;
                    case 11:
                        if (RongIM.getInstance() != null) {
                            RongIM.getInstance().startPrivateChat(mContext, mUserID, mUserNickName_tv.getText().toString());
                        }
//                    if (msg.obj.toString().equals("1")) {
//                        if (RongIM.getInstance() != null) {
//                            RongIM.getInstance().startPrivateChat(mContext, mUserID, mUserNickName_tv.getText().toString());
//                        }
//                    }else {
//                        Toast.makeText(mContext,"对方不允许SVIP直接发起聊天",Toast.LENGTH_LONG).show();
//                    }
                        break;
                }
            }catch (Exception e){
                Log.e(TAG, "onBindViewHolder: 异常"+ e.toString());
            }
        }
    };

    public void moveFriendApply(){
        if (xin.banghua.beiyuan.Common.vipTime.equals("会员已到期")){
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
                    .setFooter(R.layout.dialog_foot_needvip)
                    .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                    .create();
            dialog.show();
            View view = dialog.getFooterView();
            Button buvip = findViewById(R.id.buyvip_btn);
            buvip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, BuyvipActivity.class);
                    startActivity(intent);
                }
            });
            Button cancel = findViewById(R.id.goback_btn);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }else {
            deleteFriendNumber(mUserID,"https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=DeleteFriendsapply&m=socialchat");
        }
    }
    public void svipChat(){
        if (!xin.banghua.beiyuan.Common.vipTime.contains("svip") && SharedHelper.getInstance(App.getApplication()).readTryChat()==1){
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
                    .setFooter(R.layout.dialog_foot_needsvip)
                    .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                    .create();
            dialog.show();
            View view = dialog.getFooterView();
            Button buvip = findViewById(R.id.buyvip_btn);
            buvip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, BuysvipActivity.class);
                    startActivity(intent);
                }
            });
            Button cancel = findViewById(R.id.goback_btn);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }else {
            SharedHelper.getInstance(App.getApplication()).saveTryChat(1);
            if (!xin.banghua.beiyuan.Common.vipTime.contains("svip")){
                Toast.makeText(App.getApplication(),"本次为免费试用！",Toast.LENGTH_LONG).show();
                OkHttpInstance.exhaustSvipTry();
            }
            new Thread(new Runnable() {
                @Override
                public void run(){
                    SharedHelper shuserinfo = new SharedHelper(mContext.getApplicationContext());
                    String myid = shuserinfo.readUserInfo().get("userID");
                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .add("myid", myid)
                            .add("yourid", mUserID)
                            .build();
                    Request request = new Request.Builder()
                            .url(getString(R.string.setsvipchat_url))
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                        Message message=handler.obtainMessage();
                        message.what=11;
                        Log.d(TAG, "run: 查看返回值"+response.body().string());
                        handler.sendMessageDelayed(message,10);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    //TODO okhttp获取好友人数
    public void deleteFriendNumber(final String yourid,final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(mContext.getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");
                Log.d(TAG, "删除新好友myid"+myid+"yourid"+yourid);
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("yourid", yourid)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.what=9;
                    Log.d(TAG, "run: 查看返回值"+response.body().string());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //TODO okhttp获取用户信息
    public void getDataPersonage(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("userid", mUserID)
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
                    Log.d(TAG, "run: getDataPersonage"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //TODO 添加好友
    String gift_string = "";
    public void makeFriend(final String url){
        //Toast.makeText(mContext, "申请成功", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(App.getApplication().getApplicationContext());
                String yourid = shuserinfo.readUserInfo().get("userID");
                String yournickname = shuserinfo.readUserInfo().get("userNickName");
                String yourportrait = shuserinfo.readUserInfo().get("userPortrait");
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", mUserID)
                        .add("yourid", yourid)
                        .add("yournickname", yournickname)
                        .add("yourportrait", yourportrait)
                        .add("yourwords",mLeaveWords_et.getText().toString())
                        .add("gift_string",JSON.toJSONString(giftLists))
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=2;
                    Log.d(TAG, "run: getDataPersonage"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //TODO 判断是否已是好友好友
    public void ifFriend(final String url){
        //Toast.makeText(mContext, "申请成功", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(App.getApplication().getApplicationContext());
                String yourid = shuserinfo.readUserInfo().get("userID");
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", mUserID)
                        .add("yourid", yourid)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=5;
                    Log.d(TAG, "run: getDataPersonage"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //TODO okhttp谁看过我
    public void addSawMe(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(App.getApplication().getApplicationContext());
                String yourid = shuserinfo.readUserInfo().get("userID");
                String yournickname = shuserinfo.readUserInfo().get("userNickName");
                String yourportrait = shuserinfo.readUserInfo().get("userPortrait");
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", mUserID)
                        .add("yourid", yourid)
                        .add("yournickname", yournickname)
                        .add("yourportrait", yourportrait)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    Log.d(TAG, "run: 谁看过我"+request.body().toString());

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //TODO okhttp加入黑名单
    public void addBlacklist(final String url){
        //Toast.makeText(mContext, "已加入黑名单", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(App.getApplication().getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("yourid", mUserID)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=3;
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //TODO okhttp获取好友人数
    public void getFriendNumber(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(App.getApplication().getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=4;
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}