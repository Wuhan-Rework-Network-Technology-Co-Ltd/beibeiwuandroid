package xin.banghua.beiyuan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.xp.wavesidebar.WaveSideBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Adapter.FriendAdapter;
import xin.banghua.beiyuan.Adapter.FriendList;
import xin.banghua.beiyuan.AlphabeticalOrder.PinyinComparator;
import xin.banghua.beiyuan.AlphabeticalOrder.PinyinUtils;
import xin.banghua.beiyuan.AlphabeticalOrder.TitleItemDecoration;
import xin.banghua.beiyuan.Main2Branch.BlackListActivity;
import xin.banghua.beiyuan.Main2Branch.NewFriend;
import xin.banghua.beiyuan.ParseJSON.ParseJSONArray;
import xin.banghua.beiyuan.RongYunExtension.MyContactCard;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.Signin.SigninActivity;

public class Main2Activity extends AppCompatActivity implements RongIM.UserInfoProvider {
    private static final String TAG = "Main2Activity";

    Uniquelogin uniquelogin;

    private Integer pageindex = 1;

    private List<FriendList> friendList = new ArrayList<>();

    Map<String,FriendList> friendListMap = new HashMap();

    private FriendAdapter adapter;

    //未读信息监听相关
    private BottomNavigationView bottomNavigationView;
    private IUnReadMessageObserver iUnReadMessageObserver;
    private TextView unreadNumber;

    //朋友申请
    private TextView friendApply;

    //字母排序相关
    private WaveSideBar mSideBar;
    private LinearLayoutManager manager;
    private TitleItemDecoration mDecoration;
    private PullLoadMoreRecyclerView mRecyclerView;
    /**
     * 根据拼音来排列RecyclerView里面的数据类
     */
    private PinyinComparator mComparator;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_shenbian:

                    Intent intent1 = new Intent(Main2Activity.this, MainActivity.class);
                    startActivity(intent1);
                    return true;
                case R.id.navigation_haoyou:

                    //Intent intent2 = new Intent(Main2Activity.this, Main2Activity.class);
                    //startActivity(intent2);
                    return true;
                case R.id.navigation_xiaoxi:

                    Intent intent3 = new Intent(Main2Activity.this, Main3Activity.class);
                    startActivity(intent3);
                    return true;
                case R.id.navigation_dongtai:

                    Intent intent4 = new Intent(Main2Activity.this, Main4Activity.class);
                    startActivity(intent4);
                    return true;
                case R.id.navigation_wode:

