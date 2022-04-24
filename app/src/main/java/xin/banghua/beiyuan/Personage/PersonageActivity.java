package xin.banghua.beiyuan.Personage;

import static xin.banghua.onekeylogin.Constant.THEME_KEY;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jaeger.library.StatusBarUtil;
import com.orhanobut.dialogplus.DialogPlus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.chatroom.activity.ChatRoomActivity;
import io.agora.chatroom.gift.GiftCallBack;
import io.agora.chatroom.gift.GiftDialog;
import io.agora.chatroom.gift.GiftList;
import io.agora.chatroom.gift.GiftSentAdapter;
import io.agora.chatroom.model.Member;
import io.agora.chatroom.util.PortraitFrameView;
import io.agora.chatroom.util.ReportDialog;
import io.agora.chatroom.util.WealthAndGlamour;
import io.rong.imkit.RongIM;
import xin.banghua.beiyuan.Adapter.FollowList;
import xin.banghua.beiyuan.Adapter.InformBlacklistAdapter;
import xin.banghua.beiyuan.Adapter.LuntanList;
import xin.banghua.beiyuan.Adapter.PictureAdapter;
import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.App;
import xin.banghua.beiyuan.CircleImageViewExtension;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.Main4Branch.VideoPlayActivity;
import xin.banghua.beiyuan.Main5Branch.BuysvipActivity;
import xin.banghua.beiyuan.Main5Branch.BuyvipActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.comment.CommentDialog;
import xin.banghua.beiyuan.custom_ui.CustomVideoView;
import xin.banghua.beiyuan.custom_ui.NiceImageView;
import xin.banghua.beiyuan.custom_ui.NiceZoomImageView;
import xin.banghua.beiyuan.topic.TopicAddedItem;
import xin.banghua.beiyuan.topic.TopicList;
import xin.banghua.beiyuan.utils.CusPullLoadMoreRecyclerView;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;
import xin.banghua.onekeylogin.login.OneKeyLoginActivity;

public class PersonageActivity extends AppCompatActivity {
    private static final String TAG = "PersonageActivity";

    public String mUserID;

    AppCompatActivity mContext;

    @BindView(R.id.container)
    ConstraintLayout container;

    @BindView(R.id.recycler_view)
    CusPullLoadMoreRecyclerView recyclerView;
    @BindView(R.id.direct_chat)
    ImageView direct_chat;
    @BindView(R.id.make_friend)
    ImageView make_friend;
    @BindView(R.id.more_layout)
    FrameLayout more_layout;
    @BindView(R.id.delete_friend_iv)
    ImageView delete_friend_iv;
    @BindView(R.id.report_iv)
    ImageView report_iv;
    @BindView(R.id.add_blacklist_iv)
    ImageView add_blacklist_iv;
    @BindView(R.id.copy_id)
    ImageView copy_id;

    UserInfoList userInfoList;

    List<LuntanList> luntanLists = new ArrayList<>();
    ExampleFragmentAdapter exampleFragmentAdapter;

    private Integer pageindex = 1;


    @BindView(R.id.make_friend_layout)
    LinearLayout make_friend_layout;
    @BindView(R.id.make_friend_gift)
    Button make_friend_gift;
    @BindView(R.id.make_friend_btn)
    Button make_friend_btn;
    @BindView(R.id.make_friend_cancel)
    Button make_friend_cancel;
    @BindView(R.id.leave_words)
    EditText mLeaveWords_et;

    List<GiftList> giftLists = new ArrayList<>();
    GiftSentAdapter giftSentAdapter;
    @BindView(R.id.sent_gift_recyclerview)
    RecyclerView sent_gift_recyclerview;

