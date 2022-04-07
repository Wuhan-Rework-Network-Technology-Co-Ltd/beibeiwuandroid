package xin.banghua.beiyuan.Main5Branch;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aliyun.aliyunface.api.ZIMCallback;
import com.aliyun.aliyunface.api.ZIMFacade;
import com.aliyun.aliyunface.api.ZIMFacadeBuilder;
import com.aliyun.aliyunface.api.ZIMResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;


public class RPVerifyActivity extends AppCompatActivity {
    private static final String TAG = "RPVerifyActivity";


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    String certNo;
    String certName;

    @BindView(R.id.certNo_et)
    EditText certNo_et;
    @BindView(R.id.certName_et)
    EditText certName_et;
    @BindView(R.id.submit_btn)
    Button submit_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpverify);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("实名认证");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ZIMFacade.install(RPVerifyActivity.this);
        String metaInfos = ZIMFacade.getMetaInfos(RPVerifyActivity.this);

        Log.d(TAG, "onCreate: metaInfos"+metaInfos);
        //实名认证
        if (!Common.userInfoList.getRp_verify_time().equals("0")){
            return;
        }



        ZIMFacade zimFacade = ZIMFacadeBuilder.create(RPVerifyActivity.this);

        submit_btn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(certNo_et.getText().toString()) || TextUtils.isEmpty(certName_et.getText().toString())){
                Toast.makeText(RPVerifyActivity.this,"请输入身份证和姓名",Toast.LENGTH_SHORT).show();
            }else {
                OkHttpInstance.getRPVerifyToken(metaInfos,certNo_et.getText().toString(),certName_et.getText().toString(),new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        Log.d(TAG, "getResponseString:金融级实名认证token "+responseString);
                        zimFacade.verify(responseString, true, new ZIMCallback() {
                            @Override
                            public boolean response(ZIMResponse response) {
                                // TODO: 根据实人认证回调结果处理自身的业务。
                                if (null != response && 1000 == response.code) {
                                    // 认证成功。
                                    Integer current_timestamp = Integer.valueOf((int) (System.currentTimeMillis()/1000));
                                    Common.userInfoList.setRp_verify_time(current_timestamp+"");

                                    //远程请求验证信息
                                    OkHttpInstance.saveRPVerifyToken(responseString,certName_et.getText().toString(),certNo_et.getText().toString());

                                    finish();
                                } else {
                                    // 认证失败。
                                }
                                return true;
                            }
                        });
                    }
                });
            }
        });

    }
}