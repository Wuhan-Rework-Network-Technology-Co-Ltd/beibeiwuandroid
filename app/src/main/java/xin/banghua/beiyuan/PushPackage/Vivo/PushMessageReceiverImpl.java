package xin.banghua.beiyuan.PushPackage.Vivo;

import static xin.banghua.beiyuan.PushPackage.PushClass.pushRegID;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

import xin.banghua.beiyuan.Main3Activity;

public class PushMessageReceiverImpl  extends OpenClientPushMessageReceiver {



    /***

     * 当通知被点击时回调此方法

     * @param context 应用上下文

     * @param msg 通知详情，详细信息见API接入文档

     */

    @Override

    public void onNotificationMessageClicked(Context context, UPSNotificationMessage msg) {

        //跳转到Main3再跳转会话页
        Intent intent = new Intent(context, Main3Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.putExtra("userid",msg.getParams().get("userid"));
        intent.putExtra("username",msg.getParams().get("username"));
        context.startActivity(intent);
    }



    /***

     * 当首次turnOnPush成功或regId发生改变时，回调此方法

     * 如需获取regId，请使用PushClient.getInstance(context).getRegId()

     * @param context 应用上下文

     * @param regId 注册id

     */

    @Override

    public void onReceiveRegId(Context context, String regId) {
        pushRegID = regId;
        Log.d(TAG, "onReceiveRegIdvivo推送:注册成功 regID = " +   pushRegID);
    }

}