    String from = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.new_person_page);

        ButterKnife.bind(this);

        StatusBarUtil.setTranslucentForImageView(this, 0, findViewById(R.id.test_img));
        mContext = this;


        mUserID = getIntent().getStringExtra("userID");

        from  = getIntent().getStringExtra("from");

        giftSentAdapter = new GiftSentAdapter(mContext,giftLists);
        sent_gift_recyclerview.setLayoutManager(new GridLayoutManager(mContext,4));
        sent_gift_recyclerview.setAdapter(giftSentAdapter);


        OkHttpInstance.iffriendnew(mUserID, new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                JSONObject jsonObject = null;//原生的
                try {
                    jsonObject = new JSONObject(responseString);
                    String blacklistinfo = jsonObject.getString("blacklistinfo");
                    String friendinfo = jsonObject.getString("friendinfo");
                    if (blacklistinfo.equals("已加入黑名单")){
                        add_blacklist_iv.setVisibility(View.GONE);
                        Toast.makeText(mContext,blacklistinfo,Toast.LENGTH_LONG).show();
                    }
                    if (friendinfo.equals("已经是好友")){
                        make_friend.setEnabled(false);
                        make_friend.setImageResource(R.mipmap.is_friend);
                        Toast.makeText(mContext,friendinfo,Toast.LENGTH_LONG).show();
                    }else if (friendinfo.equals("已申请，等待对方同意")){
                        make_friend.setEnabled(true);
                        make_friend.setImageResource(R.mipmap.repeal_friend);
                        make_friend.setOnClickListener(v -> {
                            if (!Common.isVip(Common.userInfoList) && !Common.isSVip(Common.userInfoList)){
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
                                Button buvip = view.findViewById(R.id.buyvip_btn);
                                buvip.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mContext, BuyvipActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                Button cancel = view.findViewById(R.id.goback_btn);
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                            }else {
                                OkHttpInstance.deleteFriendNumber(userInfoList.getId(),Common.userInfoList.getId(),response -> {
                                    make_friend.setImageResource(R.mipmap.make_friend);
                                    make_friend.setOnClickListener(view -> {
                                        make_friend_layout.setVisibility(View.VISIBLE);
                                    });
                                });
                            }
                        });
                        Toast.makeText(mContext,friendinfo,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        OkHttpInstance.getUserAttributes(mUserID, new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                userInfoList = JSON.parseObject(responseString,UserInfoList.class);

                if (userInfoList.getAllowsvip().equals("0")){
                    direct_chat.setEnabled(false);
                    direct_chat.setImageResource(R.mipmap.refuse_chat);
                }else {
                    direct_chat.setOnClickListener(v -> {
                        if (Common.isSVip(Common.userInfoList) || "match".equals(from)){
                            if (RongIM.getInstance() != null) {
                                RongIM.getInstance().startPrivateChat(mContext, mUserID, userInfoList.getNickname());
                            }
                        }else{
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
                            Button buvip = view.findViewById(R.id.buyvip_btn);
                            buvip.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, BuysvipActivity.class);
                                    startActivity(intent);
                                }
                            });
                            Button cancel = view.findViewById(R.id.goback_btn);
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                }
                if (userInfoList.getAllowfriend().equals("0")){
                    make_friend.setEnabled(false);
                    make_friend.setImageResource(R.mipmap.refuse_friend);
                }else {
                    make_friend.setOnClickListener(v -> {
                        make_friend_layout.setVisibility(View.VISIBLE);
                    });

                    make_friend_cancel.setOnClickListener(v -> {
                        make_friend_layout.setVisibility(View.GONE);
                    });
                    make_friend_btn.setOnClickListener(v -> {
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
                        prompt.setText("确定要申请好友吗？");
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
                                make_friend.setEnabled(false);
                                make_friend.setImageResource(R.mipmap.maked_friend);
                                make_friend_layout.setVisibility(View.GONE);
                                OkHttpInstance.getFriendNumber(new OkHttpResponseCallBack() {
                                    @Override
                                    public void getResponseString(String responseString) {
                                        if (responseString.equals("好友人数未超过限制")){
                                            OkHttpInstance.addfriend(mUserID, mLeaveWords_et.getText().toString(), JSON.toJSONString(giftLists), new OkHttpResponseCallBack() {
                                                @Override
                                                public void getResponseString(String responseString) {
                                                    make_friend.setEnabled(true);
                                                    make_friend.setImageResource(R.mipmap.repeal_friend);
                                                    make_friend.setOnClickListener(v -> {
                                                        if (!Common.isVip(Common.userInfoList) && !Common.isSVip(Common.userInfoList)){
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
                                                            Button buvip = view.findViewById(R.id.buyvip_btn);
                                                            buvip.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent(mContext, BuyvipActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                            Button cancel = view.findViewById(R.id.goback_btn);
                                                            cancel.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                        }else {
                                                            OkHttpInstance.deleteFriendNumber(userInfoList.getId(),Common.userInfoList.getId(),response -> {
                                                                make_friend.setImageResource(R.mipmap.make_friend);
                                                                make_friend.setOnClickListener(view -> {
                                                                    make_friend_layout.setVisibility(View.VISIBLE);
                                                                });
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }else {
                                            Toast.makeText(mContext,responseString,Toast.LENGTH_LONG).show();
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
                                            TextView buyvip_tv = view.findViewById(R.id.buyvip_tv);
                                            buyvip_tv.setText("非会员每日可发起5次添加好友请求，请明日再试，也可开通会员，享受无限制添加好友和7大特权");
                                            Button buvip = view.findViewById(R.id.buyvip_btn);
                                            buvip.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(mContext, BuyvipActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                            Button cancel = view.findViewById(R.id.goback_btn);
                                            cancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                    });
                    make_friend_gift.setOnClickListener(v -> {
                        make_friend_gift.setText("不要忘了点击开始申请哦！");
                        try {
                            GiftDialog.getInstance(PersonageActivity.this, false, container, new GiftCallBack() {
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
                            }).initShow(new Member(userInfoList.getId(),userInfoList.getNickname(),
                                    userInfoList.getPortrait(), userInfoList.getGender(),
                                    userInfoList.getProperty(),userInfoList.getPortraitframe(),userInfoList.getVeilcel()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }


                if (Common.isFollow(userInfoList.getId())){
                    delete_friend_iv.setOnClickListener(v -> {
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
                        prompt.setText("确定要取消关注吗？");
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
                                more_layout.setVisibility(View.GONE);
                                OkHttpInstance.unfollow(userInfoList.getId(), new OkHttpResponseCallBack() {
                                    @Override
                                    public void getResponseString(String responseString) {

                                    }
                                });
                                Toast.makeText(mContext,"已取消关注",Toast.LENGTH_LONG).show();
                                delete_friend_iv.setVisibility(View.GONE);
                                dialog.dismiss();
                            }
                        });
                    });
                }else {
                    delete_friend_iv.setVisibility(View.GONE);
                }


                report_iv.setOnClickListener(v -> {
                    ReportDialog reportDialog = new ReportDialog(PersonageActivity.this);
                    reportDialog.show(ReportDialog.REPORT_TYPE_1,userInfoList.getId());
                });
                if (Common.isBlackListMe(userInfoList.getId())){
                    add_blacklist_iv.setVisibility(View.GONE);
                }else {
                    add_blacklist_iv.setOnClickListener(v -> {
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
                        prompt.setText("确定要加入黑名单吗？");
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
                                more_layout.setVisibility(View.GONE);
                                add_blacklist_iv.setVisibility(View.GONE);
                                Toast.makeText(mContext,"已加入黑名单",Toast.LENGTH_LONG).show();
                                OkHttpInstance.addblacklist(userInfoList.getId(), new OkHttpResponseCallBack() {
                                    @Override
                                    public void getResponseString(String responseString) {

                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                    });
                }

                copy_id.setOnClickListener(v -> {
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(userInfoList.getId());
                    Toast.makeText(mContext,"复制到剪切板",Toast.LENGTH_LONG).show();
                    more_layout.setVisibility(View.GONE);
                });


                //adpter绑定数据
                exampleFragmentAdapter = new ExampleFragmentAdapter(luntanLists);
                //RecyclerView绑定adpter
                recyclerView.setAdapter(exampleFragmentAdapter);
                recyclerView.setPullRefreshEnable(false);
                recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(PersonageActivity.this));
                recyclerView.setLinearLayout();
                OkHttpInstance.getDataPostlist(userInfoList.getId(), "1", new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        if (responseString.equals("false")){

                        }else {
                            recyclerView.stopWaveLoadingShow();
                            luntanLists = JSON.parseArray(responseString,LuntanList.class);
                            exampleFragmentAdapter.setluntanLists(luntanLists);
                        }
                    }
                });
                recyclerView.setOnPullLoadMoreListener(new CusPullLoadMoreRecyclerView.PullLoadMoreListener() {
                    @Override
                    public void onRefresh() {

                    }

                    @Override
                    public void onLoadMore() {
                        pageindex = pageindex+1;
                        OkHttpInstance.getDataPostlist(userInfoList.getId(), pageindex+"", new OkHttpResponseCallBack() {
                            @Override
                            public void getResponseString(String responseString) {
                                if (responseString.equals("false")){

                                }else {
                                    luntanLists.addAll(JSON.parseArray(responseString,LuntanList.class));
                                    exampleFragmentAdapter.swapData(luntanLists);
                                }
                            }
                        });
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setPullLoadMoreCompleted();
                            }
                        },1000);
                    }
                });
            }
        });

    }

    /**
     * 使用 RenderScript 对图片进行高斯模糊
     *
     * @param context
     * @param originImage 原图
     * @param blurRadius 模糊半径，取值区间为 (0, 25]
     * @param scaleRatio 缩小比例，假设传入 a，那么图片的宽高是原来的 1 / a 倍，取值 >= 1
     * @return
     */
    public static Bitmap blurBitmap(Context context, Bitmap originImage,
                                    float blurRadius, int scaleRatio) {
        if (blurRadius <= 0 || blurRadius > 25f || scaleRatio < 1) {
            throw new IllegalArgumentException("ensure blurRadius in (0, 25] and scaleRatio >= 1");
        }

        // 计算图片缩小后的宽高
        int width = originImage.getWidth() / scaleRatio;
        int height = originImage.getHeight() / scaleRatio;

        // 创建缩小的 Bitmap
        Bitmap bitmap = Bitmap.createScaledBitmap(originImage, width, height, false);

        // 创建 RenderScript 对象
        RenderScript rs = RenderScript.create(context);
        // 创建一个带模糊效果的工具对象
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // 由于 RenderScript 没有使用 VM 来分配内存，所以需要使用 Allocation 类来创建和分配内存空间
        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        // 创建相同类型的 Allocation 对象用来输出
        Allocation output = Allocation.createTyped(rs, input.getType());

        // 设置渲染的模糊程度，最大为 25f
        blur.setRadius(blurRadius);
        // 设置输入和输出内存
        blur.setInput(input);
        blur.forEach(output);
        // 将数据填充到 Bitmap
        output.copyTo(bitmap);

        // 销毁它们的内存
        input.destroy();
        output.destroy();
        blur.destroy();
        rs.destroy();

        return bitmap;
    }

    public class ExampleFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<LuntanList> luntanLists = new ArrayList<>();
        private final static int TYPE_HEAD = 0;
        private final static int TYPE_CONTENT = 1;
        public ExampleFragmentAdapter(List<LuntanList> luntanLists) {
            this.luntanLists = luntanLists;
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            Log.d(TAG, "onCreateViewHolder: 进入");
            if (i == TYPE_HEAD){
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_person_header,viewGroup,false);
                HeaderViewHolder viewHolder = new HeaderViewHolder(view);
                return viewHolder;
            }else if(i == TYPE_CONTENT){
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_peson_post,viewGroup,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int currentPosition = position;
            if (holder instanceof HeaderViewHolder){

                ((HeaderViewHolder) holder).goToRoom.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, ChatRoomActivity.class);
                    intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_ID, mUserID);
                    intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_TYPE, userInfoList.getAudioroomtype());
                    intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, io.agora.chatroom.R.mipmap.bg_channel_0);
                    startActivity(intent);
                });
                ((HeaderViewHolder) holder).follow_img.setOnClickListener(v -> {
                    Toast.makeText(mContext,"已关注",Toast.LENGTH_LONG).show();
                    ((HeaderViewHolder) holder).follow_img.setVisibility(View.GONE);
                    OkHttpInstance.follow(mUserID, responseString -> {
                        Common.followList.add(new FollowList(mUserID));
                    });
                });
                if (!Common.isFollow(mUserID)){
                    ((HeaderViewHolder) holder).follow_img.setVisibility(View.VISIBLE);
                }else {
                    ((HeaderViewHolder) holder).follow_img.setVisibility(View.GONE);
                }


                ((HeaderViewHolder) holder).wealth_view.initShow("wealth",userInfoList.getAll_money());
                ((HeaderViewHolder) holder).glamour_view.initShow("glamour",userInfoList.getAll_income());
                if (luntanLists.size()==0){
                    ((HeaderViewHolder) holder).no_dynamic.setVisibility(View.VISIBLE);
                }else {
                    ((HeaderViewHolder) holder).no_dynamic.setVisibility(View.GONE);
                }
                ((HeaderViewHolder) holder).user_location.setText(userInfoList.getLocation()+"km");
                ((HeaderViewHolder) holder).back_img.setOnClickListener(v -> {
                    onBackPressed();
                });
                ((HeaderViewHolder) holder).more_menu.setOnClickListener(v -> {
                    if (more_layout.getVisibility()==View.GONE){
                        more_layout.setVisibility(View.VISIBLE);
                    }else {
                        more_layout.setVisibility(View.GONE);
                    }
                });




                ((HeaderViewHolder) holder).id_tv.setText("乐园id："+userInfoList.getId());
                ((HeaderViewHolder) holder).user_nickname.setText(userInfoList.getNickname());
                ((HeaderViewHolder) holder).fans_tv.setText("粉丝："+userInfoList.getFans());
                ((HeaderViewHolder) holder).fans_tv.setOnClickListener(v -> {
                    Intent intent = new Intent(PersonageActivity.this,FollowAndFansActivity.class);
                    intent.putExtra("userId",userInfoList.getId());
                    intent.putExtra("type",1);
                    PersonageActivity.this.startActivity(intent);
                });
                ((HeaderViewHolder) holder).follow_tv.setText("关注："+userInfoList.getFollow());
                ((HeaderViewHolder) holder).follow_tv.setOnClickListener(v -> {
                    Intent intent = new Intent(PersonageActivity.this,FollowAndFansActivity.class);
                    intent.putExtra("userId",userInfoList.getId());
                    intent.putExtra("type",0);
                    PersonageActivity.this.startActivity(intent);
                });
                ((HeaderViewHolder) holder).user_age.setText(userInfoList.getAge());
                if (userInfoList.getGender().equals("男")){
                    Resources resources = mContext.getResources();
                    Drawable drawable = resources.getDrawable(R.mipmap.bboy,null);
                    //((UserinfoHolder) viewHolder).userGender.setForeground(drawable);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    ((HeaderViewHolder) holder).user_age.setCompoundDrawables(drawable, null, null, null);
                    ((HeaderViewHolder) holder).user_age.setBackgroundResource(R.mipmap.h_sex_male);
                }else {
                    Resources resources = mContext.getResources();
                    Drawable drawable = resources.getDrawable(R.mipmap.ggirl,null);
                    //((UserinfoHolder) viewHolder).userGender.setForeground(drawable);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    ((HeaderViewHolder) holder).user_age.setCompoundDrawables(drawable, null, null, null);
                    ((HeaderViewHolder) holder).user_age.setBackgroundResource(R.mipmap.h_sex);
                }
                ((HeaderViewHolder) holder).user_property.setText(userInfoList.getProperty());
                ((HeaderViewHolder) holder).user_region.setText(userInfoList.getRegion());
                ((HeaderViewHolder) holder).user_signature.setText(userInfoList.getSignature());

                Glide.with(App.getApplication())
                        .asBitmap()
                        .load(Common.getOssResourceUrl(userInfoList.getPortrait()))
                        .into(((HeaderViewHolder) holder).user_portrait);
                Glide.with(App.getApplication())
                        .load(Common.getOssResourceUrl(userInfoList.getPortrait()))
                        .apply(new RequestOptions()
                                .transform(new BitmapTransformation() {
                                    @Override
                                    protected Bitmap transform(@NonNull BitmapPool pool,
                                                               @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                                        // 对得到的 Bitmap 进行虚化处理，这里使用了上面的 RenderScript 高斯模糊方法
                                        return blurBitmap(App.getApplication(), toTransform, 5, 8);
                                    }

                                    @Override
                                    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

                                    }
                                }))
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    ((HeaderViewHolder) holder).back_img.setBackground(resource);
                                }
                            }
                        });

                ((HeaderViewHolder) holder).portraitFrameView.setPortraitFrame(Common.getOssResourceUrl(userInfoList.getPortraitframe()));


                ((HeaderViewHolder) holder).online_tv.setText(userInfoList.getOnline());
                if (userInfoList.getOnline().equals("在线")){
                    Glide.with(App.getApplication()).load(R.mipmap.green_point).into(((HeaderViewHolder) holder).online_img);
                }else {
                    Glide.with(App.getApplication()).load(R.mipmap.gray_point).into(((HeaderViewHolder) holder).online_img);
                }

                ((HeaderViewHolder) holder).lv_img.setImageResource(Common.getLevelFromUser(userInfoList));

                //实名认证
                if (userInfoList.getRp_verify_time().equals("0")){
                    ((HeaderViewHolder) holder).rp_verify_img.setVisibility(View.GONE);
                }else {
                    ((HeaderViewHolder) holder).rp_verify_img.setVisibility(View.VISIBLE);
                }
                //vip
                Integer current_timestamp = Math.round(new Date().getTime()/1000);
                if (!TextUtils.isEmpty(userInfoList.getSvip())) {
                    int svip_time = Integer.parseInt(userInfoList.getSvip() + "");
                    if (svip_time > current_timestamp) {
                        //vipicon分级
                        Log.d("会员时长", ((svip_time - current_timestamp) + ""));
                        if ((svip_time - current_timestamp) < 3600 * 24 * 30) {
                            Glide.with(App.getApplication())
                                    .asBitmap()
                                    .load(R.drawable.ic_svip_diamond)
                                    .into(((HeaderViewHolder) holder).vip_gray);
                        } else if ((svip_time - current_timestamp) < 3600 * 24 * 180) {
                            Glide.with(App.getApplication())
                                    .asBitmap()
                                    .load(R.drawable.ic_svip_black)
                                    .into(((HeaderViewHolder) holder).vip_gray);
                        } else {
                            Glide.with(App.getApplication())
                                    .asBitmap()
                                    .load(R.drawable.ic_svip_white)
                                    .into(((HeaderViewHolder) holder).vip_gray);
                        }
                    } else {
                        if (!TextUtils.isEmpty(userInfoList.getVip())) {
                            int vip_time = Integer.parseInt(userInfoList.getVip() + "");
                            if (vip_time > current_timestamp) {
                                //vipicon分级
                                Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                                if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                                    Glide.with(App.getApplication())
                                            .asBitmap()
                                            .load(R.drawable.ic_vip_diamond)
                                            .into(((HeaderViewHolder) holder).vip_gray);
                                } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                                    Glide.with(App.getApplication())
                                            .asBitmap()
                                            .load(R.drawable.ic_vip_black)
                                            .into(((HeaderViewHolder) holder).vip_gray);
                                } else {
                                    Glide.with(App.getApplication())
                                            .asBitmap()
                                            .load(R.drawable.ic_vip_white)
                                            .into(((HeaderViewHolder) holder).vip_gray);
                                }
                            } else {
                                Glide.with(App.getApplication())
                                        .asBitmap()
                                        .load(R.drawable.ic_vip_gray)
                                        .into(((HeaderViewHolder) holder).vip_gray);
                            }
                        }else {
                            Glide.with(App.getApplication())
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_gray)
                                    .into(((HeaderViewHolder) holder).vip_gray);
                        }
                    }
                }else if (userInfoList.getVip()!="null") {
                    int vip_time = Integer.parseInt(userInfoList.getVip() + "");
                    if (vip_time > current_timestamp) {
                        //vipicon分级
                        Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                        if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                            Glide.with(App.getApplication())
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_diamond)
                                    .into(((HeaderViewHolder) holder).vip_gray);
                        } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                            Glide.with(App.getApplication())
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_black)
                                    .into(((HeaderViewHolder) holder).vip_gray);
                        } else {
                            Glide.with(App.getApplication())
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_white)
                                    .into(((HeaderViewHolder) holder).vip_gray);
                        }
                    } else {
                        Glide.with(App.getApplication())
                                .asBitmap()
                                .load(R.drawable.ic_vip_gray)
                                .into(((HeaderViewHolder) holder).vip_gray);
                    }
                }else {
                    Glide.with(App.getApplication())
                            .asBitmap()
                            .load(R.drawable.ic_vip_gray)
                            .into(((HeaderViewHolder) holder).vip_gray);
                }
            }else if (holder instanceof ViewHolder){
                final LuntanList currentItem = luntanLists.get(currentPosition-1);
                Log.d(TAG, "onBindViewHolder: 测试帖子"+currentItem.getAuthid()+currentItem.getAuthnickname());


                ((ViewHolder) holder).lv_img.setImageResource(Common.getLevelFromPost(currentItem));

                ((ViewHolder) holder).authnickname.setText(currentItem.getAuthnickname());


                ((ViewHolder) holder).online_tv.setText(currentItem.getOnline());
                if (currentItem.getOnline().equals("在线")){
                    Glide.with(mContext).load(R.mipmap.green_point).into( ((ViewHolder) holder).online_img);
                }else {
                    Glide.with(mContext).load(R.mipmap.gray_point).into( ((ViewHolder) holder).online_img);
                }

                ((ViewHolder) holder).portraitFrameView.setPortraitFrame(currentItem.getPortraitframe());
                ((ViewHolder) holder).portraitFrameView.setTag(currentItem.getPortraitframe());
                Glide.with(mContext)
                        .asBitmap()
                        .load(Common.getOssResourceUrl(currentItem.getAuthportrait()))
                        .into(((ViewHolder) holder).authportrait);



                if (currentItem.getPlatename().equals("招募令")){
                    ((ViewHolder) holder).goToRoomBtn.setVisibility(View.VISIBLE);
                    ((ViewHolder) holder).goToRoomBtn.setOnClickListener(v -> {
                        Intent intent = new Intent(mContext, ChatRoomActivity.class);
                        intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_ID, currentItem.getAuthid());
                        intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_TYPE, "处CP");
                        intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, io.agora.chatroom.R.mipmap.bg_channel_0);
                        //intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, Constant.sRoomBG);
                        mContext.startActivity(intent);
                    });
                }else {
                    ((ViewHolder) holder).goToRoomBtn.setVisibility(View.GONE);
                }

                //GOTO  会员标识
                //现在vip传过来的是时间
                ((ViewHolder) holder).vip_gray.setVisibility(View.VISIBLE);
                Integer current_timestamp = Math.round(new Date().getTime()/1000);
                if (TextUtils.isEmpty(currentItem.getAuthsvip())){
                    if (TextUtils.isEmpty(currentItem.getAuthvip())){
                        ((ViewHolder) holder).vip_gray.setVisibility(View.GONE);
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_vip_gray)
                                .into(((ViewHolder) holder).vip_gray);
                    }else {
                        Log.d("会员时间",currentItem.getAuthvip()+"");
                        int vip_time = Integer.parseInt(currentItem.getAuthvip()+"");
                        if (vip_time > current_timestamp) {
                            ((ViewHolder) holder).vip_gray.setVisibility(View.VISIBLE);
                            //vipicon分级
                            Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                            if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_vip_diamond)
                                        .into(((ViewHolder) holder).vip_gray);
                            } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_vip_black)
                                        .into(((ViewHolder) holder).vip_gray);
                            } else {
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_vip_white)
                                        .into(((ViewHolder) holder).vip_gray);
                            }
                        } else {
                            ((ViewHolder) holder).vip_gray.setVisibility(View.GONE);
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_gray)
                                    .into(((ViewHolder) holder).vip_gray);
                        }
                    }
                }else {
                    Log.d("会员时间",currentItem.getAuthsvip()+"");
                    int svip_time = Integer.parseInt(currentItem.getAuthsvip()+"");
                    if (svip_time > current_timestamp) {
                        ((ViewHolder) holder).vip_gray.setVisibility(View.VISIBLE);
                        //vipicon分级
                        Log.d("会员时长", ((svip_time - current_timestamp) + ""));
                        if ((svip_time - current_timestamp) < 3600 * 24 * 30) {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_svip_diamond)
                                    .into(((ViewHolder) holder).vip_gray);
                        } else if ((svip_time - current_timestamp) < 3600 * 24 * 180) {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_svip_black)
                                    .into(((ViewHolder) holder).vip_gray);
                        } else {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_svip_white)
                                    .into(((ViewHolder) holder).vip_gray);
                        }
                    } else {
                        if (TextUtils.isEmpty(currentItem.getAuthvip())){
                            ((ViewHolder) holder).vip_gray.setVisibility(View.GONE);
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_gray)
                                    .into(((ViewHolder) holder).vip_gray);
                        }else {
                            Log.d("会员时间",currentItem.getAuthvip()+"");
                            int vip_time = Integer.parseInt(currentItem.getAuthvip()+"");
                            if (vip_time > current_timestamp) {
                                ((ViewHolder) holder).vip_gray.setVisibility(View.VISIBLE);
                                //vipicon分级
                                Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                                if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                                    Glide.with(mContext)
                                            .asBitmap()
                                            .load(R.drawable.ic_vip_diamond)
                                            .into(((ViewHolder) holder).vip_gray);
                                } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                                    Glide.with(mContext)
                                            .asBitmap()
                                            .load(R.drawable.ic_vip_black)
                                            .into(((ViewHolder) holder).vip_gray);
                                } else {
                                    Glide.with(mContext)
                                            .asBitmap()
                                            .load(R.drawable.ic_vip_white)
                                            .into(((ViewHolder) holder).vip_gray);
                                }
                            } else {
                                ((ViewHolder) holder).vip_gray.setVisibility(View.GONE);
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_vip_gray)
                                        .into(((ViewHolder) holder).vip_gray);
                            }
                        }
                    }
                }

                //((ViewHolder) viewHolder).posttip.setText(currentItem.getPosttip().isEmpty()?"":currentItem.getPosttip());
                ((ViewHolder) holder).posttip.setText("加精");
                if (currentItem.getPosttip().equals("加精")){
//                Resources resources = mContext.getResources();
//                Drawable drawable = resources.getDrawable(R.drawable.ic_essence,null);
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                ((ViewHolder) viewHolder).posttip.setCompoundDrawables(drawable,null,null,null);
                    ((ViewHolder) holder).posttip.setVisibility(View.VISIBLE);
                    ((ViewHolder) holder).posttop.setVisibility(View.GONE);
                }else if(currentItem.getPosttip().equals("置顶")){
//                Resources resources = mContext.getResources();
//                Drawable drawable = resources.getDrawable(R.drawable.ic_ontop,null);
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                ((ViewHolder) viewHolder).posttip.setCompoundDrawables(drawable,null,null,null);
                    ((ViewHolder) holder).posttip.setVisibility(View.GONE);
                    ((ViewHolder) holder).posttop.setVisibility(View.VISIBLE);
                }else if (currentItem.getPosttip().equals("置顶,加精")){
//                Resources resources = mContext.getResources();
//                Drawable drawable1 = resources.getDrawable(R.drawable.ic_essence,null);
//                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
//                Drawable drawable2 = resources.getDrawable(R.drawable.ic_ontop,null);
//                drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
//                ((ViewHolder) viewHolder).posttip.setCompoundDrawables(drawable1,null,drawable2,null);
                    ((ViewHolder) holder).posttip.setVisibility(View.VISIBLE);
                    ((ViewHolder) holder).posttop.setVisibility(View.VISIBLE);
                }else {
                    //必须加上，否则会错乱
//                ((ViewHolder) viewHolder).posttip.setCompoundDrawables(null,null,null,null);
                    ((ViewHolder) holder).posttip.setVisibility(View.GONE);
                    ((ViewHolder) holder).posttop.setVisibility(View.GONE);
                }

                if (TextUtils.isEmpty(currentItem.getPosttitle())){
                    ((ViewHolder) holder).posttitle.setVisibility(View.GONE);
                }else {
                    ((ViewHolder) holder).posttitle.setVisibility(View.VISIBLE);
                    ((ViewHolder) holder).posttitle.setText(currentItem.getPosttitle());
                }

                if (currentItem.getPosttext().length()>50) {
                    ((ViewHolder) holder).posttext.setText(currentItem.getPosttext().substring(0, 50)+"......");
                    ((ViewHolder) holder).detail_content.setVisibility(View.VISIBLE);
                    ((ViewHolder) holder).detail_content.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((ViewHolder) holder).posttext.setText(currentItem.getPosttext());
                            ((ViewHolder) holder).detail_content.setVisibility(View.GONE);
                        }
                    });
                }else {
                    ((ViewHolder) holder).posttext.setText(currentItem.getPosttext());
                    ((ViewHolder) holder).detail_content.setVisibility(View.GONE);
                }

                if (currentItem.getPostpicture()!=null){
                    if (currentItem.getPostpicture().contains("images")){
                        String[] postPicture = currentItem.getPostpicture().split(",");
                        PictureAdapter pictureAdapter = new PictureAdapter(mContext,currentItem);
                        ((ViewHolder) holder).recyclerView.setVisibility(View.VISIBLE);
                        ((ViewHolder) holder).recyclerView.setAdapter(pictureAdapter);
                        if (postPicture.length==1){
                            ((ViewHolder) holder).recyclerView.setLayoutManager(new GridLayoutManager(mContext,1));
                        }else if(postPicture.length==2 || postPicture.length==4){
                            ((ViewHolder) holder).recyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
                        }else {
                            ((ViewHolder) holder).recyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
                        }

                        pictureAdapter.setPictures(Arrays.asList(postPicture));
                    }else {
                        ((ViewHolder) holder).recyclerView.setVisibility(View.GONE);
                    }
                }


                ((ViewHolder) holder).like.setText(""+currentItem.getLike());
                ((ViewHolder) holder).like_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ViewHolder) holder).like.setTextColor(mContext.getResources().getColor(R.color.red));
                        //Toast.makeText(mContext, mUserID.get(i) + mUserNickName.get(i), Toast.LENGTH_LONG).show();
                        ((ViewHolder) holder).like.setText((Integer.parseInt(currentItem.getLike())+1)+"");
                        OkHttpInstance.like(currentItem.getId(), new OkHttpResponseCallBack() {
                            @Override
                            public void getResponseString(String responseString) {

                            }
                        });
                    }
                });

                ((ViewHolder) holder).time.setText(currentItem.getTime());


                ((ViewHolder) holder).menu_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InformBlacklistAdapter adapter = new InformBlacklistAdapter(mContext, "post", currentItem.getId(), currentItem.getAuthid(), new OkHttpResponseCallBack() {
                            @Override
                            public void getResponseString(String responseString) {
                                runOnUiThread(()->{
                                    luntanLists.remove(currentPosition-1);
                                    exampleFragmentAdapter.notifyDataSetChanged();
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
                ((ViewHolder) holder).comment_tv.setText(""+currentItem.getComment_sum());
                ((ViewHolder) holder).comment_layout.setOnClickListener(view -> {
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
                        ((ViewHolder) holder).comment_tv.setTextColor(mContext.getResources().getColor(R.color.red));
                        commentDialog.loadComment(currentItem);
                        commentDialog.startLoadComment();
                    }
                });

                ((ViewHolder) holder).userAge.setText(" "+currentItem.getAuthage());
                ((ViewHolder) holder).userRegion.setText(currentItem.getAuthregion());
                ((ViewHolder) holder).userProperty.setText(currentItem.getAuthproperty());
                if (currentItem.getAuthgender().equals("男")){
                    Resources resources = mContext.getResources();
                    Drawable drawable = resources.getDrawable(R.mipmap.bboy,null);
                    //((UserinfoHolder) viewHolder).userGender.setForeground(drawable);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    ((ViewHolder) holder).userAge.setCompoundDrawables(drawable, null, null, null);
                    ((ViewHolder) holder).userAge.setBackgroundResource(R.mipmap.h_sex_male);
                }else {
                    Resources resources = mContext.getResources();
                    Drawable drawable = resources.getDrawable(R.mipmap.ggirl,null);
                    //((UserinfoHolder) viewHolder).userGender.setForeground(drawable);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    ((ViewHolder) holder).userAge.setCompoundDrawables(drawable, null, null, null);
                    ((ViewHolder) holder).userAge.setBackgroundResource(R.mipmap.h_sex);
                }

                Log.d(TAG, "onBindViewHolder: 帖子视频"+currentItem.getPostvideo());
                if (!currentItem.getPostvideo().equals("https://oss.banghua.xin/0")){
                    if (Integer.parseInt(currentItem.getHeight())>=Integer.parseInt(currentItem.getWidth())){
                        ((ViewHolder) holder).video_layout.getLayoutParams().height = 700;
                        int width = Math.round(700*((Float.parseFloat(currentItem.getWidth())/Float.parseFloat(currentItem.getHeight()))));
                        ((ViewHolder) holder).video_layout.getLayoutParams().width = width;

                        ((ViewHolder) holder).cover_view.getLayoutParams().height = 700;
                        ((ViewHolder) holder).cover_view.getLayoutParams().width = width;
                    }else {
                        ((ViewHolder) holder).video_layout.getLayoutParams().width = 700;
                        int height = Math.round(700*((Float.parseFloat(currentItem.getHeight())/Float.parseFloat(currentItem.getWidth()))));
                        ((ViewHolder) holder).video_layout.getLayoutParams().height = height;

                        ((ViewHolder) holder).cover_view.getLayoutParams().width = 700;
                        ((ViewHolder) holder).cover_view.getLayoutParams().height = height;
                    }
                    Glide.with(mContext).load(currentItem.getCover()).into(((ViewHolder) holder).cover_view);

                    ((ViewHolder) holder).video_layout.setOnClickListener(v -> {
                        Log.d(TAG, "222luntanList.getPlay_once(): "+currentItem.getPlay_once());
                        Intent intent = new Intent(mContext, VideoPlayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("luntanList", (Serializable) currentItem);
                        mContext.startActivity(intent);
                    });
                    ((ViewHolder) holder).video_layout.setVisibility(View.VISIBLE);
                    if (currentItem.getStartVideo()){
                        ((ViewHolder) holder).video_layout.addView(CustomVideoView.getInstance(mContext,currentItem),2);
                    }
                }else {
                    ((ViewHolder) holder).video_layout.setVisibility(View.GONE);
                }


                ((ViewHolder) holder).follow_img.setVisibility(View.VISIBLE);
                Log.d(TAG, "onBindViewHolder: 关注的人"+Common.followList.size());
                for (int j = 0;j < Common.followList.size();j++){
                    Log.d(TAG, "onBindViewHolder: 关注的人"+Common.followList.get(j));
                    if (Common.followList.get(j).getUserId().equals(currentItem.getAuthid())){
                        ((ViewHolder) holder).follow_img.setVisibility(View.GONE);
                    }
                }

                ((ViewHolder) holder).follow_img.setOnClickListener(v -> {
                    ((ViewHolder) holder).follow_img.setVisibility(View.GONE);
                    Toast.makeText(mContext,"关注成功",Toast.LENGTH_SHORT).show();
                    OkHttpInstance.follow(currentItem.getAuthid(), responseString -> {
                        Common.followList.add(new FollowList(currentItem.getAuthid()));
                        notifyDataSetChanged();
                    });
                });

                if (!TextUtils.isEmpty(currentItem.getTopic())){
                    if (currentItem.getTopic()!="null"){
                        List<TopicList> topicLists = JSON.parseArray(currentItem.getTopic(),TopicList.class);
                        ((ViewHolder) holder).topic_layout.removeAllViews();
                        for (int j = 0;j<topicLists.size();j++){
                            TopicAddedItem topicAddedItem = new TopicAddedItem(mContext);
                            topicAddedItem.initShow(topicLists.get(j));
                            ((ViewHolder) holder).topic_layout.addView(topicAddedItem);
                        }
                    }
                }

                if (!currentItem.getRp_verify_time().equals("0")){
                    ((ViewHolder) holder).rp_verify_img.setVisibility(View.VISIBLE);
                }else {
                    ((ViewHolder) holder).rp_verify_img.setVisibility(View.GONE);
                }
            }
        }

        public void setluntanLists(List<LuntanList> luntanLists){
            this.luntanLists = luntanLists;
        }
        public void swapData(List<LuntanList> luntanLists){
            int oldSize = this.luntanLists.size();
            int newSize = luntanLists.size();
            this.luntanLists = luntanLists;
            notifyItemRangeInserted(oldSize+1 , newSize+1);
        }


        @Override
        public int getItemCount() {
            return luntanLists.size() + 1;
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
    }


    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.user_location)
        TextView user_location;

        @BindView(R.id.no_dynamic)
        TextView no_dynamic;

        @BindView(R.id.back_img)
        ConstraintLayout back_img;
        @BindView(R.id.more_menu)
        ImageView more_menu;
        @BindView(R.id.user_portrait)
        CircleImageViewExtension user_portrait;
        @BindView(R.id.portraitFrameView)
        PortraitFrameView portraitFrameView;
        @BindView(R.id.vip_gray)
        ImageView vip_gray;
        @BindView(R.id.user_nickname)
        TextView user_nickname;
        @BindView(R.id.lv_img)
        ImageView lv_img;
        @BindView(R.id.rp_verify_img)
        ImageView rp_verify_img;
        @BindView(R.id.online_tv)
        TextView online_tv;
        @BindView(R.id.online_img)
        ImageView online_img;
        @BindView(R.id.user_age)
        TextView user_age;
        @BindView(R.id.user_property)
        TextView user_property;
        @BindView(R.id.user_region)
        TextView user_region;
        @BindView(R.id.follow_tv)
        TextView follow_tv;
        @BindView(R.id.fans_tv)
        TextView fans_tv;
        @BindView(R.id.id_tv)
        TextView id_tv;
        @BindView(R.id.user_signature)
        TextView user_signature;


        @BindView(R.id.wealth_view)
        WealthAndGlamour wealth_view;
        @BindView(R.id.glamour_view)
        WealthAndGlamour glamour_view;

        @BindView(R.id.goToRoom)
        ImageView goToRoom;
        @BindView(R.id.follow_img)
        ImageView follow_img;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

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
}