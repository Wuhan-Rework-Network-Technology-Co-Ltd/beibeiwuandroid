package xin.banghua.beiyuan;

import static com.donkingliang.imageselector.ImageSelectorActivity.IMAGE_SELECTOR_REQUEST_CODE;
import static xin.banghua.beiyuan.FlashPhotoPlugin.conversationType;
import static xin.banghua.beiyuan.FlashPhotoPlugin.photoPath;
import static xin.banghua.beiyuan.FlashPhotoPlugin.uniqueID;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.donkingliang.imageselector.utils.ImageSelector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.chatroom.gift.GiftDialog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Main3Branch.ConversationSettingActivity;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;


public class ConversationActivity extends AppCompatActivity {

    private static final String TAG = "ConversationActivity";

    CircleImageView portrait;
    TextView nickname;
    Switch istop,donotdisturb;
    Button recored_clear,blacklist_btn;

    String title;
    String targetId;

    TextView svip_hint_tv;

    private final static int CONVERSATION_SETTING = 2415;

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        Log.d(TAG, "onResume: ConversationActivity");

//        if (!Common.conversationSettingUserName.equals(title)){
//            title = Common.conversationSettingUserName;
//            getSupportActionBar().setTitle(title);
//        }

    }

    public static ConstraintLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);





        container = findViewById(R.id.container);


        GiftDialog.getInstance(this,false,container,null);

        svip_hint_tv = findViewById(R.id.svip_hint_tv);

        CheckPermission.verifyPermissionAudioAndStorage(this);

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

        getSVIPChat();
    }

    //TODO okhttp获取用户信息
    public void getSVIPChat(){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", targetId)
                        .add("yourid", myid)
                        .build();
                Request request = new Request.Builder()
                        .url(getString(R.string.getsvipchat_url))
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=1;
                    Log.d(TAG, "run: getDataPersonage"+message.obj.toString());
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
            //1是用户数据，2是幻灯片
            switch (msg.what){
                case 1:
                    if (msg.obj.toString().equals("1")){
                        svip_hint_tv.setVisibility(View.VISIBLE);
                        svip_hint_tv.postDelayed(() -> svip_hint_tv.setVisibility(View.GONE), 10000);
                    }else {
                        svip_hint_tv.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };
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
            startActivityForResult(intent,CONVERSATION_SETTING);
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

        Log.d(TAG, "onActivityResult: 进入1");
        if (requestCode == CONVERSATION_SETTING) {
            Log.d(TAG, "onActivityResult: 进入2");
            title = Common.conversationSettingUserName;
            getSupportActionBar().setTitle(title);
        }

        switch (requestCode) {
            //从图片选择器回来
            case IMAGE_SELECTOR_REQUEST_CODE:
                if (data != null) {
                    ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                    boolean isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false);
                    boolean isFull = data.getBooleanExtra(ImageSelector.IS_FULL, false);
                    Log.d("isCameraImage", "是否是拍照图片：" + isCameraImage);
                    if (images == null || images.size() <= 0) {
                        Toast.makeText(this, "取消设置", Toast.LENGTH_SHORT).show();
                    }
                    for (int i=0;i<images.size();i++){
                        Log.d(TAG, "onActivityResult: 图片地址"+ images.get(i));
                        //MediaPicker_img.setImageURI(Uri.fromFile(new File(image)));

                        photoPath = images.get(i);
                        Intent intent1 = new Intent(this, FlashPhotoPreviewActivity.class);
                        intent1.putExtra("uniqueID",uniqueID);
                        intent1.putExtra("targetId",targetId);
                        intent1.putExtra("photoPath",photoPath);
                        intent1.putExtra("conversationType",conversationType);
                        startActivity(intent1);
                    }
                }
                break;
        }
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
