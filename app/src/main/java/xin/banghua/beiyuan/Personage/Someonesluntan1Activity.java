package xin.banghua.beiyuan.Personage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import xin.banghua.beiyuan.ParseJSON.ParseJSONArray;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.custom_ui.CustomVideoView;
import xin.banghua.beiyuan.utils.ScreenUtils;

public class Someonesluntan1Activity extends AppCompatActivity {
    private static final String TAG = "SomeonesluntanActivity";
    //接收用户id，查看用户发过的帖子，回帖暂时不做
    //帖子列表
    private List<LuntanList> luntanLists = new ArrayList<>();
    private LuntanAdapter adapter;
    //页码
    private Integer pageindex = 1;
    private String authid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_someonesluntan);

        recyclerView = findViewById(R.id.luntan_RecyclerView);

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
        Log.d(TAG, "onCreate: authid"+authid);
        getDataPostlist(getString(R.string.someonesluntan_url),authid,"1");

    }

    @Override
    protected void onStop() {
        super.onStop();
        CustomVideoView.getInstance(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CustomVideoView.getInstance(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //用户手动点击播放后，自动播放开始，
    // 除非用户手动点击停止，或者视频播放完毕，停止自动播放，
    private boolean isLooper = true;
    private int looperFlag = 1;//0,无自动播放，1.自动播放上一个，2自动播放下一个
    private int position_play = -1;//播放的位置
    int currentRecyclerPosition = 0;

    PullLoadMoreRecyclerView recyclerView;
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
                    luntanLists.add(posts);
                }
                adapter.swapData(luntanLists);
            }
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
                    luntanLists.add(posts);
                }
            }


            adapter = new LuntanAdapter(luntanLists,this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLinearLayout();
            recyclerView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Log.d(TAG, "onScrollStateChanged: "+newState);
                    //滑动停止后，
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && isLooper && looperFlag != 0) {
                        Log.d(TAG, "onScrollStateChanged: 停止滚蛋");
                        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                        switch (looperFlag) {
                            case 1:
                                int position_lastVisible=layoutManager.findLastVisibleItemPosition();
                                if (position_lastVisible==position_play){
                                    //自动播放上一个
                                    position_play-=1;
                                }else {
                                    //最后一个可见的item和滑出去的上次播放的view隔了N(N>=1)个Item,所以自动播放倒数第2个可见的item
                                    position_play=position_lastVisible-1;
                                }

                                break;
                            case 2:
                                int position_firstVisible=layoutManager.findFirstVisibleItemPosition();
                                if (position_firstVisible==position_play){
                                    //自动播放下一个

                                    position_play+=1;
                                }else {
                                    //第一个可见的item和滑出去的上次播放的view隔了N(N>=1)个Item,所以自动播放第2个可见的item
                                    position_play=position_firstVisible+1;
                                }
                                break;
                        }


                        try {
                            if (!luntanLists.get(position_play).getPostvideo().equals("https://oss.banghua.xin/0") && (position_play-layoutManager.findFirstVisibleItemPosition())>=0){
                                View view = recyclerView.getChildAt(position_play-layoutManager.findFirstVisibleItemPosition());
                                if (null != recyclerView.getChildViewHolder(view)){
                                    LuntanAdapter.ViewHolder viewHolder = (LuntanAdapter.ViewHolder)recyclerView.getChildViewHolder(view);
                                    viewHolder.video_layout.addView(CustomVideoView.getInstance(Someonesluntan1Activity.this,luntanLists.get(position_play)),2);
                                }
                            }
                        }catch (Exception e){
                            Log.e(TAG, "onBindViewHolder: 异常"+ e.toString());
                        }

                        //adapter.notifyItemChanged(position_play);

                        //注意
                        looperFlag=0;
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (!isLooper) return;
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    View view = layoutManager.findViewByPosition(position_play);
                    //说明播放的view还未完全消失
                    if (view != null) {

                        int y_t_rv = ScreenUtils.getViewScreenLocation(recyclerView)[1];//RV顶部Y坐标
                        int y_b_rv = y_t_rv + recyclerView.getHeight();//RV底部Y坐标

                        int y_t_view = ScreenUtils.getViewScreenLocation(view)[1];//播放的View顶部Y坐标
                        int height_view = view.getHeight();
                        int y_b_view = y_t_view + height_view;//播放的View底部Y坐标

                        //上滑
                        if (dy > 0) {
                            //播放的View上滑，消失了一半了,停止播放，
                            if ((y_t_rv > y_t_view) && ((y_t_rv - y_t_view) > height_view * 1f / 2)) {

                                CustomVideoView.getInstance(Someonesluntan1Activity.this);
                                luntanLists.get(position_play).setStartVideo(false);
                                adapter.luntanLists.get(position_play).setStartVideo(false);
                                //adapter.notifyItemChanged(position_play);
                                looperFlag = 2;//自动播放下一个
                            }

                        } else if (dy < 0) {
                            //下滑

//                        LogUtils.log("y_t_rv", y_t_rv);
//                        LogUtils.log("y_b_rv", y_b_rv);
                            //播放的View下滑，消失了一半了,停止播放
                            if ((y_b_view > y_b_rv) && ((y_b_view - y_b_rv) > height_view * 1f / 2)) {

                                CustomVideoView.getInstance(Someonesluntan1Activity.this);
                                luntanLists.get(position_play).setStartVideo(false);
                                adapter.luntanLists.get(position_play).setStartVideo(false);
                                //adapter.notifyItemChanged(position_play);
                                looperFlag = 1;//自动播放上一个
                            }
                        }
                    }
                }
            });


            recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                @Override
                public void onRefresh() {
                    Log.d(TAG, "onLoadMore: start");

                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setPullLoadMoreCompleted();
                        }
                    },1000);


                }

                @Override
                public void onLoadMore() {
                    pageindex = pageindex+1;
                    getDataPostlist(getString(R.string.someonesluntan_url),authid,pageindex+"");
                    Log.d(TAG, "个人帖子页码："+pageindex);
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setPullLoadMoreCompleted();
                        }
                    },1000);
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
                        if (msg.obj.toString().equals("[]")){
                            recyclerView.setHasMore(false);
                            return;
                        }
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
                OkHttpClient client = new OkHttpClient();
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
                    Log.d(TAG, "run: getDataPostlist发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
