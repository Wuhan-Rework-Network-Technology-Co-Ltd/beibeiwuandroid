package xin.banghua.beiyuan.Main5Branch;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSON;

import butterknife.BindView;
import butterknife.ButterKnife;
import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SliderWebViewActivity;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;

public class WalletActivity extends AppCompatActivity {

    @BindView(R.id.money_tv)
    TextView money_tv;
    @BindView(R.id.income_tv)
    TextView income_tv;
    @BindView(R.id.bill_layout)
    LinearLayout bill_layout;
    @BindView(R.id.withdraw_layout)
    LinearLayout withdraw_layout;

    //提现到支付宝
    TextView dialog_hint;
    EditText alilogonid_et,aliname_et,transamount_et;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("钱包");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        OkHttpInstance.getUserAttributes(Common.myID, new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                Common.userInfoList = JSON.parseObject(responseString,UserInfoList.class);
                money_tv.setText(Common.userInfoList.getMoney()+"");
                income_tv.setText(Common.userInfoList.getIncome()+"");
            }
        });

        bill_layout.setOnClickListener(v -> {
            Intent intent = new Intent(this, SliderWebViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            intent.putExtra("slidername","账单");
            intent.putExtra("sliderurl","https://console.banghua.xin/app/index.php?i=999999&c=entry&do=user_bill&m=socialchat&id="+Common.myID);
            startActivity(intent);
        });
        withdraw_layout.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setIcon(R.drawable.ic_zhifubao);
            builder1.setTitle("支付宝提现");
            final AlertDialog dialog1 = builder1.create();
            View dialogView1 = View.inflate(this, R.layout.dialog_foot_withdraw, null);
            dialog1.setView(dialogView1);
            dialog1.show();

            alilogonid_et =  dialogView1.findViewById(R.id.alilogonid_et);
            if (!TextUtils.isEmpty(Common.userInfoList.getAlilogonid())){
                alilogonid_et.setText(Common.userInfoList.getAlilogonid());
            }
            aliname_et =  dialogView1.findViewById(R.id.aliname_et);
            if (!TextUtils.isEmpty(Common.userInfoList.getAliname())){
                aliname_et.setText(Common.userInfoList.getAliname());
            }
            transamount_et =  dialogView1.findViewById(R.id.transamount_et);
            dialog_hint = dialogView1.findViewById(R.id.dialog_hint);
            dialog_hint.setText("提现到支付宝（审核需要3-5天,平台税费20%)");
            Button dismissdialog_btn1 = dialogView1.findViewById(R.id.cancel_btn);
            dismissdialog_btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                }
            });
            Button confirm_btn1 = dialogView1.findViewById(R.id.confirm_btn);
            confirm_btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((Double.parseDouble(transamount_et.getText().toString()))>Double.parseDouble(income_tv.getText().toString())){
                        Toast toast = Toast.makeText(WalletActivity.this, "对不起，您的余额不足！", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }else {
                        OkHttpInstance.withdraw(alilogonid_et.getText().toString(), aliname_et.getText().toString(), transamount_et.getText().toString(), new OkHttpResponseCallBack() {
                            @Override
                            public void getResponseString(String responseString) {
                                Toast toast = Toast.makeText(WalletActivity.this, responseString, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        });
                    }
                    dialog1.dismiss();
                }
            });
        });
    }
}