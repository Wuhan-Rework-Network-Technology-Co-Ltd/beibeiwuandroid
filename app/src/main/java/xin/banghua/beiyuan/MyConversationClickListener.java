package xin.banghua.beiyuan;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.RongYunExtension.FlashPhotoActivity;

public class MyConversationClickListener implements RongIM.ConversationClickListener {
    private static final String TAG = "MyConversationClickList";
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
        Log.d(TAG, "onUserPortraitClick: 点击头像了");
        //Toast.makeText(mContext,mUserID.get(i)+mUserNickName.get(i),Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, PersonageActivity.class);
        intent.putExtra("userID",userInfo.getUserId());
        context.startActivity(intent);
        return false;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
        return false;
    }

    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        if (message.getObjectName().equals("RC:TxtMsg")){
            Log.d("闪图","RC:TxtMsg");
                String[] textSplit = message.getContent().toString().split("'");
                if (textSplit[1].equals("<点击查看5秒闪图>")){
                    Log.d("闪图","发送了闪图"+textSplit[3]);
                    Intent intent = new Intent(view.getContext(), FlashPhotoActivity.class);
                    intent.putExtra("uniqueid",textSplit[3]);
                    view.getContext().startActivity(intent);
                }
        }
        //Log.d("闪图","发送了消息"+message.getContent().getJSONUserInfo()+"|"+message.getContent().getJsonMentionInfo());
        return false;
    }

    @Override
    public boolean onMessageLinkClick(Context context, String s, Message message) {
        return false;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }
}
