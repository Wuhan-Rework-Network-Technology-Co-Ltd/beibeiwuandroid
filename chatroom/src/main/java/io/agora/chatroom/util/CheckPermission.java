package io.agora.chatroom.util;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.PermissionChecker;

public class CheckPermission {
    private static final String TAG = "CheckPermission";

    /**
     * 全部权限
     * @param activity
     */
    public static void verifyAllPermission(Activity activity) {
        //1.检测权限
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission4 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission5 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission6 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission7 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NOTIFICATION_POLICY);
        if (permission1 != PermissionChecker.PERMISSION_GRANTED || permission2 != PermissionChecker.PERMISSION_GRANTED ||
                permission3 != PermissionChecker.PERMISSION_GRANTED || permission4 != PermissionChecker.PERMISSION_GRANTED ||
                permission5 != PermissionChecker.PERMISSION_GRANTED || permission6 != PermissionChecker.PERMISSION_GRANTED ||
                permission7 != PermissionChecker.PERMISSION_GRANTED) {
            //2.没有权限，弹出对话框申请
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NOTIFICATION_POLICY},1000);
        }else {
            //intentMain();
        }
    }

    /**
     * 相机，麦克风和储存空间权限
     * @param activity
     */
    public static void verifyPermissionCameraAndAudioAndStorage(Activity activity) {
        //1.检测权限
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission4 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission1 != PermissionChecker.PERMISSION_GRANTED || permission2 != PermissionChecker.PERMISSION_GRANTED ||
                permission3 != PermissionChecker.PERMISSION_GRANTED || permission4 != PermissionChecker.PERMISSION_GRANTED) {
            //2.没有权限，弹出对话框申请
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }else {
            //intentMain();
        }
    }

    /**
     * 相机，麦克风和储存空间权限
     * @param activity
     */
    public static void verifyPermissionCameraAndStorage(Activity activity) {
        //1.检测权限
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission4 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission1 != PermissionChecker.PERMISSION_GRANTED ||
                permission3 != PermissionChecker.PERMISSION_GRANTED || permission4 != PermissionChecker.PERMISSION_GRANTED) {
            //2.没有权限，弹出对话框申请
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }else {
            //intentMain();
        }
    }

    /**
     * 全部权限
     * @param activity
     */
    public static void verifyPermissionAudioAndStorage(Activity activity) {
        //1.检测权限
        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission4 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission2 != PermissionChecker.PERMISSION_GRANTED ||
                permission3 != PermissionChecker.PERMISSION_GRANTED || permission4 != PermissionChecker.PERMISSION_GRANTED) {
            //2.没有权限，弹出对话框申请
            ActivityCompat.requestPermissions(activity,new String[]{
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }else {
            //intentMain();
        }
    }

    /**
     * 全部权限
     * @param activity
     */
    public static void verifyPermissionLocation(Activity activity) {
        //1.检测权限
        int permission5 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission6 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission7 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NOTIFICATION_POLICY);
        if (permission5 != PermissionChecker.PERMISSION_GRANTED || permission6 != PermissionChecker.PERMISSION_GRANTED ||
                permission7 != PermissionChecker.PERMISSION_GRANTED) {
            //2.没有权限，弹出对话框申请
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NOTIFICATION_POLICY},1000);
        }else {
            //intentMain();
        }
    }


    //推送检测权限
    public static Boolean verifyPushPermission(Activity activity) {
        Log.d(TAG, "verifyPushPermission: 通知检测");
        NotificationManagerCompat manager = NotificationManagerCompat.from(activity);
        // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
        boolean isOpened = manager.areNotificationsEnabled();
        if (!isOpened){
            //1.检测权限
            //未打开通知
            AlertDialog alertDialog = new AlertDialog.Builder(activity)
                    .setTitle("通知权限")
                    .setMessage("为了您能够及时的收到好友消息和系统通知，请在“通知”中打开权限！")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("android.provider.extra.APP_PACKAGE", activity.getPackageName());
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //5.0
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("app_package", activity.getPackageName());
                                intent.putExtra("app_uid", activity.getApplicationInfo().uid);
                                activity.startActivity(intent);
                            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {  //4.4
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                            } else if (Build.VERSION.SDK_INT >= 15) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                            }
                            activity.startActivity(intent);

                        }
                    })
                    .create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }

        return isOpened;
    }
}
