package xin.banghua.beiyuan.Main4Branch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

public class InformReasonActivity extends AppCompatActivity {
    private static final String TAG = "InformReasonActivity";

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform_reason);

        Intent intent = getIntent();
        final String[] informparameters = intent.getStringArrayExtra("informparameters");

        context = this;

        final EditText informreason = findViewById(R.id.informreason_text);

        Button inform_btn = findViewById(R.id.imformsubmit_btn);
        inform_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (informreason.getText().toString().trim().equals("")){
                    Toast.makeText(context, "请输入账号密码", Toast.LENGTH_LONG).show();
                }else {
                    String reason = informreason.getText().toString().trim();
                    addInformer("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=addinform&m=socialchat",informparameters,reason);
                }
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
            //1
            if (msg.what==1){
                Toast.makeText(context, "举报成功", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    };
    //TODO okhttp提交举报   参数{type,itemid}
    public void addInformer(final String url,final String[] informparameters,final String reason){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(context.getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");



                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("type", informparameters[0])
                        .add("itemid",informparameters[1])
                        .add("informerid",myid)
                        .add("reason",reason)
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
