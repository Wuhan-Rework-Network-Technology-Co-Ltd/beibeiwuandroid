package xin.banghua.beiyuan.Main5Branch;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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
import xin.banghua.beiyuan.Adapter.BuyvipAdapter;
import xin.banghua.beiyuan.Adapter.BuyvipList;
import xin.banghua.beiyuan.ParseJSON.ParseJSONArray;
import xin.banghua.beiyuan.R;

public class BuyvipActivity extends AppCompatActivity {
    private static final String TAG = "BuyvipActivity";

    private List<BuyvipList> buyvipLists = new ArrayList<>();
    private View mView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyvip);

        getSupportActionBar().setTitle("购买会员");

        mView = getWindow().getDecorView();
        ImageView back_btn = findViewById(R.id.iv_back_left);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getVip("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getvip&m=socialchat");
    }

    //TODO 初始化Viprecyclerview
    private void initRecyclerView(View view){

        final PullLoadMoreRecyclerView recyclerView = view.findViewById(R.id.buyvip_recyclerview);
        BuyvipAdapter adapter = new BuyvipAdapter(BuyvipActivity.this,buyvipLists);
        recyclerView.setAdapter(adapter);
        recyclerView.setLinearLayout();
        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {


                recyclerView.setPullLoadMoreCompleted();
            }

            @Override
            public void onLoadMore() {


                recyclerView.setPullLoadMoreCompleted();
            }
        });
    }
    //TODO 用户列表
    private void initVip(View view, JSONArray jsonArray) throws JSONException {

        if (jsonArray.length()>0){
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (Integer.parseInt(jsonObject.getString("id"))<6) {
                    BuyvipList buyvipList = new BuyvipList(jsonObject.getString("id"), jsonObject.getString("vipname"), jsonObject.getString("viptime"), jsonObject.getString("vipprice"));
                    buyvipLists.add(buyvipList);
                }
            }
        }
        initRecyclerView(view);
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
                        JSONArray jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
                        initVip(getWindow().getDecorView(),jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    //TODO okhttp获取vip信息
    public void getVip(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    Log.d(TAG, "run: vip接收"+message.obj.toString());
                    message.what=1;
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