                    Intent intent5 = new Intent(Main2Activity.this, Main5Activity.class);
                    startActivity(intent5);
                    return true;
            }
            return false;
        }
    };


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        if (Common.newFriendOrDeleteFriend){
            Common.newFriendOrDeleteFriend = false;
            friendList.clear();
            for (Map.Entry<String, FriendList> m : Common.friendListMap.entrySet()) {//通过entrySet
                friendList.add(m.getValue());
            }
            initRecyclerView(getWindow().getDecorView());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        Log.d(TAG, "onCreate: Main2Activity");
        //登录判断
        ifSignin();

        //设置融云当前用户信息
        SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
        String myid = shuserinfo.readUserInfo().get("userID");
        String mynickname = shuserinfo.readUserInfo().get("userNickName");
        String myportrait = shuserinfo.readUserInfo().get("userPortrait");
        UserInfo myinfo = new UserInfo(myid, mynickname, Uri.parse(myportrait));
        RongIM.getInstance().setCurrentUserInfo(myinfo);

        //底部导航初始化和配置监听，默认选项
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.navigation_haoyou);

        //未读信息监听
        iUnReadMessageObserver = new IUnReadMessageObserver() {
            @Override
            public void onCountChanged(int i) {
                BadgeBottomNav.unreadMessageBadge(bottomNavigationView, i, getApplicationContext());
                //initUnreadBadge(bottomNavigationView,i);
            }
        };
        RongIM.getInstance().addUnReadMessageCountChangedObserver(iUnReadMessageObserver, Conversation.ConversationType.PRIVATE);

        Log.d(TAG, "onCreate: Common.friendList"+Common.friendList);

        //这里用了缓存  *好友列表还是暂时别缓存了，对方同意好友没优化，更改备注也没优化
        //getDataFriends(getString(R.string.friends_url));
        if (Common.friendListMap==null) {
            getDataFriends(getString(R.string.friends_url));
        }else {
            for (Map.Entry<String, FriendList> m : Common.friendListMap.entrySet()) {//通过entrySet
                friendList.add(m.getValue());
            }
            initRecyclerView(getWindow().getDecorView());
        }
        //getDataFriends(getString(R.string.friends_url));
        //好友申请数
        BadgeBottomNav badgeBottomNav = new BadgeBottomNav(this, handler);
        badgeBottomNav.getDataFriendsapply(getString(R.string.friendsapplynumber_url));


        Button newFriend = findViewById(R.id.new_friend);
        newFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, NewFriend.class);
                startActivity(intent);
            }
        });
        Button blacklist_friends = findViewById(R.id.blacklist_friends);
        blacklist_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, BlackListActivity.class);
                startActivity(intent);
            }
        });

        SearchView searchView = findViewById(R.id.friend_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });


        RongIM.setUserInfoProvider(this, true);


    }

    public void ifSignin() {
        Map<String, String> userInfo;
        SharedHelper sh;
        sh = new SharedHelper(getApplicationContext());
        userInfo = sh.readUserInfo();
        //Toast.makeText(mContext, userInfo.toString(), Toast.LENGTH_SHORT).show();
        if (userInfo.get("userID") == "") {
            Intent intentSignin = new Intent(Main2Activity.this, SigninActivity.class);
            startActivity(intentSignin);
        } else {
            //唯一登录验证
            uniquelogin = new Uniquelogin(this, handler);
            uniquelogin.compareUniqueLoginToken(getString(R.string.uniquelogin_url));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RongIM.getInstance().removeUnReadMessageCountChangedObserver(iUnReadMessageObserver);
    }

    public void initUnreadBadge(BottomNavigationView navigation, Integer integer) {
        //底部导航栏角标
        //获取整个的NavigationView
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        //这里就是获取所添加的每一个Tab(或者叫menu)，
        View tab = menuView.getChildAt(1);
        BottomNavigationItemView itemView = (BottomNavigationItemView) tab;
        //加载我们的角标View，新创建的一个布局
        View badge = LayoutInflater.from(this).inflate(R.layout.badge_unreadmessage, menuView, false);
        //添加到Tab上
        itemView.addView(badge);
        unreadNumber = badge.findViewById(R.id.badge_text);
        unreadNumber.setText("");
        unreadNumber.setText(String.valueOf(integer));
        //无消息时可以将它隐藏即可
        if (integer > 0) {
            unreadNumber.setVisibility(View.VISIBLE);
        } else {
            unreadNumber.setVisibility(View.GONE);
        }
    }

    //TODO 初始化用户列表
    private void initFriends(View view, JSONArray jsonArray) throws JSONException {
        Log.d(TAG, "initFriends: ");
        List<UserInfo> userInfoList = new ArrayList<>();
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                //替换备注
                String nickname = jsonObject.getString("nickname");
                if (Common.friendsRemarkMap!=null){
                    for (Map.Entry<String, String> m : Common.friendsRemarkMap.entrySet()) {//通过entrySet
                        System.out.println("key:" + m.getKey() + " value:" + m.getValue());
                        if (m.getKey().equals(jsonObject.getString("id"))){
                            nickname = m.getValue();
                        }
                    }
                }
                //好友信息
                FriendList friends = new FriendList(jsonObject.getString("id"), jsonObject.getString("portrait"), nickname, jsonObject.getString("age"), jsonObject.getString("gender"), jsonObject.getString("region"), jsonObject.getString("property"), jsonObject.getString("vip"),jsonObject.getString("svip"));
                friendList.add(filledData(friends));
                friendListMap.put(jsonObject.getString("id"),filledData(friends));

                //融云个人信息
                UserInfo userInfo = new UserInfo(jsonObject.getString("id"), nickname, Uri.parse(jsonObject.getString("portrait")));
                RongIM.getInstance().refreshUserInfoCache(userInfo);
                userInfoList.add(userInfo);
            }
            MyContactCard myContactCard = new MyContactCard();
            myContactCard.setUserInfoList(userInfoList);
            myContactCard.initContactCard();
        }
        Common.friendList = friendList;
        Common.friendListMap = friendListMap;
        if (pageindex>1){//第二页以上，只加载刷新，不新建recyclerView
            adapter.swapData(friendList);//重新赋值并调用notifyDataSetChanged();
        }else {//初次加载
            initRecyclerView(view);
        }
    }

    /**
     * 将数据中的用户名拼音首字母加入数据数组
     *
     * @param data
     * @return
     */
    public static FriendList filledData(FriendList data) {
        //汉字转换成拼音   表情会报StringIndexOutOfBoundsException
        String pinyin = PinyinUtils.getPingYin(data.getmUserNickName());
        String sortString = "#";
        try {
            sortString = pinyin.substring(0, 1).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            data.setLetters(sortString.toUpperCase());
        } else {
            data.setLetters("#");
        }
        return data;
    }

    //TODO 初始化好友recyclerview
    private void initRecyclerView(View view) {
        Log.d(TAG, "initRecyclerView: init recyclerview");
        mComparator = new PinyinComparator();

        mSideBar = (WaveSideBar) findViewById(R.id.sideBar);

        //设置右侧SideBar触摸监听
        mSideBar.setOnTouchLetterChangeListener(new WaveSideBar.OnTouchLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(letter.charAt(0));
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0);
                }
            }
        });
        // 根据a-z进行排序源数据
        Collections.sort(friendList, mComparator);

        mRecyclerView = view.findViewById(R.id.haoyou_RecyclerView);
        adapter = new FriendAdapter(Main2Activity.this, friendList);
        mRecyclerView.setAdapter(adapter);
        //RecyclerView设置manager
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.getRecyclerView().setLayoutManager(manager);
        mRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.postDelayed(() -> mRecyclerView.setPullLoadMoreCompleted(), 1000);
                Log.d(TAG, "onRefresh: start");
            }

            @Override
            public void onLoadMore() {

//                pageindex = pageindex+1;//页数加一
//                getDataFriends(getString(R.string.friends_url));//重新加载
//
//                Log.d(TAG, "onLoadMore: start");
                mRecyclerView.postDelayed(() -> mRecyclerView.setPullLoadMoreCompleted(), 1000);
            }
        });
        mDecoration = new TitleItemDecoration(this, friendList);
        //如果add两个，那么按照先后顺序，依次渲染。
        mRecyclerView.addItemDecoration(mDecoration);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(Main2Activity.this, DividerItemDecoration.VERTICAL));
    }

    //处理返回的数据
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是用户数据，2是幻灯片
            switch (msg.what) {
                case 1:
                    try {
                        Log.d(TAG, "handleMessage: 用户数据接收的值case 1" + msg.obj.toString());

                        JSONArray jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
                        initFriends(getWindow().getDecorView(), jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 11:
                    Log.d(TAG, "handleMessage: 用户数据接收的值" + msg.obj.toString());
                    friendApply = findViewById(R.id.new_friend);
                    friendApply.setText("+新朋友 " + msg.obj.toString());
                    SharedHelper shvalue = new SharedHelper(getApplicationContext());
                    String newFriendApplyNumber = shvalue.readNewFriendApplyNumber();
                    if (newFriendApplyNumber.equals(msg.obj.toString())) {
                        friendApply.setText("+新朋友 ");
                        //BadgeBottomNav.newFriendApplicationBadge(bottomNavigationView,msg.obj.toString(),getApplicationContext());
                    } else {
                        friendApply.setText("+新朋友 " + msg.obj.toString());
                        BadgeBottomNav.newFriendApplicationBadge(bottomNavigationView, msg.obj.toString(), getApplicationContext());
                    }
                    break;
                case 10:
                    if (msg.obj.toString().equals("false")) {
                        uniquelogin.uniqueNotification();
                        SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("userID", "");
                        editor.commit();
                        Intent intent = new Intent(Main2Activity.this, SigninActivity.class);
                        intent.putExtra("uniquelogin", "强制退出");
                        startActivity(intent);
                    }
                    break;
            }
        }
    };

    //TODO okhttp获取好友信息
    public void getDataFriends(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedHelper shvalue = new SharedHelper(getApplicationContext());
                String userID = shvalue.readUserInfo().get("userID");
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", userID)
                        .add("pageindex", pageindex+"")
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Message message = handler.obtainMessage();
                    message.obj = response.body().string();
                    message.what = 1;
                    Log.d(TAG, "run: Userinfo发送的值" + message.obj.toString());
                    handler.sendMessageDelayed(message, 10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public UserInfo getUserInfo(String s) {
        //自己的头像也是在列表中选取，所以需要加入自己的信息。。。有自己好友，应该设置当前用户信息
//        SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
//        String myid = shuserinfo.readUserInfo().get("userID");
//        String mynickname = shuserinfo.readUserInfo().get("userNickName");
//        String myportrait = shuserinfo.readUserInfo().get("userPortrait");
//        FriendList me = new FriendList(myid,myportrait,mynickname);
//        friendList.add(me);
        //获取用户信息
        for (FriendList i : friendList) {
            if (i.getmUserID().equals(s)) {
                Log.d(TAG, "getUserInfo: 进入" + s);
                return new UserInfo(i.getmUserID(), i.getmUserNickName(), Uri.parse(i.getmUserPortrait()));
            }
        }
        Log.d(TAG, "getUserInfo: 没进入" + s);
        return null;
    }

}
