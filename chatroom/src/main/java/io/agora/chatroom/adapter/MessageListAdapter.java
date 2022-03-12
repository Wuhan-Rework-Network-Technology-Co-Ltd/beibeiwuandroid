package io.agora.chatroom.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.chatroom.UserInfoDialog;
import io.agora.chatroom.manager.ChatRoomManager;
import io.agora.chatroom.model.ChannelData;
import io.agora.chatroom.model.Member;
import io.agora.chatroom.model.Message;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private LayoutInflater mInflater;

    private ChannelData mChannelData;

    public MessageListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mChannelData = ChatRoomManager.instance(context).getChannelData();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (mChannelData == null) return 0;
        return mChannelData.getMessageList().size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mChannelData.getMessageList().get(position);
        String userId = message.getSendId();

        Log.d("TAG", "onBindViewHolder: 发消息的人"+userId+mChannelData.getMemberAvatar(userId));
        //holder.iv_avatar.setImageResource(mChannelData.getMemberAvatar(userId));
        Glide.with(mInflater.getContext()).load(mChannelData.getMemberAvatar(userId)).into(holder.iv_avatar);
        holder.iv_avatar.setOnClickListener(v -> {
            UserInfoDialog userInfoDialog = new UserInfoDialog(mInflater.getContext());
            userInfoDialog.initShow(userId);
        });

        if (mChannelData.getMember(userId).getGender()!=null){
            if (mChannelData.getMember(userId).getGender().equals("女")){
                holder.tv_property.setBackgroundResource(R.drawable.shape_age_female);
            }else {
                holder.tv_property.setBackgroundResource(R.drawable.shape_age_male);
            }
            holder.tv_property.setText(mChannelData.getMember(userId).getProperty());
        }

        switch (message.getMessageType()) {
            case Message.MESSAGE_TYPE_TEXT:
                holder.tv_message.setVisibility(View.VISIBLE);
                Member member = mChannelData.getMember(userId);
                holder.tv_message.setText(String.format("%s：%s", member != null ? member.getName() : userId, message.getContent()));
                break;
            case Message.MESSAGE_TYPE_IMAGE:
                // TODO
                break;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.iv_avatar)
        ImageView iv_avatar;
        @BindView(R2.id.tv_message)
        TextView tv_message;
        @BindView(R2.id.iv_image)
        ImageView iv_image;
        @BindView(R2.id.tv_property)
        TextView tv_property;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
