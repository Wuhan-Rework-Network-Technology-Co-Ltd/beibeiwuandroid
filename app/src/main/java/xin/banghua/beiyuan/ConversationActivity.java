package xin.banghua.beiyuan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Main3Branch.ConversationSettingActivity;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

import static io.rong.imlib.model.Conversation.ConversationNotificationStatus.DO_NOT_DISTURB;
import static io.rong.imlib.model.Conversation.ConversationNotificationStatus.NOTIFY;


public class ConversationActivity extends AppCompatActivity {

    private static final String TAG = "ConversationActivity";

    CircleImageView portrait;
    TextView nickname;
    Switch istop,donotdisturb;
    Button recored_clear,blacklist_btn;

    String title;
    String targetId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        CheckPermission.verifyStoragePermission(this);
        Intent intent = getIntent();
        title = intent.getData().getQueryParameter("title") ;
        Log.d(TAG, "onCreate: 人名："+title);
        targetId = intent.getData().getQueryParameter("targetId") ;
        Log.d(TAG, "onCreate: id："+targetId);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);


    }

    @Override  //菜单的填充
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_conversation, menu);
        return true;
    }
    @Override  //菜单的点击，其中返回键的id是android.R.id.home
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_conversation_personpage) {
            Intent intent = new Intent(ConversationActivity.this, ConversationSettingActivity.class);
            intent.putExtra("targetId",targetId);
            intent.putExtra("title",title);
            startActivity(intent);
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                //this.finish(); // back button
                //启动会话列表    为了刷新未读信息数
                startActivity(new Intent(ConversationActivity.this, Main3Activity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        /**
         * 1.使用getSupportFragmentManager().getFragments()获取到当前Activity中添加的Fragment集合
         * 2.遍历Fragment集合，手动调用在当前Activity中的Fragment中的onActivityResult()方法。
         */
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment mFragment : fragments) {
                Log.d(TAG, "onActivityResult: 调用了这个"+mFragment.toString());
                //mFragment.onActivityResult(requestCode, resultCode, data);
                List<Fragment> subfragments = mFragment.getChildFragmentManager().getFragments();
                for (Fragment subfragment : subfragments){
                    Log.d(TAG, "onActivityResult: 又调用了这个"+subfragment.toString());
                    subfragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }

    }
}
