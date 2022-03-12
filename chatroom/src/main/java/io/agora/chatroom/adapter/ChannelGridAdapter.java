package io.agora.chatroom.adapter;

import android.content.Context;
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
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.chatroom.model.Channel;

public class ChannelGridAdapter extends RecyclerView.Adapter<ChannelGridAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private OnItemClickListener mListener;

    private List<Channel> mChannelList;

    public ChannelGridAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void initData(List<Channel> mChannelList) {
        this.mChannelList = mChannelList;

//        Resources resources = context.getResources();
//        TypedArray drawables = resources.obtainTypedArray(R.array.channel_list_drawable);
//        TypedArray backgrounds = resources.obtainTypedArray(R.array.channel_list_background);
//        String[] titles = resources.getStringArray(R.array.channel_list_title);
//
//        for (int i = 0; i < titles.length; i++) {
//            mChannelList.add(new Channel(
//                    drawables.getResourceId(i, 0),
//                    backgrounds.getResourceId(i, 0),
//                    titles[i]
//            ));
//        }
//
//
//        drawables.recycle();
//        backgrounds.recycle();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_item_channel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (mChannelList == null) return 0;
        return mChannelList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Channel channel = mChannelList.get(position);

        Glide.with(mInflater.getContext()).load(channel.getAudioroomcover()).into( holder.iv_image);
        //holder.iv_image.setImageResource(channel.getDrawableRes());
        holder.tv_name.setText(channel.getAudioroomname());

        holder.view.setOnClickListener((view) -> {
            if (mListener != null)
                mListener.onItemClick(view, position, channel);
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        @BindView(R2.id.iv_image)
        ImageView iv_image;
        @BindView(R2.id.tv_name)
        TextView tv_name;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, Channel channel);
    }

}
