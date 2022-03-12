package io.agora.chatroom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private OnItemClickListener mListener;

    private ChannelData mChannelData;
    private String suffix;

    public MemberListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mChannelData = ChatRoomManager.instance(context).getChannelData();
        suffix = context.getString(R.string.anchor);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_item_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (mChannelData == null) return 0;
        return mChannelData.getMemberList().size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = mChannelData.getMemberList().get(position);
        String userId = member.getUserId();

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


        if (mChannelData.isUserOnline(userId)) {
            boolean muted = mChannelData.isUserMuted(userId);
            holder.iv_mute.setVisibility(View.VISIBLE);
            holder.iv_mute.setImageResource(muted ? R.mipmap.ic_mic_off_little : R.mipmap.ic_mic_on_little);
            holder.btn_role.setText(R.string.to_audience);
            holder.btn_mute.setVisibility(View.VISIBLE);
            holder.btn_mute.setText(muted ? R.string.turn_on_mic : R.string.turn_off_mic);
        } else {
            holder.iv_mute.setVisibility(View.GONE);
            holder.btn_role.setText(R.string.to_broadcast);
            holder.btn_mute.setVisibility(View.GONE);
        }

        if (!mChannelData.isAnchorMyself()) {
            holder.btn_role.setVisibility(View.GONE);
            holder.btn_mute.setVisibility(View.GONE);
        }

        String name = member.getName();
        if (mChannelData.isAnchor(userId))
            name += suffix;
        holder.tv_name.setText(name);

        holder.btn_role.setOnClickListener(view -> {
            if (mListener != null)
                mListener.onItemClick(view, position, userId);
        });
        holder.btn_mute.setOnClickListener(view -> {
            if (mListener != null)
                mListener.onItemClick(view, position, userId);
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void notifyItemChangedByUserId(String userId) {
        int index = mChannelData.indexOfMemberList(userId);
        if (index >= 0)
            notifyItemChanged(index);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.iv_avatar)
        ImageView iv_avatar;
        @BindView(R2.id.iv_mute)
        ImageView iv_mute;
        @BindView(R2.id.tv_name)
        TextView tv_name;
        @BindView(R2.id.btn_role)
        Button btn_role;
        @BindView(R2.id.btn_mute)
        Button btn_mute;
        @BindView(R2.id.tv_property)
        TextView tv_property;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, String userId);
    }

}
