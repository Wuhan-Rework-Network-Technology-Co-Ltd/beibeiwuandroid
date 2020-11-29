package xin.banghua.beiyuan.Main5Branch;


import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

public class SoundActivity extends AppCompatActivity {
    RadioGroup sound_on_off;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);


        SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
        String soundSet = shuserinfo.readSoundSet();

        sound_on_off = findViewById(R.id.sound_on_off);

        if (soundSet.equals("sound_off")){
            sound_on_off.check(R.id.sound_off);
        }else {
            sound_on_off.check(R.id.sound_on);
        }

        sound_on_off.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // TODO Auto-generated method stub
                        //获取选中的radiobutton内容 group.getCheckedRadioButtonId()
                        RadioButton radiobutton = (RadioButton)SoundActivity.this.findViewById(group.getCheckedRadioButtonId());


                        if (radiobutton.getText().toString().equals("响铃提醒")){
                            updateRedisCache("0");

                            RongIM.getInstance().removeNotificationQuietHours(new RongIMClient.OperationCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(getApplicationContext(),"已开启"+ radiobutton.getText().toString(),Toast.LENGTH_LONG).show();
                                    SharedHelper shvalue = new SharedHelper(getApplicationContext());
                                    shvalue.saveSoundSet("sound_on");
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {

                                }
                            });
                        }else{
                            updateRedisCache("1");

                            RongIM.getInstance().setNotificationQuietHours("00:00:00", 1339, new RongIMClient.OperationCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(getApplicationContext(),"已开启"+ radiobutton.getText().toString(),Toast.LENGTH_LONG).show();
                                    SharedHelper shvalue = new SharedHelper(getApplicationContext());
                                    shvalue.saveSoundSet("sound_off");
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {

                                }
                            });
                        }
                    }
                }
        );

    }


    //TODO 登录 form形式的post
    public void updateRedisCache(String disturb){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("disturb", disturb)
                        .build();
                Request request = new Request.Builder()
                        .url("https://weiqing.oushelun.cn/app/index.php?i=99999&c=entry&a=webapp&do=xiaobeidisturb&m=rediscache")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
