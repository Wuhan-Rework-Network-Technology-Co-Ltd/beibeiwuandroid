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
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Adapter.DongtaiAdapter;
import xin.banghua.beiyuan.Adapter.DongtaiList;
import xin.banghua.beiyuan.ParseJSON.ParseJSONArray;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

public class CircleActivity extends AppCompatActivity {
    private static final String TAG = "CircleActivity";
    private SharedHelper sh;
    public Map<String,String> userInfo;
    //vars

    private List<DongtaiList> dongtaiLists = new ArrayList<>();

    private View mView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);

        getSupportActionBar().setTitle("朋友圈");

        mView = getWindow().getDecorView();

        getDataDongtai("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=circle&m=socialchat");

        ImageView back_btn = findViewById(R.id.iv_back_left);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    //TODO 用户列表
    private void initDongtai(View view, JSONArray jsonArray) throws JSONException {
        Log.d(TAG, "initUserInfo: preparing userinfo");

        if (jsonArray.length()>0){
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String[] picture = jsonObject.getString("picture").split(",");
                String circlePicture1 = "";
                String circlePicture2 = "";
                String circlePicture3 = "";

                switch (picture.length){
                    case 1:
                        circlePicture1 = picture[0];
                        break;
                    case 2:
                        circlePicture1 = picture[0];
                        circlePicture2 = picture[1];
                        break;
                    case 3:
                        circlePicture1 = picture[0];
                        circlePicture2 = picture[1];
                        circlePicture3 = picture[2];
                        break;
                }
                DongtaiList dongtaiList = new DongtaiList(jsonObject.getString("age"),jsonObject.getString("gender"),jsonObject.getString("region"),jsonObject.getString("property"),jsonObject.getString("id"),jsonObject.getString("myid"),jsonObject.getString("mynickname"),jsonObject.getString("myportrait"),jsonObject.getString("context"),circlePicture1,circlePicture2,circlePicture3,jsonObject.getString("video"),jsonObject.getString("share"),jsonObject.getString("like"),jsonObject.getString("time"));
                dongtaiLists.add(dongtaiList);
            }
        }
        initRecyclerView(view);
    }

    //TODO 初始化动态recyclerview
    private void initRecyclerView(View view){
        Log.d(TAG, "initRecyclerView: init recyclerview");

        final PullLoadMoreRecyclerView recyclerView = view.findViewById(R.id.circle_RecyclerView);
        DongtaiAdapter adapter = new DongtaiAdapter(this,dongtaiLists);
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
    //TODO okhttp获取动态列表
    public void getDataDongtai(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                sh = new SharedHelper(mView.getContext());
                userInfo = sh.readUserInfo();
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("userID", userInfo.get("userID"))
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

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是动态数据，2是幻灯片
            switch (msg.what){
                case 1:
                    try {
                        String resultJson1 = msg.obj.toString();
                        Log.d(TAG, "handleMessage: 动态信息接收的值"+msg.obj.toString());

                        JSONArray jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
                        initDongtai(mView,jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };
}
