package xin.banghua.beiyuan.Main4Branch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import xin.banghua.beiyuan.Adapter.PostAdapter;
import xin.banghua.beiyuan.Adapter.PostHead;
import xin.banghua.beiyuan.ParseJSON.ParseJSONArray;
import xin.banghua.beiyuan.R;

public class PostListActivity extends AppCompatActivity {
    private static final String TAG = "PostListActivity";

    List<PostHead> postHeads = new ArrayList<>();

    PostAdapter adapter;


    String id;
    String plateid;
    String platename;
    String authid;
    String authnickname;
    String authportrait;
    String posttip;
    String posttitle;
    String posttext;
    String[] postpicture;
    String like;
    String favorite;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        id = getIntent().getStringExtra("postid");
        plateid = getIntent().getStringExtra("plateid");
        platename = getIntent().getStringExtra("platename");
        authid = getIntent().getStringExtra("authid");
        authnickname = getIntent().getStringExtra("authnickname");
        authportrait = getIntent().getStringExtra("authportrait");
        posttip = getIntent().getStringExtra("posttip");
        posttitle = getIntent().getStringExtra("posttitle");
        posttext = getIntent().getStringExtra("posttext");
        postpicture = getIntent().getStringArrayExtra("postpicture");
        like = getIntent().getStringExtra("like");
        favorite = getIntent().getStringExtra("favorite");
        time = getIntent().getStringExtra("time");

        ImageView back_btn = findViewById(R.id.iv_back_left);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView follow_fabu = findViewById(R.id.follow_fabu);
        follow_fabu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostListActivity.this,FabugentieActivity.class);
                intent.putExtra("postid",id);
                intent.putExtra("plateid",plateid);
                intent.putExtra("platename",platename);
                intent.putExtra("authid",authid);
                intent.putExtra("authnickname",authnickname);
                intent.putExtra("authportrait",authportrait);
                intent.putExtra("posttip",posttip);
                intent.putExtra("posttitle",posttitle);
                intent.putExtra("posttext",posttext);
                intent.putExtra("postpicture",postpicture);
                intent.putExtra("like",like);
                intent.putExtra("favorite",favorite);
                intent.putExtra("time",time);
                startActivity(intent);
            }
        });

        //getDataPosthead("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=postdetail&m=socialchat");
        initPostHead();
    }

    public void initPostHead(){
        //初始化数据
                String postPicture1 = "";
                String postPicture2 = "";

                String postPicture3 = "";
                switch (postpicture.length){
                    case 1:
                        postPicture1 = postpicture[0];
                        break;
                    case 2:
                        postPicture1 = postpicture[0];
                        postPicture2 = postpicture[1];
                        break;
                    case 3:
                        postPicture1 = postpicture[0];
                        postPicture2 = postpicture[1];
                        postPicture3 = postpicture[2];
                        break;
                }
                PostHead posts = new PostHead(posttitle,authid,authnickname,authportrait,posttext,postPicture1,postPicture2,postPicture3,time);
                postHeads.add(posts);

        getDataFollowlist("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=postdetail&m=socialchat");

    }

    public void initPostList(JSONArray jsonArray) throws JSONException {
        //初始化数据
        if (jsonArray.length()>0){
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String[] followPicture = jsonObject.getString("followpicture").split(",");
                String followPicture1 = "";
                String followPicture2 = "";
                String followPicture3 = "";
                switch (followPicture.length){
                    case 1:
                        followPicture1 = followPicture[0];
                        break;
                    case 2:
                        followPicture1 = followPicture[0];
                        followPicture2 = followPicture[1];
                        break;
                    case 3:
                        followPicture1 = followPicture[0];
                        followPicture2 = followPicture[1];
                        followPicture3 = followPicture[2];
                        break;
                }
                PostHead posts = new PostHead(jsonObject.getString("authid"),jsonObject.getString("authnickname"),jsonObject.getString("authportrait"),jsonObject.getString("followtext"),followPicture1,followPicture2,followPicture3,jsonObject.getString("time"));
                postHeads.add(posts);
            }
        }

        //初始化recyclerview
        final PullLoadMoreRecyclerView recyclerView = findViewById(R.id.postlist_RecyclerView);
        adapter = new PostAdapter(this,postHeads);
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
                Log.d(TAG, "onLoadMore: start");
                recyclerView.setPullLoadMoreCompleted();
            }
        });
    }
    //网络数据部分
    //处理返回的数据
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是帖子，2是跟帖
            switch (msg.what){
                case 1:
//                    try {
//                        Log.d(TAG, "handleMessage: 帖子接收的值"+msg.obj.toString());
//                        JSONArray jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
//                        initPostHead(jsonArray);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    break;
                case 2:
                    try {
                        Log.d(TAG, "handleMessage: 跟帖接收的值"+msg.obj.toString());
                        JSONArray jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
                        initPostList(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    //TODO okhttp获取帖子信息  1.帖子，2.跟帖
//    public void getDataPosthead(final String url){
//        new Thread(new Runnable() {
//            @Override
//            public void run(){
//                OkHttpClient client = new OkHttpClient();
//                RequestBody formBody = new FormBody.Builder()
//                        .add("type", "getDataPosthead")
//                        .add("postid",id)
//                        .build();
//                Request request = new Request.Builder()
//                        .url(url)
//                        .post(formBody)
//                        .build();
//
//                try (Response response = client.newCall(request).execute()) {
//                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//
//                    Message message=handler.obtainMessage();
//                    message.obj=response.body().string();
//                    message.what=1;
//                    Log.d(TAG, "run: 帖子发送的值"+message.obj.toString());
//                    handler.sendMessageDelayed(message,10);
//                }catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
    public void getDataFollowlist(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("type", "getDataFollowlist")
                        .add("postid",id)
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
                    Log.d(TAG, "run: 跟帖发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
