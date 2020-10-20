package xin.banghua.beiyuan.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

public class BuyvipAdapter extends RecyclerView.Adapter<BuyvipAdapter.ViewHolder>  {

    private static final String TAG = "BuyvipAdapter";
    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    private static final String APP_ID = "wxef862b4ad2079599";

    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;

    Button button,button1;


    private List<BuyvipList> buyvipLists;


    private Activity mContext;

    private String orderInfo;

    public BuyvipAdapter(Activity mContext, List<BuyvipList> buyvipLists) {
        this.mContext = mContext;
        this.buyvipLists = buyvipLists;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_buyvip,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final BuyvipList currentItem = buyvipLists.get(i);
        viewHolder.vipid.setText(currentItem.getVipid());
        viewHolder.vipname.setText(currentItem.getVipname());
        viewHolder.viptime.setText(currentItem.getViptime()+"天");
        viewHolder.vipprice.setText(currentItem.getVipprice()+"元");

        switch (currentItem.getVipname()){
            case "钻石会员":
                viewHolder.vip_privilege_tv.setText("点亮标识，7大特权");
                break;
            case "黑金会员":
                viewHolder.vip_privilege_tv.setText("尊贵标识，优惠18％");
                break;
            case "白金会员":
                viewHolder.vip_privilege_tv.setText("年会贵宾标识，优惠23%，(排名靠前)");
                break;
        }

        viewHolder.weixin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUnifiedorder("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=payunifiedorder2&m=socialchat",currentItem.getVipid());
            }
        });
        viewHolder.alipay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alipayorder("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=alipayaddorder2&m=socialchat",currentItem.getVipid());
            }
        });

    }

    @Override
    public int getItemCount() {
        return buyvipLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView vipid;
        TextView vipname;
        TextView viptime;
        TextView vipprice;
        TextView vip_privilege_tv;
        Button weixin_btn;
        Button alipay_btn;
        RelativeLayout buyvipLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vipid = itemView.findViewById(R.id.vipid);
            vipname = itemView.findViewById(R.id.vipname);
            viptime = itemView.findViewById(R.id.viptime);
            vipprice = itemView.findViewById(R.id.vipprice);
            vip_privilege_tv = itemView.findViewById(R.id.vip_privilege_tv);
            weixin_btn = itemView.findViewById(R.id.weixin_btn);
            alipay_btn = itemView.findViewById(R.id.alipay_btn);
            buyvipLayout = itemView.findViewById(R.id.buyvip_layout);
        }
    }


    //网络数据部分
//处理返回的数据
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是微信支付
            //1是支付宝支付
            switch (msg.what){
                case 1:
                    JSONObject jsonObject = null;//原生的
                    try {
                        jsonObject = new JSONObject(msg.obj.toString());
                        Log.d(TAG, "handleMessage: "+jsonObject.getString("prepay_id")+";"+jsonObject.getString("sign"));
                        //返回了统一下单的参数和二次签名
                        PayReq request = new PayReq();
                        request.appId = jsonObject.getString("appid");
                        request.partnerId = jsonObject.getString("mch_id");
                        request.prepayId= jsonObject.getString("prepay_id");
                        request.packageValue = "Sign=WXPay";
                        request.nonceStr= jsonObject.getString("nonce_str");
                        request.timeStamp= jsonObject.getString("timeStamp");
                        request.sign= jsonObject.getString("sign");

                        api = WXAPIFactory.createWXAPI(mContext, APP_ID, true);
                        api.registerApp(APP_ID); // 将应用的appId注册到微信
                        api.sendReq(request);//用api发起支付调用
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    //支付宝生成订单后，调用后端签名，获取orderString    2和3都作废了，现在订单放到了服务端
                    Log.d(TAG, "handleMessage: 进入2");
                    alipay("https://www.banghua.xin/alipay-sdk-PHP/alipaybeiyuan2.php",msg.obj.toString());
                    break;
                case 3:
                    alipay("https://www.banghua.xin/alipay-sdk-PHP/alipaybeiyuan2.php",msg.obj.toString());
                    break;
                case 4:
                    //支付宝返回orderString，用orderString发起支付
                    Log.d(TAG, "handleMessage: 进入4");
                    orderInfo = msg.obj.toString();
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                    break;
                case 5:

                    break;
                case 6:
                    //支付宝支付完成后的同步通知
                    Log.d(TAG, "handleMessage: 支付宝同步回调"+msg.obj.toString());
                    Map<String,String> result = (Map<String,String>)msg.obj;
                    Log.d(TAG, "handleMessage: 同步通知状态"+result.get("resultStatus"));
                    if (result.get("resultStatus").equals("9000")) {
                        Log.d(TAG, "handleMessage: 支付宝通知测试");
                        Toast.makeText(mContext, "支付成功", Toast.LENGTH_LONG).show();
                    }
                    break;
            }

        }
    };

    //支付宝接口调用
    Runnable payRunnable = new Runnable() {
        
        @Override
        public void run() {
            Log.d(TAG, "run: 进入支付");
            //EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX); //关闭沙箱测试
            PayTask alipay = new PayTask(mContext);
            Map<String,String> result = alipay.payV2(orderInfo,true);

            Message msg = new Message();
            msg.what = 6;
            msg.obj = result;
            handler.sendMessageDelayed(msg,10);
        }
    };




    //TODO okhttp先生成订单
    public void alipayorder(final String url,final String vipid){
        new Thread(new Runnable() {
            @Override
            public void run(){

                SharedHelper shuserinfo = new SharedHelper(mContext.getApplicationContext());
                String userid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("vipid", vipid)
                        .add("userid", userid)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=4;//获取orderstring已经放入下单方法中，可直接调用支付
                    Log.d(TAG, "run: alipayorder发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //TODO okhttp支付
    public void alipay(final String url,final String orderid){
        new Thread(new Runnable() {
            @Override
            public void run(){

                Log.d(TAG, "run: 进入阿里pay");
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("appname", "socialchat")
                        .add("orderid", orderid)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=4;
                    Log.d(TAG, "run: alipay发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //TODO okhttp微信支付，获取统一下单
    public void getUnifiedorder(final String url,final String vipid){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(mContext.getApplicationContext());
                String userid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("userid", userid)
                        .add("vipid", vipid)
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
