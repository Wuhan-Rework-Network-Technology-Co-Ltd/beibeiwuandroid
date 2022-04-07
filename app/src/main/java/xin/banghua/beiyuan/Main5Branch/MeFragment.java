package xin.banghua.beiyuan.Main5Branch;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.orhanobut.dialogplus.DialogPlus;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.agora.chatroom.util.PortraitFrameView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.App;
import xin.banghua.beiyuan.CircleImageViewExtension;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.Personage.FollowAndFansActivity;
import xin.banghua.beiyuan.Personage.Someonesluntan1Activity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.SliderWebViewActivity;
import xin.banghua.beiyuan.comment.CommentListActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {

    private static final String TAG = "MeFragment";
    CircleImageViewExtension userportrait_iv;
    TextView usernickname_tv;
    TextView beiyuanid_btn;
    TextView personalinfo_btn;
    TextView user_signature;
    LinearLayout xiangce_btn;
    LinearLayout openvip_btn;
    LinearLayout opensvip_btn;
    LinearLayout luntan_btn;
    LinearLayout comment_btn;
    LinearLayout jifen_btn;
    LinearLayout tuiguangma_btn;
    LinearLayout sawme_btn;
    LinearLayout setting_btn;

    LinearLayout wallet_btn;
    LinearLayout store_btn;

    String myportrait;

    private TextView privacypolity_btn,useragreement_btn;
    private Context mContext;

    ImageView vip_gray;
    ImageView vip_diamond;
    ImageView vip_black;
    ImageView vip_white;

    PortraitFrameView portraitFrameView;
    TextView follow_tv,fans_tv;

    ImageView lv_img;
    ImageView rp_verify_img;
    public MeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.userInfoList!=null){
            portraitFrameView.setPortraitFrame(Common.userInfoList.getPortraitframe());
            usernickname_tv.setText(Common.userInfoList.getNickname());

            lv_img.setImageResource(Common.getLevelFromUser(Common.userInfoList));


            follow_tv.setText("关注："+Common.userInfoList.getFollow());

            fans_tv.setText("粉丝："+Common.userInfoList.getFans());


            if (!Common.userInfoList.getRp_verify_time().equals("0")){
                rp_verify_img.setVisibility(View.VISIBLE);
            }else {
                rp_verify_img.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rp_verify_img = view.findViewById(R.id.rp_verify_img);

        lv_img = view.findViewById(R.id.lv_img);
        portraitFrameView = view.findViewById(R.id.portraitFrameView);
        follow_tv = view.findViewById(R.id.follow_tv);
        fans_tv = view.findViewById(R.id.fans_tv);
        //vip
        getVipinfo("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=viptimeinsousuo&m=socialchat");

        initData(view);

        store_btn = view.findViewById(R.id.store_btn);
        store_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StoreActivity.class);
                mContext.startActivity(intent);
            }
        });

        wallet_btn = view.findViewById(R.id.wallet_btn);
        wallet_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WalletActivity.class);
                mContext.startActivity(intent);
            }
        });

        useragreement_btn = view.findViewById(R.id.useragreement_btn);
        useragreement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
                intent.putExtra("slidername","小贝乐园用户协议");
                intent.putExtra("sliderurl","https://console.banghua.xin/useragreement.html");
                mContext.startActivity(intent);
            }
        });
        privacypolity_btn = view.findViewById(R.id.privacypolicy_btn);
        privacypolity_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
                intent.putExtra("slidername","小贝乐园隐私政策");
                intent.putExtra("sliderurl","https://console.banghua.xin/privacypolicy.html");
                mContext.startActivity(intent);
            }
        });
    }
    //TODO okhttp获取用户信息
    public void getVipinfo(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(getActivity().getApplicationContext());
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
                    message.what=3;
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void initData(View view) {
        vip_gray = view.findViewById(R.id.vip_gray);
//        vip_diamond = view.findViewById(R.id.vip_diamond);
//        vip_black = view.findViewById(R.id.vip_black);
//        vip_white = view.findViewById(R.id.vip_white);

        user_signature = view.findViewById(R.id.user_signature);
        if (Common.userInfoList != null){
            user_signature.setText(Common.userInfoList.getSignature());

            follow_tv.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), FollowAndFansActivity.class);
                intent.putExtra("userId",Common.userInfoList.getId());
                intent.putExtra("type",0);
                getActivity().startActivity(intent);
            });

            fans_tv.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(),FollowAndFansActivity.class);
                intent.putExtra("userId",Common.userInfoList.getId());
                intent.putExtra("type",1);
                getActivity().startActivity(intent);
            });
        }

        userportrait_iv = view.findViewById(R.id.userportrait_iv);
        userportrait_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ResetActivity.class);
                intent.putExtra("title","头像设置");
                startActivity(intent);
            }
        });
        usernickname_tv = view.findViewById(R.id.usernickname_tv);
        usernickname_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ResetActivity.class);
                intent.putExtra("title","昵称设置");
                startActivity(intent);
            }
        });
        beiyuanid_btn = view.findViewById(R.id.beiyuanid_btn);
        personalinfo_btn = view.findViewById(R.id.personalinfo_btn);
        user_signature = view.findViewById(R.id.user_signature);
        //xiangce_btn = view.findViewById(R.id.xiangce_btn);
        openvip_btn = view.findViewById(R.id.openvip_btn);
        opensvip_btn = view.findViewById(R.id.opensvip_btn);
        luntan_btn = view.findViewById(R.id.luntan_btn);
        comment_btn = view.findViewById(R.id.comment_btn);
        jifen_btn = view.findViewById(R.id.jifen_btn);
        tuiguangma_btn = view.findViewById(R.id.tuiguangma_btn);
        sawme_btn = view.findViewById(R.id.sawme_btn);
        setting_btn = view.findViewById(R.id.setting_btn);

        SharedHelper shuserinfo = new SharedHelper(getActivity().getApplicationContext());
        final String myid = shuserinfo.readUserInfo().get("userID");


        if (Common.userInfoList!=null){
            usernickname_tv.setText(Common.userInfoList.getNickname());
        }



        beiyuanid_btn.setText("乐园号："+myid);
        personalinfo_btn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.me_reset_action));
        setting_btn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.me_setting_action));
