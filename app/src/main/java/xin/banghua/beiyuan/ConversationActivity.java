package xin.banghua.beiyuan;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import xin.banghua.beiyuan.Main3Branch.ConversationSettingActivity;


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
