package io.agora.chatroom.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

public class IsVisible {
    /**
     * 判断当前view是否在屏幕可见
     */
    public static boolean getLocalVisibleRect(Activity activity, View view, int offsetY) {
        Point p = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(p);
        int screenWidth = p.x;
        int screenHeight = p.y;
        Rect rect = new Rect(0, 0, screenWidth, screenHeight);//得到屏幕矩阵
        int[] location = new int[2];
        location[1] = location[1] + dip2px(activity,offsetY);
        view.getLocationInWindow(location);
        view.setTag(location[1]);//存储y方向的位置
        if (view.getLocalVisibleRect(rect)) {
            return true;
        } else {
            return false;
        }
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
