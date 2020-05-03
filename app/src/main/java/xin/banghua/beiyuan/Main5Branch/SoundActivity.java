package xin.banghua.beiyuan.Main5Branch;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
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
}
