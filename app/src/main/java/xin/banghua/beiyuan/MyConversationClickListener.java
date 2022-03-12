package xin.banghua.beiyuan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import xin.banghua.beiyuan.Main2Branch.NewFriend;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.RongYunExtension.FlashPhotoActivity;
import xin.banghua.beiyuan.Signin.SigninActivity;
import xin.banghua.beiyuan.comment.CommentListActivity;

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
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    intent.putExtra("uniqueid",textSplit[3]);
                    view.getContext().startActivity(intent);
                }
        }
        if (message.getObjectName().equals("RC:TxtMsg")){
            Log.d("新好友","RC:TxtMsg");
            String[] textSplit = message.getContent().toString().split("'");
            if (textSplit[1].contains("有人申请您为好友了")){
                Log.d("新好友","申请您为好友了"+textSplit[3]);
                Intent intent = new Intent(view.getContext(), NewFriend.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                view.getContext().startActivity(intent);
            }
        }
        if (message.getObjectName().equals("RC:TxtMsg")){
            Log.d("新好友","RC:TxtMsg");
            String[] textSplit = message.getContent().toString().split("'");
            if (textSplit[1].contains("回复了你的评论：")||textSplit[1].contains("评论了你的帖子：")){
                Log.d("评论","回复了你的评论"+textSplit[3]);
                Intent intent = new Intent(view.getContext(), CommentListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                view.getContext().startActivity(intent);
            }
        }
        if (message.getObjectName().equals("RC:TxtMsg")){
            Log.d("新动态","RC:TxtMsg");
            String[] textSplit = message.getContent().toString().split("'");
            if (textSplit[1].contains("您关注的用户")){
                Intent intent = new Intent(view.getContext(), Main4Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                intent.putExtra("menu","关注");
                view.getContext().startActivity(intent);
            }
        }
        if (message.getObjectName().equals("RC:TxtMsg")){
            Log.d("新好友","RC:TxtMsg");
            String[] textSplit = message.getContent().toString().split("'");
            if (textSplit[1].contains("审核员，又有新帖了，快去审核吧！") && message.getSenderUserId().equals("1")){
                Intent intent = new Intent(view.getContext(), SliderWebViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                intent.putExtra("slidername","审核");
                intent.putExtra("sliderurl","https://console.banghua.xin/app/index.php?i=99999&c=entry&do=post_censorship&m=socialchat&page=1&id="+Common.myID);
                view.getContext().startActivity(intent);
            }
        }
        if (message.getObjectName().equals("RC:TxtMsg")){
            String[] textSplit = message.getContent().toString().split("'");
            if (textSplit[1].equals("你因为违规，已被封禁。forbid")){
                SharedPreferences sp = view.getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("userID", "");
                editor.commit();
                Intent intent = new Intent(view.getContext(), SigninActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                view.getContext().startActivity(intent);
                System.exit(0);
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
