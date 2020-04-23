package xin.banghua.beiyuan.Personage;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orhanobut.dialogplus.DialogPlus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.CircleImageViewExtension;
import xin.banghua.beiyuan.Main5Branch.SomeonesluntanActivity;
import xin.banghua.beiyuan.MainActivity;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonageFragment extends Fragment {
    private static final String TAG = "PersonageFragment";

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
    private Button user_tiezi;

    private Button balcklist_btn;
    private Button deletefriend_btn;
    private Button startconversation_btn;

    private Context mContext;

    ImageView vip_gray;
    ImageView vip_diamond;
    ImageView vip_black;
    ImageView vip_white;
    Integer current_timestamp = Math.round(new Date().getTime()/1000);

    public PersonageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_personage, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: 进入person");
        //取出选中的用户id
        //SharedHelper shvalue = new SharedHelper(getActivity().getApplicationContext());
        //mUserID = shvalue.readValue().get("value");

        String result = getActivity().getIntent().getStringExtra("userID");
        mUserID = result;

        vip_gray = view.findViewById(R.id.vip_gray);
        vip_diamond = view.findViewById(R.id.vip_diamond);
        vip_black = view.findViewById(R.id.vip_black);
        vip_white = view.findViewById(R.id.vip_white);

        mUserNickName_tv=view.findViewById(R.id.user_nickname);
        mUserPortrait_iv=view.findViewById(R.id.user_portrait);
        mUserAge_tv=view.findViewById(R.id.user_age);
        mUserRegion_tv=view.findViewById(R.id.user_region);
        mUserGender_tv=view.findViewById(R.id.user_gender);
        mUserProperty_tv=view.findViewById(R.id.user_property);
        mUserSignature_tv=view.findViewById(R.id.user_signature);
        mLeaveWords_et=view.findViewById(R.id.leave_words);
        user_tiezi=view.findViewById(R.id.user_tiezi);
        user_tiezi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Someonesluntan1Activity.class);
                intent.putExtra("authid",mUserID);
                startActivity(intent);
            }
        });
        startconversation_btn = view.findViewById(R.id.start_conversation);
        startconversation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动会话界面
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat(mContext, mUserID, mUserNickName_tv.getText().toString());
                }
            }
        });
        make_friend = view.findViewById(R.id.make_friend);
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
                        getFriendNumber("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=friendsnumber&m=socialchat");
                        dialog.dismiss();
                    }
                });
            }
        });

        balcklist_btn = view.findViewById(R.id.add_blacklist);
        balcklist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (balcklist_btn.getText().toString().equals("加入黑名单")) {
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
                            addBlacklist("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=addblacklist&m=socialchat");
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
                    TextView prompt = view.findViewById(R.id.prompt_tv);
                    prompt.setText("确定要移除黑名单吗？");
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
                            deleteBlackList("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=deleteblacklist&m=socialchat");
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        deletefriend_btn = view.findViewById(R.id.delete_friend);
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
                TextView prompt = view.findViewById(R.id.prompt_tv);
                prompt.setText("确定要删除好友吗？");
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
                        RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE,mUserID,new io.rong.imlib.RongIMClient.ResultCallback(){

                            @Override
                            public void onSuccess(Object o) {

                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });
                        deleteFriend("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=deletefriend&m=socialchat");
                        dialog.dismiss();
                    }
                });

            }
        });


        getDataPersonage(getString(R.string.personage_url));

        addSawMe("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=addsawme&m=socialchat");

        ifFriend("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=iffriend&m=socialchat");
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
                SharedHelper shuserinfo = new SharedHelper(getActivity());
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
    public void initPersonage(View view,JSONObject jsonObject) throws JSONException {

        mUserNickName=jsonObject.getString("nickname");
        mUserPortrait=jsonObject.getString("portrait");


        mUserNickName_tv.setText(jsonObject.getString("nickname"));
        //if (jsonObject.getString("vip").equals("VIP"))  mUserPortrait_iv.isVIP(true,getResources(),false);
        Glide.with(view)
                .asBitmap()
                .load(jsonObject.getString("portrait"))
                .into(mUserPortrait_iv);
        int vip_time = Integer.parseInt(jsonObject.getString("vip")+"");
        if (vip_time > current_timestamp) {
            //vipicon分级
            Log.d("会员时长", ((vip_time - current_timestamp) + ""));
            if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                vip_diamond.setVisibility(View.VISIBLE);
            } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                vip_black.setVisibility(View.VISIBLE);
            } else {
                vip_white.setVisibility(View.VISIBLE);
            }
        } else {
            vip_gray.setVisibility(View.VISIBLE);
        }



        mUserAge_tv.setText(jsonObject.getString("age"));
        mUserRegion_tv.setText(jsonObject.getString("region"));
        mUserProperty_tv.setText(jsonObject.getString("property"));
        mUserGender_tv.setText(jsonObject.getString("gender"));
        if (jsonObject.getString("gender").equals("男")){
            Resources resources = mContext.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.male,null);
            mUserGender_tv.setForeground(drawable);
        }else {
            Resources resources = mContext.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.female,null);
            mUserGender_tv.setForeground(drawable);
        }
        mUserSignature_tv.setText(jsonObject.getString("signature"));
        if (jsonObject.getString("allowfriend").equals("0")){
            make_friend.setEnabled(false);
            make_friend.setText("拒绝添加好友");
        }

    }
    //网络数据部分
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是用户数据，2是幻灯片
            switch (msg.what){
                case 1:
                    try {
                        String resultJson1 = msg.obj.toString();
                        Log.d(TAG, "handleMessage: 用户数据接收的值"+msg.obj.toString());

                        JSONObject jsonObject = new ParseJSONObject(msg.obj.toString()).getParseJSON();
                        initPersonage(mView,jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    make_friend.setEnabled(false);
                    make_friend.setText("已申请，等待对方同意");
                    Toast.makeText(mContext,msg.obj.toString(),Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(mContext, MainActivity.class);
//                    startActivity(intent);
                    break;
                case 3:
                    balcklist_btn.setText("移除黑名单");
                    Toast.makeText(mContext,"已加入黑名单",Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Log.d(TAG, "handleMessage: 进入好友判断");
                    if (msg.obj.toString().equals("好友人数未超过限制")){
                        Log.d(TAG, "handleMessage: 跳转添加好友");
                        makeFriend("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=addfriend&m=socialchat");
                    }else {
                        Log.d(TAG, "handleMessage: 会员数量满");
                        Toast.makeText(mContext,msg.obj.toString(),Toast.LENGTH_LONG).show();
                    }
                    break;
                case 5:
                    JSONObject jsonObject = null;//原生的
                    try {
                        jsonObject = new JSONObject(msg.obj.toString());
                        String blacklistinfo = jsonObject.getString("blacklistinfo");
                        String friendinfo = jsonObject.getString("friendinfo");
                        if (blacklistinfo.equals("已加入黑名单")){
                            balcklist_btn.setText("移除黑名单");
                            Toast.makeText(mContext,blacklistinfo,Toast.LENGTH_LONG).show();
                        }
                        if (friendinfo.equals("已经是好友")){
                            make_friend.setVisibility(View.GONE);
                            deletefriend_btn.setVisibility(View.VISIBLE);
                            startconversation_btn.setVisibility(View.VISIBLE);
                            Toast.makeText(mContext,friendinfo,Toast.LENGTH_LONG).show();
                        }else if (friendinfo.equals("已申请，等待对方同意")){
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
                    balcklist_btn.setText("加入黑名单");
                    Toast.makeText(mContext,"已移除黑名单",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
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
    public void makeFriend(final String url){
        //Toast.makeText(mContext, "申请成功", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(getActivity().getApplicationContext());
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
                SharedHelper shuserinfo = new SharedHelper(getActivity().getApplicationContext());
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
                SharedHelper shuserinfo = new SharedHelper(getActivity().getApplicationContext());
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
                SharedHelper shuserinfo = new SharedHelper(getActivity().getApplicationContext());
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
                SharedHelper shuserinfo = new SharedHelper(getActivity().getApplicationContext());
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
