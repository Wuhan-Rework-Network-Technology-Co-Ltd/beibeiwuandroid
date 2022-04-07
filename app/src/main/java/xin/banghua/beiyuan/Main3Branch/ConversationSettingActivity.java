package xin.banghua.beiyuan.Main3Branch;

import static io.rong.imlib.model.Conversation.ConversationNotificationStatus.DO_NOT_DISTURB;
import static io.rong.imlib.model.Conversation.ConversationNotificationStatus.NOTIFY;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.orhanobut.dialogplus.DialogPlus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.chatroom.util.ReportDialog;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.Personage.SetRemarkAndTagActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

public class ConversationSettingActivity extends AppCompatActivity {
    private static final String TAG = "ConversationSettingActi";


    CircleImageView portrait;
    TextView nickname;
    Switch istop,donotdisturb;
    Button recored_clear,blacklist_btn,deletefriend_btn,remark_tag_btn,report_btn;



    String targetId;
    String title;


    private final static int REMARK_TAG_SETTING = 2413;

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_setting);

        Log.d(TAG, "onCreate: ConversationSettingActivity");

        Intent intent = getIntent();
        targetId = intent.getStringExtra("targetId");
        title = intent.getStringExtra("title");

        xin.banghua.beiyuan.Common.conversationSettingUserId = targetId;
        xin.banghua.beiyuan.Common.conversationSettingUserName = title;


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);


        report_btn = findViewById(R.id.report_btn);
        report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportDialog reportDialog = new ReportDialog(ConversationSettingActivity.this);
                reportDialog.show(ReportDialog.REPORT_TYPE_1,targetId);
            }
        });

        portrait = findViewById(R.id.portrait);
        portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PersonageActivity.class);
                intent.putExtra("userID",targetId);
                Log.d(TAG, "onClick: 跳转个人页面");
                v.getContext().startActivity(intent);
            }
        });
        nickname = findViewById(R.id.nickname);
        nickname.setText(title);
        RongIM.getInstance().getConversation(Conversation.ConversationType.PRIVATE, targetId, new RongIMClient.ResultCallback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                Log.d(TAG, "onSuccess: 获取会话成功");
                if (conversation == null){
                    Log.d(TAG, "onSuccess: 会话空");
                }else {
                    Log.d(TAG, "onSuccess: 会话不空");
                    if (conversation.isTop()){
                        istop.setChecked(true);
                    }else {
                        istop.setChecked(false);
                    }
                    if (conversation.getNotificationStatus()==DO_NOT_DISTURB){
                        donotdisturb.setChecked(true);
                    }else {
                        donotdisturb.setChecked(false);
                    }
                }

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.d(TAG, "onSuccess: 获取会话失败");
            }
        });
        istop = findViewById(R.id.istop);
        istop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    RongIM.getInstance().setConversationToTop(Conversation.ConversationType.PRIVATE, targetId, true, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            Toast.makeText(getApplicationContext(),"已开启置顶",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }else {
                    RongIM.getInstance().setConversationToTop(Conversation.ConversationType.PRIVATE, targetId, false, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            Toast.makeText(getApplicationContext(),"已关闭置顶",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }
            }
        });
        donotdisturb = findViewById(R.id.donot_disturb);
        donotdisturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    RongIM.getInstance().setConversationNotificationStatus(Conversation.ConversationType.PRIVATE, targetId, DO_NOT_DISTURB, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                        @Override
                        public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                            Toast.makeText(getApplicationContext(),"已开启免打扰",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }else {
                    RongIM.getInstance().setConversationNotificationStatus(Conversation.ConversationType.PRIVATE, targetId, NOTIFY, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                        @Override
                        public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                            Toast.makeText(getApplicationContext(),"已关闭免打扰",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }
            }
        });
        recored_clear = findViewById(R.id.record_clear);
        recored_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialog = DialogPlus.newDialog(ConversationSettingActivity.this)
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
                prompt.setText("确定要删除会话记录吗？");
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
                        RongIM.getInstance().deleteMessages(Conversation.ConversationType.PRIVATE, targetId, new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                Toast.makeText(getApplicationContext(),"已清除会话消息",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });
                        dialog.dismiss();
                    }
                });

            }
        });
        blacklist_btn = findViewById(R.id.blacklist_btn);
        blacklist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialog = DialogPlus.newDialog(ConversationSettingActivity.this)
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
                        addBlacklist("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=addblacklist&m=socialchat",targetId);
                        dialog.dismiss();
                    }
                });

            }
        });
        deletefriend_btn = findViewById(R.id.deletefriend_btn);
        deletefriend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialog = DialogPlus.newDialog(ConversationSettingActivity.this)
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
                        RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE,targetId,new io.rong.imlib.RongIMClient.ResultCallback(){

                            @Override
                            public void onSuccess(Object o) {

                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });
                        deleteFriend(getString(R.string.deletefriendnew_url),targetId);
                        dialog.dismiss();
                    }
                });

            }
        });


        remark_tag_btn = findViewById(R.id.remark_tag_btn);
        remark_tag_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConversationSettingActivity.this, SetRemarkAndTagActivity.class);
                startActivityForResult(intent, REMARK_TAG_SETTING);
            }
        });

        getDataPersonage(getString(R.string.personage_url),targetId);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: 修改备注后改名");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REMARK_TAG_SETTING) {
            title = xin.banghua.beiyuan.Common.conversationSettingUserName;
            nickname.setText(title);
            getSupportActionBar().setTitle(title);
        }
    }

    public void initPersonage(JSONObject jsonObject) throws JSONException {
        Glide.with(this)
                .asBitmap()
                .load(Common.getOssResourceUrl(jsonObject.getString("portrait")))
                .into(portrait);

        xin.banghua.beiyuan.Common.conversationSettingUserPortrait = jsonObject.getString("portrait");
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
                        Log.d(TAG, "handleMessage: 用户数据接收的值"+msg.obj.toString());

                        JSONObject jsonObject = new ParseJSONObject(msg.obj.toString()).getParseJSON();
                        initPersonage(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(),"已加入黑名单",Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(),"已删除好友",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    //TODO okhttp获取用户信息
    public void getDataPersonage(final String url,final String targetId){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("userid", targetId)
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

    //TODO okhttp加入黑名单
    public void addBlacklist(final String url,final String targetId){
        //Toast.makeText(mContext, "已加入黑名单", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("yourid", targetId)
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
    //TODO okhttp删除好友
    public void deleteFriend(final String url,final String targetId){
        //Toast.makeText(mContext, "已加入黑名单", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("yourid", targetId)
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
    @Override  //菜单的点击，其中返回键的id是android.R.id.home
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
