package xin.banghua.beiyuan.PushPackage.Oppo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import xin.banghua.beiyuan.R;

public class OppoMessageReceiverActivity extends AppCompatActivity {
    private static final String TAG = "OppoMessageReceiverActivity";
    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oppo_message_receiver);



        //这个activity废除，oppo直接在Main3跳转，否则返回会空白
        Intent intent = getIntent();
        String userid = intent.getDataString();


        Log.d(TAG, "onCreate:useridh和username "+intent.getDataString());

//        if (RongIM.getInstance() != null) {
//            RongIM.getInstance().startPrivateChat(this, userid, username);
//        }
    }
}