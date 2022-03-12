package io.agora.chatroom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.chatroom.manager.ChatRoomManager;
import io.agora.chatroom.model.ChannelData;
import io.agora.chatroom.model.Seat;
import io.agora.chatroom.util.PortraitFrameView;
import io.agora.chatroom.widget.SpreadView;

public class SeatGridAdapter extends RecyclerView.Adapter<SeatGridAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private OnItemClickListener mListener;

    private ChannelData mChannelData;

    public SeatGridAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mChannelData = ChatRoomManager.instance(context).getChannelData();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_item_seat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (mChannelData == null) return 0;
        return mChannelData.getSeatArray().length;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.size() > 0)
            holder.view_anim.startAnimation();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Seat seat = mChannelData.getSeatArray()[position];

        if (seat != null) {
            if (seat.isClosed()) {
                holder.iv_seat.setImageResource(R.mipmap.ic_ban);
                holder.iv_mute.setVisibility(View.GONE);
            } else {
                String userId = seat.getUserId();
                if (mChannelData.isUserOnline(userId)) {
                    //holder.iv_seat.setImageResource(mChannelData.getMemberAvatar(userId));
                    if (!TextUtils.isEmpty(mChannelData.getMember(userId).getPortraitframe())){
                        Log.d("TAG", "onBindViewHolder: 头像框"+mChannelData.getMember(userId).getPortraitframe());
                        holder.portraitFrameView.setVisibility(View.VISIBLE);
                        holder.portraitFrameView.setPortraitFrame(mChannelData.getMember(userId).getPortraitframe());
                    }else {
                        holder.portraitFrameView.setVisibility(View.GONE);
                    }

                    Glide.with(mInflater.getContext()).load(mChannelData.getMemberAvatar(userId)).into(holder.iv_seat);
                    holder.iv_mute.setVisibility(mChannelData.isUserMuted(userId) ? View.VISIBLE : View.GONE);
                    if (mChannelData.getMember(userId).getGender()!=null){
                        if (mChannelData.getMember(userId).getGender().equals("女")){
                            holder.tv_property.setVisibility(View.VISIBLE);
                            holder.tv_property.setBackgroundResource(R.drawable.shape_age_female);
                        }else {
                            holder.tv_property.setVisibility(View.VISIBLE);
                            holder.tv_property.setBackgroundResource(R.drawable.shape_age_male);
                        }
                        holder.tv_property.setText(mChannelData.getMember(userId).getProperty());
                    }

                    if (mChannelData.isAnchor(userId)){
                        holder.is_anchor.setVisibility(View.VISIBLE);
                    }else {
                        holder.is_anchor.setVisibility(View.GONE);
                    }
                } else {
                    holder.iv_seat.setImageResource(R.mipmap.ic_join);
                    holder.iv_mute.setVisibility(View.GONE);
                    holder.tv_property.setVisibility(View.GONE);
                    holder.is_anchor.setVisibility(View.GONE);
                    holder.portraitFrameView.setVisibility(View.GONE);
                }
            }
        } else {
            holder.iv_seat.setImageResource(R.mipmap.ic_join);
            holder.iv_mute.setVisibility(View.GONE);
            holder.tv_property.setVisibility(View.GONE);
            holder.is_anchor.setVisibility(View.GONE);
            holder.portraitFrameView.setVisibility(View.GONE);
        }

        holder.iv_seat.setOnClickListener(view -> {
            if (mListener != null)
                mListener.onItemClick(holder.view, position, seat);
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void notifyItemChanged(String userId, boolean animated) {
        int index = mChannelData.indexOfSeatArray(userId);
        if (index >= 0) {
            if (animated)
                notifyItemChanged(index, true);
            else
                notifyItemChanged(index);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        @BindView(R2.id.view_anim)
        SpreadView view_anim;
        @BindView(R2.id.iv_seat)
        CircleImageView iv_seat;
        @BindView(R2.id.iv_mute)
        ImageView iv_mute;
        @BindView(R2.id.tv_property)
        TextView tv_property;
        @BindView(R2.id.is_anchor)
        TextView is_anchor;
        @BindView(R2.id.portraitFrameView)
        PortraitFrameView portraitFrameView;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, Seat seat);
    }

}
