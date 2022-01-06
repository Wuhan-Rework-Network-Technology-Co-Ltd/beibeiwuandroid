package xin.banghua.beiyuan.utils;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * 日志输出
 */
public class LogUtil {

    private static Context context;
    private static Handler handler;

    public static void init(Application application) {
        LogUtil.context = application.getApplicationContext();
        handler = new Handler(Looper.getMainLooper());
    }

    public static void checkPoint(final String msg) {
        if (context == null) {
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
        Log.i("CheckPoint", msg);
    }

    public static void checkFail(final String msg) {
        if (context == null) {
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "Error: "+msg, Toast.LENGTH_SHORT).show();
            }
        });
        Log.e("CheckPoint", msg);
    }

}
