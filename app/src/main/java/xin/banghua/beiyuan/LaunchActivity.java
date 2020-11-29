package xin.banghua.beiyuan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

public class LaunchActivity extends Activity {
    private static final String TAG = "LaunchActivity";

    ImageView launchImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        //权限检测
        verifyStoragePermission(this);

        //启动图
        initLaunchImage();

    }

    //启动图
    private void initLaunchImage(){
        launchImage = findViewById(R.id.launchImage);
        launchImage.setImageResource(R.drawable.launch);
    }

    //1.检测权限
    private void verifyStoragePermission(Activity activity) {
        //1.检测权限
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permission != PermissionChecker.PERMISSION_GRANTED) {
            //2.没有权限，弹出对话框申请
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1000);
        }else {
            intentMain();
        }
    }

    //授权回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:
                switch (permissions[0]){
                    case Manifest.permission.ACCESS_COARSE_LOCATION://权限1
                        if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                            intentMain();
                            Log.d(TAG, "onRequestPermissionsResult: COARSE");
                        }else {
                            Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                            intentMain();
                        }
                        break;
                    case Manifest.permission.ACCESS_FINE_LOCATION://权限2
                        if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                            intentMain();
                            Log.d(TAG, "onRequestPermissionsResult: fine");
                        } else {
                            Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                            intentMain();
                        }
                        break;
                    default:
                }
                break;
            default:
        }
    }

    public void intentMain(){
        //实现定位授权，初始图片，
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
