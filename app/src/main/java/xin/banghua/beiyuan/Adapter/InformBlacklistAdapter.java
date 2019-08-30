package xin.banghua.beiyuan.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Main4Branch.InformReasonActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

public class InformBlacklistAdapter extends BaseAdapter {
    private static final String TAG = "TestAdapter";
    Context context;
    String type;//circle或post
    String itemid;
    String userid;


    LayoutInflater layoutInflater;


    //传入类型和id，itemid举报时就是条目id
    public InformBlacklistAdapter(Context context,String type,String itemid,String userid) {
        this.context = context;
        this.type = type;
        this.itemid = itemid;
        this.userid = userid;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.infrom_blacklist_layout,null);
        Button inform_btn = view.findViewById(R.id.inform_btn);
        Button blacklist_btn = view.findViewById(R.id.blacklist_btn);
        inform_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //举报
                Intent intent = new Intent(context, InformReasonActivity.class);
                String[] informparameters = {type,itemid};
                intent.putExtra("informparameters",informparameters);
                context.startActivity(intent);
            }
        });
        blacklist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拉黑
                addblacklist("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=addblacklist&m=socialchat");
            }
        });
        return view;
    }

    //网络数据部分
//处理返回的数据
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是用户数据，2是幻灯片
            switch (msg.what){
                case 1:
                    Toast.makeText(context, "拉黑成功", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    //TODO okhttp获取用户信息
    public void addblacklist(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(context.getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid",myid)
                        .add("yourid",userid)
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
                    Log.d(TAG, "run: addblacklist"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
