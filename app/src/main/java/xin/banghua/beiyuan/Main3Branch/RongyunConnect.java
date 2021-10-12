package xin.banghua.beiyuan.Main3Branch;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class RongyunConnect {
    private static final String TAG = "RongyunConnect";
    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    public void connect(String token) {
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                @Override
                public void onSuccess(String s) {
                    Log.d(TAG, "connect: 进入app的融云链接"+s+"||"+token);
                }

                @Override
                public void onError(RongIMClient.ConnectionErrorCode connectionErrorCode) {

                }

                @Override
                public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {

                }
            });

    }


    /**
     * 保证融云客户端只在RongIMClient 的进程和 Push 进程执行了 init
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