//        xiangce_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(),CircleActivity.class);
//                startActivity(intent);
//            }
//        });
        sawme_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SawMeActivity.class);
                startActivity(intent);
            }
        });

        tuiguangma_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "您的推广码是："+myid, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
                intent.putExtra("slidername","分享推广码");
                intent.putExtra("sliderurl","https://console.banghua.xin/app/index.php?i=99999&c=entry&do=referralgetscore_page&m=socialchat&userid="+myid);
                mContext.startActivity(intent);
            }
        });
        jifen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getScore("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getscore&m=socialchat");
            }
        });
        openvip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),BuyvipActivity.class);
                startActivity(intent);
            }
        });
        opensvip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),BuysvipActivity.class);
                startActivity(intent);
            }
        });
        luntan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedHelper shuserinfo = new SharedHelper(getActivity());
                String mUserID = shuserinfo.readUserInfo().get("userID");
                Intent intent = new Intent(getActivity(), Someonesluntan1Activity.class);
                intent.putExtra("authid",mUserID);
                startActivity(intent);
            }
        });

        comment_btn.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), CommentListActivity.class);
            startActivity(intent);
        });
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
                    final String allscore = msg.obj.toString();
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
                            .setFooter(R.layout.dialog_foot_vipconversion)
                            .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                            .create();
                    dialog.show();
                    View view = dialog.getFooterView();
                    TextView scoreresult = view.findViewById(R.id.scoreresult);
                    scoreresult.setText("您共有"+allscore+"积分！");
                    Button vipconversion_btn = view.findViewById(R.id.vipconversion_btn);
                    vipconversion_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sorttovip("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=sorttovip&m=socialchat",allscore);
                        }
                    });
                    Button dismissdialog_btn = view.findViewById(R.id.dismissdialog_btn);
                    dismissdialog_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    break;
                case 2:
                    Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;
                case 3:

                    String resultJson1 = msg.obj.toString();
                    Log.d(TAG, "handleMessage: 会员时长信息"+msg.obj.toString());
//                    if (!(msg.obj.toString().equals("会员已到期")))  userportrait_iv.isVIP(true,getResources(),false);
                    if (Common.userInfoList!=null){
                        Glide.with(App.getApplication())
                                .asBitmap()
                                .load(Common.userInfoList.getPortrait())
                                .into(userportrait_iv);
                    }

                    if (!(msg.obj.toString().equals("会员已到期"))){
                        // 按指定模式在字符串查找
                        String line = msg.obj.toString();
                        String pattern = "(\\D*)(\\d+)(.*)";
                        // 创建 Pattern 对象
                        Pattern r = Pattern.compile(pattern);
                        // 现在创建 matcher 对象
                        Matcher m = r.matcher(line);
                        m.find( );
                        Log.d(TAG, "handleMessage: 会员时长信息0"+m.group(0));
                        Log.d(TAG, "handleMessage: 会员时长信息1"+m.group(1));
                        Log.d(TAG, "handleMessage: 会员时长信息2"+m.group(2));
                        Log.d(TAG, "handleMessage: 会员时长信息3"+m.group(3));
                        //当前时间
                        vip_gray.setVisibility(View.VISIBLE);
                        if (m.group(0).contains("svip")){
                            int vip_Remaining_time = (Integer.parseInt(m.group(2)+""))*3600;
                            if (vip_Remaining_time < 3600 * 24 * 30) {
                                //vip_diamond.setVisibility(View.VISIBLE);
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_svip_diamond)
                                        .into(vip_gray);
                            } else if (vip_Remaining_time < 3600 * 24 * 180) {
                                //vip_black.setVisibility(View.VISIBLE);
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_svip_black)
                                        .into(vip_gray);
                            } else {
                                //vip_white.setVisibility(View.VISIBLE);
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_svip_white)
                                        .into(vip_gray);
                            }
                        }else {
                            int vip_Remaining_time = (Integer.parseInt(m.group(2)+""))*3600;
                            if (vip_Remaining_time < 3600 * 24 * 30) {
                                //vip_diamond.setVisibility(View.VISIBLE);
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_vip_diamond)
                                        .into(vip_gray);
                            } else if (vip_Remaining_time < 3600 * 24 * 180) {
                                //vip_black.setVisibility(View.VISIBLE);
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_vip_black)
                                        .into(vip_gray);
                            } else {
                                //vip_white.setVisibility(View.VISIBLE);
                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(R.drawable.ic_vip_white)
                                        .into(vip_gray);
                            }
                        }
                    }else {
                           vip_gray.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };
    //TODO okhttp获取用户
    public void getScore(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(getActivity().getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("userid", myid)
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

    //TODO okhttp获取用户信息
    public void sorttovip(final String url,final String allscore){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(getActivity().getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("allscore", allscore)
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
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
