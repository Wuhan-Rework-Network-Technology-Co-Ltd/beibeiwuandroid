package xin.banghua.beiyuan.Main2Branch;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Adapter.BlackListAdapter;
import xin.banghua.beiyuan.Adapter.FriendList;
import xin.banghua.beiyuan.ParseJSON.ParseJSONArray;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

public class BlackListActivity extends AppCompatActivity {

    private static final String TAG = "BlackListActivity";

    private Integer pageindex = 1;

    private List<FriendList> friendList = new ArrayList<>();

    private BlackListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("黑名单");

        SearchView searchView = findViewById(R.id.friend_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    adapter.getFilter().filter(newText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        getBlackList(getString(R.string.blacklist_url));
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



    //TODO 初始化用户列表
    private void initBlackList(View view, JSONArray jsonArray) throws JSONException {
        Log.d(TAG, "initFriends: ");

        if (jsonArray.length()>0){
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                FriendList friends = new FriendList(jsonObject.getString("id"),jsonObject.getString("portrait"),jsonObject.getString("nickname"),jsonObject.getString("age"),jsonObject.getString("gender"),jsonObject.getString("region"),jsonObject.getString("property"),jsonObject.getString("vip"),jsonObject.getString("svip"));
                friendList.add(friends);
            }
        }

        if (pageindex>1){//第二页以上，只加载刷新，不新建recyclerView
            adapter.swapData(friendList);//重新赋值并调用notifyDataSetChanged();
        }else {//初次加载
            initRecyclerView(view);
        }
    }

    //TODO 初始化好友recyclerview
    private void initRecyclerView(View view){
        Log.d(TAG, "initRecyclerView: init recyclerview");

        final PullLoadMoreRecyclerView recyclerView = view.findViewById(R.id.blacklist_RecyclerView);
        adapter = new BlackListAdapter(BlackListActivity.this,friendList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLinearLayout();
        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

                Log.d(TAG, "onRefresh: start");
                recyclerView.setPullLoadMoreCompleted();
            }

            @Override
            public void onLoadMore() {

                pageindex = pageindex+1;//页数加一
                getBlackList(getString(R.string.blacklist_url));//重新加载

                Log.d(TAG, "onLoadMore: start");
                recyclerView.setPullLoadMoreCompleted();
            }
        });
    }
    //处理返回的数据
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

                        JSONArray jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
                        initBlackList(getWindow().getDecorView(),jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    //TODO okhttp获取黑名单信息
    public void getBlackList(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shvalue = new SharedHelper(getApplicationContext());
                String myid = shvalue.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("pageindex", pageindex+"")
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
