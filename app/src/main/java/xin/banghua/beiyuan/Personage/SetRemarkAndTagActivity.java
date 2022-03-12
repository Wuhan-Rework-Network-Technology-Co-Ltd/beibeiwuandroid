package xin.banghua.beiyuan.Personage;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import co.lujun.androidtagview.TagContainerLayout;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Adapter.FriendList;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.Common;

public class SetRemarkAndTagActivity extends AppCompatActivity {
    private static final String TAG = "SetRemarkAndTagActivity";

    TagContainerLayout tagcontainerLayout;
    TextView add_tag_tv;
    EditText remark_et;

    LinearLayout tag_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_remark_and_tag);

        initToolbar();
        initView();
    }


    //初始化工具栏
    public void initToolbar(){
        //工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle("备注与标签设置");
        toolbar.inflateMenu(R.menu.menu_toolbar_submit);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)   //android api 2.4   version7.0
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.submit_toolbar){
                    Log.d(TAG, "提交了");

                    setFriendsRemark(getString(R.string.setfriendsremark_url));

                    //同步好友备注  map的replace需要7.0
                    if (xin.banghua.beiyuan.Common.friendsRemarkMap!=null) {
                        xin.banghua.beiyuan.Common.friendsRemarkMap.replace(xin.banghua.beiyuan.Common.conversationSettingUserId, xin.banghua.beiyuan.Common.conversationSettingUserName, remark_et.getText().toString());
                    }
                    //同步好友List
                    if (xin.banghua.beiyuan.Common.friendListMap!=null) {
                        FriendList currentFriend = xin.banghua.beiyuan.Common.friendListMap.get(xin.banghua.beiyuan.Common.conversationSettingUserId);
                        if (currentFriend!=null){
                            currentFriend.setmUserNickName(remark_et.getText().toString());
                        }
                        xin.banghua.beiyuan.Common.friendListMap.replace(xin.banghua.beiyuan.Common.conversationSettingUserId, currentFriend);
                    }

                    //融云个人信息
                    UserInfo userInfo = new UserInfo(xin.banghua.beiyuan.Common.conversationSettingUserId, remark_et.getText().toString(), Uri.parse(Common.getOssResourceUrl(xin.banghua.beiyuan.Common.conversationSettingUserPortrait)));
                    RongIM.getInstance().refreshUserInfoCache(userInfo);

                    //缓存也改了
                    xin.banghua.beiyuan.Common.conversationSettingUserName = remark_et.getText().toString();

                    finish();
                }
                return true;
            }
        });
    }


    private void initView(){
        remark_et = findViewById(R.id.remark_et);
        remark_et.setText(xin.banghua.beiyuan.Common.conversationSettingUserName);

        tagcontainerLayout = findViewById(R.id.tagcontainerLayout);

        tagcontainerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(SetRemarkAndTagActivity.this, "功能开发中，敬请期待", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
//                Intent intent = new Intent(SetRemarkAndTagActivity.this, SetFriendsTagActivity.class);
//                startActivity(intent);
            }
        });


        add_tag_tv = findViewById(R.id.add_tag_tv);

        add_tag_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: add_tag_tv");
                Toast toast = Toast.makeText(SetRemarkAndTagActivity.this, "功能开发中，敬请期待", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
//                Intent intent = new Intent(SetRemarkAndTagActivity.this, SetFriendsTagActivity.class);
//                startActivity(intent);

            }
        });

        tag_layout = findViewById(R.id.tag_layout);

        tag_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: add_tag_tv");
                Intent intent = new Intent(SetRemarkAndTagActivity.this, SetFriendsTagActivity.class);
                startActivity(intent);
            }
        });
    }


    //TODO okhttp获取用户信息
    public void setFriendsRemark(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(SetRemarkAndTagActivity.this);
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("friendid", xin.banghua.beiyuan.Common.conversationSettingUserId)
                        .add("newRemark", remark_et.getText().toString())
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //网络数据部分
    //处理返回的数据
//    @SuppressLint("HandlerLeak")
//    private Handler handler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            //1是用户数据，2是幻灯片
//            switch (msg.what){
//                case 1:
//
//                    break;
//            }
//        }
//    };

}