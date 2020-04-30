package xin.banghua.beiyuan.MainBranch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.io.IOException;
import java.math.BigDecimal;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

public class LocationService extends Service implements AMapLocationListener {
    private final static String TAG = "LocationService";

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"onCreate executed");
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId ){
//        new Thread(new Runnable() {
//            @Override
//            public void run(){
////                SharedHelper shuserinfo = new SharedHelper(getActivity().getApplicationContext());
////                String myid = shuserinfo.readUserInfo().get("userID");
//                while (true){
//                    Log.d(TAG,"onStartCommand executed");
//                    try{
//                        Thread.sleep(2000);
//                    }catch (Exception e ){
//                        Log.d(TAG,e.toString());
//                    }
//                }
//
//            }
//        }).start();
        Log.d(TAG,"onStartCommand executed");


        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

//        //获取一次定位结果：
//        //该方法默认为false。
//        mLocationOption.setOnceLocation(true);
//
//        //获取最近3s内精度最高的一次定位结果：
//        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
//        mLocationOption.setOnceLocationLatest(true);

        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(6000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.stopLocation();
        mLocationClient.startLocation();



        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy execute");
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation.getLatitude()!=0.0) {
            BigDecimal latitude = new BigDecimal(aMapLocation.getLatitude() + "").setScale(6, BigDecimal.ROUND_DOWN);
            BigDecimal longitude = new BigDecimal(aMapLocation.getLongitude() + "").setScale(6, BigDecimal.ROUND_DOWN);
            Log.d(TAG, "定位地址：纬度" + latitude + "|经度：" + longitude);
            SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
            String myid = shuserinfo.readUserInfo().get("userID");
            //保存定位值
            //Float latitude = (float)(Math.round(location.getLatitude()*10))/10;
            shuserinfo.saveLocation(latitude+"",longitude+"");
            //上传定位值
            postLocationInfo(myid, latitude + "", longitude + "", "https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=updatelocation&m=moyuan");
        }
    }
    //更新定位信息
    public void postLocationInfo(final String userID,final String latitude, final String longitude,final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("userID", userID)
                        .add("latitude", latitude)
                        .add("longitude",longitude)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    //Log.d("form形式的post",response.body().string());
                    //不需要返回值
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
