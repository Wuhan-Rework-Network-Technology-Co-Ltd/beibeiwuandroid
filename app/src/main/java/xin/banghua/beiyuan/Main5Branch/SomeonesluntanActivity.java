package xin.banghua.beiyuan.Main5Branch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

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
import xin.banghua.beiyuan.Adapter.LuntanAdapter;
import xin.banghua.beiyuan.Adapter.LuntanList;
import xin.banghua.beiyuan.Main5Activity;
import xin.banghua.beiyuan.ParseJSON.ParseJSONArray;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.comment.CommentList;
import xin.banghua.beiyuan.utils.OkHttpInstance;

public class SomeonesluntanActivity extends AppCompatActivity {
    private static final String TAG = "SomeonesluntanActivity";
    //接收用户id，查看用户发过的帖子，回帖暂时不做
    //帖子列表
    private List<LuntanList> luntanLists = new ArrayList<>();
    private LuntanAdapter adapter;
    //页码
    private Integer pageindex = 1;
    private String authid;
    private CommentList commentList;

    public static CommentList selectedComment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_someonesluntan);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("个人发帖");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
//        authid = shuserinfo.readUserInfo().get("userID");
        Intent intent = getIntent();
        authid = intent.getStringExtra("authid");
        commentList = (CommentList) getIntent().getSerializableExtra("comment");
        Log.d(TAG, "onCreate: authid"+authid);

        if (commentList==null){
            getDataPostlist(getString(R.string.someonesluntan_url),authid,"1");
        }else {
            selectedComment = commentList;
            getSelectedPostlist(commentList.getPostid());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectedComment = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SomeonesluntanActivity.this, Main5Activity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPostList(JSONArray jsonArray) throws JSONException {
        Log.d(TAG, "initPostList: start");
        if (pageindex>1){
            //不是第一页，不用清零，直接更新
            if (jsonArray.length()>0){
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String[] postPicture = jsonObject.getString("postpicture").split(",");
                    LuntanList posts = new LuntanList(jsonObject.getString("age"),jsonObject.getString("gender"),jsonObject.getString("region"),
                            jsonObject.getString("property"),jsonObject.getString("id"),jsonObject.getString("plateid"),
                            jsonObject.getString("platename"),jsonObject.getString("authid"),jsonObject.getString("authnickname"),
                            jsonObject.getString("authportrait"),jsonObject.getString("posttip"),jsonObject.getString("posttitle"),
                            jsonObject.getString("posttext"),postPicture,jsonObject.getString("like"),jsonObject.getString("favorite"),
                            jsonObject.getString("time"),jsonObject.getString("vip"),jsonObject.getString("svip"),jsonObject.getString("comment_sum"));
                    posts.setComment_forbid(jsonObject.getString("comment_forbid"));
                    posts.setPostvideo(jsonObject.getString("postvideo"));
                    posts.setWidth(jsonObject.getString("width"));
                    posts.setHeight(jsonObject.getString("height"));
                    posts.setCover(jsonObject.getString("cover"));
                    posts.setOnline(jsonObject.getString("online"));
                    posts.setPortraitframe(jsonObject.getString("portraitframe"));
                    posts.setMyfriends(jsonObject.getString("myfriends"));
                    posts.setMyblacklist(jsonObject.getString("myblacklist"));
                    posts.setPlay_once(jsonObject.getString("play_once"));
                    posts.setMore_five(jsonObject.getString("more_five"));
                    posts.setPlay_completed(jsonObject.getString("play_completed"));
                    posts.setPlay_time(jsonObject.getString("play_time"));
                    posts.setPostpicture(jsonObject.getString("postpicture"));
                    posts.setVitality(jsonObject.getString("vitality"));
                    posts.setPost(jsonObject.getString("post"));
                    posts.setComment(jsonObject.getString("comment"));
                    posts.setTopic(jsonObject.getString("topic"));
                    posts.setRp_verify_time(jsonObject.getString("rp_verify_time"));
                    posts.setAll_money(jsonObject.getString("all_money"));
                    posts.setAll_income(jsonObject.getString("all_income"));
                    luntanLists.add(posts);
                }
            }
            adapter.setLuntanLists(luntanLists);
        }else {
            //不同板块，需要先清零
            luntanLists.clear();
            if (jsonArray.length()>0){
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String[] postPicture = jsonObject.getString("postpicture").split(",");
                    LuntanList posts = new LuntanList(jsonObject.getString("age"),jsonObject.getString("gender"),jsonObject.getString("region"),
                            jsonObject.getString("property"),jsonObject.getString("id"),jsonObject.getString("plateid"),
                            jsonObject.getString("platename"),jsonObject.getString("authid"),jsonObject.getString("authnickname"),
                            jsonObject.getString("authportrait"),jsonObject.getString("posttip"),jsonObject.getString("posttitle"),
                            jsonObject.getString("posttext"),postPicture,jsonObject.getString("like"),jsonObject.getString("favorite"),
                            jsonObject.getString("time"),jsonObject.getString("vip"),jsonObject.getString("svip"),jsonObject.getString("comment_sum"));
                    posts.setComment_forbid(jsonObject.getString("comment_forbid"));
                    posts.setPostvideo(jsonObject.getString("postvideo"));
                    posts.setWidth(jsonObject.getString("width"));
                    posts.setHeight(jsonObject.getString("height"));
                    posts.setCover(jsonObject.getString("cover"));
                    posts.setOnline(jsonObject.getString("online"));
                    posts.setPortraitframe(jsonObject.getString("portraitframe"));
                    posts.setMyfriends(jsonObject.getString("myfriends"));
                    posts.setMyblacklist(jsonObject.getString("myblacklist"));
                    posts.setPlay_once(jsonObject.getString("play_once"));
                    posts.setMore_five(jsonObject.getString("more_five"));
                    posts.setPlay_completed(jsonObject.getString("play_completed"));
                    posts.setPlay_time(jsonObject.getString("play_time"));
                    posts.setPostpicture(jsonObject.getString("postpicture"));
                    posts.setVitality(jsonObject.getString("vitality"));
                    posts.setPost(jsonObject.getString("post"));
                    posts.setComment(jsonObject.getString("comment"));
                    posts.setTopic(jsonObject.getString("topic"));
                    posts.setRp_verify_time(jsonObject.getString("rp_verify_time"));
                    posts.setAll_money(jsonObject.getString("all_money"));
                    posts.setAll_income(jsonObject.getString("all_income"));
                    luntanLists.add(posts);
                }
            }

            final PullLoadMoreRecyclerView recyclerView = findViewById(R.id.luntan_RecyclerView);
            adapter = new LuntanAdapter(luntanLists,this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLinearLayout();
            recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                @Override
                public void onRefresh() {
                    Log.d(TAG, "onLoadMore: start");

                    recyclerView.setPullLoadMoreCompleted();

                }

                @Override
                public void onLoadMore() {
                    pageindex = pageindex+1;
                    getDataPostlist(getString(R.string.someonesluntan_url),authid,pageindex+"");
                    Log.d(TAG, "个人帖子页码："+pageindex);
                    recyclerView.setPullLoadMoreCompleted();
                }

            });
        }







    }
    //网络数据部分
    //处理返回的数据
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是公告，2是幻灯片，3是帖子
            switch (msg.what){
                case 1:
                    try {
                        Log.d(TAG, "handleMessage: 帖子接收的值"+msg.obj.toString());
                        JSONArray jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
                        initPostList(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    public void getDataPostlist(final String url,final String authid,final String pageindex){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("authid", authid)
                        .add("pageindex",pageindex)
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

    /**
     * 获取选中的帖子
     * @param post_id
     */
    public void getSelectedPostlist(final String post_id){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("post_id", post_id)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getSelectedPost&m=socialchat")
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
